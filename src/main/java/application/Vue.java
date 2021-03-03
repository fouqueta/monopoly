package application;

import java.awt.Dimension;
import java.awt.Toolkit;

import javafx.scene.control.Button;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
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
	private Jeu jeu;

	//Accueil
	private AnchorPane scene_accueil;
	
		//Plateau
	private AnchorPane jeu_pane;
	private Pane plateau_pane;
	
	private Pane[] tabCase_pane = new Pane[40];
	private VBox colonne_gauche;
	private VBox colonne_droite;
	private HBox ligne_bas;
	private HBox ligne_haut;
	
		//Joueurs
	private AnchorPane joueurs_pane;
	private Label[] label_tab = new Label[6]; //TODO: Paramètres du nombre de joueurs.
	
		//Boutons
	private AnchorPane achat_vente_pane;
	private Button lancer;
	private HBox boutons_box;
	private Button regles_button;
	private Button aide_button;
	private Button quitter_button;
	
	//Controleur
	private Controleur controleur;
	
	Vue(Controleur controleur){
		this.controleur = controleur;
		
		stage = new Stage();
		stage.setTitle("Monopoly");
		stage.setMaximized(true);
		root = new AnchorPane();
		
		jeu = controleur.getJeu();
		//jeu = new Jeu();
		
		initilisation_scene_jeu();
		accueil_jeu();
		
		//Tests :
		//changement_position_pion(0, 0, 26);
		
		//System.out.println(tailleEcran.width + " " + tailleEcran.height);
		
		stage.show();
	}
	
	//Interface graphique : Initialisation
	void initilisation_scene_jeu() {
		scene_jeu = new Scene(root,900,600);
		scene_jeu.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
		stage.setScene(scene_jeu);	
	}
	
	void accueil_jeu() {
		scene_accueil = new AnchorPane();
		scene_accueil.setPrefSize(tailleEcran.width,tailleEcran.height);
		scene_accueil.setStyle("-fx-background-color: #BAEEB4");
		
		GridPane grid = new GridPane();
		grid.setVgap(10);
		grid.setPadding(new Insets(300, 700, 450, 700));
		
		TextField tf0 = new TextField ();
		tf0.setPromptText("Pseudo du joueur 1");
		GridPane.setConstraints(tf0, 0, 0);
		TextField tf1 = new TextField ();
		tf1.setPromptText("Pseudo du joueur 2");
		GridPane.setConstraints(tf1, 0, 1);
		TextField tf2 = new TextField ();
		tf2.setPromptText("Pseudo du joueur 3");
		GridPane.setConstraints(tf2, 0, 2);
		TextField tf3 = new TextField ();
		tf3.setPromptText("Pseudo du joueur 4");
		GridPane.setConstraints(tf3, 0, 3);
		TextField tf4 = new TextField ();
		tf4.setPromptText("Pseudo du joueur 5");
		GridPane.setConstraints(tf4, 0, 4);
		TextField tf5 = new TextField ();
		tf5.setPromptText("Pseudo du joueur 5");
		GridPane.setConstraints(tf5, 0, 5);
		grid.getChildren().addAll(tf0,tf1,tf2,tf3,tf4,tf5);
		
		Button valider = new Button("Valider");
		grid.add(valider,0,7);
		valider.setOnAction(actionEvent->{
			jeu.getJoueurs()[0].setNom(tf0.getText());
			jeu.getJoueurs()[1].setNom(tf1.getText());
			jeu.getJoueurs()[2].setNom(tf2.getText());
			jeu.getJoueurs()[3].setNom(tf3.getText());
			jeu.getJoueurs()[4].setNom(tf4.getText());
			jeu.getJoueurs()[5].setNom(tf5.getText());
			initialisation_plateau();
			
			affichage_joueurs();
			boutons_achat_vente();
			
			definition_label();
			affichage_pions_initial();
			
			bouton_lancer_de_des();
			bouton_fin_de_tour();
			boutons_jeu();

		});
		
		scene_accueil.getChildren().add(grid);
		root.getChildren().add(scene_accueil);
		
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
		for(int i = 10; i>=0; i--) { 
			Pane case_pane = new Pane();
			tabCase_pane[i] = case_pane;
			case_pane.setPrefSize(50, 50);
			case_pane.setStyle("-fx-background-color: pink; -fx-border-color: white");
			
			Label label = new Label(String.valueOf(i));
			case_pane.getChildren().add(label);
			
			colonne_gauche.getChildren().add(case_pane);
		}
		
		colonne_droite = new VBox();
		colonne_droite.setLayoutX(500);
		plateau_pane.getChildren().add(colonne_droite);
		for(int i = 20; i<31; i++) { 
			Pane case_pane = new Pane();
			tabCase_pane[i] = case_pane;
			case_pane.setPrefSize(50, 50);
			case_pane.setStyle("-fx-background-color: pink; -fx-border-color: white");
			
			Label label = new Label(String.valueOf(i));
			case_pane.getChildren().add(label);
			
			colonne_droite.getChildren().add(case_pane);
		}
		
		ligne_haut = new HBox();
		ligne_haut.setLayoutX(50);
		plateau_pane.getChildren().add(ligne_haut);
		for(int i = 11; i<20; i++) { 
			Pane case_pane = new Pane();
			tabCase_pane[i] = case_pane;
			case_pane.setPrefSize(50, 50);
			case_pane.setStyle("-fx-background-color: pink; -fx-border-color: white");
			
			Label label = new Label(String.valueOf(i));
			case_pane.getChildren().add(label);
			
			ligne_haut.getChildren().add(case_pane);
		}
		
		ligne_bas = new HBox();
		ligne_bas.setLayoutY(500);
		ligne_bas.setLayoutX(50);
		plateau_pane.getChildren().add(ligne_bas);
		for(int i = 39; i>30; i--) { 
			Pane case_pane = new Pane();
			tabCase_pane[i] = case_pane;
			case_pane.setPrefSize(50, 50);
			case_pane.setStyle("-fx-background-color: pink; -fx-border-color: white");
			
			Label label = new Label(String.valueOf(i));
			case_pane.getChildren().add(label);
			
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
			Label label = new Label("Joueur "+ String.valueOf(i+1)+": "+jeu.getJoueurs()[i].getNom());
			joueur_case.getChildren().add(label);
			joueur_case.setStyle("-fx-background-color: pink; -fx-border-color: white");
			joueurs_liste.getChildren().add(joueur_case);
		}
	}
	
	void definition_label() {
		for(int i = 0; i<4; i++) {
			label_tab[i] = new Label("J" + String.valueOf(i+1));
			if(i == 0) {
				label_tab[i].setLayoutY(15); 
			}
			if(i == 1) {
				label_tab[i].setLayoutY(15); 
				label_tab[i].setLayoutX(15); 
			}
			if(i == 2) {
				label_tab[i].setLayoutY(15); 
				label_tab[i].setLayoutX(30); 
			}
			if(i == 3) {
				label_tab[i].setLayoutY(30); 
			}
			if(i == 4) {
				label_tab[i].setLayoutY(30); 
				label_tab[i].setLayoutX(15); 
			}
			if(i == 5) {
				label_tab[i].setLayoutY(30); 
				label_tab[i].setLayoutX(30); 
			}
		}
	}
	
	void affichage_pions_initial() {
		for(int i = 0; i<4; i++) {
			tabCase_pane[0].getChildren().add(label_tab[i]);
		}
	}
	
	void changement_position_pion(int numeroTableau, int depart, int arrivee) { 
		// Pour joueur 1, numeroTableau = 0.
		if((depart < 0 || depart > 39) || (arrivee < 0 || arrivee > 39)) return;
		tabCase_pane[arrivee].getChildren().add(label_tab[numeroTableau]);
		tabCase_pane[depart].getChildren().remove(label_tab[numeroTableau]);
	}
	
	//Interface graphique : Boutons
	void boutons_achat_vente() {
		achat_vente_pane = new AnchorPane();
		achat_vente_pane.setPrefSize(100, tailleEcran.height);
		achat_vente_pane.setStyle("-fx-background-color: white");
		achat_vente_pane.setLayoutX(750);
		root.getChildren().add(achat_vente_pane);
	}
	
	void bouton_lancer_de_des() {
		lancer = new Button("Lancer");
		Label label_des = new Label("0,0");
		
		
		lancer.setLayoutX(250);
		lancer.setLayoutY(250);
		
		label_des.setLayoutX(250);
		label_des.setLayoutY(280);
		
		plateau_pane.getChildren().add(lancer);
		plateau_pane.getChildren().add(label_des);
		
		lancer.setOnAction(actionEvent -> {
			int curseur = jeu.getCurseur();
			int[] des = jeu.lancer_de_des();
			System.out.println(des[0] + " " + des[1]);
			controleur.controleur_lancer(des, curseur);
			lancer.setDisable(true);
		});
	}
	
	void bouton_fin_de_tour() {
		Button fin = new Button("Fin");
		
		fin.setLayoutX(250);
		fin.setLayoutY(300);
		
		plateau_pane.getChildren().add(fin);
		
		fin.setOnAction(actionEvent -> {
			controleur.controleur_fin();
			lancer.setDisable(false);
		});
	}
	
	void boutons_jeu() {
		boutons_box = new HBox();
		regles_button = new Button("Regles");
		aide_button = new Button("Aide");
		quitter_button = new Button("Quitter");
		boutons_box.getChildren().addAll(regles_button,aide_button,quitter_button);
		boutons_box.setLayoutX(300);
		boutons_box.setLayoutY(700);
		jeu_pane.getChildren().add(boutons_box);
		quitter_button.setOnAction(actionEvent -> {
			Stage stage = (Stage) quitter_button.getScene().getWindow();
		    stage.close();
		}); 
	}
}
