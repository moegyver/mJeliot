package jeliot.avinteraction;

import javax.swing.*;

import jeliot.adapt.ModelEvent;
import jeliot.adapt.UMInteraction;

import avinteraction.backend.BackendInterface;

import java.awt.*;

/**
 *
 * @author Niko Myller
 */
public class PointMachine implements BackendInterface {

    //~ Static fields/initializers ------------------------------

    /** Debug level */
    private static boolean DEBUG;

    //~ Instance fields -----------------------------------------

    /** Name of the Backend */
    private final String NAME = "simple PointMachine";

    /** points the student earned */
    private int earnedPoints = 0;

    /** points available */
    private int allPoints = 0;

	private UMInteraction userModel = null;

    //~ Constructors --------------------------------------------

    /**
     * Standard Constructor, disables debugging
     */
    public PointMachine() {
        this(false);
    }

    /**
     * Constructs and enables user modelling
     */
    public PointMachine(UMInteraction userModel){
    	this.userModel = userModel;
    }	
    /**	
     * Constructs a PointsMachine object with debugging set to
     * on if tf is true, otherwise to false.
     *
     * @param tf Boolean value whether to activate debuggin or
     * 		  not
     */
    public PointMachine(boolean tf) {
        DEBUG = tf;
    }

    //~ Methods -------------------------------------------------

    /**
     * Receives the students answer (correct or not) to question
     * with  the id 'questionID' and the number of points that
     * were available for this question.
     *
     * @param questionID The questions interaction-id
     * @param correct Whether the answer was correct or not.
     * @param points The points available for the correct answer
     * @param achieved The points achieved for the correct
     * 		  answer
     *
     * @return Whether to show the answer or not.
     */
    public boolean submitAnswer(String questionID, boolean correct, int points,
            int achieved, Integer[] conceptIdentifier) {
        
        //TODO: add here the recording of the conceptIdentifiers
    	ModelEvent UMEvent;
        //TODO: set the properties of the event
        System.out.print("\t\tBackend received answer to \"" + questionID
                + "\": ... ");
        if (correct) {
        	UMEvent = new ModelEvent("questions.correct",conceptIdentifier,"1");
            System.out.println("correct.");
        } else {
        	UMEvent = new ModelEvent("questions.wrong",conceptIdentifier,"1");
        	System.out.println("wrong.");
        }
        
        for (int i = 0; i < conceptIdentifier.length; i++) {
            System.out.println(conceptIdentifier[i]);
        }

        allPoints += points;
        earnedPoints += achieved;
        userModel.recordEvent(UMEvent);
        // we want the results to be displayed
        return true;
    }

    /**
     * Shows the earned points in relation to the total count of
     * points, calculates the percentage of correct answers and
     * shows it as well.
     */

    public void showResults(Component component) {
        int percentage = 0;
        if (allPoints > 0) {
            percentage = (earnedPoints * 100) / allPoints;

            if (DEBUG) {
                System.out.println("\nFinal results: " + earnedPoints + " out of "
                        + allPoints + " points, that means " + percentage + "%");
            }

            JOptionPane.showMessageDialog(JOptionPane.getFrameForComponent(component), "Final results: " + earnedPoints + " out of "
                    + allPoints + " points, that means " + percentage + "%", "Final Results", JOptionPane.PLAIN_MESSAGE);
        }
    }

    /**
     * Tells the interaction module whether to re-enable the
     * submit button or not.
     *
     * @return A boolean value which determines whether the
     * 		   submit button should be re-enabled or not.
     */
    public boolean enableSubmit() {
        return false;
    }

    /**
     * gives back the name of the backend
     *
     * @return a String containing the name of the backend
     */
    public String toString() {
        return NAME;
    }
}
