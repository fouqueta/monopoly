import java.util.Random;

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
    	//fin du tour quand le deplacement a ete fait
    	finTour();
    	
    }

	public Joueur[] getJoueurs() {
		return joueurs;
	}

    public int[] lancer_de_des() {
		int[] des = new int[2];
		Random aleatoire = new Random();
		for(int i = 0; i<des.length; i++) {
			int intervalle = 1 + aleatoire.nextInt(7-1);
			des[i] = intervalle;
		}
		return des;
	}
    
    public void debutTour() {
    	System.out.println("Joueur "+curseur+", c'est a vous de jouer !");
    	String s = joueurs[curseur].questionDes();
    	if (s=="go") {
    		lancer_de_des();
    		//TODO: Faire le cas prison pour plus tard (dans lancer_de_des ?)
    	}else debutTour();
    	
    }
    
    public void finTour() {
    	if (curseur==4) {
    		curseur=0;
    	}else {
    		curseur++;
    	}
    	debutTour();
    }
    
}