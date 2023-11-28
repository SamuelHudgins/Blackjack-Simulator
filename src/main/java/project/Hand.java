package project;

import java.util.ArrayList;

import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;

/**
 * Holds information for representing instances of hands. A {@code Hand} 
 * instance holds {@code Card} instances and contains the bet value placed on a hand.
 */
public class Hand implements IPlaceable {
	
	@FXML private GridPane cardPane;
	private Pane parent;
	private final int MAX_HAND_SIZE = 11;
	private ArrayList<Card> cards;
	private boolean finished;
	private double bet;
	
	public Hand() {
		cards = new ArrayList<Card>(MAX_HAND_SIZE);
	}	
	
	public void setPosition(Node node) {
		Point2D point = node.localToScene(0, 0);
		cardPane.setLayoutX(point.getX());
		cardPane.setLayoutY(point.getY());
	}
	
	public void addCardToHand(Card card) {
		if (cards.size() >= MAX_HAND_SIZE) return;	
		cards.add(card);
		card.displayCard();
		cardPane.add(card.getCardImage(), cards.size(), 0);
	}
	
	public void removeCardFromHand(Card card) {
		if (cards.contains(card)) {
			cardPane.getChildren().remove(cards.indexOf(card));
			cards.remove(card);
		}
	}
	
	public int getSize() {
		return cards.size();
	}
	
	public ArrayList<Card> getCards() {
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
	
	public boolean hasNaturalBlackjack() {
		return getHandValue() == 21 && getSize() == 2;
	}
	
	public boolean busted() {
		return getHandValue() > 21;
	}
	
	public void setFinished(boolean finished) {
		this.finished = finished;
	}
	
	public boolean getFinished() {
		return finished;
	}
	
	public void setBet(double bet) {
		this.bet = bet;
	}
	
	public double getBet() {
		return bet;
	}
	
	public void setSplitScale() {
		cardPane.setScaleX(0.6);
		cardPane.setScaleY(0.6);
	}
	
	public void resetScale() {
		cardPane.setScaleX(1);
		cardPane.setScaleY(1);
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
