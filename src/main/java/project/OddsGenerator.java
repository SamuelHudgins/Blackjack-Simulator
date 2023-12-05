package project;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * This class contains methods for determining the best choices of actions during 
 * blackjack matches.
 */
public class OddsGenerator {
	
	private static OddsGenerator instance;
	private final String CSV_PATH = "/blackjackStrategies/strategy/";

	// The keys in these maps represent hand sums or card values, while the values 
	// represent a list of best choices.
	private Map<Integer, ArrayList<String>> hardTotal;
	private Map<Integer, ArrayList<String>> softTotal;
	private Map<Integer, ArrayList<String>> pairSplitting;

	/**
	 * Gets the current {@code OddsGenerator} instance.
	 * @return The current {@code OddsGenerator} instance, or a new one 
	 * if one does not exist. 
	 *
	 */
	public static OddsGenerator getInstance() {
		if (instance == null) {
			instance = new OddsGenerator();
			instance.setUpStrategy();
		}
		return instance;
	}
	
	
	/**
	 * Sets up the strategy mappings for hard and soft totals and pair splitting, which 
	 * this class uses for determining optimal decisions during blackjack matches.
	 */
	private void setUpStrategy() {
		// These strategy tables and their values are based on the ones provided on the 
		// Blackjack Apprenticeship website here: 
		//	https://www.blackjackapprenticeship.com/blackjack-strategy-charts/
		hardTotal = getCSVTableData("Hard Totals.csv");
		softTotal = getCSVTableData("Soft Totals.csv");
		pairSplitting = getCSVTableData("Pair Splitting.csv");
	}
		
	/**
	 * Searches for a .csv file containing blackjack strategy data, then reads and returns 
	 * its values as a data mapping.
	 * @param fileName The name of the .csv file to scan.
	 * @return A HashMap with Integer keys and ArrayList of Strings as values.
	 */
	private HashMap<Integer, ArrayList<String>> getCSVTableData(String fileName) {
		HashMap<Integer, ArrayList<String>> choiceMap = new HashMap<Integer, ArrayList<String>>();
		InputStream in = this.getClass().getResourceAsStream(CSV_PATH + fileName);
		try (Scanner scanner = new Scanner(in)) {
			boolean firstline = true;
			while (scanner.hasNextLine()) {
				ArrayList<String> values = getCSVRowData(scanner.nextLine());
				if (firstline) {  // Skips the dealer up card header lines in files.
					firstline = false;
					continue;
				}
				int cardValue = Integer.parseInt(values.get(0));
				choiceMap.put(cardValue, new ArrayList<String>(values.subList(1, values.size())));
			}
			return choiceMap;
		}
	}
	
	/**
	 * Scans a .csv file's row and returns a String ArrayList of its values.
	 * @param line The line to extract data from.
	 */
	private ArrayList<String> getCSVRowData(String line) {
		ArrayList<String> values = new ArrayList<String>();
		try (Scanner rowScanner = new Scanner(line)) {
			while (rowScanner.hasNext()) {
				values.add(rowScanner.next());
			}
		}
		return values;
	}
	
	/**
	 * Given a player and dealer's hand, this method uses the set hard and soft totals and pair splitting 
	 * strategy to return the best choice (hit, stand, double, or split) for a player to take.
	 */
	public BestChoice getBestChoice(Hand playerHand, Hand dealerHand) {
		ArrayList<String> dealerUpCards = new ArrayList<String>( 
				Arrays.asList("2", "3", "4", "5", "6", "7", "8", "9", "10", "A")
		);
		String upCard = dealerHand.getFaceUpCard().getValue();
		if ("JQK".contains(upCard)) upCard = "10";
		int index = dealerUpCards.indexOf(upCard);
		
		ArrayList<String> options;
		String choice;
		
		Card firstCard = playerHand.getCards().get(0);
		if (playerHand.splittable() && firstCard.getFaceValue() != 10) {
			if (playerHand.hasAce()) return BestChoice.Split;
			options = instance.pairSplitting.get(firstCard.getFaceValue());
		}		
		
		int sum = playerHand.getHandValue();
		if (playerHand.hasAce()) {
			options = instance.softTotal.get(sum);
		}
		else {
			if (sum <= 8) return BestChoice.Hit;
			if (sum >= 17) return BestChoice.Stand;
			options = instance.hardTotal.get(sum);
		}
		
		choice = options.get(index);
		if (choice.equals("Y")) return BestChoice.Split;
		if (choice.equals("H")) return BestChoice.Hit;
		if (choice.equals("D")) return BestChoice.Double;
		if (choice.equals("S")) return BestChoice.Stand;		
		return null;
	}
}
