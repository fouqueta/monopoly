package monopoly;

import java.util.Random;

public class Jeu {

    private Joueur[] joueurs;
    private Plateau plateau;
    private int curseur;
	private int nbJ;

    public Jeu() {
        plateau = new Plateau("cases.csv");
        curseur = 0;
    }

	public void initialisation_joueurs(String[] noms){
		nbJ = noms.length;
		joueurs = new Joueur[nbJ];
		for(int i=0;i<nbJ;i++){
			joueurs[i] = new Joueur(noms[i]);
		}

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

	public int getNbJ(){
		return nbJ;
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
    		System.out.println("Vous avez " + joueurs[curseur].getArgent() + "e");
    		achat_ou_vente(joueurs[curseur].getPion());
    		finTour();
    	}else debutTour();
    	
    }
    
    public void finTour() {
		curseur = (curseur + 1) % nbJ;
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
    				joueurs[curseur].achat_effectue(prix,pos_actuelle);
    				pos_actuelle.setProprietaire(joueurs[curseur]);
    				System.out.println("Vous avez " + joueurs[curseur].getArgent() + "e");
    			}
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
    public void loyer(Proprietes p){
    	int argent = joueurs[curseur].paye(p.getLoyer());
    	p.getProprietaire().ajout(argent);
		System.out.println("Vous avez payé " + argent + "e. Il vous reste "+ joueurs[curseur].getArgent() + "e." );
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
		curseur = (curseur + 1) % nbJ;
    }
    
    public void achat_IG(Pion p) {
    	Cases case_actuelle = plateau.getCases(p.getPosition());
    	if(case_actuelle.getType().equals("Propriete")) {
    		Proprietes pos_actuelle = (Proprietes) plateau.getCases(p.getPosition());
    		if(pos_actuelle.est_Libre()){
    			pos_actuelle.toString();
    			int prix = pos_actuelle.getPrix();
    			joueurs[curseur].achat_effectue(prix,pos_actuelle);
				pos_actuelle.setProprietaire(joueurs[curseur]);
    		}
    	}
    }
    
    public void vente_IG(Pion p ) {
    	Proprietes pos_actuelle = (Proprietes) plateau.getCases(p.getPosition());
    	//if(!(pos_actuelle.est_Libre()) && pos_actuelle.getProprietaire()!=joueurs[curseur] && joueurs[curseur].getArgent() >= pos_actuelle.getPrix()) {
			Joueur proprietaire = pos_actuelle.getProprietaire();
	            int prix = pos_actuelle.getPrix();
	            joueurs[curseur].achat_effectue(prix,pos_actuelle);
	            proprietaire.vente_effectuee(prix, pos_actuelle);
	            pos_actuelle.setProprietaire(joueurs[curseur]);
	       // }
		
    }
}