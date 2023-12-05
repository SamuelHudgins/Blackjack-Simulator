package project;

import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.layout.Pane;

/**
 * An interface that instantiable objects, like {@code Card} and {@code Hand} 
 * instances, use to provide implementations for setting their parent node and position.
 */
public interface IPlaceable {
	public void setSceneNode(Node node);
	public Node getNode();
	public <T extends Pane> void setParentPane(T parent);
	public Point2D getPosition();
	public void setPosition(double x, double y);
	public void setPosition(Node node);
}
