

import test1.GUI;
import test1.Main;

public class Main implements Runnable {
	
	GUI gui = new GUI();
	
	public static void main(String[] args) {
		new Thread( new Main() ).start();
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}

}
