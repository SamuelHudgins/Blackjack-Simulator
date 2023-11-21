package project;


public class Player {
	private static Player instance;
	private String username;
	private int balance;
	private int bet;
	
	private Player(String username, int balance) {
		this.username = username;
		this.balance = 1000;
		bet = 0;
	}
	
	public static void setInstance(String username, int balance) {
		if (instance != null) {
			instance.username = username;
			instance.balance = balance;
			return;
		}
		instance = new Player(username, balance);
	}
	
	public static Player getInstance() {
		if (instance == null) throw new NullPlayerException();
		return instance;
	}
	
	public String getUsername() {
		return username;
	}
	
	public int getBalance() {
		return balance;
	}
	
	public void setBalance(int amount) {
		balance = amount;
	}

	public void adjustBalance(int amount) {
		balance += amount;
	}
	
	public int getBet() {
		return bet;
	}
	
	public void setBet(int amount) {
		bet = amount;
	}
}
