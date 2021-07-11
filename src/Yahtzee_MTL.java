import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JLabel;

import acm.graphics.GLabel;
import acm.graphics.GPoint;
import acm.graphics.GRoundRect;
import acm.program.GraphicsProgram;

/**
 * Practice application for learning how to code in Java and use git hub
 * Let's up to 6 players play yahtzee together ... on the same computer
 * will gradually add more options.
 * @author mustardman13
 *
 */
public class Yahtzee_MTL extends GraphicsProgram {

	private static final long serialVersionUID = 1L;

	public void init() {
		addMouseListeners();
		addActionListeners();
		rollButton.addActionListener(new RollButtonListener());
		
		setSize (APPLICATION_WIDTH,APPLICATION_HEIGHT);	// Set initial screen size, does not prevent later resize

		setBackground (greenFelt);
		
		// Initialize to player 1
		activePlayer = 1;
		
		// Initialize the state machines
		stateYahtzee = PLAYING_GAME;
		stateGamePlay = WAITING_FOR_FIRST_ROLL;


		// Update the names for the players
		playerNames[1] = "Michael";
		playerNames[2] = "Sam";
//		playerNames[3] = "Riley";
//		playerNames[4] = "Rebecca";
//		playerNames[5] = "Jolie";
//		playerNames[6] = "Sam";
		
		// Initialize color schemes for columns
		playerColorScheme[0] = ColorScheme_Dice.white_Red;
		playerColorScheme[1] = ColorScheme_Dice.orange_Blue;
//		playerColorScheme[2] = ColorScheme_Dice.yellow_Black;
//		playerColorScheme[3] = ColorScheme_Dice.green_Black;
//		playerColorScheme[4] = ColorScheme_Dice.white_Blue;
//		playerColorScheme[5] = ColorScheme_Dice.white_Purple;
		
		
		// Add the status label to the top of the application
		//statusBar.setText(playerNames[activePlayer] + ": Click button for first roll.");
		statusBar.setText(playerNames[activePlayer] + ": Click button for first roll.");
		statusBar.setVerticalTextPosition(JLabel.CENTER);
		statusBar.setHorizontalTextPosition(JLabel.CENTER);
		add (statusBar, NORTH);

		
		// Add the roll button to the North section
		rollButton.setText("First Roll");
		rollButton.setVerticalTextPosition(JButton.CENTER);
		rollButton.setHorizontalTextPosition(JButton.CENTER);
		rollButton.setMnemonic(KeyEvent.VK_R);
		rollButton.setActionCommand("firstDiceRollCommand");
		add (rollButton, NORTH);
		
		// Add dice sections
		rerollSection = new DiceSection (0,REROLL_SECTION_START,"Reroll");
		add (rerollSection);
		
		double keepSectionY = rerollSection.getY() + rerollSection.getHeight() + SECTION_SPACING;
		keepSection = new DiceSection (0,keepSectionY,"Keep");
		add (keepSection);
		
		// Add the score board
		double scoreSectionX = rerollSection.getWidth();
		initializeScoreSection(scoreSectionX, REROLL_SECTION_START);
		
		// Create an array of dice
		for (int i = 0; i < N_DICE; i++) {
			yahtzeeDice[i] = new Die (0, 0, currentColorScheme);
		}
	}


	/* When mouse button is pressed, determine whether a die was selected and determine
	 * the section from which it was dragged. */
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

	/* When the user releases the mouse, if object is a die should either snap to the currently selected 
	 * grid (if one is selected and available) or to the first available space. If a score option button, 
	 * need to process the selection. */
	public void mouseReleased(MouseEvent e) {
		// If no die was selected then check the score selection buttons
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
		} else {
			// Check whether it was in the score options column.
			for (int i=0; i< ROW_NUM_TOTAL;i++) {
				System.out.println("Checking button on row " + i);
				if (scoreGrid[i][SCORE_OPTION_COL].contains(e.getX(), e.getY()) &&
						scoreGrid[i][SCORE_OPTION_COL].isVisible() == true) {
					System.out.println("Selected Row " + i);
					processScoreSelection(i);
					break;
				}
			}
			
		}
		
		rerollSection.removeHighlight();
		keepSection.removeHighlight();
	}
	
	
	private void initializeScoreSection(double x, double y) {
		
		/* Add outside boundary first so the inside boundary will be on top when it is added.
		 * This means that the green felt will be visible in the center instead of just the brown border element */
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
	
		// Initialize the scores. Use a flag to indicate that a valid score has
		// not yet been added. Display as blank (not 0) and allow a new
		// score to be assigned.
		for (int i = 0; i < scores.length; i++) {
			for (int j = 0; j < scores[1].length; j++) {
				scores[i][j] = UNASSIGNED_FLAG;
				scoresOptions[i][j] = UNASSIGNED_FLAG;
			}
		}
	
		// Add the section header.
		double tempX;
		double tempY;
		
		// set the font label (text previously set) first so we can determine how wide it is and get the
		// placement right
		sectionHeader.setFont(headerRowFont);
		
		tempX = insideBoundary.getX() + (BORDER_INSIDE_WIDTH - sectionHeader.getWidth())/2;
		tempY = insideBoundary.getY() + GRID_OUTSIDE_MARGIN + sectionHeader.getHeight();
		sectionHeader.setLocation(tempX, tempY);
		add (sectionHeader);
		
		// Start adding the grid next. One section at a time
		// Header row has index 0
		// Update tempY after adding a row based on the height of that row just added.
		tempX = insideBoundary.getX() + GRID_OUTSIDE_MARGIN;
		tempY = insideBoundary.getY() + GRID_OUTSIDE_MARGIN + HEADER_HEIGHT + GRID_INSIDE_MARGIN;
		addHeaderRow(tempX, tempY);
		tempY = tempY + HEADER_BOX_HEIGHT + GRID_INSIDE_MARGIN;
		
		addRow(tempX, tempY, ROW_NUM_ONES, "Ones", SCORE_ROW);
		tempY = tempY + SCORE_BOX_HEIGHT + GRID_INSIDE_MARGIN;
		
		addRow(tempX, tempY, ROW_NUM_TWOS, "Twos", SCORE_ROW);
		tempY = tempY + SCORE_BOX_HEIGHT + GRID_INSIDE_MARGIN;
		
		addRow(tempX, tempY, ROW_NUM_THREES, "Threes", SCORE_ROW);
		tempY = tempY + SCORE_BOX_HEIGHT + GRID_INSIDE_MARGIN;
		
		addRow(tempX, tempY, ROW_NUM_FOURS, "Fours", SCORE_ROW);
		tempY = tempY + SCORE_BOX_HEIGHT + GRID_INSIDE_MARGIN;
		
		addRow(tempX, tempY, ROW_NUM_FIVES, "Fives", SCORE_ROW);
		tempY = tempY + SCORE_BOX_HEIGHT + GRID_INSIDE_MARGIN;
		
		addRow(tempX, tempY, ROW_NUM_SIXES, "Sixes", SCORE_ROW);
		tempY = tempY + SCORE_BOX_HEIGHT + GRID_INSIDE_MARGIN;
		
		addRow(tempX, tempY, ROW_NUM_SECTION_1_PRE_BONUS_SUBTOTAL, "Subtotal", SUBTOTAL_ROW);
		tempY = tempY + SUBTOTAL_BOX_HEIGHT + GRID_INSIDE_MARGIN;
		
		addRow(tempX, tempY, ROW_NUM_BONUS, "Bonus", SUBTOTAL_ROW);
		tempY = tempY + SUBTOTAL_BOX_HEIGHT + GRID_INSIDE_MARGIN;
		
		addRow(tempX, tempY, ROW_NUM_SECTION_1_SUBTOTAL, "Section 1", SUBTOTAL_ROW);
		tempY = tempY + SUBTOTAL_BOX_HEIGHT + GRID_INSIDE_MARGIN;
		
		addRow(tempX, tempY, ROW_NUM_THREE_OF_A_KIND, "Three of a Kind", SCORE_ROW);
		tempY = tempY + SCORE_BOX_HEIGHT + GRID_INSIDE_MARGIN;
		
		addRow(tempX, tempY, ROW_NUM_FOUR_OF_A_KIND, "Four of a Kind", SCORE_ROW);
		tempY = tempY + SCORE_BOX_HEIGHT + GRID_INSIDE_MARGIN;
		
		addRow(tempX, tempY, ROW_NUM_FULL_HOUSE, "Full House", SCORE_ROW);
		tempY = tempY + SCORE_BOX_HEIGHT + GRID_INSIDE_MARGIN;
		
		addRow(tempX, tempY, ROW_NUM_SM_STRAIGHT, "Small Straight", SCORE_ROW);
		tempY = tempY + SCORE_BOX_HEIGHT + GRID_INSIDE_MARGIN;
		
		addRow(tempX, tempY, ROW_NUM_LG_STRAIGHT, "Large Straight", SCORE_ROW);
		tempY = tempY + SCORE_BOX_HEIGHT + GRID_INSIDE_MARGIN;
		
		addRow(tempX, tempY, ROW_NUM_YAHTZEE, "Yahtzee", SCORE_ROW);
		tempY = tempY + SCORE_BOX_HEIGHT + GRID_INSIDE_MARGIN;
		
		addRow(tempX, tempY, ROW_NUM_CHANCE, "Chance", SCORE_ROW);
		tempY = tempY + SCORE_BOX_HEIGHT + GRID_INSIDE_MARGIN;
		
		addRow(tempX, tempY, ROW_NUM_SECTION_2_SUBTOTAL, "Section 2", SUBTOTAL_ROW);
		tempY = tempY + SUBTOTAL_BOX_HEIGHT + GRID_INSIDE_MARGIN;
		
		addRow(tempX, tempY, ROW_NUM_TOTAL, "Total", TOTAL_ROW);
		
	}


	private void addHeaderRow(double tempX, double tempY) {

		// First cell in the header row is the header for the category
		scoreGrid [0][0]  = new GRoundRect (tempX, tempY, CATEGORY_BOX_WIDTH, HEADER_BOX_HEIGHT, GRID_CORNER);
		scoreGridLabel [0][0] = new GLabel("Category");

		// next six cells will be for the players names
		// Update tempX after the box is created so the increase in tempX for the next will be
		// box will be based on the size of current box.
		tempX = tempX + CATEGORY_BOX_WIDTH + GRID_INSIDE_MARGIN;
		for (int j= 1; j < N_PLAYERS+1; j++) {
			scoreGrid [0][j] = new GRoundRect (tempX, tempY, SCORE_BOX_WIDTH, HEADER_BOX_HEIGHT, GRID_CORNER);
			//scoreGridLabel [0][j] = new GLabel("Player "+j);
			scoreGridLabel [0][j] = new GLabel(playerNames[j]);
			tempX = tempX + SCORE_BOX_WIDTH + GRID_INSIDE_MARGIN;		
		}
		scoreGrid [0][GRID_COLS-1] = new GRoundRect (tempX, tempY, SCORE_BUTTON_WIDTH, HEADER_BOX_HEIGHT, GRID_CORNER);
		scoreGridLabel [0][GRID_COLS-1] = new GLabel("Select");
		
		// Apply the standard formatting to all cells. Then go back and adjust
		// the player column color scheme
		for (int j = 0; j < GRID_COLS; j++) {
			stdGridFormatting(scoreGrid[0][j], scoreGridLabel[0][j], headerRowFont, JUSTIFICATION_CENTER);
			add (scoreGrid[0][j]);
			add (scoreGridLabel[0][j]);
		}
		
		// Player columns have different formatting
		// Start at j=1 since the first column is for
		// the category label
		for (int j = 1; j < N_PLAYERS+1; j++) {
			ApplyColorScheme (scoreGrid[0][j], scoreGridLabel[0][j], playerColorScheme[j-1]);			
		}

		// Default condition for the score option column is not visible
		scoreGrid[0][GRID_COLS-1].setVisible(false);
		scoreGridLabel[0][GRID_COLS-1].setVisible(false);
	}

	// Routine for adding rows other than the header row. Make the header label and
	// position within the grid parameters to this can be reused for all of the rows
	private void addRow (double tempX, double tempY, int i, String headerLabel, int rowType) {
		
		int rowHeight;
		Font rowFont;
		int justification;
		
		// Look at the rowType parameter to select the right row height and font size
		switch (rowType) {
			case HEADER_ROW:
				rowHeight = HEADER_BOX_HEIGHT;
				rowFont = headerRowFont;
				justification = JUSTIFICATION_CENTER;
				break;	
			case SCORE_ROW:
				rowHeight = SCORE_BOX_HEIGHT;
				rowFont = scoreRowFont;
				justification = JUSTIFICATION_RIGHT;
				break;	
			case SUBTOTAL_ROW:
				rowHeight = SUBTOTAL_BOX_HEIGHT;
				rowFont = subtotalRowFont;
				justification = JUSTIFICATION_RIGHT;
				break;	
			case TOTAL_ROW:
				rowHeight = TOTAL_BOX_HEIGHT;
				rowFont = totalRowFont;
				justification = JUSTIFICATION_RIGHT;
				break;	
			default:
				rowHeight = SCORE_BOX_HEIGHT;
				rowFont = scoreRowFont;
				justification = JUSTIFICATION_RIGHT;
				break;
		}
		
		// First cell in the header row is the header for the category. It will always be center justified.
		scoreGrid [i][0]  = new GRoundRect (tempX, tempY, CATEGORY_BOX_WIDTH, rowHeight, GRID_CORNER);
		scoreGridLabel [i][0] = new GLabel(headerLabel);
		stdGridFormatting(scoreGrid[i][0], scoreGridLabel[i][0], rowFont, JUSTIFICATION_CENTER);
		add (scoreGrid[i][0]);
		add (scoreGridLabel[i][0]);

		// next six cells will be for the players names
		// Update tempX after the box is created so the increase in tempX for the next will be
		// box will be based on the size of current box.
		tempX = tempX + CATEGORY_BOX_WIDTH + GRID_INSIDE_MARGIN;
		for (int j = 1; j < N_PLAYERS+1; j++) {
			scoreGrid [i][j] = new GRoundRect (tempX, tempY, SCORE_BOX_WIDTH, rowHeight, GRID_CORNER);
			scoreGridLabel [i][j] = new GLabel("");			
			tempX = tempX + SCORE_BOX_WIDTH + GRID_INSIDE_MARGIN;		
		}
		scoreGrid [i][GRID_COLS-1] = new GRoundRect (tempX, tempY, SCORE_BUTTON_WIDTH, rowHeight, GRID_CORNER);
		scoreGridLabel [i][GRID_COLS-1] = new GLabel("Option");

		// Apply the standard formatting to the cells other than the header. Then go back and adjust
		// the player column color scheme
		for (int j = 1; j < GRID_COLS; j++) {
			stdGridFormatting(scoreGrid[i][j], scoreGridLabel[i][j], rowFont, justification);
			add (scoreGrid[i][j]);
			add (scoreGridLabel[i][j]);
		}
		
		// Player columns have different formatting
		// Start at j=1 since the first column is for
		// the category label
		for (int j = 1; j < N_PLAYERS+1; j++) {
			ApplyColorScheme (scoreGrid[i][j], scoreGridLabel[i][j], playerColorScheme[j-1]);			
		}

		// Default condition for the score option column is not visible
		scoreGrid[i][GRID_COLS-1].setVisible(false);
		scoreGridLabel[i][GRID_COLS-1].setVisible(false);
	}

	
	// Routine for doing the standard cell formatting.
	// This will be overridden for player cells that need
	// special formatting.
	private void stdGridFormatting (GRoundRect block, GLabel label, Font labelFont, int justification) {
		double labelX;
		double labelY;
		
		block.setColor(stdBoxColor);
		block.setFilled(true);
		block.setFillColor(stdBoxColor);
		block.setVisible(true);
		
		label.setColor(headerBoxFontColor);
		label.setFont(labelFont);
		
		// Calculate position of label
		if (justification == JUSTIFICATION_RIGHT) {
			labelX = block.getX() + block.getWidth() - label.getWidth();
		} else {
			labelX = block.getX() + (block.getWidth() - label.getWidth())/2;
		}
			
		labelY = block.getY() + (block.getHeight() + label.getHeight())/2;
		label.setLocation((int)labelX, (int)labelY);
	}
	
/* routine to update the player names. Return true unless
	 * the player number is an invalid number */
	private boolean updatePlayerNames (String[] playerName) {
		
		if (playerName.length > 0 &&  playerName.length <= N_PLAYERS) {
			double labelX;
			double labelY;
			for (int j = 0; j < playerName.length; j++) {
				
				// Update the label and calculate position
				// Add one to skip over the header column
				int k = j + 1;
				scoreGridLabel[0][k].setLabel(playerName[j]);
		
				// center the name unless it is too long to fit, then left justify so the first character(s) do not get cut off
				if (scoreGrid[0][k].getWidth() > scoreGridLabel[0][k].getWidth()) {
					labelX = scoreGrid[0][k].getX() + (scoreGrid[0][k].getWidth() - scoreGridLabel[0][k].getWidth())/2;
				} else {
					labelX = scoreGrid[0][k].getX();
				}
				labelY = scoreGrid[0][k].getY() + (scoreGrid[0][k].getHeight() + scoreGridLabel[0][k].getHeight())/2;
				scoreGridLabel[0][k].setLocation((int)labelX, (int)labelY);
			}
			return true;
		} else {
			return false;
		}
	}

	



/* routine to update the player color schemes. Return true unless
 * the player number is an invalid number */
private boolean updatePlayerColors (ColorScheme_Dice[] playerColors) {
	
	if (playerColors.length > 0 &&  playerColors.length <= N_PLAYERS) {	
		for (int j = 0; j < playerColors.length; j++) {
			
			// Update the color scheme - should not change text size.
			// Add one to skip over the header column
			int k = j + 1;
			
			// iterate over all rows for this player j and apply the new color scheme
			for (int i = 0; i < GRID_ROWS; i++) {
				ApplyColorScheme(scoreGrid[i][k], scoreGridLabel[i][k], playerColors[k]);
			}
		}
		return true;
	} else {
		return false;
	}
}


//	private void addHeaderRow(GPoint gridStartPoint) {
//		double tempX = gridStartPoint.getX();
//		double tempY = gridStartPoint.getY();
//		double labelX;
//		double labelY;
//		
//		// First cell in the header row is the header for the category
//		headerGrid [0][0]  = new GRoundRect (tempX, tempY, CATEGORY_BOX_WIDTH, HEADER_BOX_HEIGHT, GRID_CORNER);
//		headerGridLabel [0][0] = new GLabel("Category");
//
//		// next six cells will be for the players names
//		tempX = tempX + CATEGORY_BOX_WIDTH + GRID_INSIDE_MARGIN;
//		headerGrid [0][1] = new GRoundRect (tempX, tempY, SCORE_BOX_WIDTH, HEADER_BOX_HEIGHT, GRID_CORNER);
//		headerGridLabel [0][1] = new GLabel("Player 1");
//		
//		tempX = tempX + SCORE_BOX_WIDTH + GRID_INSIDE_MARGIN;		
//		headerGrid [0][2] = new GRoundRect (tempX, tempY, SCORE_BOX_WIDTH, HEADER_BOX_HEIGHT, GRID_CORNER);
//		headerGridLabel [0][2] = new GLabel("Player 2");
//		
//		tempX = tempX + SCORE_BOX_WIDTH + GRID_INSIDE_MARGIN;		
//		headerGrid [0][3] = new GRoundRect (tempX, tempY, SCORE_BOX_WIDTH, HEADER_BOX_HEIGHT, GRID_CORNER);
//		headerGridLabel [0][3] = new GLabel("Player 3");
//		
//		tempX = tempX + SCORE_BOX_WIDTH + GRID_INSIDE_MARGIN;		
//		headerGrid [0][4] = new GRoundRect (tempX, tempY, SCORE_BOX_WIDTH, HEADER_BOX_HEIGHT, GRID_CORNER);
//		headerGridLabel [0][4] = new GLabel("Player 4");
//		
//		tempX = tempX + SCORE_BOX_WIDTH + GRID_INSIDE_MARGIN;		
//		headerGrid [0][5] = new GRoundRect (tempX, tempY, SCORE_BOX_WIDTH, HEADER_BOX_HEIGHT, GRID_CORNER);
//		headerGridLabel [0][5] = new GLabel("Player 5");
//		
//		tempX = tempX + SCORE_BOX_WIDTH + GRID_INSIDE_MARGIN;		
//		headerGrid [0][6] = new GRoundRect (tempX, tempY, SCORE_BOX_WIDTH, HEADER_BOX_HEIGHT, GRID_CORNER);
//		headerGridLabel [0][6] = new GLabel("Player 6");
//		
//		tempX = tempX + SCORE_BOX_WIDTH + GRID_INSIDE_MARGIN;
//		headerGrid [0][7] = new GRoundRect (tempX, tempY, SCORE_BUTTON_WIDTH, HEADER_BOX_HEIGHT, GRID_CORNER);
//		headerGridLabel [0][7] = new GLabel("Score");
//
//		
//		// loop through all of the header cells and apply the standard formatting.
//		for (int j = 0; j < GRID_COLS; j++) {
//			
////			System.out.println("j = " + j);
//			
//			headerGrid [0][j].setColor(headerBoxColor);
//			headerGrid [0][j].setFilled(true);
//			headerGrid [0][j].setFillColor(headerBoxColor);
//			headerGrid [0][j].setVisible(true);
//			
//			headerGridLabel [0][j].setColor(headerBoxFontColor);
//			headerGridLabel [0][j].setFont(headerBoxFont);
//			
//			// Calculate position of label
//			labelX = headerGrid[0][j].getX() + (headerGrid[0][j].getWidth() - headerGridLabel[0][j].getWidth())/2;
//			labelY = headerGrid[0][j].getY() + (headerGrid[0][j].getHeight() + headerGridLabel[0][j].getHeight())/2;
//			headerGridLabel[0][j].setLocation((int)labelX, (int)labelY);
//
////			System.out.println("X Location: " + labelX);
//			
//			add(headerGrid [0][j]);						// Put on the canvas
//			add(headerGridLabel [0][j]);
//			
//			// Player columns have different formatting
//			if (j>0 && j <=N_PLAYERS) {
//				ApplyColorScheme (headerGrid[0][j], headerGridLabel[0][j], playerColorScheme[j-1]);
//			}
//			
//			// Default condition for the score option column is not visible
//			if (j==GRID_COLS) {
//				headerGrid[0][j].setVisible(false);
//				headerGridLabel[0][j].setVisible(false);
//			}
//		}
//	}
	
//	private void addSection1Rows(GPoint gridStartPoint) {
//		double tempX = gridStartPoint.getX();
//		double tempY = gridStartPoint.getY();
//		double labelX;
//		double labelY;
//		
//		// First add the category names. Then go back and add the rest of the cells.
//		section1ScoreGrid [0][0]  = new GRoundRect (tempX, tempY, CATEGORY_BOX_WIDTH, SCORE_BOX_HEIGHT, GRID_CORNER);
//		section1ScoreGridLabel [0][0] = new GLabel("Ones");
//		
//		tempY = tempY + SCORE_BOX_HEIGHT + GRID_INSIDE_MARGIN;		
//		section1ScoreGrid [1][0] = new GRoundRect (tempX, tempY, CATEGORY_BOX_WIDTH, SCORE_BOX_HEIGHT, GRID_CORNER);
//		section1ScoreGridLabel [1][0] = new GLabel("Twos");
//		
//		tempY = tempY + SCORE_BOX_HEIGHT + GRID_INSIDE_MARGIN;
//		section1ScoreGrid [2][0] = new GRoundRect (tempX, tempY, CATEGORY_BOX_WIDTH, SCORE_BOX_HEIGHT, GRID_CORNER);
//		section1ScoreGridLabel [2][0] = new GLabel("Threes");
//		
//		tempY = tempY + SCORE_BOX_HEIGHT + GRID_INSIDE_MARGIN;
//		section1ScoreGrid [3][0] = new GRoundRect (tempX, tempY, CATEGORY_BOX_WIDTH, SCORE_BOX_HEIGHT, GRID_CORNER);
//		section1ScoreGridLabel [3][0] = new GLabel("Fours");
//		
//		tempY = tempY + SCORE_BOX_HEIGHT + GRID_INSIDE_MARGIN;
//		section1ScoreGrid [4][0] = new GRoundRect (tempX, tempY, CATEGORY_BOX_WIDTH, SCORE_BOX_HEIGHT, GRID_CORNER);
//		section1ScoreGridLabel [4][0] = new GLabel("Fives");
//		
//		tempY = tempY + SCORE_BOX_HEIGHT + GRID_INSIDE_MARGIN;
//		section1ScoreGrid [5][0] = new GRoundRect (tempX, tempY, CATEGORY_BOX_WIDTH, SCORE_BOX_HEIGHT, GRID_CORNER);
//		section1ScoreGridLabel [5][0] = new GLabel("Sixes");
//				
//		// loop through all of the header cells and apply the standard formatting.
//		for (int i = 0; i < section1ScoreGrid.length; i++) {
//			
//			section1ScoreGrid [i][0].setColor(headerBoxColor);
//			section1ScoreGrid [i][0].setFilled(true);
//			section1ScoreGrid [i][0].setFillColor(headerBoxColor);
//			section1ScoreGrid [i][0].setVisible(true);
//			
//			section1ScoreGridLabel [i][0].setColor(headerBoxFontColor);
//			section1ScoreGridLabel [i][0].setFont(headerBoxFont);
//			
//			// Calculate position of label
//			labelX = section1ScoreGrid[i][0].getX() + (section1ScoreGrid[i][0].getWidth() - section1ScoreGridLabel[i][0].getWidth())/2;
//			labelY = section1ScoreGrid[i][0].getY() + (section1ScoreGrid[i][0].getHeight() + section1ScoreGridLabel[i][0].getHeight())/2;
//			section1ScoreGridLabel[i][0].setLocation((int)labelX, (int)labelY);
//			
//			add(section1ScoreGrid [i][0]);						// Put on the canvas
//			add(section1ScoreGridLabel [i][0]);			
//		}
//		
//		// Now add the score boxes to the right of the category boxes. (skip column 0)
//		// Don't add the last column yet ... it will have different formatting
//		for (int i = 0; i < section1ScoreGrid.length; i++) {
//			for (int j = 1; j < N_PLAYERS+1; j++) {
//				tempX = gridStartPoint.getX() + CATEGORY_BOX_WIDTH + GRID_INSIDE_MARGIN + (SCORE_BOX_WIDTH + GRID_INSIDE_MARGIN)*(j-1);
//				tempY = gridStartPoint.getY() + (SCORE_BOX_HEIGHT + GRID_INSIDE_MARGIN)*i;
//				
//				section1ScoreGrid [i][j]  = new GRoundRect (tempX, tempY, SCORE_BOX_WIDTH, SCORE_BOX_HEIGHT, GRID_CORNER);
//				section1ScoreGridLabel [i][j] = new GLabel("Player "+j);
//				
//				section1ScoreGrid [i][j].setFilled(true);				
//				section1ScoreGrid [i][j].setVisible(true);				
//				section1ScoreGridLabel [i][j].setFont(headerBoxFont);
//				
//				ApplyColorScheme (section1ScoreGrid[i][j], section1ScoreGridLabel[i][j], playerColorScheme[j-1]);
//				
//				// Right justify the scores so the digits will lign up.
//				labelX = section1ScoreGrid[i][j].getX() + section1ScoreGrid[i][j].getWidth() - section1ScoreGridLabel[i][j].getWidth() - TEXT_INDENT;
//				labelY = section1ScoreGrid[i][j].getY() + (section1ScoreGrid[i][j].getHeight() + section1ScoreGridLabel[i][j].getHeight())/2;
//				section1ScoreGridLabel[i][j].setLocation((int)labelX, (int)labelY);
//				
//				add(section1ScoreGrid [i][j]);						// Put on the canvas
//				add(section1ScoreGridLabel [i][j]);			
//				
//			}
//		}
//	}
//
//	private void addSection1SubtotalRow (GPoint gridStartPoint) {
//		double tempX = gridStartPoint.getX();
//		double tempY = gridStartPoint.getY();
//		double labelX;
//		double labelY;
//		
//		// First add the category name.
//		section1SubtotalGrid [0][0]  = new GRoundRect (tempX, tempY, CATEGORY_BOX_WIDTH, SCORE_BOX_HEIGHT, GRID_CORNER);
//		section1SubtotalGridLabel [0][0] = new GLabel("Subtotal");
//					
//		section1SubtotalGrid [0][0].setColor(headerBoxColor);
//		section1SubtotalGrid [0][0].setFilled(true);
//		section1SubtotalGrid [0][0].setFillColor(headerBoxColor);
//		section1SubtotalGrid [0][0].setVisible(true);
//		
//		section1SubtotalGridLabel [0][0].setColor(headerBoxFontColor);
//		section1SubtotalGridLabel [0][0].setFont(headerBoxFont);
//		
//		// Calculate position of label
//		labelX = section1SubtotalGrid[0][0].getX() + (section1SubtotalGrid[0][0].getWidth() - section1SubtotalGridLabel[0][0].getWidth())/2;
//		labelY = section1SubtotalGrid[0][0].getY() + (section1SubtotalGrid[0][0].getHeight() + section1SubtotalGridLabel[0][0].getHeight())/2;
//		section1SubtotalGridLabel[0][0].setLocation((int)labelX, (int)labelY);
//		
//		add(section1SubtotalGrid [0][0]);						// Put on the canvas
//		add(section1SubtotalGridLabel [0][0]);			
//		
//		// Now add the score boxes to the right of the category boxes. (skip column 0)
//		// Don't add the last column yet ... it will have different formatting
//		for (int j = 1; j < N_PLAYERS+1; j++) {
//			tempX = gridStartPoint.getX() + CATEGORY_BOX_WIDTH + GRID_INSIDE_MARGIN + (SCORE_BOX_WIDTH + GRID_INSIDE_MARGIN)*(j-1);
//			
//			section1SubtotalGrid [0][j]  = new GRoundRect (tempX, tempY, SCORE_BOX_WIDTH, SCORE_BOX_HEIGHT, GRID_CORNER);
//			section1SubtotalGridLabel [0][j] = new GLabel("Player: "+j);
//			
//			section1SubtotalGrid [0][j].setFilled(true);				
//			section1SubtotalGrid [0][j].setVisible(true);				
//			section1SubtotalGridLabel [0][j].setFont(headerBoxFont);
//			
//			ApplyColorScheme (section1SubtotalGrid[0][j], section1SubtotalGridLabel[0][j], playerColorScheme[j-1]);
//			
//			// Right justify the scores so the digits will lign up.
//			labelX = section1SubtotalGrid[0][j].getX() + section1SubtotalGrid[0][j].getWidth() - section1SubtotalGridLabel[0][j].getWidth() - TEXT_INDENT;
//			labelY = section1SubtotalGrid[0][j].getY() + (section1SubtotalGrid[0][j].getHeight() + section1SubtotalGridLabel[0][j].getHeight())/2;
//			section1SubtotalGridLabel[0][j].setLocation((int)labelX, (int)labelY);
//			
//			add(section1SubtotalGrid [0][j]);						// Put on the canvas
//			add(section1SubtotalGridLabel [0][j]);			
//			
//		}
//	}
//
//	private void addSection1BonusRow (GPoint gridStartPoint) {
//		double tempX = gridStartPoint.getX();
//		double tempY = gridStartPoint.getY();
//		double labelX;
//		double labelY;
//		
//		// First add the category name.
//		section1BonusGrid [0][0]  = new GRoundRect (tempX, tempY, CATEGORY_BOX_WIDTH, SCORE_BOX_HEIGHT, GRID_CORNER);
//		section1BonusGridLabel [0][0] = new GLabel("Subtotal");
//					
//		section1BonusGrid [0][0].setColor(headerBoxColor);
//		section1BonusGrid [0][0].setFilled(true);
//		section1BonusGrid [0][0].setFillColor(headerBoxColor);
//		section1BonusGrid [0][0].setVisible(true);
//		
//		section1BonusGridLabel [0][0].setColor(headerBoxFontColor);
//		section1BonusGridLabel [0][0].setFont(headerBoxFont);
//		
//		// Calculate position of label
//		labelX = section1BonusGrid[0][0].getX() + (section1BonusGrid[0][0].getWidth() - section1BonusGridLabel[0][0].getWidth())/2;
//		labelY = section1BonusGrid[0][0].getY() + (section1BonusGrid[0][0].getHeight() + section1BonusGridLabel[0][0].getHeight())/2;
//		section1BonusGridLabel[0][0].setLocation((int)labelX, (int)labelY);
//		
//		add(section1BonusGrid [0][0]);						// Put on the canvas
//		add(section1BonusGridLabel [0][0]);			
//		
//		// Now add the score boxes to the right of the category boxes. (skip column 0)
//		// Don't add the last column yet ... it will have different formatting
//		for (int j = 1; j < N_PLAYERS+1; j++) {
//			tempX = gridStartPoint.getX() + CATEGORY_BOX_WIDTH + GRID_INSIDE_MARGIN + (SCORE_BOX_WIDTH + GRID_INSIDE_MARGIN)*(j-1);
//			
//			section1BonusGrid [0][j]  = new GRoundRect (tempX, tempY, SCORE_BOX_WIDTH, SCORE_BOX_HEIGHT, GRID_CORNER);
//			section1BonusGridLabel [0][j] = new GLabel("Player: "+j);
//			
//			section1BonusGrid [0][j].setFilled(true);				
//			section1BonusGrid [0][j].setVisible(true);				
//			section1BonusGridLabel [0][j].setFont(headerBoxFont);
//			
//			ApplyColorScheme (section1BonusGrid[0][j], section1BonusGridLabel[0][j], playerColorScheme[j-1]);
//			
//			// Right justify the scores so the digits will lign up.
//			labelX = section1BonusGrid[0][j].getX() + section1BonusGrid[0][j].getWidth() - section1BonusGridLabel[0][j].getWidth() - TEXT_INDENT;
//			labelY = section1BonusGrid[0][j].getY() + (section1BonusGrid[0][j].getHeight() + section1BonusGridLabel[0][j].getHeight())/2;
//			section1BonusGridLabel[0][j].setLocation((int)labelX, (int)labelY);
//			
//			add(section1BonusGrid [0][j]);						// Put on the canvas
//			add(section1BonusGridLabel [0][j]);			
//			
//		}
//	}
	
	private void ApplyColorScheme(GRoundRect block, GLabel label,
			ColorScheme_Dice colorScheme) {
		
		// Rainbow cannot really be accommodated.
		switch (colorScheme) {
			case red_White:
				block.setColor(Color.red);
				block.setFillColor(Color.red);
				label.setColor(Color.white);
				break;
	
			case white_Red:
				block.setColor(Color.white);
				block.setFillColor(Color.white);
				label.setColor(Color.red);
				break;
				
			case orange_Blue:
				block.setColor(Color.orange);
				block.setFillColor(Color.orange);
				label.setColor(Color.blue);
				break;
			
			case blue_Orange:
				block.setColor(Color.blue);
				block.setFillColor(Color.blue);
				label.setColor(Color.orange);
				break;
				
			case yellow_Black:
				block.setColor(Color.yellow);
				block.setFillColor(Color.yellow);
				label.setColor(Color.black);
				break;
			
			case black_Yellow:
				block.setColor(Color.black);
				block.setFillColor(Color.black);
				label.setColor(Color.yellow);
				break;
				
			case green_Black:
				block.setColor(Color.green);
				block.setFillColor(Color.green);
				label.setColor(Color.black);
				break;
	
			case black_Green:
				block.setColor(Color.black);
				block.setFillColor(Color.black);
				label.setColor(Color.green);
				break;
	
			case blue_White:
				block.setColor(Color.blue);
				block.setFillColor(Color.blue);
				label.setColor(Color.white);
				break;
	
			case white_Blue:
				block.setColor(Color.white);
				block.setFillColor(Color.white);
				label.setColor(Color.blue);
				break;
	
			case purple_White:
				block.setColor(PURPLE);
				block.setFillColor(PURPLE);
				label.setColor(Color.white);
				break;
	
			case white_Purple:
				block.setColor(Color.white);
				block.setFillColor(Color.white);
				label.setColor(PURPLE);
				break;
	
			case black_White:
				block.setColor(Color.black);
				block.setFillColor(Color.black);
				label.setColor(Color.white);
				break;	
			case white_Black:
			default:
				block.setColor(Color.white);
				block.setFillColor(Color.white);
				label.setColor(Color.black);
				break;
		}
	}

	
	

	/* subroutine to determine what score options are applicable and display them
		 * on the score option buttons Return true unless the player number is an invalid number*/
		private void displayScoreOption (int playerNumber, Die[] dice) {
	
			/* get the count of each number up front and leverage that data in the evaluations below. This
			 * will avoid the need to iterate over the dice list multiple times and call the getValue function
			 * multiple times */
			int countOnes = 0;
			int countTwos = 0;
			int countThrees = 0;
			int countFours = 0;
			int countFives = 0;
			int countSixes = 0;
			int sum = 0;
			
			for (int i = 0; i < dice.length; i++) {
				int dieValue = dice[i].getValue();
				sum = sum + dieValue;
				switch (dieValue) {
					case 1:
						countOnes++;
						break;
					case 2:
						countTwos++;
						break;
					case 3:
						countThrees++;
						break;
					case 4:
						countFours++;
						break;
					case 5:
						countFives++;
						break;
					case 6:
						countSixes++;
						break;
					default:
						// do nothing
				}
				
			}
			
			/* first hide all of the options then only make the options visible that should based on the dice. */
			hideOptions();
			
	
			
			/* Look at each score option in turn. If the current score is -1 that indicates the field is
			 *  open to be changed. Only Yahtzee can be changed after a score is added by adding an additional
			 *  Yahtzee*/
			
			int currentOption = UNASSIGNED_FLAG;
			if (scores[ROW_NUM_ONES][playerNumber]==UNASSIGNED_FLAG) {
				currentOption = countOnes;
				scoresOptions[ROW_NUM_ONES][playerNumber] = currentOption;
				displayOption (ROW_NUM_ONES, currentOption);
			}
			if (scores[ROW_NUM_TWOS][playerNumber]==UNASSIGNED_FLAG) {
				currentOption = countTwos*2;
				scoresOptions[ROW_NUM_TWOS][playerNumber] = currentOption;
				displayOption (ROW_NUM_TWOS, currentOption);
			}
			if (scores[ROW_NUM_THREES][playerNumber]==UNASSIGNED_FLAG) {
				currentOption = countThrees*3;
				scoresOptions[ROW_NUM_THREES][playerNumber] = currentOption;
				displayOption (ROW_NUM_THREES, currentOption);
			}
			if (scores[ROW_NUM_FOURS][playerNumber]==UNASSIGNED_FLAG) {
				currentOption = countFours*4;
				scoresOptions[ROW_NUM_FOURS][playerNumber] = currentOption;
				displayOption (ROW_NUM_FOURS, currentOption);
			}
			if (scores[ROW_NUM_FIVES][playerNumber]==UNASSIGNED_FLAG) {
				currentOption = countFives*5;
				scoresOptions[ROW_NUM_FIVES][playerNumber] = currentOption;
				displayOption (ROW_NUM_FIVES, currentOption);
			}
			if (scores[ROW_NUM_SIXES][playerNumber]==UNASSIGNED_FLAG) {
				currentOption = countSixes*6;
				scoresOptions[ROW_NUM_SIXES][playerNumber] = currentOption;
				displayOption (ROW_NUM_SIXES, currentOption);
			}
			if (scores[ROW_NUM_THREE_OF_A_KIND][playerNumber]==UNASSIGNED_FLAG) {
				// If the count for any number is three or more then
				// get the sum of all dice values. Player has an option
				// to take a 0 on this if three of a kind is not achieved
				if (countOnes >= 3 || countTwos >= 3 || countThrees >= 3 || countFours >= 3 || countFives >= 3 || countSixes >= 3) {
					currentOption = sum;
				} else {
					currentOption = 0;
				}
				scoresOptions[ROW_NUM_THREE_OF_A_KIND][playerNumber] = currentOption;
				displayOption (ROW_NUM_THREE_OF_A_KIND, currentOption);
			}
			if (scores[ROW_NUM_FOUR_OF_A_KIND][playerNumber]==UNASSIGNED_FLAG) {
				// If the count for any number is four or more then
				// get the sum of all dice values. Player has an option
				// to take a 0 on this if three of a kind is not achieved
				if (countOnes >= 4 || countTwos >= 4 || countThrees >= 4 || countFours >= 4 || countFives >= 4 || countSixes >= 4) {
					currentOption = sum;
				} else {
					currentOption = 0;
				}
				scoresOptions[ROW_NUM_FOUR_OF_A_KIND][playerNumber] = currentOption;
				displayOption (ROW_NUM_FOUR_OF_A_KIND, currentOption);
			}
			if (scores[ROW_NUM_FULL_HOUSE][playerNumber]==UNASSIGNED_FLAG) {
				/* Check for a full house, three of a kind and two of a kind. Don't treat
				 * Yahtzee as a full house. Score is 25 regardless of which values are used.
				 * The greater than condition is to accommodate a variation where more than
				 * 5 dice are utilized. */
	
				if ((countOnes >= 3 && (countTwos >= 2 || countThrees >= 2 || countFours >= 2 || countFives >= 2 || countSixes >= 2))
						|| (countTwos >= 3 && (countOnes >= 2 || countThrees >= 2 || countFours >= 2 || countFives >= 2 || countSixes >= 2))
						|| (countThrees >= 3 && (countOnes >= 2 || countTwos >= 2 || countFours >= 2 || countFives >= 2 || countSixes >= 2))
						|| (countFours >= 3 && (countOnes >= 2 || countTwos >= 2 || countThrees >= 2 || countFives >= 2 || countSixes >= 2))
						|| (countFives >= 3 && (countOnes >= 2 || countTwos >= 2 || countThrees >= 2 || countFours >= 2 || countSixes >= 2))
						|| (countSixes >= 3 && (countOnes >= 2 || countTwos >= 2 || countThrees >= 2 || countFours >= 2 || countFives >= 2)) ) { 
					currentOption = 25;
				} else {
					currentOption = 0;
				}
				scoresOptions[ROW_NUM_FULL_HOUSE][playerNumber] = currentOption;
				displayOption (ROW_NUM_FULL_HOUSE, currentOption);
			}
			if (scores[ROW_NUM_SM_STRAIGHT][playerNumber]==UNASSIGNED_FLAG) {
				/* Check for a small straight. It is worth 30 points */
	
				if ((countOnes >= 1 && countTwos >= 1 && countThrees >= 1 && countFours >= 1)
						|| (countTwos >= 1 && countThrees >= 1 && countFours >= 1 && countFives >= 1)
						|| (countThrees >= 1 && countFours >= 1 && countFives >= 1 && countSixes >= 1)) { 
					currentOption = 30;
				} else {
					currentOption = 0;
				}
				scoresOptions[ROW_NUM_SM_STRAIGHT][playerNumber] = currentOption;
				displayOption (ROW_NUM_SM_STRAIGHT, currentOption);
			}
			if (scores[ROW_NUM_LG_STRAIGHT][playerNumber]==UNASSIGNED_FLAG) {
				/* Check for a large straight. It is worth 40 points */
	
				if ((countOnes >= 1 && countTwos >= 1 && countThrees >= 1 && countFours >= 1 && countFives >= 1)
						|| (countTwos >= 1 && countThrees >= 1 && countFours >= 1 && countFives >= 1 && countSixes >= 1)) { 
					currentOption = 40;
				} else {
					currentOption = 0;
				}
				scoresOptions[ROW_NUM_LG_STRAIGHT][playerNumber] = currentOption;
				displayOption (ROW_NUM_LG_STRAIGHT, currentOption);
			}
			
			/* The only value that results in Yahtzee not being evaluated is if it has a zero. If it has a -1 then make it 50.
			 * If it has a previous value (not 0) then add 50 to it.*/
			if (scores[ROW_NUM_YAHTZEE][playerNumber]!=0) {
				if ((countOnes >= 5 || countTwos >= 5 || countThrees >= 5 || countFours >= 5 || countFives >= 5 || countSixes >= 5)) { 
					if (scores[ROW_NUM_YAHTZEE][playerNumber] == UNASSIGNED_FLAG) {
						currentOption = 50;
					} else {
						currentOption = scores[ROW_NUM_YAHTZEE][playerNumber] + 50;
					}
					scoresOptions[ROW_NUM_YAHTZEE][playerNumber] = currentOption;
					displayOption (ROW_NUM_YAHTZEE, currentOption);
				} else {
					if (scores[ROW_NUM_YAHTZEE][playerNumber] == UNASSIGNED_FLAG) {
						currentOption = 0;
						scoresOptions[ROW_NUM_YAHTZEE][playerNumber] = currentOption;
						displayOption (ROW_NUM_YAHTZEE, currentOption);
					}
					// If already has a score other than 0 and has not earned another Yahtzee then there should be no option displayed.
					// Cannot change the current score back to 0.
				}
			}
			if (scores[ROW_NUM_CHANCE][playerNumber]==UNASSIGNED_FLAG) {
				/* Chance is available until it is used. There is not criteria other than only
				 * being able to use it once. */
				currentOption = sum;
				scoresOptions[ROW_NUM_CHANCE][playerNumber] = currentOption;
				displayOption (ROW_NUM_CHANCE, currentOption);
			}
		}


	/* Procedure that hides the score options. */
	private void hideOptions() {
		for (int i = 0; i < GRID_ROWS; i++) {
			scoreGrid[i][SCORE_OPTION_COL].setVisible(false);
			scoreGridLabel[i][SCORE_OPTION_COL].setVisible(false);
		}
		
	}


	/* displays the value on the applicable score option button and
	 * makes it visible. */
	private void displayOption(int rowNumber, int value) {
		/* Set the value of the score option button to the score the user is able to
		 * select at that time. Need to make that option visible. */ 
		scoreGridLabel[rowNumber][SCORE_OPTION_COL].setLabel(String.valueOf(value));
		scoreGrid[rowNumber][SCORE_OPTION_COL].setVisible(true);
		scoreGridLabel[rowNumber][SCORE_OPTION_COL].setVisible(true);
		
		/* adjust the justification - make it right justified */
	
		double labelX = scoreGrid[rowNumber][SCORE_OPTION_COL].getX() + scoreGrid[rowNumber][SCORE_OPTION_COL].getWidth() - scoreGridLabel[rowNumber][SCORE_OPTION_COL].getWidth();
		double labelY = scoreGrid[rowNumber][SCORE_OPTION_COL].getY() + (scoreGrid[rowNumber][SCORE_OPTION_COL].getHeight() + scoreGridLabel[rowNumber][SCORE_OPTION_COL].getHeight())/2;
		scoreGridLabel[rowNumber][SCORE_OPTION_COL].setLocation((int)labelX, (int)labelY);
	}

	
	private void processScoreSelection (int category) {
		// For the current player process the selected category
		System.out.println("Player: " + playerNames[activePlayer]);
		System.out.println("Category: " + category);
		int newValue = scoresOptions[category][activePlayer];
		scores[category][activePlayer] = newValue;
		setScoreDisplay (category, activePlayer, newValue);
		
		// Calculate section 1 and determine if a bonus is in order.
		int tempValue = 0;
		int section1ValueCount = 0;	// Used to detect when all section 1 categories are scored in case the bonus needs to be marked as unachieved.
		int section1Subtotal = 0;
		int section2Subtotal = 0;
		for (int i=ROW_NUM_ONES;i<=ROW_NUM_SIXES;i++) {
			tempValue = scores[i][activePlayer];
			if (tempValue != UNASSIGNED_FLAG) {
				section1Subtotal += scores[i][activePlayer];
				section1ValueCount ++;
			}
		}
		scores[ROW_NUM_SECTION_1_PRE_BONUS_SUBTOTAL][activePlayer] = section1Subtotal;
		setScoreDisplay (ROW_NUM_SECTION_1_PRE_BONUS_SUBTOTAL, activePlayer, section1Subtotal);
		
		// If the temp sum is greater than the bonus threshold then assign that value and add to the sum for the section 1 total.
		// If not, but there are still unassigned categories in section 1 then leave the bonus blank. If all categories are assigned
		// and the bonus threshold has not been reached then indicate 0.
		if (section1Subtotal >= BONUS_THRESHOLD) {
			scores[ROW_NUM_BONUS][activePlayer] = BONUS_VALUE;
			setScoreDisplay (ROW_NUM_BONUS, activePlayer, BONUS_VALUE);
			section1Subtotal += BONUS_VALUE;
		} else if (section1ValueCount < N_SECTION_1_SCORES) {
			scores[ROW_NUM_BONUS][activePlayer] = UNASSIGNED_FLAG;
			setScoreDisplay (ROW_NUM_BONUS, activePlayer, UNASSIGNED_FLAG);			
		} else {
			scores[ROW_NUM_BONUS][activePlayer] = 0;
			setScoreDisplay (ROW_NUM_BONUS, activePlayer, 0);
		}
		scores[ROW_NUM_SECTION_1_SUBTOTAL][activePlayer] = section1Subtotal;
		setScoreDisplay (ROW_NUM_SECTION_1_SUBTOTAL, activePlayer, section1Subtotal);
		
		
		// Calculate section 2 and the overall total.
		for (int i=ROW_NUM_THREE_OF_A_KIND;i<=ROW_NUM_CHANCE;i++) {
			tempValue = scores[i][activePlayer];
			if (tempValue != UNASSIGNED_FLAG) {
				section2Subtotal += scores[i][activePlayer];
			}
		}
		scores[ROW_NUM_SECTION_2_SUBTOTAL][activePlayer] = section2Subtotal;
		setScoreDisplay (ROW_NUM_SECTION_2_SUBTOTAL, activePlayer, section2Subtotal);
		
		scores[ROW_NUM_TOTAL][activePlayer] = section2Subtotal + section1Subtotal;
		setScoreDisplay (ROW_NUM_TOTAL, activePlayer, section2Subtotal + section1Subtotal);
		
		
		// Hide the score options.
		hideOptions();
		
		// Hide the dice
		for (int i=0; i<N_DICE; i++) {
			remove(yahtzeeDice[i]);
		}
		
		// Reset the game control variable(s) and displayed instructions
		activePlayer = getNextPlayer();
		if (activePlayer == UNASSIGNED_FLAG) {
			// If No players need to take any more turns ... use this flag as the "game over flag".
			System.out.println("Game Over");
			stateGamePlay = POLLING_USER_FOR_NEW_GAME;
			statusBar.setText("GAME OVER");
			// Unhide the button (it is hidden if the final roll is made)
			rollButton.setEnabled(false);
			rollButton.setVisible(false);
		} else {
			System.out.println("New Player " + activePlayer);
			
			stateGamePlay = WAITING_FOR_FIRST_ROLL;
			statusBar.setText(playerNames[activePlayer] + ": Click button for first roll.");
			rollButton.setText("First Roll");
			rollButton.setActionCommand("firstDiceRollCommand");
			
			// Unhide the button (it is hidden if the final roll is made)
			rollButton.setEnabled(true);
			rollButton.setVisible(true);

			
		}
	}

	/* Determine the next player. If no players have any remaining moves then return the unassigned flag. */
	private int getNextPlayer () {
		// Need to check each player starting with the next one. Be careful not to create an infinite loop
			
		int testNextPlayer;
		for (int playersTested = 0; playersTested < N_PLAYERS; playersTested ++) {
			testNextPlayer = activePlayer % N_PLAYERS + + playersTested + 1;
			// System.out.println("Testing player: " + playerNames[testNextPlayer]);
			// Check all of the scores for that player to see if they still need to take a turn.
			// If even one value is unassigned then they need to take their turn.
			for (int i = ROW_NUM_ONES;i <= ROW_NUM_SIXES;i++) {
				if (scores[i][testNextPlayer] == UNASSIGNED_FLAG) {
					return testNextPlayer;
				}
			}
			
			for (int i = ROW_NUM_THREE_OF_A_KIND;i <= ROW_NUM_CHANCE;i++) {
				if (scores[i][testNextPlayer] == UNASSIGNED_FLAG) {
					return testNextPlayer;
				}
			}
		}
			
		// If no next player is found then return the error flag.
		return UNASSIGNED_FLAG;
	}
	
	
	/* Update the scores inside the grid */
	private void setScoreDisplay (int row, int col, int newValue) {
		if (newValue != UNASSIGNED_FLAG) {
			scoreGridLabel[row][activePlayer].setLabel(String.valueOf(newValue));
			// center justify
			double labelX = scoreGrid[row][col].getX() + (scoreGrid[row][col].getWidth() - scoreGridLabel[row][col].getWidth())/2;
			double labelY = scoreGrid[row][col].getY() + (scoreGrid[row][col].getHeight() + scoreGridLabel[row][col].getHeight())/2;
			scoreGridLabel[row][col].setLocation((int)labelX, (int)labelY);
		}
		// Else just leave it blank.
	}

	// Get the variable for use in the state machine.
	private String getPlayerName (int playerNumber) {
		return playerNames[playerNumber];
	}
	

		/* Constants and variables for Overall Game Play */
		/* Constants */
		private static final int APPLICATION_HEIGHT = 850;
		private static final int APPLICATION_WIDTH = 1155;
		private static final int REROLL_SECTION_START = 0;	// top of the keep section
		private static final int SECTION_SPACING = 0;
		private static final int N_DICE = 5;	// Hard code the game for 5 dice
		
		/* Yahtzee State Machine Constants*/
		private static final int SETTING_UP_GAME = 1;
		private static final int PLAYING_GAME = 2;
		private static final int DEAD_SCREEN = 3;
		private static final int PREFERENCES = 4;
		private static final int VIEW_HIGH_SCORES = 5;
		
		/* Game Play State Machine Constants*/
		private static final int WAITING_FOR_FIRST_ROLL = 1;
		private static final int WAITING_FOR_SECOND_ROLL = 2;
		private static final int WAITING_FOR_THRID_ROLL = 3;
		private static final int WAITING_FOR_SCORE_SELECTION = 4;
		private static final int POLLING_USER_FOR_NEW_GAME = 5;
		
		/* Variables */
		
		// Dice sections
		private DiceSection rerollSection;
		private DiceSection keepSection;
		
		// Score Section
		private ScoreSection scoreBoard;
	
		// Buttons and Labels for instructions
		private JLabel statusBar = new JLabel();
		private JButton rollButton = new JButton ("the Btn");
	
		private ColorScheme_Dice currentColorScheme = ColorScheme_Dice.white_Red;
	
		// Mouse listener variables
		// private GObject mouseObject;
		private Die selectedDie;
		
		/* when a dragging event occurs, get the starting point of the object (not the mouse click point). 
		 * the last point is the mouse click and current mouse location point*/
		private DiceSection startSection;
		private GPoint startPoint;
		private GPoint last;
	
		private Die[] yahtzeeDice = new Die[N_DICE];
		private boolean diceCreated;
	
		// Variable for active player
		private int activePlayer;
		private String[] playerNames = new String[N_PLAYERS + 1];
		
		// Variables for the state machine status
		private int stateYahtzee;
		private int stateGamePlay;
		
		/* Background colors - felt color for the dice compartments */
		Color greenFelt = new Color(0,90,0);
		
	
	
	
	
	// Constants and variables for scoring section
		// Score Section Header Label Constants
		private final static int HEADER_HEIGHT = 35;
		
		private final static int N_PLAYERS = 2;
		private final static int N_HEADER_ROWS = 1;
		private final static int N_SECTION_1_SCORES = 6;
		private final static int N_SECTION_1_PRE_BONUS_SUBTOTAL_ROWS = 1;
		private final static int N_SECTION_1_BONUS_ROWS = 1;
		private final static int N_SECTION_1_SUBTOTAL_ROWS = 1;
		private final static int N_SECTION_2_SCORES = 7;
		private final static int N_SECTION_2_SUBTOTAL_ROWS = 1;
		private final static int N_TOTAL_ROWS = 1;
		private final static int GRID_ROWS = N_HEADER_ROWS + N_SECTION_1_SCORES + + N_SECTION_1_PRE_BONUS_SUBTOTAL_ROWS + N_SECTION_1_BONUS_ROWS + N_SECTION_1_SUBTOTAL_ROWS + N_SECTION_2_SCORES + N_SECTION_2_SUBTOTAL_ROWS + N_TOTAL_ROWS; 
		private final static int GRID_COLS = N_PLAYERS + 2;
		
		/* Boxes to form the scoring grid*/
		private final static int SCORE_BOX_WIDTH = 100;
		private final static int SCORE_BOX_HEIGHT = 25;		// bonus row will utilize this
		private final static int CATEGORY_BOX_WIDTH = 180;
		private final static int HEADER_BOX_HEIGHT = 35;
		private final static int SUBTOTAL_BOX_HEIGHT = 40;
		private final static int TOTAL_BOX_HEIGHT = 45;
		private final static int SCORE_BUTTON_WIDTH = 80;
		private final static int GRID_CORNER = 10;	// diameter of circle forming corner, not the radius
		private final static int GRID_INSIDE_MARGIN = 5;	// margin between grid squares
		private final static int GRID_OUTSIDE_MARGIN = 20;	// margin around the outside of the grid
		private final static int TEXT_INDENT = 10;

		private final static int BORDER = 10;				// thickness of the brown border around the dice compartment
		private final static int BORDER_INSIDE_WIDTH = CATEGORY_BOX_WIDTH + (N_PLAYERS)*SCORE_BOX_WIDTH + SCORE_BUTTON_WIDTH + (N_PLAYERS+1)*GRID_INSIDE_MARGIN + 2*GRID_OUTSIDE_MARGIN;
		private final static int BORDER_OUTSIDE_WIDTH = BORDER_INSIDE_WIDTH + 2*BORDER;
		private final static int BORDER_INSIDE_HEIGHT = HEADER_HEIGHT + GRID_INSIDE_MARGIN + N_HEADER_ROWS*HEADER_BOX_HEIGHT + (N_SECTION_1_PRE_BONUS_SUBTOTAL_ROWS + N_SECTION_1_SUBTOTAL_ROWS + N_SECTION_2_SUBTOTAL_ROWS)*SUBTOTAL_BOX_HEIGHT + (N_SECTION_1_SCORES + N_SECTION_1_BONUS_ROWS + N_SECTION_2_SCORES)*SCORE_BOX_HEIGHT + N_TOTAL_ROWS*TOTAL_BOX_HEIGHT + (GRID_ROWS-1)*GRID_INSIDE_MARGIN +2*GRID_OUTSIDE_MARGIN;
		private final static int BORDER_OUTSIDE_HEIGHT = BORDER_INSIDE_HEIGHT + 2*BORDER;
		
		// Corner dimensions are the diameter of circle forming corner, not the radius
		private final static int BORDER_INSIDE_CORNER = GRID_CORNER + 2*GRID_OUTSIDE_MARGIN;
		private final static int BORDER_OUTSIDE_CORNER = BORDER_INSIDE_CORNER + 2*BORDER;
		
		// Colors & Fonts
		private final static Color BROWN_BORDER = new Color(96, 46, 0);
		private final static Color GREEN_FELT = new Color(0, 90, 0);
		
		// Aliases for category row numbers
		// Row 0 is the header row
		private final static int ROW_NUM_ONES = 1;
		private final static int ROW_NUM_TWOS = 2;
		private final static int ROW_NUM_THREES = 3;
		private final static int ROW_NUM_FOURS = 4;
		private final static int ROW_NUM_FIVES = 5;
		private final static int ROW_NUM_SIXES = 6;
		private final static int ROW_NUM_SECTION_1_PRE_BONUS_SUBTOTAL = 7;
		private final static int ROW_NUM_BONUS = 8;
		private final static int ROW_NUM_SECTION_1_SUBTOTAL = 9;
		private final static int ROW_NUM_THREE_OF_A_KIND = 10;
		private final static int ROW_NUM_FOUR_OF_A_KIND = 11;
		private final static int ROW_NUM_FULL_HOUSE = 12;
		private final static int ROW_NUM_SM_STRAIGHT = 13;
		private final static int ROW_NUM_LG_STRAIGHT = 14;
		private final static int ROW_NUM_YAHTZEE = 15;
		private final static int ROW_NUM_CHANCE = 16;
		private final static int ROW_NUM_SECTION_2_SUBTOTAL = 17;
		private final static int ROW_NUM_TOTAL = 18;
		
		// Alias for the score options column
		private final static int SCORE_OPTION_COL = GRID_COLS - 1;
		
		// Flag to be used to indicate when a score category is unassigned (not the same as zero)
		private final static int UNASSIGNED_FLAG = -1;
		
		// Threshold section 1 subtotal for achieving the bonus.
		private final static int BONUS_THRESHOLD = 63;
		private final static int BONUS_VALUE = 35;
		
		// Aliases for Box Type parameter - drives application of font size and row height
		private final static int SCORE_ROW = 0;
		private final static int HEADER_ROW = 1;
		private final static int SUBTOTAL_ROW = 2;
		private final static int TOTAL_ROW = 3;
		
		// Aliases for justification of text in box. Scores should all be right justified
		// so their digits line up properly
		private final static int JUSTIFICATION_LEFT = 0;
		private final static int JUSTIFICATION_RIGHT = 1;
		private final static int JUSTIFICATION_CENTER = 2;
		
		// Private variables for the graphics object structure and appearance
		private GRoundRect outsideBoundary;
		private GRoundRect insideBoundary;
//		private GRoundRect[][] scoreGrid = new GRoundRect[GRID_ROWS][GRID_COLS];	// Use a 2-D array of points as
//		private GLabel[][] scoreGridLabel = new GLabel[GRID_ROWS][GRID_COLS];	// Use a 2-D array of points as
		
		
		// Section Header
		private GLabel sectionHeader = new GLabel("Scores");

		// one grid for everything
		private GRoundRect[][] scoreGrid = new GRoundRect[GRID_ROWS][GRID_COLS];
		private GLabel[][] scoreGridLabel = new GLabel[GRID_ROWS][GRID_COLS];
				
		// grid and labels for the header row
		private GRoundRect[][] headerGrid = new GRoundRect[N_HEADER_ROWS][GRID_COLS];
		private GLabel[][] headerGridLabel = new GLabel[N_HEADER_ROWS][GRID_COLS];
		
		// grid and labels for section 1 scores
		private GRoundRect[][] section1ScoreGrid = new GRoundRect[N_SECTION_1_SCORES][GRID_COLS];
		private GLabel[][] section1ScoreGridLabel = new GLabel[N_SECTION_1_SCORES][GRID_COLS];
		
		// grid and labels for section 1 pre-bonus subtotals
		private GRoundRect[][] section1PreBonusSubtotalGrid = new GRoundRect[N_SECTION_1_PRE_BONUS_SUBTOTAL_ROWS][GRID_COLS];
		private GLabel[][] section1PreBonusSubtotalGridLabel = new GLabel[N_SECTION_1_PRE_BONUS_SUBTOTAL_ROWS][GRID_COLS];
		
		// grid and labels for section 1 bonus scores
		private GRoundRect[][] section1BonusGrid = new GRoundRect[N_SECTION_1_BONUS_ROWS][GRID_COLS];
		private GLabel[][] section1BonusGridLabel = new GLabel[N_SECTION_1_BONUS_ROWS][GRID_COLS];
		
		// grid and labels for section 1 subtotals
		private GRoundRect[][] section1SubtotalGrid = new GRoundRect[N_SECTION_1_SUBTOTAL_ROWS][GRID_COLS];
		private GLabel[][] section1SubtotalGridLabel = new GLabel[N_SECTION_1_SUBTOTAL_ROWS][GRID_COLS];
		
		// grid and labels for section 2 scores
		private GRoundRect[][] section2ScoreGrid = new GRoundRect[N_SECTION_2_SCORES][GRID_COLS];
		private GLabel[][] section2ScoreGridLabel = new GLabel[N_SECTION_2_SCORES][GRID_COLS];
		
		// grid and labels for section 2 subtotals
		private GRoundRect[][] section2SubtotalGrid = new GRoundRect[N_SECTION_2_SUBTOTAL_ROWS][GRID_COLS];
		private GLabel[][] section2SubtotalGridLabel = new GLabel[N_SECTION_2_SUBTOTAL_ROWS][GRID_COLS];
		
		// grid and labels for total section
		private GRoundRect[][] totalGrid = new GRoundRect[GRID_ROWS][GRID_COLS];
		private GLabel[][] totalGridLabel = new GLabel[GRID_ROWS][GRID_COLS];
		
		/* Will not use all of these cells, but this makes it so the score row number will be the same as its position in the score grid.
		 * Also the player number will correspond with the column number. Options will temporarily contain the valid options that can be
		 * selected. scores contains the actual score. */
		private int[][] scores = new int[GRID_ROWS][GRID_COLS];
		private int[][] scoresOptions = new int[GRID_ROWS][GRID_COLS];

		private GPoint headerSectionStartPoint;
		private GPoint section1ScoreStartPoint;
		private GPoint section1PreBonusSubtotalStartPoint;
		private GPoint section1BonusStartPoint;
		private GPoint section1SubtotalStartPoint;
		private GPoint section2ScoreStartPoint;
		private GPoint section2SubtotalStrartPoint;
		private GPoint totalStartPoint;
		
		private Font sectionHeaderLabelFont = Font.decode("Arial-18");
		private Color stdBoxColor = Color.gray;
		private Color headerBoxFontColor = Color.black;
		private Font headerRowFont = Font.decode("Arial-24");
		private Font scoreRowFont = Font.decode("Arial-18");
		private Font subtotalRowFont = Font.decode("Arial-28");
		private Font totalRowFont = Font.decode("Arial-32");
		
		private ColorScheme_Dice[] playerColorScheme = new ColorScheme_Dice[N_PLAYERS];
		
		// Custom colors
		private final static Color PURPLE = new Color(112, 48, 160);
		
		
//		/* Variable used when placing a new die.  These variables represent the
//		 * row and column numbers for the grid on which it should be placed.*/
//		private int targetGridRow, targetGridCol;
			
		// List of dice
		private ArrayList<Die> diceList = new ArrayList<Die>();
		
		/* Try using an array instead of a list. The list only has one spot per die
		 * but this array should help coordinate the location of the dice in the grid as well*/
		private final static int dieArraySize = GRID_ROWS*GRID_COLS;
		private int nDice;	// Actual number of dice in this section, not the size of the array

	
	
	// Button listeners to deal with pressing the roll button
	class RollButtonListener implements ActionListener {

	    public void actionPerformed(ActionEvent e) { 
			// The action to be taken depends on the status of the state machines
			if (e.getActionCommand().equals("firstDiceRollCommand")) {
				// Add each die to the reroll section in the next available spot.
				// Roll each to get an initial value.
				for (int i = 0; i < N_DICE; i++) {
					
					// Remove from the keep section and add to the reroll section
					keepSection.removeDie(yahtzeeDice[i]);
					rerollSection.removeDie(yahtzeeDice[i]);
					rerollSection.addDie(yahtzeeDice[i]);
					
					yahtzeeDice[i].rollDice();
					
					// Add the die to the canvas.
					add (yahtzeeDice[i]);				
				}
				
				// display the score options based on the current dice values
				displayScoreOption(activePlayer, yahtzeeDice);
				
				// Update button and instructions for second roll
				statusBar.setText(playerNames[activePlayer] + ": Select score or move dice to retain to the keep section and roll again.");
				rollButton.setText("Second Roll");
				rollButton.setActionCommand("secondDiceRollCommand");
				
				// Update the state value
				stateGamePlay = WAITING_FOR_SECOND_ROLL;
			} else if (e.getActionCommand().equals("secondDiceRollCommand")) {
				// reroll only the dice in the reroll section
				rerollSection.rollDice();

				// display the score options based on the current dice values
				displayScoreOption(activePlayer, yahtzeeDice);

				// Update button and instructions for third roll
				statusBar.setText(playerNames[activePlayer] + ": Select score or move dice to retain to the keep section and roll again.");
				rollButton.setText("Final Roll");
				rollButton.setActionCommand("thirdDiceRollCommand");
				
				// Update the state value
				stateGamePlay = WAITING_FOR_THRID_ROLL;
			} else if (e.getActionCommand().equals("thirdDiceRollCommand")) {
				// reroll only the dice in the reroll section
				rerollSection.rollDice();

				// display the score options based on the current dice values
				displayScoreOption(activePlayer, yahtzeeDice);
				
				// Update button and instructions. After the third roll no additional rolls are allowed.
				statusBar.setText(playerNames[activePlayer] + ": Select score to end turn.");
				rollButton.setText("");
				rollButton.setActionCommand("buttonInactive");
				rollButton.setEnabled(false);
				rollButton.setVisible(false);
				
				// Update the state value
				stateGamePlay = WAITING_FOR_SCORE_SELECTION;
			} else {
				System.out.println("Error: The roll dice button was pressed at an invalid point in the program.");
			}
	    }
	}
}
