package project;

/**
 * This class is used by the {@code DatabaseManager} class to store result set row information 
 * and by the {@code StatsScene} class when displaying the row information in the stats screen.
 */
public class UserStat {
	
	private String rank;
	private String name;
	private String balance;
	private String winnings;
	private String losses;

	public UserStat(String rank, String name, String balance, String winnings, String losses) {
		this.rank = rank;
		this.name = name;
		this.balance = balance;
		this.winnings = winnings;
		this.losses = losses;
	}
	
	public String getRank() {
		return rank;
	}

	public String getName() {
		return name;
	}

	public String getBalance() {
		return "$"+ balance;
	}

	public String getWinnings() {
		return "$"+ winnings;
	}

	public String getLosses() {
		return "$"+ losses;
	}
}
