package project;

public enum Scenes {
	START("StartScene"),
	LOGIN("LoginScene"),
	REGISTRATION("RegistrationScene"),
	MAIN_MENU("MainMenuScene"),
	TUTORIAL("TutorialScene"),
	BETTING("BettingScene"),
	BLACKJACK("BlackjackScene"),
	STATS("StatsScene"),
	DELETE("DeleteScene");

	private final String stringValue;

	Scenes(final String s) {
		stringValue = s;
	}

	public String toString() {
		return stringValue;
	}
	
	public String toPath() {
		return stringValue+".fxml";
	}
}
