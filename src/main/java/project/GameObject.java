package project;

import java.io.IOException;
import javafx.fxml.FXMLLoader;

public class GameObject {
	
	public static <T> T Instantiate(String url) {
		return new GameObject().getFXML(url);
	}
	
	private <T> T getFXML(String url) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(url));
			loader.load();
			return loader.getController();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
}
