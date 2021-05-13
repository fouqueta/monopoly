package application;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Scanner;

import javafx.animation.PauseTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.util.Duration;
import monopoly.Cartes;
import monopoly.Cases;
import monopoly.Jeu;
import monopoly.Joueur;
import monopoly.Proprietes;

public class Vue {
	
	//Interface graphique
	private AnchorPane root;
	private Stage stage;
	private Dimension tailleEcran = Toolkit.getDefaultToolkit().getScreenSize();
	private double panePlateau_x;
	private double panePlateau_y;
	private double police = tailleEcran.getHeight()/500;

	//Accueil
	private AnchorPane accueil_pane;
		
	//Scene de jeu
	private Scene scene_jeu;
	private Jeu jeu;

		//Plateau
	private AnchorPane panePlateau; //jeu_pane
	private Pane revente_pane;
	//private Pane joueur_boutons;

	private Pane[] casesPlateau = new Pane[40]; //tabCase_pane
	private double casesPlateau_x;
	private double casesPlateau_y;
	private VBox colonne_gauche;
	private VBox colonne_droite;
	private HBox ligne_bas;
	private HBox ligne_haut;
	private HBox[] maisonsPlateau_tab = new HBox[40];
	private HBox[] hotelsPlateau_tab = new HBox[40];
	private Image maison_image;
	private Image hotel_image;

	private Label desLabel = new Label();

		//Proprietaires
	private int[] proprietaires = new int[40];
	
		//Joueurs
	private int nbJoueurs;
	private AnchorPane paneJoueurs; //joueurs_pane
	private Label[] pionLabel = new Label[6]; //pseudo_tab
	private Pane[] infoJoueurs = new Pane[6]; //joueursPane_tab
	private Label joueur_actuel;
	
		//Boutons
	private VBox boutonsJoueurs;
	private Button lancer;
	private Button fin;
	private Button achat;
	private Button vente;
	private Button prison;

	private MenuItem maison_menuItem;
	private MenuItem hotel_menuItem;
	private MenuButton achatBatiments_menu;

	private HBox boutons_box;
	private Button regles_button;
	private Button quitter_button;
	private Button historique_button;
	private Button tchat_button;

	private Button achat_tab[] = new Button[6];
	private Button vente_tab[] = new Button[6];
	private Button prison_tab[] = new Button[6];
	private MenuButton[] achatBatiments_menu_tab = new MenuButton[6];
	private Button nom_proprietes_button[];
	private MenuButton menu_proprietesBat_revente[];
	private MenuItem venteMaison;
	private MenuItem venteHotel;
	private Button venteCartePrison;

	private Button defis;
	private Button defis_tab[] = new Button[6];

	//Controleur
	private Controleur controleur;
		
	//Historique(Local/Reseau)
	private AnchorPane rootHisto;
	private Stage stageHisto;
	private Scene sceneHisto;
	private Label historique_tab[];
	private VBox historiqueVBox;

	//Tchat(Reseau)
	private AnchorPane rootTchat;
	private Stage stageTchat;
	private Scene sceneTchat;
	private Label tchat_tab[];
	private VBox tchatVBox;

	private Thread t;

	public Vue(Controleur controleur){
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
	
	public int getTabProprietaires(int position) { return this.proprietaires[position]; }
	
	//Interface graphique : Initialisation
	void initilisation_scene_jeu() {
		scene_jeu = new Scene(root,900,600);
		//scene_jeu.getStylesheets().add("application.css");
		stage.setScene(scene_jeu);	
	}

	//Interface graphique : Plateau de jeu
	void initialisation_plateau() {
		panePlateau = new AnchorPane();
		panePlateau.setPrefSize((tailleEcran.width*80)/100, tailleEcran.height);
		panePlateau.setStyle("-fx-background-color: beige");
		root.getChildren().add(panePlateau);
		
		panePlateau_x = panePlateau.getPrefWidth();
		panePlateau_y = panePlateau.getPrefHeight();
		
		casesPlateau_x = (panePlateau_x*8)/100;
		casesPlateau_y = (panePlateau_y*8)/100;

		colonne_gauche = new VBox();
		AnchorPane.setLeftAnchor(colonne_gauche, (double) (panePlateau_x*2)/100);
		AnchorPane.setTopAnchor(colonne_gauche, (double) (panePlateau_y*2)/100);
		panePlateau.getChildren().add(colonne_gauche);
		for(int i = 10; i>=0; i--) { 
			casesPlateau[i] = new Pane();
			casesPlateau[i].setPrefSize(casesPlateau_x, casesPlateau_y);
			casesPlateau[i].setStyle("-fx-background-color: white; -fx-border-color: black");
			
			Label numero = new Label(String.valueOf(i));
			numero.setLayoutX((casesPlateau_x*5)/100);
			casesPlateau[i].getChildren().add(numero);
			numero.setStyle("-fx-font-weight: bold");
			
			if(jeu.getPlateau().getGrille()[i].getType().equals("Propriete")) { 
				Proprietes prop = (Proprietes) jeu.getPlateau().getCases(i);
				int prix_prop = prop.getPrix();
				Label prix = new Label(String.valueOf(prix_prop));
				prix.setLayoutX((casesPlateau_x*20)/100);
				casesPlateau[i].getChildren().add(prix);
			}			
			colonne_gauche.getChildren().add(casesPlateau[i]);
		}
		
		colonne_droite = new VBox();
		AnchorPane.setRightAnchor(colonne_droite, (double) (panePlateau_x*9.90)/100);
		AnchorPane.setTopAnchor(colonne_droite, (double) (panePlateau_y*2)/100);
		panePlateau.getChildren().add(colonne_droite);
		for(int i = 20; i<31; i++) { 
			casesPlateau[i] = new Pane();
			casesPlateau[i].setPrefSize(casesPlateau_x, casesPlateau_y);
			casesPlateau[i].setStyle("-fx-background-color: white; -fx-border-color: black");
			
			Label numero = new Label(String.valueOf(i));
			numero.setLayoutX((casesPlateau_x*5)/100);
			casesPlateau[i].getChildren().add(numero);
			numero.setStyle("-fx-font-weight: bold");
			
			if(jeu.getPlateau().getGrille()[i].getType().equals("Propriete")) { 
				Proprietes prop = (Proprietes) jeu.getPlateau().getCases(i);
				int prix_prop = prop.getPrix();
				Label prix = new Label(String.valueOf(prix_prop));
				prix.setLayoutX((casesPlateau_x*30)/100);
				casesPlateau[i].getChildren().add(prix);
			}
			
			colonne_droite.getChildren().add(casesPlateau[i]);
		}
		
		ligne_haut = new HBox();
		AnchorPane.setLeftAnchor(ligne_haut, (double) (panePlateau_x*10)/100);
		AnchorPane.setTopAnchor(ligne_haut, (double) (panePlateau_y*2)/100);
		panePlateau.getChildren().add(ligne_haut);
		for(int i = 11; i<20; i++) { 
			casesPlateau[i] = new Pane();
			casesPlateau[i].setPrefSize(casesPlateau_x, casesPlateau_y);
			casesPlateau[i].setStyle("-fx-background-color: white; -fx-border-color: black");
			
			Label numero = new Label(String.valueOf(i));
			numero.setLayoutX((casesPlateau_x*5)/100);
			casesPlateau[i].getChildren().add(numero);
			numero.setStyle("-fx-font-weight: bold");
			
			if(jeu.getPlateau().getGrille()[i].getType().equals("Propriete")) { 
				Proprietes prop = (Proprietes) jeu.getPlateau().getCases(i);
				int prix_prop = prop.getPrix();
				Label prix = new Label(String.valueOf(prix_prop));
				prix.setLayoutX((casesPlateau_x*30)/100);
				casesPlateau[i].getChildren().add(prix);
			}
			
			ligne_haut.getChildren().add(casesPlateau[i]);
		}
		
		ligne_bas = new HBox();
		AnchorPane.setLeftAnchor(ligne_bas, (double) (panePlateau_x*10)/100);
		AnchorPane.setTopAnchor(ligne_bas, (double) (panePlateau_y*82.5)/100);
		panePlateau.getChildren().add(ligne_bas);
		for(int i = 39; i>30; i--) { 
			casesPlateau[i] = new Pane();
			casesPlateau[i].setPrefSize(casesPlateau_x, casesPlateau_y);
			casesPlateau[i].setStyle("-fx-background-color: white; -fx-border-color: black");
			
			Label numero = new Label(String.valueOf(i));
			numero.setLayoutX((casesPlateau_x*5)/100);
			casesPlateau[i].getChildren().add(numero);
			numero.setStyle("-fx-font-weight: bold");
			
			if(jeu.getPlateau().getGrille()[i].getType().equals("Propriete")) { 
				Proprietes prop = (Proprietes) jeu.getPlateau().getCases(i);
				int prix_prop = prop.getPrix();
				Label prix = new Label(String.valueOf(prix_prop));
				prix.setLayoutX((casesPlateau_x*30)/100);
				casesPlateau[i].getChildren().add(prix);
			}
			
			ligne_bas.getChildren().add(casesPlateau[i]);
		}
		
		joueur_actuel = new Label("Au tour de J"+ Integer.toString(jeu.getCurseur()+1));
		joueur_actuel.setLayoutX((panePlateau_x*39)/100);
		joueur_actuel.setLayoutY((panePlateau_y*75)/100);
		joueur_actuel.setFont(new Font("Arial", 30));
		panePlateau.getChildren().add(joueur_actuel);
		
		initialisation_familles();
		initialisation_casesSpeciales();
		initialisation_labelDes();
		initialisation_batiments();
	}
	
	void initialisation_casesSpeciales() {
		Label com1 = new Label("Commu.");
		com1.setLayoutX((casesPlateau_x*20)/100);
		casesPlateau[2].getChildren().add(com1);
		
		Label com2 = new Label("Commu.");
		com2.setLayoutX((casesPlateau_x*30)/100);
		casesPlateau[17].getChildren().add(com2);
		
		Label com3 = new Label("Commu.");
		com3.setLayoutX((casesPlateau_x*30)/100);
		casesPlateau[33].getChildren().add(com3);
		
		Label ch1 = new Label("Chance");
		ch1.setLayoutX((casesPlateau_x*20)/100);
		casesPlateau[7].getChildren().add(ch1);
		
		Label ch2 = new Label("Chance");
		ch2.setLayoutX((casesPlateau_x*30)/100);
		casesPlateau[22].getChildren().add(ch2);
		
		Label ch3 = new Label("Chance");
		ch3.setLayoutX((casesPlateau_x*30)/100);
		casesPlateau[36].getChildren().add(ch3);
		
		Label imp1 = new Label("Impots");
		imp1.setLayoutX((casesPlateau_x*20)/100);
		casesPlateau[4].getChildren().add(imp1);
		
		Label imp2 = new Label("Taxe");
		imp2.setLayoutX((casesPlateau_x*30)/100);
		casesPlateau[38].getChildren().add(imp2);
		
		Label prison = new Label("Prison");
		prison.setLayoutX((casesPlateau_x*30)/100);
		casesPlateau[10].getChildren().add(prison);
		
		Label allerPrison = new Label("->Prison");
		allerPrison.setLayoutX((casesPlateau_x*30)/100);
		casesPlateau[30].getChildren().add(allerPrison);
		
		Label parc = new Label("Parc");
		parc.setLayoutX((casesPlateau_x*30)/100);
		casesPlateau[20].getChildren().add(parc);
	}
	
	void initialisation_familles() {
		//Famille violette
		for(int i = 1; i<4; i++) {
			Cases case_curseur = jeu.getPlateau().getGrille()[i];
			if(case_curseur.getType().equals("Propriete")) {
				Rectangle rec = bordure("violette");
				rec.setOpacity(0.7);
				rec.setViewOrder(0.5);
				casesPlateau[i].getChildren().add(rec);
			}
		}
		
		//Famille ciel
		for(int i = 6; i<10; i++) {
			Cases case_curseur = jeu.getPlateau().getGrille()[i];
			if(case_curseur.getType().equals("Propriete")) {
				Rectangle rec = bordure("ciel");
				rec.setOpacity(0.7);
				rec.setViewOrder(0.5);
				casesPlateau[i].getChildren().add(rec);
			}
		}
		
		//Famille rose
		for(int i = 11; i<15; i++) {
			Cases case_curseur = jeu.getPlateau().getGrille()[i];
			if(case_curseur.getType().equals("Propriete") && i != 12) {
				Rectangle rec = bordure("rose");
				rec.setOpacity(0.7);
				rec.setViewOrder(0.5);
				casesPlateau[i].getChildren().add(rec);
			}
		}
		
		//Famille orange
		for(int i = 16; i<20; i++) {
			Cases case_curseur = jeu.getPlateau().getGrille()[i];
			if(case_curseur.getType().equals("Propriete")) {
				Rectangle rec = bordure("orange");
				rec.setOpacity(0.7);
				rec.setViewOrder(0.5);
				casesPlateau[i].getChildren().add(rec);
			}
		}
		
		//Famille rouge
		for(int i = 21; i<25; i++) {
			Cases case_curseur = jeu.getPlateau().getGrille()[i];
			if(case_curseur.getType().equals("Propriete")) {
				Rectangle rec = bordure("rouge");
				rec.setOpacity(0.7);
				rec.setViewOrder(0.5);
				casesPlateau[i].getChildren().add(rec);
			}
		}
		
		//Famille jaune
		for(int i = 26; i<30; i++) {
			Cases case_curseur = jeu.getPlateau().getGrille()[i];
			if(case_curseur.getType().equals("Propriete") && i != 28) {
				Rectangle rec = bordure("jaune");
				rec.setOpacity(0.7);
				rec.setViewOrder(0.5);
				casesPlateau[i].getChildren().add(rec);
			}
		}
		
		//Famille verte
		for(int i = 31; i<35; i++) {
			Cases case_curseur = jeu.getPlateau().getGrille()[i];
			if(case_curseur.getType().equals("Propriete")) {
				Rectangle rec = bordure("verte");
				rec.setOpacity(0.7);
				rec.setViewOrder(0.5);
				casesPlateau[i].getChildren().add(rec);
			}
		}
		
		//Famille bleue
		for(int i = 37; i<40; i++) {
			Cases case_curseur = jeu.getPlateau().getGrille()[i];
			if(case_curseur.getType().equals("Propriete")) {
				Rectangle rec = bordure("bleue");
				rec.setOpacity(0.7);
				rec.setViewOrder(0.5);
				casesPlateau[i].getChildren().add(rec);
			}
		}
	}
	
	Rectangle bordure(String couleur) {
		Rectangle rec = new Rectangle();
		rec.setLayoutX(1);
		rec.setWidth((casesPlateau_x*98)/100);
		rec.setHeight((casesPlateau_y*25)/100);
		switch(couleur) {
		case "violette":
			rec.setFill(Color.rgb(125,122,188));
			break;
		case "ciel":
			rec.setFill(Color.rgb(109,157,197));
			break;
		case "rose":
			rec.setFill(Color.rgb(238,180,179));
			break;
		case "orange":
			rec.setFill(Color.rgb(255,158,31));
			break;
		case "rouge":
			rec.setFill(Color.rgb(163,0,33));
			break;
		case "jaune":
			rec.setFill(Color.rgb(255,196,61));
			break;
		case "verte":
			rec.setFill(Color.rgb(185,255,183));
			break;
		case "bleue":
			rec.setFill(Color.rgb(16,37,66));
			break;
		}
		return rec;
	}
	
	public void initialisation_batiments() {
		try {
			FileInputStream inputstreamM = new FileInputStream("src/main/resources/maison.png");
			FileInputStream inputstreamH = new FileInputStream("src/main/resources/hotel.png");
			maison_image = new Image(inputstreamM);
			hotel_image = new Image(inputstreamH);
		}
    	catch(Exception e) {
    		System.out.println("Erreur lors d ouverture fichier:");
    		e.printStackTrace();
    		System.exit(1);
    	}
//		for (int i=0; i<40; i++) {
//			Cases caseC = jeu.getPlateau().getGrille()[i];
//			if (caseC instanceof Proprietes) {
//				if ( !((Proprietes) caseC).getCouleur().equals("gare") && !((Proprietes) caseC).getCouleur().equals("compagnie") ) {
////					HBox batiments = new HBox();
////					batiments.setLayoutX(2);
////					batiments.setLayoutY(52);
////					batiments.getChildren().addAll(maison, hotel);
//					maisonsPlateau_tab[i] = new HBox();
//					hotelsPlateau_tab[i] = new HBox();
//					casesPlateau[i].getChildren().addAll(maisonsPlateau_tab[i], hotelsPlateau_tab[i]);
//					maisonsPlateau_tab[i].setVisible(false);
//					hotelsPlateau_tab[i].setVisible(false);
//				}
//			}
//		}
	}

	public void actualisation_HBoxImagesMaisons(Proprietes p) {
		casesPlateau[p.getPosition()].getChildren().remove(maisonsPlateau_tab[p.getPosition()]);
		if (p.getNbMaisons()==0) { return; } //Si on vend toutes les maisons d'un coup, il faut juste remove l'ancienne HBox
		casesPlateau[p.getPosition()].getChildren().remove(hotelsPlateau_tab[p.getPosition()]);
		HBox maisons = new HBox();
		maisons.setLayoutX(panePlateau_y*0.6/100);
		maisons.setLayoutY(panePlateau_y*6.5/100);
		for (int i=0; i<p.getNbMaisons(); i++) {
			maisons.getChildren().add(newImageViewMaison());
		}
		maisonsPlateau_tab[p.getPosition()] = maisons;
		casesPlateau[p.getPosition()].getChildren().add(maisons);
	}

	public void actualisation_HBoxImageHotel(Proprietes p) {
		casesPlateau[p.getPosition()].getChildren().remove(hotelsPlateau_tab[p.getPosition()]);
		if (!p.aUnHotel()) { return; } //Si on vend un hotel, il faut juste remove l'ancienne HBox hotelsPlateau_tab[p.getPosition()]
		casesPlateau[p.getPosition()].getChildren().remove(maisonsPlateau_tab[p.getPosition()]);
		HBox hotel = new HBox();
		hotel.setLayoutX(panePlateau_y*0.6/100);
		hotel.setLayoutY(panePlateau_y*6.5/100);
		hotel.getChildren().add(newImageViewHotel());
		hotelsPlateau_tab[p.getPosition()] = hotel;
		casesPlateau[p.getPosition()].getChildren().add(hotel);
	}

	public ImageView newImageViewMaison() {
		ImageView maison = new ImageView(maison_image);
		maison.setFitHeight(panePlateau_y*1.4/100);
		maison.setPreserveRatio(true);
		return maison;
	}

	public ImageView newImageViewHotel()  {
		ImageView hotel = new ImageView(hotel_image);
		hotel.setFitHeight(panePlateau_y*1.4/100);
		hotel.setPreserveRatio(true);
		return hotel;
	}


	//Interface graphique : Informations des joueurs
	void affichage_joueurs() {
		paneJoueurs = new AnchorPane();
		
		paneJoueurs.setPrefSize((tailleEcran.width*20)/100, tailleEcran.height);
		paneJoueurs.setStyle("-fx-background-color: white");
		paneJoueurs.setLayoutX((tailleEcran.width*80)/100);
		
		root.getChildren().add(paneJoueurs);
		
		VBox joueurs_liste = new VBox();
		paneJoueurs.getChildren().add(joueurs_liste);
		int nbr = jeu.getNbJ();
		for(int i = 0; i<nbr; i++) {
			infoJoueurs[i] = new Pane();
			
			infoJoueurs[i].setPrefSize((tailleEcran.width*20)/100, (int) ((tailleEcran.height-50)/nbr));
			
			Label pseudo = new Label("Joueur "+ String.valueOf(i+1)+": "+jeu.getJoueurs()[i].getNom());
			Label argent = new Label("Argent :" + jeu.getJoueurs()[i].getArgent());
			
			argent.setLayoutY(20);
			argent.setLayoutX(10);
			
			pseudo.setLayoutX(10);
			
			//joueursPane_tab[i].setStyle("-fx-background-color: mistyrose; -fx-border-color: white");
			infoJoueurs_style(i);
			
			infoJoueurs[i].getChildren().add(pseudo);
			infoJoueurs[i].getChildren().add(argent);
			joueurs_liste.getChildren().add(infoJoueurs[i]);
		}
	}
	
	void infoJoueurs_style(int i) {
		switch(i){
			case 0:
				infoJoueurs[0].setStyle("-fx-background-color: #C4E6E9; -fx-border-color: white");
				break;
			case 1:
				infoJoueurs[1].setStyle("-fx-background-color: #F6E8A2; -fx-border-color: white");
				break;
			case 2:
				infoJoueurs[2].setStyle("-fx-background-color: #DDBDBB; -fx-border-color: white");
				break;
			case 3:
				infoJoueurs[3].setStyle("-fx-background-color: #ADBAA1; -fx-border-color: white");
				break;
			case 4:
				infoJoueurs[4].setStyle("-fx-background-color: #B2A9C6; -fx-border-color: white");
				break;
			case 5:
				infoJoueurs[5].setStyle("-fx-background-color: #E6B589; -fx-border-color: white");
				break;
		}
	}
	
	void pionLabel_positionnement() {
		for(int i = 0; i< jeu.getNbJ(); i++) {
			pionLabel[i] = new Label("J" + String.valueOf(i+1));
			if(i == 0) {
				pionLabel[i].setLayoutY((casesPlateau_y*25)/100);
				pionLabel[i].setLayoutX((casesPlateau_x*5)/100);
			}
			if(i == 1) {
				pionLabel[i].setLayoutY((casesPlateau_y*25)/100);
				pionLabel[i].setLayoutX((casesPlateau_x*25)/100);
			}
			if(i == 2) {
				pionLabel[i].setLayoutY((casesPlateau_y*25)/100);
				pionLabel[i].setLayoutX((casesPlateau_x*45)/100);
			}
			if(i == 3) {
				pionLabel[i].setLayoutY((casesPlateau_y*50)/100);
				pionLabel[i].setLayoutX((casesPlateau_x*5)/100);
			}
			if(i == 4) {
				pionLabel[i].setLayoutY((casesPlateau_y*50)/100);
				pionLabel[i].setLayoutX((casesPlateau_x*25)/100);
			}
			if(i == 5) {
				pionLabel[i].setLayoutY((casesPlateau_y*50)/100);
				pionLabel[i].setLayoutX((casesPlateau_x*45)/100);
			}
		}
	}
	
	void affichage_pions_initial() {
		for(int i = 0; i< jeu.getNbJ(); i++) {
			casesPlateau[0].getChildren().add(pionLabel[i]);
		}
	}
	
	void changement_position_pion(int numeroTableau, int depart, int arrivee) { 
		// Pour joueur 1, numeroTableau = 0.
		if((depart < 0 || depart > 39) || (arrivee < 0 || arrivee > 39)) return;
		casesPlateau[arrivee].getChildren().add(pionLabel[numeroTableau]);
		casesPlateau[depart].getChildren().remove(pionLabel[numeroTableau]);
	}
	
	void changement_couleur_case(int curseur, int position) {
		switch(curseur) {
		case 0:
			casesPlateau[position].setStyle("-fx-background-color: #C4E6E9; -fx-border-color: white");
			break;
		case 1:
			casesPlateau[position].setStyle("-fx-background-color: #F6E8A2; -fx-border-color: white");
			break;
		case 2:
			casesPlateau[position].setStyle("-fx-background-color: #DDBDBB; -fx-border-color: white");
			break;
		case 3:
			casesPlateau[position].setStyle("-fx-background-color: #ADBAA1; -fx-border-color: white");
			break;
		case 4:
			casesPlateau[position].setStyle("-fx-background-color: #B2A9C6; -fx-border-color: white");
			break;
		case 5:
			casesPlateau[position].setStyle("-fx-background-color: #E6B589; -fx-border-color: white");
			break;
		}
	}
	
	void changement_couleur_case_blanche(int position) {
		casesPlateau[position].setStyle("-fx-background-color: #FFFFFF; -fx-border-color: black");
	}
	
	void changement_argent(int curseur) {
		infoJoueurs[curseur].getChildren().remove(1);
		Label argent = new Label("Argent :" + jeu.getJoueurs()[curseur].getArgent());
		
		argent.setLayoutY(20);
		argent.setLayoutX(10);
		infoJoueurs[curseur].getChildren().add(argent);
	}
	
	public void changement_joueur_actuel() {
		panePlateau.getChildren().remove(joueur_actuel);
		joueur_actuel = new Label("Au tour de J"+ String.valueOf(jeu.getCurseur()+1));
		joueur_actuel.setLayoutX((panePlateau_x*39)/100);
		joueur_actuel.setLayoutY((panePlateau_y*75)/100);
		joueur_actuel.setFont(new Font("Arial", 30));

		panePlateau.getChildren().add(joueur_actuel);

		if(jeu.isReseau() && !(jeu.getJoueurReseau() == jeu.getJoueurs()[jeu.getCurseur()])){
			lancer.setDisable(true);
			fin.setDisable(true);
		}
		else{
			lancer.setDisable(false);
			fin.setDisable(false);
		}

	}
	
	public void raz(int curseur) {
		//achat_tab[curseur].setDisable(true);
		achatBatiments_menu_tab[curseur].setDisable(true);
		defis_tab[curseur].setDisable(true);
		venteHotel = null;
		venteMaison = null;
		venteCartePrison = null;
	}

	public void affichage_revente_proprietes(int curseur, int montant, Cartes carteTiree) {
		raz(curseur);
		revente_pane = new Pane();
		revente_pane.setPrefSize((panePlateau_x*50)/100, (panePlateau_y*50)/100);
		revente_pane.setStyle("-fx-background-color: white; -fx-border-color: black");
		revente_pane.setLayoutX((panePlateau_x*21)/100);
		revente_pane.setLayoutY((panePlateau_y*15)/100);
		
		Label texte = new Label ("Joueur "+String.valueOf(jeu.getCurseur()+1)+", vous n'avez plus d'argent pour payer la somme due s'elevant a " + montant +"e.\nVendez une/des propriete(s) :");
		texte.setFont(new Font("Arial", police*9));
		texte.setPadding(new Insets(15, 15, 15, 15));
		revente_pane.getChildren().add(texte);
		
		double margeEspace = panePlateau_y*0.08;
		Joueur joueurJ = jeu.getJoueurs()[curseur];
		int nbPropSansBatiment = joueurJ.getProprietes().length - joueurJ.getNbPropAvecBatiments();
		nom_proprietes_button = new Button[nbPropSansBatiment];
		menu_proprietesBat_revente = new MenuButton[joueurJ.getNbPropAvecBatiments()];

		panePlateau.getChildren().add(revente_pane);
		revente_pane.setVisible(true);
		
		affichageBoutons_revente_propSansBat(curseur, montant, carteTiree, joueurJ, margeEspace);
		affichageMenus_revente_propAvecBat(curseur, montant, carteTiree, joueurJ, margeEspace+30*nbPropSansBatiment);
		affichageBouton_revente_carteLibPrison(curseur, montant, carteTiree, joueurJ, margeEspace+30*joueurJ.getProprietes().length);

		if (joueurJ.isRobot()) {
			if (nbPropSansBatiment!=0) {
				nom_proprietes_button[0].fire();
			}
			else if (venteMaison!=null) {
				venteMaison.fire();
			}
			else if (venteHotel!=null) {
				venteHotel.fire();
			}
			else if (venteCartePrison!=null) {
				venteCartePrison.fire();
			}
		}
	}

	public void affichageBoutons_revente_propSansBat(int curseur, int montant, Cartes carteTiree, Joueur joueurJ, double margeEspace) {
		int i = 0;
		for (Proprietes p : joueurJ.getProprietesSansBat()) {
			nom_proprietes_button[i] = new Button(p.getPosition() + " " + p.getNom() + " - Prix de vente : " + p.getPrix());
			nom_proprietes_button[i].setLayoutX(75);
			nom_proprietes_button[i].setLayoutY(margeEspace);
			margeEspace+=30;
			revente_pane.getChildren().add(nom_proprietes_button[i]);

			nom_proprietes_button[i].setOnAction(actionEvent->{
				int ancienne_position = joueurJ.vendreLaPropriete_IG(p);
				if(jeu.isReseau() && jeu.getJoueurReseau() == jeu.getJoueurs()[jeu.getCurseur()]) {
					controleur.sendMsg("vendre", joueurJ.getNom() + "-" + montant + "-" + ancienne_position);
				}
				changement_couleur_case_blanche(ancienne_position);
				if (joueurJ.getArgent() < montant && (joueurJ.getProprietes().length>=1 || joueurJ.aCarteLibPrison())) {
					changement_argent(curseur);
					revente_pane.setVisible(false);
					affichage_revente_proprietes(curseur, montant, carteTiree);
				}
				else {
					panePlateau.getChildren().remove(revente_pane);
					controleur.transactionSelonType(curseur, montant, carteTiree);
					activer_bouton_achat(curseur);
				}
			});
			i++;
		}
	}

	public void affichageMenus_revente_propAvecBat(int curseur, int montant, Cartes carteTiree, Joueur joueurJ, double margeEspace) {
		int i = 0;
		for (Proprietes p : joueurJ.getProprietesAvecBat()) {
			menu_proprietesBat_revente[i] = new MenuButton(p.getPosition() + " " + p.getNom() + " - " + (p.aUnHotel()?"1":p.getNbMaisons())
					+ " batiment(s) a vendre : ");
			menu_proprietesBat_revente[i].setLayoutX(75);
			menu_proprietesBat_revente[i].setLayoutY(margeEspace);
			margeEspace+=30;

			if (!p.aUnHotel()) { //Si la propriete n'a que des maisons
				for (int j = 1; j <= p.getNbMaisons(); j++) {
					int prixReventeBat = p.getPrixBatiment()/2*j;
					venteMaison = new MenuItem("Vendre " + j + " maison(s) pour " + prixReventeBat + "e");
					menu_proprietesBat_revente[i].getItems().addAll(venteMaison);

					int jF = j;
					venteMaison.setOnAction(actionEvent -> {
						controleur.controleur_venteBatiment(p, "maison", jF); //i reventes de maisons

						if(jeu.isReseau() && jeu.getJoueurReseau() == jeu.getJoueurs()[jeu.getCurseur()]) {
							controleur.sendMsg("vendre", "maison-" + jF + "-" + prixReventeBat + "-" + p.getPosition() +"-"+joueurJ.getNom()+"-"+montant);
						}

						if (joueurJ.getArgent() < montant && joueurJ.getProprietes().length>=1) {
							revente_pane.setVisible(false);
							affichage_revente_proprietes(curseur, montant, carteTiree);
						}
						else {
							Platform.runLater(() -> {
								panePlateau.getChildren().remove(revente_pane);
							});
							controleur.transactionSelonType(curseur, montant, carteTiree);
							activer_bouton_achat(curseur);
						}
					});
				}
			}
			else { //Si la propriete a un hotel (donc 0 maison)
				int prixReventeBat = p.getPrixBatiment()*5/2;
				venteHotel = new MenuItem("Vendre l'hotel pour " + prixReventeBat + "e");
				menu_proprietesBat_revente[i].getItems().addAll(venteHotel);

				venteHotel.setOnAction(actionEvent -> {
					controleur.controleur_venteBatiment(p, "hotel", 1); //une seule revente d'hotel

					if(jeu.isReseau() && jeu.getJoueurReseau() == jeu.getJoueurs()[jeu.getCurseur()]) {
						controleur.sendMsg("vendre", "hotel-"+ 1 + "-" + prixReventeBat + "-" + p.getPosition() +"-"+joueurJ.getNom()+"-"+montant);;
					}

					if (joueurJ.getArgent() < montant && joueurJ.getProprietes().length>=1) {
						revente_pane.setVisible(false);
						affichage_revente_proprietes(curseur, montant, carteTiree);
					}
					else {
						Platform.runLater(() -> {
							panePlateau.getChildren().remove(revente_pane);
						});
						controleur.transactionSelonType(curseur, montant, carteTiree);
						activer_bouton_achat(curseur);
					}
				});
			}
			revente_pane.getChildren().add(menu_proprietesBat_revente[i]);
			i++;
		}
	}

	public void affichageBouton_revente_carteLibPrison(int curseur, int montant, Cartes carteTiree, Joueur joueurJ, double margeEspace) {
		if (jeu.getJoueurs()[curseur].aCarteLibPrison()) {
			venteCartePrison = new Button ("Carte Libere de prison - Prix de vente : 500e");
			venteCartePrison.setLayoutY(margeEspace);
			venteCartePrison.setLayoutX(75);
			revente_pane.getChildren().add(venteCartePrison);
			venteCartePrison.setOnAction(actionEvent->{
				controleur.controleur_venteCartePrison(curseur);
				if (joueurJ.getArgent() < montant && joueurJ.getProprietes().length>=1) {
					revente_pane.setVisible(false);
					affichage_revente_proprietes(curseur, montant, carteTiree);
				}
				else {
					panePlateau.getChildren().remove(revente_pane);
					controleur.transactionSelonType(curseur, montant, carteTiree);
					activer_bouton_achat(curseur);
				}
			});
		}
	}


	void afficherRegles() {
		String texte="";
		try {
			Scanner sc = new Scanner(new File("src/main/resources/regles.txt"));
			while(sc.hasNext()) {
				String prochaineLigne = sc.nextLine();
				texte=texte+prochaineLigne+"\n";
			}
		} catch (FileNotFoundException e) {
			System.out.println("Fichier introuvable");
		}

		BorderPane regles_pane = new BorderPane();
		Text regles_texte = new Text(texte);
		Button fermer = new Button("Fermer");
		ScrollPane scrollPane = new ScrollPane();

		scrollPane.setContent(regles_texte);
		scrollPane.setPrefSize(panePlateau.getWidth()*72/100, 600);
		regles_pane.setPrefSize(panePlateau.getWidth()*72/100, 600);
		regles_pane.setLayoutX(panePlateau.getWidth()*10/100);
		regles_pane.setLayoutY(panePlateau.getHeight()*10/100);
		regles_pane.setStyle("-fx-background-color: white");
		regles_pane.setTop(scrollPane);
		regles_pane.setBottom(fermer);
		BorderPane.setAlignment(scrollPane,Pos.TOP_CENTER);
		BorderPane.setAlignment(fermer,Pos.BOTTOM_RIGHT);
	    fermer.setOnAction(actionEvent -> {
			panePlateau.getChildren().remove(regles_pane);
		});
		panePlateau.getChildren().add(regles_pane);
	}


	//Interface graphique : Boutons
	void bouton_lancer_de_des() {
		lancer = new Button("Lancer");
		lancer.setLayoutX((panePlateau_x*42)/100);
		lancer.setLayoutY((panePlateau_y*20)/100);
		lancer.setPrefSize(100, 100);
		panePlateau.getChildren().add(lancer);

		if(jeu.isReseau() && !(jeu.getJoueurReseau() == jeu.getJoueurs()[jeu.getCurseur()])) { lancer.setDisable(true); }
		else { lancer.setDisable(false); }

		lancer.setOnAction(actionEvent -> {
			lancer.setDisable(true);
			int curseur = jeu.getCurseur();
			int argent = jeu.getJoueurs()[curseur].getArgent();
			if(jeu.isReseau()){
				controleur.sendMsg("lancer des", "");
			}else{
				int[] des = jeu.lancer_de_des();
				controleur.controleur_lancer(des, curseur);
				bouton_defis(curseur, argent);
				bouton_achat(curseur, argent);
			}

		});
	}

	void initialisation_labelDes() {
		desLabel = new Label("0 - 0");
		
		desLabel.setFont(new Font("Arial", 40));
		desLabel.setLayoutX((panePlateau_x*43)/100);
		desLabel.setLayoutY((panePlateau_y*40)/100);
		
		panePlateau.getChildren().add(desLabel);
	}
	
	void changement_labelDes(int des[]) {
		panePlateau.getChildren().remove(desLabel);
		
		desLabel = new Label(des[0] + " - " + des[1]);
		desLabel.setFont(new Font("Arial", 40));
		desLabel.setLayoutX((panePlateau_x*43)/100);
		desLabel.setLayoutY((panePlateau_y*40)/100);
		
		panePlateau.getChildren().add(desLabel);
	}
	
	void bouton_fin_de_tour() {
		fin = new Button("Fin");
		fin.setLayoutX((panePlateau_x*42)/100);
		fin.setLayoutY((panePlateau_y*55)/100);
		fin.setPrefSize(100, 20);
		panePlateau.getChildren().add(fin);

		if(jeu.isReseau() && !(jeu.getJoueurReseau() == jeu.getJoueurs()[jeu.getCurseur()])) {
			fin.setDisable(true);
		}else{
			fin.setDisable(false);
		}
		if(jeu.onlyRobot()) fin.setVisible(false);
		fin.setOnAction(actionEvent -> {
			int curseur = jeu.getCurseur();	
			int position = jeu.getJoueurs()[curseur].getPion().getPosition();

			achat_tab[curseur].setDisable(true);
			vente_tab[proprietaires[position]].setDisable(true);
			lancer.setDisable(false);

			defis_tab[curseur].setDisable(true);
			defis_tab[proprietaires[position]].setDisable(true);
			achatBatiments_menu_tab[curseur].setDisable(true);

			controleur.controleur_faillite(curseur);

			int curseurSuivant = controleur.controleur_curseurSuivant(curseur);

			actions_bouton_fin_tour(curseur, curseurSuivant);
	        achatBatiments_bouton_fin_tour(curseurSuivant);

	        if(jeu.getJoueurs()[jeu.getCurseur()].isRobot()){
	        	lancer.setVisible(false);
	        	fin.setVisible(false);
	        }
	        else {
	        	lancer.setVisible(true);
	        	fin.setVisible(true);
	        }
		});
	}

	public void actions_bouton_fin_tour(int curseur, int curseurSuivant) {
		if(!jeu.onlyRobot() || (jeu.getJoueurs()[curseur].getFaillite() && !jeu.getJoueurs()[curseur].isRobot() && jeu.getJoueurs()[curseurSuivant].isRobot())) {
			if(!jeu.isReseau() || jeu.getJoueurs()[curseur]==jeu.getJoueurReseau()){
				controleur.controleur_fin();
			}

			if (jeu.getJoueurs()[curseurSuivant].isEnPrison() && jeu.getJoueurs()[curseurSuivant].aCarteLibPrison()){
				bouton_prison(curseurSuivant);
				prison_tab[curseur].setDisable(true);
			}else {
				prison_tab[curseur].setDisable(true);
			}
        }
		if (jeu.onlyRobot()) {
        	Joueur joueurSuivant = jeu.getJoueurs()[curseurSuivant];
        	if (joueurSuivant.isEnPrison() && joueurSuivant.aCarteLibPrison()){
				bouton_prison(curseurSuivant);
				prison_tab[curseur].setDisable(true);
        	}
        }
	}

	public void achatBatiments_bouton_fin_tour(int curseurSuivant) {
		Joueur joueurSuivant = jeu.getJoueurs()[curseurSuivant];
		if(jeu.isReseau() && joueurSuivant!=jeu.getJoueurReseau()) return;
		achatBatiments_menu_tab[curseurSuivant].getItems().clear();
		maison_menuItem = null;
		hotel_menuItem = null;
		for (Proprietes p : joueurSuivant.getProprietes()) {
			if (p.getCouleur().equals("gare") || p.getCouleur().equals("compagnie")) { continue; } //On ne peut pas acheter de batiments sur les gares ou compagnies
			if (p.familleComplete() && p.estUniforme("maison") && joueurSuivant.getArgent()>=p.getPrixBatiment() && p.getNbMaisons() < 4 && !p.aUnHotel()) {
				int nbMaisonsPlus1 = p.getNbMaisons()+1;
				maison_menuItem = new MenuItem(p.getPosition() + " " + p.getNom() + " : acheter la maison " + nbMaisonsPlus1 + " pour " + p.getPrixBatiment() + "e");
				achatBatiments_menu_tab[curseurSuivant].getItems().addAll(maison_menuItem);
				achatBatiments_menu_tab[curseurSuivant].setDisable(false);

				maison_menuItem.setOnAction(actionEvent -> {
					controleur.controleur_achatBatiment(p, "maison");
					achatBatiments_bouton_fin_tour(curseurSuivant);
				});
			}
			else if (p.familleComplete() && p.estUniforme("hotel") && joueurSuivant.getArgent()>=p.getPrixBatiment() && p.getNbMaisons() == 4 && !p.aUnHotel()) {
				hotel_menuItem = new MenuItem(p.getPosition() + " " + p.getNom() + " : acheter un hotel pour " + p.getPrixBatiment() + "e");
				achatBatiments_menu_tab[curseurSuivant].getItems().addAll(hotel_menuItem);
				achatBatiments_menu_tab[curseurSuivant].setDisable(false);

				hotel_menuItem.setOnAction(actionEvent -> {
					controleur.controleur_achatBatiment(p, "hotel");
					achatBatiments_bouton_fin_tour(curseurSuivant);
				});
			}
		}
		if(joueurSuivant.isRobot()){
			PauseTransition wait = new PauseTransition(Duration.seconds(0.1));
			wait.setOnFinished((e) -> {
				if (maison_menuItem!=null) {
					maison_menuItem.fire();
				}
				else if (hotel_menuItem!=null) {
					hotel_menuItem.fire();
				}
			});
			wait.play();
		}
	}


	void boutons_jeu() {
		boutons_box = new HBox();
		regles_button = new Button("Regles");
		quitter_button = new Button("Quitter");
		historique_button = new Button("Historique");
		tchat_button = new Button("Tchat");
		boutons_box.getChildren().addAll(regles_button,historique_button,quitter_button, tchat_button);
		boutons_box.setLayoutX((panePlateau_x*37)/100);
		boutons_box.setLayoutY((panePlateau_y*70)/100);
		panePlateau.getChildren().add(boutons_box);
		regles_button.setOnAction(actionEvent -> {
			afficherRegles();
		});
		quitter_button.setOnAction(actionEvent -> {
		    stage.close();
		    stageHisto.close();
		});

		historique_button.setOnAction(actionEvent-> {
			if(stageHisto.isShowing()) {
				stageHisto.hide();
			}
			else {
				stageHisto.show();
			}
		});

		if(!jeu.isReseau()) {
			tchat_button.setDisable(true);
		}
		tchat_button.setOnAction(actionEvent-> {
			if(stageTchat.isShowing()) {
				stageTchat.hide();
			}
			else {
				stageTchat.show();
			}
		});
	}
	
	void initialisation_boutons() {
		boutonsJoueurs = new VBox();
		boutonsJoueurs.setLayoutX((tailleEcran.width*10)/100);
		paneJoueurs.getChildren().add(boutonsJoueurs);

		int nbr = jeu.getNbJ();
		for(int i = 0; i<nbr; i++) {
			Pane joueur_boutons = new Pane();
			joueur_boutons.setPrefSize((tailleEcran.width*10)/100, (int) ((tailleEcran.height-50)/nbr));

			achat = new Button("Achat");
			vente = new Button("Vente");
			defis = new Button("Defis");
			prison = new Button("Prison");
			achatBatiments_menu = new MenuButton("Achats de batiments");

			achat_tab[i] = achat;
			vente_tab[i] = vente;
			defis_tab[i] = defis;
			prison_tab[i] = prison;
			achatBatiments_menu_tab[i] = achatBatiments_menu;

			achat.setDisable(true);
			vente.setDisable(true);
			defis.setDisable(true);
			prison.setDisable(true);
			achatBatiments_menu.setDisable(true);
			if (jeu.getJoueurs()[i].isRobot()) {
				achatBatiments_menu_tab[i].setVisible(false);
			}

			achat.setLayoutY(5);
			vente.setLayoutY(35);

			defis.setLayoutY(5);
			defis.setLayoutX(50);
			prison.setLayoutX(50);
			prison.setLayoutY(35);
			achatBatiments_menu.setLayoutY(65);

			joueur_boutons.getChildren().addAll(achat, vente, defis, prison, achatBatiments_menu);

			boutonsJoueurs.getChildren().add(joueur_boutons);
		}
	}
	
	void caseChanceCommu(int curseur, Cartes carteTiree) {
		BorderPane carte_pane = new BorderPane();
		Label type_carte = (carteTiree.getType().equals("chance")) ? new Label("CHANCE") : new Label("COMMUNAUTE");
		Label contenu_carte = new Label(carteTiree.getContenu());
		Button bouton_fermer = new Button("Fermer");
		
		type_carte.setFont(new Font("Arial", 23));
		contenu_carte.setFont(new Font("Arial", 18));
		contenu_carte.setTextAlignment(TextAlignment.CENTER);
		
		carte_pane.setPrefSize((panePlateau_x*50)/100, (panePlateau_y*50)/100);
		carte_pane.setStyle("-fx-background-color: white; -fx-border-color: black");
		carte_pane.setLayoutX((panePlateau_x*21)/100);
		carte_pane.setLayoutY((panePlateau_y*15)/100);
		  
		carte_pane.setTop(type_carte);
		carte_pane.setCenter(contenu_carte);
		carte_pane.setBottom(bouton_fermer);
		type_carte.setPadding(new Insets(20,0,0,0));
		BorderPane.setAlignment(type_carte, Pos.TOP_CENTER);
		BorderPane.setAlignment(contenu_carte, Pos.CENTER);
		BorderPane.setAlignment(bouton_fermer, Pos.BOTTOM_CENTER);
		   
		panePlateau.getChildren().add(carte_pane);
		  
		bouton_fermer.setOnAction(actionEvent -> panePlateau.getChildren().remove(carte_pane));
		if(jeu.getJoueurs()[curseur].isRobot()){
			PauseTransition wait = new PauseTransition(Duration.seconds(1));
			wait.setOnFinished((e) -> {
				bouton_fermer.fire();
			});
			wait.play();
		}
	}
	
	void bouton_achat(int curseur, int argent) {
		int position = jeu.getJoueurs()[curseur].getPion().getPosition();
		int argent_joueur = jeu.getJoueurs()[curseur].getArgent();
		Cases case_curseur = jeu.getPlateau().getGrille()[position];
		
		boolean libre = false;
		boolean argent_suffisant = false;
		if(case_curseur.getType().equals("Propriete")) {
			Proprietes prop_curseur = (Proprietes) jeu.getPlateau().getGrille()[position];
			if(prop_curseur.est_Libre()) { 
				libre = true; 
			}
			if(argent_joueur >= prop_curseur.getPrix()) {
				argent_suffisant = true;
			}
		}
		
		int[] non_achetables = {0,2,4,7,10,17,20,22,30,33,36,38};
		boolean position_valide = true;
		for(int i = 0; i<non_achetables.length;i++) {
			if(position == non_achetables[i]) {
				position_valide = false;
			}
		}
		//sans proprietaire
		if(libre && argent_suffisant && position_valide) {
			achat_tab[curseur].setDisable(false);
			achat_tab[curseur].setOnAction(actionEvent ->{
				proprietaires[position]=curseur;
				controleur.controleur_achat(curseur);
				achat_tab[curseur].setDisable(true);
				changement_argent(curseur);
				gestion_historique(unJoueur_historique("achat", jeu.getJoueurs()[curseur], null, position));
			});
			if(jeu.getJoueurs()[curseur].isRobot()){
				achat_tab[curseur].fire();
				fin.fire();
			}
		}else {
			//avec proprio
			if(!libre && argent_suffisant && position_valide && proprietaires[position]!=curseur) {
				Proprietes prop_curseur = (Proprietes) jeu.getPlateau().getGrille()[position];
				if (argent>=prop_curseur.getLoyer()) {
					bouton_vente(curseur,position);
					if(jeu.getJoueurs()[curseur].isRobot()){
						achat_tab[curseur].fire();
					}	
				}
			}else if(jeu.getJoueurs()[curseur].isRobot()){
				fin.fire();
			}
		}
	}
	
	void bouton_vente(int curseur, int position) {
		achat_tab[curseur].setDisable(false);
		
		achat_tab[curseur].setOnAction(actionEvent ->{
			achat_tab[curseur].setDisable(true);
			gestion_historique(deuxJoueurs_historique("achat", jeu.getJoueurs()[curseur], jeu.getJoueurs()[proprietaires[position]], null, position));
			if(jeu.isReseau()) controleur.sendMsg("demande achat", "");
			else vente_tab[proprietaires[position]].setDisable(false);
			if(jeu.getJoueurs()[proprietaires[position]].isRobot()) {
				vente_tab[proprietaires[position]].fire();
			}
			if( !(jeu.getJoueurs()[proprietaires[position]].isRobot()) ){
		      	fin.setVisible(true);
			}
		});
		
		vente_tab[proprietaires[position]].setOnAction(actionEvent ->{
			vente_tab[proprietaires[position]].setDisable(true);
			defis_tab[curseur].setDisable(true);
			defis_tab[proprietaires[position]].setDisable(true);
			gestion_historique(deuxJoueurs_historique("vente", jeu.getJoueurs()[curseur], jeu.getJoueurs()[proprietaires[position]], null, position));
			controleur.controleur_vente(curseur);
			changement_argent(curseur);
			changement_argent(proprietaires[position]);

			proprietaires[position]=curseur;
			if(jeu.getJoueurs()[curseur].isRobot()){
				fin.fire();
			}
		});
	}

	public void active_vente(int position, int curseur){
		vente_tab[proprietaires[position]].setDisable(false);
		vente_tab[proprietaires[position]].setOnAction(actionEvent ->{
			vente_tab[proprietaires[position]].setDisable(true);
			defis_tab[curseur].setDisable(true);
			defis_tab[proprietaires[position]].setDisable(true);
			gestion_historique(deuxJoueurs_historique("vente", jeu.getJoueurs()[curseur], jeu.getJoueurs()[proprietaires[position]], null, position));
			controleur.controleur_vente(curseur);
			changement_argent(curseur);
			changement_argent(proprietaires[position]);

			proprietaires[position]=curseur;

			Proprietes pos_actuelle = (Proprietes) jeu.getPlateau().getCases(position);
			int prix = pos_actuelle.getPrix();

			controleur.sendMsg("vente a joueur", position + "-" + jeu.getJoueurs()[curseur].getNom() + "-" + prix);

			if(jeu.getJoueurs()[curseur].isRobot()){
				fin.fire();
			}
		});
	}

	public void activer_bouton_achat(int curseur) {        
        Joueur joueurJ = jeu.getJoueurs()[jeu.getCurseur()];
        int position = joueurJ.getPion().getPosition();
        Cases caseC = jeu.getPlateau().getCases(position);
            
        if (caseC instanceof Proprietes) {
            if (joueurJ.getArgent()>=((Proprietes) caseC).getPrix()) {
                bouton_vente(curseur,position);
                if (jeu.getJoueurs()[curseur].isRobot()){
                    achat_tab[curseur].fire();
                }
            }
        }
    }

	void bouton_defis(int curseur, int argent) {
        int position = jeu.getJoueurs()[curseur].getPion().getPosition();
        Cases case_curseur = jeu.getPlateau().getGrille()[position];

        if(case_curseur.getType().equals("Propriete")) {
            Proprietes prop_curseur = (Proprietes) jeu.getPlateau().getGrille()[position];
            if( !(prop_curseur.est_Libre()) && proprietaires[position]!=curseur && argent>=prop_curseur.getLoyer() ) {
                defis_tab[curseur].setDisable(false);
            }
        }
        defis_tab[curseur].setOnAction(actionEvent ->{
            defis_tab[curseur].setDisable(true);
            gestion_historique(deuxJoueurs_historique("lancerDefi", jeu.getJoueurs()[curseur], jeu.getJoueurs()[proprietaires[position]], null, position));
            if(jeu.isReseau()) controleur.sendMsg("demande defis", "");
            else defis_tab[proprietaires[position]].setDisable(false);
            if(jeu.getJoueurs()[proprietaires[position]].isRobot()) {
                defis_tab[proprietaires[position]].fire();
            }

        });
        defis_tab[proprietaires[position]].setOnAction(actionEvent ->{
            controleur.controleur_defis(curseur);
            defis_tab[proprietaires[position]].setDisable(true);
            Proprietes prop_curseur = (Proprietes) jeu.getPlateau().getGrille()[position];
            if(jeu.getJoueurs()[curseur].getArgent() < prop_curseur.getPrix()) {
                achat_tab[curseur].setDisable(true);
            }
        });
        if(jeu.getJoueurs()[curseur].isRobot()){
            defis_tab[curseur].fire();
        }
    }

	void bouton_prison(int curseur) {
		prison_tab[curseur].setDisable(false);
		prison_tab[curseur].setOnAction(actionEvent ->{
			prison_tab[curseur].setDisable(true);
			controleur.controleur_libererPrison(curseur);
			if(jeu.isReseau()) controleur.sendMsg("carte prison", "");
		});
		if(jeu.getJoueurs()[curseur].isRobot()){
			prison_tab[curseur].fire();
		}
	}


	//Interface graphique : Accueil
	void accueil_jeu() {
		accueil_pane = new AnchorPane();
		accueil_pane.setPrefSize(tailleEcran.width, tailleEcran.height);
		accueil_pane.setStyle("-fx-background-color: #BAEEB4");
		Label titre = new Label("Monopoly");
		titre.setFont(new Font("Arial", 50));
		titre.setLayoutX((tailleEcran.width*40)/100);
		titre.setLayoutY((tailleEcran.height*10)/100);
		accueil_pane.getChildren().add(titre);
		root.getChildren().add(accueil_pane);
		accueil_nbPseudo_reseau();
	}

	void accueil_nbPseudo_reseau() {
		GridPane grid = new GridPane ();
		grid.setVgap(10);
		grid.setPadding(new Insets((tailleEcran.height*40)/100, (tailleEcran.width*40)/100, (tailleEcran.height*40)/100, (tailleEcran.width*40)/100));

		Label consigne = new Label ("Nombre de joueur (entre 2 et 6)");
		GridPane.setConstraints(consigne, 0, 1);
		TextField tf = new TextField();
		tf.setPromptText("Nombre de joueur");
		GridPane.setConstraints(tf, 0, 2);
		Button suivant = new Button ("Suivant");
		GridPane.setConstraints(suivant, 0, 3);
		accueil_pane.getChildren().add(grid);

		Button reseau;
		if(jeu.isReseau()){
			reseau = new Button("Mode hors-ligne");
			reseau.setStyle("-fx-background-color: #FF0000");
		}else{
			reseau = new Button("Mode Reseau");
			reseau.setStyle("-fx-background-color: #0000FF");
		}
		reseau.setOnAction(actionEvent->{
			if(t==null){
				t = new Thread(controleur, "controleur");
				controleur.startSocket();
				t.start();
				TranslateTransition transition = new TranslateTransition(Duration.seconds(0.5), grid);
				transition.setToX(grid.getTranslateX() + 400);
				transition.play();
				transition.setOnFinished(evt -> {
					accueil_pseudo(false);
	                accueil_pane.getChildren().remove(grid);
				});
			}
		});
		GridPane.setConstraints(reseau, 0, 4);

		suivant.setOnAction(actionEvent->{
			if(tf.getText().length()!=1 || !Character.isDigit(tf.getText().charAt(0)) || Integer.parseInt(tf.getText())<2 || Integer.parseInt(tf.getText())>6){
				Label erreur = new Label("Choisissez un nombre entre 2 et 6");
				erreur.setFont(new Font("Arial", 10));
				erreur.setLayoutX((tailleEcran.width*40)/100);
				erreur.setLayoutY((tailleEcran.height*60)/100);
				GridPane.setConstraints(erreur, 0, 6);
				grid.getChildren().add(erreur);

			}else {
				TranslateTransition transition = new TranslateTransition(Duration.seconds(0.5), grid);
				transition.setToX(grid.getTranslateX() - 400);
				transition.play();
				transition.setOnFinished(evt -> {
	                	accueil_pane.getChildren().remove(grid);
	                    nbJoueurs=Integer.parseInt(tf.getText());
	                    accueil_pseudo(false);
	                });
			}
		});
		grid.getChildren().addAll(consigne, tf,suivant,reseau);
	}

	void accueil_pseudo(boolean b) {
		GridPane grid = new GridPane();
		grid.setVgap(10);
		grid.setPadding(new Insets((tailleEcran.height*40)/100, (tailleEcran.width*40)/100, (tailleEcran.height*40)/100, (tailleEcran.width*40)/100));

		TextField[] tf;
		Button[] bt;
		boolean[] flags;

		Label consigne = new Label("Entrez votre pseudo");
		consigne.setFont(new Font("Arial", 10));
		consigne.setLayoutX((tailleEcran.width*40)/100);
		consigne.setLayoutY((tailleEcran.height*35)/100);
		consigne.setFont(new Font("Arial", 20));

		if(!jeu.isReseau()){
			tf = new TextField[nbJoueurs];
			bt = new Button[nbJoueurs];
			flags = new boolean[nbJoueurs];
		}else{
			tf = new TextField[1];
			bt = new Button[1];
			flags = new boolean[1];
			nbJoueurs=1;
			Button reseau;
			reseau = new Button("Mode hors-ligne");
			reseau.setStyle("-fx-background-color: #FF0000");
			GridPane.setConstraints(reseau, 1, 2);
			grid.getChildren().add(reseau);
			reseau.setOnAction(actionEvent->{
				accueil_pane.getChildren().remove(consigne);
				if(t!=null) {
					t.interrupt();
					controleur.startSocket();
					t = null;
					TranslateTransition transition = new TranslateTransition(Duration.seconds(0.5), grid);
					transition.setToX(grid.getTranslateX() - 400);
					transition.play();
					transition.setOnFinished(evt -> {
						accueil_nbPseudo_reseau();
		                accueil_pane.getChildren().remove(grid);
					});
				}
			});
		}

		for(int i=0;i<nbJoueurs;i++){
			tf[i] = new TextField ();
			tf[i].setPromptText("Pseudo du joueur " + (i+1));
			GridPane.setConstraints(tf[i], 0, i);

			bt[i] = new Button("Mode robot pour joueur " + (i+1));
			GridPane.setConstraints(bt[i], 1, i);
			bt[i].setLayoutX(400);
			int finalI = i;
			bt[i].setOnAction(actionEvent->{
				Color color = (Color)bt[finalI].getBackground().getFills().get(0).getFill();
				if (color != Color.LIGHTSKYBLUE) { bt[finalI].setStyle("-fx-background-color: LIGHTSKYBLUE"); }
				else { bt[finalI].setStyle(null); }
				flags[finalI] = !flags[finalI];
			});
		}
		grid.getChildren().addAll(tf);
		if(!jeu.isReseau()) grid.getChildren().addAll(bt);
		accueil_pane.getChildren().add(consigne);
		accueil_pane.getChildren().add(grid);

		if(b){
			Label erreur = new Label("Pseudo invalide ou deja pris.");
			erreur.setFont(new Font("Arial", 10));
			erreur.setLayoutX((tailleEcran.width*40)/100);
			erreur.setLayoutY((tailleEcran.height*60)/100);
			GridPane.setConstraints(erreur, 0, 5);
			grid.getChildren().add(erreur);
		}

		Button valider = new Button("Valider");
		grid.add(valider,0,nbJoueurs+1);
		valider.setOnAction(actionEvent->{
			String[] noms = fieldToString(tf);
			if(jeu.isReseau()){
				if (!tf[0].equals("")) {
					jeu.setJoueurReseau(new Joueur(tf[0].getText()));
					controleur.sendMsg("start", jeu.getJoueurReseau().getNom());
				}
			}else if(noms.length<nbJoueurs) {
				Label erreur = new Label("Veuillez remplir tous les champs");
				erreur.setFont(new Font("Arial", 10));
				erreur.setLayoutX((tailleEcran.width*40)/100);
				erreur.setLayoutY((tailleEcran.height*60)/100);
				grid.add(erreur,0,nbJoueurs+2);
			} else if (meme_pseudo(noms)) {
				Label erreur = new Label("Choisissez des pseudos differents");
				erreur.setFont(new Font("Arial", 10));
				erreur.setLayoutX((tailleEcran.width*40)/100);
				erreur.setLayoutY((tailleEcran.height*60)/100);
				grid.add(erreur,0,nbJoueurs+3);
			}else {
				root.getChildren().remove(accueil_pane);
				jeu.initialisation_joueurs(noms, flags);

				initialisation_plateau();

				affichage_joueurs();

				pionLabel_positionnement();
				affichage_pions_initial();

				bouton_lancer_de_des();
				bouton_fin_de_tour();
				boutons_jeu();
				initialisation_boutons();

				creation_fenetreHistorique();

				if(jeu.getJoueurs()[jeu.getCurseur()].isRobot()){
					lancerRobot();
					if (jeu.onlyRobot()) controleur.controleur_fin();
				}
			}
		});

		if(!jeu.isReseau()) {
			Button retour = new Button("Retour");
			grid.add(retour,1,nbJoueurs+1);
			retour.setOnAction(actionEvent->{
				accueil_pane.getChildren().remove(consigne);
				TranslateTransition transition = new TranslateTransition(Duration.seconds(0.5), grid);
				transition.setToX(grid.getTranslateX() + 400);
				transition.play();
				transition.setOnFinished(evt -> {
	                accueil_pane.getChildren().remove(grid);
	                accueil_nbPseudo_reseau();
				});
			});
		}
	}

	boolean meme_pseudo(String [] noms) {
		for (int i=0; i<nbJoueurs-1; i++) {
			for (int j=i+1; j<nbJoueurs; j++) {
				if (noms[i].equals(noms[j])){
					return true;
				}
			}
		}
		return false;
	}

	private String[] fieldToString(TextField[] tf){
		int cpt = 0;
		for(TextField t: tf){
			if(!t.getText().isEmpty()) cpt++;
		}
		String[] rep = new String[cpt];
		int i=0;
		for(TextField t: tf){
			if(!t.getText().isEmpty()){
				rep[i] = t.getText();
				i++;
			}
		}
		return rep;
	}
	
	//Interface graphique: fin de la partie
	void fin_partie() {
		lancer.setDisable(true);
		fin.setDisable(true);
		achat.setDisable(true);
		vente.setDisable(true);
		defis.setDisable(true);
		prison.setDisable(true);

		BorderPane victoire_pane = new BorderPane ();
		String nom = "";
		for (int i=0; i<jeu.getJoueurs().length; i++) {
			if (!jeu.getJoueurs()[i].getFaillite()) {
				nom = jeu.getJoueurs()[i].getNom();
				achatBatiments_menu_tab[i].setDisable(true);
			}
		}
		Label l1 = new Label ("VICTOIRE DU JOUEUR");
		Label l2 = new Label (nom);
		victoire_pane.setPrefSize((panePlateau_x*50)/100, (panePlateau_y*50)/100);
		victoire_pane.setStyle("-fx-background-color: white; -fx-border-color: black");
		victoire_pane.setLayoutX((panePlateau_x*21)/100);
		victoire_pane.setLayoutY((panePlateau_y*15)/100);
		l1.setFont(new Font(police*22));
		l2.setFont(new Font(police*22));
		victoire_pane.setTop(l1);
		victoire_pane.setCenter(l2);
		l1.setPadding(new Insets(panePlateau_y*0.15,0,0,0));
		l2.setPadding(new Insets(-panePlateau_y*0.1,0,0,0));
		BorderPane.setAlignment(l1, Pos.TOP_CENTER);
		BorderPane.setAlignment(l2, Pos.CENTER);
		panePlateau.getChildren().add(victoire_pane);
	}

	//lance tour robot
	void lancerRobot(){
		lancer.fire();
	}


	//Interface graphique: Historique des actions/Tchat
	void creation_fenetreHistorique() { //Mode local/Reseau
		stageHisto = new Stage();
		stageHisto.setTitle("Historique");
		stageHisto.setResizable(false);
		stageHisto.show();

		rootHisto = new AnchorPane();
		rootHisto.setStyle("-fx-background-color: beige");

		sceneHisto = new Scene(rootHisto, 500, (tailleEcran.height*50)/100);
		stageHisto.setScene(sceneHisto);

		historiqueVBox = new VBox();
		historique_tab = new Label[20];
		rootHisto.getChildren().add(historiqueVBox);

		remplissage_temporaireHistorique();
		actualiser_historique();
	}

	void remplissage_temporaireHistorique() {
		for(int i = 0; i<19; i++) {
			historique_tab[i] = new Label("");
		}
		historique_tab[19] = new Label("Bienvenue dans Monopoly !");
	}

	void actualiser_historique() {
		historiqueVBox.getChildren().clear();
		for(int i = 0; i<20; i++) {
			historiqueVBox.getChildren().add(historique_tab[i]);
		}
	}

	void ajouter_historique(Label nouveau) {
		for(int i = 0; i<19;i++) {
			historique_tab[i] = historique_tab[i+1];
		}
		historique_tab[19] = nouveau;
	}

	//Creation d'un message d'evenement (action concernant un seul joueur)
	Label unJoueur_historique(String type, Joueur joueur, int des[], int variable) {
		Label nouveau = new Label();
		switch(type) {
			case "lancer":
				nouveau = new Label(
					joueur.getNom() + " a fait " + Integer.toString(des[0]) +
					" et " + Integer.toString(des[1]) + " avec les des. Arrivee en case " +
					Integer.toString(variable) + ".");
				break;

			case "achat":
				nouveau = new Label(
					joueur.getNom() + " a achete la propriete a la position " + variable + ".");
				break;

			case "enPrison":
				if(des[0] == des[1]) {
					nouveau = new Label(
						joueur.getNom() + " a fait " + Integer.toString(des[0]) +
						" et " + Integer.toString(des[1]) + " avec les des. Liberation de prison.");
				}
				else {
					nouveau = new Label(
						joueur.getNom() + " a fait " + Integer.toString(des[0]) +
						" et " + Integer.toString(des[1]) + " avec les des.");
				}
				break;

			case "tirerUneCarte":
				nouveau = new Label(
					joueur.getNom() + " a tire une carte.");
				break;

			case "carteLiberation":
				nouveau = new Label(
					joueur.getNom() + " a utilise une carte de liberation.");
				break;

			case "achatMaison":
				nouveau = new Label(
					joueur.getNom() + " a achete une maison a la position " + variable + ".");
				break;

			case "achatHotel":
				nouveau = new Label(
					joueur.getNom() + " a achete un hotel a la position " + variable + ".");
				break;

			case "faillite":
				nouveau = new Label(
					joueur.getNom() + " a fait faillite.");
				break;

			//Case fin de jeu: Controleur
		}
		return nouveau;
	}

	//Creation d'un message d'evenement (action concernant deux joueurs)
	Label deuxJoueurs_historique(String type, Joueur joueur, Joueur proprietaire, int des[], int variable) {
		Label nouveau = new Label();
		switch(type) {
			case "loyer":
				nouveau = new Label(
					joueur.getNom() + " a paye " + variable + " euros de loyer a " + proprietaire.getNom() + ".");
				break;

			case "achat":
				nouveau = new Label(
					joueur.getNom() + " a fait une proposition d'achat a " + proprietaire.getNom() +
					" pour sa propriete position " + variable + ".");
				break;

			case "vente":
				nouveau = new Label(
					proprietaire.getNom() + " a accepte la proposition d'achat de " + joueur.getNom() + ".");
				break;

			case "lancerDefi":
				nouveau = new Label(
					joueur.getNom() + " a lance un defi a " + proprietaire.getNom() + ".");
				break;

			case "accepterDefi":
				if(des[0] > des[1]) {
					nouveau = new Label(
						proprietaire.getNom() + " a accepte le defi. " + joueur.getNom() +
						" a gagne avec " + des[0] + " contre " + des[1] + "." );
				}
				else if(des[1] > des[0]) {
					nouveau = new Label(
							proprietaire.getNom() + " a accepte le defi. " + proprietaire.getNom() +
							" a gagne avec " + des[1] + " contre " + des[0] + "." );
				}
				else {
					nouveau = new Label(
						proprietaire.getNom() + " a accepte le defi. Il y a egalite avec " + des[0] + " partout.");
				}
				break;
		}
		return nouveau;
	}

	void gestion_historique(Label nouveau) {
		ajouter_historique(nouveau);
		actualiser_historique();
	}

	void creation_fenetreTchat() { //Mode reseau
		stageTchat = new Stage();
		stageTchat.setTitle("Tchat");
		stageTchat.setResizable(false);
		stageTchat.show();

		rootTchat = new AnchorPane();
		rootTchat.setStyle("-fx-background-color: pink");

		sceneTchat = new Scene(rootTchat, 500, (tailleEcran.height*55)/100);
		stageTchat.setScene(sceneTchat);

		tchatVBox = new VBox();
		tchat_tab = new Label[20];
		rootTchat.getChildren().add(tchatVBox);

		TextField zone = new TextField();
		zone.setPrefSize(425, (rootTchat.getHeight())/100);
		AnchorPane.setBottomAnchor(zone, (double) (rootTchat.getHeight())/100);
		rootTchat.getChildren().add(zone);

		Button validerMsg = new Button("Envoyer");
		AnchorPane.setBottomAnchor(validerMsg, (double) (rootTchat.getHeight())/100);
		AnchorPane.setRightAnchor(validerMsg, (double) (rootTchat.getWidth())/100);
		rootTchat.getChildren().add(validerMsg);

		remplissageTemporaire_Tchat();
		actualiser_tchat();
		validerMsg.setOnAction(actionEvent -> {
			if(zone.getText() != null) {
				String msg = zone.getText();
				controleur.sendMsg("message", msg);
				zone.clear();
			}
		});
	}

	void remplissageTemporaire_Tchat() {
		for(int i = 0; i<19; i++) {
			tchat_tab[i] = new Label("");
			//tchat_tab[i] = new Label(Integer.toString(i));
		}
		tchat_tab[19] = new Label("Bienvenue dans Monopoly !");
	}

	void actualiser_tchat() {
		tchatVBox.getChildren().clear();
		for(int i = 0; i<20; i++) {
			tchatVBox.getChildren().add(tchat_tab[i]);
		}
	}

	void ajouter_tchat(Label nouveau) {
		for(int i = 0; i<19;i++) {
			tchat_tab[i] = tchat_tab[i+1];
		}
		tchat_tab[19] = nouveau;
	}

	void gestion_tchat(Label nouveau) {
		ajouter_tchat(nouveau);
		actualiser_tchat();
	}

	//Reseau
	//Actualise l'IG a la fin du tour en mode reseau
	void finDeTourReseau(){
		int curseur = jeu.getCurseur();
		int position = jeu.getJoueurs()[curseur].getPion().getPosition();
		achat_tab[curseur].setDisable(true);
		vente_tab[proprietaires[position]].setDisable(true);
		lancer.setDisable(false);

		defis_tab[curseur].setDisable(true);
		defis_tab[proprietaires[position]].setDisable(true);
		achatBatiments_menu_tab[curseur].setDisable(true);

		controleur.controleur_faillite(curseur);

		int curseurSuivant = controleur.controleur_curseurSuivant(curseur);

		actions_bouton_fin_tour(curseur, curseurSuivant);
		achatBatiments_bouton_fin_tour(curseurSuivant);
	}

	void achatReseau(){
		int curseur = jeu.getCurseur();
		int position = jeu.getJoueurs()[curseur].getPion().getPosition();

		proprietaires[position]=curseur;
		controleur.controleur_achat(curseur);
		changement_argent(curseur);
		gestion_historique(unJoueur_historique("achat", jeu.getJoueurs()[curseur], null, position));
	}

	//Vend la propriete en reseau
	void vendPropReseau(int ancienne_position, Joueur joueurJ, int montant){
		if(joueurJ==null) return;
		int curseur = jeu.getCurseurDuJoueur(joueurJ);
		if(curseur==-1) return;

		//int position = joueurJ.getPion().getPosition();
		//Proprietes propriete_actuelle = (Proprietes) jeu.getPlateau().getCases(position);

		changement_couleur_case_blanche(ancienne_position);
		changement_argent(curseur);
		/*if(jeu.getJoueurs()[curseur].getArgent()>=propriete_actuelle.getLoyer()){
			if (!propriete_actuelle.estCompagnie()) { controleur.controleur_loyerIG(propriete_actuelle, propriete_actuelle.getLoyer()); }
			else { controleur.controleur_loyerIG(propriete_actuelle, propriete_actuelle.getLoyer()*jeu.getSommeDes()); }
			changement_argent(proprietaires[position]);
			changement_argent(curseur);
		}*/

		if (joueurJ.getArgent() < montant && (joueurJ.getProprietes().length>=1 || joueurJ.aCarteLibPrison())) {
			changement_argent(curseur);
		}
		else {
			controleur.transactionSelonType(curseur, montant, controleur.getCarte());
			activer_bouton_achat(curseur);
		}
	}

	void vendBatReseau( Joueur joueurJ, int montant){
		if(joueurJ==null) return;
		int curseur = jeu.getCurseurDuJoueur(joueurJ);
		if(curseur==-1) return;

		//int position = joueurJ.getPion().getPosition();

		/*if(jeu.getJoueurs()[curseur].getArgent()>=propriete_actuelle.getLoyer()){
			if (!propriete_actuelle.estCompagnie()) { controleur.controleur_loyerIG(propriete_actuelle, propriete_actuelle.getLoyer()); }
			else { controleur.controleur_loyerIG(propriete_actuelle, propriete_actuelle.getLoyer()*jeu.getSommeDes()); }
			changement_argent(proprietaires[position]);
			changement_argent(curseur);
		}*/

		if (joueurJ.getArgent() < montant && (joueurJ.getProprietes().length>=1 || joueurJ.aCarteLibPrison())) {
			changement_argent(curseur);
		}
		else {
			controleur.transactionSelonType(curseur, montant, controleur.getCarte());
			activer_bouton_achat(curseur);
		}

	}

	//Vend la propriete a un autre joueur
	public void updateVenteReseau(int position, int curseur){
		defis_tab[curseur].setDisable(true);
		controleur.controleur_vente(curseur);
		changement_argent(curseur);
		changement_argent(proprietaires[position]);
		proprietaires[position]=curseur;
		gestion_historique(deuxJoueurs_historique("vente", jeu.getJoueurs()[curseur], jeu.getJoueurs()[proprietaires[position]], null, position));
	}

	public void boutonDefisReseau(int position, int curseur) {
		defis_tab[proprietaires[position]].setDisable(false);
		defis_tab[proprietaires[position]].setOnAction(actionEvent ->{
			controleur.controleur_defis(curseur);
			defis_tab[proprietaires[position]].setDisable(true);
		});
	}

}
