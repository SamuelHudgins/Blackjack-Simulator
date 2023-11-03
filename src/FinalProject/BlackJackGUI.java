package FinalProject;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;

public class BlackJackGUI extends JFrame {
  /**
   *
   */
  private static final long serialVersionUID = 1L;
  // Existing fields from BlackJack
  ArrayList<Card> deck;
  Random random = new Random();
  Card hiddenCard;
  ArrayList<Card> dealerHand;
  int dealerSum;
  int dealerAceCount;
  ArrayList<Card> playerHand;
  int playerSum;
  int playerAceCount;

  // GUI specific fields
  JButton hit = new JButton("Hit");
  JButton stay = new JButton("Stay");
  Font buttonFont = new Font("Arial Black", Font.PLAIN, 30);
  Color backgroundColor = new Color(53, 101, 77);
  Color buttonColor = new Color(255, 215, 0);

  public BlackJackGUI() {
    // Initialize game logic
    startGame();

    // GUI setup
    this.setSize(1300, 640);
    this.setTitle("BlackJack");
    this.setVisible(true);
    this.setResizable(false);
    this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    GameBoard board = new GameBoard(dealerHand, playerHand, hiddenCard, dealerSum, playerSum);
    this.setContentPane(board);
    this.setLayout(null);

    // Hit button config
    HitAction hitA = new HitAction(board);
    hit.addActionListener(hitA);
    hit.setBounds(100, 520, 120, 80);
    hit.setBackground(buttonColor);
    hit.setFont(buttonFont);
    board.add(hit);

    // Stay button config
    StayAction stayA = new StayAction(board);
    stay.addActionListener(stayA);
    stay.setBounds(250, 520, 120, 80);
    stay.setBackground(buttonColor);
    stay.setFont(buttonFont);
    board.add(stay);

    //setters for the screen sum
    board.setDealerSum(dealerSum);
    board.setPlayerSum(playerSum);
    board.repaint();


  }

  public void startGame() {
    // Initialize deck and shuffle
    buildDeck();
    shuffleDeck();

    // Initialize dealer's hand, sum, and Ace count
    dealerHand = new ArrayList<>();
    dealerSum = 0;
    dealerAceCount = 0;

    // Deal one hidden card to the dealer
    hiddenCard = deck.remove(deck.size() - 1);
    // Do not add the hidden card's value to the dealer's sum yet
    dealerAceCount += hiddenCard.isAce() ? 1 : 0;

    // Deal one visible card to the dealer
    Card dealerCard = deck.remove(deck.size() - 1);
    dealerSum += dealerCard.getValue();
    dealerAceCount += dealerCard.isAce() ? 1 : 0;
    dealerHand.add(dealerCard);

    // Initialize player's hand, sum, and Ace count
    playerHand = new ArrayList<>();
    playerSum = 0;
    playerAceCount = 0;

    // Deal two cards to the player
    for (int i = 0; i < 2; i++) {
      Card playerCard = deck.remove(deck.size() - 1);
      playerSum += playerCard.getValue();
      playerAceCount += playerCard.isAce() ? 1 : 0;
      playerHand.add(playerCard);
    }

    // Enable hit and stay buttons for the new game
    hit.setEnabled(true);
    stay.setEnabled(true);

    // Repaint the board to reflect the new game state
    repaint();
  }
  public void buildDeck() {
    deck = new ArrayList<>();
    String[] values = {"A", "2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K"};
    String[] types = {"C", "D", "H", "S"};
    for (String type : types) {
      for (String value : values) {
        deck.add(new Card(value, type));
      }
    }
  }

  public void shuffleDeck() {
    for (int i = 0; i < deck.size(); i++) {
      int j = random.nextInt(deck.size());
      Card temp = deck.get(i);
      deck.set(i, deck.get(j));
      deck.set(j, temp);
    }
  }

  public int reducePlayerAce() {
    while (playerSum > 21 && playerAceCount > 0) {
      playerSum -= 10;
      playerAceCount--;
    }
    return playerSum;
  }

  public int reduceDealerAce() {
    while (dealerSum > 21 && dealerAceCount > 0) {
      dealerSum -= 10;
      dealerAceCount--;
    }
    return dealerSum;
  }
  public class HitAction implements ActionListener {
    private GameBoard gameBoard; // Reference to the GameBoard

    // Constructor to pass the GameBoard reference
    public HitAction(GameBoard gameBoard) {
      this.gameBoard = gameBoard;
    }
    @Override
    public void actionPerformed(ActionEvent e) {
      // Draw a card from the deck
      Card card = deck.remove(deck.size() - 1);
      playerHand.add(card);
      playerSum += card.getValue();
      playerAceCount += card.isAce() ? 1 : 0;
      repaint();

      // Adjust for Aces if the sum is over 21
      playerSum = reducePlayerAce();
      gameBoard.setPlayerSum(playerSum);
      repaint(); // Repaint after each card is drawn


      // Check if player has busted
      if (playerSum > 21) {
        hit.setEnabled(false); // Disable hit button if player has busted
        stay.setEnabled(false); // Disable stay button if player has busted

        // Show message that player has busted
        JOptionPane.showMessageDialog(null, "You've busted!");
      }
      if (playerSum == 21) {
        hit.setEnabled(false); // Disable hit button if player has busted
        stay.setEnabled(false); // Disable stay button if player has busted

        // Show message that player has busted
        JOptionPane.showMessageDialog(null, "BlackJack You Win!");
      }


      repaint(); // Repaint the GUI to show the new card and updated sum
    }
  }


  public class StayAction implements ActionListener {
    private GameBoard gameBoard; // Reference to the GameBoard

    // Constructor to pass the GameBoard reference
    public StayAction(GameBoard gameBoard) {
      this.gameBoard = gameBoard;
    }
    @Override
    public void actionPerformed(ActionEvent e) {
      // Remove the hidden card from the dealer's hand
      dealerHand.remove(hiddenCard);

      // Reveal the hidden card and update the dealer's hand
      dealerHand.add(0, hiddenCard);
      // Now update the sum with the hidden card's value
      dealerSum += hiddenCard.getValue();
      dealerAceCount += hiddenCard.isAce() ? 1 : 0;
      dealerSum = reduceDealerAce(); // Adjust for Aces if necessary

      // Disable the hit and stay buttons
      hit.setEnabled(false);
      stay.setEnabled(false);


      // Delay for the dealer's turn
      Timer timer = new Timer(1000, new ActionListener() {
        public void actionPerformed(ActionEvent evt) {
          // Dealer draws cards until the sum is 17 or higher
          while (dealerSum < 17) {
            Card card = deck.remove(deck.size() - 1);
            dealerHand.add(card);
            dealerSum += card.getValue();
            dealerAceCount += card.isAce() ? 1 : 0;
            dealerSum = reduceDealerAce(); // Adjust for Aces
            //update dealer sum
            gameBoard.setDealerSum(dealerSum);
            repaint(); // Repaint after each card is drawn
          }

          // Determine the outcome of the game
          String message;
          if (dealerSum > 21) {
            message = "Dealer busts, you win!";
          } else if (playerSum > dealerSum) {
            message = "You win!";
          } else if (playerSum == dealerSum) {
            message = "It's a tie!";
          } else {
            message = "Dealer wins!";
          }
          if (dealerSum == 21) {
            message = "Dealer Blackjack, you lose!";
          }
          // Show the result after the dealer's turn is over
          JOptionPane.showMessageDialog(null, message);
        }
      });
      timer.setRepeats(false); // Make sure the action is only performed once
      timer.start();
    }
  }


}


