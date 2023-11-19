package project;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

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
			startStage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		Player.setPlayerInstance("Player");
		launch(args);	
	}

}