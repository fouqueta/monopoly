package application;
	
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {
	
	private Vue vue;
	
	@Override
	public void start(Stage primaryStage) {
		/*try {
			URL url = getClass().getResource("/Monopoly.fxml");
			FXMLLoader fxmlLoader = new FXMLLoader(url);
			AnchorPane root = (AnchorPane) fxmlLoader.load();
			Scene scene = new Scene(root, 400, 400);
			primaryStage.setMaximized(true);
			primaryStage.setScene(scene);
			
			primaryStage.show();
		}catch(Exception e){
			e.printStackTrace();
		}
		*/
		
		Controleur controleur = new Controleur();
		vue = new Vue(controleur);
		controleur.setVue(vue);
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
