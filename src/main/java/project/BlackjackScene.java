package project;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class BlackjackScene extends SceneController {

	private int playerBet;
	
	@FXML
	private Label playerBetLabel;
	
	@FXML
	protected BettingScene switchToBettingScene() {
		super.switchToBettingScene();
		return null;
	}
	
	@FXML
	protected MainMenuScene switchToMainMenuScene() {
		super.switchToMainMenuScene();
		return null;
	}
	
	public void setPlayerBetAmount(int amount) {
		playerBet = amount;
		playerBetLabel.setText("Bet: " + Integer.toString(playerBet));
	}
}
