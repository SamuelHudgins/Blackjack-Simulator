package project;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

public class Card implements IPlaceable {
	
	@FXML private ImageView cardImage;
	private Pane parent;
	private String value; 
	private String suit; 
	private int faceValue; 
	private boolean faceDown;
	
	public void setValueAndSuit(String value, String suit) {
		this.value = value;
		this.suit = suit;
		
		if (value.equals("A")) faceValue = 11;
		else if (("JQK").contains(value)) faceValue = 10;			
		else faceValue =  Integer.parseInt(value); // For numbers 2-10
	}
	
	public String getValue() {
		return value;
	}
	
	public int getFaceValue() {		
		return faceValue;
	}
	
	public void setFaceValue(int value) {
		faceValue = value;
	}
	
	public boolean getFaceDown() {
		return faceDown;
	}
	
	public void setFaceDown(boolean down) {
		faceDown = down;
		displayCard();
	}
	
	public Node getCardImage() {
		return cardImage;
	}
	
	public void displayCard() {
		String path = faceDown ? "/images/BACK.png" : "/images/"+value+"-"+suit+".png";
		cardImage.setImage(new Image(getClass().getResource(path).toExternalForm()));
	}
	
	// IPlaceable methods
	public <T extends Pane> void setParentPane(T _parent) {
		if (parent != null) {
			parent.getChildren().remove(cardImage);
		}
		parent = _parent;
		if (parent != null) parent.getChildren().add(cardImage);
	}
	
	public void setPosition(double x, double y) {
		cardImage.setLayoutX(x);
		cardImage.setLayoutY(y);
	}	
}
