import java.awt.Color;

import acm.graphics.GCompound;
import acm.graphics.GOval;
import acm.graphics.GRoundRect;
import acm.util.RandomGenerator;


public class Die extends GCompound {

	/* Basic constructor using the default color scheme*/
	public Die(double x, double y) {
		
		block = new GRoundRect (x, y, DIE_SIZE, DIE_SIZE, DIE_CORNER);
		add (block);
		dieBorder = new GRoundRect (x, y, DIE_SIZE, DIE_SIZE, DIE_CORNER);
		add (dieBorder);
		
		for(int i=0; i<N_PIPS; i++) {
			pip[i] = new GOval (x+i*PIP_SIZE, 2*y+i*PIP_SIZE, PIP_SIZE, PIP_SIZE);
			pip[i].setVisible(false);
			add (pip[i]);
		}
		setColorScheme (ColorScheme_Dice.white_Black);
	}
	
	/* Constructor that allows definition of the color scheme at creation*/
	public Die(double x, double y, ColorScheme_Dice colorScheme) {
		this (x, y);
		setColorScheme (colorScheme);
	}
		
	/* Subroutine to randomly select a new die value and cause it to be displayed.  Return the value of the new roll*/
	public int rollDice() {
		// randomly select the new value
		value = rgen.nextInt(1,N_SIDES);
		
		// Display the correct pips
		displayValue (value);
				
		// return new value
		return (value);
	}
	
	public int getValue() {
		return value;
	}
	
//	/* Routine for setting the center point for the die.*/
//	public void setCenter(double x, double y) {
//		block.setLocation( x - DIE_SIZE/2, y - DIE_SIZE/2);
//	}
	
	/* Routine for setting the color scheme using the colors from the DiceColorScheme enum.
	 * Make White Dice w/ Black Pips the default */
	public void setColorScheme(ColorScheme_Dice colorScheme) {
		switch (colorScheme) {
			case red_White:
				block.setColor(Color.red);
				block.setFilled(true);
				block.setFillColor(Color.red);
				dieBorder.setColor(Color.white);
				for (int i = 0; i < pip.length; i++) {
					pip[i].setColor(Color.white);
					pip[i].setFilled(true);
					pip[i].setFillColor(Color.white);
				}
				break;
	
			case white_Red:
				block.setColor(Color.white);
				block.setFilled(true);
				block.setFillColor(Color.white);
				dieBorder.setColor(Color.red);
				for (int i = 0; i < pip.length; i++) {
					pip[i].setColor(Color.red);
					pip[i].setFilled(true);
					pip[i].setFillColor(Color.red);
				}
				break;
				
			case orange_Blue:
				block.setColor(Color.orange);
				block.setFilled(true);
				block.setFillColor(Color.orange);
				dieBorder.setColor(Color.blue);
				for (int i = 0; i < pip.length; i++) {
					pip[i].setColor(Color.blue);
					pip[i].setFilled(true);
					pip[i].setFillColor(Color.blue);
				}
				break;
			
			case blue_Orange:
				block.setColor(Color.blue);
				block.setFilled(true);
				block.setFillColor(Color.blue);
				dieBorder.setColor(Color.orange);
				for (int i = 0; i < pip.length; i++) {
					pip[i].setColor(Color.orange);
					pip[i].setFilled(true);
					pip[i].setFillColor(Color.orange);
				}
				break;
				
			case yellow_Black:
				block.setColor(Color.yellow);
				block.setFilled(true);
				block.setFillColor(Color.yellow);
				dieBorder.setColor(Color.black);
				for (int i = 0; i < pip.length; i++) {
					pip[i].setColor(Color.black);
					pip[i].setFilled(true);
					pip[i].setFillColor(Color.black);
				}
				break;
			
			case black_Yellow:
				block.setColor(Color.black);
				block.setFilled(true);
				block.setFillColor(Color.black);
				dieBorder.setColor(Color.yellow);
				for (int i = 0; i < pip.length; i++) {
					pip[i].setColor(Color.yellow);
					pip[i].setFilled(true);
					pip[i].setFillColor(Color.yellow);
				}
				break;
				
			case green_Black:
				block.setColor(Color.green);
				block.setFilled(true);
				block.setFillColor(Color.green);
				dieBorder.setColor(Color.black);
				for (int i = 0; i < pip.length; i++) {
					pip[i].setColor(Color.black);
					pip[i].setFilled(true);
					pip[i].setFillColor(Color.black);
				}
				break;
	
			case black_Green:
				block.setColor(Color.black);
				block.setFilled(true);
				block.setFillColor(Color.black);
				dieBorder.setColor(Color.green);
				for (int i = 0; i < pip.length; i++) {
					pip[i].setColor(Color.green);
					pip[i].setFilled(true);
					pip[i].setFillColor(Color.green);
				}
				break;
	
			case blue_White:
				block.setColor(Color.blue);
				block.setFilled(true);
				block.setFillColor(Color.blue);
				dieBorder.setColor(Color.blue);
				for (int i = 0; i < pip.length; i++) {
					pip[i].setColor(Color.white);
					pip[i].setFilled(true);
					pip[i].setFillColor(Color.white);
				}
				break;
	
			case white_Blue:
				block.setColor(Color.white);
				block.setFilled(true);
				block.setFillColor(Color.white);
				dieBorder.setColor(Color.blue);
				for (int i = 0; i < pip.length; i++) {
					pip[i].setColor(Color.blue);
					pip[i].setFilled(true);
					pip[i].setFillColor(Color.blue);
				}
				break;
	
			case purple_White:
				block.setColor(PURPLE);
				block.setFilled(true);
				block.setFillColor(PURPLE);
				dieBorder.setColor(Color.white);
				for (int i = 0; i < pip.length; i++) {
					pip[i].setColor(Color.white);
					pip[i].setFilled(true);
					pip[i].setFillColor(Color.white);
				}
				break;
	
			case white_Purple:
				block.setColor(Color.white);
				block.setFilled(true);
				block.setFillColor(Color.white);
				dieBorder.setColor(PURPLE);
				for (int i = 0; i < pip.length; i++) {
					pip[i].setColor(PURPLE);
					pip[i].setFilled(true);
					pip[i].setFillColor(PURPLE);
				}
				break;
	
			case black_White:
				block.setColor(Color.black);
				block.setFilled(true);
				block.setFillColor(Color.black);
				dieBorder.setColor(Color.white);
				for (int i = 0; i < pip.length; i++) {
					pip[i].setColor(Color.white);
					pip[i].setFilled(true);
					pip[i].setFillColor(Color.white);
				}
				break;
				
			case rainbow:
				block.setColor(Color.black);
				block.setFilled(true);
				block.setFillColor(Color.black);
				dieBorder.setColor(Color.pink);
				for (int i = 0; i < pip.length; i++) {
					switch (i) {
					case 0:
						pip[i].setColor(Color.red);
						pip[i].setFillColor(Color.red);
						break;
					case 1:
						pip[i].setColor(Color.orange);
						pip[i].setFillColor(Color.orange);
						break;
					case 2:
						pip[i].setColor(Color.yellow);
						pip[i].setFillColor(Color.yellow);
						break;
					case 3:
						pip[i].setColor(Color.green);
						pip[i].setFillColor(Color.green);
						break;
					case 4:
						pip[i].setColor(Color.blue);
						pip[i].setFillColor(Color.blue);
						break;
					case 5:
						pip[i].setColor(PURPLE);
						pip[i].setFillColor(PURPLE);
						break;
					}
					pip[i].setFilled(true);
				}
				break;
	
			case white_Black:
			default:
				block.setColor(Color.white);
				block.setFilled(true);
				block.setFillColor(Color.white);
				dieBorder.setColor(Color.black);
				for (int i = 0; i < pip.length; i++) {
					pip[i].setColor(Color.black);
					pip[i].setFilled(true);
					pip[i].setFillColor(Color.black);
				}
				break;
				
		}
	}
	
	/* subroutine to correctly place the pips on the die depending on the value.*/
	private void displayValue(int newValue) {
		double x, y;
		
		// Hide previous pips
		hidePips();
				
		switch (newValue) {
		case 1:
			// Add a single pip to the center
			x = block.getX() + DIE_SIZE/2 - PIP_RADIUS;
			y = block.getY() + DIE_SIZE/2 - PIP_RADIUS;
			pip[0].setLocation(x, y);
			pip[0].setVisible(true);
			break;
		case 2:
			// Add two pips on the diagonal
			x = block.getX() + DIE_SIZE/3 - PIP_RADIUS;
			y = block.getY() + DIE_SIZE/3 - PIP_RADIUS;
			pip[0].setLocation(x, y);
			pip[0].setVisible(true);
			
			x = block.getX() + DIE_SIZE - DIE_SIZE/3 - PIP_RADIUS;
			y = block.getY() + DIE_SIZE - DIE_SIZE/3 - PIP_RADIUS;
			pip[1].setLocation(x, y);
			pip[1].setVisible(true);
			break;
		case 3:
			// Add two pips on the diagonal
			x = block.getX() + DIE_SIZE/4 - PIP_RADIUS;
			y = block.getY() + DIE_SIZE/4 - PIP_RADIUS;
			pip[0].setLocation(x, y);
			pip[0].setVisible(true);
			
			x = block.getX() + DIE_SIZE/2 - PIP_RADIUS;
			y = block.getY() + DIE_SIZE/2 - PIP_RADIUS;
			pip[1].setLocation(x, y);
			pip[1].setVisible(true);
			
			x = block.getX() + DIE_SIZE - DIE_SIZE/4 - PIP_RADIUS;
			y = block.getY() + DIE_SIZE - DIE_SIZE/4 - PIP_RADIUS;
			pip[2].setLocation(x, y);
			pip[2].setVisible(true);
			break;
		case 4:
			// Add four pips near the corners
			x = block.getX() + DIE_SIZE/4 - PIP_RADIUS;
			y = block.getY() + DIE_SIZE/4 - PIP_RADIUS;
			pip[0].setLocation(x, y);
			pip[0].setVisible(true);
			
			x = block.getX() + DIE_SIZE - DIE_SIZE/4 - PIP_RADIUS;
			y = block.getY() + DIE_SIZE/4 - PIP_RADIUS;
			pip[1].setLocation(x, y);
			pip[1].setVisible(true);
			
			x = block.getX() + DIE_SIZE/4 - PIP_RADIUS;
			y = block.getY() + DIE_SIZE - DIE_SIZE/4 - PIP_RADIUS;
			pip[2].setLocation(x, y);
			pip[2].setVisible(true);
			
			x = block.getX() + DIE_SIZE - DIE_SIZE/4 - PIP_RADIUS;
			y = block.getY() + DIE_SIZE - DIE_SIZE/4 - PIP_RADIUS;
			pip[3].setLocation(x, y);
			pip[3].setVisible(true);
			break;
		case 5:
			// Add five pips with four near the corners and one in the center
			x = block.getX() + DIE_SIZE/4 - PIP_RADIUS;
			y = block.getY() + DIE_SIZE/4 - PIP_RADIUS;
			pip[0].setLocation(x, y);
			pip[0].setVisible(true);
			
			x = block.getX() + DIE_SIZE - DIE_SIZE/4 - PIP_RADIUS;
			y = block.getY() + DIE_SIZE/4 - PIP_RADIUS;
			pip[1].setLocation(x, y);
			pip[1].setVisible(true);
			
			x = block.getX() + DIE_SIZE/2 - PIP_RADIUS;
			y = block.getY() + DIE_SIZE/2 - PIP_RADIUS;
			pip[2].setLocation(x, y);
			pip[2].setVisible(true);
			
			x = block.getX() + DIE_SIZE/4 - PIP_RADIUS;
			y = block.getY() + DIE_SIZE - DIE_SIZE/4 - PIP_RADIUS;
			pip[3].setLocation(x, y);
			pip[3].setVisible(true);
			
			x = block.getX() + DIE_SIZE - DIE_SIZE/4 - PIP_RADIUS;
			y = block.getY() + DIE_SIZE - DIE_SIZE/4 - PIP_RADIUS;
			pip[4].setLocation(x, y);
			pip[4].setVisible(true);
			break;
			
		case 6:
			// Add six pips with three on the top row and three on the bottom
			x = block.getX() + DIE_SIZE/4 - PIP_RADIUS;
			y = block.getY() + DIE_SIZE/4 - PIP_RADIUS;
			pip[0].setLocation(x, y);
			pip[0].setVisible(true);
			
			x = block.getX() + DIE_SIZE/2 - PIP_RADIUS;
			y = block.getY() + DIE_SIZE/4 - PIP_RADIUS;
			pip[1].setLocation(x, y);
			pip[1].setVisible(true);
			
			x = block.getX() + DIE_SIZE - DIE_SIZE/4 - PIP_RADIUS;
			y = block.getY() + DIE_SIZE/4 - PIP_RADIUS;
			pip[2].setLocation(x, y);
			pip[2].setVisible(true);
			
			x = block.getX() + DIE_SIZE/4 - PIP_RADIUS;
			y = block.getY() + DIE_SIZE - DIE_SIZE/4 - PIP_RADIUS;
			pip[3].setLocation(x, y);
			pip[3].setVisible(true);

			x = block.getX() + DIE_SIZE/2 - PIP_RADIUS;
			y = block.getY() + DIE_SIZE - DIE_SIZE/4 - PIP_RADIUS;
			pip[4].setLocation(x, y);
			pip[4].setVisible(true);
			
			x = block.getX() + DIE_SIZE - DIE_SIZE/4 - PIP_RADIUS;
			y = block.getY() + DIE_SIZE - DIE_SIZE/4 - PIP_RADIUS;
			pip[5].setLocation(x, y);
			pip[5].setVisible(true);
			break;
		}
		
	}

	/* Internal routine to hide all of the pips by changing the isVisible parameter */
	private void hidePips() {
		for (int i = 0; i < pip.length; i++) {
			pip[i].setVisible(false);
		}
	}


	// Constants
	private final static int DIE_SIZE = 40;
	private final static int DIE_CORNER = 20;	// diameter of circle forming corner, not the radius
	private final static int PIP_SIZE = 6;
	private final static int PIP_RADIUS = PIP_SIZE/2;
	private final static int N_SIDES = 6;
	private final static int N_PIPS = N_SIDES;

	// Custom colors
	private final static Color PURPLE = new Color(112, 48, 160);

	
	// Private variables for the die structure and appearance
	private GRoundRect block;
	private GRoundRect dieBorder;
	private GOval[] pip = new GOval[N_PIPS];
	
	// Value to be shows on the face of the die.
	private int value;
	private RandomGenerator rgen = RandomGenerator.getInstance();
	
}