package project;

import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

/**
 * This class extends the {@code SceneController} and manages the GUI events 
 * for the account registration scene.
 */
public final class RegistrationScene extends SceneController implements IAccountScenes {
	
	@FXML private VBox registrationPane;
	@FXML private TextField usernameField;
	@FXML private Text usernameErrorLabel;
	
	@FXML private PasswordField passwordField;
	@FXML private Text passwordErrorLabel;
	
	@FXML private PasswordField confirmPasswordField;
	@FXML private Text confirmPasswordErrorLabel;
	
	@FXML
	private void initialize() {
		resetErrorLabels();
		if (Player.exists()) addBackButton(registrationPane, this);
	}
	
	private void resetErrorLabels() {
		usernameErrorLabel.setText("");
		passwordErrorLabel.setText("");
		confirmPasswordErrorLabel.setText("");
	}
	
	// Scene control methods
	
	@FXML
	protected MainMenuScene switchToMainMenuScene() {
		super.switchToMainMenuScene();
		return null;
	}
	
	@FXML
	protected LoginScene switchToLoginScene() {
		super.switchToLoginScene();
		return null;
	}
	
	// Button Methods
	@FXML
	private void onSubmitButtonPressed() {
		if (!credentialsValid()) return;
		String username = usernameField.getText();
		String password = PasswordHandler.encrypt(passwordField.getText());
		DatabaseManager DBM = DatabaseManager.getInstance();
		DBM.insertUser(username, password);
		DBM.insertBank(username, Player.START_BALANCE);
		Player.setInstance(username, Player.START_BALANCE);
		switchToMainMenuScene();
	}
	
	private boolean credentialsValid() {
		resetErrorLabels();
		boolean inValid = true;
		if (usernameField.getText().isBlank()) {
			usernameErrorLabel.setText("Please enter a username.");
			inValid = false;
		}
		else if (usernameField.getText().length() < 3) {
			usernameErrorLabel.setText("The username must be at least 3 characters.");
			inValid = false;
		}
		
		if (passwordField.getText().isBlank()) {
			passwordErrorLabel.setText("Please enter a password.");
			inValid = false;
		}
		else if (!passwordField.getText().equals(confirmPasswordField.getText())) {
			confirmPasswordErrorLabel.setText("The passwords do not match.");
			inValid = false;
		}
		
		if (DatabaseManager.getInstance().usernameExist(usernameField.getText())) {
			usernameErrorLabel.setText("The username is currently in use.");
			inValid = false;
		}
		return inValid;
	}
}
