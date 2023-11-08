import java.util.Scanner;
import java.util.*;
 
public class blackjacklogic {
 public static void main(String[] args) {
      
     String anotherCard, playAgain = "y", ctn = null;
     int nextCard, card1, card2, dCard1, dCard2;
     int cardTotal = 0, dTotal = 0;
   
     Scanner keyboard = new Scanner(System.in);
      
     Random random = new Random();
      
     // Begin dealing the players first two cards
      
     while ("y".equals(playAgain))
     {
         //dealers first two random cards
         dCard1 = random.nextInt(10) + 1;
         dCard2 = random.nextInt(10) + 1;
          
         //players first two random cards and card total
         card1 = random.nextInt(10) + 1;
         card2 = random.nextInt(10) + 1;
         cardTotal = card1 + card2;
          
         //dealers two card total and display only one dealer card
         dTotal = dCard1 + dCard2;
         System.out.println("Dealer shows: " + dCard1);
          
         //display players first two cards & card total
         System.out.println("First Cards: " + card1 + ", " + card2);
         System.out.println("Total: "+ cardTotal);
          
         System.out.println("Another Card (y/n)?: ");
         anotherCard = keyboard.nextLine();
          
         while ("y".equals(anotherCard))
         {
             nextCard = random.nextInt(10) + 1;
             cardTotal += nextCard;
             System.out.println("Card: " + nextCard);
             System.out.println("Total: " + cardTotal);
              
             if (cardTotal > 21)
             {
             System.out.println("You busted, Dealer wins");
             System.out.println("Do you want to play again? (y/n):");
             playAgain = keyboard.nextLine();
             }
              
             if (cardTotal < 21)
                  
            System.out.println("Another Card (y/n)?: ");
             anotherCard = keyboard.nextLine();
            if ("n".equals(anotherCard))
             System.out.println("Dealer had: " + dTotal);
             System.out.println("You had: " + cardTotal);
              
             while ("n".equals(anotherCard))
             {
                    if (dTotal < cardTotal && cardTotal < 21)
                    {
                     System.out.println("Yay you win!");
                     System.out.println("Play again? (y/n): ");
                     playAgain = keyboard.nextLine();
                     if (playAgain.equalsIgnoreCase("y"))
                         playAgain = "y";
                      
                     if (dTotal > cardTotal && dTotal < 21)
                         System.out.println("You lose!");
                         playAgain = keyboard.nextLine();
                     }   
                }
            }
        }
    }
}