package project;

import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;

public class Hand implements IPlaceable {
	
	@FXML private GridPane cardPane;
	private Pane parent;
	private final int MAX_HAND_SIZE = 11;
	private int size;
	private Card[] cards;
	
	public Hand() {
		size = 0;
		cards = new Card[MAX_HAND_SIZE];
	}	
	
	public void setPosition(Node node) {
		Point2D point = node.localToScene(0, 0);
		cardPane.setLayoutX(point.getX());
		cardPane.setLayoutY(point.getY());
	}
	
	public void addCardToHand(Card card) {
		if (size >= MAX_HAND_SIZE) return;	
		cards[size] = card;
		card.displayCard();
		cardPane.add(card.getCardImage(), size, 0);
		size++;
	}
	
	public int getSize() {
		return size;
	}
	
	public Card[] getCards() {
		return cards;
	}
	
	public int getHandValue() {
		int value = 0;
		for (Card card : cards) {
			if (card == null || card.getFaceDown()) continue;
			if (card.getValue().equals("A")) {
				if (value + card.getFaceValue() > 21) {
					card.setFaceValue(1);
				}
			}
			value += card.getFaceValue();			
		}
		return value;
	}
	
	// IPlaceable methods
	public <T extends Pane> void setParentPane(T _parent) {
		if (parent != null) {
			parent.getChildren().remove(cardPane);
		}
		parent = _parent;
		if (parent != null) parent.getChildren().add(cardPane);
	}
	
	public void setPosition(double x, double y) {
		cardPane.setLayoutX(x);
		cardPane.setLayoutY(y);
	}
}
