package FinalProject;
import javax.swing.*;
import java.util.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
/**
 * The main GUI for the blackjack app 
 * @author Jose Reyes
 */
public class GUI extends JFrame {
	
	
	
	//buttons 
	JButton hit = new JButton("Hit");
	JButton stay = new JButton("Stay");
	
	//button font
	Font buttonFont = new Font("Arial Black",Font.PLAIN,30);
	
	//background color 
	Color backgroundColor = new Color(53,101,77);
	Color buttonColor = new Color(255,215,0);
	int height = 800;
	int width = 1300;
	
	
	
	public GUI() {
		this.setSize(width,height);
		this.setTitle("BlackJack- Team 4");
		this.setVisible(true);
		this.setResizable(false);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		GameBoard board = new GameBoard();
		this.setContentPane(board);
		this.setLayout(null);
		
		//hit button config
		HitAction hitA = new HitAction();
		hit.addActionListener(hitA);
		hit.setBounds(100,520,120,80);
		hit.setBackground(buttonColor);
		hit.setFont(buttonFont);
		board.add(hit);
		
		// stay button config
		StayAction stayA = new StayAction();
		stay.addActionListener(stayA);
		stay.setBounds(250,520,120,80);
		stay.setBackground(buttonColor);
		stay.setFont(buttonFont);
		board.add(stay);
		
	}

	
	public class GameBoard extends JPanel{
		
		public void paintComponent( Graphics g) {
			g.setColor(backgroundColor);
			g.fillRect(EXIT_ON_CLOSE, DISPOSE_ON_CLOSE, width, height);
			
			//Lines for dealer's space
			g.setColor(Color.WHITE);
		    g.drawRoundRect(100, 50, 1100, 150, 50, 50);
		    
		    //Lines for player's space 
		    g.drawRoundRect(100, 600, 1100, 150, 50, 50);
		}
		
	}
	public class HitAction implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			System.out.println("Hit Button works");
			
		}
		
	}
	public class StayAction implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			System.out.println("Stay Button works");
			
		}
		
	}
}
