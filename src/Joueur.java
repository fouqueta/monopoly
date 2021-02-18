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
    		System.out.println(proprietaire.getNom() + ", souhaitez-vous vendre cette propriete a " + this.getNom() + "?");
    		return decision_vente(proprietaire);
    	}
	}

}