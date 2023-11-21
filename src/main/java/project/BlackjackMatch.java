package project;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.layout.Pane;
import javafx.util.Duration;
import project.Routine.Action;
import project.Routine.Conditional;

public class BlackjackMatch {

	private BlackjackScene blackjackScene;
	private Deck deck;
	
	private Player player;
	private int playerBet;
	
	private Hand playerHand;	
	private HandDisplay playerHandDisplay;
	
	private Hand dealerHand;	
	private HandDisplay dealerHandDisplay;	
	private final int DEALER_HIT_STOP = 17;
	private boolean matchEnded;
	
	private enum MatchResult {
		Push("Push"), Loss("Dealer Wins!"), Won("You Win!"), Blackjack("You Win!");
		private final String stringValue;

		MatchResult(final String s) {
			stringValue = s;
		}

		public String toString() {
			return stringValue;
		}
	}
	MatchResult matchResult;
	
	public BlackjackMatch(BlackjackScene scene, HandDisplay playerHandDisplay, HandDisplay dealerHandDisplay) {		
		player = Player.getInstance();
		playerBet = player.getBet();
		blackjackScene = scene;
		this.playerHandDisplay = playerHandDisplay;
		this.dealerHandDisplay = dealerHandDisplay;
	}
	
	public void start(Pane boardPane, Pane playerCardPane, Pane dealerCardPane) {
		deck = new Deck();		
		playerHand = GameObject.Instantiate("Hand.fxml");
		playerHand.setParentPane(boardPane);
		playerHand.setPosition(playerCardPane);
		dealerHand = GameObject.Instantiate("Hand.fxml");
		dealerHand.setParentPane(boardPane);
		dealerHand.setPosition(dealerCardPane);		
		disperseCards();
	}
	
	private void disperseCards() {
		Timeline timeline = new Timeline();
		int startTime = 500;
		int increment = 300;
		int duration = increment;
		timeline.getKeyFrames().add(new KeyFrame(Duration.millis(duration), event -> {
			hitHand(playerHand);
			updateHandDisplay(playerHandDisplay, playerHand);
		}));
		duration += increment;
		timeline.getKeyFrames().add(new KeyFrame(Duration.millis(duration), event -> {
			hitHand(dealerHand, true);
			updateHandDisplay(dealerHandDisplay, dealerHand);
		}));
		duration += increment;
		timeline.getKeyFrames().add(new KeyFrame(Duration.millis(duration), event -> {
			hitHand(playerHand);
			updateHandDisplay(playerHandDisplay, playerHand);
		}));
		duration += increment;
		timeline.getKeyFrames().add(new KeyFrame(Duration.millis(duration), event -> {
			hitHand(dealerHand);
			updateHandDisplay(dealerHandDisplay, dealerHand);
		}));
		duration += increment;
		timeline.getKeyFrames().add(new KeyFrame(Duration.millis(duration), event -> {
			boolean canDouble = (player.getBalance() - (playerBet)) >= 0;
			blackjackScene.showMatchOptions(canDouble);
			checkPlayerHand(); 
		}));
		timeline.setDelay(Duration.millis(startTime));
		timeline.play();
	}
	
	public void playerHit() {
		hitHand(playerHand);
		updateHandDisplay(playerHandDisplay, playerHand);
		checkPlayerHand();
	}
	
	public void doublePlayerBet() {
		player.adjustBalance(-playerBet);
		playerBet *= 2;
		player.setBet(playerBet);
		blackjackScene.setPlayerBankLabel(Integer.toString(player.getBalance()));
		blackjackScene.setPlayerBetLabel(Integer.toString(playerBet));
		Routine.doAfter(() -> {
			hitHand(playerHand);
			updateHandDisplay(playerHandDisplay, playerHand);
			checkPlayerHand();
			endPlayerTurn(true);
		}, 1000);
	}
	
	public void playerStand() {
		endPlayerTurn(true);
	}
	
	private void checkPlayerHand() {
		int handValue = playerHand.getHandValue();
		if (handValue < 21) return;
		endPlayerTurn(handValue == 21);
	}
	
	private void endPlayerTurn(boolean dealerHits) {
		blackjackScene.hideMatchOptions();
		revealDealerCard();
		if (dealerHits) dealerHits();
		else Routine.doAfter(() -> endMatch(), 1000);
	}
	
	private Card hitHand(Hand hand) {
		Card card = deck.getCard();
		hand.addCardToHand(card);
		return card;
	}
	
	private void hitHand(Hand hand, boolean faceDown) {
		Card card = hitHand(hand);
		card.setFaceDown(faceDown);
	}
	
	private void updateHandDisplay(HandDisplay handDisplay, Hand hand) {
		int handValue = hand.getHandValue();
		handDisplay.setHandLabel(Integer.toString(handValue));		
		if (handValue == 21) handDisplay.setStatusLabel("Blackjack!");
		else if (handValue > 21) handDisplay.setStatusLabel("Bust");
	}
	
	private void revealDealerCard() {
		dealerHand.getCards()[0].setFaceDown(false);
		updateHandDisplay(dealerHandDisplay, dealerHand);
	}
	
	private void dealerHits() {
		Action action = () -> {
			hitHand(dealerHand);
			updateHandDisplay(dealerHandDisplay, dealerHand);
		};
		Conditional endCondition = () -> {
			return dealerHand.getHandValue() >= DEALER_HIT_STOP;
		};
		Action endAction = () -> endMatch();
		Routine.doRepeatOnCondition(action, 1000, endCondition, endAction);
	}
	
	private void endMatch() {
		if (matchEnded) return;
		matchEnded = true;
		
		int playerHandValue = playerHand.getHandValue();
		int dealerHandValue = dealerHand.getHandValue();
		
		if (playerHandValue > 21) {  // The player busts.
			matchResult = MatchResult.Loss;
		}
		
		// The player did not bust, and their hand value is between 1-21.
		else if (dealerHandValue > 21) {  // The dealer busts.
			if (playerHandValue == 21) matchResult = MatchResult.Blackjack;
			else matchResult = MatchResult.Won;
		}
		
		// The player and dealer did not bust, and their hands are between 1-21.
		else {
			if (playerHandValue > dealerHandValue) {
				if (playerHandValue == 21) matchResult = MatchResult.Blackjack;
				else matchResult = MatchResult.Won;
			}
			else if (playerHandValue == dealerHandValue) matchResult = MatchResult.Push;
			else matchResult = MatchResult.Loss;
		}		
		blackjackScene.showEndMatchResult(matchResult.toString());		
		adjustPlayerBalance(matchResult);
	}
	
	private void adjustPlayerBalance(MatchResult result) {
		if (result == MatchResult.Blackjack) player.adjustBalance((int) (2.5f * playerBet));
		else if (result == MatchResult.Won) player.adjustBalance(2 * playerBet);
		else if (result == MatchResult.Push) player.adjustBalance(playerBet);
		
		boolean balanceDepleted = player.getBalance() <= 0;
		blackjackScene.showEndMatchOptions(balanceDepleted);		
		if (balanceDepleted) player.adjustBalance(1000);
	}
}
