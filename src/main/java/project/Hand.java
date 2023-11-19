package project;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;

public class Hand {
	
	private GridPane cardPane;
	private Pane parent;
	private final int MAX_HAND_SIZE = 11;
	private int size;
	private Card[] cards;
		
	public Hand() {
		System.out.println(0);
		System.out.println(cardPane);
		cardPane.getChildren().forEach(image -> ((ImageView)image).setImage(null));
	}
	
	public Hand(Pane pane) {
		size = 0;
		cards = new Card[MAX_HAND_SIZE];
		try {
			cardPane = new FXMLLoader(getClass().getResource("Hand.fxml")).load();
			cardPane.getChildren().forEach(image -> {
				if (image instanceof ImageView) {
					 ((ImageView) image).setImage(null);
				}
			});
		} catch (IOException e) {
			e.printStackTrace();
		}
		parent = pane;
		parent.getChildren().add(cardPane);
	}
	
	@FXML
	private void initialize() {
		System.out.println(1);
		System.out.println(cardPane);
		cardPane.getChildren().forEach(image -> ((ImageView)image).setImage(null));
	}
	
	public void addCardToHand(Card card) {
		if (size >= MAX_HAND_SIZE) return;	
		cards[size] = card;
		setCardImage(size, card.getImage());
		size++;
	}
	
	private void setCardImage(int index, Image image) {
		((ImageView) cardPane.getChildren().get(index)).setImage(image);
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
}
