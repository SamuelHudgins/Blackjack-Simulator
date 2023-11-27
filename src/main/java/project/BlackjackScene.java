package project;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

public class BlackjackScene extends SceneController {

	@FXML private Pane boardPane;
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
	@FXML private Button evenMoneyButton;	
	
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
		evenMoneyButton.setVisible(false);
	}
	
	public void setup() {
		Player player = Player.getInstance();
		playerBankLabel.setText("Bank: $" + (player.getBalance()));
		playerBetLabel.setText("Bet: $" + Double.toString(player.getBet()));
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
		if (allowEvenMoney) evenMoneyButton.setVisible(true);
		else if (allowInsurance) insuranceButton.setVisible(true);
	}
	
	public void hideMatchOptions() {
		hitButton.setVisible(false);
		doubleBetButton.setVisible(false);
		standButton.setVisible(false);
		splitButton.setVisible(false);
		insuranceButton.setVisible(false);
		evenMoneyButton.setVisible(false);
	}
	
	public void hideInsuranceOptions() {
		insuranceButton.setVisible(false);
		evenMoneyButton.setVisible(false);
	}
	
	public void showSplitButton(boolean canSplit) {
		splitButton.setVisible(canSplit);
	}
	
	@FXML
	private void onHitButtonPressed() {
		doubleBetButton.setVisible(false);
		hideInsuranceOptions();
		blackjackMatch.playerHit();
	}
	
	@FXML
	private void onDoubleButtonPressed() {
		doubleBetButton.setVisible(false);
		hideInsuranceOptions();
		blackjackMatch.doublePlayerBet();
	}
	
	@FXML
	private void onStandButtonPressed() {
		blackjackMatch.playerStand();
	}
	
	@FXML
	private void onSplitButtonPressed() {
		splitButton.setVisible(false);
		blackjackMatch.playerSplit();
	}
	
	@FXML
	private void oninsuranceButtonPressed() {
		insuranceButton.setVisible(false);
		matchHBox.setVisible(false);
		blackjackMatch.useInsurance();
	}
	
	@FXML
	private void onEvenMoneyButtonPressed() {
		evenMoneyButton.setVisible(false);
		matchHBox.setVisible(false);
		blackjackMatch.useInsurance();
	}
	
	public void showInusuranceLost() {
		insuranceLostLabel.setVisible(true);
		Routine.doAfter(() -> { 
			insuranceLostLabel.setVisible(false); 
			matchHBox.setVisible(true);
		}, 2000);
	}
	
	public void setPlayerBankLabel(String text) {
		playerBankLabel.setText("Bank: $" + text);
	}
	
	public void setPlayerBetLabel(String text) {
		playerBetLabel.setText("Bet: $" + text);
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
			}, 2000);			
		}
	}
	
	@FXML
	private void onGameOverContinueButtonPressed() {		
		switchToMainMenuScene();
	}
}
