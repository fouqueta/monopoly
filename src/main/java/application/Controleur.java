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
		}
		if( !(jeu.getJoueurs()[curseur].isEnPrison()) ){
			Pion p = jeu.getJoueurs()[curseur].getPion();
			int depart = p.getPosition();
			jeu.deplace_IG(p, des);
			controleur_surCaseParticuliere(p, curseur);
			int arrivee = p.getPosition();
			
			if (depart!=arrivee) { vue.changement_position_pion(curseur, depart, arrivee); }
		}	
		else {
			int tour_restant = jeu.getJoueurs()[curseur].getNbToursPrison();
			jeu.getJoueurs()[curseur].setNbToursPrison(tour_restant-1);
		}
	}
	
	//Verifie si on est sur une case particuliere
	void controleur_surCaseParticuliere(Pion pion, int curseur) {
		Cases case_actuelle = jeu.getPlateau().getCases(pion.getPosition());
		if (case_actuelle instanceof Proprietes) { return; }
		else if (case_actuelle instanceof CasesChance || case_actuelle instanceof CasesCommunaute) {
			controleur_chance_commu(curseur, case_actuelle);
    	}
    	else if (case_actuelle instanceof CasesSpeciales) {
    		controleur_case_speciale(curseur, case_actuelle);
    	}
	}


	//Gestion des cases chance/communaute
	public void controleur_chance_commu(int curseur, Cases case_actuelle) {
		if(!jeu.isReseau() || jeu.getJoueurReseau() == jeu.getJoueurs()[jeu.getCurseur()]) {
			carteTiree = jeu.tireCarteChanceCommu(case_actuelle);
		}else{
			carteTiree = carte;
		}
		Cartes carteTiree = jeu.tireCarteChanceCommu(case_actuelle);
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
				vue.changement_argent(vue.getTabProprietaires(position));
			}
		}
	}

	private void fin_only_robot(){
		PauseTransition wait = new PauseTransition(Duration.seconds(2));
		wait.setOnFinished((e) -> {
			if (jeu.jeuFini_IG()) {
				vue.fin_partie();
			} else {
				jeu.finTour_IG();
				vue.changement_joueur_actuel();
				vue.lancerRobot();
			}
			wait.playFromStart();
		});
		wait.play();
	}


	void controleur_fin() {
		if(jeu.onlyRobot()) {
			fin_only_robot();
		}
		else if (jeu.jeuFini_IG()) {
			vue.fin_partie();
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
		}
		else if(sommeProprio > sommeJoueur) { //Joueur paye deux fois le loyer, il l'a deja paye une fois donc seulement une autre fois encore.
			joueur.thisPayeA(proprio, loyerEnJeu);
		}
		else {
			System.out.println("Egalite");
		}
		vue.changement_argent(curseur);
		vue.changement_argent(vue.getTabProprietaires(position));
	}

	void controleur_libererPrison(int curseur) {
		jeu.getJoueurs()[curseur].utiliserCarteLibPrison_IG();
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
			pw = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
			sendMsg("start", jeu.getJoueurReseau().getNom());
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
					vue.updateVenteReseau(position, curseur);
				});
				break;
			case "vendre":
				Platform.runLater(() -> {
					int curseur = jeu.getCurseur();
					int a = controleur_vendreSesProprietes(curseur, Integer.valueOf(info));
					vue.vendPropReseau(a, curseur);
				});

				break;
			case "deco":
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