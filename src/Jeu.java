
public class Jeu {

    private Joueur[] joueurs;
    private Plateau plateau;
    private int curseur;

    public Jeu() {
        joueurs = new Joueur[4];
        joueurs[0] = new Joueur("1");
        joueurs[1] = new Joueur("2");
        joueurs[2] = new Joueur("3");
        joueurs[3] = new Joueur("4");
        plateau = new Plateau();
        curseur = 0;
    }

    public void affiche(){
    	plateau.actualisePosJoueurs(joueurs);
        plateau.affiche();
    }
    
    public void deplace(Pion pion, int nbCases) {
    	pion.setPosition((pion.getPosition() + nbCases) % 40);
    	
    }

	public Joueur[] getJoueurs() {
		return joueurs;
	}

}