package project;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;

public class BettingScene extends SceneController {

	@FXML
	private Slider betSlider;
	private int betSliderValue;
	
	@FXML
	private Label betLabel;
	
	@FXML
	private void initialize() { // Called when this controller's FXML file loads
		betSlider.valueProperty().addListener(new ChangeListener<Number>() {

			@Override
			public void changed(ObservableValue<? extends Number> arg0, Number arg1, Number arg2) {
				betSliderValue = (int) betSlider.getValue();
				betLabel.setText(Integer.toString(betSliderValue));
			}			
		});
		
		int min = 1;
		betSliderValue = min;
		betSlider.setMin(min);
		betSlider.setMax(100);
		betSlider.setValue(betSliderValue);
	}
	
	@FXML
	protected BlackjackScene switchToBlackjackScene() {
		BlackjackScene blackjackScene = super.switchToBlackjackScene();
		blackjackScene.setPlayerBetAmount(betSliderValue);
		return null;
	}
	
	@FXML
	protected MainMenuScene switchToMainMenuScene() {
		super.switchToMainMenuScene();
		return null;
	}
}
