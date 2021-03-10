package monopoly;

import java.util.Random;

public class Jeu {

    private Joueur[] joueurs;
    private Plateau plateau;
    private int curseur;

    public Jeu() {
        joueurs = new Joueur[6];
        joueurs[0] = new Joueur("1");
        joueurs[1] = new Joueur("2");
        joueurs[2] = new Joueur("3");
        joueurs[3] = new Joueur("4");
        joueurs[4] = new Joueur("5");
        joueurs[5] = new Joueur("6");
        plateau = new Plateau("cases.csv");
        curseur = 0;
    }
    
    //Getters
  	public Joueur[] getJoueurs() {
  		return joueurs;
  	}
  	
  	public int getCurseur() {
  		return curseur;
  	}
  	
  	public Plateau getPlateau() {
  		return plateau;
  	}
    
    //Affichage
    public void affiche(){
    	plateau.actualisePosJoueurs(joueurs);
        plateau.affiche();
    }
    
    //Gestion des deplacements
    public void deplace(Pion pion, int[] des) {
    	int nbCases = des[0] + des[1];
    	for(int i=0;i<nbCases;i++){
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
    	System.out.println("Joueur " + joueurs[curseur].getNom() + ", c'est a vous de jouer !");
    	String s = joueurs[curseur].questionDes();
    	if (s=="go") {
    		int[] des = lancer_de_des();
    		deplace(joueurs[curseur].getPion(), des);
    		achat_ou_vente(joueurs[curseur].getPion());
    		finTour();
    	}else debutTour();
    	
    }
    
    public void finTour() {
    	if (curseur==5) {
    		curseur=0;
    	}else {
    		curseur++;
    	}
    	debutTour();
    }
    
    //Gestion de l'achat/vente de proprietes
    public void achat_ou_vente(Pion p) {
    	Cases case_actuelle = plateau.getCases(p.getPosition());
    	if(case_actuelle.getType().equals("Propriete")) {
    		Proprietes pos_actuelle = (Proprietes) plateau.getCases(p.getPosition());
    		if(pos_actuelle.est_Libre() && joueurs[curseur].getArgent() >= pos_actuelle.getPrix()) {
    			pos_actuelle.toString();
    			if(joueurs[curseur].decision_achat()) {
    				int prix = pos_actuelle.getPrix();
    				joueurs[curseur].achat_effectue(prix);
    				pos_actuelle.setProprietaire(joueurs[curseur]);
    			}
    		}
    		else {
    			//TODO: Loyer
    			if(!(pos_actuelle.est_Libre()) && pos_actuelle.getProprietaire()!=joueurs[curseur] && joueurs[curseur].getArgent() >= pos_actuelle.getPrix()) {
    				Joueur proprietaire = pos_actuelle.getProprietaire();
			        if(joueurs[curseur].decision_vente(proprietaire)) {
			            int prix = pos_actuelle.getPrix();
			            joueurs[curseur].achat_effectue(prix);
			            proprietaire.vente_effectuee(prix);
			            pos_actuelle.setProprietaire(joueurs[curseur]);
			        }
    			}
    		}
    	}
    }
    
    //Interface graphique
    public void deplace_IG(Pion pion, int[] des) {
    	int nbCases = des[0] + des[1];
    	for(int i=1;i<=nbCases;i++){
	  		if((joueurs[curseur].getPion().getPosition()+i)%40==0) {
	  			joueurs[curseur].setArgent(joueurs[curseur].getArgent()+2000);
	  		}
	  	} 
    	pion.setPosition((pion.getPosition() + nbCases) % 40);
    }
    
    public void finTour_IG() {
    	if (curseur==3) {
    		curseur=0;
    	}else {
    		curseur++;
    	}
    }
    
    public void achat_ou_vente_IG(Pion p) {
    	Cases case_actuelle = plateau.getCases(p.getPosition());
    	if(case_actuelle.getType().equals("Propriete")) {
    		Proprietes pos_actuelle = (Proprietes) plateau.getCases(p.getPosition());
    		//if(pos_actuelle.est_Libre() && joueurs[curseur].getArgent() >= pos_actuelle.getPrix()) {
    		if(pos_actuelle.est_Libre()){
    			pos_actuelle.toString();
    			int prix = pos_actuelle.getPrix();
				joueurs[curseur].achat_effectue(prix);
				pos_actuelle.setProprietaire(joueurs[curseur]);
    		}
    	}
    }
    
    public void vente_IG(Pion p ) {
    	Proprietes pos_actuelle = (Proprietes) plateau.getCases(p.getPosition());
    	//if(!(pos_actuelle.est_Libre()) && pos_actuelle.getProprietaire()!=joueurs[curseur] && joueurs[curseur].getArgent() >= pos_actuelle.getPrix()) {
			Joueur proprietaire = pos_actuelle.getProprietaire();
	            int prix = pos_actuelle.getPrix();
	            joueurs[curseur].achat_effectue(prix);
	            proprietaire.vente_effectuee(prix);
	          //  pos_actuelle.setProprietaire(joueurs[curseur]);
	       // }
		
    }
}