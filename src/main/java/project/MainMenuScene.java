package project;

import javafx.application.Platform;
import javafx.fxml.FXML;

public class MainMenuScene extends SceneController {

	@FXML
	protected BettingScene switchToBettingScene() {
		super.switchToBettingScene();
		return null;
	}
	
	@FXML
	protected TutorialScene switchToTutorialScene() {
		super.switchToTutorialScene();
		return null;
	}
	
	@FXML
	protected LoginScene switchToLoginScene() {
		super.switchToLoginScene();
		return null;
	}
	
	@FXML
	protected StatsScene switchToStatsScene() {
		super.switchToStatsScene();
		return null;
	}
	
	@FXML
	private void exitGame() {
		Platform.exit();
	}
	
}
