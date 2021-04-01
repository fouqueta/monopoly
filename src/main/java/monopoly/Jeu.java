package monopoly;

import java.util.Random;

public class Jeu {

    private Joueur[] joueurs;
    private Plateau plateau;
    private int curseur;
    private int nbJ;

    public Jeu() {
        plateau = new Plateau();
        curseur = 0;
    }

	public void initialisation_joueurs(String[] noms, boolean[] flags){
		nbJ = noms.length;
		joueurs = new Joueur[nbJ];
		for(int i=0;i<nbJ;i++){
			joueurs[i] = new Joueur(noms[i]);
			if(flags[i]) joueurs[i].setRobot();
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
    	Joueur joueurJ = joueurs[curseur];
    	if (!(joueurJ.isEnPrison())) { //Si le joueur n'est pas en prison
    		passeParDepart((pion.getPosition() + nbCases) % 40);
    		pion.setPosition((pion.getPosition() + nbCases) % 40);
    		affiche();
    		surCaseParticuliere(pion);
    	}
    	else if ( joueurJ.isEnPrison() && (des[0] == des[1] || joueurJ.getNbToursPrison() == 1) ) {
    		System.out.println("Vous etes libere de prison !");
    		joueurJ.setEnPrison(false);
    	}
    	else { //Si le joueur est en prison
    		quandEnPrison(pion, joueurJ, nbCases);
    	}
    }
    
    public void quandEnPrison(Pion pion, Joueur joueurJ, int nbCases) {
    	if(joueurJ.aCarteLibPrison()) {
    		System.out.println("Vous possedez une carte \"libere de prison\". L'utiliser maintenant ? Tapez \"oui\" ou \"non\".");
    		if (joueurJ.utiliserCarteLibPrison()) { 
    			//passeParDepart((pion.getPosition() + nbCases) % 40); //Pas necessaire si on considere que nbCases depasse pas 12
        		//pion.setPosition((pion.getPosition() + nbCases) % 40);
        		affiche();
        		surCaseParticuliere(pion);
    			return; 
    		}
    	}
    	joueurJ.setNbToursPrison(joueurJ.getNbToursPrison()-1);
    	System.out.println("Vous etes en prison. Il faut faire un double ou attendre encore " + joueurJ.getNbToursPrison() 
    			+ " tours pour etre libere.");
    }
    
    //Si le pion est sur une case speciale/commu/chance, effectue les actions speciales associees a cette case (appelle les fonctions auxi)
    public void surCaseParticuliere(Pion pion) {
    	Cases caseC = plateau.getCases(pion.getPosition());
    	if (caseC instanceof Proprietes) { return; }
    	else if (caseC instanceof CasesChance || caseC instanceof CasesCommunaute) {
    		Cartes carte = tireCarteChanceCommu(caseC);
    		surCaseChanceCommu(pion, carte);
    	}
    	else if (caseC instanceof CasesSpeciales) {
    		surCaseSpeciale(pion, caseC);
    	}
    }
    
    //Si on tombe sur une case speciale, effectue l'action associee
    public void surCaseSpeciale(Pion pion, Cases caseC) {
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
				affiche();
				break;
    	}
    }
    
    //Si on tombe sur une case chance ou communaute, tire une carte au hasard et effectue l'action associee
    public void surCaseChanceCommu(Pion pion, Cartes carte) {
		System.out.println(carte.getContenu());
		switch (carte.getTypeAction()) {
			case "prelevement" :
			case "recette" :
				joueurs[curseur].ajout(carte.getParametres());
				break;
			case "immo" :
				//TODO : cas immo quand il y aura les maisons et hotels (ne pas oublier de modifier cartes.csv pour mettre plusieurs parametres de prix)
				break;
			case "trajet" : //Demander pour le cas prison : direct en prison (donc rajouter un cas if) ou passer par aller en prison (moins de code)
				if ( carte.getParametres() != 30 ) { //Seul moyen de savoir si on va en case prison ou pas
					passeParDepart(carte.getParametres()); 
				}
				pion.setPosition(carte.getParametres());
				affiche();
				surCaseParticuliere(pion);
				break;
			case "reculer" :
				pion.setPosition(carte.getParametres());
				affiche();
				surCaseParticuliere(pion);
				break;
			case "trajet spe" :
				pion.setPosition(pion.getPosition() - carte.getParametres());
				affiche();
				surCaseParticuliere(pion);
				break;
			case "bonus" : //Faire la possibilite de vendre sa carte
				joueurs[curseur].setCarteLibPrison(true);
				break;
			case "cadeau" : //Faire le cas si apres avoir donner le cadeau un joueur est en faillite ou non
				for(Joueur j : joueurs) {
					if(j != joueurs[curseur] && !j.getFaillite()) {
						joueurs[curseur].transaction(carte.getParametres(), j);
					}
				}
				break;
		}
    }
    
    //Tirer au sort une carte chance ou communaute
    public Cartes tireCarteChanceCommu(Cases caseC) {
    	Random rand = new Random();
		int alea = rand.nextInt(16);
		Cartes carte = null;
		if (caseC instanceof CasesChance) {
			carte = plateau.getCartesChance()[alea];
		}
		else if (caseC instanceof CasesCommunaute) {
			carte = plateau.getCartesCommu()[alea];
		}
		return carte;
    }
    
    //Verifie si lors du deplacement on passe par la case depart
    public void passeParDepart(int posFinale) {
    	Cases caseDepart = plateau.getCases(0);
    	if (joueurs[curseur].getPion().getPosition() > posFinale) { //Comme 0 <= position <= 39, si posInit > posFinale, alors cela veut dire qu'on passe par la case depart, sinon non
    		joueurs[curseur].ajout( ((CasesSpeciales) caseDepart).getTransaction() ); //Exemple : on est en case 10, on va en case 20 -> on ne passe pas par depart / on est en case 10, on va en case 5 -> on passe par depart
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
		return des;
	}
    
    //Gestion de debut et fin de tour
    public void deroulerPartie() {
    	while (!jeuFini()) {
    		System.out.println("\nJoueur " + joueurs[curseur].getNom() + ", c'est a vous de jouer !");
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
    public void loyer(Proprietes p){
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

    //Interface graphique
    public void deplace_IG(Pion pion, int[] des) {
    	int nbCases = des[0] + des[1]; 
    	passeParDepart((pion.getPosition() + nbCases) % 40);
    	pion.setPosition((pion.getPosition() + nbCases) % 40);
    }
    
    public void finTour_IG() {
    	curseur = (curseur + 1) % nbJ;
    	while(joueurs[curseur].getFaillite()==true) {
			curseur= + (curseur + 1) % nbJ;
		}
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
    
    public void vente_IG(Pion p) {
    	Proprietes pos_actuelle = (Proprietes) plateau.getCases(p.getPosition());
			Joueur proprietaire = pos_actuelle.getProprietaire();
	            int prix = pos_actuelle.getPrix();
	            joueurs[curseur].achat_effectue(prix,pos_actuelle);
	            proprietaire.vente_effectuee(prix, pos_actuelle);
	            pos_actuelle.setProprietaire(joueurs[curseur]);
    }
    
    
    public void faillite_IG(Joueur j) {
    	if (j.getArgent()<=0){
    		j.setFaillite(true);
    	}
    }
    
    public boolean jeuFini_IG() {
    	int nbFaillite = 0;
    	for (Joueur j : joueurs) {
    		if (j.getFaillite()==true) {
    			nbFaillite++;
    		}
    	}
    	if (nbFaillite==joueurs.length-1) {
    		return true;
    	}else {
    		return false;
    	}
    }
    
    public void loyer_IG(Proprietes p) {
        int argent = joueurs[curseur].paye_IG(p.getLoyer());
        p.getProprietaire().ajout(argent);
        System.out.println("Vous avez paye " + argent + "e. Il vous reste "+ joueurs[curseur].getArgent() + "e." );
    }

	public boolean onlyRobot(){
		for(int i=0;i< joueurs.length;i++){
			if(!(joueurs[i].isRobot())){
				return false;
			}
		}
		return true;
	}


}
