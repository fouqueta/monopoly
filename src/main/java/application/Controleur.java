package application;

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
	}
	
	//Gere les deplacements (sur quel type de case on tombe etc)
	void controleur_deplacement(int[] des, int curseur) {
		if(!(jeu.getJoueurs()[curseur].isEnPrison())){
			Pion p = jeu.getJoueurs()[curseur].getPion();
			int depart = p.getPosition();
			jeu.deplace_IG(p, des);
		
			controleur_surCaseParticuliere(p, curseur);
			int arrivee = p.getPosition();
			
			//controleur_chance_commu(curseur, jeu.getPlateau().getCases(p.getPosition())); //a rappeler qlqpart pour le cas ou on recule sur une case chance ou commu
			vue.changement_argent(curseur);
			if (depart!=arrivee) { vue.changement_position_pion(curseur, depart, arrivee); }
		}
		else if(jeu.getJoueurs()[curseur].isEnPrison() && 
				(des[0] == des[1] || jeu.getJoueurs()[curseur].getNbToursPrison() == 1)) {
				System.out.println("Vous etes libre.");
				jeu.getJoueurs()[curseur].setEnPrison(false);
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
    		jeu.surCaseSpeciale(pion, case_actuelle);
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
			jeu.loyer(propriete_actuelle);
			vue.changement_argent(curseur);
			vue.changement_argent(vue.getTabProprietaires(position));
			System.out.println("Loyer paye.");
			}
		}
	}
	
	
	void controleur_fin() {
		if (jeu.jeuFini_IG()) {
			vue.fin_partie();
		}else {
			jeu.finTour_IG();
			vue.changement_joueur_actuel();
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
	
	//S'occupe du cas quand on tombe sur une case chace ou communaute
	void controleur_chance_commu(int curseur, Cases case_actuelle) {
		Cartes carteTiree = jeu.tireCarteChanceCommu(case_actuelle);
		Pion p = jeu.getJoueurs()[curseur].getPion();
		
		vue.caseChanceCommu(curseur, carteTiree);
		jeu.surCaseChanceCommu(p, carteTiree);
		
		if (carteTiree.getTypeAction().equals("cadeau")) {
			for(int i = 0; i < jeu.getNbJ() ;i++) {
    			if(curseur != i) { vue.changement_argent(i); }
			}
		}
	}
}
