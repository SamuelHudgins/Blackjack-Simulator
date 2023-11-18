package project;

public class Player {
	public static Player instance;
	private String username;
	private int balance;
	
	public static void setPlayerInstance(String username) {
		if (instance != null) return;
		instance = new Player(username);
	}
	
	public static Player getPlayerInstance() {
		return instance;
	}
	
	public Player(String username) {
		this.username = username;
		this.balance = 1000;
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
}
