public class Jeu {

    Joueur[] joueurs;
    Plateau p;
    int curseur;

    Jeu(){
        joueurs = new Joueur[4];
        p = new Plateau();
        curseur = 0;
    }

    void affiche(){
        p.affiche();
    }

}