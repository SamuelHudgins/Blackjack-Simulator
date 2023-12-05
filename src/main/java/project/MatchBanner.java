package project;

import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

/**
 * This class contains data for displaying and positioning match banners, which 
 * indicate information about the player and dealer's hands, such as when a hand 
 * busts, wins against another, and has a natural blackjack.
 */
public class MatchBanner implements IPlaceable  {
	
	@FXML private StackPane bannerPane;
	private Node sceneNode;
	private Pane parent;
	@FXML private ImageView winImage;
	@FXML private ImageView blackjackImage;
	@FXML private ImageView bustImage;
	@FXML private ImageView pushImage;
	@FXML private Label bannerLabel;
	private String player;
	
	public MatchBanner() {
		
	}
	
	@FXML
	private void initialize() {
		bannerPane.setVisible(false);
	}
	
	/**
	 * Set the name of the blackjack player 
	 * (either the player themselves or the dealer) for which this banner will appear. 
	 * The provided String name is displayed whenever a player wins.
	 */
	public void setPlayer(String player) {
		this.player = player;
	}
	
	/**
	 * Uses the provided match result to determine which banner to display.
	 * @param result A match result that determines which banner to display.
	 */
	public void showBanner(MatchResult result) {
		bannerPane.setVisible(true);
		switch (result) {
			case Blackjack:
				showBlackjackBanner();
				break;
			case Won:
				showWinBanner();
				break;
			case Push:
				showPushBanner();
				break;
			case Bust:
				showBustBanner();
				break;
			case Loss:
				bannerPane.setVisible(false);
				break;
			default:
				bannerPane.setVisible(false);
				break;
		}
	}
	
	private void showBlackjackBanner() {
		blackjackImage.setVisible(true);
		winImage.setVisible(false);
		bustImage.setVisible(false);
		pushImage.setVisible(false);
		bannerLabel.setText("Blackjack!");
		bannerPane.toFront();
	}
	
	private void showWinBanner() {
		winImage.setVisible(true);
		blackjackImage.setVisible(false);
		bustImage.setVisible(false);
		pushImage.setVisible(false);
		bannerLabel.setText(player + " Wins!");
		bannerPane.toFront();
	}
	
	private void showPushBanner() {
		pushImage.setVisible(true);
		winImage.setVisible(false);
		blackjackImage.setVisible(false);
		bustImage.setVisible(false);
		bannerLabel.setText("Push");
		bannerPane.toFront();
	}
	
	private void showBustBanner() {
		bustImage.setVisible(true);
		winImage.setVisible(false);
		blackjackImage.setVisible(false);
		pushImage.setVisible(false);
		bannerLabel.setText("Bust");
		bannerPane.toFront();
	}
	
	public void hideBanner() {
		bannerPane.setVisible(false);
		bustImage.setVisible(false);
		winImage.setVisible(false);
		blackjackImage.setVisible(false);
		pushImage.setVisible(false);
		bannerLabel.setText("");
	}
	
	// IPlaceable methods
	/**
	 * Sets the node this object will use for calculating scene space positions. 
	 * The provided node should be the root node of a scene.
	 */
	public void setSceneNode(Node node) {
		sceneNode = node;
	}
	
	/**
	 * Returns the JavaFX node this object controls.
	 */
	public Node getNode() {
		return bannerPane;
	}
	
	/**
	 * Sets the parent node of this object's JavaFX node. The parent node must be 
	 * a type of {@code Pane}.
	 * @param The parent node of this object's JavaFX node.
	 */
	public <T extends Pane> void setParentPane(T _parent) {
		if (parent != null) {
			parent.getChildren().remove(bannerPane);
		}
		parent = _parent;
		if (parent != null) parent.getChildren().add(bannerPane);
	}
	
	/**
	 * Returns the scene space position of this node.
	 */
	public Point2D getPosition() {	
		Point2D point = bannerPane.localToScene(0, 0);
		return new Point2D(point.getX(), point.getY());
	}
	
	/**
	 * Sets the scene space position of this node. This position is based on the center of the 
	 * set scene node. If the provided {@code x} and {@code y} values are zero, this object 
	 * will be placed at the center of the scene node.
	 * @param x The x offset from the center of the scene node.
	 * @param y The y offset from the center of the scene node.
	 */
	public void setPosition(double x, double y) {
		Double centerX = sceneNode.getLayoutBounds().getCenterX();
		Double centerY = sceneNode.getLayoutBounds().getCenterY();
		Point2D point = sceneNode.localToScene(centerX + x, centerY + y);
		Point2D nodeCenterPoint = new Point2D(bannerPane.getBoundsInLocal().getCenterX(), bannerPane.getBoundsInLocal().getCenterY());
		bannerPane.setLayoutX(point.getX() - nodeCenterPoint.getX());
		bannerPane.setLayoutY(point.getY() - nodeCenterPoint.getY());
	}	
	
	/**
	 * Sets the scene space position of this node. This position is based on the scene 
	 * space position of the provided node.
	 */
	public void setPosition(Node node) {
		Double centerX = node.getLayoutBounds().getCenterX();
		Double centerY = node.getLayoutBounds().getCenterY();
		Point2D point = node.localToScene(centerX, centerY);
		Point2D nodeCenterPoint = new Point2D(bannerPane.getBoundsInLocal().getCenterX(), bannerPane.getBoundsInLocal().getCenterY());
		bannerPane.setLayoutX(point.getX() - nodeCenterPoint.getX());
		bannerPane.setLayoutY(point.getY() - nodeCenterPoint.getY());
	}
}
