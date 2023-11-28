package project;

import javafx.scene.layout.Pane;

/**
 * An interface that instantiable objects, like {@code Card} and {@code Hand} 
 * instances, use to provide implementations for setting their parent node and position.
 */
public interface IPlaceable {
	public <T extends Pane> void setParentPane(T parent);
	public void setPosition(double x, double y);
}
