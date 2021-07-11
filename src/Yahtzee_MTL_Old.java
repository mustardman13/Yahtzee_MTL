import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JLabel;

import acm.graphics.GPoint;
import acm.program.GraphicsProgram;


public class Yahtzee_MTL_Old extends GraphicsProgram {

	
	/**
	 * @param args
	 */
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

		
		// Add the status label to the top of the application
		statusBar.setText("Player " + activePlayer + ": Click button for first roll.");
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
		
		rerollSection = new DiceSection (0,REROLL_SECTION_START,"Reroll");
		add (rerollSection);
		
		double keepSectionY = rerollSection.getY() + rerollSection.getHeight() + SECTION_SPACING;
		keepSection = new DiceSection (0,keepSectionY,"Keep");
		add (keepSection);

		double scoreSectionX = rerollSection.getWidth();
		scoreBoard = new ScoreSection (scoreSectionX, REROLL_SECTION_START);
		add (scoreBoard);
		
		String[] playerNames = {"Michelle", "Michael", "Riley", "Rebecca", "Jolie", "Sam"};
		scoreBoard.updatePlayerNames(playerNames);
		
		// Create an array of dice
		for (int i = 0; i < N_DICE; i++) {
			yahtzeeDice[i] = new Die (0, 0, currentColorScheme);
			yahtzeeDice[i].rollDice();
		}
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

	// Variable for active player
	private int activePlayer;
	
	// Variables for the state machine status
	private int stateYahtzee;
	private int stateGamePlay;
	
	/* Background colors - felt color for the dice compartments */
	Color greenFelt = new Color(0,90,0);
	
	
	// Button listeners to deal with pressing the roll button
	class RollButtonListener implements ActionListener {

	    public void actionPerformed(ActionEvent e) {    
			// The action to be taken depends on the status of the state machines
			if (e.getActionCommand().equals("firstDiceRollCommand")) {
				// Add each die to the reroll section in the next available spot.
				// Roll each to get an initial value.
				for (int i = 0; i < N_DICE; i++) {
					yahtzeeDice[i] = new Die (0, 0, currentColorScheme);
					yahtzeeDice[i].rollDice();
					int resultCheck = rerollSection.addDie(yahtzeeDice[i]);
					if (resultCheck == 0) {
						System.out.println("Error: Not enough room for all of the dice.");
					}

				// Add the die to the canvas.
				add (yahtzeeDice[i]);
				}
				
				// display the score options based on the current dice values
				scoreBoard.displayScoreOption(activePlayer, yahtzeeDice);
				
				// Update button and instructions for second roll
				statusBar.setText("Player " + activePlayer + ": Select score or move dice to retain to the keep section and roll again.");
				rollButton.setText("Second Roll");
				rollButton.setActionCommand("secondDiceRollCommand");
				
				// Update the state value
				stateGamePlay = WAITING_FOR_SECOND_ROLL;
			} else if (e.getActionCommand().equals("secondDiceRollCommand")) {
				// reroll only the dice in the reroll section
				rerollSection.rollDice();

				// display the score options based on the current dice values
				scoreBoard.displayScoreOption(activePlayer, yahtzeeDice);

				// Update button and instructions for third roll
				statusBar.setText("Player " + activePlayer + ": Select score or move dice to retain to the keep section and roll again.");
				rollButton.setText("Final Roll");
//				rollButton.setActionCommand("thirdDiceRollCommand");
				
				// Update the state value
//				stateGamePlay = WAITING_FOR_THRID_ROLL;
			} else if (e.getActionCommand().equals("thirdDiceRollCommand")) {
				// reroll only the dice in the reroll section
				rerollSection.rollDice();

				// display the score options based on the current dice values
				scoreBoard.displayScoreOption(activePlayer, yahtzeeDice);
				
				// Update button and instructions. After the third roll no additional rolls are allowed.
				statusBar.setText("Player " + activePlayer + ": Select score to end turn.");
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
