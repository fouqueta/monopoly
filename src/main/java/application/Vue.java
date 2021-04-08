package application;

import java.awt.Dimension;
import java.awt.Toolkit;

import javafx.animation.PauseTransition;
import javafx.scene.control.Button;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;
import monopoly.*;

public class Vue {
	
	//Interface graphique
	private AnchorPane root;
	private Stage stage;
	private Dimension tailleEcran = Toolkit.getDefaultToolkit().getScreenSize();
	
	//Accueil
	private AnchorPane scene_accueil;
		
	//Scene de jeu
	private Scene scene_jeu;
	private Jeu jeu;

		//Plateau
	private AnchorPane panePlateau; //jeu_pane
	private Pane grillePlateau; //plateau_pane
	private Pane revente_pane;
	
	private Pane[] casesPlateau = new Pane[40]; //tabCase_pane
	private VBox colonne_gauche;
	private VBox colonne_droite;
	private HBox ligne_bas;
	private HBox ligne_haut;
	
	private Label desLabel = new Label();
	
		//Proprietaires
	private int[] proprietaires = new int [40];
	
		//Joueurs
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
	private HBox boutons_box;
	private Button regles_button;
	private Button aide_button;
	private Button quitter_button;
	
	private Button achat_tab[] = new Button[6];
	private Button vente_tab[] = new Button[6];
	private Button nom_proprietes_button[];
	
	private Button defis;
	private Button defis_tab[] = new Button[6];
	
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
		
		grillePlateau = new Pane();
		grillePlateau.setPrefSize(550, 550);
		AnchorPane.setLeftAnchor(grillePlateau, (double) 100);
		AnchorPane.setTopAnchor(grillePlateau, (double) 50);
		panePlateau.getChildren().add(grillePlateau);
		
		colonne_gauche = new VBox();
		grillePlateau.getChildren().add(colonne_gauche);
		for(int i = 10; i>=0; i--) { 
			casesPlateau[i] = new Pane();
			casesPlateau[i].setPrefSize(50, 50);
			casesPlateau[i].setStyle("-fx-background-color: white; -fx-border-color: black");
			
			Label numero = new Label(String.valueOf(i));
			casesPlateau[i].getChildren().add(numero);
			numero.setStyle("-fx-font-weight: bold");
			
			if(jeu.getPlateau().getGrille()[i].getType().equals("Propriete")) { 
				Proprietes prop = (Proprietes) jeu.getPlateau().getCases(i);
				int prix_prop = prop.getPrix();
				Label prix = new Label(String.valueOf(prix_prop));
				prix.setLayoutX(15);
				casesPlateau[i].getChildren().add(prix);
			}			
			colonne_gauche.getChildren().add(casesPlateau[i]);
		}
		
		colonne_droite = new VBox();
		colonne_droite.setLayoutX(500);
		grillePlateau.getChildren().add(colonne_droite);
		for(int i = 20; i<31; i++) { 
			casesPlateau[i] = new Pane();
			casesPlateau[i].setPrefSize(50, 50);
			casesPlateau[i].setStyle("-fx-background-color: white; -fx-border-color: black");
			
			Label numero = new Label(String.valueOf(i));
			casesPlateau[i].getChildren().add(numero);
			numero.setStyle("-fx-font-weight: bold");
			
			if(jeu.getPlateau().getGrille()[i].getType().equals("Propriete")) { 
				Proprietes prop = (Proprietes) jeu.getPlateau().getCases(i);
				int prix_prop = prop.getPrix();
				Label prix = new Label(String.valueOf(prix_prop));
				prix.setLayoutX(15);
				casesPlateau[i].getChildren().add(prix);
			}
			
			colonne_droite.getChildren().add(casesPlateau[i]);
		}
		
		ligne_haut = new HBox();
		ligne_haut.setLayoutX(50);
		grillePlateau.getChildren().add(ligne_haut);
		for(int i = 11; i<20; i++) { 
			casesPlateau[i] = new Pane();
			casesPlateau[i].setPrefSize(50, 50);
			casesPlateau[i].setStyle("-fx-background-color: white; -fx-border-color: black");
			
			Label numero = new Label(String.valueOf(i));
			casesPlateau[i].getChildren().add(numero);
			numero.setStyle("-fx-font-weight: bold");
			
			if(jeu.getPlateau().getGrille()[i].getType().equals("Propriete")) { 
				Proprietes prop = (Proprietes) jeu.getPlateau().getCases(i);
				int prix_prop = prop.getPrix();
				Label prix = new Label(String.valueOf(prix_prop));
				prix.setLayoutX(15);
				casesPlateau[i].getChildren().add(prix);
			}
			
			ligne_haut.getChildren().add(casesPlateau[i]);
		}
		
		ligne_bas = new HBox();
		ligne_bas.setLayoutY(500);
		ligne_bas.setLayoutX(50);
		grillePlateau.getChildren().add(ligne_bas);
		for(int i = 39; i>30; i--) { 
			casesPlateau[i] = new Pane();
			casesPlateau[i].setPrefSize(50, 50);
			casesPlateau[i].setStyle("-fx-background-color: white; -fx-border-color: black");
			
			Label numero = new Label(String.valueOf(i));
			casesPlateau[i].getChildren().add(numero);
			numero.setStyle("-fx-font-weight: bold");
			
			if(jeu.getPlateau().getGrille()[i].getType().equals("Propriete")) { 
				Proprietes prop = (Proprietes) jeu.getPlateau().getCases(i);
				int prix_prop = prop.getPrix();
				Label prix = new Label(String.valueOf(prix_prop));
				prix.setLayoutX(15);
				casesPlateau[i].getChildren().add(prix);
			}
			
			ligne_bas.getChildren().add(casesPlateau[i]);
		}
		
		joueur_actuel = new Label("Au tour de J"+ Integer.toString(jeu.getCurseur()+1));
		joueur_actuel.setLayoutX(300);
		joueur_actuel.setLayoutY((tailleEcran.height*85)/100);
		joueur_actuel.setFont(new Font("Arial", 30));
		panePlateau.getChildren().add(joueur_actuel);
		
		initialisation_familles();
		initialisation_casesSpeciales();
		initialisation_labelDes();
	}
	
	void initialisation_casesSpeciales() {
		Label com1 = new Label("Com.");
		com1.setLayoutX(10);
		com1.setStyle("-fx-font-size: 10");
		casesPlateau[2].getChildren().add(com1);
		
		Label com2 = new Label("Com.");
		com2.setLayoutX(15);
		com2.setStyle("-fx-font-size: 10");
		casesPlateau[17].getChildren().add(com2);
		
		Label com3 = new Label("Com.");
		com3.setLayoutX(15);
		com3.setStyle("-fx-font-size: 10");
		casesPlateau[33].getChildren().add(com3);
		
		Label ch1 = new Label("Chance");
		ch1.setLayoutX(10);
		ch1.setStyle("-fx-font-size: 10");
		casesPlateau[7].getChildren().add(ch1);
		
		Label ch2 = new Label("Chance");
		ch2.setLayoutX(15);
		ch2.setStyle("-fx-font-size: 10");
		casesPlateau[22].getChildren().add(ch2);
		
		Label ch3 = new Label("Chance");
		ch3.setLayoutX(15);
		ch3.setStyle("-fx-font-size: 10");
		casesPlateau[36].getChildren().add(ch3);
		
		Label imp1 = new Label("Impots");
		imp1.setLayoutX(10);
		casesPlateau[4].getChildren().add(imp1);
		
		Label imp2 = new Label("Taxe");
		imp2.setLayoutX(15);
		casesPlateau[38].getChildren().add(imp2);
		
		Label prison = new Label("Prison");
		prison.setLayoutX(15);
		casesPlateau[10].getChildren().add(prison);
		
		Label allerPrison = new Label("Prison");
		allerPrison.setLayoutX(15);
		allerPrison.setStyle("-fx-font-size: 10");
		casesPlateau[30].getChildren().add(allerPrison);
		
		Label parc = new Label("Parc");
		parc.setLayoutX(15);
		casesPlateau[20].getChildren().add(parc);
	}
	
	void initialisation_familles() {
		//Famille violette
		for(int i = 1; i<4; i++) {
			Cases case_curseur = jeu.getPlateau().getGrille()[i];
			if(case_curseur.getType().equals("Propriete")) {
				Rectangle rec = bordure("violette");
				casesPlateau[i].getChildren().add(rec);
			}
		}
		
		//Famille ciel
		for(int i = 6; i<10; i++) {
			Cases case_curseur = jeu.getPlateau().getGrille()[i];
			if(case_curseur.getType().equals("Propriete")) {
				Rectangle rec = bordure("ciel");
				casesPlateau[i].getChildren().add(rec);
			}
		}
		
		//Famille rose
		for(int i = 11; i<15; i++) {
			Cases case_curseur = jeu.getPlateau().getGrille()[i];
			if(case_curseur.getType().equals("Propriete") && i != 12) {
				Rectangle rec = bordure("rose");
				casesPlateau[i].getChildren().add(rec);
			}
		}
		
		//Famille orange
		for(int i = 16; i<20; i++) {
			Cases case_curseur = jeu.getPlateau().getGrille()[i];
			if(case_curseur.getType().equals("Propriete")) {
				Rectangle rec = bordure("orange");
				casesPlateau[i].getChildren().add(rec);
			}
		}
		
		//Famille rouge
		for(int i = 21; i<25; i++) {
			Cases case_curseur = jeu.getPlateau().getGrille()[i];
			if(case_curseur.getType().equals("Propriete")) {
				Rectangle rec = bordure("rouge");
				casesPlateau[i].getChildren().add(rec);
			}
		}
		
		//Famille jaune
		for(int i = 26; i<30; i++) {
			Cases case_curseur = jeu.getPlateau().getGrille()[i];
			if(case_curseur.getType().equals("Propriete") && i != 28) {
				Rectangle rec = bordure("jaune");
				casesPlateau[i].getChildren().add(rec);
			}
		}
		
		//Famille verte
		for(int i = 31; i<35; i++) {
			Cases case_curseur = jeu.getPlateau().getGrille()[i];
			if(case_curseur.getType().equals("Propriete")) {
				Rectangle rec = bordure("verte");
				casesPlateau[i].getChildren().add(rec);
			}
		}
		
		//Famille bleue
		for(int i = 37; i<40; i++) {
			Cases case_curseur = jeu.getPlateau().getGrille()[i];
			if(case_curseur.getType().equals("Propriete")) {
				Rectangle rec = bordure("bleue");
				casesPlateau[i].getChildren().add(rec);
			}
		}
	}
	
	Rectangle bordure(String couleur) {
		Rectangle rec = new Rectangle();
		rec.setWidth(5);
		rec.setHeight(50);
		rec.setLayoutX(45);
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
				pionLabel[i].setLayoutY(15); 
			}
			if(i == 1) {
				pionLabel[i].setLayoutY(15); 
				pionLabel[i].setLayoutX(15); 
			}
			if(i == 2) {
				pionLabel[i].setLayoutY(15); 
				pionLabel[i].setLayoutX(30); 
			}
			if(i == 3) {
				pionLabel[i].setLayoutY(30); 
			}
			if(i == 4) {
				pionLabel[i].setLayoutY(30); 
				pionLabel[i].setLayoutX(15); 
			}
			if(i == 5) {
				pionLabel[i].setLayoutY(30); 
				pionLabel[i].setLayoutX(30); 
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
		joueur_actuel.setLayoutX(300);
		joueur_actuel.setLayoutY((tailleEcran.height*85)/100);
		joueur_actuel.setFont(new Font("Arial", 30));

		panePlateau.getChildren().add(joueur_actuel);
	}
	
	public void affichage_revente_proprietes(int curseur, int montant, Cartes carteTiree) {
		achat_tab[curseur].setDisable(true);
		revente_pane = new Pane();
		revente_pane.setPrefSize(430, 430);
		revente_pane.setStyle("-fx-background-color: white");
		revente_pane.setLayoutX(grillePlateau.getWidth()*30/100);
		revente_pane.setLayoutY(grillePlateau.getWidth()*20/100);
		
		Label texte = new Label ("Joueur "+String.valueOf(jeu.getCurseur()+1)+", vous n'avez plus d'argent pour payer la somme due \n s'elevant a " + montant +"e.\n Vendez une/des propriete(s) :");
		revente_pane.getChildren().add(texte);
		
		int taille = 60;
		Proprietes [] proprietes_joueur_actuel= jeu.getJoueurs()[curseur].getProprietes();
		
		nom_proprietes_button = new Button [proprietes_joueur_actuel.length];
		
		//initialiser les boutons	
		for (int i=0; i<proprietes_joueur_actuel.length; i++) {
			System.out.println("Je suis rentrée dans le for " + i + " fois !");
			
			nom_proprietes_button[i] = new Button (proprietes_joueur_actuel[i].getNom()+" - Prix de vente: "+proprietes_joueur_actuel[i].getPrix());
			nom_proprietes_button[i].setLayoutY(taille);
			taille+=30;
			nom_proprietes_button[i].setLayoutX(75);
			revente_pane.getChildren().add(nom_proprietes_button[i]);
			int n = i;
			
			nom_proprietes_button[n].setOnAction(actionEvent->{
				int ancienne_position = jeu.getJoueurs()[curseur].vendreLaPropriete_IG(n);
				changement_couleur_case_blanche(ancienne_position);
				nom_proprietes_button[n].setVisible(false);
				if (jeu.getJoueurs()[curseur].getArgent() < montant && proprietes_joueur_actuel.length>1) {
					changement_argent(curseur);
					revente_pane.setVisible(false);
					affichage_revente_proprietes(curseur, montant, carteTiree);
				}
				else { 
					panePlateau.getChildren().remove(revente_pane);
					controleur.transactionSelonType(curseur, carteTiree);
			 	}
			});
		}
		panePlateau.getChildren().add(revente_pane);
		revente_pane.setVisible(true);
		
		if(jeu.getJoueurs()[curseur].isRobot()){
			nom_proprietes_button[0].fire();
		}		
	}
	

	//Interface graphique : Boutons
	void bouton_lancer_de_des() {
		lancer = new Button("Lancer");
		lancer.setLayoutX(250);
		lancer.setLayoutY(250);
		grillePlateau.getChildren().add(lancer);
		
		if(jeu.onlyRobot()) lancer.setVisible(false);
		lancer.setOnAction(actionEvent -> {
			int curseur = jeu.getCurseur();
			int[] des = jeu.lancer_de_des();
			controleur.controleur_lancer(des, curseur);
			lancer.setDisable(true);
			
			bouton_achat(curseur);
			bouton_defis(curseur);
		});
	}
	
	void initialisation_labelDes() {
		desLabel = new Label("0 - 0");
		
		desLabel.setLayoutX(250);
		desLabel.setLayoutY(280);
		
		grillePlateau.getChildren().add(desLabel);
	}
	
	void changement_labelDes(int des[]) {
		grillePlateau.getChildren().remove(desLabel);
		
		desLabel = new Label(des[0] + " - " + des[1]);
		desLabel.setLayoutX(250);
		desLabel.setLayoutY(280);
		
		grillePlateau.getChildren().add(desLabel);
	}
	
	void bouton_fin_de_tour() {
		fin = new Button("Fin");
		
		fin.setLayoutX(250);
		fin.setLayoutY(300);
		
		grillePlateau.getChildren().add(fin);
		if(jeu.onlyRobot()) fin.setVisible(false);
		fin.setOnAction(actionEvent -> {
			int curseur = jeu.getCurseur();	
			int position = jeu.getJoueurs()[curseur].getPion().getPosition();
			achat_tab[curseur].setDisable(true);
			vente_tab[proprietaires[position]].setDisable(true);
			lancer.setDisable(false);
			
			defis_tab[curseur].setDisable(true);
			defis_tab[proprietaires[position]].setDisable(true);
			
			controleur.controleur_faillite(curseur);
			if(!jeu.onlyRobot()) {
				controleur.controleur_fin();
			}
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
		panePlateau.getChildren().add(boutons_box);
		quitter_button.setOnAction(actionEvent -> {
			Stage stage = (Stage) quitter_button.getScene().getWindow();
		    stage.close();
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
			
			achat_tab[i] = achat;
			vente_tab[i] = vente;
			defis_tab[i] = defis;
			
			achat.setDisable(true);
			vente.setDisable(true);
			defis.setDisable(true);
			
			achat.setLayoutY(5);
			vente.setLayoutY(35);
			
			defis.setLayoutY(5);
			defis.setLayoutX(50);
			
			joueur_boutons.getChildren().add(achat);
			joueur_boutons.getChildren().add(vente);
			joueur_boutons.getChildren().add(defis);
			
			boutonsJoueurs.getChildren().add(joueur_boutons);
		}
	}
	
	void initialisation_boutons_achat_vente() {
		VBox AV_boutons = new VBox();
		AV_boutons.setLayoutX((tailleEcran.width*20)/100);
		paneJoueurs.getChildren().add(AV_boutons);

		int nbr = jeu.getNbJ();
		for(int i = 0; i<nbr; i++) { 
			Pane joueur_boutons = new Pane();
			joueur_boutons.setPrefSize((tailleEcran.width*10)/100, (int) ((tailleEcran.height-50)/nbr));
			joueur_boutons.setStyle("-fx-background-color: peachpuff; -fx-border-color: white");
			
			achat = new Button("Achat");
			vente = new Button("Vente");
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
	
	void caseChanceCommu(int curseur, Cartes carteTiree) {
		BorderPane carte_pane = new BorderPane();
		Label type_carte = (carteTiree.getType().equals("chance")) ? new Label("CHANCE") : new Label("COMMUNAUTE");
		Label contenu_carte = new Label(carteTiree.getContenu());
		Button bouton_fermer = new Button("Fermer");
		
		type_carte.setFont(new Font("Arial", 17));
		contenu_carte.setFont(new Font("Arial", 15));
		
		carte_pane.setPrefSize(grillePlateau.getWidth()/1.5, grillePlateau.getHeight()/2.5);
		carte_pane.setStyle("-fx-background-color: white");
		carte_pane.setLayoutX(grillePlateau.getWidth()*17/100);
		carte_pane.setLayoutY(grillePlateau.getHeight()*30/100);
		  
		carte_pane.setTop(type_carte);
		carte_pane.setCenter(contenu_carte);
		carte_pane.setBottom(bouton_fermer);
		type_carte.setPadding(new Insets(15,0,0,0));
		BorderPane.setAlignment(type_carte, Pos.TOP_CENTER);
		BorderPane.setAlignment(contenu_carte, Pos.CENTER);
		BorderPane.setAlignment(bouton_fermer, Pos.BOTTOM_CENTER);
		   
		grillePlateau.getChildren().add(carte_pane);
		  
		bouton_fermer.setOnAction(actionEvent -> grillePlateau.getChildren().remove(carte_pane));
		if(jeu.getJoueurs()[curseur].isRobot()){
			PauseTransition wait = new PauseTransition(Duration.seconds(1));
			wait.setOnFinished((e) -> {
				bouton_fermer.fire();
			});
			wait.play();
		}
	}
	
	void bouton_achat(int curseur) {
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
			});
			if(jeu.getJoueurs()[curseur].isRobot()){
				achat_tab[curseur].fire();
				fin.fire();
			}
		}else {
			//avec proprietaire
			if(!libre && argent_suffisant && position_valide && proprietaires[position]!=curseur) {
				bouton_vente(curseur,position);
				if(jeu.getJoueurs()[curseur].isRobot()){
					achat_tab[curseur].fire();
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
			vente_tab[proprietaires[position]].setDisable(false);
			if(jeu.getJoueurs()[proprietaires[position]].isRobot()) {
				vente_tab[proprietaires[position]].fire();
			}
		});
		
		vente_tab[proprietaires[position]].setOnAction(actionEvent ->{
			vente_tab[proprietaires[position]].setDisable(true);
			controleur.controleur_vente(curseur);
			changement_argent(curseur);
			changement_argent(proprietaires[position]);
			proprietaires[position]=curseur;
			if(jeu.getJoueurs()[curseur].isRobot()){
				fin.fire();
			}
		});
	}
	
	void bouton_defis(int curseur) {
		int position = jeu.getJoueurs()[curseur].getPion().getPosition();
		Cases case_curseur = jeu.getPlateau().getGrille()[position];
		
		if(case_curseur.getType().equals("Propriete")) {
			Proprietes prop_curseur = (Proprietes) jeu.getPlateau().getGrille()[position];
			if(!(prop_curseur.est_Libre())){
				defis_tab[curseur].setDisable(false);
				defis_tab[curseur].setOnAction(actionEvent ->{
					defis_tab[proprietaires[position]].setDisable(false);
					defis_tab[curseur].setDisable(true);
				});
			}
		}
		defis_tab[proprietaires[position]].setOnAction(actionEvent ->{
			controleur.controleur_defis(curseur);
			defis_tab[proprietaires[position]].setDisable(true);
			Proprietes prop_curseur = (Proprietes) jeu.getPlateau().getGrille()[position];
			if(jeu.getJoueurs()[curseur].getArgent() < prop_curseur.getPrix()) {
				achat_tab[curseur].setDisable(true);
			}
		});
	}
	
	//Interface graphique : Accueil
	void accueil_jeu() {
		scene_accueil = new AnchorPane();
		scene_accueil.setPrefSize(tailleEcran.width,tailleEcran.height);
		scene_accueil.setStyle("-fx-background-color: #BAEEB4");
		
		GridPane grid = new GridPane();
		grid.setVgap(10);
		grid.setPadding(new Insets(300, 700, 450, 700));

		TextField[] tf = new TextField[6];

		Button[] bt = new Button[6];
		boolean[] flags = new boolean[6];
		for(int i=0;i<6;i++){
			tf[i] = new TextField ();
			tf[i].setPromptText("Pseudo du joueur " + (i+1));
			GridPane.setConstraints(tf[i], 0, i);

			bt[i] = new Button("Mode robot pour joueur " + (i+1));
			GridPane.setConstraints(bt[i], 1, i);
			int finalI = i;
			bt[i].setOnAction(actionEvent->{
				flags[finalI] = !flags[finalI];
			});

		}

		grid.getChildren().addAll(tf);
		grid.getChildren().addAll(bt);


		Button valider = new Button("Valider");
		grid.add(valider,0,7);
		valider.setOnAction(actionEvent->{
			String[] noms = fieldToString(tf);
			if(noms.length>1) {
				jeu.initialisation_joueurs(noms, flags);

				initialisation_plateau();

				affichage_joueurs();

				pionLabel_positionnement();
				affichage_pions_initial();

				bouton_lancer_de_des();
				bouton_fin_de_tour();
				boutons_jeu();
				initialisation_boutons();
				if(jeu.getJoueurs()[jeu.getCurseur()].isRobot()){
					lancerRobot();
					if(jeu.onlyRobot()) controleur.controleur_fin();
				}

			}
		});
		scene_accueil.getChildren().add(grid);
		root.getChildren().add(scene_accueil);
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
	}

	void lancerRobot(){
		lancer.fire();
	}

}
