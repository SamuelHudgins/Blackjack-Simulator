package project;

import javafx.scene.control.Label;

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
