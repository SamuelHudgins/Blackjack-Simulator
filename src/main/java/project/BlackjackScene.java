package project;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

/**
 * Extends the {@code SceneController} and manages the 
 * GUI events for the blackjack match scene. This class also sets up a 
 * {@code BlackjackMatch} instance, which controls the gameplay logic of the blackjack match.
 */
public class BlackjackScene extends SceneController {

	@FXML private Pane boardPane;
	@FXML private Pane deckPane;
	@FXML private Pane removedCardPane;
	
	@FXML private Button placeBetButton;	
	@FXML private Button mainMenuButton;
	@FXML private HBox matchHBox;	
	@FXML private HBox endMatchHBox;
	
	@FXML private Label playerBankLabel;	
	@FXML private Label playerBetLabel;
	
	@FXML private Button hitButton;	
	@FXML private Button doubleBetButton;	
	@FXML private Button standButton;	
	@FXML private Button splitButton;	
	@FXML private Button insuranceButton;	
	
	@FXML private Pane playerCardPane;
	@FXML private Pane playerSplitPane1;
	@FXML private Pane playerSplitPane2;
	@FXML private Pane playerSplitPane3;
	@FXML private Label playerHandLabel;
	@FXML private Label playerStatusLabel;
	
	@FXML private Pane dealerCardPane;
	@FXML private Label dealerHandLabel;
	@FXML private Label dealerStatusLabel;
	
	@FXML private Label insuranceLostLabel;
	@FXML private Label matchResultsLabel;
	
	private BlackjackMatch blackjackMatch;
	
	@FXML private StackPane gameOverPane;
	@FXML private Label gameOverText;

	// Scene control methods
	
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
	
	public Label getMatchResultsLabel() {
		return matchResultsLabel;
	}
	
	public Pane getDeckPane() {
		return deckPane;
	}
	
	public Pane getRemovedCardPane() {
		return removedCardPane;
	}
	
	@FXML
	private void initialize() {
		placeBetButton.setVisible(false);
		mainMenuButton.setVisible(false);
		matchHBox.setVisible(true);
		endMatchHBox.setVisible(false);
		matchResultsLabel.setText("");
		insuranceLostLabel.setVisible(false);
		playerHandLabel.setText("");
		playerStatusLabel.setText("");
		dealerHandLabel.setText("");
		dealerStatusLabel.setText("");
		hitButton.setVisible(false);
		doubleBetButton.setVisible(false);
		standButton.setVisible(false);
		splitButton.setVisible(false);
		insuranceButton.setVisible(false);
	}
	
	/**
	 * Sets up the GUI and provides GUI information to the {@code BlackjackMatch} instance 
	 * to determine the placement of hands and their displays.
	 */
	public void setup() {
		Player player = Player.getInstance();
		setPlayerBankLabel(Double.toString(player.getBalance()));
		setPlayerBetLabel(Double.toString(player.getBet()));
		HandDisplay playerHandDisplay = new HandDisplay(playerHandLabel, playerStatusLabel);
		HandDisplay dealerHandDisplay = new HandDisplay(dealerHandLabel, dealerStatusLabel);		
		blackjackMatch = new BlackjackMatch(this, playerHandDisplay, dealerHandDisplay);
		Pane [] playerCardPanes = new Pane[] { playerCardPane, playerSplitPane1, playerSplitPane2, playerSplitPane3 };
		blackjackMatch.start(boardPane, playerCardPanes, dealerCardPane);
	}
	
	public void showMatchOptions(boolean canHit, boolean canDouble, boolean canSplit, boolean allowInsurance, boolean allowEvenMoney) {
		hitButton.setVisible(canHit);
		doubleBetButton.setVisible(canDouble);
		standButton.setVisible(true);
		splitButton.setVisible(canSplit);
		insuranceButton.setVisible(allowEvenMoney || allowInsurance);
		if (allowEvenMoney) {
			insuranceButton.setText("Even Money");
		}
		else if (allowInsurance) {
			insuranceButton.setText("Insurance");
		}
		matchHBox.setDisable(false);
	}
	
	public void setMatchOptionsEnabled(boolean enabled) {
		matchHBox.setDisable(!enabled);
		if (!enabled) resetButtonEffects();
	}
	
	public void showBestOption(Hand playerHand, Hand dealerHand) {
		resetButtonEffects();
		BestChoice choice = OddsGenerator.getInstance().getBestChoice(playerHand, dealerHand);
		switch (choice) {
			case Hit:
				highlightButton(hitButton);
				break;
			case Stand:
				highlightButton(standButton);
				break;
			case Double:
				highlightButton(doubleBetButton);
				break;
			case Split:
				highlightButton(splitButton);
				break;	
		}		
	}
	
	private void highlightButton(Button button) {
		if (!button.isVisible()) return;
		DropShadow dropShadow = new DropShadow();
		dropShadow.setBlurType(BlurType.GAUSSIAN);
		dropShadow.setWidth(50);
		dropShadow.setHeight(50);
		dropShadow.setSpread(0.5);
		dropShadow.setColor(Color.WHITE);
		button.setEffect(dropShadow);
	}
	
	private void resetButtonEffects() {
		hitButton.setEffect(null);
		standButton.setEffect(null);
		doubleBetButton.setEffect(null);
		splitButton.setEffect(null);
	}
	
	public void hideMatchOptions() {
		hitButton.setVisible(false);
		doubleBetButton.setVisible(false);
		standButton.setVisible(false);
		splitButton.setVisible(false);
		insuranceButton.setVisible(false);
	}
	
	public void showSplitButton(boolean canSplit) {
		splitButton.setVisible(canSplit);
	}
	
	@FXML
	private void onHitButtonPressed() {
		setMatchOptionsEnabled(false);
		doubleBetButton.setVisible(false);
		insuranceButton.setVisible(false);
		blackjackMatch.playerHit();
	}
	
	@FXML
	private void onDoubleButtonPressed() {
		setMatchOptionsEnabled(false);
		doubleBetButton.setVisible(false);
		insuranceButton.setVisible(false);
		blackjackMatch.doublePlayerBet();
	}
	
	@FXML
	private void onStandButtonPressed() {
		setMatchOptionsEnabled(false);
		blackjackMatch.finishPlayerHand();
	}
	
	@FXML
	private void onSplitButtonPressed() {
		setMatchOptionsEnabled(false);
		splitButton.setVisible(false);
		blackjackMatch.playerSplit();
	}
	
	@FXML
	private void oninsuranceButtonPressed() {
		insuranceButton.setVisible(false);
		blackjackMatch.useInsurance();
	}
	
	public void showInusuranceLost() {
		insuranceLostLabel.setVisible(true);
		Routine.doAfter(() -> { 
			insuranceLostLabel.setVisible(false); 
			matchHBox.setVisible(true);
			setMatchOptionsEnabled(true);
		}, 2);
	}
	
	public void setPlayerBankLabel(String text) {
		playerBankLabel.setText("Bank: $" + text);
	}
	
	public void setPlayerBetLabel(String text) {
		playerBetLabel.setText("$" + text.replace(".0", ""));
	}
	
	public void setMatchResultText(String matchResult) {
		matchResultsLabel.setText(matchResult);
	}
	
	public void showEndMatchOptions(boolean balanceDepleted) {
		if (!balanceDepleted) {
			matchHBox.setVisible(false);
			endMatchHBox.setVisible(true);
			mainMenuButton.setVisible(true);
			placeBetButton.setVisible(true);
		}
		else {
			// Reset the player's stats and give them money if their balance is depleted.
			Player player = Player.getInstance();
			DatabaseManager.getInstance().resetUserStats(player.getUsername(), Player.START_BALANCE);
			player.setBalance(Player.START_BALANCE);
			
			Routine.doAfter(() -> {
				gameOverPane.setVisible(true);
				String text = !Player.getInstance().getGuest() ? "Your account transaction history has been reset." : "";
				text += "\n\nAn anonymous donor\nhas gifted you $" + Player.START_BALANCE + ".";
				gameOverText.setText(text);
				gameOverPane.toFront();
			}, 2);			
		}
	}
	
	@FXML
	private void onGameOverContinueButtonPressed() {		
		switchToMainMenuScene();
	}
}
