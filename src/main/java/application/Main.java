package application;
	
import javafx.application.Application;
import javafx.stage.Stage;
import monopoly.*;

public class Main extends Application {
	
	private Vue vue;
	
	@Override
	public void start(Stage primaryStage) {
		
		//Controleur controleur = new Controleur();
		Jeu jeu = new Jeu();
		Controleur controleur = new Controleur(jeu);
		vue = new Vue(controleur);
		controleur.setVue(vue);
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
