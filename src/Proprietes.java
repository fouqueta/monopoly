
public class Proprietes extends Cases {
	
	int prixAchat;
	int loyer;
	String couleur;
	
	public Proprietes(String type, int prixAchat, int loyer, String couleur) {
		super("Propriete");
		this.prixAchat = prixAchat;
		this.loyer = loyer;
		this.couleur = couleur;
	}

}
