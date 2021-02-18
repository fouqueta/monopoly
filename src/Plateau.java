
public class Plateau {

    private Cases[] grille;
    private int banque;
    private String[] posJoueurs;

    Plateau(){
        grille = new Cases[40];
        posJoueurs = new String[40];
        for(int i =0;i<40;i++){
            posJoueurs[i] = "";
            grille[i] = new Proprietes(i,i,"", String.valueOf(i));
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

    // AFFICHAGE :

    private void afficheLigne(int n){
        System.out.println("-".repeat(n));
    }

    private String complete(String s, int n){
        while(s.length() != n) s = s + " ";
        return s;
    }

    private void afficheLigneCentre(int n){
        System.out.println("-----------" + " ".repeat(n) + "-----------");
    }

    private String buildNom(int n){
        String tmp = "|    " + grille[n].getNom();
        tmp = complete(tmp,10);
        return tmp;
    }

    private String buildPrix(int n){
        String tmp = "|   ";
        if(grille[n].type.equals("Propriete")) {
            Proprietes prop = (Proprietes) (grille[n]);
            tmp = tmp + prop.getPrix() + "€";
        }
        tmp = complete(tmp, 10);
        return tmp;
    }


    private String buildProp(int n){
        String tmp = "| ";
        if(grille[n].type.equals("Propriete")) {
            Proprietes prop = (Proprietes) (grille[n]);

            if (prop.getProprietaire() != null) {
                tmp = tmp + prop.getLoyer();
                tmp = tmp + "€ - " + prop.getProprietaire().getNom();
            }
        }
        tmp = complete(tmp, 10);
        return tmp;
    }

    private String buildLigneJoueur(int n){
        String tmp = "| ";
        if(posJoueurs[n].length()!=0){
            tmp = tmp + posJoueurs[n];

        }
        tmp = complete(tmp, 10);
        return tmp;
    }

    private void afficheNom(int n, int p){
        if(n<p){
            for(int i=n; i<p;i++){
                System.out.print(buildNom(i));
            }
        }else{
            for(int i=n; i>p;i--){
                System.out.print(buildNom(i));
            }
        }
        System.out.println("|");
        affichePrix(n,p);
        afficheProp(n,p);
        afficheJoueurs(n,p);
    }

    private void affichePrix(int n, int p){
        if(n<p){
            for(int i=n; i<p;i++){
                System.out.print(buildPrix(i));
            }
        }else{
            for(int i=n; i>p;i--){
                System.out.print(buildPrix(i));
            }
        }
        System.out.println("|");
    }

    private void afficheProp(int n, int p){
        if(n<p){
            for(int i=n; i<p;i++){
                System.out.print(buildProp(i));
            }
        }else{
            for(int i=n; i>p;i--){
                System.out.print(buildProp(i));
            }
        }
        System.out.println("|");
    }

    private void afficheJoueurs(int n, int p){
        if(n<p){
            for(int i=n; i<p;i++){
                System.out.print(buildLigneJoueur(i));
            }
        }else{
            for(int i=n; i>p;i--){
                System.out.print(buildLigneJoueur(i));
            }
        }
        System.out.println("|");
    }


    private void afficheCentrePlateau(){
        int cpt = 28;
        for(int i =11;i<20;i++){
            String gauche = buildNom(i+cpt);
            String droite = buildNom(i);

            System.out.println(gauche + "|" + " ".repeat(89) + droite + "|");

            String prixG = buildPrix(i+cpt);
            String prixD = buildPrix(i);

            System.out.println(prixG + "|" + " ".repeat(89) + prixD + "|");

            String propG = buildProp(i+cpt);
            String propD = buildProp(i);

            System.out.println(propG + "|" + " ".repeat(89) + propD + "|");

            String joueursG = buildLigneJoueur(i+cpt);
            String joueursD = buildLigneJoueur(i);

            System.out.println(joueursG + "|" + " ".repeat(89) + joueursD + "|");

            if(i!=19) afficheLigneCentre(89);

            cpt = cpt - 2;
        }
    }


    public void affiche(){
        afficheLigne(111);
        afficheNom(0,11);
        afficheLigne(111);

        afficheCentrePlateau();

        afficheLigne(111);
        afficheNom(30,19);
        afficheLigne(111);

    }

	public Cases[] getGrille() {
		return grille;
	}

	public Cases getCases(int position) {
		return grille[position];
	}
}