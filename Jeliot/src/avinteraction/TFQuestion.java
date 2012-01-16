package avinteraction;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;

import jeliot.util.Util;

/**
 * True-False-Question. Encapsulates the data and visualizes the
 * question.
 *
 * @author Gina Haeussge,
 * 		   huge(at)rbg.informatik.tu-darmstadt.de, building
 * 		   upon work by Laura L. Norton
 */
public class TFQuestion extends Question implements TFQuestionInterface {

    private static ResourceBundle avInteractionProperties = ResourceBundle
            .getBundle("avinteraction.resources.properties", Locale
                    .getDefault());

    //~ Static fields/initializers ------------------------------

    /** The Debug level */
    private static boolean DEBUG;

    //~ Instance fields -----------------------------------------

    /**
     * Defines whether the correct answer is "right" or "wrong"
     */
    private boolean correctAnswer;

    /** JRadioButton for "right" */
    protected JRadioButton trueBox;

    /** JRadioButton for "wrong" */
    protected JRadioButton falseBox;

    /** ButtonGroup grouping the two RadioButtons */
    protected ButtonGroup answerBoxes;

    /**
     * The comment that should be displayed if question is
     * answered wrong
     */
    private String theComment = "";

    //~ Constructors --------------------------------------------

    /**
     * The constructor of a true-false-question. Disables
     * debugging.
     *
     * @param id The id used for calling the question.
     * @param group The group id of the question. Leave blank if
     * 		  none.
     */
    public TFQuestion(String id, String group, Integer[] concept) {
        this(id, group, false, concept);
    }

    /**
     * 
     * @param id
     * @param group
     * @param tf
     */
    public TFQuestion(String id, String group, boolean tf) {
        this(id, group, tf, new Integer[0]);
    }
    
    /**
     * The constructor of a true-false-question. Sets debugging
     * to the value of tf.
     *
     * @param id The id used for calling the question.
     * @param group The group id of the question. Leave blank if
     * 		  none.
     * @param tf The debug level.
     */
    public TFQuestion(String id, String group, boolean tf, Integer[] concepts) {
        super(concepts);
        this.objectID = id;
        this.groupID = group;
        questionText = "";
        correctAnswer = false;
        DEBUG = tf;
    }

    //~ Methods -------------------------------------------------

    /**
     * Set the correct answer, true for "right", false for
     * "wrong".
     *
     * @param answer The answers boolean value.
     */
    public void setAnswer(boolean answer) {
        correctAnswer = answer;
    }

    /**
     * Use this method to construct the GUI for presenting the
     * question and that will be shown in the main window used
     * for interaction. It MUST be called before showing the
     * question.
     */
    public void makeGUI() {
        JLabel headlineLabel;
        JScrollPane questionScroller;
        Icon TFIcon;
        JRadioButton fakeBox;
        JPanel boxPanel;
        JPanel bottomPanel;
        JPanel buttonPanel;

        // sets the layout manager of the object
        setLayout(new BorderLayout());

        URL imageURL = Util.getResourceURL(avInteractionProperties
                .getString("directory.images")
                + avInteractionProperties.getString("image.tf_question"), this.getClass());

        // constructs the headline of the question window
        TFIcon = new ImageIcon(imageURL);
        headlineLabel = new JLabel(TFIcon, SwingConstants.CENTER);

        // wraps the question, the answer input field and the submit button
        mainPanel = new JPanel(new GridLayout(4, 1, 5, 5));

        // the question, made scrollable using a JScrollPane
        questionOutput = new JTextArea(questionText);
        questionScroller = new JScrollPane(questionOutput,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        questionOutput.setEditable(false);
        questionOutput.setWrapStyleWord(true);
        questionOutput.setLineWrap(true);

        // the true/false answer buttons
        answerBoxes = new ButtonGroup();
        trueBox = new JRadioButton("True", false);
        falseBox = new JRadioButton("False", false);
        fakeBox = new JRadioButton("null", true);
        answerBoxes.add(trueBox);
        answerBoxes.add(falseBox);
        answerBoxes.add(fakeBox);
        boxPanel = new JPanel(new BorderLayout());
        boxPanel.add(BorderLayout.NORTH, trueBox);
        boxPanel.add(BorderLayout.SOUTH, falseBox);

        // feedback of the system about the current state
        // 	   of answering the question
        setFeedback("No answer submitted yet");
        setFeedbackRed();

        submitButton = new JButton("Submit Response");
        submitButton.addActionListener(this);

        // panel that takes care of the submit button
        buttonPanel = new JPanel();
        buttonPanel.add(submitButton);

        // lower part of the main panel, consists of 
        //     buttons and information labels
        bottomPanel = new JPanel(new GridLayout(2, 1));
        bottomPanel.add(buttonPanel);

        // put together what belongs together
        mainPanel.add(questionScroller);
        mainPanel.add(boxPanel);
        mainPanel.add(bottomPanel);
        mainPanel.add(feedbackScroller);
        add(BorderLayout.NORTH, headlineLabel);
        add(BorderLayout.CENTER, mainPanel);

        if (DEBUG)
            System.out.println("\t\tPanel created");

        guiBuilded = true;
    }

    /**
     * Returns the comment saved for this question if answer was
     * wrong, otherwise returns an empty string.
     *
     * @return Comment or emtpy string.
     */
    public String getComment() {
        if (!correct)
            return theComment;
        else
            return "";
    }

    /**
     * API method of this question object. It allows to set a
     * comment that should be displayed if the question is
     * answered wrong.
     *
     * @param c The comment that should belong to the question.
     */
    public void setComment(String c) {
        theComment = c;
    }

    /**
     * Returns the title for the interactions window.
     *
     * @return the title of the interactions window.
     */
    public String getTitle() {
        return TITLE;
    }

    /**
     * Resets the interaction object so it can be called again
     * in it's original state.
     */
    public void rebuildQuestion() {
        trueBox.setSelected(false);
        falseBox.setSelected(false);
        setFeedback("");
        submitButton.setEnabled(true);
    }

    /**
     * Evaluate the answer, disable the submit button.
     *
     * @param event The event that occured.
     */
    public void actionPerformed(ActionEvent event) {
        if (submitted) {
            submitButton.setEnabled(false);
            this.parentFrame.dispose();
            return;
        }
        if ((answerBoxes != null)
                && (trueBox.isSelected() || falseBox.isSelected())) {
            submitButton.setEnabled(false);
            correct = ((trueBox.isSelected() && correctAnswer) || (falseBox
                    .isSelected() && !correctAnswer));
            if (correct)
                achievedPoints = points;
            else
                achievedPoints = 0;
            fireSubmit(event);
            submitted = true;
            submitButton.setText("Close");
            submitButton.setEnabled(true);
        }
    }

}
