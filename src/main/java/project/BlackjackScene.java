package project;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.util.Duration;

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
	@FXML private HBox endMatchHBox;
	
	@FXML private Label playerBankLabel;	
	@FXML private Label playerBetLabel;
	@FXML private Label playerInsuranceBetLabel;
	
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
	private MatchBanner playerBanner;
	
	@FXML private Pane dealerCardPane;
	@FXML private Label dealerHandLabel;
	private MatchBanner dealerBanner;
	
	private MatchBanner pushBanner;
	@FXML private ImageView vignetteImage;
	private final double VIGNETTE_TIME = 0.5;
	private final double VIGNETTE_OPACITY = 0.5;
	
	@FXML private Label insuranceLostLabel;
	
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
		endMatchHBox.setVisible(false);
		insuranceLostLabel.setVisible(false);
		playerHandLabel.setText("");
		dealerHandLabel.setText("");
		hitButton.setVisible(false);
		doubleBetButton.setVisible(false);
		standButton.setVisible(false);
		splitButton.setVisible(false);
		insuranceButton.setVisible(false);
		vignetteImage.setOpacity(0);
	}
	
	/**
	 * Sets up the GUI and provides GUI information to the {@code BlackjackMatch} instance 
	 * to determine the placement of hands and their displays.
	 */
	public void setup() {
		Player player = Player.getInstance();
		setPlayerBankLabel(Double.toString(player.getBalance()));
		setPlayerBetLabel(Double.toString(player.getBet()));
		HandDisplay playerHandDisplay = new HandDisplay(playerHandLabel);
		HandDisplay dealerHandDisplay = new HandDisplay(dealerHandLabel);		
		blackjackMatch = new BlackjackMatch(this, playerHandDisplay, dealerHandDisplay);
		Pane [] playerCardPanes = new Pane[] { playerCardPane, playerSplitPane1, playerSplitPane2, playerSplitPane3 };
		playerBanner = GameObject.Instantiate("MatchBanner.fxml");
		playerBanner.setParentPane(boardPane);
		playerBanner.setSceneNode(boardPane);
		playerBanner.setPosition(playerCardPane);
		playerBanner.setPlayer("Player");
		
		dealerBanner = GameObject.Instantiate("MatchBanner.fxml");
		dealerBanner.setParentPane(boardPane);
		dealerBanner.setSceneNode(boardPane);
		dealerBanner.setPosition(dealerCardPane);
		dealerBanner.setPlayer("Dealer");
		
		pushBanner = GameObject.Instantiate("MatchBanner.fxml");
		pushBanner.setParentPane(boardPane);
		pushBanner.setSceneNode(boardPane);
		pushBanner.setPosition(0, 0);
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
		setMatchOptionsDisabled(false);
	}
	
	public void setMatchOptionsDisabled(boolean disabled) {
		hitButton.setDisable(disabled);
		doubleBetButton.setDisable(disabled);
		standButton.setDisable(disabled);
		splitButton.setDisable(disabled);
		insuranceButton.setDisable(disabled);
		if (disabled) resetButtonEffects();
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
		setMatchOptionsDisabled(true);
		doubleBetButton.setVisible(false);
		insuranceButton.setVisible(false);
		blackjackMatch.playerHit();
	}
	
	@FXML
	private void onDoubleButtonPressed() {
		setMatchOptionsDisabled(true);
		doubleBetButton.setVisible(false);
		insuranceButton.setVisible(false);
		blackjackMatch.doublePlayerBet();
	}
	
	@FXML
	private void onStandButtonPressed() {
		setMatchOptionsDisabled(true);
		blackjackMatch.finishPlayerHand();
	}
	
	@FXML
	private void onSplitButtonPressed() {
		setMatchOptionsDisabled(true);
		splitButton.setVisible(false);
		blackjackMatch.playerSplit();
	}
	
	@FXML
	private void oninsuranceButtonPressed() {
		insuranceButton.setVisible(false);
		blackjackMatch.useInsurance();
	}
	
	public void setPlayerBankLabel(String text) {
		playerBankLabel.setText("Bank: $" + text);
	}
	
	public void setPlayerBetLabel(String text) {
		playerBetLabel.setText("$" + text.replace(".0", ""));
	}
	
	public void setPlayerInsuranceBetLabel(String text) {
		playerInsuranceBetLabel.setText("$" + text.replace(".0", ""));
	}
	
	public void showPlayerBanner(MatchResult result) {
		playerBanner.showBanner(result);
	}
	
	public void showDealerBanner(MatchResult result) {
		dealerBanner.showBanner(result);
	}
	
	public void hidePlayerBanner() {
		playerBanner.hideBanner();
	}
	
	public void hideDealerBanner() {
		dealerBanner.hideBanner();
	}
	
	public void showPushBanner() {
		pushBanner.showBanner(MatchResult.Push);
		applyVignette();
	}
	
	public void showMatchResults(MatchResult playerMatchResult, MatchResult dealerMatchResult) {
		if (playerMatchResult == MatchResult.Blackjack) playerMatchResult = MatchResult.Won;
		if (dealerMatchResult == MatchResult.Blackjack) dealerMatchResult = MatchResult.Won;
		
		applyVignette();
		playerBanner.showBanner(playerMatchResult);
		dealerBanner.showBanner(dealerMatchResult);
	}
	
	public void hideMatchResults() {
		hidePlayerBanner();
		hideDealerBanner();
		pushBanner.hideBanner();
		removeVignette();
	}
	
	private void applyVignette() {
		vignetteImage.toFront();
		Timeline vignetteAnim = new Timeline(
			new KeyFrame(Duration.seconds(0), new KeyValue(vignetteImage.opacityProperty(), 0)),
			new KeyFrame(Duration.seconds(VIGNETTE_TIME), new KeyValue(vignetteImage.opacityProperty(), VIGNETTE_OPACITY)));
		vignetteAnim.play();
	}
	
	private void removeVignette() {
		Timeline vignetteAnim = new Timeline(
			new KeyFrame(Duration.seconds(0), new KeyValue(vignetteImage.opacityProperty(), VIGNETTE_OPACITY)),
			new KeyFrame(Duration.seconds(VIGNETTE_TIME), new KeyValue(vignetteImage.opacityProperty(), 0)));
		vignetteAnim.play();
	}
	
	public void showEndMatchOptions(boolean balanceDepleted) {
		if (!balanceDepleted) {
			hideMatchOptions();
			endMatchHBox.setVisible(true);
			endMatchHBox.toFront();
			mainMenuButton.setVisible(true);
			placeBetButton.setVisible(true);
		}
		else {
			// Reset the player's stats and give them money if their balance is too low.
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
