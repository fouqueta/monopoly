package monopoly;

public class Proprietes extends Cases {
	
	private int prixAchat;
	private String[] loyer;
	private String couleur;
	private Joueur proprietaire;
	private int nbMaisons;
	private boolean aHotel;
	private int prixBatiment;
	
	public Proprietes(int position, String nom, String couleur, int prixAchat, String[] loyer, int prixBatiment) {
		super(position, "Propriete", nom);
		this.couleur = couleur;
		this.prixAchat = prixAchat;
		this.loyer = loyer;
		this.proprietaire = null;
		this.nbMaisons = 0;
		this.aHotel = false;
		this.prixBatiment = prixBatiment;
	}
	
	
	//Getters
	public int getPrix() { return this.prixAchat; }
	
	public int getLoyer() {
		int nbPropFamille = proprietaire.getNbPropCouleur(this.couleur);
		if(nbPropFamille==0) return -1;
		else if (nbPropFamille!=0 && nbMaisons==0 && !aUnHotel()) { //Quand on a des proprietes d'une couleur, mais aucun batiment
			return Integer.valueOf(this.loyer[nbPropFamille-1]);
		}
		else { //Si on a au moins un batiment, le loyer se calcule en fonction du nombre et du type de batiments
			if (!aUnHotel()) { return Integer.valueOf(this.loyer[ (this.loyer.length-1) - 5 + getNbMaisons() ]); } //Voir la structure des paliers de loyers dans cases.csv
			else { return Integer.valueOf(this.loyer[this.loyer.length-1]); } //Si on a un hotel, le loyer est le dernier palier
		}
	}
	
	public String getCouleur() { return this.couleur; }
	
	public String getNom() { return this.nom; }
	
	public Joueur getProprietaire() { return this.proprietaire; }
	
	public int getNbMaisons() { return this.nbMaisons; }
	
	public boolean aUnHotel() { return this.aHotel; }
	
	public int getPrixBatiment() { return this.prixBatiment; }

	//Setters
	public void setPrix(int prix) { this.prixAchat = prix; }
	
	public void setProprietaire(Joueur nouveau_proprietaire) { this.proprietaire = nouveau_proprietaire; }
	
	public void achatMaison() {
		this.nbMaisons+=1 ;
		proprietaire.transaction(-prixBatiment);
	}
	
	public void achatHotel() {
		this.nbMaisons = 0; //Pour acheter un hotel, il faut "donner" toutes ses maisons
		this.aHotel = true;
		proprietaire.transaction(-prixBatiment);		
	}
	
	public void venteMaison() {
		this.nbMaisons-=1;
		proprietaire.transaction(prixBatiment/2); //Le prix de vente d'une maison est la moitie du prix d'achat
	}
	
	public void venteHotel() {
		this.aHotel = false;
		proprietaire.transaction(prixBatiment*5/2); //Le prix de vente d'un hotel est la moitie du prix d'achat (qui est 5 fois le prix d'une maison puisqu'on a donne toutes ses maisons)
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
	
	public boolean familleComplete() {
		Plateau plateau = new Plateau();
		return proprietaire.getNbPropCouleur(this.getCouleur()) == plateau.nbProprDansUneFamille(this.getCouleur());
	}
	
	public boolean estUniforme(String typeBatiment) { //Pour acheter des maisons, il faut que le nombre de maisons sur chaque propriete d'une meme famille soit uniforme
		if(typeBatiment.equals("maison")) { //Si on veut acheter une maison
			for(Proprietes c : proprietaire.getProprietes()){
				if(c.getCouleur().equals(this.couleur) && (this.nbMaisons+1 - c.getNbMaisons()) >= 2) { //Si quand on rajoute une maison a la propriete this, son nb de maisons devient superieur... 
					//...de 2 maisons au nb de maisons d'une des proprietes c, alors cela veut dire que l'achat de la maison sur this ne va pas respecter l'uniformite...
					return false; //...donc l'achat de la maison ne sera pas propose pour respecter l'uniformite
				}
			}
		}
		else if(typeBatiment.equals("hotel")) { //Si on veut acheter un hotel
			for(Proprietes c : proprietaire.getProprietes()){
				if(c.getCouleur().equals(this.couleur) && (c.getNbMaisons()) != 4 && !c.aUnHotel()) { //Pour pouvoir acheter un hotel sur la propriete this en respectant l'uniformite, il faut que...
					return false; //...chaque propriete c ait 4 maisons OU un hotel (et donc 0 maison), donc l'achat ne serait pas uniforme si ce n'est pas le cas
				}
			}
		}
		return true;		
	}
	
}
