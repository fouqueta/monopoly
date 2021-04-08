package serveur.monopoly;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

class JoueurS extends Thread{
    
    String nom;
    public Socket socket;
    PrintWriter pw;
    ArrayList<JoueurS> list;
    boolean pret = false;
    static boolean lance = false;

    JoueurS(Socket socket, ArrayList<JoueurS> list) {
        this.socket = socket;
        this.list = list;
    }

    @Override
    public void run(){
        try{
            BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            pw=new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
            nom = br.readLine();
            System.out.println(nom);
            while(true){
                String action = br.readLine();
                String info = br.readLine();
                System.out.println(action);
		System.out.println(info);
                build_message(action, info);
                if(!lance){tousPret();}
            }
        }
        catch(Exception e){
            System.out.println(e);
            e.printStackTrace();
        }
    }
    
    private void sendToAllClients(String action, String info){
        System.out.println(action);
	System.out.println(info);
        for(JoueurS s: list){
            s.pw.println(action);
            s.pw.println(info);
        }
    }
    
    private void sendToAllClientsNotSender(String action, String info){
        System.out.println(action);
	System.out.println(info);
        for(JoueurS s: list){
            if(!s.getNom().equals(nom)){
                s.pw.println(action);
                s.pw.println(info);
            }
            
        }
    }
    
    /*private void sendToAClient(String action, String info, String name){
        System.out.println(action);
        System.out.println(info);
        for(JoueurS s: list){
            if(s.getNom().equals(name)){
                s.pw.println(action);
                s.pw.println(info);
                return;
            }
            
        }
    }*/
    
    public String getNom(){
        return nom;
    }

    private void build_message(String action, String info) {
        String[] temp;
        switch(action){
            case "message":
                info = nom + " : " + info; 
                sendToAllClients(action, info);
                break;
            case "start":
                pret = true;
                break;
            default:
                sendToAllClientsNotSender(action, info);
                break;
        }
    }
    
    private void tousPret(){
        boolean b = true;
        String noms = "" ;
        for(JoueurS s: list){
            if(!s.pret){
                b = false;
                
            }
            else{
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
}
