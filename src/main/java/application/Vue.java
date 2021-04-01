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
	private AnchorPane jeu_pane;
	private Pane plateau_pane;
	private Pane revente_pane;
	
	private Pane[] tabCase_pane = new Pane[40];
	private VBox colonne_gauche;
	private VBox colonne_droite;
	private HBox ligne_bas;
	private HBox ligne_haut;
	
	//Proprietaires
	private int[] proprietaires = new int [40];
	
		//Joueurs
	private AnchorPane joueurs_pane;
	private Label[] pseudo_tab = new Label[6];
	private Pane[] joueursPane_tab = new Pane[6];
	private Label joueur_actuel;
	
		//Boutons
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
		
		joueur_actuel = new Label("Au tour de J"+ Integer.toString(jeu.getCurseur()+1));
		joueur_actuel.setLayoutX(300);
		joueur_actuel.setLayoutY((tailleEcran.height*85)/100);
		joueur_actuel.setFont(new Font("Arial", 30));
		jeu_pane.getChildren().add(joueur_actuel);
		
		initialisation_familles();
		initialisation_casesSpeciales();
	}
	
	void initialisation_casesSpeciales() {
		Label com1 = new Label("Com.");
		com1.setLayoutX(10);
		com1.setStyle("-fx-font-size: 10");
		tabCase_pane[2].getChildren().add(com1);
		
		Label com2 = new Label("Com.");
		com2.setLayoutX(15);
		com2.setStyle("-fx-font-size: 10");
		tabCase_pane[17].getChildren().add(com2);
		
		Label com3 = new Label("Com.");
		com3.setLayoutX(15);
		com3.setStyle("-fx-font-size: 10");
		tabCase_pane[33].getChildren().add(com3);
		
		Label ch1 = new Label("Chance");
		ch1.setLayoutX(10);
		ch1.setStyle("-fx-font-size: 10");
		tabCase_pane[7].getChildren().add(ch1);
		
		Label ch2 = new Label("Chance");
		ch2.setLayoutX(15);
		ch2.setStyle("-fx-font-size: 10");
		tabCase_pane[22].getChildren().add(ch2);
		
		Label ch3 = new Label("Chance");
		ch3.setLayoutX(15);
		ch3.setStyle("-fx-font-size: 10");
		tabCase_pane[36].getChildren().add(ch3);
		
		Label imp1 = new Label("Impots");
		imp1.setLayoutX(10);
		tabCase_pane[4].getChildren().add(imp1);
		
		Label imp2 = new Label("Taxe");
		imp2.setLayoutX(15);
		tabCase_pane[38].getChildren().add(imp2);
		
		Label prison = new Label("Prison");
		prison.setLayoutX(15);
		tabCase_pane[10].getChildren().add(prison);
		
		Label allerPrison = new Label("Prison");
		allerPrison.setLayoutX(15);
		allerPrison.setStyle("-fx-font-size: 10");
		tabCase_pane[30].getChildren().add(allerPrison);
		
		Label parc = new Label("Parc");
		parc.setLayoutX(15);
		tabCase_pane[20].getChildren().add(parc);
	}
	
	void initialisation_familles() {
		//Famille violette
		for(int i = 1; i<4; i++) {
			Cases case_curseur = jeu.getPlateau().getGrille()[i];
			if(case_curseur.getType().equals("Propriete")) {
				Rectangle rec = bordure("violette");
				tabCase_pane[i].getChildren().add(rec);
			}
		}
		
		//Famille ciel
		for(int i = 6; i<10; i++) {
			Cases case_curseur = jeu.getPlateau().getGrille()[i];
			if(case_curseur.getType().equals("Propriete")) {
				Rectangle rec = bordure("ciel");
				tabCase_pane[i].getChildren().add(rec);
			}
		}
		
		//Famille rose
		for(int i = 11; i<15; i++) {
			Cases case_curseur = jeu.getPlateau().getGrille()[i];
			if(case_curseur.getType().equals("Propriete") && i != 12) {
				Rectangle rec = bordure("rose");
				tabCase_pane[i].getChildren().add(rec);
			}
		}
		
		//Famille orange
		for(int i = 16; i<20; i++) {
			Cases case_curseur = jeu.getPlateau().getGrille()[i];
			if(case_curseur.getType().equals("Propriete")) {
				Rectangle rec = bordure("orange");
				tabCase_pane[i].getChildren().add(rec);
			}
		}
		
		//Famille rouge
		for(int i = 21; i<25; i++) {
			Cases case_curseur = jeu.getPlateau().getGrille()[i];
			if(case_curseur.getType().equals("Propriete")) {
				Rectangle rec = bordure("rouge");
				tabCase_pane[i].getChildren().add(rec);
			}
		}
		
		//Famille jaune
		for(int i = 26; i<30; i++) {
			Cases case_curseur = jeu.getPlateau().getGrille()[i];
			if(case_curseur.getType().equals("Propriete") && i != 28) {
				Rectangle rec = bordure("jaune");
				tabCase_pane[i].getChildren().add(rec);
			}
		}
		
		//Famille verte
		for(int i = 31; i<35; i++) {
			Cases case_curseur = jeu.getPlateau().getGrille()[i];
			if(case_curseur.getType().equals("Propriete")) {
				Rectangle rec = bordure("verte");
				tabCase_pane[i].getChildren().add(rec);
			}
		}
		
		//Famille bleue
		for(int i = 37; i<40; i++) {
			Cases case_curseur = jeu.getPlateau().getGrille()[i];
			if(case_curseur.getType().equals("Propriete")) {
				Rectangle rec = bordure("bleue");
				tabCase_pane[i].getChildren().add(rec);
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
		joueurs_pane = new AnchorPane();
		
		joueurs_pane.setPrefSize((tailleEcran.width*30)/100, tailleEcran.height);
		joueurs_pane.setStyle("-fx-background-color: white");
		joueurs_pane.setLayoutX((tailleEcran.width*70)/100);
		
		root.getChildren().add(joueurs_pane);
		
		VBox joueurs_liste = new VBox();
		joueurs_pane.getChildren().add(joueurs_liste);
		int nbr = jeu.getNbJ();
		for(int i = 0; i<nbr; i++) {
			joueursPane_tab[i] = new Pane();
			
			joueursPane_tab[i].setPrefSize((tailleEcran.width*20)/100, (int) ((tailleEcran.height-50)/nbr));
			
			Label pseudo = new Label("Joueur "+ String.valueOf(i+1)+": "+jeu.getJoueurs()[i].getNom());
			Label argent = new Label("Argent :" + jeu.getJoueurs()[i].getArgent());
			
			argent.setLayoutY(50);
			argent.setLayoutX(10);
			
			pseudo.setLayoutX(10);
			
			joueursPane_tab[i].setStyle("-fx-background-color: mistyrose; -fx-border-color: white");
			style_pane(i);
			
			joueursPane_tab[i].getChildren().add(pseudo);
			joueursPane_tab[i].getChildren().add(argent);
			joueurs_liste.getChildren().add(joueursPane_tab[i]);
			
			
		}
	}
	
	void style_pane(int i) {
		switch(i){
			case 0:
				joueursPane_tab[0].setStyle("-fx-background-color: #C4E6E9; -fx-border-color: white");
				break;
			case 1:
				joueursPane_tab[1].setStyle("-fx-background-color: #F6E8A2; -fx-border-color: white");
				break;
			case 2:
				joueursPane_tab[2].setStyle("-fx-background-color: #DDBDBB; -fx-border-color: white");
				break;
			case 3:
				joueursPane_tab[3].setStyle("-fx-background-color: #ADBAA1; -fx-border-color: white");
				break;
			case 4:
				joueursPane_tab[4].setStyle("-fx-background-color: #B2A9C6; -fx-border-color: white");
				break;
			case 5:
				joueursPane_tab[5].setStyle("-fx-background-color: #E6B589; -fx-border-color: white");
				break;
		}
	}
	
	void definition_label() {
		for(int i = 0; i< jeu.getNbJ(); i++) {
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
		for(int i = 0; i< jeu.getNbJ(); i++) {
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
		switch(curseur) {
		case 0:
			tabCase_pane[position].setStyle("-fx-background-color: #C4E6E9; -fx-border-color: white");
			break;
		case 1:
			tabCase_pane[position].setStyle("-fx-background-color: #F6E8A2; -fx-border-color: white");
			break;
		case 2:
			tabCase_pane[position].setStyle("-fx-background-color: #DDBDBB; -fx-border-color: white");
			break;
		case 3:
			tabCase_pane[position].setStyle("-fx-background-color: #ADBAA1; -fx-border-color: white");
			break;
		case 4:
			tabCase_pane[position].setStyle("-fx-background-color: #B2A9C6; -fx-border-color: white");
			break;
		case 5:
			tabCase_pane[position].setStyle("-fx-background-color: #E6B589; -fx-border-color: white");
			break;
		}
	}
	
	void changement_couleur_case_blanche(int position) {
		tabCase_pane[position].setStyle("-fx-background-color: #FFFFFF; -fx-border-color: black");
	}
	
	void changement_argent(int curseur) {
		joueursPane_tab[curseur].getChildren().remove(1);
		Label argent = new Label("Argent :" + jeu.getJoueurs()[curseur].getArgent());
		
		argent.setLayoutY(50);
		argent.setLayoutX(10);
		joueursPane_tab[curseur].getChildren().add(argent);
	}
	
	public void changement_joueur_actuel() {
		jeu_pane.getChildren().remove(joueur_actuel);
		joueur_actuel = new Label("Au tour de J"+ String.valueOf(jeu.getCurseur()+1));
		joueur_actuel.setLayoutX(300);
		joueur_actuel.setLayoutY((tailleEcran.height*85)/100);
		joueur_actuel.setFont(new Font("Arial", 30));

		jeu_pane.getChildren().add(joueur_actuel);
	}
	
	public void affichage_revente_proprietes(int curseur) {
		//FIXME: desactiver bouton achat a partir du moment ou on a pas assez d'argent pour payer le loyer
		achat_tab[curseur].setDisable(true);
		revente_pane = new Pane();
		revente_pane.setPrefSize(430, 430);
		revente_pane.setStyle("-fx-background-color: white");
		revente_pane.setLayoutX(plateau_pane.getWidth()*30/100);
		revente_pane.setLayoutY(plateau_pane.getWidth()*20/100);
		
		int position = jeu.getJoueurs()[curseur].getPion().getPosition();
		Proprietes propriete_actuelle = (Proprietes) jeu.getPlateau().getCases(position);
		Label texte = new Label ("Joueur "+String.valueOf(jeu.getCurseur()+1)+", vous n'avez plus d'argent pour payer le loyer \n s'elevant a " +propriete_actuelle.getLoyer() +".\n Vendez une/des propriete(s):");
		revente_pane.getChildren().add(texte);
		
		int taille =60;
		Proprietes [] proprietes_joueur_actuel= jeu.getJoueurs()[curseur].getProprietes();
		
		nom_proprietes_button = new Button [proprietes_joueur_actuel.length];
		//initialiser les boutons
		for (int i=0; i<proprietes_joueur_actuel.length; i++) {
			nom_proprietes_button[i] = new Button (proprietes_joueur_actuel[i].getNom()+" - Prix de vente: "+proprietes_joueur_actuel[i].getPrix());
			nom_proprietes_button[i].setLayoutY(taille);
			taille+=30;
			nom_proprietes_button[i].setLayoutX(75);
			revente_pane.getChildren().add(nom_proprietes_button[i]);
			int n = i;

			nom_proprietes_button[n].setOnAction(actionEvent->{
				int ancienne_position= controleur.controleur_vendreSesProprietes(curseur,n);
				changement_couleur_case_blanche(ancienne_position);
				changement_argent(curseur);
				revente_pane.setVisible(false);
				if (jeu.getJoueurs()[curseur].getArgent()<propriete_actuelle.getLoyer() && proprietes_joueur_actuel.length>1) {
					affichage_revente_proprietes(curseur);
				}else {
					jeu_pane.getChildren().remove(revente_pane);
					controleur.controleur_loyerIG(propriete_actuelle);
					//FIXME: update l'argent de l'ancien proprio une fois que le joueur a assez d'argent pour payer le loyer
					changement_argent(proprietaires[position]);
					changement_argent(curseur);
				}
			});	
		}


		
		jeu_pane.getChildren().remove(revente_pane);
		jeu_pane.getChildren().add(revente_pane);
		revente_pane.setVisible(true);

		if(jeu.getJoueurs()[curseur].isRobot()){
			nom_proprietes_button[0].fire();

		}

		if(jeu.getJoueurs()[curseur].isRobot()){

		}
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
		if(jeu.onlyRobot()) lancer.setVisible(false);
		lancer.setOnAction(actionEvent -> {
			int curseur = jeu.getCurseur();
			int[] des = jeu.lancer_de_des();
			System.out.println(des[0] + " " + des[1]);
			controleur.controleur_lancer(des, curseur);
			lancer.setDisable(true);
			
			bouton_achat(curseur);
		});
	}
	
	void bouton_fin_de_tour() {
		fin = new Button("Fin");
		
		fin.setLayoutX(250);
		fin.setLayoutY(300);
		
		plateau_pane.getChildren().add(fin);
		if(jeu.onlyRobot()) fin.setVisible(false);
		fin.setOnAction(actionEvent -> {
			int curseur = jeu.getCurseur();	
			int position = jeu.getJoueurs()[curseur].getPion().getPosition();
			achat_tab[curseur].setDisable(true);
			vente_tab[proprietaires[position]].setDisable(true);
			lancer.setDisable(false);
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
		int position = jeu.getJoueurs()[curseur].getPion().getPosition();
			
		BorderPane carte_pane = new BorderPane();
		Label type_carte = (carteTiree.getType().equals("chance")) ? new Label("CHANCE") : new Label("COMMUNAUTE");
		Label contenu_carte = new Label(carteTiree.getContenu());
		Button bouton_fermer = new Button("Fermer");
		
		type_carte.setFont(new Font("Arial", 17));
		contenu_carte.setFont(new Font("Arial", 15));
		
		carte_pane.setPrefSize(plateau_pane.getWidth()/1.5, plateau_pane.getHeight()/2.5);
		carte_pane.setStyle("-fx-background-color: white");
		carte_pane.setLayoutX(plateau_pane.getWidth()*17/100);
		carte_pane.setLayoutY(plateau_pane.getHeight()*30/100);
		  
		carte_pane.setTop(type_carte);
		carte_pane.setCenter(contenu_carte);
		carte_pane.setBottom(bouton_fermer);
		type_carte.setPadding(new Insets(15,0,0,0));
		BorderPane.setAlignment(type_carte, Pos.TOP_CENTER);
		BorderPane.setAlignment(contenu_carte, Pos.CENTER);
		BorderPane.setAlignment(bouton_fermer, Pos.BOTTOM_CENTER);
		   
		plateau_pane.getChildren().add(carte_pane);
		  
		bouton_fermer.setOnAction(actionEvent -> plateau_pane.getChildren().remove(carte_pane));
		if(jeu.getJoueurs()[curseur].isRobot()){
			PauseTransition wait = new PauseTransition(Duration.seconds(0.5));

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

				definition_label();
				affichage_pions_initial();

				bouton_lancer_de_des();
				bouton_fin_de_tour();
				boutons_jeu();
				initialisation_boutons_achat_vente();
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
	}

	void lancerRobot(){
		lancer.fire();

	}

}
