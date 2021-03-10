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
    	Joueur joueurJ = joueurs[curseur];
    	if ( joueurJ.isEnPrison() && (des[0] == des[1] || joueurJ.getNbToursPrison() == 1) ) {
    		System.out.println("Vous etes libere de prison !");
    		joueurJ.setEnPrison(false);
    	}
    	if (!(joueurJ.isEnPrison())) { //Si le joueur n'est pas en prison
    		for(int i=1;i<=nbCases;i++){
    			if((joueurJ.getPion().getPosition()+i)%40==0) { joueurJ.ajout(2000); }
    		}
    		pion.setPosition((pion.getPosition() + nbCases) % 40);
    		surCaseSpeciale(pion);
        	affiche();
        	return;
    	} //Si le joueur est en prison
    	joueurJ.setNbToursPrison(joueurJ.getNbToursPrison()-1);
    	System.out.println("Vous etes en prison. Il faut faire un double ou attendre encore " + joueurJ.getNbToursPrison() 
    			+ " tours pour etre libere.");
    }
    
    //Si le pion est sur une case speciale/commu/chance, effectue les actions speciales associees a cette case
    public void surCaseSpeciale(Pion pion) {
    	Cases caseC = plateau.getCases(pion.getPosition());
    	if (caseC instanceof Proprietes) { return; }
    	if (caseC instanceof CasesSpeciales) {
    		switch (caseC.getNom()) {
				case "Impots revenu" :
				case "Taxe de luxe" :
					joueurs[curseur].ajout( ((CasesSpeciales)caseC).getTransaction() );
					System.out.println("Vous avez paye " + Math.abs(((CasesSpeciales)caseC).getTransaction()) + "e.");
					break;
				case "Aller prison" :
					System.out.println("Vous etes en prison.");
					joueurs[curseur].setEnPrison(true);
					joueurs[curseur].setNbToursPrison(3);
					pion.setPosition(10); //Correspond a la case Prison
					break;
    		}
    	}
    	
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