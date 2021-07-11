import java.awt.Color;
import java.awt.Font;
import java.util.ArrayList;

import acm.graphics.GCompound;
import acm.graphics.GLabel;
import acm.graphics.GPoint;
import acm.graphics.GRoundRect;


public class DiceSection extends GCompound{

	/* Basic constructor using the default color scheme*/
	public DiceSection(double x, double y, String sectionTitle) {
	
		/* Add outside boundary first so the inside boundary will be on top when it is added.
		 * This means that the green felt will be visible in the center instead of just the brown border element*/
		outsideBoundary = new GRoundRect (x, y, BORDER_OUTSIDE_WIDTH, BORDER_OUTSIDE_HEIGHT, BORDER_OUTSIDE_CORNER);
		
		outsideBoundary.setColor(BROWN_BORDER);
		outsideBoundary.setFilled(true);
		outsideBoundary.setFillColor(BROWN_BORDER);
		add (outsideBoundary);
		
		insideBoundary = new GRoundRect (x+BORDER, y+BORDER, BORDER_INSIDE_WIDTH, BORDER_INSIDE_HEIGHT, BORDER_INSIDE_CORNER);
		insideBoundary.setColor(GREEN_FELT);
		insideBoundary.setFilled(true);
		insideBoundary.setFillColor(GREEN_FELT);
		add (insideBoundary);
		
		// Add the grid next.
		gridStartPoint = new GPoint (insideBoundary.getLocation());
		
		double tempx;
		double tempy;
		
		// set the text for the label first so we can determine how wide it is and get the
		// placement right
		sectionHeader.setLabel(sectionTitle);
		sectionHeader.setFont(headerLabelFont);
		
		tempx = gridStartPoint.getX() + (BORDER_INSIDE_WIDTH - sectionHeader.getWidth())/2;
		tempy = gridStartPoint.getY() + GRID_OUTSIDE_MARGIN + sectionHeader.getHeight();
		sectionHeader.setLocation(tempx, tempy);
		add (sectionHeader);
				
		// Columns first
		for (int i = 0; i < GRID_COLS; i++) {
			for (int j = 0; j < GRID_ROWS; j++) {
				tempx = gridStartPoint.getX() + GRID_OUTSIDE_MARGIN + i*(GRID_SIZE + GRID_INSIDE_MARGIN);
				tempy = gridStartPoint.getY() + GRID_OUTSIDE_MARGIN + HEADER_HEIGHT + GRID_INSIDE_MARGIN + j*(GRID_SIZE + GRID_INSIDE_MARGIN);
				grid [j][i] = new GRoundRect (tempx, tempy, GRID_SIZE, GRID_SIZE, GRID_CORNER);
				grid [j][i].setColor(GREEN_FELT);
				grid [j][i].setFilled(true);
				grid [j][i].setFillColor(GREEN_FELT);
				grid [j][i].setVisible(true);		// Default is not visible by making the color the same as the background.  When dragging die over grid the current grid should become visible.
				add(grid [j][i]);
			}
		}
		
		/* Initialize the dice array to be empty, columns first*/
		nDice = 0;
		for (int i = 0; i < GRID_COLS; i++) {
			for (int j = 0; j < GRID_ROWS; j++) {
				dieArray[j][i] = null;
			}
		}
	}
	
	/* see if the point is over one of the grid squares.  If so, make it visible.
	 * All others must be not visible. Check columns first.*/
	public void highlightGridAt(double x, double y) {
		for (int i = 0; i < GRID_COLS; i++) {
			for (int j = 0; j < GRID_ROWS; j++) {
				if (grid[j][i].contains(x, y) ) {
					grid[j][i].setColor(GRID_HIGHLIGHT);
					grid[j][i].setFillColor(GRID_HIGHLIGHT);
				}
				else {
					grid[j][i].setColor(GREEN_FELT);
					grid[j][i].setFillColor(GREEN_FELT);
				}
			}
		}
		
	}
	
	/* Remove the highlighting */
	public void removeHighlight() {
		for (int i = 0; i < GRID_COLS; i++) {
			for (int j = 0; j < GRID_ROWS; j++) {
				grid[j][i].setColor(GREEN_FELT);
				grid[j][i].setFillColor(GREEN_FELT);
			}
		}
		
	}
	
	/* Routine to remove the die from the list associated with the section*/
	public void removeDie (Die die) {
		// diceList.remove(die);
		
		/* Search through array, if die is present, change placeholder to null and
		 * decrement the number of dice. Check columns first.*/
		for (int i = 0; i < GRID_COLS; i++) {
			for (int j = 0; j < GRID_ROWS; j++) {
				if ( dieArray[j][i] == die ) {
					dieArray[j][i] = null;
					nDice --;
				}
			}
		}
		
		// System.out.println("Remove Die: remaining dice: " + nDice);
		
	}

	/* Routine to add the die to the list associated with the section.  Also need
	 * to position the die within the grid.  It either belongs at the grid under
	 * the center point of the die or it just goes in the first open cell after the last die.
	 * If a die is in the new spot the old die will be pushed down one place. If a die gets's pushed
	 * off the end of the array it will be moved to the first open spot*/
	public int addDie (Die die) {
		
		// Look for the first open grid. Look through columns first.
		for (int j=0;j<GRID_COLS;j++){
			for (int i=0;i<GRID_ROWS;i++) {
				if (dieArray[i][j] == null) {
					dieArray[i][j] = die;
					nDice ++;
					die.setLocation(grid[i][j].getLocation());
					return (1); // Return a value to break out of the loop and to indicate success.
				}
			}
		}
		return (0); // Return a value of 0 for the case where no open space was found.
	}
	
	/* Add die at a specific coordinate */
	public void addDie (Die die, double x, double y) {
		
		boolean dieOnGrid = false;
		int newDieRow = 0, newDieCol = 0;
		
		// first see if the die is on the grid, check the columns first
		for (int i = 0; i < GRID_COLS; i++) {
			for (int j = 0; j < GRID_ROWS; j++) {
				if (grid[j][i].contains(x, y) ) {
					dieOnGrid = true;
					newDieCol = i;
					newDieRow = j;
					// System.out.println("Grid ID " + newDieRow + ":" + newDieCol);
					break;
				}
			}
		}
		
		/* If the current spot is empty, just put the die there. Otherwise put in the first empty grid.*/
		if (dieOnGrid && dieArray[newDieRow][newDieCol] == null) {
				// Assignment in array
				dieArray[newDieRow][newDieCol] = die;
				nDice ++;
				
				// Snap to grid
				die.setLocation( grid[newDieRow][newDieCol].getLocation() );
		}

		else {
			this.addDie(die);
		}
		
		// System.out.println("Added Die: New number of dice: " + nDice);
	}

	// go through the dice grid and roll all of them. Look through columns first
	public void rollDice () {
		for (int i = 0; i < GRID_COLS; i++) {
			for (int j = 0; j < GRID_ROWS; j++) {
				if (dieArray[j][i] != null) {
					dieArray[j][i].rollDice();
					// Remove and re-add the die to make them move up to the top
					// of the grid
					Die tempDie = dieArray[j][i];
					this.removeDie(tempDie);
					this.addDie(tempDie);
					
				}
			}
		}
	}
	
	// Constants
	
	// Header Label
	private final static int HEADER_HEIGHT = 35;
	private Font headerLabelFont = Font.decode("Arial-18");
	
	private final static int GRID_ROWS = 6;
	private final static int GRID_COLS = 3;
	
	/* Make the next two match the die size/shape.  That will allow the program to highlight the grid object to show where the die
	 * will end up when it is being dragged.*/
	private final static int GRID_SIZE = 40;
	private final static int GRID_CORNER = 20;	// diameter of circle forming corner, not the radius
	private final static int GRID_INSIDE_MARGIN = 10;	// margin between grid squares
	private final static int GRID_OUTSIDE_MARGIN = 20;	// margin around the outside of the grid

	private final static int BORDER = 10;				// thickness of the brown border around the dice compartment
	private final static int BORDER_INSIDE_WIDTH = GRID_COLS*GRID_SIZE + (GRID_COLS-1)*GRID_INSIDE_MARGIN +2*GRID_OUTSIDE_MARGIN;
	private final static int BORDER_OUTSIDE_WIDTH = BORDER_INSIDE_WIDTH + 2*BORDER;
	private final static int BORDER_INSIDE_HEIGHT = HEADER_HEIGHT + GRID_INSIDE_MARGIN + GRID_ROWS*GRID_SIZE + (GRID_ROWS-1)*GRID_INSIDE_MARGIN +2*GRID_OUTSIDE_MARGIN;
	private final static int BORDER_OUTSIDE_HEIGHT = BORDER_INSIDE_HEIGHT + 2*BORDER;
	
	// Corner dimensions are the diameter of circle forming corner, not the radius
	private final static int BORDER_INSIDE_CORNER = GRID_CORNER + 2*GRID_OUTSIDE_MARGIN;
	private final static int BORDER_OUTSIDE_CORNER = BORDER_INSIDE_CORNER + 2*BORDER;
	
	// Custom colors
	private final static Color BROWN_BORDER = new Color(96, 46, 0);
	private final static Color GREEN_FELT = new Color(0, 90, 0);
	private final static Color GRID_HIGHLIGHT = Color.cyan;
	
	// Private variables for the graphics object structure and appearance
	private GRoundRect outsideBoundary;
	private GRoundRect insideBoundary;
	private GRoundRect[][] grid = new GRoundRect[GRID_ROWS][GRID_COLS];	// Use a 2-D array of points as
	private GPoint gridStartPoint;
	
	private GLabel sectionHeader = new GLabel("Section Header");
	
	
//	/* Variable used when placing a new die.  These variables represent the
//	 * row and column numbers for the grid on which it should be placed.*/
//	private int targetGridRow, targetGridCol;
		
	// List of dice
	private ArrayList<Die> diceList = new ArrayList<Die>();
	
	/* Try using an array instead of a list. The list only has one spot per die
	 * but this array should help coordinate the location of the dice in the grid as well*/
	private Die[][] dieArray = new Die[GRID_ROWS][GRID_COLS];
	private final static int dieArraySize = GRID_ROWS*GRID_COLS;
	private int nDice;	// Actual number of dice in this section, not the size of the array
}