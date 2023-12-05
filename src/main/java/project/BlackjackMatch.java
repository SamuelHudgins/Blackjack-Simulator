package project;

import javafx.scene.layout.Pane;
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
	private Hand[] playerSplitHands;
	private int splitHandCount;
	private Hand currentPlayerHand;
	private final int MAX_SPLIT_HANDS = 3;
	private Pane[] playerSplitCardPanes;
	private Pane currentPlayerHandPane;
	private HandDisplay playerHandDisplay;
	
	private Hand dealerHand;	
	private HandDisplay dealerHandDisplay;	
	private final int DEALER_HIT_STOP = 17;
	
	MatchResult playerMatchResult;
	MatchResult dealerMatchResult;
	
	public BlackjackMatch(BlackjackScene scene, HandDisplay playerHandDisplay, HandDisplay dealerHandDisplay) {		
		player = Player.getInstance();
		blackjackScene = scene;
		this.playerHandDisplay = playerHandDisplay;
		this.dealerHandDisplay = dealerHandDisplay;
		playerSplitHands = new Hand[MAX_SPLIT_HANDS];
	}
	
	public void start(Pane boardPane, Pane[] playerCardPanes, Pane dealerCardPane) {
		this.boardPane = boardPane;
		deck = new Deck(blackjackScene.getDeckPane(), boardPane);	
		blackjackScene.setPlayerInsuranceBetLabel(Double.toString(0));
		currentPlayerHand = GameObject.Instantiate("Hand.fxml");
		currentPlayerHand.setParentPane(this.boardPane);
		currentPlayerHand.setSceneNode(this.boardPane);
		currentPlayerHandPane = playerCardPanes[0];
		currentPlayerHand.setPosition(currentPlayerHandPane);
		currentPlayerHand.setBet(player.getBet());
		playerSplitCardPanes = new Pane[] { playerCardPanes[1], playerCardPanes[2], playerCardPanes[3] };
		dealerHand = GameObject.Instantiate("Hand.fxml");
		dealerHand.setParentPane(this.boardPane);
		dealerHand.setSceneNode(this.boardPane);
		dealerHand.setPosition(dealerCardPane);
		disperseCards();
	}
	
	/**
	 * Uses a {@code TimeLine} while dispersing cards to the player and dealer's 
	 * hands over time. The first card is given to the player, the second to the 
	 * dealer, and the third and fourth to the player and dealer again, respectively.
	 */
	private void disperseCards() {		
		// Gives the first card to the player.
		Action giveCard1 = () -> hitHand(currentPlayerHand, playerHandDisplay);
		
		// Gives the second flipped card to the dealer.
		Action giveCard2 = () -> hitHand(dealerHand, dealerHandDisplay, true);
		
		// Gives the third card to the player.
		Action giveCard3 = () -> hitHand(currentPlayerHand, playerHandDisplay);
		
		// Gives the last card to the dealer.
		Action giveCard4 = () -> hitHand(dealerHand, dealerHandDisplay);
		
		Action[] actions = new Action[] { giveCard1, giveCard2, giveCard3, giveCard4, () -> checkPlayerHand() };
		float increment = 0.35f;
		Routine.doActionList(increment, actions);
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
		Routine.onStartAndFinish(() -> hitHand(currentPlayerHand, playerHandDisplay), 
			() -> checkPlayerHand(), Animation.CARD_TRANSLATE_TIME);	
	}
	
	// Doubling
	
	/**
	 * Determines whether the player can double their bet.
	 * @return True, if:
	 * <br> a) the player's hand value is less than 21,
	 * <br> b) the player's hand has less than or equal to two cards, 
	 * <br> c) and if the player has enough money. 
	 * <p> Otherwise, false.
	 */
	private boolean canDouble() {
		if (!canHit()) return false;
		if (currentPlayerHand.getSize() > 2) return false;
		return (player.getBalance() - currentPlayerHand.getBet()) >= 0;
	}
	
	public void doublePlayerBet() {
		player.adjustBalance(-currentPlayerHand.getBet());
		currentPlayerHand.doubleMainBet();
		blackjackScene.setPlayerBankLabel(Double.toString(player.getBalance()));
		blackjackScene.setPlayerBetLabel(Double.toString(currentPlayerHand.getBet()));		
		Action[] actions = new Action[] {
			() -> hitHand(currentPlayerHand, playerHandDisplay),
			() -> finishPlayerHand()
		};
		Routine.doActionList(Animation.CARD_TRANSLATE_TIME, actions);
	}
	
	// Splitting
	/**
	 * Determines whether the player can split their hand into multiple hands.
	 * @return True, if:
	 * <br> a) the player's hand has less than or equal to two cards, 
	 * <br> b) if the player has less than three split hands, 
	 * <br> c) and if the player has enough money to perform a split. 
	 * <p> Otherwise, false.
	 */
	private boolean canSplit() {
		if (splitHandCount >= MAX_SPLIT_HANDS) return false;
		if ((player.getBalance() - currentPlayerHand.getBet()) <= 0) return false;
		return currentPlayerHand.splittable();
	}
	
	public void playerSplit() {	
		player.adjustBalance(-currentPlayerHand.getBet());		
		blackjackScene.setPlayerBankLabel(Double.toString(player.getBalance()));
		
		Routine.doAfter(() -> {
			// Splitting assumes the current player's hand has only two cards, so pull the second card 
			// from the player's hand.
			Card splitCard = currentPlayerHand.getCards().get(1);
			Hand splitHand = GameObject.Instantiate("Hand.fxml");
			splitHand.setParentPane(boardPane);
			splitHand.setSceneNode(boardPane);
			currentPlayerHand.removeCardFromHand(splitCard);
			splitHand.addCardToHand(splitCard);
			splitHand.setBet(currentPlayerHand.getBet());			
			playerSplitHands[splitHandCount] = splitHand;
			Animation.Translate(splitHand, currentPlayerHandPane, playerSplitCardPanes[splitHandCount], () -> playerHit());
			Animation.Scale(splitHand, splitHand.getCurrentScale(), splitHand.getSplitScale());
			splitHandCount++;
		}, 0.5f);
	}
	
	// Insurance
	/**
	 * Determines whether the player can accept insurance.
	 * @return True, if:
	 * <br> a) the player hasn't accepted insurance on their current hand, 
	 * <br> b) if the player's current hand has two cards, 
	 * <br> c) if the player's current hand has not busted, 
	 * <br> d) if the difference between half of the player's bet and their balance is greater than zero, 
	 * <br> e) and if the dealer's revealed card is an Ace. 
	 * <p> Otherwise, false.
	 */
	private boolean canAcceptInsurance() {
		if (currentPlayerHand.getInsuranceBet() > 0) return false;
		if (currentPlayerHand.getSize() > 2) return false;
		if (currentPlayerHand.busted()) return false;
		if (player.getBalance() - (currentPlayerHand.getBet() * 0.5f) <= 0) return false;
		if (dealerHand.getFaceUpCard().getValue().equals("A")) return true;
		return false;
	}
	
	/**
	 * Determines whether the player can accept even money.
	 * @return True, if the conditions for accepting insurance are true and the player's 
	 * hand equals 21. Otherwise, false.
	 */
	private boolean canAcceptEvenMoney() {
		return canAcceptInsurance() && currentPlayerHand.hasNaturalBlackjack();
	}
	
	public void useInsurance() {
		double insuranceBet = currentPlayerHand.getBet() * 0.5f;
		player.adjustBalance(-insuranceBet);
		currentPlayerHand.setInsuranceBet(insuranceBet);
		blackjackScene.setPlayerBankLabel(Double.toString(player.getBalance()));
		blackjackScene.setPlayerInsuranceBetLabel(Double.toString(insuranceBet));
		DatabaseManager.getInstance().insertLosses(player.getUsername(), insuranceBet);
		checkPlayerHand();
	}
	
	/**
	 * Determines which actions the player can perform and finishes their hand if they have 
	 * blackjack, bust, or cannot perform any actions (other than standing).
	 */
	private void checkPlayerHand() {
		if (currentPlayerHand.hasNaturalBlackjack()) {
			blackjackScene.showPlayerBanner(MatchResult.Blackjack);
			currentPlayerHand.setFinished(true);
		}
		else if (currentPlayerHand.busted()) {
			blackjackScene.showPlayerBanner(MatchResult.Bust);
			currentPlayerHand.setFinished(true);
		}
		boolean canHit = canHit();
		boolean canDouble = canDouble();
		boolean canSplit = canSplit();
		boolean canAcceptInsurance = canAcceptInsurance();
		boolean canAcceptEvenMoney = canAcceptEvenMoney();
		if (canHit || canDouble || canSplit || canAcceptInsurance || canAcceptEvenMoney) {
			blackjackScene.showMatchOptions(canHit, canDouble, canSplit, canAcceptInsurance, canAcceptEvenMoney);
			blackjackScene.showBestOption(currentPlayerHand, dealerHand);			
		}
		else {
			blackjackScene.hideMatchOptions();
			Routine.doAfter(() -> finishPlayerHand(), 1f);
		}
	}
	
	public void finishPlayerHand() {
		currentPlayerHand.setFinished(true);		
		
		// Look for the next unfinished split hand, switch the current player's hand with it, 
		// hit it, and update the display.
		for (int i = 0; i < splitHandCount; i++) {
			if (!playerSplitHands[i].getFinished()) {
				Hand nextSplitHand = playerSplitHands[i];
				playerSplitHands[i] = currentPlayerHand;
				Animation.Translate(nextSplitHand, playerSplitCardPanes[i], currentPlayerHandPane);
				Animation.Scale(nextSplitHand, nextSplitHand.getCurrentScale(), nextSplitHand.getDefaultScale());
				Animation.Translate(currentPlayerHand, currentPlayerHandPane, playerSplitCardPanes[i], () -> playerHit());				
				Animation.Scale(currentPlayerHand, currentPlayerHand.getCurrentScale(), currentPlayerHand.getSplitScale());
				currentPlayerHand = nextSplitHand;
				playerHandDisplay.setHandLabel(Integer.toString(currentPlayerHand.getHandValue()));
				blackjackScene.hidePlayerBanner();
				return;
			}
		}
		playDealer();	
	}
	
	private void playDealer() {
		blackjackScene.hideMatchOptions();
		revealDealerCard();
		if (splitHandCount > 0) {
			for (Hand hand : playerSplitHands) {
				if (hand == null) continue;
				if (!hand.busted()) {
					dealerHits();
					return;
				}
			}			
		}
		if (!currentPlayerHand.busted()) {
			dealerHits();
		}
		else Routine.doAfter(() -> compareHands(), 1);
	}
	
	private Card hitHand(Hand hand, HandDisplay display) {
		Card card = deck.getCard();
		hand.addCardToHand(card);	
		Action endAction = () -> display.setHandLabel(Integer.toString(hand.getHandValue()));
		Animation.Translate(card, blackjackScene.getDeckPane(), hand.getNode(), endAction);
		return card;
	}
	
	private void hitHand(Hand hand, HandDisplay display, boolean faceDown) {
		Card card = hitHand(hand, display);
		card.setFaceDown(faceDown);
	}
		
	private void revealDealerCard() {
		Action flipCard = () -> dealerHand.getCards().get(0).flipCardUp();
		Action updateDisplay = () -> dealerHandDisplay.setHandLabel(Integer.toString(dealerHand.getHandValue()));
		Routine.onStartAndFinish(flipCard, updateDisplay, Animation.CARD_TRANSLATE_TIME);	
	}
	
	/**
	 * Uses an infinitely looping routine to hit the dealer's hand until their hand's 
	 * value is 21 or higher.
	 */
	private void dealerHits() {
		// The event(s) to perform on each iteration of the routine.
		Action action = () -> hitHand(dealerHand, dealerHandDisplay);
		
		// The boolean condition that ends the routine.
		Conditional endCondition = () -> dealerHand.getHandValue() >= DEALER_HIT_STOP;
		
		// The event(s) to perform at the end of the routine.
		Action endAction = () -> compareHands();
		Routine.doRepeatOnCondition(action, 1, endCondition, endAction);
	}
	
	private void compareHands() {			
		if (dealerHand.hasNaturalBlackjack()) blackjackScene.showDealerBanner(MatchResult.Blackjack);
		else if (dealerHand.busted()) blackjackScene.showDealerBanner(MatchResult.Bust);
		
		Routine.doAfter(() -> {
			if (currentPlayerHand.busted()) {  // The player busts.
				playerMatchResult = MatchResult.Bust;
				dealerMatchResult = dealerHand.hasNaturalBlackjack() ? MatchResult.Blackjack : MatchResult.Won;
			}
			
			// The player did not bust, and their hand value is between 1-21.
			else if (dealerHand.busted()) {  // The dealer busts.
				dealerMatchResult = MatchResult.Bust;
				playerMatchResult = currentPlayerHand.hasNaturalBlackjack() ? MatchResult.Blackjack : MatchResult.Won;
			}
			
			// The player and dealer did not bust, and their hands are between 1-21.
			else {
				int playerHandValue = currentPlayerHand.getHandValue();
				int dealerHandValue = dealerHand.getHandValue();
				if (playerHandValue > dealerHandValue) {
					dealerMatchResult = MatchResult.Loss;
					playerMatchResult = currentPlayerHand.hasNaturalBlackjack() ? MatchResult.Blackjack : MatchResult.Won;
				}
				else if (playerHandValue == dealerHandValue) {
					playerMatchResult = MatchResult.Push;
					dealerMatchResult = MatchResult.Push;
				}
				else {
					playerMatchResult = MatchResult.Loss;
					dealerMatchResult = dealerHand.hasNaturalBlackjack() ? MatchResult.Blackjack : MatchResult.Won;
				}
			}		
			if (playerMatchResult == MatchResult.Push) blackjackScene.showPushBanner();	
			else blackjackScene.showMatchResults(playerMatchResult, dealerMatchResult);		
			adjustPlayerBalance(playerMatchResult);
			playOtherPlayerHands();
		}, 1.5f);		
	}
	
	private void adjustPlayerBalance(MatchResult result) {
		double payout = 0;
		double insurancePayout = currentPlayerHand.getInsuranceBet() + (currentPlayerHand.getInsuranceBet() * 2);
		if (result == MatchResult.Blackjack) payout = 2.5f * currentPlayerHand.getBet();
		else if (result == MatchResult.Won) payout = 2 * currentPlayerHand.getBet();
		else if (result == MatchResult.Push) payout = currentPlayerHand.getBet() + insurancePayout;
		else if (result == MatchResult.Loss) payout = insurancePayout;
		
		player.adjustBalance(payout);
		if (!player.getGuest()) {
			DatabaseManager DBM = DatabaseManager.getInstance();
			// When the player wins, they earn more money than they bet since they earn their money back plus extra. 
			// So, if their payout exceeds the bet, they have earned money. Otherwise, they have lost money.
			if (currentPlayerHand.getBet() < payout) DBM.insertWinnings(player.getUsername(), payout - currentPlayerHand.getBet());
			else if (currentPlayerHand.getBet() > payout) DBM.insertLosses(player.getUsername(), currentPlayerHand.getBet());
			DBM.setUserBankBalance(player.getUsername(), player.getBalance());
		}
		blackjackScene.setPlayerBankLabel(Double.toString(player.getBalance()));
	}
	
	private void playOtherPlayerHands() {
		if (splitHandCount == 0) {
			Routine.doAfter(() -> {  // Wait, then show exit options.
				blackjackScene.showEndMatchOptions(player.getBalance() <= 1);				
			}, 1);
			return;
		}
			
		// Reset match results and player hand display.
		Action hideResults = () -> {
			blackjackScene.hideMatchResults();
			playerHandDisplay.setHandLabel("");
		};
		
		// Move the current hand off the board and get the next split hand and place it in the center.
		Action moveHands = () -> {
			int index = 0;
			for (int i = 0; i < playerSplitHands.length; i++) {
				if (playerSplitHands[i] != null) {
					index = i;
					break;
				}
			}			
			Hand nextHand = playerSplitHands[index];
			Animation.Translate(nextHand, playerSplitCardPanes[index], currentPlayerHandPane);
			Animation.Scale(nextHand, nextHand.getCurrentScale(), nextHand.getDefaultScale());
			Animation.Translate(currentPlayerHand, currentPlayerHandPane, blackjackScene.getRemovedCardPane());	
			currentPlayerHand = nextHand;
			playerSplitHands[index] = null;
			splitHandCount--;
		};
		
		// Update the player hand display.
		Action updateDislay = () -> {
			playerHandDisplay.setHandLabel(Integer.toString(currentPlayerHand.getHandValue()));
		};
		
		Action[] actions = new Action[] { hideResults, moveHands, updateDislay, () -> compareHands() };
		float increment = 0.3f;
		Routine.doAfter(() -> Routine.doActionList(increment, actions), 1);
	}
}
