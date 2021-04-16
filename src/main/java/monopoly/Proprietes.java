package monopoly;

public class Proprietes extends Cases {
	
	private int prixAchat;
	private String[] loyer;
	private String couleur;
	private Joueur proprietaire;
	private int[] batiments;
	private int prixBatiment;
	
	public Proprietes(int position, String nom, String couleur, int prixAchat, String[] loyer, int prixBatiment) {
		super(position, "Propriete", nom);
		this.couleur = couleur;
		this.prixAchat = prixAchat;
		this.loyer = loyer;
		this.proprietaire = null;
		this.batiments = new int[0];
		this.prixBatiment = prixBatiment;
	}
	
	
	//Getters
	public int getPrix() { return this.prixAchat; }
	
	public int getLoyer() {
		int nbProp = proprietaire.getNbPropCouleur(this.couleur);
		if(nbProp==0) return -1;
		else if (nbProp!=0 && batiments.length==0) { //Quand on a des proprietes d'une couleur, mais aucun batiment
			return Integer.valueOf(this.loyer[nbProp-1]);
		}
		else { //Si j'ai au moins un batiment, le loyer se calcule en fonction du nombre et du type de batiments
			if (!aUnHotel()) { return Integer.valueOf(this.loyer[ (this.loyer.length-1) - 5 + getNbMaisons() ]); } //Voir la structure des paliers de loyers dans cases.csv
			else { return Integer.valueOf(this.loyer[this.loyer.length-1]); } //Si on a un hotel, le loyer est le dernier palier
		}
	}
	
	public String getCouleur() { return this.couleur; }
	
	public String getNom() { return this.nom; }
	
	public Joueur getProprietaire() { return this.proprietaire; }
	
	public int[] getBatiments() { return this.batiments ;}
	
	public int getNbMaisons() { 
		if (batiments[0] == 4) { return 0; }
		int i = 0, cpt = 0;
		while (batiments[i]==1) {
			cpt++;
		}
		return cpt;
	}
	
	public boolean aUnHotel() { return batiments[0]==4; }

	//Setters
	public void setPrix(int prix) { this.prixAchat = prix; }
	
	public void setProprietaire(Joueur nouveau_proprietaire) { this.proprietaire = nouveau_proprietaire; }
	
	public void achatBatiment(int typeBatiment, Joueur joueurJ) { //int 1 pour une maison, int 4 pour un hotel 
		int[] nvTabBatiments = (typeBatiment == 4) ? new int[1] : new int[this.batiments.length+1]; //Si on achete un hotel, il remplace les 4 maisons
		if (typeBatiment == 1) { //Si on achete une maison, on agrandit le tableau d'1 
			System.arraycopy(batiments, 0, nvTabBatiments, 0, batiments.length);
		}
		joueurJ.transaction(-prixBatiment);
		nvTabBatiments[nvTabBatiments.length-1] = typeBatiment; //Ajout du nouveau batiment a la derniere case
		this.batiments = nvTabBatiments; 
	}
	
	public void venteBatiment(int typeBatiment, Joueur joueurJ) {
		int[] nvTabBatiments = (typeBatiment == 4) ? new int[0] : new int[this.batiments.length-1];
		if (typeBatiment == 4) { //Si on vend un hotel, on n'a plus de batiment et on recupere la moitie de son prix d'achat (qui est 5 fois le prix d'une maison)
			joueurJ.transaction(prixBatiment*5/2); 
		}
		else { //Si on vend une maison
			System.arraycopy(batiments, 0, nvTabBatiments, 0, nvTabBatiments.length);
			joueurJ.transaction(prixBatiment/2); 
		}
		this.batiments = nvTabBatiments; 
	}
	
	//toString()
	public String toString() {
		return("Prix d'achat: " + this.prixAchat + " , Loyer actuel: " + this.loyer + " , Couleur: " + this.couleur + " , Nom: " + this.nom);
	}

	//Verification
	public boolean est_Libre() {
		if(this.proprietaire == null) { return true; }
		return false;
	}
	
	public boolean coloree() {
		if(this.couleur.equals("compagnie")) { return false; }
		return true;
	}
}
