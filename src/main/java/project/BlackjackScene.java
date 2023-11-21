package project;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;

public class BlackjackScene extends SceneController {

	@FXML private Pane boardPane;
	
	@FXML private Button placeBetButton;	
	@FXML private Button mainMenuButton;
	@FXML private HBox matchHBox;	
	@FXML private HBox endMatchHBox;
	
	@FXML private Label playerBankLabel;	
	@FXML private Label playerBetLabel;	
	
	@FXML private Button hitButton;	
	@FXML private Button doubleBetButton;	
	@FXML private Button standButton;	
	
	@FXML private Pane playerCardPane;
	@FXML private Label playerHandLabel;
	@FXML private Label playerStatusLabel;
	
	@FXML private Pane dealerCardPane;
	@FXML private Label dealerHandLabel;
	@FXML private Label dealerStatusLabel;
	
	@FXML private Label matchResultsLabel;
	
	private BlackjackMatch blackjackMatch;
	
	public Label getMatchResultsLabel() {
		return matchResultsLabel;
	}
	
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
		Player player = Player.getInstance();
		playerBankLabel.setText("Bank: $" + (player.getBalance()));
		playerBetLabel.setText("Bet: $" + Integer.toString(player.getBet()));
		HandDisplay playerHandDisplay = new HandDisplay(playerHandLabel, playerStatusLabel);
		HandDisplay dealerHandDisplay = new HandDisplay(dealerHandLabel, dealerStatusLabel);
		blackjackMatch = new BlackjackMatch(this, playerHandDisplay, dealerHandDisplay);
		blackjackMatch.start(boardPane, playerCardPane, dealerCardPane);
	}
	
	public void showMatchOptions(boolean canDouble) {
		hitButton.setVisible(true);
		doubleBetButton.setVisible(canDouble);
		standButton.setVisible(true);
	}
	
	public void hideMatchOptions() {
		hitButton.setVisible(false);
		doubleBetButton.setVisible(false);
		standButton.setVisible(false);
	}
	
	@FXML
	private void onHitButtonPressed() {
		doubleBetButton.setVisible(false);
		blackjackMatch.playerHit();
	}
	
	@FXML
	private void onDoubleButtonPressed() {
		blackjackMatch.doublePlayerBet();
	}
	
	@FXML
	private void onStandButtonPressed() {
		hitButton.setVisible(false);
		doubleBetButton.setVisible(false);
		standButton.setVisible(false);
		blackjackMatch.playerStand();
	}
	
	public void setPlayerBankLabel(String text) {
		playerBankLabel.setText("Bank: $" + text);
	}
	
	public void setPlayerBetLabel(String text) {
		playerBetLabel.setText("Bet: $" + text);
	}
	
	public void showEndMatchResult(String matchResult) {
		matchResultsLabel.setText(matchResult);
	}
	
	public void showEndMatchOptions(boolean balanceDepleted) {
		matchHBox.setVisible(false);
		endMatchHBox.setVisible(true);
		mainMenuButton.setVisible(true);
		placeBetButton.setVisible(!balanceDepleted);
	}
}
