package project;

import java.util.ArrayList;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.layout.Pane;
import javafx.util.Duration;
import project.Routine.Action;
import project.Routine.Conditional;

public class BlackjackMatch {

	private BlackjackScene blackjackScene;
	private Pane boardPane;
	private Deck deck;
	
	private Player player;
	private int playerBet;
	private int insuranceBet;
	
	private ArrayList<Hand> playerSplitHands;
	private Hand currentPlayerHand;
	private final int MAX_SPLIT_HANDS = 3;
	private Pane[] playerSplitCardPanes;
	private Pane currentPlayerHandPane;
	private HandDisplay playerHandDisplay;
	
	private Hand dealerHand;	
	private HandDisplay dealerHandDisplay;	
	private final int DEALER_HIT_STOP = 17;
	
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
		playerSplitHands = new ArrayList<Hand>(MAX_SPLIT_HANDS);
	}
	
	public void start(Pane boardPane, Pane[] playerCardPanes, Pane dealerCardPane) {
		this.boardPane = boardPane;
		deck = new Deck();		
		currentPlayerHand = GameObject.Instantiate("Hand.fxml");
		currentPlayerHand.setParentPane(this.boardPane);
		currentPlayerHandPane = playerCardPanes[0];
		currentPlayerHand.setPosition(currentPlayerHandPane);
		currentPlayerHand.setBet(playerBet);
		playerSplitCardPanes = new Pane[] { playerCardPanes[1], playerCardPanes[2], playerCardPanes[3] };
		dealerHand = GameObject.Instantiate("Hand.fxml");
		dealerHand.setParentPane(this.boardPane);
		dealerHand.setPosition(dealerCardPane);		
		disperseCards();
	}
	
	private void disperseCards() {
		Timeline timeline = new Timeline();
		int startTime = 500;
		int increment = 300;
		int duration = increment;
		timeline.getKeyFrames().add(new KeyFrame(Duration.millis(duration), event -> {
			hitHand(currentPlayerHand);
			updateHandDisplay(playerHandDisplay, currentPlayerHand);
		}));
		duration += increment;
		timeline.getKeyFrames().add(new KeyFrame(Duration.millis(duration), event -> {
			hitHand(dealerHand, true);
			updateHandDisplay(dealerHandDisplay, dealerHand);
		}));
		duration += increment;
		timeline.getKeyFrames().add(new KeyFrame(Duration.millis(duration), event -> {
			hitHand(currentPlayerHand);
			updateHandDisplay(playerHandDisplay, currentPlayerHand);
		}));
		duration += increment;
		timeline.getKeyFrames().add(new KeyFrame(Duration.millis(duration), event -> {
			hitHand(dealerHand);
			updateHandDisplay(dealerHandDisplay, dealerHand);
		}));
		duration += increment;
		timeline.getKeyFrames().add(new KeyFrame(Duration.millis(duration), event -> {
			boolean canDouble = (player.getBalance() - (playerBet)) >= 0;
			blackjackScene.showMatchOptions(canDouble, canSplit(), canAcceptInsurance(), canAcceptEvenMoney());
			checkPlayerHand(); 
		}));
		timeline.setDelay(Duration.millis(startTime));
		timeline.play();
	}
	
	public void playerHit() {
		hitHand(currentPlayerHand);
		updateHandDisplay(playerHandDisplay, currentPlayerHand);
		checkPlayerHand();
	}
	
	public void doublePlayerBet() {
		player.adjustBalance(-playerBet);
		currentPlayerHand.setBet(playerBet*2);
		blackjackScene.setPlayerBankLabel(Integer.toString(player.getBalance()));
		blackjackScene.setPlayerBetLabel(Integer.toString(playerBet));
		Routine.doAfter(() -> {
			hitHand(currentPlayerHand);
			updateHandDisplay(playerHandDisplay, currentPlayerHand);
			finishPlayerHand();
		}, 1000);
	}
	
	public void playerStand() {
		finishPlayerHand();
	}
	
	// Splitting
	private boolean canSplit() {
		if (playerSplitHands.size() >= MAX_SPLIT_HANDS) return false;
		if ((player.getBalance() - playerBet) <= 0) return false;
		int cardValue = 0;
		for (Card card : currentPlayerHand.getCards()) {
			if (cardValue == 0) {
				cardValue = card.getFaceValue();
			}
			else if (cardValue == card.getFaceValue()) return true;		
		}
		return false;
	}
	
	public void playerSplit() {
		player.adjustBalance(-playerBet);		
		blackjackScene.setPlayerBankLabel(Integer.toString(player.getBalance()));
		blackjackScene.setPlayerBetLabel(Integer.toString(playerBet));
		Routine.doAfter(() -> {
			// Splitting assumes the current player's hand has only two cards, so pull a card from index 0 or 1.
			Card splitCard = currentPlayerHand.getCards().get(1);
			Hand splitHand = GameObject.Instantiate("Hand.fxml");
			splitHand.setParentPane(boardPane);
			currentPlayerHand.removeCardFromHand(splitCard);
			splitHand.addCardToHand(splitCard);
			splitHand.setBet(playerBet);
			splitHand.setSplitScale();
			
			playerSplitHands.add(splitHand);
			int index = playerSplitHands.size() - 1;
			playerSplitHands.get(index).setPosition(playerSplitCardPanes[index]);
			hitHand(currentPlayerHand);
			blackjackScene.showSplitButton(canSplit());
		}, 1000);
	}
	
	// Insurance
	private boolean canAcceptInsurance() {
		if (player.getBalance() - (playerBet * 0.5f) <= 0) return false;
		for (Card card : dealerHand.getCards()) {
			if (!card.getFaceDown() && card.getValue().equals("A")) return true;
		}
		return false;
	}
	
	private boolean canAcceptEvenMoney() {
		return canAcceptInsurance() && currentPlayerHand.getHandValue() == 21;
	}
	
	// Using insurance ends the game if the dealer has a blackjack
	public void useInsurance() {
		insuranceBet = (int) Math.floor(playerBet * 0.5f);
		player.adjustBalance(-insuranceBet);
		blackjackScene.setPlayerBankLabel(Integer.toString(player.getBalance()));
		blackjackScene.setPlayerBetLabel(Integer.toString(playerBet));
		
		Routine.doAfter(() -> {
			boolean dealerHasBlackjack = false;
			for (Card card : dealerHand.getCards()) {
				if (card.getFaceDown() && card.getFaceValue() == 10) {
					dealerHasBlackjack = true;
					revealDealerCard();
					compareHands();
					break;
				}
			}
			if (!dealerHasBlackjack) {
				blackjackScene.showInusuranceLost();
				insuranceBet = 0;
			}
		}, 1000);
	}
	
	private void checkPlayerHand() {
		int handValue = currentPlayerHand.getHandValue();
		if (handValue <= 21) return;
		else finishPlayerHand();
	}
	
	private void finishPlayerHand() {
		currentPlayerHand.setFinished(true);
		int unfinishedHandIndex = -1;
		for (int i = 0; i < playerSplitHands.size(); i++) {
			if (!playerSplitHands.get(i).getFinished()) {
				unfinishedHandIndex = i;
				break;
			}
		}

		if (unfinishedHandIndex != -1) {
			Hand nextSplitHand = playerSplitHands.get(unfinishedHandIndex);
			playerSplitHands.remove(unfinishedHandIndex);
			currentPlayerHand.setPosition(playerSplitCardPanes[unfinishedHandIndex]);
			nextSplitHand.setPosition(currentPlayerHandPane);
			playerSplitHands.add(unfinishedHandIndex, currentPlayerHand);
			currentPlayerHand.setSplitScale();
			currentPlayerHand = nextSplitHand;
			currentPlayerHand.resetScale();
			hitHand(currentPlayerHand);
			updateHandDisplay(playerHandDisplay, currentPlayerHand);
		}
		else playDealer();	
	}
	
	private void playDealer() {
		blackjackScene.hideMatchOptions();
		revealDealerCard();
		if (currentPlayerHand.getHandValue() <= 21) dealerHits();
		else Routine.doAfter(() -> compareHands(), 1000);
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
		handDisplay.setHandLabel(Integer.toString(hand.getHandValue()));
	}
	
	private void displayHandStatus(HandDisplay handDisplay, int handValue) {
		if (handValue == 21) handDisplay.setStatusLabel("Blackjack!");
		else if (handValue > 21) handDisplay.setStatusLabel("Bust");
	}
	
	private void revealDealerCard() {
		dealerHand.getCards().get(0).setFaceDown(false);
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
		Action endAction = () -> compareHands();
		Routine.doRepeatOnCondition(action, 1000, endCondition, endAction);
	}
	
	private void compareHands() {		
		int playerHandValue = currentPlayerHand.getHandValue();
		int dealerHandValue = dealerHand.getHandValue();
		displayHandStatus(playerHandDisplay, playerHandValue);
		displayHandStatus(dealerHandDisplay, dealerHandValue);
		
		Routine.doAfter(() -> {
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
			
			blackjackScene.setMatchResultText(matchResult.toString());		
			adjustPlayerBalance(matchResult);
			playOtherPlayerHands();
		}, 500);
		
	}
	
	private void adjustPlayerBalance(MatchResult result) {
		int insurancePayout = insuranceBet + (insuranceBet * 2);
		if (result == MatchResult.Blackjack) player.adjustBalance((int) (2.5f * currentPlayerHand.getBet()));
		else if (result == MatchResult.Won) player.adjustBalance(2 * currentPlayerHand.getBet());
		else if (result == MatchResult.Push) player.adjustBalance(playerBet + insurancePayout);
		else if (result == MatchResult.Loss) player.adjustBalance(insurancePayout);
		
		blackjackScene.setPlayerBankLabel(Integer.toString(player.getBalance()));
	}
	
	private void playOtherPlayerHands() {
		if (playerSplitHands.size() == 0) {
			boolean balanceDepleted = player.getBalance() <= 0;
			blackjackScene.showEndMatchOptions(balanceDepleted);		
			if (balanceDepleted) player.adjustBalance(1000);
			return;
		}
		Timeline timeline = new Timeline();
		int startTime = 500;
		int increment = 300;
		int duration = increment;
		
		// Move the current hand off the board
		timeline.getKeyFrames().add(new KeyFrame(Duration.millis(duration), event -> {
			blackjackScene.setMatchResultText("");
			currentPlayerHand.setPosition(blackjackScene.getRemovedCardPane());
		}));
		duration += increment;
		
		// Get the next split hand and place it in the center
		timeline.getKeyFrames().add(new KeyFrame(Duration.millis(duration), event -> {
			currentPlayerHand = playerSplitHands.get(0);
			playerSplitHands.remove(0);
			currentPlayerHand.setPosition(currentPlayerHandPane);
			currentPlayerHand.resetScale();
		}));
		duration += increment;
		
		// Update the hand display
		timeline.getKeyFrames().add(new KeyFrame(Duration.millis(duration), event -> {
			updateHandDisplay(playerHandDisplay, currentPlayerHand);
		}));
		duration += increment;
		
		// Compare the hands
		timeline.getKeyFrames().add(new KeyFrame(Duration.millis(duration), event -> {
			compareHands();			
		}));
		duration += increment;
		
		timeline.setDelay(Duration.millis(startTime));
		timeline.play();
	}
}
