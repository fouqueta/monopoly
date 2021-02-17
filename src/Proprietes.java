
public class Proprietes extends Cases {
	
	private int prixAchat;
	private int loyer;
	private String couleur;
	private String nom;
	private Joueur proprietaire;
	
	public Proprietes(int prixAchat, int loyer, String couleur, String nom) {
		super("Propriete");
		this.prixAchat = prixAchat;
		this.loyer = loyer;
		this.couleur = couleur;
		this.nom = nom;
		this.proprietaire = null;
	}
	
	//Getters
	public int getPrix() { return this.prixAchat; }
	
	public int getLoyer() { return this.loyer; }
	
	public String getCouleur() { return this.couleur; }
	
	public String getNom() { return this.nom; }
	
	public Joueur getProprietaire() { return this.proprietaire; }

	//Setters
	public void setPrix(int prix) { this.prixAchat = prix; }
	
	public void setLoyer(int loyer) { this.loyer = loyer; }
	
	public void setProprietaire(Joueur nouveau_proprietaire) { this.proprietaire = nouveau_proprietaire; }
	
	//toString()
	public String toString() {
		return("Prix d'achat: " + this.prixAchat + " , Loyer actuel: " + this.loyer + " , Couleur: " + this.couleur + " , Nom: " + this.nom);
	}
	
	//Vérification
	public boolean est_Libre() {
		if(this.proprietaire == null) { return true; }
		return false;
	}
}
