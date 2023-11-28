package project;

import java.util.ArrayList;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

/**
 * This class extends the {@code SceneController} and manages the GUI events 
 * for the stats scene. This class uses {@code UserStats} class instances 
 * to populate the table.
 */
public class StatsScene extends SceneController {

	// Table
	@FXML private TableView<UserStat> statsTable;
	
	// Columns
	@FXML private TableColumn<UserStat, String> Rank;
	@FXML private TableColumn<UserStat, String> Name;
	@FXML private TableColumn<UserStat, String> BankBalance;
	@FXML private TableColumn<UserStat, String> Winnings;
	@FXML private TableColumn<UserStat, String> Losses;
	
	@FXML
	private void initialize() {
		Rank.setCellValueFactory(new PropertyValueFactory<UserStat, String>("rank"));
		Name.setCellValueFactory(new PropertyValueFactory<UserStat, String>("name"));
		BankBalance.setCellValueFactory(new PropertyValueFactory<UserStat, String>("balance"));
		Winnings.setCellValueFactory(new PropertyValueFactory<UserStat, String>("winnings"));
		Losses.setCellValueFactory(new PropertyValueFactory<UserStat, String>("losses"));
		
		displayAllUserStats();
	}
	
	@FXML
	protected MainMenuScene switchToMainMenuScene() {
		super.switchToMainMenuScene();
		return null;
	}
	
	private void displayAllUserStats() {
		ArrayList<UserStat> stats = DatabaseManager.getInstance().getAllUsersStats();
		ObservableList<UserStat> userStats;
		for (UserStat stat : stats) {
			userStats = statsTable.getItems();
			userStats.add(stat);	
			statsTable.setItems(userStats);
		}
	}
}
