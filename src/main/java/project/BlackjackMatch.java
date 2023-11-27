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
	private double playerBet;
	private double insuranceBet;
	
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
			playerHandDisplay.setHandLabel(Integer.toString(currentPlayerHand.getHandValue()));
		}));
		duration += increment;
		timeline.getKeyFrames().add(new KeyFrame(Duration.millis(duration), event -> {
			hitHand(dealerHand, true);
			dealerHandDisplay.setHandLabel(Integer.toString(dealerHand.getHandValue()));
		}));
		duration += increment;
		timeline.getKeyFrames().add(new KeyFrame(Duration.millis(duration), event -> {
			hitHand(currentPlayerHand);
			playerHandDisplay.setHandLabel(Integer.toString(currentPlayerHand.getHandValue()));
		}));
		duration += increment;
		timeline.getKeyFrames().add(new KeyFrame(Duration.millis(duration), event -> {
			hitHand(dealerHand);
			dealerHandDisplay.setHandLabel(Integer.toString(dealerHand.getHandValue()));
		}));
		duration += increment;
		timeline.getKeyFrames().add(new KeyFrame(Duration.millis(duration), event -> {
//			blackjackScene.showMatchOptions(canHit(), canDouble(), canSplit(), canAcceptInsurance(), canAcceptEvenMoney());
			checkPlayerHand(); 
		}));
		timeline.setDelay(Duration.millis(startTime));
		timeline.play();
	}
	
	// Hitting
	private boolean canHit() {
		return currentPlayerHand.getHandValue() < 21;
	}
	
	public void playerHit() {
		hitHand(currentPlayerHand);
		playerHandDisplay.setHandLabel(Integer.toString(currentPlayerHand.getHandValue()));
		checkPlayerHand();
	}
	
	// Doubling
	private boolean canDouble() {
		if (currentPlayerHand.getSize() > 2) return false;
		return (player.getBalance() - (playerBet)) >= 0;
	}
	
	public void doublePlayerBet() {
		player.adjustBalance(-playerBet);
		currentPlayerHand.setBet(playerBet * 2);
		blackjackScene.setPlayerBankLabel(Double.toString(player.getBalance()));
		blackjackScene.setPlayerBetLabel(Double.toString(playerBet));
		Routine.doAfter(() -> {
			hitHand(currentPlayerHand);
			playerHandDisplay.setHandLabel(Integer.toString(currentPlayerHand.getHandValue()));
			finishPlayerHand();
		}, 1000);
	}
	
	// Standing
	public void playerStand() {
		finishPlayerHand();
	}
	
	// Splitting
	private boolean canSplit() {
		if (currentPlayerHand.getCards().size() >= 3) return false;
		if (playerSplitHands.size() >= MAX_SPLIT_HANDS) return false;
		if ((player.getBalance() - playerBet) <= 0) return false;
		int cardValue = 0;
		for (Card card : currentPlayerHand.getCards()) {
			if (cardValue == 0) cardValue = card.getFaceValue();
			else if (cardValue == card.getFaceValue()) return true;		
		}
		return false;
	}
	
	public void playerSplit() {
		player.adjustBalance(-playerBet);		
		blackjackScene.setPlayerBankLabel(Double.toString(player.getBalance()));
		blackjackScene.setPlayerBetLabel(Double.toString(playerBet));
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
			playerHandDisplay.setHandLabel(Integer.toString(currentPlayerHand.getHandValue()));
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
		insuranceBet = playerBet * 0.5f;
		player.adjustBalance(-insuranceBet);
		blackjackScene.setPlayerBankLabel(Double.toString(player.getBalance()));
		blackjackScene.setPlayerBetLabel(Double.toString(playerBet));
		
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
				DatabaseManager.getInstance().insertLosses(player.getUsername(), insuranceBet);
				blackjackScene.showInusuranceLost();
				insuranceBet = 0;
			}
		}, 1000);
	}
	
	private void checkPlayerHand() {
		if (currentPlayerHand.getHandValue() < 21) {
			blackjackScene.showMatchOptions(canHit(), canDouble(), canSplit(), canAcceptInsurance(), canAcceptEvenMoney());
			return;
		}
		finishPlayerHand();
		if (currentPlayerHand.hasNaturalBlackjack()) playerHandDisplay.setStatusLabel("Blackjack!");
		else if (currentPlayerHand.busted()) playerHandDisplay.setStatusLabel("Bust");
	}
	
	private void finishPlayerHand() {
		currentPlayerHand.setFinished(true);		
		
		// Look for the next unfinished split hand, switch the current player's hand with it, 
		// hit it, and update the display.
		for (int i = 0; i < playerSplitHands.size(); i++) {
			if (!playerSplitHands.get(i).getFinished()) {
				Hand nextSplitHand = playerSplitHands.get(i);
				playerSplitHands.remove(i);
				currentPlayerHand.setPosition(playerSplitCardPanes[i]);
				nextSplitHand.setPosition(currentPlayerHandPane);
				playerSplitHands.add(i, currentPlayerHand);
				currentPlayerHand.setSplitScale();
				currentPlayerHand = nextSplitHand;
				playerHandDisplay.setHandLabel(Integer.toString(currentPlayerHand.getHandValue()));
				currentPlayerHand.resetScale();
				Routine.doAfter(() -> {
					hitHand(currentPlayerHand);
					playerHandDisplay.setHandLabel(Integer.toString(currentPlayerHand.getHandValue()));
					checkPlayerHand();
				}, 500);				
				return;
			}
		}
		playDealer();	
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
	
	private void displayHandStatus(Hand hand, HandDisplay handDisplay) {
		if (hand.hasNaturalBlackjack()) handDisplay.setStatusLabel("Blackjack!");
		else if (hand.busted()) handDisplay.setStatusLabel("Bust");
	}
	
	private void revealDealerCard() {
		dealerHand.getCards().get(0).setFaceDown(false);
		dealerHandDisplay.setHandLabel(Integer.toString(dealerHand.getHandValue()));
	}
	
	private void dealerHits() {
		Action action = () -> {
			hitHand(dealerHand);
			dealerHandDisplay.setHandLabel(Integer.toString(dealerHand.getHandValue()));
		};
		Conditional endCondition = () -> dealerHand.getHandValue() >= DEALER_HIT_STOP;
		Action endAction = () -> compareHands();
		Routine.doRepeatOnCondition(action, 1000, endCondition, endAction);
	}
	
	private void compareHands() {	
		int playerHandValue = currentPlayerHand.getHandValue();
		int dealerHandValue = dealerHand.getHandValue();
		displayHandStatus(currentPlayerHand, playerHandDisplay);
		displayHandStatus(dealerHand, dealerHandDisplay);
		
		Routine.doAfter(() -> {
			if (playerHandValue > 21) {  // The player busts.
				matchResult = MatchResult.Loss;
			}
			
			// The player did not bust, and their hand value is between 1-21.
			else if (dealerHandValue > 21) {  // The dealer busts.
				if (playerHandValue == 21 && currentPlayerHand.getSize() == 2) matchResult = MatchResult.Blackjack;
				else matchResult = MatchResult.Won;
			}
			
			// The player and dealer did not bust, and their hands are between 1-21.
			else {
				if (playerHandValue > dealerHandValue) {
					if (playerHandValue == 21 && currentPlayerHand.getSize() == 2) matchResult = MatchResult.Blackjack;
					else matchResult = MatchResult.Won;
				}
				else if (playerHandValue == dealerHandValue) matchResult = MatchResult.Push;
				else matchResult = MatchResult.Loss;
			}		
			
			blackjackScene.setMatchResultText(matchResult.toString());		
			adjustPlayerBalance(matchResult);
			playOtherPlayerHands();
		}, 1500);
		
	}
	
	private void adjustPlayerBalance(MatchResult result) {
		double payout = 0;
		double insurancePayout = insuranceBet + (insuranceBet * 2);
		if (result == MatchResult.Blackjack) payout = (int) (2.5f * currentPlayerHand.getBet());
		else if (result == MatchResult.Won) payout = 2 * currentPlayerHand.getBet();
		else if (result == MatchResult.Push) payout = currentPlayerHand.getBet() + insurancePayout;
		else if (result == MatchResult.Loss) payout = insurancePayout;
		
		player.adjustBalance(payout);
		if (!player.getGuest()) {
			DatabaseManager DBM = DatabaseManager.getInstance();
			// When the player wins, they earn more money than they bet since they earn their money back plus extra.
			// So, if their payout is higher than the bet, then they earned money. Otherwise, they loss money.
			if (playerBet < payout) DBM.insertWinnings(player.getUsername(), payout - currentPlayerHand.getBet());
			else if (playerBet > payout) DBM.insertLosses(player.getUsername(), currentPlayerHand.getBet());
			DBM.setUserBankBalance(player.getUsername(), player.getBalance());
		}
		blackjackScene.setPlayerBankLabel(Double.toString(player.getBalance()));
	}
	
	private void playOtherPlayerHands() {
		playerHandDisplay.setStatusLabel("");
		if (playerSplitHands.size() == 0) {
			dealerHandDisplay.setStatusLabel("");
			blackjackScene.showEndMatchOptions(player.getBalance() <= 0);
			return;
		}
		
		Timeline timeline = new Timeline();
		int startTime = 500;
		int increment = 300;
		int duration = increment;
		
		// Move the current hand off the board
		timeline.getKeyFrames().add(new KeyFrame(Duration.millis(duration), event -> {
			blackjackScene.setMatchResultText("");
			playerHandDisplay.setHandLabel("");
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
			playerHandDisplay.setHandLabel(Integer.toString(currentPlayerHand.getHandValue()));
		}));
		duration += increment;
		
		// Compare the hands
		timeline.getKeyFrames().add(new KeyFrame(Duration.millis(duration), event -> {
			compareHands();			
		}));
		
		timeline.setDelay(Duration.millis(startTime));
		timeline.play();
	}
}
