package project;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

/**
 * This class extends the {@code SceneController} and manages the GUI events 
 * for the main menu scene.
 */
public class MainMenuScene extends SceneController {

	@FXML private Label usernameLabel;	
	@FXML private Label balanceLabel;
	
	@FXML
	private void initialize() {
		Player player = Player.getInstance();
		String displayName = player.getGuest() ? "Logged in as Guest" : player.getUsername();
		usernameLabel.setText(displayName);
		balanceLabel.setText("Balance: $" + Double.toString(player.getBalance()));
	}
	
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
