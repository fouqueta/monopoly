package application;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.util.Duration;
import monopoly.*;

import java.io.*;
import java.net.Socket;

public class Controleur extends Thread {
	private Vue vue;
	private Jeu jeu;
	//Reseau
	private PrintWriter pw;
	private Socket socket;
	private Cartes carte = null;

	Controleur() {
	}

	Controleur(Jeu jeu) {
		this.jeu = jeu;


	}

	//Setters
	void setVue(Vue vue) {
		this.vue = vue;
	}

	void setJeu(Jeu jeu) {
		this.jeu = jeu;
	}

	public void startSocket(){
		if(jeu.isReseau()){

			try {
				pw = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
				sendMsg("close","");
				pw.close();
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			jeu.setReseau(false);
		}else{
			try {
				socket = new Socket("176.144.217.163", 666);
				jeu.setReseau(true);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	//Getters
	Jeu getJeu() {
		return this.jeu;
	}

	//Apres lancer de des	
	void controleur_lancer(int[] des, int curseur) {
		vue.changement_labelDes(des);
		controleur_deplacement(des, curseur);
		controleur_loyer(des, curseur);
		if(jeu.isReseau() && jeu.getJoueurReseau() == jeu.getJoueurs()[jeu.getCurseur()]){
			sendMsg("deplace", des[0] + "," + des[1]);
		}

	}

	//Gere les deplacements (sur quel type de case on tombe etc)
	void controleur_deplacement(int[] des, int curseur) {
		if (!(jeu.getJoueurs()[curseur].isEnPrison())) {
			Pion p = jeu.getJoueurs()[curseur].getPion();
			int depart = p.getPosition();
			jeu.deplace_IG(p, des);

			controleur_surCaseParticuliere(p, curseur);
			int arrivee = p.getPosition();

			//controleur_chance_commu(curseur, jeu.getPlateau().getCases(p.getPosition())); //a rappeler qlqpart pour le cas ou on recule sur une case chance ou commu
			vue.changement_argent(curseur);
			if (depart != arrivee) {
				vue.changement_position_pion(curseur, depart, arrivee);
			}
		} else if (jeu.getJoueurs()[curseur].isEnPrison() &&
				(des[0] == des[1] || jeu.getJoueurs()[curseur].getNbToursPrison() == 1)) {
			System.out.println("Vous etes libre.");
			jeu.getJoueurs()[curseur].setEnPrison(false);
		} else {
			int tour_restant = jeu.getJoueurs()[curseur].getNbToursPrison();
			System.out.println("Vous restez en prison pour encore " + tour_restant + " tours.");
			jeu.getJoueurs()[curseur].setNbToursPrison(tour_restant - 1);
		}
	}

	//Verifie si on est sur une case particuliere
	void controleur_surCaseParticuliere(Pion pion, int curseur) {
		Cases case_actuelle = jeu.getPlateau().getCases(pion.getPosition());
		if (case_actuelle instanceof Proprietes) {
			return;
		} else if (case_actuelle instanceof CasesChance || case_actuelle instanceof CasesCommunaute) {
			controleur_chance_commu(curseur, case_actuelle);
		} else if (case_actuelle instanceof CasesSpeciales) {
			jeu.surCaseSpeciale(pion, case_actuelle);
		}
	}

	//Paie le loyer si besoin
	void controleur_loyer(int[] des, int curseur) {
		int position = jeu.getJoueurs()[curseur].getPion().getPosition();
		Cases case_actuelle = jeu.getPlateau().getCases(position);
		if (case_actuelle instanceof Proprietes) {
			Proprietes propriete_actuelle = (Proprietes) jeu.getPlateau().getCases(position);
			if (!(propriete_actuelle.est_Libre()) && vue.getTabProprietaires(position) != curseur
					&& propriete_actuelle.coloree()) {
				if (jeu.getJoueurs()[curseur].getArgent() < propriete_actuelle.getLoyer() && jeu.getJoueurs()[curseur].getProprietes().length != 0) {
					vue.affichage_revente_proprietes(curseur);
				} else {
					jeu.loyer_IG(propriete_actuelle);
					vue.changement_argent(curseur);
					vue.changement_argent(vue.getTabProprietaires(position));
					System.out.println("Loyer paye.");
				}
			}
		}
	}

	private void fin_only_robot() {
		PauseTransition wait = new PauseTransition(Duration.seconds(3));
		wait.setOnFinished((e) -> {
			if (jeu.jeuFini_IG()) {
				vue.fin_partie();
			} else {
				jeu.finTour_IG();
				vue.changement_joueur_actuel();
				System.out.println("Curseur :" + jeu.getCurseur());
				vue.lancerRobot();


			}
			wait.playFromStart();
		});
		wait.play();
	}


	void controleur_fin() {
		if (jeu.onlyRobot()) {
			fin_only_robot();
		} else if (jeu.jeuFini_IG()) {
			vue.fin_partie();
		} else {
			if(jeu.isReseau() && jeu.getJoueurReseau() == jeu.getJoueurs()[jeu.getCurseur()]){
				sendMsg("fin tour", "");
			}
			jeu.finTour_IG();
			vue.changement_joueur_actuel();
			if (jeu.getJoueurs()[jeu.getCurseur()].isRobot()) {
				vue.lancerRobot();
			}
		}

	}


	void controleur_faillite(int curseur) {
		Joueur joueur_actuel = jeu.getJoueurs()[curseur];
		jeu.faillite_IG(joueur_actuel);
	}

	//Gestion de l'achat/vente
	void controleur_achat(int curseur) {
		Pion p = jeu.getJoueurs()[curseur].getPion();
		int position = p.getPosition();
		jeu.achat_IG(p);
		vue.changement_couleur_case(curseur, position);
		if(jeu.isReseau() && jeu.getJoueurReseau() == jeu.getJoueurs()[jeu.getCurseur()]) {
			sendMsg("achat", "");
		}
	}

	void controleur_vente(int curseur) {
		Pion p = jeu.getJoueurs()[curseur].getPion();
		int position = p.getPosition();
		jeu.vente_IG(p);
		vue.changement_couleur_case(curseur, position);
		if(jeu.isReseau() && jeu.getJoueurReseau() == jeu.getJoueurs()[jeu.getCurseur()]) {
			sendMsg("vente", "");
		}
	}

	//S'occupe du cas quand on tombe sur une case chace ou communaute
	void controleur_chance_commu(int curseur, Cases case_actuelle) {
		Cartes carteTiree;
		if(!jeu.isReseau() || jeu.getJoueurReseau() == jeu.getJoueurs()[jeu.getCurseur()]) {
			carteTiree = jeu.tireCarteChanceCommu(case_actuelle);
		}else{
			carteTiree = carte;
		}
		Pion p = jeu.getJoueurs()[curseur].getPion();

		vue.caseChanceCommu(curseur, carteTiree);
		jeu.surCaseChanceCommu(p, carteTiree);

		if (carteTiree.getTypeAction().equals("cadeau")) {
			for (int i = 0; i < jeu.getNbJ(); i++) {
				if (curseur != i) {
					vue.changement_argent(i);
				}
			}
		}

		if(jeu.isReseau() && jeu.getJoueurReseau() == jeu.getJoueurs()[jeu.getCurseur()]) {
			sendMsg("carte", String.valueOf(jeu.numCase(carteTiree)));
		}
	}

	int controleur_vendreSesProprietes(int curseur, int n) {
		return jeu.getJoueurs()[curseur].vendreSesProprietes_IG(n);
	}

	void controleur_loyerIG(Proprietes propriete_actuelle) {
		jeu.loyer_IG(propriete_actuelle);
	}

	@Override
	public void run() {
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			pw = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
			sendMsg("start", jeu.getJoueurReseau().getNom());
			while (true) {
				String action = br.readLine();
				String info = br.readLine();
				System.out.println(action);
				System.out.println(info);
				action(action, info);
			}
		} catch (Exception e) {
			System.out.println(e);
			e.printStackTrace();
		}
	}

	private void action(String action, String info) {
		switch (action) {
			case "message":
				break;
			case "carte":
				Platform.runLater(() -> {
					int curseur = jeu.getCurseur();
					String[] temp = info.split("-");
					carte = jeu.carteParIndex(Integer.valueOf(temp[0]), temp[1]);
				});
				break;
			case "achat":
				Platform.runLater(() -> {
					int curseur = jeu.getCurseur();
					controleur_achat(curseur);
					vue.changement_argent(curseur);
				});
				break;
			case "fin tour":
				Platform.runLater(() -> {
					controleur_fin();
				});
				break;
			case "deplace":
				Platform.runLater(() -> {
					String[] temp = info.split(",");
					int[] des = new int[2];
					des[0] = Integer.valueOf(temp[0]);
					des[1] = Integer.valueOf(temp[1]);
					int curseur = jeu.getCurseur();
					controleur_lancer(des, curseur);
				});
				break;
			case "start":
				Platform.runLater(() -> {
					String[] noms = info.split("-");
					jeu.initialisation_joueurs(noms, null);
					vue.initialisation_plateau();

					vue.affichage_joueurs();

					vue.definition_label();
					vue.affichage_pions_initial();

					vue.bouton_lancer_de_des();
					vue.bouton_fin_de_tour();
					vue.boutons_jeu();
					vue.initialisation_boutons_achat_vente();

				});
				break;
			case "demande achat":
				Platform.runLater(() -> {
					int curseur = jeu.getCurseur();
					int position = jeu.getJoueurs()[curseur].getPion().getPosition();
					if (jeu.getJoueurReseau() == ((Proprietes) jeu.getPlateau().getCases(position)).getProprietaire()) {
						vue.active_vente(position, curseur);
					}
				});
				break;
			case "vente à joueur":
				Platform.runLater(() -> {
					int curseur = jeu.getCurseur();
					int position = jeu.getJoueurs()[curseur].getPion().getPosition();
					if (jeu.getJoueurReseau() == jeu.getJoueurs()[curseur]) {
						vue.updateVente(position, curseur);
					}
				});
				break;
			default:
				break;
		}
	}

	public void sendMsg(String action, String info) {
		pw.println(action);
		pw.println(info);
	}

}
