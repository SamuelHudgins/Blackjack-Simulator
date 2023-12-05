package project;

import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;

/**
 * This interface is used only by the {@code RegistrationScene}, {@code LoginScene}, and {@code DeleteScene} classes. 
 * It provides reusable code for creating a button that returns the user to the application's main menu.
 */
public sealed interface IAccountScenes permits RegistrationScene, LoginScene, DeleteScene {

	default void addBackButton(Pane pane, SceneController controller) {
		Button backButton = new Button("Return to Menu");
		Font font = new Font(15);
		backButton.setFont(font);
		backButton.setOnAction(e -> controller.switchToMainMenuScene());
		pane.getChildren().add(backButton);
	}
}
