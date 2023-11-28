package project;

import javafx.fxml.FXML;

/**
 * This class extends the {@code SceneController} and manages the GUI events 
 * for the start scene.
 */
public class StartScene extends SceneController {
	
	@FXML
	protected LoginScene switchToLoginScene() {
		super.switchToLoginScene();
		return null;
	}

	@FXML
	protected RegistrationScene switchToRegistrationScene() {
		super.switchToRegistrationScene();
		return null;
	}
}
