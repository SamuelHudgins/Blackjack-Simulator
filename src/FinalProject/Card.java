package FinalProject;

public class Card {
    private String value; // Could be "2", "3", ..., "10", "J", "Q", "K", "A"
    private String suit;  // Could be "C", "D", "H", "S" for Clubs, Diamonds, Hearts, Spades
    private boolean isRevealed; // New field to track if the card is revealed


    // Constructor
    public Card(String value, String suit) {
        this.value = value;
        this.suit = suit;
        this.isRevealed = false; // Cards start as not revealed by default
    }

    // Getter for the card's value as a string
    public int getValue() {
        if ("A".equals(value)) {
            return 11;
        } else if ("J".equals(value) || "Q".equals(value) || "K".equals(value)) {
            return 10;
        } else {
            return Integer.parseInt(value); // For numbers 2-10
        }
    }


    // Getter for the card's suit
    public String getSuit() {
        return suit;
    }

    // Get the numerical value of the card for game logic
    public int getNumericalValue() {
        if ("JQK".contains(value)) {
            return 10;
        } else if ("A".equals(value)) {
            return 11;
        } else {
            return Integer.parseInt(value); // For "2", "3", ..., "10"
        }
    }

    public void reveal() {
        this.isRevealed = true;
    }
    // Method to hide the card
    public void hide() {
        this.isRevealed = false;
    }
    public boolean isRevealed() {
        return this.isRevealed;
    }
    
    // Check if the card is an Ace
    public boolean isAce() {
        return "A".equals(value);
    }

    // Get the image path for the card 
    public String getImagePath() {
        return "src/FinalProject/cards/" +value+"-"+suit+ ".png";
    }

    
}
