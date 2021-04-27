package serveur.monopoly;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

class JoueurS extends Thread{
    
    private ServeurMonopoly serveur;
    private final Socket socket;
    private PrintWriter pw;
    private boolean pret = false;
    private boolean running = true;
    
    private String nom;
    private int position;
    private int argent;
    private int[] proprietes;
    private boolean enPrison;
    private boolean cartePrison;
    private int nbToursPrison;
    private boolean faillite;
    

    JoueurS(Socket socket, ServeurMonopoly s) {
        this.socket = socket;
        this.serveur = s;
        position = 0;
        argent = 10000;
        proprietes = new int[0];
    }
    
    //Getters
    public String getNom(){
        return nom;
    }
    
    public boolean getPret(){
        return pret;
    }
    
    //Envoi un message
    public void sendMsg(String action, String info){
        pw.println(action);
        pw.println(info);
    }
    
    //Run thread
    @Override
    public void run(){
        try{
            BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            pw=new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
            while(running){    
                String action = br.readLine();
                String info = br.readLine();
                System.out.println(action);
                System.out.println(info);
                build_message(action, info);
                if(!serveur.estLance()){
                    serveur.tousPret();
                }
            }
        }
        catch(IOException e){
            if(serveur.estLance()) sendToAllClientsNotSender("deco", this.getNom());
            this.closeClient();
            System.out.println(e);
        }
    }
    
    //Envoie le message à tout les clients
    private void sendToAllClients(String action, String info){
        serveur.getList().forEach(s -> {
            s.sendMsg(action, info);
        });
    }
    
    //Envoie le message à tout les clients sauf l'envoyeur 
    private void sendToAllClientsNotSender(String action, String info){
        serveur.getList().forEach(s -> {
            if(!s.getNom().equals(nom)){
                s.sendMsg(action, info);
            }
        });
    }
    
    
    //Traite le message reçu
    private void build_message(String action, String info) {
        switch(action){
            case "message" -> {
                info = nom + " : " + info; 
                sendToAllClients(action, info);
            }
            case "start" -> {
                if(pseudoNonPresent(info)){
                    pret = true;
                    nom = info;
                }else{
                    pw.println("erreur");
                    pw.println("Pseudo deja prit");
                }
                
            }
            case "close" -> closeClient();
            case "achat" -> {
                achatCase(Integer.parseInt(info));
                sendToAllClientsNotSender(action, info);
            }
            /*case "tire carte" -> {
                String carte = serveur.tireCarteChanceCommu(info);
                this.sendMsg("carte tiree", carte + "-" + info);
            }*/
            case "lancer des" ->{
                int[] des = this.lancer_de_des();
                this.sendMsg("lancer des", des[0] + "-" + des[1]);
            }
            case "deplace" -> {
                String[] temp = info.split("-");
                this.position = Integer.parseInt(temp[2]);
                this.argent = Integer.parseInt(temp[3]);
                this.enPrison = temp[4].equals("true") ? true: false;
                this.nbToursPrison = Integer.parseInt(temp[5]);
                this.faillite = temp[6].equals("true") ? true: false;
                this.cartePrison = temp[7].equals("true") ? true: false;
                sendToAllClientsNotSender(action, info);
            }
            case "fin tour" -> {
                serveur.setCurseur(Integer.parseInt(info));
                sendToAllClientsNotSender(action, info);
                System.out.println(serveur);
            }
            case "vente a joueur" -> {
                String[] temp = info.split("-");
                venteCase(Integer.parseInt(temp[0]), Integer.parseInt(temp[2]));
                serveur.achatCase(temp[1], Integer.parseInt(temp[2]));
                sendToAllClientsNotSender(action, info);
            }
            case "vendre" -> {
                String[] temp = info.split("-");
                venteCase(Integer.parseInt(temp[2]), Integer.parseInt(temp[1]));
                sendToAllClientsNotSender(action, info);
            }
            case "defis gagnant" -> {
                String[] t = info.split("-");
                int loyerEnJeu = Integer.parseInt(t[3]);

		if(t[0].equals("joueur")) { //Rembourse le loyer au joueur gagnant.
                    this.argent = this.argent + loyerEnJeu;
		}
		else if(t[0].equals("proprio")) { //Joueur paye deux fois le loyer, il l'a deja paye une fois donc seulement une autre fois encore.
                    loyer(loyerEnJeu,t[4]);
		}
                sendToAllClientsNotSender(action, info);
            }
            case "loyer" -> {
                String[] temp = info.split("-");
                loyer(Integer.parseInt(temp[0]),temp[1]);
            }
            default -> sendToAllClientsNotSender(action, info);
        }
    }
    
    public void achatCase(int prix){
        int[] temp = new int[this.proprietes.length+1];
        System.arraycopy(this.proprietes, 0, temp, 0, this.proprietes.length);
        temp[this.proprietes.length] = this.position;
        this.proprietes = temp;
        this.ajoutArgent(-prix);
    }
    
    private void venteCase(int num, int prix){
        int[] temp = new int[this.proprietes.length-1];
        int j = 0;
        for(int i: this.proprietes){
            if(i!=num){
                temp[j] = i;
                j++;
            }
        }
        this.proprietes = temp;
        this.ajoutArgent(prix);
    }
    
    public void loyer(int loyer, String nom){
        ajoutArgent(-loyer);
        serveur.loyer(loyer, nom);
    }
    
    private void deplacePos(String info) {
        this.position = Integer.parseInt(info);
    }
    
    
    //Ferme le client correctement
    private void closeClient(){
        try {
            pw.println("close");
            pw.println("");
            socket.close();
            serveur.removePlayer(this);
            running = false;

            this.interrupt();
        } catch (IOException ex) {
            Logger.getLogger(JoueurS.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private boolean pseudoNonPresent(String info) {
        for(JoueurS j: serveur.getList()){
            //System.out.println(j + " " + this + " " + j.getNom() + " " + j.socket + " " + this.socket);
            if(j!=this && j.getNom()!=null && j.getNom().equals(info)) return false;
        }
        return !info.contains("-");
    }

    void ajoutArgent(int loyer) {
        this.argent = this.argent + loyer;
    }
    
    public String toString(){
        String rep = this.nom + ": \nArgent : " + this.argent + "\nPosition : " + this.position + "\nProprietes : ";
        for(int i: this.proprietes){
            rep = rep + i + " ";
        }
        rep = rep + "\nEn prison :" + this.enPrison + "\nNb de Tour : " + this.nbToursPrison + "\nCarte Prison : " + this.cartePrison + "\nEn Faillite" + this.faillite + "\n";
        return rep;
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
}
