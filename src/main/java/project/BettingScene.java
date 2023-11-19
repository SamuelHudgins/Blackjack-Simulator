package project;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;

public class BettingScene extends SceneController {

	@FXML private Label bankLabel;
	@FXML private Slider betSlider;
	private int betSliderValue;
	private int playerBalance;
	@FXML private Label maxBetLabel;
	
	@FXML private Label betLabel;
	
	@FXML
	private void initialize() { // Called when this controller's FXML file loads
		betSlider.valueProperty().addListener(new ChangeListener<Number>() {

			@Override
			public void changed(ObservableValue<? extends Number> arg0, Number arg1, Number arg2) {
				betSliderValue = (int) betSlider.getValue();
				betLabel.setText("$" + Integer.toString(betSliderValue));
				bankLabel.setText("Bank: $" + Integer.toString(playerBalance - betSliderValue));
			}			
		});
		
		int min = 1;
		betSliderValue = min;
		betSlider.setMin(min);
		playerBalance = Player.getPlayerInstance().getBalance();
		bankLabel.setText("Bank: $" + Integer.toString(playerBalance));
		betSlider.setMax(playerBalance);
		betSlider.setMajorTickUnit(playerBalance);
		betSlider.setValue(betSliderValue);
		maxBetLabel.setText("$" + Integer.toString(playerBalance));
	}
	
	@FXML
	protected BlackjackScene switchToBlackjackScene() {
		BlackjackScene blackjackScene = super.switchToBlackjackScene();		
		blackjackScene.setup();
		blackjackScene.setPlayer(Player.getPlayerInstance(), betSliderValue);
		return null;
	}
	
	@FXML
	protected MainMenuScene switchToMainMenuScene() {
		super.switchToMainMenuScene();
		return null;
	}
}
