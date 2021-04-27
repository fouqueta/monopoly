package serveur.monopoly;

import java.io.File;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;


public class ServeurMonopoly{

    private final ArrayList<JoueurS> list;
    private boolean lance;
    private int curseur;
    /*private String[] cartesChance;
    private String[] cartesCommu;
    private Scanner scan;*/

    public ServeurMonopoly() {
        this.list = new ArrayList<>();
        this.lance = false;
        this.curseur = 0;
        //init_cartes();
    }
    
    /*public void init_cartes() {
    	new_scan("cartes.csv");
    	cartesChance = new String[16];
    	cartesCommu = new String[16];
    	int i = 0;
        scan.nextLine(); //Pour sauter la ligne des titres des categories
        while (scan.hasNextLine()) {
        	String cartes = scan.nextLine();
        	String[] attributs = cartes.split(";"); //tab de taille 4, voir cartes.csv
        	attributs[1] = attributs[1].replace("\\n", "\n");
            switch (attributs[0]) {
            	case "chance":
            		cartesChance[i] =  attributs[2];
            		break;
            	case "commu":
            		cartesCommu[i%16] = attributs[2];
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
    }*/
    
    public String toString(){
        String rep = "Curseur : " + curseur + "\n";
        for(JoueurS j: list){
            rep = rep + j + "\n";
        }
        return rep;
    }
    
    public void setCurseur(int i){
        this.curseur = i;
    }
    
    public ArrayList<JoueurS> getList(){
        return list;
    }
    
    public void add(JoueurS j){
        list.add(j);
    }
    
    public int nbJ(){
        return list.size();
    }
    
    public boolean estLance(){
        return lance;
    }
        
    //Eneleve un joueur de la liste
    public void removePlayer(JoueurS j){
        list.remove(j);
        if(list.isEmpty()) lance = false;
    }
    
    //Verifie si tout les joueurs sont prêts et envoie la liste des noms de joueurs aux clients si tous sont prets
    public void tousPret(){
        boolean b = true;
        String noms = "" ;
        for(JoueurS s: list){
            if(!s.getPret()){
                b = false;
            }else{
                if(!noms.equals("")){
                    noms = noms + "-" + s.getNom();
                }else{
                    noms = s.getNom();
                }
            }
        }
        System.out.println(noms);
        if(list.size()>=2 && b){
            sendtoAllClients("start", noms);
            lance = true;
        }
    }
    
    private void sendtoAllClients(String action, String info){
        list.forEach(j -> {
            j.sendMsg(action, info);
        });
    }

    void achatCase(String string, int prix) {
        list.forEach(j -> {
            if(j.getNom().equals(string)){
                j.achatCase(prix);
            }
        });
    }

    void loyer(int loyer, String nom) {
         list.forEach(j -> {
            if(j.getNom().equals(nom)){
                j.ajoutArgent(loyer);
            }
        });
    }
    
    /*public String tireCarteChanceCommu(String caseC) {
    	Random rand = new Random();
	int alea = rand.nextInt(16);
	String carte = null;
	if (caseC.equals("chance")) {
            carte = this.cartesChance[alea];
	}
	else if (caseC.equals("cmmu")) {
            carte = this.cartesCommu[alea];
	}
	return carte;
    }*/
    
}
