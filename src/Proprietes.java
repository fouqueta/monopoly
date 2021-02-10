
public class Proprietes extends Cases {
	
	private int prixAchat;
	private int loyer;
	private String couleur;
	
	public Proprietes(int prixAchat, int loyer, String couleur) {
		super("Propriete");
		this.prixAchat = prixAchat;
		this.loyer = loyer;
		this.couleur = couleur;
	}

}
