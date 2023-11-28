package project;

import java.util.ArrayList;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.layout.Pane;
import javafx.util.Duration;
import project.Routine.Action;
import project.Routine.Conditional;

/**
 * Contains the processes required for running the blackjack match. This class 
 * communicates with the {@code BlackjackScene} controller to determine what and 
 * when GUI elements should be available during the match.
 */
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
	
	/**
	 * Uses a {@code TimeLine} while dispersing cards to the player and dealer's 
	 * hands over time. The first card is given to the player, the second to the 
	 * dealer, and the third and fourth to the player and dealer again, respectively.
	 */
	private void disperseCards() {
		Timeline timeline = new Timeline();
		int startTime = 500;
		int increment = 300;
		int duration = increment;
		
		// Gives the first card to the player.
		timeline.getKeyFrames().add(new KeyFrame(Duration.millis(duration), event -> {
			hitHand(currentPlayerHand);
			playerHandDisplay.setHandLabel(Integer.toString(currentPlayerHand.getHandValue()));
		}));
		duration += increment;
		
		// Gives the second flipped card to the dealer.
		timeline.getKeyFrames().add(new KeyFrame(Duration.millis(duration), event -> {
			hitHand(dealerHand, true);
			dealerHandDisplay.setHandLabel(Integer.toString(dealerHand.getHandValue()));
		}));
		duration += increment;
		
		// Gives the third card to the player.
		timeline.getKeyFrames().add(new KeyFrame(Duration.millis(duration), event -> {
			hitHand(currentPlayerHand);
			playerHandDisplay.setHandLabel(Integer.toString(currentPlayerHand.getHandValue()));
		}));
		duration += increment;
		
		// Gives the last card to the dealer.
		timeline.getKeyFrames().add(new KeyFrame(Duration.millis(duration), event -> {
			hitHand(dealerHand);
			dealerHandDisplay.setHandLabel(Integer.toString(dealerHand.getHandValue()));
		}));
		duration += increment;
		timeline.getKeyFrames().add(new KeyFrame(Duration.millis(duration), event -> {
			checkPlayerHand(); 
		}));
		timeline.setDelay(Duration.millis(startTime));
		timeline.play();
	}
	
	// Hitting
	/**
	 * Determines whether the player can perform a hit.
	 * @return True, if the player's hand value is less than 21. Otherwise, false.
	 */
	private boolean canHit() {
		return currentPlayerHand.getHandValue() < 21;
	}
	
	public void playerHit() {
		hitHand(currentPlayerHand);
		playerHandDisplay.setHandLabel(Integer.toString(currentPlayerHand.getHandValue()));
		checkPlayerHand();
	}
	
	// Doubling
	
	/**
	 * Determines whether the player can double their bet.
	 * @return True, if the player's hand has less than or equal to two cards 
	 * and if the player has enough money. Otherwise, false.
	 */
	private boolean canDouble() {
		if (currentPlayerHand.getSize() > 2) return false;
		return (player.getBalance() - playerBet) >= 0;
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
	/**
	 * Determines whether the player can split their hand into multiple hands.
	 * @return True, if the player's hand has less than or equal to two cards, if 
	 * they have less than three split hands, and if the player has enough money 
	 * to perform a split. Otherwise, false.
	 */
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
			// Splitting assumes the current player's hand has only two cards, so 
			// pull the card from the hand at index 0 or 1.
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
	/**
	 * Determines whether the player can accept insurance.
	 * @return True, if the difference between half of the player's bet and their balance is 
	 * greater than zero and if the dealer's revealed card is an Ace. Otherwise, false.
	 */
	private boolean canAcceptInsurance() {
		if (player.getBalance() - (playerBet * 0.5f) <= 0) return false;
		for (Card card : dealerHand.getCards()) {
			if (!card.getFaceDown() && card.getValue().equals("A")) return true;
		}
		return false;
	}
	
	/**
	 * Determines whether the player can accept even money.
	 * @return True, if the conditions for accepting insurance are true and the player's 
	 * hand equals 21. Otherwise, false.
	 */
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
	
	/**
	 * Determines which actions the player can perform or finishes their hand if 
	 * their hand's value is 21 or higher.
	 */
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
	
	/**
	 * Uses an infinitely looping routine to hit the dealer's hand until their hand's 
	 * value is 21 or higher.
	 */
	private void dealerHits() {
		// The event(s) to perform on each iteration of the routine.
		Action action = () -> {
			hitHand(dealerHand);
			dealerHandDisplay.setHandLabel(Integer.toString(dealerHand.getHandValue()));
		};
		// The boolean condition that ends the routine.
		Conditional endCondition = () -> dealerHand.getHandValue() >= DEALER_HIT_STOP;
		
		// The event(s) to perform at the end of the routine.
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
			// So, if their payout exceeds the bet, they have earned money. Otherwise, they have lost money.
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
