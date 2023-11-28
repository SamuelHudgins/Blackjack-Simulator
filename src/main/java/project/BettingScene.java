package project;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;

/**
 * This class extends the {@code SceneController} and manages the GUI events for the betting scene.
 */
public class BettingScene extends SceneController {

	@FXML private Label bankLabel;
	@FXML private Slider betSlider;
	private int betSliderValue;
	private Double playerBalance = 0.0;
	@FXML private Label maxBetLabel;	
	@FXML private Label betLabel;
	private Player player;
	
	@FXML private Pane accountEmptyPane;
	@FXML private Text accountEmptyText;
	
	@FXML
	private void initialize() { // Called when this controller's FXML file loads
		betSlider.valueProperty().addListener(new ChangeListener<Number>() {

			@Override
			public void changed(ObservableValue<? extends Number> arg0, Number arg1, Number arg2) {
				betSliderValue = (int) betSlider.getValue();
				betLabel.setText("$" + Integer.toString(betSliderValue));
				bankLabel.setText("Bank: $" + Double.toString(playerBalance - betSliderValue));
			}			
		});
		
		player = Player.getInstance();
		if (player.getBalance() <= 0) {			
			// Reset the player's stats and give them money if their balance is depleted.
			Player player = Player.getInstance();
			DatabaseManager.getInstance().resetUserStats(player.getUsername(), Player.START_BALANCE);
			player.setBalance(Player.START_BALANCE);
			showAccountResetMessage();
		}
		 
		int min = 1;
		betSliderValue = min;
		betSlider.setMin(min);
		playerBalance = player.getBalance();
		bankLabel.setText("Bank: $" + Double.toString(playerBalance));
		betSlider.setMax(playerBalance);
		betSlider.setMajorTickUnit(playerBalance);
		betSlider.setValue(betSliderValue);
		maxBetLabel.setText("$" + Double.toString(playerBalance));
	}
	
	@FXML
	protected BlackjackScene switchToBlackjackScene() {
		player.adjustBalance(-betSliderValue);
		if (!player.getGuest()) DatabaseManager.getInstance().setUserBankBalance(player.getUsername(), player.getBalance());
		player.setBet(betSliderValue);
		super.switchToBlackjackScene().setup();
		return null;
	}
	
	@FXML
	protected MainMenuScene switchToMainMenuScene() {
		super.switchToMainMenuScene();
		return null;
	}

	private void showAccountResetMessage() {
		accountEmptyPane.setVisible(true);
		String text = "Your account transaction history has been reset."
				+ "\n\nAn anonymous donor\nhas gifted you $" + Player.START_BALANCE + ".";
		accountEmptyText.setText(text);
	}
	
	@FXML
	private void hideAccountResetMessage() {
		accountEmptyPane.setVisible(false);
	}
}
