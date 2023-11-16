package project;

import java.io.IOException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public abstract class SceneController {
		
	private static Stage stage;
	
	public static void setStage(Stage mainStage) {
		stage = mainStage;
	}
	
	private SceneController switchScene(Scenes sceneName) {		
		FXMLLoader loader = new FXMLLoader(getClass().getResource(sceneName.toPath()));
		Parent root = null;
		try {
			root = loader.load();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		Scene scene = new Scene(root);
		stage.setScene(scene);		
		stage.show();
		return loader.getController();
	}
	
	protected StartScene switchToStartScene() {
		return (StartScene) switchScene(Scenes.START);
	}
	
	protected LoginScene switchToLoginScene() {
		return (LoginScene) switchScene(Scenes.LOGIN);
	}
	
	protected RegistrationScene switchToRegistrationScene() {
		return (RegistrationScene) switchScene(Scenes.REGISTRATION);
	}
	
	protected MainMenuScene switchToMainMenuScene() {
		return (MainMenuScene) switchScene(Scenes.MAIN_MENU);
	}
	
	protected TutorialScene switchToTutorialScene() {
		return (TutorialScene) switchScene(Scenes.TUTORIAL);
	}
	
	protected BettingScene switchToBettingScene() {
		return (BettingScene) switchScene(Scenes.BETTING);
	}
	
	protected BlackjackScene switchToBlackjackScene() {
		return (BlackjackScene) switchScene(Scenes.BLACKJACK);
	}
	
	protected StatsScene switchToStatsScene() {
		return (StatsScene) switchScene(Scenes.STATS);
	}
	
	protected DeleteScene switchToDeleteScene() {
		return (DeleteScene) switchScene(Scenes.DELETE);
	}
}
