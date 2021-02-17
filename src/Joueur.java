import java.util.Scanner;
public class Joueur{

    private String nom;
    private Pion pion;
    private int argent;
    private Cases[] proprietes; // Les proprietes que le joueur possede
    Scanner reponse;
    
    public Joueur(String nom) { // Au debut, le joueur est en case 0, a 1500 clochettes et pas de propriete
    	this.nom = nom;
    	pion = new Pion(0);
    	argent = 1500;
    	proprietes = null;
    	this.reponse = new Scanner(System.in);
    }
    
   public String questionDes() {
    	System.out.println("Tapez \"go\" pour lancer les des");
    	String s = reponse.next();
	    if(s.equals("go")) {
	    	return "go";
    	}else {
    		System.out.println("Erreur de frappe. Tapez \"go\" pour lancer les des");
    		return questionDes();
    	}
    }
   
   //Getters
	public Pion getPion() { return pion; }
	
	public String getNom() { return nom; }
	
	public int getArgent() { return argent; }
	
	//Gestion de l'achat/vente de propriétés
	public boolean decision_achat() {
		System.out.println(this.getNom() + ", si vous souhaitez acheter la propriété, tapez \"oui\" sinon \"non\".");
    	String s = reponse.next();
	    if(s.equals("oui")) {
	    	return true;
    	}
	    else if(s.equals("non")) {
	    	return false;
	    }
	    else {
    		System.out.println("Erreur de frappe. Tapez \"oui\" ou \"non\".");
    		return decision_achat();
    	}
	}
	
	public void achat_effectue(int prix) {
		this.argent = this.argent - prix;
	}
	
	public void vente_effectuee(int prix) {
		this.argent = this.argent + prix;
	}
	
	public boolean decision_vente(Joueur proprietaire) {
		System.out.println(proprietaire.getNom() + ", souhaitez-vous vendre cette propriété à " + this.getNom() + "?");
		System.out.println(proprietaire.getNom() + ", si vous souhaitez vendre la propriété, tapez \"oui\" sinon \"non\".");
    	String s = reponse.next();
	    if(s.equals("oui")) {
	    	return decision_achat();
    	}
	    else if(s.equals("non")) {
	    	return false;
	    }
	    else {
    		System.out.println(proprietaire.getNom() + ", souhaitez-vous vendre cette propriété à " + this.getNom() + "?");
    		return decision_vente(proprietaire);
    	}
	}

}