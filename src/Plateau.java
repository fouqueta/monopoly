
public class Plateau {

    private Cases[] grille;
    private int banque;
    private String[] posJoueurs;

    Plateau(){
        grille = new Cases[40];
        posJoueurs = new String[40];
        for(int i =0;i<40;i++){
            posJoueurs[i] = "";
        }
        posJoueurs[0] = "1,2,3,4";
    }
    
    public void actualisePosJoueurs(Joueur[] joueurs) {
    	for (int i=0; i<40; i++) {
    		posJoueurs[i] = "";
    	}
    	for (Joueur joueur : joueurs) {
			int position = joueur.getPion().getPosition();
			if (!posJoueurs[position].equals("")) {
				posJoueurs[position] = posJoueurs[position] + "," + joueur.getNom();
			}
            else {
            	posJoueurs[position] = joueur.getNom();
            }
		}
    }

    public void affiche(){
        System.out.println("-".repeat(111));
        for(int i=0; i<11;i++){
            String tmp = "|    " + (i+1);
            while(tmp.length() != 10) tmp = tmp + " ";
            System.out.print(tmp);
        }
        System.out.println("|");
        System.out.println("|         ".repeat(11) + "|");
        for(int i=0; i<11;i++){
            String tmp = "| ";
            if(posJoueurs[i].length()!=0){
                tmp = tmp + posJoueurs[i];

            }
            while(tmp.length() != 10) tmp = tmp + " ";
            System.out.print(tmp);

        }
        System.out.println("|");
        System.out.println("-".repeat(111));
        int cpt = 28;
        for(int i =11;i<20;i++){
            String tmp = "|    " + (i+1+cpt);
            while(tmp.length() != 10) tmp = tmp + " ";
            tmp = tmp + "|" + " ".repeat(89);
            String droite = "|    " + (i+1);
            while(droite.length() != 10) droite = droite + " ";
            tmp = tmp + droite + "|";
            System.out.println(tmp);

            System.out.println("|         |" + " " .repeat(89) + "|         |");
            tmp = "| ";
            if(posJoueurs[i+cpt].length()!=0){
                tmp = tmp + posJoueurs[i];

            }
            while(tmp.length() != 10) tmp = tmp + " ";
            tmp = tmp + "|" + " ".repeat(89);
            droite = "| ";
            if(posJoueurs[i].length()!=0){
                droite = droite + posJoueurs[i];

            }
            while(droite.length() != 10) droite = droite + " ";
            System.out.println(tmp + droite + "|");
            if(i!=19) System.out.println("-----------" + " ".repeat(89) + "-----------");
            cpt = cpt - 2;
        }

        System.out.println("-".repeat(111));
        for(int i=30; i>19;i--){
            String tmp = "|    " + (i+1);
            while(tmp.length() != 10) tmp = tmp + " ";
            System.out.print(tmp);
        }
        System.out.println("|");
        System.out.println("|         ".repeat(11) + "|");
        for(int i=30; i>19;i--){
            String tmp = "| ";
            if(posJoueurs[i].length()!=0){
                tmp = tmp + posJoueurs[i];

            }
            while(tmp.length() != 10) tmp = tmp + " ";
            System.out.print(tmp);

        }
        System.out.println("|");
        System.out.println("-".repeat(111));

    }

	public Cases[] getGrille() {
		return grille;
	}


}