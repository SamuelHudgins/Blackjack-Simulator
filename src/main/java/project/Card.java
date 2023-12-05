package project;

import javafx.animation.ParallelTransition;
import javafx.animation.PauseTransition;
import javafx.animation.ScaleTransition;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.util.Duration;

/**
 * Holds information for representing instances of cards, including their 
 * face value and imagery.
 */
public class Card implements IPlaceable {
	
	@FXML private ImageView cardImage;
	private Node sceneNode;
	private Pane parent;
	private String value; 
	private String suit; 
	private int faceValue; 
	private boolean faceDown;
	
	public Card() {
		
	}
	
	public Card(String value, String suit, Bounds bounds) {
		this.value = value;
		this.suit = suit;
		
		if (value.equals("A")) faceValue = 11;
		else if (("JQK").contains(value)) faceValue = 10;			
		else faceValue =  Integer.parseInt(value);  // For numbers 2-10
		
		cardImage = new ImageView();
		cardImage.setFitWidth(bounds.getWidth());
		cardImage.setFitHeight(bounds.getHeight());
	}
	
	/**
	 * Returns the alphanumeric value of this card.
	 */
	public String getValue() {
		return value;
	}
	
	/**
	 * Returns the value that's used when calculating a hand's value.
	 */
	public int getFaceValue() {		
		return faceValue;
	}
	
	public boolean getFaceDown() {
		return faceDown;
	}
	
	public void setFaceDown(boolean down) {
		faceDown = down;
		displayCard();
	}	
	
	/**
	 * This method flips a card up and creates the effect of flipping a card. It performs the flip 
	 * by adjusting the card's scale to preserve its rotation values, then switches its image 
	 * halfway through the flip.
	 */
	public void flipCardUp() {
		faceDown = false;
		Image backImage = new Image(getClass().getResourceAsStream("/images/BACK.png"));
		Image frontImage = new Image(getClass().getResourceAsStream("/images/"+value+"-"+suit+".png"));
		cardImage.setImage(backImage);
		
		double flipTime = 0.5;
		ScaleTransition rotator = new ScaleTransition(Duration.seconds(flipTime), cardImage);
		rotator.setFromX(-1);
		rotator.setToX(1);

		PauseTransition pause = new PauseTransition(Duration.seconds(flipTime * 0.5));
		pause.setOnFinished(e -> cardImage.setImage(frontImage));

		ParallelTransition parallelTransition = new ParallelTransition(rotator, pause);
		parallelTransition.play();
	}
	
	public Node getCardImage() {
		return cardImage;
	}
	
	public void displayCard() {
		String path = faceDown ? "/images/BACK.png" : "/images/"+value+"-"+suit+".png";
		cardImage.setImage(new Image(getClass().getResource(path).toExternalForm()));
	}
	
	public void setRotation(double z) {
		cardImage.setRotate(z);
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
		return cardImage;
	}
	
	/**
	 * Sets the parent node of this object's JavaFX node. The parent node must be 
	 * a type of {@code Pane}.
	 * @param The parent node of this object's JavaFX node.
	 */
	public <T extends Pane> void setParentPane(T _parent) {
		if (parent != null) {
			parent.getChildren().remove(cardImage);
		}
		parent = _parent;
		if (parent != null) parent.getChildren().add(cardImage);
	}
	
	/**
	 * Returns the scene space position of this node.
	 */
	public Point2D getPosition() {
		Point2D point = cardImage.localToScene(0, 0);
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
		cardImage.setLayoutX(point.getX());
		cardImage.setLayoutY(point.getY());
	}	
	
	/**
	 * Sets the scene space position of this node. This position is based on the scene 
	 * space position of the provided node.
	 */
	public void setPosition(Node node) {
		Point2D point = node.localToScene(0, 0);
		cardImage.setLayoutX(point.getX());
		cardImage.setLayoutY(point.getY());
	}
}
