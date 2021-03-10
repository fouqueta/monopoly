package monopoly;

import java.util.Scanner;

public class Joueur{

    private String nom;
    private Pion pion;
    private int argent;
    private Proprietes[] proprietes; // Proprietes possedees par un joueur
    Scanner reponse;
    
    public Joueur(String nom) { // Au debut, le joueur est en case 0, a 15000 clochettes et aucune propriete
    	this.nom = nom;
    	pion = new Pion(0);
    	argent = 10000;
    	proprietes = new Proprietes[0];
    	this.reponse = new Scanner(System.in);
    }
    
    //Getters
    public Pion getPion() { return pion; }
	
	public String getNom() { return nom; }
	
	public int getArgent() { return argent; }
	
	public Proprietes[] getProprietes() { return proprietes; }

	public int getNbPropCouleur(String couleur){
    	int cpt = 0;
    	for(Proprietes c : proprietes){
    		if(c.getType().equals("Propriete")){
    			if(c.getCouleur().equals(couleur)) cpt++;
			}
		}
    	return cpt;
	}
	
	//Setters
	public void setArgent(int argent) { this.argent = argent; }

   
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
	
	public void achat_effectue(int prix, Proprietes p) {
		this.argent = this.argent - prix;
		Proprietes[] tmp = new Proprietes[this.proprietes.length+1];
		for(int i=0;i<this.proprietes.length;i++){
			tmp[i] = this.proprietes[i];
		}
		tmp[this.proprietes.length] = p;
		this.proprietes = tmp;
	}
	
	public void vente_effectuee(int prix, Proprietes p) {
		this.argent = this.argent + prix;
		Proprietes[] tmp = new Proprietes[this.proprietes.length-1];
		int i = 0;
		for(Proprietes c: proprietes){
			if(c!=p){
				tmp[i] = c;
				i++;
			}
		}
		this.proprietes = tmp;
	}
	
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
	public int paye(int x){
    	//TODO: modifier le while pour la vente aux encheres ou les hypotheques
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

	public void ajout(int x){
    	argent = argent + x;
	}

}