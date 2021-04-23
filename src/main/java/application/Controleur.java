package application;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.scene.control.Label;
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
				jeu.setReseau(false);
			} catch (IOException e) {
				e.printStackTrace();
			}

		}else{
			try {
				socket = new Socket("176.144.217.163", 666);
				pw = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
				jeu.setReseau(true);
				this.start();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	//Getters
	Jeu getJeu() { return this.jeu; }
	
	//Apres lancer de des	
	void controleur_lancer(int[] des, int curseur) {
		vue.changement_labelDes(des);
		controleur_deplacement(des, curseur);
		controleur_loyer(des, curseur);
		vue.changement_argent(curseur);
		if(jeu.isReseau() && jeu.getJoueurReseau() == jeu.getJoueurs()[jeu.getCurseur()]){
			sendMsg("deplace", des[0] + "," + des[1]);
		}

	}
	
	//Gere les deplacements (sur quel type de case on tombe etc)
	void controleur_deplacement(int[] des, int curseur) {
		if(jeu.getJoueurs()[curseur].isEnPrison() &&
			(des[0] == des[1] || jeu.getJoueurs()[curseur].getNbToursPrison() == 1)) {
			jeu.getJoueurs()[curseur].setEnPrison(false);
			vue.gestion_historique(vue.unJoueur_historique("enPrison", jeu.getJoueurs()[curseur], des, jeu.getJoueurs()[curseur].getPion().getPosition()));
		}
		if( !(jeu.getJoueurs()[curseur].isEnPrison()) ){
			Pion p = jeu.getJoueurs()[curseur].getPion();
			int depart = p.getPosition();
			jeu.deplace_IG(p, des);
			vue.gestion_historique(vue.unJoueur_historique("lancer", jeu.getJoueurs()[curseur], des, jeu.getJoueurs()[curseur].getPion().getPosition()));
			controleur_surCaseParticuliere(p, curseur);
			int arrivee = p.getPosition();
			
			if (depart!=arrivee) {
				vue.changement_position_pion(curseur, depart, arrivee);
			}
		}	
		else {
			int tour_restant = jeu.getJoueurs()[curseur].getNbToursPrison();
			jeu.getJoueurs()[curseur].setNbToursPrison(tour_restant-1);
			vue.gestion_historique(vue.unJoueur_historique("enPrison", jeu.getJoueurs()[curseur], des, jeu.getJoueurs()[curseur].getPion().getPosition()));
		}
	}
	
	//Verifie si on est sur une case particuliere
	void controleur_surCaseParticuliere(Pion pion, int curseur) {
		Cases case_actuelle = jeu.getPlateau().getCases(pion.getPosition());
		if (case_actuelle instanceof Proprietes) { return; }
		else if (case_actuelle instanceof CasesChance || case_actuelle instanceof CasesCommunaute) {
			controleur_chance_commu(curseur, case_actuelle);
			vue.gestion_historique(vue.unJoueur_historique("tirerUneCarte", jeu.getJoueurs()[curseur], null, jeu.getJoueurs()[curseur].getPion().getPosition()));
    	}
    	else if (case_actuelle instanceof CasesSpeciales) {
    		controleur_case_speciale(curseur, case_actuelle);
    	}
	}


	//Gestion des cases chance/communaute
	public void controleur_chance_commu(int curseur, Cases case_actuelle) {
		Cartes carteTiree;
		if(!jeu.isReseau() || jeu.getJoueurReseau() == jeu.getJoueurs()[jeu.getCurseur()]) {
			carteTiree = jeu.tireCarteChanceCommu(case_actuelle);
		}else{
			carteTiree = carte;
		}
		Joueur joueurJ = jeu.getJoueurs()[curseur];

		vue.caseChanceCommu(curseur, carteTiree);
		jeu.surCaseChanceCommu_IG(joueurJ.getPion(), carteTiree);

		switch (carteTiree.getTypeAction()) {
			case "prelevement" :
			case "immo" :
				verifPuisPaiement(curseur, -carteTiree.getParametres(), carteTiree);
				break;
			case "trajet" :
			case "reculer" :
			case "trajet spe" :
				controleur_surCaseParticuliere(joueurJ.getPion(), curseur);
				break;
			case "cadeau" :
				for(int i = 0; i < jeu.getNbJ() ;i++) {
					if(i != curseur && !jeu.getJoueurs()[i].getFaillite()) {
						verifPuisPaiement(i, carteTiree.getParametres(), carteTiree);
						vue.changement_argent(i);
					}
				}
				break;
		}

		if(jeu.isReseau() && jeu.getJoueurReseau() == jeu.getJoueurs()[jeu.getCurseur()]) {
			sendMsg("carte", String.valueOf(jeu.numCase(carteTiree)));
		}
	}

	//Gestion des cases speciales
	public void controleur_case_speciale(int curseur, Cases case_actuelle) {
		Joueur joueurJ = jeu.getJoueurs()[curseur];
		jeu.surCaseSpeciale_IG(joueurJ.getPion(), case_actuelle);
		if (case_actuelle.getNom().equals("Impots revenu") || case_actuelle.getNom().equals("Taxe de luxe")) {
			verifPuisPaiement(curseur, -((CasesSpeciales)case_actuelle).getTransaction(), null );
		}
	}


	//Paie le loyer si besoin
	void controleur_loyer(int[] des, int curseur) {
		int position = jeu.getJoueurs()[curseur].getPion().getPosition();
		Cases case_actuelle = jeu.getPlateau().getCases(position);
		if(case_actuelle instanceof Proprietes) {
			Proprietes propriete_actuelle = (Proprietes) jeu.getPlateau().getCases(position);
			if(!(propriete_actuelle.est_Libre()) && vue.getTabProprietaires(position) != curseur
				&& propriete_actuelle.coloree()){
				verifPuisPaiement(curseur, propriete_actuelle.getLoyer(), null);
				vue.gestion_historique(vue.deuxJoueurs_historique("loyer", jeu.getJoueurs()[curseur], propriete_actuelle.getProprietaire(), null, propriete_actuelle.getLoyer()));
				vue.changement_argent(vue.getTabProprietaires(position));
			}
		}
	}

	//Gestion des robots
	private void fin_only_robot(){
		PauseTransition wait = new PauseTransition(Duration.seconds(2));
		wait.setOnFinished((e) -> {
			if (jeu.jeuFini_IG()) {
				vue.fin_partie();
				vue.gestion_historique(new Label("La partie est terminee (onlyRobot)."));
			} else {
				jeu.finTour_IG();
				vue.changement_joueur_actuel();
				vue.lancerRobot();
				wait.playFromStart();
			}
		});
		wait.play();
	}

	void controleur_fin() {
		if(jeu.onlyRobot()) {
			fin_only_robot();
		}
		else if (jeu.jeuFini_IG()) {
			vue.fin_partie();
			vue.gestion_historique(new Label("La partie est terminee."));
		} else {
			if(jeu.isReseau() && jeu.getJoueurReseau() == jeu.getJoueurs()[jeu.getCurseur()]){
				sendMsg("fin tour", "");
			}
			jeu.finTour_IG();
			vue.changement_joueur_actuel();
			if(jeu.getJoueurs()[jeu.getCurseur()].isRobot()){
				PauseTransition wait = new PauseTransition(Duration.seconds(2));
				wait.setOnFinished((e) -> {
					vue.lancerRobot();
				});
				wait.play();
			}
		}
	}


	void controleur_faillite(int curseur) {
		Joueur joueur_actuel = jeu.getJoueurs()[curseur];
		boolean faillite = jeu.faillite_IG(joueur_actuel);
		if(faillite) {
			vue.gestion_historique(vue.unJoueur_historique("faillite", jeu.getJoueurs()[curseur], null, 0));
		}
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
	}


	//Verifie si on doit revendre ses proprietes avant de payer, puis passe au paiement
	public void verifPuisPaiement(int curseur, int sommeApayer, Cartes carteTiree) {
		if (jeu.getJoueurs()[curseur].getArgent() < sommeApayer && jeu.getJoueurs()[curseur].getProprietes().length!=0) {
			if(!jeu.isReseau() || jeu.getJoueurReseau() == jeu.getJoueurs()[curseur]){
				vue.affichage_revente_proprietes(curseur, sommeApayer, carteTiree);
			}
		}
		else {
			transactionSelonType(curseur, carteTiree);
		}
	}

	public void transactionSelonType(int curseur, Cartes carteTiree) {
		Joueur joueurJ = jeu.getJoueurs()[curseur];
		int position = jeu.getJoueurs()[curseur].getPion().getPosition();
		Cases caseC = jeu.getPlateau().getCases(position);

		if (caseC instanceof Proprietes) {
			jeu.loyer_IG((Proprietes) caseC);
		}
		else if (caseC.getNom().equals("Impots revenu") || caseC.getNom().equals("Taxe de luxe")) {
			joueurJ.transaction( ((CasesSpeciales) caseC).getTransaction() );
		}
		else if ( (caseC instanceof CasesCommunaute || caseC instanceof CasesChance) &&
				(carteTiree.getTypeAction().equals("prelevement") || carteTiree.getTypeAction().equals("immo")) ) {
			joueurJ.transaction(carteTiree.getParametres());
		}
		else if ( (caseC instanceof CasesCommunaute || caseC instanceof CasesChance) && (carteTiree.getTypeAction().equals("cadeau")) ) {
			jeu.getJoueurs()[jeu.getCurseur()].thisRecoitDe(jeu.getJoueurs()[curseur], carteTiree.getParametres());
		}
		vue.changement_argent(curseur);
		vue.changement_argent(vue.getTabProprietaires(position));
	}

	
	void controleur_loyerIG(Proprietes propriete_actuelle) {
		jeu.loyer_IG(propriete_actuelle);
	}

	//Systeme de defis
	void controleur_defis(int curseur) {
		int desJoueur[] = jeu.lancer_de_des();
		int desProprio[] = jeu.lancer_de_des();
		int sommeJoueur = desJoueur[0] + desJoueur[1];
		int sommeProprio = desProprio[0] + desProprio[1];

		int position = jeu.getJoueurs()[curseur].getPion().getPosition();
		Proprietes propriete_actuelle = (Proprietes) jeu.getPlateau().getCases(position);
		int loyerEnJeu = propriete_actuelle.getLoyer();
		Joueur joueur = jeu.getJoueurs()[curseur];
		Joueur proprio = propriete_actuelle.getProprietaire();

		if(sommeJoueur > sommeProprio) { //Rembourse le loyer au joueur gagnant.
			joueur.thisRecoitDe(proprio, loyerEnJeu);
			if(jeu.isReseau()) sendMsg("defis gagnant", "joueur-" + sommeJoueur + "-" + sommeProprio);
		}
		else if(sommeProprio > sommeJoueur) { //Joueur paye deux fois le loyer, il l'a deja paye une fois donc seulement une autre fois encore.
			joueur.thisPayeA(proprio, loyerEnJeu);
			if(jeu.isReseau()) sendMsg("defis gagnant", "proprio-" + sommeJoueur + "-" + sommeProprio);
		}
		else {
			if(jeu.isReseau()) sendMsg("defis gagnant", "egalite-" + sommeJoueur + "-" + sommeProprio);
		}
		int tab_histo[] = {sommeJoueur, sommeProprio};
		vue.gestion_historique(vue.deuxJoueurs_historique("accepterDefi", joueur, proprio, tab_histo, 0));

		vue.changement_argent(curseur);
		vue.changement_argent(vue.getTabProprietaires(position));
	}

	void controleur_libererPrison(int curseur) {
		jeu.getJoueurs()[curseur].utiliserCarteLibPrison_IG();
		vue.gestion_historique(vue.unJoueur_historique("carteLiberation", jeu.getJoueurs()[curseur], null, jeu.getJoueurs()[curseur].getPion().getPosition()));
	}

	int controleur_curseurSuivant(int curseur) {
		int curseurSuivant = (curseur+1)%jeu.getNbJ();
    	while(jeu.getJoueurs()[curseurSuivant].getFaillite()==true) {
			curseurSuivant= + (curseurSuivant + 1) % jeu.getNbJ();
    	}
    	return curseurSuivant;
	}


	@Override
	public void run() {
		boolean running = true;
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			while (running) {
				String action = br.readLine();
				String info = br.readLine();
				System.out.println(action);
				System.out.println(info);
				action(action, info);
			}
		} catch (Exception e) {
			this.interrupt();
			running = false;
			System.out.println(e);
			e.printStackTrace();
		}
	}

	private void action(String action, String info) {
		int curseur = jeu.getCurseur();
		switch (action) {
			case "message":
				Platform.runLater(() -> {
					vue.gestion_tchat(new Label(info));
				});
				break;
			case "carte":
				Platform.runLater(() -> {
					String[] temp = info.split("-");
					carte = jeu.carteParIndex(Integer.valueOf(temp[0]), temp[1]);
				});
				break;
			case "achat":
				Platform.runLater(() -> {
					vue.achatReseau();
				});
				break;
			case "fin tour":
				Platform.runLater(() -> {
					vue.finDeTourReseau();
					controleur_fin();
				});
				break;
			case "deplace":
				Platform.runLater(() -> {
					String[] temp = info.split(",");
					int[] des = new int[2];
					des[0] = Integer.valueOf(temp[0]);
					des[1] = Integer.valueOf(temp[1]);
					controleur_lancer(des, curseur);
				});
				break;
			case "start":
				Platform.runLater(() -> {
					String[] noms = info.split("-");
					jeu.initialisation_joueurs(noms, null);
					vue.initialisation_plateau();

					vue.affichage_joueurs();

					vue.pionLabel_positionnement();
					vue.affichage_pions_initial();

					vue.bouton_lancer_de_des();
					vue.bouton_fin_de_tour();
					vue.boutons_jeu();
					vue.initialisation_boutons();

					vue.creation_fenetreHistorique();
					vue.creation_fenetreTchat();

				});
				break;
			case "demande achat":
				Platform.runLater(() -> {
					int position = jeu.getJoueurs()[curseur].getPion().getPosition();
					if (jeu.getJoueurReseau() == ((Proprietes) jeu.getPlateau().getCases(position)).getProprietaire()) {
						vue.active_vente(position, curseur);
					}
					vue.gestion_historique(vue.deuxJoueurs_historique("achat", jeu.getJoueurs()[curseur], jeu.getJoueurs()[vue.getTabProprietaires(position)], null, position));
				});
				break;
			case "vente Ã  joueur":
				Platform.runLater(() -> {
					int position = jeu.getJoueurs()[curseur].getPion().getPosition();
					vue.updateVenteReseau(position, curseur);
				});
				break;
			case "vendre":
				Platform.runLater(() -> {
					int n = Integer.parseInt(info);
					int ancienne_position = jeu.getJoueurs()[curseur].vendreLaPropriete_IG(n);
					vue.changement_couleur_case_blanche(ancienne_position);
					vue.vendPropReseau(ancienne_position, curseur);
				});

				break;
			case "deco":
				Platform.runLater(() -> {
                    int pos = jeu.quiEstJ(info);

                    if(pos>-1){
                        Joueur j = jeu.getJoueurs()[pos];
                        j.setArgent(0);
                        j.setFaillite(true);

                        vue.changement_argent(pos);

                        for(Proprietes p: j.getProprietes()){
                            vue.changement_couleur_case_blanche(p.getPosition());
                            p.setProprietaire(null);
                        }

                        j.viderPropriete();

                        if(jeu.getJoueurs()[curseur] == j){
                            vue.finDeTourReseau();
                            controleur_fin();
                        }else if(jeu.jeuFini_IG()){
                            vue.fin_partie();
                            vue.gestion_historique(new Label("La partie est terminee."));
                        }
                    }
                });
				break;
			case "demande defis":
				Platform.runLater(() -> {
					int position = jeu.getJoueurs()[curseur].getPion().getPosition();
					if (jeu.getJoueurReseau() == ((Proprietes) jeu.getPlateau().getCases(position)).getProprietaire()) {
						vue.boutonDefisReseau(position, curseur);
					}
					vue.gestion_historique(vue.deuxJoueurs_historique("lancerDefi", jeu.getJoueurs()[curseur], jeu.getJoueurs()[vue.getTabProprietaires(position)], null, position));
				});
				break;
			case "defis gagnant":
				Platform.runLater(() -> {
					int position = jeu.getJoueurs()[curseur].getPion().getPosition();
					Proprietes propriete_actuelle = (Proprietes) jeu.getPlateau().getCases(position);
					int loyerEnJeu = propriete_actuelle.getLoyer();
					Joueur joueur = jeu.getJoueurs()[curseur];
					Joueur proprio = propriete_actuelle.getProprietaire();

					String[] t = info.split("-");

					if(t[0].equals("joueur")) { //Rembourse le loyer au joueur gagnant.
						joueur.thisRecoitDe(proprio, loyerEnJeu);
					}
					else if(t[0].equals("proprio")) { //Joueur paye deux fois le loyer, il l'a deja paye une fois donc seulement une autre fois encore.
						joueur.thisPayeA(proprio, loyerEnJeu);
					}
					vue.changement_argent(curseur);

					int tab_histo[] = {Integer.parseInt(t[1]), Integer.parseInt(t[2])};
					vue.gestion_historique(vue.deuxJoueurs_historique("accepterDefi", joueur, proprio, tab_histo, 0));
				});

				break;
			case "carte prison":
				Platform.runLater(() -> {
					controleur_libererPrison(curseur);
				});
				break;
			case "erreur":
				Platform.runLater(() -> {
					if(info.equals("Pseudo deja prit")){
						vue.accueil_jeu(true);
					}
				});

			default:
				break;
		}
	}

	public void sendMsg(String action, String info) {
		pw.println(action);
		pw.println(info);
	}

}
