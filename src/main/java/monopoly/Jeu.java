package monopoly;

import java.util.Random;

public class Jeu {

    private Joueur[] joueurs;
    private Plateau plateau;
    private int curseur;
    private int nbJ;
    private int[] des;
    private Joueur joueurReseau;
    private boolean reseau;


    public Jeu() {
        plateau = new Plateau();
        curseur = 0;
        reseau = false;
    }

	public void initialisation_joueurs(String[] noms, boolean[] flags){
		nbJ = noms.length;
		joueurs = new Joueur[nbJ];
		for(int i=0;i<nbJ;i++){
			joueurs[i] = new Joueur(noms[i]);
			if(flags!=null && flags[i]) joueurs[i].setRobot();
			if(isReseau() && noms[i].equals(joueurReseau.getNom())){
				joueurReseau = joueurs[i];
			}
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

	public int getSommeDes() {
		return des[0] + des[1];
	}

	public Joueur getJoueurReseau(){
    	return joueurReseau;
	}

	public boolean isReseau(){
    	return reseau;
	}

	//Setter

	public void setJoueurReseau(Joueur j){
    	joueurReseau = j;
	}

	public void setReseau(boolean b){
    	reseau = b;
	}
    
    //Affichage
    public void affiche(){
    	plateau.actualisePosJoueurs(joueurs);
        plateau.affiche();
    }
    
    //Gestion des deplacements
    public void deplace(Pion pion, int[] des) {
    	des[0] = 1;
    	des[1] = 0;
    	int nbCases = des[0] + des[1];
    	Joueur joueurJ = joueurs[curseur];
    	if ( joueurJ.isEnPrison() && (des[0] == des[1] || joueurJ.getNbToursPrison() == 1) ) {
    		System.out.println("Vous etes libere de prison !");
    		joueurJ.setEnPrison(false);
    	}
    	if (!(joueurJ.isEnPrison())) { //Si le joueur n'est pas en prison
    		passeParDepart((pion.getPosition() + nbCases) % 40);
    		pion.setPosition((pion.getPosition() + nbCases) % 40);
    		affiche();
    		surCaseParticuliere(pion);
    	}
    	else { //Si le joueur est en prison
    		quandEnPrison(pion, joueurJ, nbCases);
    	}
    }
    
    public void quandEnPrison(Pion pion, Joueur joueurJ, int nbCases) {
    	if(joueurJ.aCarteLibPrison()) {
    		System.out.println("Vous possedez une carte \"libere de prison\". L'utiliser maintenant ? Tapez \"oui\" ou \"non\".");
    		if (joueurJ.utiliserCarteLibPrison()) { 
    			pion.setPosition((pion.getPosition() + nbCases) % 40);
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
				joueurs[curseur].transaction( -((CasesSpeciales)caseC).getTransaction() );
				System.out.println("Vous avez paye " + ((CasesSpeciales)caseC).getTransaction() + "e.");
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
				joueurs[curseur].transaction(-carte.getParametres());
				break;
			case "recette" :
				joueurs[curseur].transaction(carte.getParametres());
				break;
			case "immo" :
				joueurs[curseur].transaction(-(joueurs[curseur].getNbTotalMaisons()*carte.getParametres() + joueurs[curseur].getNbTotalHotels()*4*carte.getParametres()));
				break;
			case "trajet" :
				if ( carte.getParametres() != 30 ) {
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
			case "bonus" :
				joueurs[curseur].setCarteLibPrison(true);
				break;
			case "cadeau" :
				for(Joueur j : joueurs) {
					if(j != joueurs[curseur] && !j.getFaillite()) {
						joueurs[curseur].thisRecoitDe(j, carte.getParametres());
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
    		joueurs[curseur].transaction( ((CasesSpeciales) caseDepart).getTransaction() ); //Exemple : on est en case 10, on va en case 20 -> on ne passe pas par depart
    	} // on est en case 10, on va en case 5 -> on passe par depart
    }

	//Gestion de lancement de des
    public int[] lancer_de_des() {
		int[] des = new int[2];

		Random aleatoire = new Random();
		for(int i = 0; i<des.length; i++) {
			int intervalle = 1 + aleatoire.nextInt(7-1);
			des[i] = intervalle;
		}
		des[0] = 1;
		des[1] = 0;
		return des;
	}
    
    //Gestion de debut et fin de tour
    public void deroulerPartie() {
    	while (!jeuFini()) {
    		System.out.println("\nJoueur " + joueurs[curseur].getNom() + ", c'est a vous de jouer !");
        	joueurs[curseur].questionDes();
        	des = lancer_de_des();
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
    	int argent = joueurs[curseur].paye_loyer(p.getLoyer());
    	p.getProprietaire().transaction(argent);
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
    
    public void surCaseChanceCommu_IG(Pion pion, Cartes carte) {
    	switch (carte.getTypeAction()) {
			case "recette" :
				joueurs[curseur].transaction(carte.getParametres());
				break;
			case "trajet" :
				if ( carte.getParametres() != 30 ) {
					passeParDepart(carte.getParametres()); 
				}
				pion.setPosition(carte.getParametres());
				break;
			case "reculer" :
				pion.setPosition(carte.getParametres());
				break;
			case "trajet spe" :
				pion.setPosition(pion.getPosition() - carte.getParametres());
				break;
			case "bonus" :
				joueurs[curseur].setCarteLibPrison(true);
				break;
		}
    }
    
    public void surCaseSpeciale_IG(Pion pion, Cases caseC) {
    	if (caseC.getNom().equals("Aller prison")) {
    		joueurs[curseur].setEnPrison(true);
			joueurs[curseur].setNbToursPrison(3);
			pion.setPosition(10);
    	}
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

	    while (pos_actuelle.getNbMaisons() != 0) { //Si on vend une propriete avec des batiments, on recupere l'argent des batiment et la propriete n'a plus de batiment pour le nouveau proprietaire
	    	pos_actuelle.venteMaison();
	    }
	    if (pos_actuelle.aUnHotel()) { pos_actuelle.venteHotel(); }

	    joueurs[curseur].achat_effectue(prix,pos_actuelle);
	    proprietaire.vente_effectuee(prix, pos_actuelle);
	    pos_actuelle.setProprietaire(joueurs[curseur]);
    }
    
    
    public boolean faillite_IG(Joueur j) {
    	if (j.getArgent()<=0 && j.getProprietes().length == 0 && !j.aCarteLibPrison()){
    		j.setFaillite(true);
    		return true;
    	}
    	return false;
    }
    
    public boolean jeuFini_IG() {
    	int nbFaillite = 0;
    	for (Joueur j : joueurs) {
    		if (j.getFaillite()==true) {
    			nbFaillite++;
    		}
    	}
    	return nbFaillite==joueurs.length-1;
    }
     
    public int loyer_IG(Proprietes p, int loyer) { //parametre loyer necessaire car pour les compagnies, loyer a payer = p.getLoyer*somme des des
    	return joueurs[curseur].thisPayeA(p.getProprietaire(), loyer);
    }

	public boolean onlyRobot(){
		for(int i=0;i< joueurs.length;i++){
			if( !(joueurs[i].isRobot())){
				return false;
			}
		}
		return true;
	}


	public String numCase(Cartes carteTiree) {
    	Cartes[] t;
		t = plateau.getCartesCommu();
		for(int i=0;i<t.length;i++){
			if(t[i] == carteTiree){
				System.out.println(i);
				return i + "-commu";
			}
		}
		t = plateau.getCartesChance();
		for(int i=0;i<t.length;i++){
			if(t[i] == carteTiree){
				System.out.println(i);
				return i + "-chance";
			}
		}
    	return "";
	}

	public Cartes carteParIndex(int i, String s){
    	System.out.println(s);
    	if(s.equals("chance")){
    		return plateau.getCartesChance()[i];
		}
    	else {
    		return plateau.getCartesCommu()[i];
		}
	}
	
	public int quiEstJ(String info) {
		for (int i=0; i<nbJ; i++) {
			if (joueurs[i].getNom().equals(info)) {
				return i;
			}
		}
		return -1;
	}

}
