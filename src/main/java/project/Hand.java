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
	private Node sceneNode;
	private Pane parent;
	private final int MAX_HAND_SIZE = 11;
	private ArrayList<Card> cards;
	private boolean finished;
	private double bet;
	private double insuranceBet;
	
	public Hand() {
		cards = new ArrayList<Card>(MAX_HAND_SIZE);
	}	
	
	public void addCardToHand(Card card) {
		if (cards.size() >= MAX_HAND_SIZE) return;	
		cardPane.add(card.getCardImage(), cards.size(), 0);
		cards.add(card);
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
	
	/**
	 * Returns the total value of this hand. If this hand has an ace card and its 
	 * value exceeds 21, the ace card's value will decrease to 1 (from 11), so 
	 * the hand's value will decrease by 10.
	 * @return The total value of this hand.
	 */
	public int getHandValue() {
		int value = 0;
		int aces = 0;
		for (Card card : cards) {
			if (card.getFaceDown()) continue;
			if (card.getValue().equals("A")) {
				aces++;				
			}
			value += card.getFaceValue();			
		}
		while (value > 21 && aces > 0) {
			value -= 10;
			aces--;
		}
		return value;		
	}
	
	
	/**
	 * Used for retrieving the dealer's current face-up card.
	 * @return The dealer's current face-up card.
	 */
	public Card getFaceUpCard() {
		for (Card card : cards) {
			if (!card.getFaceDown()) return card;
		}
		return null;
	}
	
	/**
	 * Determines whether or not someone could split this hand.
	 * @return True if this hand has two cards of the same value. Otherwise, false.
	 */
	public boolean splittable() {
		if (cards.size() != 2) return false;
		return cards.get(0).getFaceValue() == cards.get(1).getFaceValue();
	}
	
	/**
	 * Determines whether or not this hand has an Ace card.
	 * @return True if this hand has an Ace card. Otherwise, false.
	 */
	public boolean hasAce() {
		for (Card card : cards) {
			if (card.getValue() == "A") return true;		
		}
		return false;
	}
	
	/**
	 * Determines whether or not this hand has a natural blackjack.
	 * @return True if this hand has two cards that sum to 21. Otherwise, false.
	 */
	public boolean hasNaturalBlackjack() {
		return cards.size() == 2 && getHandValue() == 21;
	}
	
	/**
	 * Determines whether or not this hand busted.
	 * @return True if this hand's sum is greater than 21. Otherwise, false.
	 */
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
	
	public void doubleMainBet() {
		bet *= 2;
	}
	
	public double getBet() {
		return bet;
	}
	
	public void setInsuranceBet(double bet) {
		insuranceBet = bet;
	}
	
	public double getInsuranceBet() {
		return insuranceBet;
	}
	
	public Point2D getDefaultScale() {
		return new Point2D(1, 1);
	}
	
	public Point2D getCurrentScale() {
		return new Point2D(cardPane.getScaleX(), cardPane.getScaleX());
	}
	
	public Point2D getSplitScale() {
		return new Point2D(0.6, 0.6);
	}
	
	public void resetScale() {
		cardPane.setScaleX(1);
		cardPane.setScaleY(1);
	}
	
	// IPlaceable methods
	/**
	 * Sets the node this object will use for calculating scene space positions. 
	 * The provided node should be the root node of a scene.
	 */
	public void setSceneNode(Node node) {
		sceneNode = node;
	}
	
	/**
	 * Returns the JavaFX node this object controls.
	 */
	public Node getNode() {
		return cardPane;
	}
	
	/**
	 * Sets the parent node of this object's JavaFX node. The parent node must be 
	 * a type of {@code Pane}.
	 * @param The parent node of this object's JavaFX node.
	 */
	public <T extends Pane> void setParentPane(T _parent) {
		if (parent != null) {
			parent.getChildren().remove(cardPane);
		}
		parent = _parent;
		if (parent != null) parent.getChildren().add(cardPane);
	}
	
	/**
	 * Returns the scene space position of this node.
	 */
	public Point2D getPosition() {	
		Point2D point = cardPane.localToScene(0, 0);
		return new Point2D(point.getX(), point.getY());
	}
	
	/**
	 * Sets the scene space position of this node. This position is based on the center of the 
	 * set scene node. If the provided {@code x} and {@code y} values are zero, this object 
	 * will be placed at the center of the scene node.
	 * @param x The x offset from the center of the scene node.
	 * @param y The y offset from the center of the scene node.
	 */
	public void setPosition(double x, double y) {
		Double centerX = sceneNode.getLayoutBounds().getCenterX();
		Double centerY = sceneNode.getLayoutBounds().getCenterY();
		Point2D point = sceneNode.localToScene(centerX + x, centerY + y);
		cardPane.setLayoutX(point.getX());
		cardPane.setLayoutY(point.getY());
	}	
	
	/**
	 * Sets the scene space position of this node. This position is based on the scene 
	 * space position of the provided node.
	 */
	public void setPosition(Node node) {
		Point2D point = node.localToScene(0, 0);
		cardPane.setLayoutX(point.getX());
		cardPane.setLayoutY(point.getY());
	}
}
