package project;

import java.sql.*;
import java.text.MessageFormat;

/**
 * <p>The object used for creating and managing a SQLite database. 
 * This class focuses on managing a single table per {@code DBTable} 
 * instance, so the available methods operate on a single table.
 */
public class DatabaseManager {

	private final String DEFAULT_URL;
	private Connection connection;
	private final String TABLE_NAME;
	private static enum Columns {
		Username,
		Password,
		Rank,
		Wins,
		Losses;
	}
	
	/**
	 * <p>Attempts to establish a connection to the given database and creates 
	 * a new table. This should also create a new database file in the current 
	 * directory the application is running in, where the new table resides.
	 * @param tableName the name of the new table and resulting file.
     */
	public DatabaseManager(String tableName) {
		TABLE_NAME = tableName;
		DEFAULT_URL = String.format("jdbc:sqlite:%s.db", TABLE_NAME);
		try {
			connection = DriverManager.getConnection(DEFAULT_URL);
			String sql = MessageFormat.format(""
					+ "CREATE TABLE IF NOT EXISTS {0} ("
						+ "{1} TEXT NOT NULL UNIQUE, "
						+ "{2} TEXT NOT NULL, "
						+ "{3} TEXT, "
						+ "{4} INTEGER, "
						+ "{5} INTEGER"
					+ ")", TABLE_NAME, Columns.Username, Columns.Password, 
					Columns.Rank, Columns.Wins, Columns.Losses);
			try {
				Statement stmt = connection.createStatement();
				stmt.execute(sql);
				stmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		} catch (SQLException e) {
			System.err.println(e.getMessage());
		}
	}
	
	/**
     * <p>Releases this Connection object's database and JDBC resources 
     * immediately instead of waiting for them to be automatically released. 
     */
	public void closeConnection() {
		try {
			connection.close();
		} catch (SQLException e) {
			System.err.println(e.getMessage());
		}
	}
	
	public void deleteTable() {
		String sql = "DROP TABLE IF EXISTS " + TABLE_NAME;
		try {
			Statement stmt = connection.createStatement();
			stmt.execute(sql);
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}		
	}
	
	public ResultSet getRecord(String username) {
		String sql = MessageFormat.format("SELECT * FROM {0} WHERE {1} is \"{2}\"",
				TABLE_NAME, Columns.Username, username);
		try {
			Statement stmt = connection.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			return rs;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}		
	}
	
	public ResultSet getAllRecords() {
		String sql = MessageFormat.format("SELECT * FROM {0}", TABLE_NAME);
		try {
			Statement stmt = connection.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			return rs;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	private boolean recordExist(String username) {
		try (ResultSet rs = getRecord(username);) {
			if (rs.getObject(1) != null) {
				return true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public boolean insertUser(String userName, String password) {		
		if (recordExist(userName)) {
			System.out.println("The user \'" + userName + "\' is already in the database.");
			return false;
		}
		String sql = MessageFormat.format("INSERT INTO {0} ({1}, {2}) VALUES (?, ?)", 
				TABLE_NAME, Columns.Username, Columns.Password);
		try {
			PreparedStatement stmt = connection.prepareStatement(sql);
			stmt.setString(1, userName);
			stmt.setString(2, password);
			stmt.executeUpdate();
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}		
		return true;
	}
	
	public void updateUserRank(String userName, String value) {
		updateUser(userName, Columns.Rank, value);	
	}
	
	public void updateUserWins(String userName, int value) {
		updateUser(userName, Columns.Wins, value);	
	}
	
	public void updateUserLosses(String userName, int value) {
		updateUser(userName, Columns.Losses, value);	
	}
	
	private <T> void updateUser(String userName, Columns column, T value) {
		String sql = MessageFormat.format("UPDATE {0} SET {1} = \"{2}\" WHERE {3} = \"{4}\"", 
				TABLE_NAME, column, value, Columns.Username, userName);
		try {
			Statement stmt = connection.createStatement();
			stmt.executeUpdate(sql);
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}		
	}
	
	public boolean deleteUser(String userName) {
		if (!recordExist(userName)) {
			System.out.println("The user \'" + userName + "\' is not in the database.");
			return false;
		}
		String sql = MessageFormat.format("DELETE FROM {0} WHERE {1} = \"{2}\"", 
				TABLE_NAME, Columns.Username, userName);
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
	
	public static void displayResultSet(ResultSet rs) {
		try {
			ResultSetMetaData rsmd = rs.getMetaData();
			while (rs.next()) {
				for (int i = 1; i <= rsmd.getColumnCount(); i++)
					System.out.print(rsmd.getColumnName(i) + "[" + rs.getObject(rsmd.getColumnName(i)) + "]"
							+ (rs.getObject(rsmd.getColumnName(i)) != null ? "(" + rs.getObject(rsmd.getColumnName(i)).getClass().getSimpleName() + ") " : " "));
				System.out.println();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
