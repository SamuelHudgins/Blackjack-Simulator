package project;

/**
 * Holds information about the current {@code Player} instance, including their 
 * username, current balance, and bet. A user may be logged into the application 
 * or playing as a guest, and there can only ever be one {@code Player} instance 
 * in the application.
 */
public class Player {
	private static Player instance;
	private String username;
	private boolean guest;
	public static final Double START_BALANCE = 1000.0;
	private double balance;
	private double bet;
	
	/**
	 * The private constructor of {@code Player} prevents outside classes from 
	 * instantiating copies of a player.
	 */
	private Player(String username, double balance) {
		this.username = username;
		this.balance = balance;
		guest = username.isBlank();
		bet = 0;
	}
	
	public static void setInstance(double balance) {
		setInstance("", balance);
	}
	
	/**
	 * Sets a new {@code Player} instance with the provided username and balance or 
	 * updates the current {@code Player} instance with the provided username and balance.
	 * @param username The userame to assign to the player.
	 * @param balance The balance to assign to this player.
	 */
	public static void setInstance(String username, double balance) {
		if (instance != null) {
			instance.username = username;
			instance.balance = balance;
			instance.guest = username.isBlank();
			return;
		}
		instance = new Player(username, balance);
	}
	
	/**
	 * Gets the {@code Player} instance.
	 * @return The current {@code Player} instance. If one does not exist, a 
	 * {@code NullPlayerException} occurs to prevent the application from failing 
	 * later in the event a null {@code Player} instance does not immediately result 
	 * in an exception.
	 */
	public static Player getInstance() {
		if (instance == null) throw new NullPlayerException();
		return instance;
	}
	
	/**
	 * Check whether a {@code Player} instance is set.
	 * @return True if the {@code Player} instance is not null. Otherwise, false.
	 */
	public static boolean exists() {
		return instance != null;
	}
	
	public static void remove() {
		instance = null;
	}
	
	public String getUsername() {
		return username;
	}
	
	public boolean getGuest() {
		return guest;
	}
	
	public double getBalance() {
		return balance;
	}
	
	public void setBalance(double amount) {
		balance = amount;
	}

	public void adjustBalance(double amount) {
		balance += amount;
	}
	
	public double getBet() {
		return bet;
	}
	
	public void setBet(int amount) {
		bet = amount;
	}
}
