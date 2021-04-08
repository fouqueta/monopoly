package serveur.monopoly;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;


public class ServeurMonopoly {

    static ArrayList<JoueurS> list = new ArrayList<>();
    public static void main(String[] args) {
        try{
            ServerSocket server=new ServerSocket(666);
            while(true){
                Socket socket=server.accept();
                JoueurS serv=new JoueurS(socket, list);
                list.add(serv);
                serv.start();
            }   
        }
        catch(Exception e){
            System.out.println(e);
            e.printStackTrace();
        }
    }
    
}
