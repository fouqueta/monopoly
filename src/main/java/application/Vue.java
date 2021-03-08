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
	//TODO : Gerer les redimensionnements d'ecran pour adapter la taille des composants.
	
	//Accueil
		private AnchorPane scene_accueil;
		
	//Scene de jeu
	private Scene scene_jeu;
	private Jeu jeu;

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
	private Label[] pseudo_tab = new Label[6]; //TODO: Parametres du nombre de joueurs.
	private Pane[] joueursPane_tab = new Pane[6];
	
		//Boutons
	private Button lancer;
	private HBox boutons_box;
	private Button regles_button;
	private Button aide_button;
	private Button quitter_button;
	
	private Button achat_tab[] = new Button[6]; //TODO: Parametres du nombre de joueurs.
	private Button vente_tab[] = new Button[6]; //TODO: Parametres du nombre de joueurs.
	
	//Controleur
	private Controleur controleur;
	
	Vue(Controleur controleur){
		this.controleur = controleur;
		
		stage = new Stage();
		stage.setTitle("Monopoly");
		stage.setMaximized(true);
		root = new AnchorPane();
		
		jeu = controleur.getJeu();
		
		initilisation_scene_jeu();
		accueil_jeu();
		
		stage.show();
	}
	
	//Interface graphique : Initialisation
	void initilisation_scene_jeu() {
		scene_jeu = new Scene(root,900,600);
		scene_jeu.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
		stage.setScene(scene_jeu);	
	}

	//Interface graphique : Plateau de jeu
	void initialisation_plateau() {
		jeu_pane = new AnchorPane();
		jeu_pane.setPrefSize((tailleEcran.width*70)/100, tailleEcran.height);
		jeu_pane.setStyle("-fx-background-color: beige");
		root.getChildren().add(jeu_pane);
		
		plateau_pane = new Pane();
		plateau_pane.setPrefSize(550, 550);
		AnchorPane.setLeftAnchor(plateau_pane, (double) 100);
		AnchorPane.setTopAnchor(plateau_pane, (double) 50);
		jeu_pane.getChildren().add(plateau_pane);
		
		colonne_gauche = new VBox();
		plateau_pane.getChildren().add(colonne_gauche);
		for(int i = 10; i>=0; i--) { 
			tabCase_pane[i] = new Pane();
			tabCase_pane[i].setPrefSize(50, 50);
			tabCase_pane[i].setStyle("-fx-background-color: white; -fx-border-color: black");
			
			Label numero = new Label(String.valueOf(i));
			tabCase_pane[i].getChildren().add(numero);
			numero.setStyle("-fx-font-weight: bold");
			
			if(jeu.getPlateau().getGrille()[i].getType().equals("Propriete")) { 
				Proprietes prop = (Proprietes) jeu.getPlateau().getCases(i);
				int prix_prop = prop.getPrix();
				Label prix = new Label(String.valueOf(prix_prop));
				prix.setLayoutX(15);
				tabCase_pane[i].getChildren().add(prix);
			}
			
			
			colonne_gauche.getChildren().add(tabCase_pane[i]);
		}
		
		colonne_droite = new VBox();
		colonne_droite.setLayoutX(500);
		plateau_pane.getChildren().add(colonne_droite);
		for(int i = 20; i<31; i++) { 
			tabCase_pane[i] = new Pane();
			tabCase_pane[i].setPrefSize(50, 50);
			tabCase_pane[i].setStyle("-fx-background-color: white; -fx-border-color: black");
			
			Label numero = new Label(String.valueOf(i));
			tabCase_pane[i].getChildren().add(numero);
			numero.setStyle("-fx-font-weight: bold");
			
			if(jeu.getPlateau().getGrille()[i].getType().equals("Propriete")) { 
				Proprietes prop = (Proprietes) jeu.getPlateau().getCases(i);
				int prix_prop = prop.getPrix();
				Label prix = new Label(String.valueOf(prix_prop));
				prix.setLayoutX(15);
				tabCase_pane[i].getChildren().add(prix);
			}
			
			colonne_droite.getChildren().add(tabCase_pane[i]);
		}
		
		ligne_haut = new HBox();
		ligne_haut.setLayoutX(50);
		plateau_pane.getChildren().add(ligne_haut);
		for(int i = 11; i<20; i++) { 
			tabCase_pane[i] = new Pane();
			tabCase_pane[i].setPrefSize(50, 50);
			tabCase_pane[i].setStyle("-fx-background-color: white; -fx-border-color: black");
			
			Label numero = new Label(String.valueOf(i));
			tabCase_pane[i].getChildren().add(numero);
			numero.setStyle("-fx-font-weight: bold");
			
			if(jeu.getPlateau().getGrille()[i].getType().equals("Propriete")) { 
				Proprietes prop = (Proprietes) jeu.getPlateau().getCases(i);
				int prix_prop = prop.getPrix();
				Label prix = new Label(String.valueOf(prix_prop));
				prix.setLayoutX(15);
				tabCase_pane[i].getChildren().add(prix);
			}
			
			ligne_haut.getChildren().add(tabCase_pane[i]);
		}
		
		ligne_bas = new HBox();
		ligne_bas.setLayoutY(500);
		ligne_bas.setLayoutX(50);
		plateau_pane.getChildren().add(ligne_bas);
		for(int i = 39; i>30; i--) { 
			tabCase_pane[i] = new Pane();
			tabCase_pane[i].setPrefSize(50, 50);
			tabCase_pane[i].setStyle("-fx-background-color: white; -fx-border-color: black");
			
			Label numero = new Label(String.valueOf(i));
			tabCase_pane[i].getChildren().add(numero);
			numero.setStyle("-fx-font-weight: bold");
			
			if(jeu.getPlateau().getGrille()[i].getType().equals("Propriete")) { 
				Proprietes prop = (Proprietes) jeu.getPlateau().getCases(i);
				int prix_prop = prop.getPrix();
				Label prix = new Label(String.valueOf(prix_prop));
				prix.setLayoutX(15);
				tabCase_pane[i].getChildren().add(prix);
			}
			
			ligne_bas.getChildren().add(tabCase_pane[i]);
		}
	}
	
	//Interface graphique : Informations des joueurs
	void affichage_joueurs() {
		joueurs_pane = new AnchorPane();
		
		joueurs_pane.setPrefSize((tailleEcran.width*30)/100, tailleEcran.height);
		joueurs_pane.setStyle("-fx-background-color: white");
		joueurs_pane.setLayoutX((tailleEcran.width*70)/100);
		
		root.getChildren().add(joueurs_pane);
		
		VBox joueurs_liste = new VBox();
		joueurs_pane.getChildren().add(joueurs_liste);
		int nbr = 6; //TODO: Parametres du nombre de joueurs.
		for(int i = 0; i<nbr; i++) {
			joueursPane_tab[i] = new Pane();
			
			joueursPane_tab[i].setPrefSize((tailleEcran.width*20)/100, (int) ((tailleEcran.height-50)/nbr));
			
			Label pseudo = new Label("Joueur "+ String.valueOf(i+1)+": "+jeu.getJoueurs()[i].getNom());
			Label argent = new Label("Argent :" + jeu.getJoueurs()[i].getArgent());
			
			argent.setLayoutY(50);
			joueursPane_tab[i].setStyle("-fx-background-color: mistyrose; -fx-border-color: white");

			joueursPane_tab[i].getChildren().add(pseudo);
			joueursPane_tab[i].getChildren().add(argent);
			joueurs_liste.getChildren().add(joueursPane_tab[i]);
		}
	}
	
	void definition_label() {
		for(int i = 0; i<4; i++) {
			pseudo_tab[i] = new Label("J" + String.valueOf(i+1));
			if(i == 0) {
				pseudo_tab[i].setLayoutY(15); 
			}
			if(i == 1) {
				pseudo_tab[i].setLayoutY(15); 
				pseudo_tab[i].setLayoutX(15); 
			}
			if(i == 2) {
				pseudo_tab[i].setLayoutY(15); 
				pseudo_tab[i].setLayoutX(30); 
			}
			if(i == 3) {
				pseudo_tab[i].setLayoutY(30); 
			}
			if(i == 4) {
				pseudo_tab[i].setLayoutY(30); 
				pseudo_tab[i].setLayoutX(15); 
			}
			if(i == 5) {
				pseudo_tab[i].setLayoutY(30); 
				pseudo_tab[i].setLayoutX(30); 
			}
		}
	}
	
	void affichage_pions_initial() {
		for(int i = 0; i<4; i++) {
			tabCase_pane[0].getChildren().add(pseudo_tab[i]);
		}
	}
	
	void changement_position_pion(int numeroTableau, int depart, int arrivee) { 
		// Pour joueur 1, numeroTableau = 0.
		if((depart < 0 || depart > 39) || (arrivee < 0 || arrivee > 39)) return;
		tabCase_pane[arrivee].getChildren().add(pseudo_tab[numeroTableau]);
		tabCase_pane[depart].getChildren().remove(pseudo_tab[numeroTableau]);
	}
	
	void changement_couleur_case(int curseur, int position) {
		if(curseur == 0) {
			tabCase_pane[position].setStyle("-fx-background-color: firebrick; -fx-border-color: black");
		}
		else if(curseur == 1) {
			tabCase_pane[position].setStyle("-fx-background-color: plum; -fx-border-color: black");
		}
		else if(curseur == 2) {
			tabCase_pane[position].setStyle("-fx-background-color: coral; -fx-border-color: black");
		}
		else if(curseur == 3) {
			tabCase_pane[position].setStyle("-fx-background-color: cyan; -fx-border-color: black");
		}
		else if(curseur == 4) {
			tabCase_pane[position].setStyle("-fx-background-color: powderblue; -fx-border-color: black");
		}
		else if(curseur == 5) {
			tabCase_pane[position].setStyle("-fx-background-color: seagreen; -fx-border-color: black");
		}
	}
	
	void changement_argent(int curseur) {
		joueursPane_tab[curseur].getChildren().remove(1);
		Label argent = new Label("Argent :" + jeu.getJoueurs()[curseur].getArgent());
		
		argent.setLayoutY(50);
		joueursPane_tab[curseur].getChildren().add(argent);
	}
	
	//Interface graphique : Boutons
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
			
			achat_tab[curseur].setDisable(false);
			bouton_achat(curseur);
		});
	}
	
	void bouton_fin_de_tour() {
		Button fin = new Button("Fin");
		
		fin.setLayoutX(250);
		fin.setLayoutY(300);
		
		plateau_pane.getChildren().add(fin);
		
		fin.setOnAction(actionEvent -> {
			int curseur = jeu.getCurseur();
			achat_tab[curseur].setDisable(true);
			
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
	
	void initialisation_boutons_achat_vente() {
		VBox AV_boutons = new VBox();
		AV_boutons.setLayoutX((tailleEcran.width*20)/100);
		joueurs_pane.getChildren().add(AV_boutons);
		
		int nbr = 6; //TODO: Parametres du nombre de joueurs.
		for(int i = 0; i<nbr; i++) { 
			Pane joueur_boutons = new Pane();
			joueur_boutons.setPrefSize((tailleEcran.width*10)/100, (int) ((tailleEcran.height-50)/nbr));
			joueur_boutons.setStyle("-fx-background-color: peachpuff; -fx-border-color: white");
			
			Button achat = new Button("Achat");
			Button vente = new Button("Vente");
			achat_tab[i] = achat;
			vente_tab[i] = vente;
			
			achat.setDisable(true);
			vente.setDisable(true);
			
			achat.setLayoutY(0);
			vente.setLayoutY(50);
			
			joueur_boutons.getChildren().add(achat);
			joueur_boutons.getChildren().add(vente);
			
			AV_boutons.getChildren().add(joueur_boutons);
		}
	}
	
	void bouton_achat(int curseur) {
		int position = jeu.getJoueurs()[curseur].getPion().getPosition();
		
		if(position != 0) { //TODO: Vérifier que le joueur a assez d'argent pour disable(false) le bouton
			achat_tab[curseur].setDisable(false);
			achat_tab[curseur].setOnAction(actionEvent ->{
				controleur.controleur_achat(curseur);
				achat_tab[curseur].setDisable(true);
				changement_argent(curseur);
			}); //TODO: Case départ qui donne de l'argent des le premier tour
		}
	}
	
	//Interface graphique : Accueil
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
		tf5.setPromptText("Pseudo du joueur 6");
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
			
			definition_label();
			affichage_pions_initial();
			
			bouton_lancer_de_des();
			bouton_fin_de_tour();
			boutons_jeu();
			initialisation_boutons_achat_vente();

		});
		
		Button passer = new Button("Passer (test)");
		grid.add(passer,0,8);
		passer.setOnAction(actionEvent->{
			initialisation_plateau();
			
			affichage_joueurs();
			
			definition_label();
			affichage_pions_initial();
			
			bouton_lancer_de_des();
			bouton_fin_de_tour();
			boutons_jeu();
			initialisation_boutons_achat_vente();
		});
		
		scene_accueil.getChildren().add(grid);
		root.getChildren().add(scene_accueil);
	}
}
