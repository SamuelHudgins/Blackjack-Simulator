package project;

import java.util.ArrayList;
import java.util.Random;

import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.layout.Pane;

/**
 * Holds information for representing and creating the 52 cards in a deck.
 */
public class Deck {
	private String[] cardValues = { "A", "2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K" };
	private String[] cardSuits = { "C", "D", "H", "S" };
	private ArrayList<String> cards;
	private Random random;
	private Pane deckPane;
	private Node sceneNode;
	private Bounds cardBounds;
	
	public Deck(Pane deckPane, Node sceneNode) {
		this.deckPane = deckPane;
		this.sceneNode = sceneNode;
		cards = new ArrayList<String>();
		random = new Random();
		
		Card sample = GameObject.Instantiate("Card.fxml");		
		cardBounds = sample.getNode().getLayoutBounds();
		for (String suit : cardSuits) {
			for (String value : cardValues) {
				cards.add(value + "-" + suit);
			}
		}
	}
	
	public Card getCard() {
		int cardIndex = random.nextInt(cards.size());
		String[] cardValueSuit = cards.get(cardIndex).split("-");
		Card card = new Card(cardValueSuit[0], cardValueSuit[1], cardBounds);
		card.setParentPane(deckPane);
		card.setSceneNode(sceneNode);
		card.setPosition(deckPane);
		card.setRotation(random.nextFloat(-5, 5));
		card.displayCard();
		cards.remove(cardIndex);
		return card;
	}
}
