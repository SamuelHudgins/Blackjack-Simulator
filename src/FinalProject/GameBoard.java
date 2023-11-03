package FinalProject;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class GameBoard extends JPanel {
  /**
   *
   */
  private static final long serialVersionUID = 1L;
  private ArrayList<Card> dealerHand;
  private ArrayList<Card> playerHand;
  private Card hiddenCard ;
  private int boardWidth = 1300;
  private int boardHeight = 600;
  private int cardWidth = 110;
  private int cardHeight = 154;
  private int dealerSum;
  private int playerSum;



  public GameBoard(ArrayList<Card> dealerHand, ArrayList<Card> playerHand, Card hiddenCard, int dealerSum, int playerSum) {
    this.dealerHand = dealerHand;
    this.playerHand = playerHand;
    this.hiddenCard = hiddenCard;
    this.dealerSum = dealerSum;
    this.playerSum = playerSum;

  }

  @Override
  public void paintComponent(Graphics g) {
    super.paintComponent(g);

    // Draw background
    g.setColor(new Color(53, 101, 77));
    g.fillRect(0, 0, boardWidth, boardHeight);

    // Draw Dealer's Area
    g.setColor(Color.WHITE);
    g.drawRect(10, 10, boardWidth - 20, 200);



    // Draw Dealer's hand
    int xOffset = 20; // Starting x position for the first card
    for (int i = 0; i < dealerHand.size(); i++) {
      // Conditionally draw dealer's hidden card or revealed card
     if (!dealerHand.contains(hiddenCard)) {
        // Draw hidden card
        Image hiddenCardImg = new ImageIcon("src/FinalProject/cards/BACK.png").getImage();
        g.drawImage(hiddenCardImg, 20, 20, cardWidth, cardHeight, null);
      }
      Card card = dealerHand.get(i);
      Image cardImg;


      // Draw the card face next to the back image if it's the hidden card, or just draw the card face if it's not
      cardImg = new ImageIcon(card.getImagePath()).getImage();
      g.drawImage(cardImg, xOffset, 50, cardWidth, cardHeight, null);
      // Increment the offset for the next card
      xOffset += cardWidth + 5;
    }


    // Draw Player's Area
    g.setColor(Color.WHITE);
    g.drawRect(10, 300, boardWidth - 20, 200);

    // Draw player's hand
    xOffset = 20; // Reset offset for player's cards
    for (Card card : playerHand) {
      Image cardImg = new ImageIcon(card.getImagePath()).getImage();
      g.drawImage(cardImg, xOffset, 320, cardWidth, cardHeight, null);
      xOffset += cardWidth + 5; // Increment the offset for the next card
    }

    // Draw the sums
    g.drawString("Dealer's Sum: " + dealerSum, 20, 250);
    g.drawString("Player's Sum: " + playerSum, 20, 280);
    repaint();
  }


  // Setter for dealerSum
  public void setDealerSum(int sum) {
    dealerSum = sum;
    repaint(); // Repaint the board to show the updated sum
  }

  // Setter for playerSum
  public void setPlayerSum(int sum) {
    playerSum = sum;
    repaint(); // Repaint the board to show the updated sum
  }


}

