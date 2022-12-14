package monopoly;

import java.util.Scanner;

public class Joueur{ 

    private String nom;
    private Pion pion;
    private int argent;
    private Proprietes[] proprietes; // Proprietes possedees par un joueur
    private boolean enPrison;
    private int nbToursPrison;
    private boolean carteLibPrison;
    private Scanner reponse;
    private boolean faillite;
	private boolean robot = false;
    
    public Joueur(String nom) { // Au debut, le joueur est en case 0, a 10000 euros et aucune propriete
    	this.nom = nom;
    	pion = new Pion(0);
    	argent = 10000;
    	proprietes = new Proprietes[0];
    	enPrison = false;
    	carteLibPrison = false;
    	this.reponse = new Scanner(System.in);
    	faillite = false;
    }
    
    //Getters
    public Pion getPion() { return pion; }
	
	public String getNom() { return nom; }
	
	public int getArgent() { return argent; }
	
	public Proprietes[] getProprietes() { return proprietes; }
	
	public boolean getFaillite() { return faillite; }

	public int getNbPropCouleur(String couleur){
    	int cpt = 0;
    	for(Proprietes c : proprietes){
    		if(c.getType().equals("Propriete")){
    			if(c.getCouleur().equals(couleur)) cpt++;
			}
		}
    	return cpt;
	}
	
	public Proprietes[] getProprietesAvecBat() { 
		Proprietes[] propAvecBat = new Proprietes[getNbPropAvecBatiments()];
		int i = 0;
		for (Proprietes p : proprietes) {
			if (p.getNbMaisons()>0 || p.aUnHotel()) {
				propAvecBat[i] = p;
				i++;
			}
		}
		return propAvecBat;
	}
	
	public Proprietes[] getProprietesSansBat() { 
		Proprietes[] propSansBat = new Proprietes[proprietes.length - getNbPropAvecBatiments()];
		int i = 0;
		for (Proprietes p : proprietes) {
			if (p.getNbMaisons()==0 && !p.aUnHotel()) {
				propSansBat[i] = p;
				i++;
			}
		}
		return propSansBat;
	}
	
	public int getNbPropAvecBatiments() {
		int cpt = 0;
		for (Proprietes p : proprietes) {
			if (p.getNbMaisons()!=0 || p.aUnHotel()) {
				cpt++;
			}
		}
		return cpt;
	}
	
	public int getNbTotalMaisons() {
		int cpt = 0;
		for (Proprietes p : proprietes) {
			cpt += p.getNbMaisons();
		}
		return cpt;
	}
	
	public int getNbTotalHotels() {
		int cpt = 0;
		for (Proprietes p : proprietes) {
			if (p.aUnHotel()) {
				cpt++;
			}
		}
		return cpt;
	}

	public boolean isEnPrison() { return enPrison; }

	public boolean isRobot(){
		return robot;
	}

	public int getNbToursPrison() { return nbToursPrison; }
	
	public boolean aCarteLibPrison() { 	return carteLibPrison; }

	//Setters
	public void setNom (String nom) { this.nom=nom; }
	
	public void setArgent(int argent) { this.argent = argent; }
	
	public void setEnPrison(boolean enPrison) { this.enPrison = enPrison; }
	
	public void setNbToursPrison(int nbToursPrison) { this.nbToursPrison = nbToursPrison; }
	
	public void setCarteLibPrison(boolean carteLibPrison) { this.carteLibPrison = carteLibPrison; }
	
	public void setFaillite(boolean faillite) { this.faillite = faillite; }

	public void setRobot(){
		robot = true;
	}


	//Gestion de lancement de des
    public void questionDes() {
    	System.out.println("Tapez \"go\" pour lancer les des");
    	String s = reponse.next();
	    if(s.equals("go")) {
	    	return;
    	}
	    else{
    		System.out.println("Erreur de frappe.");
    		questionDes();
    	}
    }
   
	//Gestion de l'achat/vente de proprietes
	public boolean decision_achat() {
		System.out.println(this.getNom() + ", si vous souhaitez acheter la propriete, tapez \"oui\" sinon \"non\".");
    	String s = reponse.next();
	    if(s.equals("oui")) {
	    	System.out.println("Achat effectue.");
	    	return true;
    	}
	    else if(s.equals("non")) {
	    	System.out.println("Achat non-effectue.");
	    	return false;
	    }
	    else {
    		System.out.println("Erreur de frappe.");
    		return decision_achat();
    	}
	}
	
	//Gestion de l'achat d'une propriete
	public void achat_effectue(int prix, Proprietes p) {
		this.argent = this.argent - prix; //retrait du prix d'achat de la propriete dans l'argent du joueur
		Proprietes[] tmp = new Proprietes[this.proprietes.length+1];
		for(int i=0;i<this.proprietes.length;i++){
			tmp[i] = this.proprietes[i];
		}
		tmp[this.proprietes.length] = p; //ajout de la propriete dans la nouvelle tab proprietes du joueur
		this.proprietes = tmp;
	}
	
	//Gestion de la vente d'une propriete
	public void vente_effectuee(int prix, Proprietes p) {
		this.argent = this.argent + prix; //ajout du prix de vente de la propriete dans l'argent du joueur
		Proprietes[] tmp = new Proprietes[this.proprietes.length-1];
		int i = 0;
		for(Proprietes c: proprietes){ 
			if(c!=p){
				tmp[i] = c;
				i++;
			}
		}
		this.proprietes = tmp; //nouvelle tab proprietes du joueur sans la propriete vendue
	}
	
	//Possibilite au joueur de choisir s'il veut vendre une de ses proprietes a un autre joueur
	public boolean decision_vente(Joueur proprietaire) {
		System.out.println(proprietaire.getNom() + ", souhaitez-vous vendre cette propriete a " + this.getNom() + "?");
		System.out.println(proprietaire.getNom() + ", si vous souhaitez vendre la propriete, tapez \"oui\" sinon \"non\".");
    	String s = reponse.next();
	    if(s.equals("oui")) {
	    	System.out.println(proprietaire.getNom() + " accepte de vendre cette propriete a " + this.getNom());
	    	return decision_achat();
    	}
	    else if(s.equals("non")) {
	    	System.out.println(proprietaire.getNom() + " refuse de vendre cette propriete a " + this.getNom());
	    	return false;
	    }
	    else {
	    	System.out.println("Erreur de frappe.");
    		return decision_vente(proprietaire);
    	}
	}
	

	//Loyer
	public int paye_loyer(int x){
    	while (argent < x && proprietes.length != 0) { //Tant que le joueur n'a pas assez d'argent mais qu'il lui reste des proprietes a vendre
    		System.out.println("Vous n'avez pas assez d'argent pour payer le loyer. Quelle propriete souhaitez-vous vendre ?");
    		for (int i = 0; i < proprietes.length; i++) {
    			System.out.println("Tapez " + i + " pour vendre " + proprietes[i].getNom() 
    					+ " d'une valeur de " + proprietes[i].getPrix() + "e." );
			}
    		vendreSesProprietes();
	 	} //Condition de sortie : si le joueur a assez d'argent grace a la vente ou si le joueur n'a plus de propr
    	if(argent>=x){
    		argent = argent - x;
    		return x;
		}
    	//Si le joueur n'a pas assez d'argent et pas/plus de propriete a vendre
    	x = argent;
	    argent = 0;
	    return x;	
	}
	
	//Revente de proprietes
	public void vendreSesProprietes() {
    	String s = reponse.next();
	    for (int i = 0; i < proprietes.length; i++) {
			if(s.equals(String.valueOf(i))) {
				proprietes[i].setProprietaire(null);
				vente_effectuee(proprietes[i].getPrix(), proprietes[i]);
				return;
			}
		}
	    System.out.println("Entree invalide, recommencez.");
    	vendreSesProprietes();
    }
	
	//Possibilite de se liberer de prison en utilisant la carte Libere de prison
	public boolean utiliserCarteLibPrison() {
		String s = reponse.next();
		if (s.equals("oui")) {
			System.out.println("Vous etes libere de prison !");
    		enPrison = false;
    		carteLibPrison = false;
    		return true;
		}
		else if (s.equals("non")) {
			return false;
		}
		else {
			System.out.println("Entree invalide, recommencez.");
			return utiliserCarteLibPrison();
		}
	}

	public void transaction(int x){
    	argent = argent + x;
    	if (argent < 0) { argent = 0; }
	}
	
	//Le joueur qui est en train de jouer donne x somme d'argent a un autre joueur
	public int thisPayeA(Joueur receveur, int montant) {
		if(argent < montant){
			montant = argent;
		}
		receveur.transaction(montant);
		transaction(-montant);
		System.out.println("Vous avez donne " + montant + "e a " + receveur.getNom() + ". " +
				receveur.getNom() + " a maintenant " + receveur.getArgent() + "e." );
		return montant;
	}
	
	//Le joueur qui est en train de jouer recoit x somme d'argent de la part d'un autre joueur
	public void thisRecoitDe(Joueur payeur, int montant) {
		if(payeur.argent < montant){
			montant = payeur.argent;
		}
		payeur.transaction(-montant);
		transaction(montant);
		System.out.println(payeur.getNom() + " vous a donne " + montant + "e. " +
				payeur.getNom() + " a maintenant " + payeur.getArgent() + "e." );
	}
	
	
	//Interface graphique
	public int vendreLaPropriete_IG(Proprietes p) {
		int position_ancienne_propriete = p.getPosition();
		p.setProprietaire(null);
		vente_effectuee(p.getPrix(), p);
		return position_ancienne_propriete;
	}
	
	//Systeme qui permet d'utiliser sa carte Libere de prison
	public void utiliserCarteLibPrison_IG() {
		enPrison = false;
		carteLibPrison = false;
	}

	public void viderPropriete() {
        proprietes = new Proprietes[0];
    }
	
	//Systeme pour vendre sa carte Libere de prison
	public void AVenduCartePrison() {
		carteLibPrison=false;
		transaction(500);
	}

}
