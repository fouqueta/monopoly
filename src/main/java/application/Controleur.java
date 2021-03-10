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
	}
	
	void controleur_fin() {
		jeu.finTour_IG();
		vue.changement_joueur_actuel();
	}
	
	//Gestion de l'achat/vente
	void controleur_achat(int curseur) {
		Pion p = jeu.getJoueurs()[curseur].getPion();
		int position = p.getPosition();
		jeu.achat_ou_vente_IG(p);
		vue.changement_couleur_case(curseur, position);
	}
	
	void controleur_vente(int curseur) {
		Pion p = jeu.getJoueurs()[curseur].getPion();
		int position = p.getPosition();
		jeu.vente_IG(p);
		vue.changement_couleur_case(curseur, position);
	}
}
