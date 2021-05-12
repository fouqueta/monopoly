package application;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.util.Duration;
import monopoly.*;

import java.io.*;
import java.net.Socket;

public class Controleur implements Runnable {
	private Vue vue;
	private Jeu jeu;
	//Reseau
	private PrintWriter pw;
	private Socket socket;
	private Cartes carte = null;
	boolean running;

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
				running = false;
				pw = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(),"UTF8"), true);
				sendMsg("close","");
				jeu.setReseau(false);

			} catch (IOException e) {
				e.printStackTrace();
			}

		}else{
			try {
				socket = new Socket("176.144.217.163", 666);
				//socket = new Socket("127.0.0.1", 666);
				pw = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(),"UTF8"), true);
				jeu.setReseau(true);
				running = true;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	//Getters
	Jeu getJeu() { return this.jeu; }

	Cartes getCarte() { return this.carte; }
	
	//Apres lancer de des	
	void controleur_lancer(int[] des, int curseur) {
		vue.changement_labelDes(des);
		controleur_deplacement(des, curseur);
		controleur_loyer(curseur, des);
		vue.changement_argent(curseur);
		if(jeu.getJoueurReseau() == jeu.getJoueurs()[jeu.getCurseur()]){
			Joueur j = jeu.getJoueurs()[curseur];
			sendMsg("deplace",
					des[0] + "-"
					+ des[1] + "-"
					+ j.getPion().getPosition() + "-"
					+ j.getArgent() + "-"
					+ j.isEnPrison() + "-"
					+ j.getNbToursPrison() + "-"
					+ j.getFaillite() + "-"
					+ j.aCarteLibPrison());
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
				verifPuisPaiement(curseur, carteTiree.getParametres(), carteTiree);
				break;
			case "immo" :
				int sommeApayer = joueurJ.getNbTotalMaisons()*carteTiree.getParametres() + joueurJ.getNbTotalHotels()*4*carteTiree.getParametres();
				verifPuisPaiement(curseur, sommeApayer, carteTiree);
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
			sendMsg("carte", jeu.numCase(carteTiree) + "-" + joueurJ.getArgent()+ "-" + carteTiree.getTypeAction() + "-" + carteTiree.getParametres());
		}
	}

	//Gestion des cases speciales
	public void controleur_case_speciale(int curseur, Cases case_actuelle) {
		Joueur joueurJ = jeu.getJoueurs()[curseur];
		jeu.surCaseSpeciale_IG(joueurJ.getPion(), case_actuelle);
		if (case_actuelle.getNom().equals("Impots revenu") || case_actuelle.getNom().equals("Taxe de luxe")) {
			verifPuisPaiement(curseur, ((CasesSpeciales)case_actuelle).getTransaction(), null);
		}
	}


	//Paie le loyer si besoin
	void controleur_loyer(int curseur, int[] des) {
		int position = jeu.getJoueurs()[curseur].getPion().getPosition();
		Cases case_actuelle = jeu.getPlateau().getCases(position);
		if(case_actuelle instanceof Proprietes) {
			Proprietes propriete_actuelle = (Proprietes) jeu.getPlateau().getCases(position);
			if(!(propriete_actuelle.est_Libre()) && vue.getTabProprietaires(position) != curseur
				&& !propriete_actuelle.estCompagnie()){
				verifPuisPaiement(curseur, propriete_actuelle.getLoyer(), null);
				vue.gestion_historique(vue.deuxJoueurs_historique("loyer", jeu.getJoueurs()[curseur], propriete_actuelle.getProprietaire(), null, propriete_actuelle.getLoyer()));
				vue.changement_argent(vue.getTabProprietaires(position));
			}
			else if (!(propriete_actuelle.est_Libre()) && vue.getTabProprietaires(position) != curseur
				&& propriete_actuelle.estCompagnie()){
				int sommeDes = des[0] + des[1];
				verifPuisPaiement(curseur, sommeDes*propriete_actuelle.getLoyer(), null);
				vue.gestion_historique(vue.deuxJoueurs_historique("loyer", jeu.getJoueurs()[curseur], propriete_actuelle.getProprietaire(), null, sommeDes*propriete_actuelle.getLoyer()));
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
				sendMsg("fin tour", String.valueOf(controleur_curseurSuivant(jeu.getCurseur())));

			}
			jeu.finTour_IG();
			vue.changement_joueur_actuel();
			if(jeu.getJoueurs()[jeu.getCurseur()].isRobot()){
				PauseTransition wait = new PauseTransition(Duration.seconds(0.4));
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
			if(jeu.isReseau() && jeu.getJoueurReseau() == jeu.getJoueurs()[jeu.getCurseur()])
				sendMsg("faillite", "");
		}

	}


	//Gestion de l'achat/vente
	void controleur_achat(int curseur) {
		Pion p = jeu.getJoueurs()[curseur].getPion();
		int position = p.getPosition();
		jeu.achat_IG(p);
		vue.changement_couleur_case(curseur, position);
		if(jeu.isReseau() && jeu.getJoueurReseau() == jeu.getJoueurs()[jeu.getCurseur()]) {
			Proprietes pos_actuelle = (Proprietes) jeu.getPlateau().getCases(position);
			int prix = pos_actuelle.getPrix();
			sendMsg("achat", String.valueOf(prix));
		}
	}
	
	void controleur_vente(int curseur) {
		Pion pion = jeu.getJoueurs()[curseur].getPion();
		int position = pion.getPosition();
		Proprietes propP = (Proprietes) jeu.getPlateau().getCases(position);
		jeu.vente_IG(pion);
		vue.actualisation_HBoxImagesMaisons(propP);
		vue.actualisation_HBoxImageHotel(propP);
		vue.changement_couleur_case(curseur, position);
	}


	//Verifie si on doit revendre ses proprietes avant de payer, puis passe au paiement
	public void verifPuisPaiement(int curseur, int sommeApayer, Cartes carteTiree) {
		if (jeu.getJoueurs()[curseur].getArgent() < sommeApayer && (jeu.getJoueurs()[curseur].getProprietes().length!=0 || jeu.getJoueurs()[curseur].aCarteLibPrison())) {
			if(!jeu.isReseau() || jeu.getJoueurReseau() == jeu.getJoueurs()[curseur]){
				vue.affichage_revente_proprietes(curseur, sommeApayer, carteTiree);
			}
		}
		else {
			transactionSelonType(curseur, sommeApayer, carteTiree);
		}
	}

	public void transactionSelonType(int curseur,  int sommeApayer, Cartes carteTiree) {
        Joueur joueurJ = jeu.getJoueurs()[jeu.getCurseur()];
        int position = joueurJ.getPion().getPosition();
        Cases caseC = jeu.getPlateau().getCases(position);

        if (caseC instanceof Proprietes) {
            jeu.loyer_IG((Proprietes) caseC, sommeApayer);
			if(jeu.isReseau()){
				System.out.println("loyer");
				sendMsg("loyer", sommeApayer +"-"+ ((Proprietes) caseC).getProprietaire().getNom());
			}
        }
        else if (caseC.getNom().equals("Impots revenu") || caseC.getNom().equals("Taxe de luxe")) {
            joueurJ.transaction(-sommeApayer);
        }
        else if ( (caseC instanceof CasesCommunaute || caseC instanceof CasesChance) &&
                (carteTiree.getTypeAction().equals("prelevement") || carteTiree.getTypeAction().equals("immo")) ) {
            joueurJ.transaction(-sommeApayer);
        }
        else if ( (caseC instanceof CasesCommunaute || caseC instanceof CasesChance) &&
                (carteTiree.getTypeAction().equals("cadeau")) ) {
            joueurJ.thisRecoitDe(jeu.getJoueurs()[curseur], sommeApayer);
        }
        vue.changement_argent(curseur);
        vue.changement_argent(vue.getTabProprietaires(position));
    }


	public void controleur_achatBatiment(Proprietes p, String typeBatiment) {
		if (typeBatiment.equals("maison")) {
			p.achatMaison();
			vue.actualisation_HBoxImagesMaisons(p);
			vue.gestion_historique(vue.unJoueur_historique("achatMaison", p.getProprietaire(), null, p.getPosition()));
			if(jeu.isReseau() && jeu.getJoueurs()[jeu.getCurseur()] == jeu.getJoueurReseau()){
				sendMsg("batiment", "maison-" + p.getPosition() + "-" + p.getPrixBatiment());
			}
		}
		else if (typeBatiment.equals("hotel")) {
			p.achatHotel();
			vue.actualisation_HBoxImageHotel(p);
			vue.gestion_historique(vue.unJoueur_historique("achatHotel", p.getProprietaire(), null, p.getPosition()));
			if(jeu.isReseau() && jeu.getJoueurs()[jeu.getCurseur()] == jeu.getJoueurReseau()){
				sendMsg("batiment", "hotel-" + p.getPosition() + "-" + p.getPrixBatiment());
			}
		}
		vue.changement_argent(jeu.getCurseur());
	}

	public void controleur_venteBatiment(Proprietes p, String typeBatiment, int nbVentesBat) {
		for(int i = 0; i < nbVentesBat; i++) {
			if (typeBatiment.equals("maison") && p.getNbMaisons()>0) {
				p.venteMaison();
				vue.actualisation_HBoxImagesMaisons(p);
			}
			else if (typeBatiment.equals("hotel")) {
				p.venteHotel();
				vue.actualisation_HBoxImageHotel(p);
			}
		}
		vue.changement_argent(jeu.getCurseur());
	}

	public void controleur_venteCartePrison(int curseur) {
		jeu.getJoueurs()[curseur].AVenduCartePrison();
		vue.changement_argent(curseur);
	}

	
	void controleur_loyerIG(Proprietes propriete_actuelle, int loyer) {
		jeu.loyer_IG(propriete_actuelle, loyer);

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
			if(jeu.isReseau()) sendMsg("defis gagnant", "joueur-" + sommeJoueur + "-" + sommeProprio+ "-" + loyerEnJeu);
		}
		else if(sommeProprio > sommeJoueur) { //Joueur paye deux fois le loyer, il l'a deja paye une fois donc seulement une autre fois encore.
			int montant = 0;
			if(joueur.getArgent()<loyerEnJeu && !jeu.isReseau()) {
				vue.affichage_revente_proprietes(curseur, loyerEnJeu, null);
			}else {
				montant = joueur.thisPayeA(proprio, loyerEnJeu);
			}
			if(jeu.isReseau()) sendMsg("defis gagnant", "proprio-" + sommeJoueur + "-" + sommeProprio + "-" + montant + "-" + proprio.getNom());
		}
		else {
			if(jeu.isReseau()) sendMsg("defis gagnant", "egalite-" + sommeJoueur + "-" + sommeProprio+ "-" + loyerEnJeu);
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

		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF8"));
			while (running) {
				String action = br.readLine();
				String info = br.readLine();
				System.out.println(action);
				System.out.println(info);
				action(action, info);
			}
		} catch (Exception e) {
			//this.interrupt();
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
			case "lancer des":
				Platform.runLater(() -> {
					String[] temp = info.split("-");
					int[] des = new int[2];
					des[0] = Integer.valueOf(temp[0]);
					des[1] = Integer.valueOf(temp[1]);
					int argent = jeu.getJoueurs()[curseur].getArgent();
					controleur_lancer(des, curseur);
					vue.bouton_defis(curseur, argent);
					vue.bouton_achat(curseur, argent);
				});
				break;
			case "deplace":
				Platform.runLater(() -> {
					String[] temp = info.split("-");
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
			case "vente a joueur":
				Platform.runLater(() -> {
					int position = jeu.getJoueurs()[curseur].getPion().getPosition();
					vue.updateVenteReseau(position, curseur);
				});
				break;
			case "vendre":
				Platform.runLater(() -> {
					String[] temp = info.split("-");
					if(temp[0].equals("hotel") || temp[0].equals("maison")){
						int jF = Integer.parseInt(temp[1]);
						Proprietes p = (Proprietes) jeu.getPlateau().getCases(Integer.parseInt(temp[3]));
						Joueur j = jeu.getJoueurParNom(temp[4]);
						int prix = Integer.parseInt(temp[5]);
						controleur_venteBatiment(p,temp[0],jF);
						vue.vendBatReseau(j,prix);
					}else{
						System.out.println("ici");
						Joueur j = jeu.getJoueurParNom(temp[0]);
						int prix = Integer.parseInt(temp[1]);
						int ancienne_position = Integer.parseInt(temp[2]);
						j.vendreLaPropriete_IG((Proprietes) jeu.getPlateau().getCases(ancienne_position));
						vue.changement_couleur_case_blanche(ancienne_position);
						vue.vendPropReseau(ancienne_position, j, prix);
					}

				});
				break;
			case "vendre prison":
				Platform.runLater(() -> {
					controleur_venteCartePrison(curseur);
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
						if(joueur.getArgent()<loyerEnJeu && jeu.getJoueurReseau()==jeu.getJoueurs()[curseur]) {
							vue.affichage_revente_proprietes(curseur, loyerEnJeu, null);
						}else {
							joueur.thisPayeA(proprio, loyerEnJeu);
						}
					}
					vue.changement_argent(curseur);
					vue.changement_argent(vue.getTabProprietaires(position));

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
						vue.accueil_pseudo(true);
					}
				});
				break;
			case "close":
				try{
					pw.close();
					socket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}

				break;
			case "batiment":
				Platform.runLater(() -> {
					String[] temp = info.split("-");
					int posProp = Integer.parseInt(temp[1]);
					Proprietes p = (Proprietes) jeu.getPlateau().getCases(posProp);
					controleur_achatBatiment(p, temp[0]);
				});
				break;
			case "faillite":
				Platform.runLater(() -> {
					jeu.getJoueurs()[curseur].setFaillite(true);
					if (jeu.jeuFini_IG()) {
						vue.fin_partie();
						vue.gestion_historique(new Label("La partie est terminee."));
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
