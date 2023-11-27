package project;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

public class DeleteScene extends SceneController {

	@FXML private TextField usernameField;
	
	@FXML private PasswordField passwordField;
	
	@FXML private TextField deleteField;
	@FXML private Text deleteErrorText;
	@FXML private Label errorLabel;
	
	@FXML
	private void initialize() {
		resetErrorLabels();
	}
	
	private void resetErrorLabels() {
		deleteErrorText.setText("");
		errorLabel.setText("");
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
		resetErrorLabels();
		boolean isValid = credentialsValid();
		if (!isValid) {
			errorLabel.setText("Incorrect username or password.");
		}
		if (!deleteField.getText().toLowerCase().equals("delete")) {
			deleteErrorText.setText("Please enter \"delete\".");
			isValid = false;
		}
		if (!isValid) return;
		
		String username = usernameField.getText();
		DatabaseManager DBM = DatabaseManager.getInstance();
		DBM.removeUser(username);
		Player.remove();
		switchToRegistrationScene();
	}
	
	private boolean credentialsValid() {
		if (usernameField.getText().isBlank() || passwordField.getText().isBlank()) return false;
		
		DatabaseManager DBM = DatabaseManager.getInstance();		
		if (!DBM.usernameExist(usernameField.getText())) return false;
		
		String encryptedPassword = DBM.getUserPassword(usernameField.getText());
		String decryptedPassword = PasswordHandler.decrypt(encryptedPassword);
		if (!passwordField.getText().equals(decryptedPassword)) return false;
		
		return true;
	}
}
