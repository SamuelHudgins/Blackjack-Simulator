package project;

import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;

public class HandDisplay {
	
	private StackPane cardStackPane;
	private Label handLabel;        
	private Label statusLabel;      
	
	public HandDisplay(StackPane cardStackPane, Label handLabel, Label statusLabel) {
		this.cardStackPane = cardStackPane;
		this.handLabel = handLabel;
		this.statusLabel = statusLabel;
		this.cardStackPane.getChildren().forEach(image -> ((ImageView)image).setImage(null));
		this.statusLabel.setText("");
	}
	
	public void setCardImage(int index, Image image) {
		((ImageView) cardStackPane.getChildren().get(index)).setImage(image);
	}
	
	public void setHandLabel(String value) {
		handLabel.setText(value);
	}
	
	public void setStatusLabel(String value) {
		statusLabel.setText(value);
	}
}
