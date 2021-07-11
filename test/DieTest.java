/* Test file to test functionality of the Die.java class.*/

import acm.graphics.GObject;
import acm.graphics.GPoint;
import acm.program.GraphicsProgram;
import java.awt.Color;
import java.awt.event.*;

public class DieTest extends GraphicsProgram {


	/**
	 * @param args
	 */
	public void run() {
		
		addMouseListeners();
		
		setSize (APPLICATION_WIDTH,APPLICATION_HEIGHT);	// Set initial screen size, does not prevent later resize
		setBackground(greenFelt);
		
		System.out.println("Hello World!");
		
		// Test with a single die
//		Die testDie = new Die(50,50);
//		add (testDie);
//		testDie.rollDice();
		
		// test with an array of dice
		for (int i = 0; i < testDice.length; i++) {
			ColorScheme_Dice tempColorScheme;
			if (i%2 == 0) {
				tempColorScheme = ColorScheme_Dice.red_White;
			}
			else {
				tempColorScheme = ColorScheme_Dice.white_Red;
			}
			testDice[i] = new Die (50, (i+1)*50, tempColorScheme);
			add (testDice[i]);
			testDice[i].rollDice();
			
			System.out.println("Die " + i + ": " + testDice[i].getValue());
		}
		
		pause(2000);
		
//		testDice[0] = testDieA;
//		testDice[1] = testDieB;
//		testDice[2] = testDieC;
//		testDice[3] = testDieD;
//		testDice[4] = testDieE;
//		
		for (int i = 0; i < testDice.length; i++) {
			add (testDice[i]);
			testDice[i].rollDice();
		}
		
		pause(1000);
		
		for (int i = 0; i < testDice.length; i++) {
			testDice[i].rollDice();
			pause(1000);
		}
	}
	
	/* When mouse button is pressed, determine the object being selected*/
	public void mousePressed(MouseEvent e) {
		last = new GPoint (e.getPoint());
		mouseObject = getElementAt(last);
	}
	
	
	/* Do not currently plan to use a click event.  I thought this might be a good way to update all of the dice.*/
	public void mouseClicked(MouseEvent e) {
		for (int i = 0; i < testDice.length; i++) {
			if(mouseObject.equals(testDice[i]) ) {
				testDice[i].rollDice();
				System.out.println("Die " + i + ": " + testDice[i].getValue());
			}
		}
		
	}
	
	/* Handle the event where a die is dragged by the mouse*/
	public void mouseDragged(MouseEvent e) {
		if (mouseObject != null) {
			mouseObject.move(e.getX() - last.getX(), e.getY() - last.getY());
			last = new GPoint(e.getPoint());
			
			/* move die to the front so the die being moved is in front of the other dice.*/
			mouseObject.sendToFront();
		}
	}
	
	
	// Constants
	private static final int APPLICATION_HEIGHT = 700;
	private static final int APPLICATION_WIDTH = 400;
	
	private Die[] testDice = new Die[5];
//	private static Die testDieA = new Die(50,50);
//	private static Die testDieB = new Die(50,100);
//	private static Die testDieC = new Die(50,150);
//	private static Die testDieD = new Die(50,200);
//	private static Die testDieE = new Die(50,250);
	
	
	private GObject mouseObject;
	private GPoint last;
	
	/* Background colors - these will eventually be used in the real program to color
	 * the felt for the dice compartments */
	Color greenFelt = new Color(0,90,0);
	
}