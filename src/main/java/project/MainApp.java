package project;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Contains the processes for setting up a database and starting the JavaFX application.
 */
public class MainApp extends Application {
	
	@Override
	public void start(Stage startStage) {
		try {
			Parent root = FXMLLoader.load(getClass().getResource(Scenes.START.toPath()));
			Scene scene = new Scene(root);
			scene.getStylesheets().add(getClass().getResource("/css/betSlider.css").toExternalForm());
			SceneController.setStage(startStage);
			startStage.setScene(scene);
			startStage.setTitle("Blackjack Simulator");
			startStage.setResizable(false);
			startStage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		DatabaseManager DBM = DatabaseManager.getInstance();
		DBM.createUsersTable();
		DBM.createBanksTable();
		DBM.createWinsTable();
		DBM.createLossesTable();
		launch(args);	
		DBM.closeConnection();		
	}

}