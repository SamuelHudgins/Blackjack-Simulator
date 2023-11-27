package project;

import java.sql.*;
import java.util.ArrayList;

public class DatabaseManager {

	private static DatabaseManager instance;
	private static final String DEFAULT_URL = String.format("jdbc:sqlite:");
	private static final String DATABASE_NAME = "User Data";
	private Connection connection;
	
	// Table names
	public final String USERS = "Users";	
	public final String BANKS = "Banks";	
	public final String WINS = "Wins";	
	public final String LOSSES = "Losses";
	
	public static DatabaseManager getInstance() {
		if (instance == null) {
			instance = new DatabaseManager(DATABASE_NAME);			
		}
		return instance;
	}
	
	private DatabaseManager(String databaseName) {
		try {
			connection = DriverManager.getConnection(DEFAULT_URL + DATABASE_NAME + ".db");
			String sql = "PRAGMA foreign_keys = ON";
			Statement stmt = connection.createStatement();
			stmt.execute(sql);
			stmt.close();
			
		} catch (SQLException e) {
			System.err.println(e.getMessage());
		}
	}
	
	private boolean createTable(String tableName, String createSQL) {
//		String sql = MessageFormat.format("SELECT name FROM sqlite_master WHERE type=\"table\" AND name=\"{0}\"", tableName);
		String sql = "SELECT name FROM sqlite_master WHERE type=\"table\" AND name=\"%s\"".formatted(tableName);
		try {
			Statement test = connection.createStatement();
			ResultSet rs  = test.executeQuery(sql);
			if (rs.next()) {  // The table already exists			
				test.close();
				return false;
			}
			Statement stmt = connection.createStatement();
			stmt.execute(createSQL);
			stmt.close();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}	
	}
	
	public void createUsersTable() {
		String sql = "CREATE TABLE \"Users\" ("
					+ "	\"ID\"			INTEGER NOT NULL, "
					+ "	\"Username\"	TEXT NOT NULL, "
					+ "	\"Password\"	TEXT NOT NULL, "
					+ "	PRIMARY KEY(\"ID\" AUTOINCREMENT) "
					+ ");";
		createTable(USERS, sql);
	}
	
	public void createBanksTable() {
		String sql = "CREATE TABLE \"Banks\" ("
					+ "	\"ID\"			INTEGER, "
					+ "	\"User_ID\"		INTEGER, "
					+ "	\"Balance\"		REAL, "
					+ "	FOREIGN KEY(\"User_ID\") REFERENCES \"Users\"(\"ID\") ON DELETE CASCADE, "
					+ "	PRIMARY KEY(\"ID\" AUTOINCREMENT) "
					+ ");";
		createTable(BANKS, sql);
	}
	
	public void createWinsTable() {
		String sql = "CREATE TABLE \"Wins\" ("
				+ "	\"User_ID\"		INTEGER, "
				+ "	\"Amount\"		REAL NOT NULL, "
				+ "	FOREIGN KEY(\"User_ID\") REFERENCES \"Users\"(\"ID\") ON DELETE CASCADE "
				+ ");";
		createTable(WINS, sql);
	}
	
	public void createLossesTable() {
		String sql = "CREATE TABLE \"Losses\" ("
					+ "	\"User_ID\"		INTEGER, "
					+ "	\"Amount\"		REAL NOT NULL, "
					+ "	FOREIGN KEY(\"User_ID\") REFERENCES \"Users\"(\"ID\") ON DELETE CASCADE "
					+ ");";
		createTable(LOSSES, sql);
	}
	
	public boolean usernameExist(String username) {
		String sql = "SELECT EXISTS (SELECT 1 FROM Users WHERE Username = ?);";				
		try {
			// Use prepared statements to prevent SQL injection attacks from client-supplied data
			// 	(https://docs.oracle.com/javase/tutorial/jdbc/basics/prepared.html). 
			PreparedStatement pstmt = connection.prepareStatement(sql);
			pstmt.setString(1, username);
		 	ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				// If the result is 1, then a matching record was located, and this method will return true.
				return rs.getInt(1) == 1;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	public boolean insertUser(String userName, String password) {
		if (usernameExist(userName)) return false;

		String sql = "INSERT INTO Users (Username, Password) VALUES (?, ?);";
		try {
			PreparedStatement pstmt = connection.prepareStatement(sql);
			pstmt.setString(1, userName);
			pstmt.setString(2, password);
			pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	private int getUserID(String userName) {
		if (!usernameExist(userName)) return -1;

		String sql = "SELECT ID FROM Users WHERE Username = ?";
		try {			
			PreparedStatement pstmt = connection.prepareStatement(sql);
			pstmt.setString(1, userName);
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				return rs.getInt(1);
			}			
			pstmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
			return -1;
		}
		return -1;
	}
	
	public String getUserPassword(String userName) {
		if (!usernameExist(userName)) return null;
		
		String sql = "SELECT Password FROM Users WHERE Username = ?";
		try {			
			PreparedStatement pstmt = connection.prepareStatement(sql);
			pstmt.setString(1, userName);
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				return rs.getString(1);
			}			
			pstmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
		return null;
	}
	
	public Double getUserBankBalance(String userName) {
		int userID = getUserID(userName);
		if (userID == -1) return -1.0;

		String sql = "SELECT Balance FROM BANKS WHERE User_ID = " + userID;
		try {			
			Statement stmt = connection.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			if (rs.next()) {
				return rs.getDouble(1);
			}			
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
			return -1.0;
		}
		return -1.0;
	}
	
	public void setUserBankBalance(String userName, Double amount) {
		int userID = getUserID(userName);
		if (userID == -1) return;

		String sql = "UPDATE Banks SET Balance = " + amount + " WHERE User_ID = " + userID;
		try {			
			Statement stmt = connection.createStatement();
			stmt.executeUpdate(sql);
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public boolean insertBank(String userName, Double amount) {		
		int userID = getUserID(userName);
		if (userID == -1) return false;

		String sql = "INSERT INTO Banks (User_ID, Balance) VALUES (" + userID + ", " + amount + ")";
		try {			
			Statement stmt = connection.createStatement();
			stmt.executeUpdate(sql);			
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	public boolean insertWinnings(String userName, Double amount) {		
		int userID = getUserID(userName);
		if (userID == -1) return false;

		String sql = "INSERT INTO Wins (User_ID, Amount) VALUES (" + userID + ", ?)";
		try {			
			PreparedStatement pstmt = connection.prepareStatement(sql);
			pstmt.setDouble(1, amount);
			pstmt.executeUpdate();			
			pstmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	public boolean insertLosses(String userName, Double amount) {		
		int userID = getUserID(userName);
		if (userID == -1) return false;

		String sql = "INSERT INTO Losses (User_ID, Amount) VALUES (" + userID + ", ?)";
		try {			
			PreparedStatement pstmt = connection.prepareStatement(sql);
			pstmt.setDouble(1, amount);
			pstmt.executeUpdate();			
			pstmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	public boolean removeUser(String userName) {
		if (!usernameExist(userName)) return false;

		String sql = "DELETE FROM Users WHERE Username = ?";
		try {
			PreparedStatement pstmt = connection.prepareStatement(sql);
			pstmt.setString(1, userName);
			pstmt.executeUpdate();			
			pstmt.close();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public ArrayList<UserStat> getAllUsersStats() {
		String sql = ("SELECT "
						+ "ROW_NUMBER() OVER(ORDER BY Balance DESC) AS Rank, "
						+ "Username, "
						+ "Banks.Balance, "
						+ "(SELECT ifnull(sum(Amount), 0) FROM Wins WHERE Wins.User_ID = Users.ID) AS winnings, "
						+ "(SELECT ifnull(sum(Amount), 0) FROM Losses WHERE Losses.User_ID = Users.ID) AS loss "
					+ "FROM "
						+ "Users "
						+ "LEFT JOIN Banks ON Banks.User_ID = Users.ID");
		try {
			Statement stmt = connection.createStatement();
			ResultSet rs  = stmt.executeQuery(sql);
			return resultSetToUserStatList(rs);
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	private ArrayList<UserStat> resultSetToUserStatList(ResultSet rs) {
		if (rs == null) return null;
		
		ArrayList<UserStat> stats = new ArrayList<UserStat>();
		try {
			ResultSetMetaData rsmd = rs.getMetaData();
				while (rs.next()) {
					String rank = String.valueOf(rs.getObject(rsmd.getColumnName(1)));
					String name = String.valueOf(rs.getObject(rsmd.getColumnName(2)));
					String balance = String.valueOf(rs.getObject(rsmd.getColumnName(3)));
					String winnings = String.valueOf(rs.getObject(rsmd.getColumnName(4)));
					String losses = String.valueOf(rs.getObject(rsmd.getColumnName(5)));
					UserStat userStat = new UserStat(rank, name, balance, winnings, losses);
					stats.add(userStat);
				}
				return stats;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public void resetUserStats(String userName, Double bankAmount) {
		int userID = getUserID(userName);
		if (userID == -1) return;

		String deleteWins = "DELETE FROM Wins WHERE User_ID = " + userID;
		String deleteLosses = "DELETE FROM Losses WHERE User_ID = " + userID;
		String updateBank = "UPDATE Banks SET Balance = " + bankAmount + " WHERE User_ID = " + userID;
		
		try {
			Statement stmt = connection.createStatement();
			stmt.execute(deleteWins);
			stmt.execute(deleteLosses);
			stmt.execute(updateBank);
			stmt.close();			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void closeConnection() {
		try {
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
