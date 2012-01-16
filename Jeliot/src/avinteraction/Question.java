package avinteraction;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 * Base class for all questionlike interactions. Current count
 * of instances is stored as well.
 *
 * @author Gina Haeussge, huge(at)rbg.informatik.tu-darmstadt.de
 */
public abstract class Question extends JPanel implements QuestionInterface {

    protected boolean submitted = false;
    
    //~ Static fields/initializers ------------------------------

    /** number of questions. */
    public static int questionCounter = 0;

    //~ Instance fields -----------------------------------------

    /** number of question-instance */
    protected int questionNumber;

    /** String identifying the question */
    protected String objectID;

    /** String identifying the question group */
    protected String groupID;

    /**
     * Integer containing the available points for answering
     * this question
     */
    protected int points;

    /** Integer containing the points achieved. */
    protected int achievedPoints;

    /**
     * Concept identifiers that are related to the question.
     */
    protected Integer[] conceptIdentifiers;
    
    /**
     * HTMLComponent used for displaying feedback concerning the
     * interactions state to the user
     */
    protected JEditorPane feedbackView;

    /** Used to make the feedbackView scrollable. */
    protected JScrollPane feedbackScroller;

    /** Text to show in the feedbackView */
    protected String feedbackText;

    /**
     * After evaluation in the eventlistener, this variable
     * contains whether the answer to the question is correct
     * or not.
     */
    protected boolean correct = false;

    /** The Submitbutton for submitting the given answer. */
    protected JButton submitButton;

    /** The question that should be displayed */
    protected String questionText;

    /** The main panel of the interactions interface. */
    protected JPanel mainPanel;

    /** The text area for displaying the question. */
    protected JTextArea questionOutput;

    /** true if makeGUI got already called, false otherwise */
    protected boolean guiBuilded = false;

    /** Listeners listening for ActionEvents. */
    protected Vector listeners;

    /** The JFrame encapsulating the question's panel */
    protected JDialog parentFrame;

    //~ Constructors --------------------------------------------
    /**
     * Standard constructor. Assigns a number to the question
     * and increments the number of the questions.
     */
    Question(Integer[] concepts) {
        this.conceptIdentifiers = concepts;
        questionNumber = questionCounter;
        questionCounter++;
        feedbackView = new JEditorPane("text/html", "");
        feedbackView.setEditable(false);
        feedbackScroller = new JScrollPane(feedbackView,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        feedbackText = "";

        listeners = new Vector();
    }

    //~ Methods -------------------------------------------------

    /**
     * Build the question's panel.
     */
    public abstract void makeGUI();

    /**
     * Reset the question.
     */
    public abstract void rebuildQuestion();

    /**
     * Retrieve a comment for the question.
     *
     * @return a String containig a comment.
     */
    public abstract String getComment();

    /**
     * Returns wether the answer to the given question was
     * already submitted or not.
     *
     * @return a boolean value telling whether the question
     * 		   already was submitted or not.
     */
    public boolean wasSubmitted() {
        return !submitButton.isEnabled();
    }

    /**
     * (Re)Enables the submit button.
     */
    public void enableSubmit() {
        submitButton.setEnabled(true);
    }

    /**
     * Sets the text of the feedback label to the given string.
     *
     * @param feedback The text that should be displayed.
     */
    public void setFeedback(String feedback) {
        feedbackText = feedback;
        feedbackView.setText("<html><head><title></title></head><body>"
                + feedback + "</body>");
    }

    /**
     * Sets the text of the feedback label to color red
     */
    public void setFeedbackRed() {
        feedbackView.setForeground(Color.RED);
    }

    /**
     * Sets the text of the feedback label to color black
     */
    public void setFeedbackBlack() {
        feedbackView.setForeground(Color.BLACK);
    }

    /**
     * Sets the question to the given text.
     *
     * @param question a String containing the questions text.
     */
    public void setQuestion(String question) {
        questionText = question;
    }

    /**
     * Save the frame object used for displaying the
     * interaction.
     *
     * @param frame The frame object to store.
     */
    public void setJDialog(JDialog frame) {
        parentFrame = frame;
    }

    /**
     * Retrieve the frame used for displaying the interaction
     * compent.
     *
     * @return the frame used for displaying the question.
     */
    public JDialog getJDialog() {
        return parentFrame;
    }

    /**
     * Returns a boolean value showing whether the questionw as
     * answered correct or not.
     *
     * @return Correctness of students answer.
     */
    public boolean isCorrect() {
        return correct;
    }

    /**
     * Get the group id of this question.
     *
     * @return A String containg the group id
     */
    public String getGroupID() {
        return groupID;
    }

    /**
     * Set number of points available for this question.
     *
     * @param pts The number of points available.
     */
    public void setPoints(int pts) {
        points = pts;
    }

    /**
     * Retrieve number of points available for this question.
     *
     * @return The number of points
     */
    public int getPoints() {
        return points;
    }

    /**
     * Retrieve number of points achieved for this question.
     *
     * @return The number of points
     */
    public int getAchievedPoints() {
        return achievedPoints;
    }

    /**
     * Retrieve whether GUI was already constructed by makeGUI
     * or not.
     *
     * @return The value of guiBuilded.
     */
    public boolean getGuiBuilded() {
        return guiBuilded;
    }

    /**
     * Adds a Listener to the list of listeners
     *
     * @param listener The listener to add
     */
    public void addSubmitListener(InteractionModule listener) {
        listeners.add(listener);
    }

    /**
     * Removes a listener from the list of listeners.
     *
     * @param listener The listener to remove
     */
    public void removeSubmitListener(InteractionModule listener) {
        listeners.remove(listener);
    }

    /**
     * Fire the SubmitButtonWasPressed-Event to all
     * InteractionModules listening.
     *
     * @param event The event that caused the firing of the
     * 		  custom event.
     */
    protected void fireSubmit(ActionEvent event) {
        for (Iterator i = listeners.iterator(); i.hasNext();) {
            ((InteractionModule) i.next()).submitPressed(this);
        }
    }

    
    /**
     * Records the concepts that the question concerns.
     * @return
     */
    public Integer[] getConceptIdentifiers() {
        return conceptIdentifiers;
    }

    /**
     * Records the concepts that the question concerns. 
     * @param conceptIdentifiers
     */
    public void setConceptIdentifiers(Integer[] conceptIdentifiers) {
        this.conceptIdentifiers = conceptIdentifiers;
    }
}
