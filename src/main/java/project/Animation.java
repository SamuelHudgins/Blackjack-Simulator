package project;

import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.util.Duration;
import project.Routine.Action;

/**
 * This class provides methods for animating objects that implement the {@code IPlaceable} interface.
 */
public class Animation {
	public static final float CARD_TRANSLATE_TIME = 0.5f;
	
	/**
	 * Animates the target object from a starting node's position to an ending node's position.
	 * @param target The target object to move.
	 * @param startNode The initial node the target node will be placed at before being moved.
	 * @param endNode The node the target node will end on after the animation ends.
	 */
	public static void Translate(IPlaceable target, Node startNode, Node endNode) {	
		Translate(target, startNode, endNode, null);
	}
	
	/**
	 * Animates the target object from a starting node's position to an ending node's position. 
	 * After the animation is complete, this calls the specified action(s).
	 * @param target The target object to move.
	 * @param startNode The initial node the target node will be placed at before being moved.
	 * @param endNode The node the target node will end on after the animation ends.
	 * @param endAction The action to invoke at the end of the animation.
	 */
	public static void Translate(IPlaceable target, Node startNode, Node endNode,  Action endAction) {
		TranslateTransition translate = new TranslateTransition(Duration.seconds(CARD_TRANSLATE_TIME), target.getNode());
		// Setting the target object's translation units to the start node and then translating 
		// to zero  allows objects that will become children to translate to their child coordinates 
		// rather than their parent's.
		target.setPosition(endNode);
		Point2D startPoint = startNode.localToScene(0, 0);
		Point2D endPoint = endNode.localToScene(0, 0);
		target.getNode().setTranslateX(startPoint.getX() - endPoint.getX());
		target.getNode().setTranslateY(startPoint.getY() - endPoint.getY());
		translate.setToX(0);
		translate.setToY(0);
		if (endAction != null) translate.setOnFinished(e -> endAction.invoke());
		translate.play();	
	}
	
	/**
	 * Scales the target object from a starting scale to an ending scale.
	 * @param target The target object to scale.
	 * @param startScale The initial scale this object will be set to before applying the scale animation.
	 * @param endScale The ending scale this object will be set to once the scale animation completes.
	 */
	public static void Scale(IPlaceable target, Point2D startScale, Point2D endScale) {	
		ScaleTransition scale = new ScaleTransition(Duration.seconds(CARD_TRANSLATE_TIME), target.getNode());
		scale.setFromX(startScale.getX());
		scale.setFromY(startScale.getY());
		scale.setToX(endScale.getX());
		scale.setToY(endScale.getY());
		scale.play();	
	}
	
}
