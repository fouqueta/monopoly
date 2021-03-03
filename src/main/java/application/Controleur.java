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
	
	//Lancer de des
	void controleur_lancer(int[] des, int curseur) {
		System.out.println(curseur);
		Pion p = jeu.getJoueurs()[curseur].getPion();
		jeu.deplace_IG(p, des);
		int depart = p.getPosition();
		int arrivee = depart + des[0] + des[1];
		vue.changement_position_pion(curseur, depart, arrivee);
	}
	
	void controleur_fin() {
		jeu.finTour_IG();
	}
}
