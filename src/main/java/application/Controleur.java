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
		System.out.println("Curseur: " + curseur);
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
			System.out.println("Loyer payé.");
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
