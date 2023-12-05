package project;

import javafx.scene.control.Label;

/**
 * Holds the references to the GUI element used for displaying a hand's value.
 */
public class HandDisplay {
	
	private Label handLabel;         
	
	public HandDisplay(Label handLabel) {
		this.handLabel = handLabel;
	}
	
	public void setHandLabel(String value) {
		handLabel.setText(value);
	}
}
