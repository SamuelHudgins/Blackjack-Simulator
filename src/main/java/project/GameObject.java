package project;

import java.io.IOException;
import javafx.fxml.FXMLLoader;

/**
 * Contains a static method for creating and returning FXML controller object 
 * instances. This class is used for instantiating {@code Card} and {@code Hand} instances.
 */
public class GameObject {
	
	/**
	 * Uses the provided String URL to locate an FXML file and retrieves its associated controller.
	 * @param <T> The type of classes returned by this method.
	 * @param url The URL to use to find the FXML file.
	 * @return The controller class assigned to the FXML file.
	 */
	public static <T> T Instantiate(String url) {
		try {
			FXMLLoader loader = new FXMLLoader(GameObject.class.getResource(url));
			loader.load();
			return loader.getController();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
}
