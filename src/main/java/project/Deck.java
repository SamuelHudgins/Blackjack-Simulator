package project;

import java.util.ArrayList;
import java.util.Random;

public class Deck {
	private String[] cardValues = {"A", "2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K"};
	private String[] cardSuits = {"C", "D", "H", "S"};
	private ArrayList<Card> availableCards;
	private ArrayList<Card> removedCards;
	private Random random;
	
	public Deck() {
		availableCards = new ArrayList<Card>();
		removedCards = new ArrayList<Card>();
		random = new Random();
		for (String suit : cardSuits) {
			for (String value : cardValues) {
				Card card = GameObject.Instantiate("Card.fxml");
				card.setValueAndSuit(value, suit);
				availableCards.add(card);
			}
		}
	}
	
	public void shuffleDeck() {
		for (int i = 0; i < availableCards.size(); i++) {
			int j = random.nextInt(availableCards.size());
			Card temp = availableCards.get(i);
			availableCards.set(i, availableCards.get(j));
			availableCards.set(j, temp);
		}
	}
	
	public Card getCard() {
		int cardIndex = random.nextInt(availableCards.size());
		Card card = availableCards.get(cardIndex);
		availableCards.remove(cardIndex);
		removedCards.add(card);
		return card;
	}
}
