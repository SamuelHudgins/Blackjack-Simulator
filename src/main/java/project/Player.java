package project;


public class Player {
	private static Player instance;
	private String username;
	private boolean guest;
	public static final Double START_BALANCE = 1000.0;
	private double balance;
	private double bet;
	
	private Player(String username, double balance) {
		this.username = username;
		this.balance = balance;
		guest = username.isBlank();
		bet = 0;
	}
	
	public static void setInstance(double balance) {
		setInstance("", balance);
	}
	
	public static void setInstance(String username, double balance) {
		if (instance != null) {
			instance.username = username;
			instance.balance = balance;
			instance.guest = username.isBlank();
			return;
		}
		instance = new Player(username, balance);
	}
	
	public static Player getInstance() {
		if (instance == null) throw new NullPlayerException();
		return instance;
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
