package project;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.util.Duration;
import project.Routine.Action;
import project.Routine.Conditional;

public class BlackjackScene extends SceneController {

	@FXML private Pane boardPane;
	
	@FXML private Button placeBetButton;	
	@FXML private Button mainMenuButton;
	@FXML private HBox matchHBox;	
	@FXML private HBox endMatchHBox;
	
	private Deck deck;
	
	private Player player;
	private int playerBet;
	@FXML private Label playerBankLabel;	
	@FXML private Label playerBetLabel;	
	
	@FXML private Button hitButton;	
	@FXML private Button doubleBetButton;	
	@FXML private Button standButton;	
	
	private Hand playerHand;	
	private HandDisplay playerHandDisplay;	
	@FXML private Pane playerCardPane;
	@FXML private Label playerHandLabel;
	@FXML private Label playerStatusLabel;
	
	private Hand dealerHand;	
	private HandDisplay dealerHandDisplay;	
	@FXML private Pane dealerCardPane;
	@FXML private Label dealerHandLabel;
	@FXML private Label dealerStatusLabel;
	private final int DEALER_HIT_STOP = 17;
	
	@FXML private Label matchResultsLabel;
	
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
	
	@FXML
	protected BettingScene switchToBettingScene() {
		super.switchToBettingScene();
		return null;
	}
	
	@FXML
	protected MainMenuScene switchToMainMenuScene() {
		super.switchToMainMenuScene();
		return null;
	}
	
	@FXML
	private void initialize() {
		placeBetButton.setVisible(false);
		mainMenuButton.setVisible(false);
		matchHBox.setVisible(true);
		endMatchHBox.setVisible(false);
		matchResultsLabel.setText("");
		playerHandLabel.setText("");
		playerStatusLabel.setText("");
		dealerHandLabel.setText("");
		dealerStatusLabel.setText("");
		hitButton.setVisible(false);
		doubleBetButton.setVisible(false);
		standButton.setVisible(false);
	}
	
	public void setup() {		
		deck = new Deck();		
		playerHandDisplay = new HandDisplay(playerHandLabel, playerStatusLabel);
		dealerHandDisplay = new HandDisplay(dealerHandLabel, dealerStatusLabel);
		playerHand = GameObject.Instantiate("Hand.fxml");
		playerHand.setParentPane(boardPane);
		playerHand.setPosition(playerCardPane);
		dealerHand = GameObject.Instantiate("Hand.fxml");
		dealerHand.setParentPane(boardPane);
		dealerHand.setPosition(dealerCardPane);
		disperseCards();
	}
	
	public void setPlayer(Player player, int betAmount) {
		this.player = player;
		playerBet = betAmount;
		playerBankLabel.setText("Bank: $" + (this.player.getBalance() - betAmount));
		playerBetLabel.setText("Bet: $" + Integer.toString(playerBet));
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
			hitButton.setVisible(true);
			boolean canDouble = (player.getBalance() - (playerBet * 2)) >= 0;
			doubleBetButton.setVisible(canDouble);
			standButton.setVisible(true);
			checkPlayerHand(); 
		}));
		timeline.setDelay(Duration.millis(startTime));
		timeline.play();
	}
	
	@FXML
	private void onHitButtonPressed() {
		hitHand(playerHand);
		updateHandDisplay(playerHandDisplay, playerHand);
		checkPlayerHand();
		doubleBetButton.setVisible(false);
	}
	
	@FXML
	private void onDoubleButtonPressed() {
		playerBet *= 2;
		playerBankLabel.setText("Bank: $" + (this.player.getBalance() - playerBet));
		playerBetLabel.setText("Bet: $" + Integer.toString(playerBet));
		Routine.doAfter(new Action() {			
			@Override
			public void invoke() {
				hitHand(playerHand);
				updateHandDisplay(playerHandDisplay, playerHand);
				checkPlayerHand();
				endPlayerTurn(true);
			}
		}, 1000);
	}
	
	@FXML
	private void onStandButtonPressed() {
		endPlayerTurn(true);
	}
	
	private void checkPlayerHand() {
		int handValue = playerHand.getHandValue();
		if (handValue < 21) return;
		endPlayerTurn(handValue == 21);
	}
	
	private void endPlayerTurn(boolean dealerHits) {
		hitButton.setVisible(false);
		doubleBetButton.setVisible(false);
		standButton.setVisible(false);
		revealDealerCard();
		if (dealerHits) dealerHits();
		else Routine.doAfter(new Action() {			
			@Override
			public void invoke() {
				endMatch();
			}
		}, 1000);
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
		Action action = new Action() {
			@Override
			public void invoke() {
				hitHand(dealerHand);
				updateHandDisplay(dealerHandDisplay, dealerHand);
			}
		};
		Conditional endCondition = new Conditional() {
			@Override
			public boolean condition() {
				return dealerHand.getHandValue() >= DEALER_HIT_STOP;
			}
		};
		Action endAction = new Action() {
			@Override
			public void invoke() {
				endMatch();
			}
		};
		Routine.doRepeatOnCondition(action, 1000, endCondition, endAction);
	}
	
	private void endMatch() {
		int playerHandValue = playerHand.getHandValue();
		int dealerHandValue = dealerHand.getHandValue();

		if (playerHandValue > 21) {  // The player busts.
			matchResult = MatchResult.Loss;
			matchResultsLabel.setText(matchResult.toString());
		}
		// The player did not bust, and their hand value is between 1-21.
		else if (dealerHandValue > 21) {  // The dealer busts.
			matchResult = MatchResult.Won;
			matchResultsLabel.setText(matchResult.toString());
		}
		
		// The player and dealer did not bust, and their hands are between 1-21.
		else if (playerHandValue < dealerHandValue) matchResult = MatchResult.Loss;	
		else if (playerHandValue == dealerHandValue) matchResult = MatchResult.Push;
		else matchResult = MatchResult.Blackjack;
		matchResultsLabel.setText(matchResult.toString());
		
		adjustPlayerBalance(matchResult);
	}
	
	private void adjustPlayerBalance(MatchResult result) {
		if (result == MatchResult.Loss) {
			player.adjustBalance(-playerBet);
		}
		else {
			if (result == MatchResult.Blackjack) player.adjustBalance(2 * playerBet);
			else if (result == MatchResult.Won) player.adjustBalance(playerBet);
		}
		
		matchHBox.setVisible(false);
		endMatchHBox.setVisible(true);
		mainMenuButton.setVisible(true);
		
		boolean balanceDepleted = player.getBalance() <= 0;
		placeBetButton.setVisible(!balanceDepleted);
		
		if (balanceDepleted) player.adjustBalance(1000);
	}
}
