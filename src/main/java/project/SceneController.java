package project;

import java.io.IOException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * The base class for all classes that control GUI scene events in the application.
 */
public abstract class SceneController {
		
	private static Stage stage;	
	
	/**
	 * Sets the stage that all scenes and subclasses of {@code SceneController} will 
	 * use to display the GUI to the given stage.
	 * @param mainStage The stage to display GUI through. This is a top-level 
	 * JavaFX container.
	 */
	public static void setStage(Stage mainStage) {
		stage = mainStage;
	}
	
	/**
	 * Switches the current scene to a new one with the provided {@code Scenes} value.
	 * @param sceneName The {@code Scenes} type to use for setting the new scene. This 
	 * method uses this value to load an associated FXML file containing the scene to load.
	 * @return The controller associated with the loaded scene's root object.
	 */
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
