import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

class JoueurS extends Thread{
    
    private final ServeurMonopoly serveur;
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
            BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
            pw=new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"), true);
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
        String temp[];
        switch(action){
            case "message":
                info = nom + " : " + info; 
                sendToAllClients(action, info);
                break;
            case "start":
                if(pseudoNonPresent(info)){
                    pret = true;
                    nom = info;
                }else{
                    pw.println("erreur");
                    pw.println("Pseudo deja prit");
                }
                
                break;
            case "faillite":
                this.faillite = true;
                
                sendToAllClientsNotSender(action, info);
                break;
            case "carte":
                temp = info.split("-");
                int arg = Integer.parseInt(temp[2]);
                String typeAction = temp[3];
                int p = Integer.parseInt(temp[4]);
                actionCarte(typeAction, p, arg);
                
                sendToAllClientsNotSender(action, info);
                break;
            case "close":
                closeClient();
                break;
            case "achat":
                achatCase(Integer.parseInt(info));
                sendToAllClientsNotSender(action, info);
                break;
            case "lancer des":
                int[] des = this.lancer_de_des();
                this.sendMsg("lancer des", des[0] + "-" + des[1]);
                break;
            case "deplace":
                temp = info.split("-");
                //this.position = Integer.parseInt(temp[2]);
                this.argent = Integer.parseInt(temp[3]);
                this.enPrison = temp[4].equals("true");
                this.nbToursPrison = Integer.parseInt(temp[5]);
                this.faillite = temp[6].equals("true");
                //this.cartePrison = temp[7].equals("true") ? true: false;
                sendToAllClientsNotSender(action, info);
                break;
            case "fin tour" :
                serveur.setCurseur(Integer.parseInt(info));
                sendToAllClientsNotSender(action, info);
                System.out.println(serveur);
                break;
            case "vente a joueur":
                temp = info.split("-");
                venteCase(Integer.parseInt(temp[0]), Integer.parseInt(temp[2]));
                serveur.achatCase(temp[1], Integer.parseInt(temp[2]));
                sendToAllClientsNotSender(action, info);
                break;
            case "vendre":
                temp = info.split("-");
                if(temp[0].equals("hotel") || temp[0].equals("maison")){
                    venteProp(temp[0], Integer.parseInt(temp[1]), Integer.parseInt(temp[2]), Integer.parseInt(temp[3]));
                }else{
                    venteCase(Integer.parseInt(temp[2]), Integer.parseInt(temp[1]));
                }
                sendToAllClientsNotSender(action, info);
                break;
            case "defis gagnant":
                String[] t = info.split("-");
                int loyerEnJeu = Integer.parseInt(t[3]);

		if(t[0].equals("joueur")) { //Rembourse le loyer au joueur gagnant.
                    this.argent = this.argent + loyerEnJeu;
		}
		else if(t[0].equals("proprio")) { //Joueur paye deux fois le loyer, il l'a deja paye une fois donc seulement une autre fois encore.
                    loyer(loyerEnJeu,t[4]);
		}
                sendToAllClientsNotSender(action, info);
                break;
            case "loyer":
                temp = info.split("-");
                loyer(Integer.parseInt(temp[0]),temp[1]);
                break;
            case "batiment":
                temp = info.split("-");
                String type = temp[0];
                int pos = Integer.parseInt(temp[1]);
                int prix = Integer.parseInt(temp[2]);
                this.ajoutArgent(-prix);
                this.serveur.achatBatiment(type, pos);
                sendToAllClientsNotSender(action, info);
                break;
            default:
                sendToAllClientsNotSender(action, info);
                break;
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
    
    private void venteProp(String type, int num, int montant, int pos){
        this.ajoutArgent(montant);
        if(type.equals("maison")) this.serveur.venteMaisons(num, pos);
        else serveur.venteHotel(num, pos);
        
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
    
    @Override
    public String toString(){
        String rep = this.nom + ": \nArgent : " + this.argent + "\nPosition : " + this.position + "\nProprietes : ";
        for(int i: this.proprietes){
            rep = rep + i + " ";
        }
        rep = rep + "\nEn prison :" + this.enPrison + "\nNb de Tour : " + this.nbToursPrison + "\nCarte Prison : " + this.cartePrison + "\nEn Faillite : " + this.faillite + "\n";
        return rep;
    }  
    
    public int[] lancer_de_des() {
	int[] des = new int[2];
	Random aleatoire = new Random();
	for(int i = 0; i<des.length; i++) {
            int intervalle = 1 + aleatoire.nextInt(7-1);
            des[i] = intervalle;
	}
        des[0] = 1;
        des[1] = 0;
        avance(des);
	return des;
    }
    
    private void avance(int[] des){
        int posFinale = (this.position + des[0] + des[1]) %40;
        if(this.position>posFinale){
            this.ajoutArgent(2000);
        }
        this.position = posFinale;
    }

    private void actionCarte(String typeAction, int p, int argent) {
        switch (typeAction) {
            case "prelevement" :
		this.ajoutArgent(-p);
		break;
            case "immo" :
		int sommeApayer = this.getNbTotalMaisons()*p + this.getNbTotalHotels()*4*p;
		this.ajoutArgent(-p);
		break;
            case "cadeau" :
                serveur.getList().forEach(j ->{
                    if(this==j){
                        ajoutArgent(p*this.serveur.nbJ());
                    }else{
                        j.ajoutArgent(-p);
                    }
                });
                break;
            case "recette" :
		this.ajoutArgent(p);
		break;
            case "trajet" :
		if ( p != 30 ) {
                    this.argent = argent;
                    this.position = p;
		}
		break;
            case "reculer" :
		this.position = p;
                this.argent = argent;
		break;
            case "trajet spe" :
                this.position -= p;
                this.argent = argent;
		break;
            case "bonus" :
		this.cartePrison = true;
		break;
        }
    }

    private int getNbTotalMaisons() {
        int rep =0;
        for(int p: this.proprietes){
           rep = rep + this.serveur.getNbMaisons(p);
        }
        return rep;
    }

    private int getNbTotalHotels() {
        int rep =0;
        for(int p: this.proprietes){
           rep = rep + this.serveur.getNbHotel(p);
        }
        return rep;
    }
}
