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
    }
    
    public Joueur() { //FIXME: jsp si je le met direct dans le 1er constructeur
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

	public Pion getPion() {
		return pion;
	}
	
	public String getNom() {
		return nom;
	}

}