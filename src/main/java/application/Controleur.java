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
	
	//Lancer de des
	void controleur_lancer(int[] des, int curseur) {
		//System.out.println("Curseur: " + curseur);
		
		if(jeu.getJoueurs()[curseur].isEnPrison() && 
		(des[0] == des[1] || jeu.getJoueurs()[curseur].getNbToursPrison() == 1)) {
			System.out.println("Vous etes libre.");
			jeu.getJoueurs()[curseur].setEnPrison(false);
		}
		
		else if(jeu.getJoueurs()[curseur].isEnPrison()) {
			int tour_restant = jeu.getJoueurs()[curseur].getNbToursPrison();
			System.out.println("Vous restez en prison pour encore " + tour_restant + " tours.");
			jeu.getJoueurs()[curseur].setNbToursPrison(tour_restant-1);
		}
		
		else if(!(jeu.getJoueurs()[curseur].isEnPrison())){
			Pion p = jeu.getJoueurs()[curseur].getPion();
			int depart = p.getPosition();
			jeu.deplace_IG(p, des);
			vue.changement_argent(curseur);
			int arrivee = p.getPosition();
			vue.changement_position_pion(curseur, depart, arrivee);
		
			Cases case_actuelle = jeu.getPlateau().getCases(arrivee);
			if(case_actuelle.getType().equals("Propriete")) {
				Proprietes propriete_actuelle = (Proprietes) jeu.getPlateau().getCases(arrivee);
				if(!(propriete_actuelle.est_Libre()) && vue.getTabProprietaires(arrivee) != curseur
				&& propriete_actuelle.coloree()){
					jeu.loyer(propriete_actuelle);
					vue.changement_argent(curseur);
					vue.changement_argent(vue.getTabProprietaires(arrivee));
					System.out.println("Loyer paye.");
				}
			}
		
			//Cases speciales (prison, taxe, impots)
			else if(case_actuelle.getType().equals("Speciale")) {
				switch(case_actuelle.getNom()) {
					case "Impots revenu" :
						break;
					
					case "Taxe de luxe" :
						jeu.getJoueurs()[curseur].ajout( ((CasesSpeciales)case_actuelle).getTransaction() );
						vue.changement_argent(curseur);
						System.out.println("Vous avez paye " + Math.abs(((CasesSpeciales)case_actuelle).getTransaction()) + "e.");
						break;
					
					case "Aller prison" :
						System.out.println("Vous etes en prison.");
						jeu.getJoueurs()[curseur].setEnPrison(true);
						jeu.getJoueurs()[curseur].setNbToursPrison(3);
						p.setPosition(10);
						vue.changement_position_pion(curseur, arrivee, 10);
						break;
				}
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
}
