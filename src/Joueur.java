
public class Joueur{

    private String nom;
    private Pion pion;
    private int argent;
    private Cases[] proprietes; // Les proprietes que le joueur possede
    
    public Joueur(String nom) { // Au debut, le joueur est en case 0, a 1500 clochettes et pas de propriete
    	this.nom = nom;
    	pion = new Pion(0);
    	argent = 1500;
    	proprietes = null;
    }

	public Pion getPion() {
		return pion;
	}
	
	public String getNom() {
		return nom;
	}

}
