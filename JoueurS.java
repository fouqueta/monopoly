package serveur.monopoly;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

class JoueurS extends Thread{
    
    private String nom;
    private final Socket socket;
    private PrintWriter pw;
    private final ArrayList<JoueurS> list;
    private boolean pret = false;
    static boolean lance = false;
    private boolean running = true;

    JoueurS(Socket socket, ArrayList<JoueurS> list) {
        this.socket = socket;
        this.list = list;
    }
    
    //Getters
    public String getNom(){
        return nom;
    }
    
    public boolean getPret(){
        return pret;
    }
    
    //Enleve un joueur de la liste de joueurs
    public void removePlayer(JoueurS j){
        list.remove(j);
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
                if(!lance){
                    tousPret();
                }
                
            }
        }
        catch(IOException e){
            if(lance) sendToAllClientsNotSender("deco", this.getNom());
            this.closeClient();
            System.out.println(e);
        }
    }
    
    //Envoie le message à tout les clients
    private void sendToAllClients(String action, String info){
        list.forEach(s -> {
            s.sendMsg(action, info);
        });
    }
    
    //Envoie le message à tout les clients sauf l'envoyeur 
    private void sendToAllClientsNotSender(String action, String info){
        list.forEach(s -> {
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
            default -> sendToAllClientsNotSender(action, info);
        }
    }
    
    //Verifie si tout les joueurs sont prêts et renvoie la liste des noms de joueurs
    private void tousPret(){
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
            sendToAllClients("start", noms);
            lance = true;
        }
    }
    
    //Ferme le client correctement
    private void closeClient(){
        try {
            pw.println("close");
            pw.println("");
            socket.close();
            ServeurMonopoly.removePlayer(this);
            list.forEach(j -> {
                j.removePlayer(this);
            });
            running = false;

            this.interrupt();
        } catch (IOException ex) {
            Logger.getLogger(JoueurS.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private boolean pseudoNonPresent(String info) {
        for(JoueurS j: list){
            if(j!=this && j.getNom().equals(info)) return false;
        }
        return true;
    }
}
