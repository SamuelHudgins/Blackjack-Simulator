package project;

import java.sql.*;
import java.util.ArrayList;

/**
 * <p>This class is used for creating and managing a SQLite relational database. 
 * The database contains four tables: Users, Banks, Wins, and Losses.
 */
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
	
	/**
	 * The private constructor of {@code DatabaseManager} prevents outside classes from 
	 * instantiating copies of the database. It attempts to establish a connection to the database. 
	 * This should also create a new database file in the current directory the application is running 
	 * in, where the database will reside.
	 */
	private DatabaseManager() {
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
	
	/**
	 * Gets the {@code DatabaseManager} instance.
	 * @return A new {@code DatabaseManager}, if one doesn't exist, or the current instance.
	 */
	public static DatabaseManager getInstance() {
		if (instance == null) {
			instance = new DatabaseManager();			
		}
		return instance;
	}		
	
	/**
     * Checks if the given table exists in the database, then uses the given string query to create the table.
     * @param tableName The name of the table to create.
     * @param createSQL The SQL query to use for creating the table.
     */
	private void createTable(String tableName, String createSQL) {
		String sql = "SELECT name FROM sqlite_master WHERE type=\"table\" AND name=\"%s\"".formatted(tableName);
		try {
			Statement test = connection.createStatement();
			ResultSet rs  = test.executeQuery(sql);
			if (rs.next()) {  // The table already exists.	
				test.close();
				return;
			}
			Statement stmt = connection.createStatement();
			stmt.execute(createSQL);
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}	
	}
		
	/**
     * Creates the Users table in the database. 
     */
	public void createUsersTable() {
		String sql = "CREATE TABLE \"Users\" ("
					+ "	\"ID\"			INTEGER NOT NULL, "
					+ "	\"Username\"	TEXT NOT NULL, "
					+ "	\"Password\"	TEXT NOT NULL, "
					+ "	PRIMARY KEY(\"ID\" AUTOINCREMENT) "
					+ ");";
		createTable(USERS, sql);
	}
	
	/**
     * Creates the Banks table in the database. 
     */
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
	
	/**
     * Creates the Wins table in the database. 
     */
	public void createWinsTable() {
		String sql = "CREATE TABLE \"Wins\" ("
				+ "	\"User_ID\"		INTEGER, "
				+ "	\"Amount\"		REAL NOT NULL, "
				+ "	FOREIGN KEY(\"User_ID\") REFERENCES \"Users\"(\"ID\") ON DELETE CASCADE "
				+ ");";
		createTable(WINS, sql);
	}
	
	/**
     * Creates the Losses table in the database. 
     */
	public void createLossesTable() {
		String sql = "CREATE TABLE \"Losses\" ("
					+ "	\"User_ID\"		INTEGER, "
					+ "	\"Amount\"		REAL NOT NULL, "
					+ "	FOREIGN KEY(\"User_ID\") REFERENCES \"Users\"(\"ID\") ON DELETE CASCADE "
					+ ");";
		createTable(LOSSES, sql);
	}
	
	/**
     * Checks the Users table for the given username. 
     * @param username The username to search for.
     * @return True if the username exists. Otherwise, false.
     */
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

	/**
     * Checks if the given username exists in the Users table, then inserts a 
     * new username and password into the table, if the given username is not already within it. 
     * @param username The username to search for and add.
     * @param password The password to add with the given username.
     */
	public void insertUser(String userName, String password) {
		if (usernameExist(userName)) return;

		String sql = "INSERT INTO Users (Username, Password) VALUES (?, ?);";
		try {
			PreparedStatement pstmt = connection.prepareStatement(sql);
			pstmt.setString(1, userName);
			pstmt.setString(2, password);
			pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Attempts to get the user ID associated with the given user from the Users table.
	 * @param userName The username to retrieve the ID for.
	 * @return The retrieved user ID from the Users table. If the username is not in 
	 * the Users table or an SQL exception, then this method returns -1.
	 */
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
	
	/**
	 * Attempts to get the user password associated with the given user from the Users table.
	 * @param userName The username to retrieve the password for.
	 * @return The retrieved user password from the Users table. If the username is not in 
	 * the Users table or an SQL exception, then this method returns null.
	 */
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
	
	
	/**
	 * Attempts to get the user bank balance associated with the given user from the Banks table.
	 * @param userName The username to use for retrieving their bank balance.
	 * @return The retrieved user's bank balance from the Banks table. If the username is not 
	 * in the Users table or an SQL exception, then this method returns -1.0.
	 */
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
	
	/**
	 * Sets the bank balance associated with the given user in the Banks table. 
	 * @param userName The username of the user whose balance will be adjusted.
	 * @param amount The amount to set in the bank balance.
	 */
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

	/**
	 * Inserts a new bank relation for the given user with the specified amount.
	 * @param userName The username of the user that will receive the new bank account.
	 * @param amount The initial amount to place in the bank account.
	 */
	public void insertBank(String userName, Double amount) {		
		int userID = getUserID(userName);
		if (userID == -1) return;

		String sql = "INSERT INTO Banks (User_ID, Balance) VALUES (" + userID + ", " + amount + ")";
		try {			
			Statement stmt = connection.createStatement();
			stmt.executeUpdate(sql);			
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * Inserts a new winnings relation for the given user with the specified amount.
	 * @param userName The username of the user that will receive winnings.
	 * @param amount The amount of the winnings.
	 * @return
	 */
	public void insertWinnings(String userName, Double amount) {		
		int userID = getUserID(userName);
		if (userID == -1) return;

		String sql = "INSERT INTO Wins (User_ID, Amount) VALUES (" + userID + ", ?)";
		try {			
			PreparedStatement pstmt = connection.prepareStatement(sql);
			pstmt.setDouble(1, amount);
			pstmt.executeUpdate();			
			pstmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Inserts a new losses relation for the given user with the specified amount.
	 * @param userName The username of the user that will receive losses.
	 * @param amount The amount of the losses.
	 * @return
	 */
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
	
	
	/**
	 * Attempts to remove the provided user from the Users table of the database.
	 * @param userName The username of the user to remove.
	 */
	public void removeUser(String userName) {
		if (!usernameExist(userName)) return;

		String sql = "DELETE FROM Users WHERE Username = ?";
		try {
			PreparedStatement pstmt = connection.prepareStatement(sql);
			pstmt.setString(1, userName);
			pstmt.executeUpdate();			
			pstmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * Generates a result set of users that includes their usernames, balance, and 
	 * summed wins and losses and sorts the users in descending order by their balance.
	 * @return An ArrayList of {@code UserStat} instances. Each {@code UserStat} holds 
	 * information about a single row of the result set.
	 */
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
	
	/**
	 * Converts a given result set into an ArrayList of {@code UserStat} instances.
	 * @param rs The result set to convert into an ArrayList.
	 * @return An ArrayList of {@code UserStat} instances.
	 */
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
	
	/**
	 * Resets the wins and losses relations of the given user's username and sets 
	 * their bank balance to the provided amount.
	 * @param userName The username of the user whose information will be changed.
	 * @param bankAmount The amount to set the user's balance to.
	 */
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
	
	/**
     * Releases this Connection object's database and JDBC resources 
     * immediately instead of waiting for them to be automatically released. 
     */
	public void closeConnection() {
		try {
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
