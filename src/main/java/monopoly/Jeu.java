package monopoly;

import java.util.Random;

public class Jeu {

    private Joueur[] joueurs;
    private Plateau plateau;
    private int curseur;

    public Jeu() {
        joueurs = new Joueur[4];
        joueurs[0] = new Joueur("1");
        joueurs[1] = new Joueur("2");
        joueurs[2] = new Joueur("3");
        joueurs[3] = new Joueur("4");
        plateau = new Plateau("cases.csv");
        curseur = 0;
    }
    
    //Getters
  	public Joueur[] getJoueurs() {
  		return joueurs;
  	}
    
    //Affichage
    public void affiche(){
    	plateau.actualisePosJoueurs(joueurs);
        plateau.affiche();
    }
    
    //Gestion des deplacements
    public void deplace(Pion pion, int[] des) {
    	int nbCases = des[0] + des[1];
    	for(int i=1;i<=nbCases;i++){
	  		if((joueurs[curseur].getPion().getPosition()+i)%40==0) {
	  			joueurs[curseur].setArgent(joueurs[curseur].getArgent()+2000);
	  		}
	  	} 
    	pion.setPosition((pion.getPosition() + nbCases) % 40);
    	affiche();
    }

	//Gestion de lancement de des
    public int[] lancer_de_des() {
		int[] des = new int[2];
		Random aleatoire = new Random();
		for(int i = 0; i<des.length; i++) {
			int intervalle = 1 + aleatoire.nextInt(7-1);
			des[i] = intervalle;
		}
		//TODO: Prison
		return des;
	}
    
    //Gestion de debut et fin de tour
    public void debutTour() {
    	while (!jeuFini()) {
    		System.out.println("Joueur " + joueurs[curseur].getNom() + ", c'est a vous de jouer !");
        	joueurs[curseur].questionDes();
        	int[] des = lancer_de_des();
        	deplace(joueurs[curseur].getPion(), des);
    		System.out.println("Vous avez " + joueurs[curseur].getArgent() + "e");
        	achat_ou_vente(joueurs[curseur].getPion());
        	faillite(joueurs[curseur]); //Verifie si le joueur est tombe en faillite apres avoir paye le loyer
        	finTour();
    	}
    	System.out.println("Bravo " + joueurs[0].getNom() + ", vous avez gagne la partie !");
    }
    
    public void finTour() {
    	if (curseur>=joueurs.length-1) {
    		curseur=0;
    	}else {
    		curseur++;
    	}
    }
    
    //Gestion de l'achat/vente de proprietes
    public void achat_ou_vente(Pion p) {
    	Cases case_actuelle = plateau.getCases(p.getPosition());
    	if(case_actuelle.getType() == "Propriete") {
    		Proprietes pos_actuelle = (Proprietes) plateau.getCases(p.getPosition());
    		if(pos_actuelle.est_Libre() && joueurs[curseur].getArgent() >= pos_actuelle.getPrix()) {
    			pos_actuelle.toString();
    			if(joueurs[curseur].decision_achat()) {
    				int prix = pos_actuelle.getPrix();
    				joueurs[curseur].achat_effectue(prix,pos_actuelle);
    				pos_actuelle.setProprietaire(joueurs[curseur]);
					System.out.println("Vous avez " + joueurs[curseur].getArgent() + "e");
    			}
    		}
    		else if(pos_actuelle.est_Libre() && joueurs[curseur].getArgent() < pos_actuelle.getPrix()) {
    			System.out.println("Vous n'avez pas assez d'argent pour acheter cette propriete.");
    		}    		
    		else if(pos_actuelle.getProprietaire()!=joueurs[curseur]){
    			loyer(pos_actuelle);
    			if(!(pos_actuelle.est_Libre()) && joueurs[curseur].getArgent() >= pos_actuelle.getPrix()) {
    				Joueur proprietaire = pos_actuelle.getProprietaire();
			        if(joueurs[curseur].decision_vente(proprietaire)) {
			            int prix = pos_actuelle.getPrix();
			            joueurs[curseur].achat_effectue(prix,pos_actuelle);
			            proprietaire.vente_effectuee(prix, pos_actuelle);
			            pos_actuelle.setProprietaire(joueurs[curseur]);
						System.out.println("Vous avez " + joueurs[curseur].getArgent() + "e");
			        }
    			}
    		}
    	}
    }


    //Loyer
    private void loyer(Proprietes p){
    	int argent = joueurs[curseur].paye(p.getLoyer());
    	p.getProprietaire().ajout(argent);
		System.out.println("Vous avez paye " + argent + "e. Il vous reste "+ joueurs[curseur].getArgent() + "e." );
    }
    
    
    //Faillite
    public void faillite(Joueur joueurJ) { //joueurJ en faillite = joueurJ elimine du jeu (=du tableau de joueurs)
    	if (joueurJ.getArgent() <= 0 && joueurJ.getProprietes().length == 0) {
    		Joueur[] tmp = new Joueur[this.joueurs.length-1];
    		int i = 0;
    		for(Joueur j : joueurs){
    			if(j != joueurJ){
    				tmp[i] = j;
    				i++;
    			}
    		}
    		this.joueurs = tmp;
    		System.out.println(joueurJ.getNom() + " a fait faillite.");
    	}
    }
    
    
    //Condition jeu fini
    public boolean jeuFini() { //Jeu fini quand tous les joueurs sauf un sont en faillite, le joueur restant a gagne
    	return joueurs.length == 1;
    }
   
}