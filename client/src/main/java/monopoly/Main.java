package monopoly;

public class Main {
	
    public static void main(String[] args){
        Jeu j = new Jeu();
        String[] s = {"s","d"};
        boolean[] b = {false, false};
        j.initialisation_joueurs(s, b);
        j.deroulerPartie();        
    }    
    
}
