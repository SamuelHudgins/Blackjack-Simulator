package project;

import java.util.ArrayList;

public class Hand {
	
	private final int MAX_HAND_SIZE = 11;
	private int size;
	private ArrayList<Card> cards;
	
	public Hand() {
		cards = new ArrayList<Card>();
		size = 0;
	}
	
	public void addCardToHand(Card card) {
		if (size >= MAX_HAND_SIZE) return;		
		cards.add(card);
		size++;
	}
	
	public int getSize() {
		return size;
	}
	
	public ArrayList<Card> getCards() {
		return cards;
	}
	
	public int getHandValue() {
		int value = 0;
		for (Card card : cards) {
			if (card.getFaceDown()) continue;
			if (card.getValue().equals("A")) {
				if (value + card.getFaceValue() > 21) {
					card.setFaceValue(1);
				}
			}
			value += card.getFaceValue();			
		}
		return value;
	}
}
