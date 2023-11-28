package project;

import javafx.scene.control.Label;

/**
 * Holds the references to GUI elements used for displaying hand values and statuses.
 */
public class HandDisplay {
	
	private Label handLabel;        
	private Label statusLabel;      
	
	public HandDisplay(Label handLabel, Label statusLabel) {
		this.handLabel = handLabel;
		this.statusLabel = statusLabel;
		this.statusLabel.setText("");
	}
	
	public void setHandLabel(String value) {
		handLabel.setText(value);
	}
	
	public void setStatusLabel(String value) {
		statusLabel.setText(value);
	}
}
