package monopoly;

public class Proprietes extends Cases {
	
	private int prixAchat;
	private String[] loyer;
	private String couleur;
	private Joueur proprietaire;
	
	public Proprietes(String nom, String couleur, int prixAchat, String[] loyer) {
		super("Propriete", nom);
		this.couleur = couleur;
		this.prixAchat = prixAchat;
		this.loyer = loyer;
		this.proprietaire = null;
	}
	
	//Getters
	public int getPrix() { return this.prixAchat; }
	
	public int getLoyer() {
		int nbProp = proprietaire.getNbPropCouleur(this.couleur);
		if(nbProp==0) return -1;
		return Integer.valueOf(this.loyer[nbProp-1]);
	}
	
	public String getCouleur() { return this.couleur; }
	
	public String getNom() { return this.nom; }
	
	public Joueur getProprietaire() { return this.proprietaire; }

	//Setters
	public void setPrix(int prix) { this.prixAchat = prix; }
<<<<<<< HEAD

=======
>>>>>>> a9dd8f4 (Implémentation du loyer dans l'interface graphique)
	
	public void setProprietaire(Joueur nouveau_proprietaire) { this.proprietaire = nouveau_proprietaire; }
	
	//toString()
	public String toString() {
		return("Prix d'achat: " + this.prixAchat + " , Loyer actuel: " + this.loyer + " , Couleur: " + this.couleur + " , Nom: " + this.nom);
	}
	
<<<<<<< HEAD
	//Verification
=======
	//Vérification
>>>>>>> a9dd8f4 (Implémentation du loyer dans l'interface graphique)
	public boolean est_Libre() {
		if(this.proprietaire == null) { return true; }
		return false;
	}
	
	public boolean coloree() {
		if(this.couleur.equals("compagnie")) { return false; }
		return true;
	}
}
