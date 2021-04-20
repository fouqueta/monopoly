package monopoly;

import java.io.File;
import java.util.Scanner;

public class Plateau {

    private Cases[] grille;
    private int banque;
    private String[] posJoueurs;
    private Cartes[] cartesChance;
    private Cartes[] cartesCommu;
    private Scanner scan;

    public Plateau() {
    	init_plateau();
    	init_cartes();
    }
    
    //Getters
  	public Cases[] getGrille() {
  		return grille;
  	}

  	public Cases getCases(int position) {
  		return grille[position];
  	}
    
    public Cartes[] getCartesChance() {
		return cartesChance;
	}

	public Cartes[] getCartesCommu() {
		return cartesCommu;
	}
	
	public int nbProprDansUneFamille(String couleur) { //renvoie le nombre de proprietes qu'il y a dans la famille couleur
		int cpt = 0;
		for (Cases c : grille) {
			if ( c instanceof Proprietes && couleur.equals(((Proprietes) c).getCouleur()) ) {
				cpt++;
			}
		}
		return cpt;
	}

	//Initilisation du plateau
	public void init_plateau() {
    	new_scan("cases.csv");
    	grille = new Cases[40];
        posJoueurs = new String[40];
        int i = 0;
        scan.nextLine(); //Pour sauter la ligne des titres des categories
        while (scan.hasNextLine()) {
        	String casePlateau = scan.nextLine();
        	String[] attributs = casePlateau.split(";"); //tab de taille 6, voir cases.csv
            switch (attributs[1]) {
            	case "Proprietes": 
            		grille[i] = new Proprietes(Integer.parseInt(attributs[0]), attributs[2], attributs[3], Integer.parseInt(attributs[4]), attributs[5].split("-"), Integer.parseInt(attributs[6]));
            		break;
            	case "CasesSpeciales":
            		grille[i] = new CasesSpeciales(Integer.parseInt(attributs[0]), attributs[2], Integer.parseInt(attributs[6]));
            		break;
            	case "CasesCommunaute":
            		grille[i] = new CasesCommunaute(Integer.parseInt(attributs[0]), attributs[2]);
            		break;
            	case "CasesChance":
            		grille[i] = new CasesChance(Integer.parseInt(attributs[0]), attributs[2]);
            		break;
            }
            posJoueurs[i] = "";
        	i++;
        }
        posJoueurs[0] = "1,2,3,4";
        scan.close();
    }
    
    //Initialisation des cartes chance et communaute
    public void init_cartes() {
    	new_scan("cartes.csv");
    	cartesChance = new Cartes[16];
    	cartesCommu = new Cartes[16];
    	int i = 0;
        scan.nextLine(); //Pour sauter la ligne des titres des categories
        while (scan.hasNextLine()) {
        	String cartes = scan.nextLine();
        	String[] attributs = cartes.split(";"); //tab de taille 4, voir cartes.csv
        	attributs[1] = attributs[1].replace("\\n", "\n");
            switch (attributs[0]) {
            	case "chance":
            		cartesChance[i] = new Cartes(attributs[0], attributs[1], attributs[2], Integer.parseInt(attributs[3]));
            		break;
            	case "commu":
            		cartesCommu[i%16] = new Cartes(attributs[0], attributs[1], attributs[2], Integer.parseInt(attributs[3]));
            		break;
            }
        	i++;
        }
    	scan.close();
    }
    
    public void new_scan(String fichier) {
    	try {
    		scan = new Scanner(new File(fichier), "UTF-8");
    	}
    	catch(Exception e) {
    		System.out.println("Erreur lors d ouverture fichier:");
    		e.printStackTrace();
    		System.exit(1);
    	}
    }
    
    //Actualisation de la position des joueurs
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

    //Affichage
    private void afficheLigne(int n){
        System.out.println("-".repeat(n));
    }

    private String complete(String s, int n){
        while(s.length() != n) s = s  + " ";
        return s;
    }

    private void afficheLigneCentre(int n){
        System.out.println("----------------" + " ".repeat(n) + "----------------");
    }

    private String buildNom(int n){
        String tmp = "|" + grille[n].getNom();
        tmp = complete(tmp,15);
        return tmp;
    }

    private String buildPrix(int n){
        String tmp = "|";
        if(grille[n].type.equals("Propriete")) {
            Proprietes prop = (Proprietes) (grille[n]);
            tmp = tmp + prop.getPrix() + "e";
        }
        tmp = complete(tmp, 15);
        return tmp;
    }


    private String buildProp(int n){
        String tmp = "|";
        if(grille[n].type.equals("Propriete")) {
            Proprietes prop = (Proprietes) (grille[n]);

            if (prop.getProprietaire() != null) {
                tmp = tmp + prop.getLoyer();
                tmp = tmp + "e-" + prop.getProprietaire().getNom();
            }
        }
        tmp = complete(tmp, 15);
        return tmp;
    }

    private String buildLigneJoueur(int n){
        String tmp = "|";
        if(posJoueurs[n].length()!=0){
            tmp = tmp + posJoueurs[n];

        }
        tmp = complete(tmp, 15);
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


    private void afficheCentrePlateau(int n, int p){
        int cpt = 39-n;
        for(int i = n;i<p;i++){
            String gauche = buildNom(i+cpt);
            String droite = buildNom(i);

            System.out.println(gauche + "|" + " ".repeat(134) + droite + "|");

            String prixG = buildPrix(i+cpt);
            String prixD = buildPrix(i);

            System.out.println(prixG + "|" + " ".repeat(134) + prixD + "|");

            String propG = buildProp(i+cpt);
            String propD = buildProp(i);

            System.out.println(propG + "|" + " ".repeat(134) + propD + "|");

            String joueursG = buildLigneJoueur(i+cpt);
            String joueursD = buildLigneJoueur(i);

            System.out.println(joueursG + "|" + " ".repeat(134) + joueursD + "|");

            if(i!=19) afficheLigneCentre(134);

            cpt = cpt - 2;
        }
    }


    public void affiche(){
        afficheLigne(166);
        afficheNom(0,11);
        afficheLigne(166);

        afficheCentrePlateau(11,20);

        afficheLigne(166);
        afficheNom(30,19);
        afficheLigne(166);

    }
}