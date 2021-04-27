package serveur.monopoly;

import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {

    public static void main(String[] args) {
        ServeurMonopoly s = new ServeurMonopoly();
        try{
            ServerSocket server=new ServerSocket(666);
            while(true){
                Socket socket=server.accept();
                if(s.nbJ()<7 && !s.estLance()){
                    JoueurS j=new JoueurS(socket, s);
                    s.add(j);
                    j.start();
                    System.out.println(socket);
                }else{
                    PrintWriter pw=new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
                    pw.println("erreur");
                    pw.println("partie deja lance");
                    socket.close();
                }
            }   
        }
        catch(Exception e){
            System.out.println(e);
            e.printStackTrace();
        }
    }
    
}
