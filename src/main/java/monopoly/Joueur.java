package monopoly;

import java.util.Scanner;
public class Joueur{

    private String nom;
    private Pion pion;
    private int argent;
    private Cases[] proprietes; // Proprietes possedees par un joueur
    Scanner reponse;
    
    public Joueur(String nom) { // Au debut, le joueur est en case 0, a 15000 clochettes et aucune propriete
    	this.nom = nom;
    	pion = new Pion(0);
    	argent = 15000;
    	proprietes = new Cases[0];
    	this.reponse = new Scanner(System.in);
    }
    
    //Getters
    public Pion getPion() { return pion; }
	
	public String getNom() { return nom; }
	
	public int getArgent() { return argent; }

	public int getNbPropCouleur(String couleur){
    	int cpt = 0;
    	for(Cases c : proprietes){
    		if(c.getType().equals("Propriete")){
    			if(((Proprietes) c).getCouleur().equals(couleur)) cpt++;
			}
		}
    	return cpt;
	}
	
	//Setters
	public void setArgent(int argent) { this.argent = argent; }

   
    //Gestion de lancement de des
    public String questionDes() {
    	System.out.println("Tapez \"go\" pour lancer les des");
    	String s = reponse.next();
	    if(s.equals("go")) {
	    	return "go";
    	}
	    else{
    		System.out.println("Erreur de frappe.");
    		return questionDes();
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
		Cases[] tmp = new Cases[this.proprietes.length+1];
		for(int i=0;i<this.proprietes.length;i++){
			tmp[i] = this.proprietes[i];
		}
		tmp[this.proprietes.length] = p;
		this.proprietes = tmp;
	}
	
	public void vente_effectuee(int prix, Proprietes p) {
		this.argent = this.argent + prix;
		Cases[] tmp = new Cases[this.proprietes.length-1];
		int i = 0;
		for(Cases c: proprietes){
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
    	if(argent>=x){
    		argent = argent - x;
    		return x;
		}
    	x = argent;
    	argent = 0;
    	return x;
	}

	public void ajout(int x){
    	argent = argent + x;
	}

}