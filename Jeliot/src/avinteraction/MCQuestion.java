package avinteraction;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.net.URL;
import java.util.Locale;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.Vector;

import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JToggleButton;
import javax.swing.SwingConstants;

import jeliot.util.Util;

/**
 * Multiple-Choice-Question. Encapsulates the data and
 * visualizes the  question.
 *
 * @author Gina Haeussge,
 * 		   huge(at)rbg.informatik.tu-darmstadt.de, building
 * 		   upon work by Laura L. Norton
 */
public class MCQuestion extends Question implements MCQuestionInterface {

    private static ResourceBundle avInteractionProperties = ResourceBundle
            .getBundle("avinteraction.resources.properties", Locale
                    .getDefault());

    //~ Static fields/initializers ------------------------------

    /** The Debug level */
    private static boolean DEBUG;

    //~ Instance fields -----------------------------------------

    /** A Vector containg the possible answers to choose from */
    private Vector possibleAnswers;

    /** Permutation function */
    private Vector permutation;

    /**
     * A Vector containing the RadioButton objects representing
     * the  answer possiblities
     */
    private Vector answers;

    /** index of the right answer, counting from 1. */
    private Vector correctAnswer;

    /**
     * flag indicating whether to use RadioButtons or CheckBoxes
     */
    private boolean checkBoxes;

    /** The ButtonGroup grouping the answer possibilities. */
    private ButtonGroup answerBoxes;

    /** The comment to return */
    private String theComment;

    //~ Constructors --------------------------------------------

    /**
     * The constructor of a multiple-choice-question. Disables
     * debugging.
     *
     * @param id The id used for calling the question.
     * @param group The group id of the question. Leave blank if
     * 		  none.
     */
    public MCQuestion(String id, String group) {
        this(id, group, false, new Integer[0]);
    }

    /**
     * 
     * @param id
     * @param group
     * @param concepts
     */
    public MCQuestion(String id, String group, Integer[] concepts) {
        this(id, group, false, concepts);
    }

    /**
     * 
     * @param id
     * @param group
     * @param tf
     */
    public MCQuestion(String id, String group, boolean tf) {
        this(id, group, tf, new Integer[0]);
    }
            
    /**
     * The constructor of a multiple-choice-question, setting
     * debugging to tf.
     *
     * @param id The id used for calling the question.
     * @param group The group id of the question. Leave blank if
     * 		  none.
     * @param tf The debugging level to use.
     */
    public MCQuestion(String id, String group, boolean tf, Integer[] concepts) {
        super(concepts);
        this.objectID = id;
        this.groupID = group;
        possibleAnswers = new Vector();
        questionText = "";
        correctAnswer = new Vector();
        permutation = new Vector();
        checkBoxes = false;
        DEBUG = tf;
    }

    //~ Methods -------------------------------------------------

    /**
     * Sets the index of the current answer to the given
     * paramter.
     *
     * @param answer The index of the current answer, counting
     * 		  from 1.
     */
    public void addCorrectAnswer(int answer) {
        correctAnswer.add(new Integer(answer));
    }

    public void useCheckBoxes(boolean flag) {
        checkBoxes = flag;
    }

    /**
     * Calling this method permutes the indices of the answers.
     * The internal order of them is not changed by this
     * method, a permutation-lookup-table is created instead to
     * be used later when accessing the answers. The used
     * "algorithm" can be looked up at
     * http://java.sun.com/docs/books/tutorial/collections/interfaces/list.html#shuffle
     */
    public void randomize() {
        // randomize the order of the possible answers
        int i;

        // randomize the order of the possible answers
        int j;

        Integer swap;
        Random randomizer = new Random();

        // permute the order of the indices
        for (i = (permutation.size() - 1); i > 0; i--) {
            // create a new random index
            j = randomizer.nextInt(i);
            // save element at the j'th position
            swap = (Integer) permutation.elementAt(j);
            // copy the i'th element at the j'th position
            permutation.setElementAt((Integer) permutation.elementAt(i), j);
            // insert the saved former j'th element into position i 
            permutation.setElementAt(swap, i);
        }
    }

    /**
     * Use this method to construct the GUI for presenting the
     * question and that will be shown in the main window used
     * for interaction. It MUST be called before showing the
     * question.
     */
    public void makeGUI() {
        answerBoxes = new ButtonGroup();
        JScrollPane questionScroller;
        Icon mcIcon;
        JLabel headlineLabel;
        JPanel bottomPanel;
        JPanel buttonPanel;
        JPanel answerPanel;
        JTextArea answerArea;
        JScrollPane answerScroller;
        JToggleButton choice;
        JToggleButton nullbox;

        int i;

        answers = new Vector(permutation.size());

        // sets the layout manager of the object
        setLayout(new BorderLayout());

        URL imageURL = Util.getResourceURL(avInteractionProperties
                .getString("directory.images")
                + avInteractionProperties.getString("image.mc_question"), this.getClass());

        // constructs the headline of the question window
        mcIcon = new ImageIcon(imageURL);
        
        headlineLabel = new JLabel(mcIcon, SwingConstants.CENTER);

        // wraps the question, the answer input field and 
        // the submit button
        mainPanel = new JPanel(new GridLayout((possibleAnswers.size()) + 3, 1));
        //mainPanel.setBackground(new Color(192,192,192));

        // the question, made scrollable using a JScrollPane
        questionOutput = new JTextArea(questionText);
        questionOutput.setEditable(false);
        questionOutput.setWrapStyleWord(true);
        questionOutput.setLineWrap(true);
        questionScroller = new JScrollPane(questionOutput,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        mainPanel.add(questionScroller);

        // construct choices
        for (i = 0; i < possibleAnswers.size(); i++) {
            if (checkBoxes) {
                choice = new JCheckBox("#" + (new Integer(i + 1)).toString(),
                        false);

            } else {
                choice = new JRadioButton(
                        "#" + (new Integer(i + 1)).toString(), false);
            }

            // create radio box
            answers.addElement(choice);
            if (!checkBoxes)
                answerBoxes.add(choice);

            // create answer text
            answerArea = new JTextArea(
                    ((MCAnswerObject) possibleAnswers
                            .elementAt(((Integer) permutation.elementAt(i))
                                    .intValue())).answer.trim());
            answerArea.setWrapStyleWord(true);
            answerArea.setLineWrap(true);
            answerArea.setEditable(false);
            answerScroller = new JScrollPane(answerArea,
                    JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                    JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

            // create answer panel
            answerPanel = new JPanel(new BorderLayout());
            answerPanel.add(BorderLayout.WEST, choice);
            answerPanel.add(BorderLayout.CENTER, answerScroller);

            // add answer to main panel
            mainPanel.add(answerPanel);
        }

        // panel that takes care of the submit button
        buttonPanel = new JPanel();
        submitButton = new JButton("Submit Response");
        submitButton.addActionListener(this);

        buttonPanel.add(submitButton);

        // feedback of the system about the current state
        // 	   of answering the question
        setFeedback("No answer submitted yet");
        setFeedbackRed();

        // lower part of the main panel, consists of 
        //     buttons and information labels
        bottomPanel = new JPanel(new GridLayout(2, 1));
        bottomPanel.add(buttonPanel);

        // put together what belongs together
        mainPanel.add(buttonPanel);
        mainPanel.add(feedbackScroller);
        add(BorderLayout.NORTH, headlineLabel);
        add(BorderLayout.CENTER, mainPanel);

        if (DEBUG)
            System.out.println("\t\tPanel created");

        guiBuilded = true;
    }

    /**
     * Sets the text of the question to the given string.
     *
     * @param question The text of the question.
     */
    public void setQuestion(String question) {
        questionText = question;
    }

    /**
     * Adds a possible answer to the number of the answers to
     * choose from.
     *
     * @param answer The answer to add.
     * @param subpoints The pointparts available for selecting
     * 		  this possible answer.  If answer is declared as
     * 		  wrong, this amount will be substracted from the
     * 		  number of gained points.
     * @param comment The comment belonging to this possible
     * 		  answer.
     */
    public void addPossibleAnswer(String answer, int subpoints, String comment) {
        MCAnswerObject anAnswer = new MCAnswerObject(answer, subpoints, comment);
        permutation.add(new Integer(possibleAnswers.size()));
        possibleAnswers.add(anAnswer);
        return;
    }

    /**
     * Retrieve the comment.
     *
     * @return A String containg the comment to display.
     */
    public String getComment() {
        return theComment;
    }

    /**
     * Retrieve the windows title.
     *
     * @return A String with the titlebars text.
     */
    public String getTitle() {
        return TITLE;
    }

    /**
     * Resets the interaction object so it can be called again
     * in it's original state.
     */
    public void rebuildQuestion() {
        int i;
        for (i = 0; i < answers.size(); i++) {
            ((JToggleButton) answers.elementAt(i)).setSelected(false);
        }

        setFeedback("");
        submitButton.setEnabled(true);
    }

    /**
     * Evaluate the answer, disable the submit button.
     *
     * @param event The event that occured.
     */
    public void actionPerformed(ActionEvent event) {

        submitButton.setEnabled(false);
        if (submitted) {
            submitButton.setEnabled(false);
            this.parentFrame.dispose();
            return;
        }

        //int i;
        //int correctAnswerPos;
        int correctCount = 0;
        theComment = "";
        int tmpPoints = 0;
        int selectedCount = 0;

        correct = true;

        if (answerBoxes != null) {
            for (int i = 0; i < possibleAnswers.size(); i++) {
                // answer was selected?
                if (((JToggleButton) answers.elementAt(i)).isSelected()) {
                    // If it is a right one, add the points
                    // and the comment.
                    if (correctAnswer.contains(new Integer(
                            ((Integer) permutation.get(i)).intValue() + 1))) {
                        correctCount++;
                        tmpPoints += ((MCAnswerObject) possibleAnswers
                                .get(((Integer) permutation.get(i)).intValue())).pointparts;
                        theComment += (((MCAnswerObject) possibleAnswers
                                .get(((Integer) permutation.get(i)).intValue())).comment + "<br>\n");
                        selectedCount++;
                    }
                    // otherwise substract the points
                    else {
                        correct = false;
                        selectedCount++;
                        tmpPoints -= ((MCAnswerObject) possibleAnswers
                                .get(((Integer) permutation.get(i)).intValue())).pointparts;
                        theComment += (((MCAnswerObject) possibleAnswers
                                .get(((Integer) permutation.get(i)).intValue())).comment + "<br>\n");
                    }
                } else {
                    // a correct answer was not selected? Then the question
                    // was not answered correct. 
                    if (correctAnswer.contains(new Integer(
                            ((Integer) permutation.get(i)).intValue() + 1))) {
                        correct = false;
                    }
                }
            }
        }

        // not all correct answers where selected -> wrong
        if (correctCount != correctAnswer.size())
            correct = false;

        // not enough answers selected -> how many were found?
        if (!correct && (selectedCount < correctAnswer.size()))
            theComment += ("You found " + correctCount + " out of "
                    + correctAnswer.size() + " right answers.");
        // enough answers selected, but wrong ones too
        else if (!correct && (selectedCount == correctAnswer.size()))
            theComment += ("You found " + correctCount + " out of "
                    + correctAnswer.size() + " right answers but also selected wrong ones.");
        //too many answers selected, therefore also wrong ones
        else if (!correct && (selectedCount > correctAnswer.size()))
            theComment += "You selected wrong answers.";

        // final points less than zero? Make them zero
        if (tmpPoints < 0)
            tmpPoints = 0;

        // If points for the whole question where defined and their value
        // is higher than then sum of points given for a correct answer,
        // then we should give them instead of the sum.
        if (correct && (points > 0) && (points > tmpPoints))
            achievedPoints = points;
        else
            achievedPoints = tmpPoints;

        submitted = true;
        submitButton.setText("Close");
        submitButton.setEnabled(true);
        fireSubmit(event);

        return;
    }

}
