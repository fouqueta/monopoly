package serveur.monopoly;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;


public class ServeurMonopoly {

    private static ArrayList<JoueurS> list = new ArrayList<>();
    
    //Main qui attend les connections
    public static void main(String[] args) {
        try{
            ServerSocket server=new ServerSocket(666);
            while(true){
                Socket socket=server.accept();
                //TODO: verifier que la partie n'est pas lancé
                if(list.size()<7){
                    JoueurS serv=new JoueurS(socket, list);
                    list.add(serv);
                    serv.start();
                    System.out.println(socket);
                }else{
                    socket.close();
                }
            }   
        }
        catch(Exception e){
            System.out.println(e);
            e.printStackTrace();
        }
    }
    
    //Eneleve un joueur de la liste
    static void removePlayer(JoueurS j){
        list.remove(j);
    }
    
}
