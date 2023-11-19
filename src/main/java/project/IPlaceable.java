package project;

import javafx.scene.layout.Pane;

public interface IPlaceable {
	public <T extends Pane> void setParentPane(T parent);
	public void setPosition(double x, double y);
}
