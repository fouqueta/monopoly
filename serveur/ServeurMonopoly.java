import java.util.ArrayList;
import java.util.Random;


public class ServeurMonopoly{

    private final ArrayList<JoueurS> list;
    private boolean lance;
    private int curseur;
    private String[] cartesChance;
    private String[] cartesCommu;
    private int[][] plateau;

    public ServeurMonopoly() {
        this.list = new ArrayList<>();
        this.lance = false;
        this.curseur = 0;
        initPlateau();
        
    }
    
    private void initPlateau(){
         plateau = new int[40][2];
         for(int i=0;i<plateau.length;i++){
            for(int j=0; j<plateau[i].length;j++){
                plateau[i][j] = 0; 
            }
         }
    }
    
    public String toString(){
        String rep = "Curseur : " + curseur + "\n";
        for(JoueurS j: list){
            rep = rep + j + "\n";
        }
        rep = rep + stringPlateau();
        return rep;
    }
    
    private String stringPlateau(){
        String rep = "";
        for(int i=0;i<plateau.length;i++){
            rep = rep + plateau[i][0] + "," + plateau[i][1] + " ";
        }
        return rep + "\n";
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
    
    public int getNbMaisons(int p){
        return this.plateau[p][0];
    }
    
    public int getNbHotel(int p){
        return this.plateau[p][1];
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

    void venteMaisons(int num, int pos) {
        this.plateau[pos][0] -= num;
        if(this.plateau[pos][0]<0) this.plateau[pos][0]=0;
    }

    void venteHotel(int num, int pos) {
        this.plateau[pos][1] = 0;
    }

    void achatBatiment(String type, int pos) {
        if(type.equals("maison")){
            this.plateau[pos][0]++;
        }else{
            this.plateau[pos][0]=0;
            this.plateau[pos][1]++;
        }
    }

    void defis(JoueurS proprio, String nom, int loyerEnJeu) {
        JoueurS joueur = null;
        for(JoueurS j: list){
            if(j.getNom().equals(nom)){
                joueur = j;
            }
        }
        
        if(joueur.getArgent()>=loyerEnJeu || (joueur.getProp().length<1 && !joueur.aCarteLibPrison())){
            joueur.ajoutArgent(-loyerEnJeu);
            proprio.ajoutArgent(loyerEnJeu);
	}
        
        
    }
    
}
