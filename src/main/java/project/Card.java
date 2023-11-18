package project;

import javafx.scene.image.Image;

public class Card {
	
	private String value; 
	private String suit; 
	private int faceValue; 
	private boolean faceDown;
	
	public Card(String value, String suit) {
		this.value = value;
		this.suit = suit;
		
		if (value.equals("A")) {
			faceValue =  11;
		} else if (value.equals("J") || value.equals("Q") || value.equals("K")) {
			faceValue =  10;
		} else {
			faceValue = Integer.parseInt(value); // For numbers 2-10
		}
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
	}
	
	public Image getImage() { 
		String path = faceDown ? "/images/BACK.png" : "/images/"+value+"-"+suit+".png";
		return new Image(getClass().getResource(path).toExternalForm());
	}
}
