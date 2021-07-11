import java.awt.Color;
import java.awt.event.MouseEvent;

import acm.graphics.GObject;
import acm.graphics.GPoint;
import acm.program.GraphicsProgram;


public class DiceSectionTest extends GraphicsProgram {

	/**
	 * @param args
	 */
	public void run() {
		addMouseListeners();
		
		setSize (APPLICATION_WIDTH,APPLICATION_HEIGHT);	// Set initial screen size, does not prevent later resize
		setColor (GREEN_FELT);
		
		// reroll section needs to have a button
		rerollSection = new DiceSection (0,REROLL_SECTION_START,"Reroll");
		add (rerollSection);
		
		double keepSectionY = rerollSection.getY() + rerollSection.getHeight() + SECTION_SPACING;
		keepSection = new DiceSection (0,keepSectionY,"Keep");
		add (keepSection);
		
		double scoreSectionX = rerollSection.getWidth();
		scoreBoard = new ScoreSection (scoreSectionX, REROLL_SECTION_START);
		add (scoreBoard);
		
		// test with an array of dice
		for (int i = 0; i < N_DICE; i++) {
			yahtzeeDice[i] = new Die (0, 0, currentColorScheme);
			yahtzeeDice[i].rollDice();
			
			// Add each to the reroll section in the next available spot, check that the add process was successful
			int resultCheck = rerollSection.addDie(yahtzeeDice[i]);
			if (resultCheck == 0) {
				System.out.println("Error: Not enough room for all of the dice.");
			}

			// Add the die to the canvas.
			add (yahtzeeDice[i]);
		}
	}
	
	private void setColor(Color greenFelt2) {
		// TODO Auto-generated method stub
		
	}

	/* When mouse button is pressed, determine the object being selected and
	 * the section from which it was dragged*/
	public void mousePressed(MouseEvent e) {
		last = new GPoint (e.getPoint());
		selectedDie = null;
		
		for (int i = 0; i < N_DICE; i++) {
			if ( yahtzeeDice[i].contains(last.getX(), last.getY()) ) {
				selectedDie = yahtzeeDice[i];
				startPoint = new GPoint (yahtzeeDice[i].getLocation());
				
				GPoint pt = new GPoint( e.getPoint() );
				if (rerollSection.contains(pt) ) {
					startSection = rerollSection;
				}
				else if (keepSection.contains(pt) ) {
					startSection = keepSection;
				}
				break;
			}
		}
	}
	
	/* Handle the event where a die is dragged by the mouse. highlight
	 * the grid at the location where the mouse is currently located.*/
	public void mouseDragged(MouseEvent e) {
		if (selectedDie != null) {
			selectedDie.move(e.getX() - last.getX(), e.getY() - last.getY());
			last = new GPoint(e.getPoint());
			
			if ( rerollSection.contains(last.getX(), last.getY()) ) {
				rerollSection.highlightGridAt (last.getX(), last.getY());
			}
			if ( keepSection.contains(last.getX(), last.getY()) ) {
				keepSection.highlightGridAt (last.getX(), last.getY());
			}
			selectedDie.sendToFront();	// The die being dragged should be visible above the others.
		}
	}

	/* When the user releases the mouse the die should either snap to the currently selected 
	 * grid (if one is selected and available) or to the first available space*/
	public void mouseReleased(MouseEvent e) {
		// If no die was selected there is nothing that needs to be done.
		if (selectedDie != null) {
			/* Find out the section in which the release happens. If it is remove from the previous section
			 * and add to the new section.  If they are the same section, no harm is done.  It will just get
			 * a new location within that section.*/
			if ( rerollSection.contains(e.getX(), e.getY()) ) {
				startSection.removeDie(selectedDie);
				rerollSection.addDie(selectedDie, e.getX(), e.getY());
			}
			else if ( keepSection.contains(e.getX(), e.getY()) ) {
				startSection.removeDie(selectedDie);
				keepSection.addDie(selectedDie, e.getX(), e.getY());
			}
			else {
				selectedDie.setLocation(startPoint);
			}
		}
		
		rerollSection.removeHighlight();
		keepSection.removeHighlight();
	}

	
	/* Constants */
	private static final int APPLICATION_HEIGHT = 700;
	private static final int APPLICATION_WIDTH = 1000;
	private static final int REROLL_SECTION_START = 0;	// top of the keep section
	private static final int SECTION_SPACING = 0;
	private static final int N_DICE = 5;	// Hard code the game for 5 dice
	
	/* Variables */
	
	// Display panel sections
	private DiceSection rerollSection;
	private DiceSection keepSection;
	
	private ScoreSection scoreBoard;
	
	// Color constants/variables
	private final static Color GREEN_FELT = new Color(0, 90, 0);

	private ColorScheme_Dice currentColorScheme = ColorScheme_Dice.rainbow;

	// Mouse listener variables
	// private GObject mouseObject;
	private Die selectedDie;
	
	/* when a dragging event occurs, get the starting point of the object (not the mouse click point). 
	 * the last point is the mouse click and current mouse location point*/
	private DiceSection startSection;
	private GPoint startPoint;
	private GPoint last;

	private Die[] yahtzeeDice = new Die[N_DICE];
	
	/* Background colors - felt color for the dice compartments */
	Color greenFelt = new Color(0,90,0);
	
}