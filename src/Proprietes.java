
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

	@Override
	public String getNom() {
		return nom;
	}

	public int getPrix(){
		return this.prixAchat;
	}

	public Joueur getProp(){
		return this.proprietaire;
	}
}
