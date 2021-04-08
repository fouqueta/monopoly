package application;

import javafx.animation.PauseTransition;
import javafx.util.Duration;
import monopoly.*;

public class Controleur {
	private Vue vue;
	private Jeu jeu;
	
	Controleur(){}
	
	Controleur(Jeu jeu){
		this.jeu = jeu;
	}
	
	//Setters
	void setVue(Vue vue) { this.vue = vue; }
	
	void setJeu(Jeu jeu) { this.jeu = jeu; }
	
	//Getters
	Jeu getJeu() { return this.jeu; }
	
	//Apres lancer de des	
	void controleur_lancer(int[] des, int curseur) {
		controleur_deplacement(des, curseur);
		controleur_loyer(des, curseur);
		vue.changement_argent(curseur);
		//System.out.println("JE SUIS LA");
	}
	
	//Gere les deplacements (sur quel type de case on tombe etc)
	void controleur_deplacement(int[] des, int curseur) {
		if(jeu.getJoueurs()[curseur].isEnPrison() && 
			(des[0] == des[1] || jeu.getJoueurs()[curseur].getNbToursPrison() == 1)) {
			System.out.println("Vous etes libre.");
			jeu.getJoueurs()[curseur].setEnPrison(false);
		}
		if( !(jeu.getJoueurs()[curseur].isEnPrison()) ){
			Pion p = jeu.getJoueurs()[curseur].getPion();
			int depart = p.getPosition();
			jeu.deplace_IG(p, des);
		
			controleur_surCaseParticuliere(p, curseur);
			int arrivee = p.getPosition();
			
			//controleur_chance_commu(curseur, jeu.getPlateau().getCases(p.getPosition())); //a rappeler qlqpart pour le cas ou on recule sur une case chance ou commu
			if (depart!=arrivee) { vue.changement_position_pion(curseur, depart, arrivee); }
		}	
		else {
			int tour_restant = jeu.getJoueurs()[curseur].getNbToursPrison();
			System.out.println("Vous restez en prison pour encore " + tour_restant + " tours.");
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

	
	//S'occupe du cas quand on tombe sur une case chace ou communaute
	public void controleur_chance_commu(int curseur, Cases case_actuelle) {
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
	}
		
	//S'occupe du cas quand on tombe sur une case speciale
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
				System.out.println("Loyer paye.");
			}
		}
	}

	private void fin_only_robot(){
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
		if(jeu.onlyRobot()) {
			fin_only_robot();
		}
		else if (jeu.jeuFini_IG()) {
			vue.fin_partie();
		}else {
			jeu.finTour_IG();
			vue.changement_joueur_actuel();
			if(jeu.getJoueurs()[jeu.getCurseur()].isRobot()){
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
	}
	
	void controleur_vente(int curseur) {
		Pion p = jeu.getJoueurs()[curseur].getPion();
		int position = p.getPosition();
		jeu.vente_IG(p);
		vue.changement_couleur_case(curseur, position);
	}

	
	//Verifie si on doit revendre ses proprietes avant de payer, puis passe au paiement
	public void verifPuisPaiement(int curseur, int sommeApayer, Cartes carteTiree) {
		//System.out.println("Je rentre dans le controleur_revente");
		if (jeu.getJoueurs()[curseur].getArgent() < sommeApayer && jeu.getJoueurs()[curseur].getProprietes().length!=0) {
			//System.out.println("Je rentre dans le IF du controleur_revente");
			vue.affichage_revente_proprietes(curseur, sommeApayer, carteTiree);
		}
		else {
			//System.out.println("Je rentre dans le ELSE du controleur_revente");
			transactionSelonType(curseur, carteTiree);
		}
	}
	
	public void transactionSelonType(int curseur, Cartes carteTiree) {
		//System.out.println("Je rentre dans transactionSelonType");
		Joueur joueurJ = jeu.getJoueurs()[curseur];
		int position = jeu.getJoueurs()[curseur].getPion().getPosition();
		Cases caseC = jeu.getPlateau().getCases(position);
		
		if (caseC instanceof Proprietes) {
			//System.out.println("Je rentre dedans si c'est une propriete");
			jeu.loyer_IG((Proprietes) caseC);
		}
		else if (caseC.getNom().equals("Impots revenu") || caseC.getNom().equals("Taxe de luxe")) {
			//System.out.println("Je rentre dedans si c'est la case impots ou taxe");
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
}
