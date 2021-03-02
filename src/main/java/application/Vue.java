package application;

import java.awt.Button;
import java.awt.Dimension;
import java.awt.Toolkit;

import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import monopoly.*;

public class Vue {
	
	//Interface graphique
	private AnchorPane root;
	private Stage stage;
	private Dimension tailleEcran = Toolkit.getDefaultToolkit().getScreenSize();
	//TODO : Gérer les redimensionnements d'écran pour adapter la taille des composants.
	
	//Scene de jeu
	private Scene scene_jeu;

		//Plateau
	private AnchorPane jeu_pane;
	private Pane plateau_pane;
	private Plateau plateau;
	
	private VBox colonne_gauche;
	private VBox colonne_droite;
	private HBox ligne_bas;
	private HBox ligne_haut;
	
		//Joueurs
	private AnchorPane joueurs_pane;
	
		//Boutons
	private AnchorPane achat_vente_pane;
	
	//Controleur
	private Controleur controleur;
	
	Vue(Controleur controleur){
		this.controleur = controleur;
		
		stage = new Stage();
		stage.setTitle("Monopoly");
		stage.setMaximized(true);
		root = new AnchorPane();
		
		initilisation_scene_jeu();
		initialisation_plateau();
		affichage_joueurs();
		boutons_achat_vente();
		
		System.out.println(tailleEcran.width + " " + tailleEcran.height);
		
		stage.show();
	}
	
	//Interface graphique : Initialisation
	void initilisation_scene_jeu() {
		scene_jeu = new Scene(root,900,600);
		//scene_jeu.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
		stage.setScene(scene_jeu);	
	}
	
	//Interface graphique : Plateau de jeu
	void initialisation_plateau() {
		jeu_pane = new AnchorPane();
		jeu_pane.setPrefSize(750, tailleEcran.height);
		jeu_pane.setStyle("-fx-background-color: gray");
		root.getChildren().add(jeu_pane);
		
		plateau_pane = new Pane();
		plateau_pane.setPrefSize(550, 550);
		AnchorPane.setLeftAnchor(plateau_pane, (double) 100);
		AnchorPane.setTopAnchor(plateau_pane, (double) 50);
		jeu_pane.getChildren().add(plateau_pane);
		
		colonne_gauche = new VBox();
		plateau_pane.getChildren().add(colonne_gauche);
		for(int i = 0; i<11; i++) { 
		//for(int i = 11; i>0; i--) { 
			Pane case_pane = new Pane();
			case_pane.setPrefSize(50, 50);
			case_pane.setStyle("-fx-background-color: pink; -fx-border-color: white");
			
			Label label = new Label(String.valueOf(i));
			case_pane.getChildren().add(label);
			
			colonne_gauche.getChildren().add(case_pane);
		}
		
		colonne_droite = new VBox();
		colonne_droite.setLayoutX(500);
		plateau_pane.getChildren().add(colonne_droite);
		for(int i = 0; i<11; i++) { 
			Pane case_pane = new Pane();
			case_pane.setPrefSize(50, 50);
			case_pane.setStyle("-fx-background-color: pink; -fx-border-color: white");
			colonne_droite.getChildren().add(case_pane);
		}
		
		ligne_haut = new HBox();
		ligne_haut.setLayoutX(50);
		plateau_pane.getChildren().add(ligne_haut);
		for(int i = 0; i<9; i++) { 
			Pane case_pane = new Pane();
			case_pane.setPrefSize(50, 50);
			case_pane.setStyle("-fx-background-color: pink; -fx-border-color: white");
			ligne_haut.getChildren().add(case_pane);
		}
		
		ligne_bas = new HBox();
		ligne_bas.setLayoutY(500);
		ligne_bas.setLayoutX(50);
		plateau_pane.getChildren().add(ligne_bas);
		for(int i = 0; i<9; i++) { 
			Pane case_pane = new Pane();
			case_pane.setPrefSize(50, 50);
			case_pane.setStyle("-fx-background-color: pink; -fx-border-color: white");
			ligne_bas.getChildren().add(case_pane);
		}
	}
	
	//Interface graphique : Informations des joueurs
	void affichage_joueurs() {
		joueurs_pane = new AnchorPane();
		joueurs_pane.setPrefSize(tailleEcran.width - 750, tailleEcran.height);
		joueurs_pane.setStyle("-fx-background-color: pink");
		joueurs_pane.setLayoutX(850);
		root.getChildren().add(joueurs_pane);
		
		VBox joueurs_liste = new VBox();
		joueurs_pane.getChildren().add(joueurs_liste);
		int nbr = 6; //TODO: A remplacer par le paramètre du nombre de joueurs.
		for(int i = 0; i<nbr; i++) { 
			Pane joueur_case = new Pane();
			joueur_case.setPrefSize(tailleEcran.width - 850, (int) ((tailleEcran.height-50)/nbr));
			Label label = new Label("Joueur " + String.valueOf(i));
			joueur_case.getChildren().add(label);
			joueur_case.setStyle("-fx-background-color: pink; -fx-border-color: white");
			joueurs_liste.getChildren().add(joueur_case);
		}
	}
	
	//Interface graphique : Boutons
	void boutons_achat_vente() {
		achat_vente_pane = new AnchorPane();
		achat_vente_pane.setPrefSize(100, tailleEcran.height);
		achat_vente_pane.setStyle("-fx-background-color: white");
		achat_vente_pane.setLayoutX(750);
		root.getChildren().add(achat_vente_pane);
	}
}
