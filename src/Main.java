
public class Main {
	
    public static void main(String[] args){
        Jeu j = new Jeu();
        j.affiche();
        j.deplace(j.getJoueurs()[0].getPion(), 6);
        j.affiche();
        
    }
    
    
}
