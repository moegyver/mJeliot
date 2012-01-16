package avinteraction;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Vector;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import jeliot.util.Util;

/**
 * Fill-In-Blanks-Question. Encapsulates the data and visualizes
 * the  question.
 *
 * @author Gina Haeussge,
 * 		   huge(at)rbg.informatik.tu-darmstadt.de, building
 * 		   upon work by Laura L. Norton
 */
public class FIBQuestion extends Question implements FIBQuestionInterface {

    private static ResourceBundle avInteractionProperties = ResourceBundle
            .getBundle("avinteraction.resources.properties", Locale
                    .getDefault());

    //~ Static fields/initializers ------------------------------

    /** The debug level */
    private static boolean DEBUG;

    //~ Instance fields -----------------------------------------

    /** A Vector containing all correct answers. */
    protected Vector correctAnswers;

    /** The text field for answer input. */
    protected JTextField answerInput;

    /**
     * The comment that should be displayed if question is
     * answered wrong
     */
    private String theComment;

    //~ Constructors --------------------------------------------

    /**
     * Creates a new instance of FIBQuestion with debugging
     * disabled.
     *
     * @param id The unique id identifying the interaction.
     * @param group The group id of the interaction, if any.
     */
    public FIBQuestion(String id, String group) {
        this(id, group, false, new Integer[0]);
    }

    /**
     * Creates a new instance of FIBQuestion with debugging
     * disabled.
     *
     * @param id The unique id identifying the interaction.
     * @param group The group id of the interaction, if any.
     */
    public FIBQuestion(String id, String group, Integer[] concepts) {
        this(id, group, false, concepts);
    }

    /**
     * 
     * @param id
     * @param group
     * @param tf
     */
    public FIBQuestion(String id, String group, boolean tf) {
        this(id, group, tf, new Integer[0]);
    }
    
    /**
     * Creates a new instance of FIBQuestion with debugging
     * disabled with debugging set to tf.
     *
     * @param id The unique id identifying the interaction.
     * @param group The group id of the interaction, if any.
     * @param tf The debug level.
     */
    public FIBQuestion(String id, String group, boolean tf, Integer[] concepts) {
        super(concepts);
        this.objectID = id;
        this.groupID = group;
        correctAnswers = new Vector();
        questionText = "";
        DEBUG = tf;
    }

    //~ Methods -------------------------------------------------

    /**
     * Adds an answer to the set of correct answers.
     *
     * @param answer The answer to add
     */
    public void addAnswer(String answer) {
        correctAnswers.addElement(answer.trim());
    }

    /**
     * Sets the text of the question to the given String.
     *
     * @param question The text of the question.
     */
    public void setQuestion(String question) {
        questionText = question;
    }

    /**
     * Use this method to construct the GUI for presenting the
     * question and that will be shown in the main window used
     * for interaction. It MUST be called before showing the
     * question.
     */
    public void makeGUI() {
        JScrollPane questionScroller;
        Icon fibIcon;
        JLabel headlineLabel;
        JPanel mainPanel;
        JPanel fieldPanel;
        JPanel buttonPanel;
        JPanel bottomPanel;

        // sets the layout manager of the object
        setLayout(new BorderLayout());

        URL imageURL = Util.getResourceURL(avInteractionProperties
                .getString("directory.images")
                + avInteractionProperties.getString("image.fib_question"), this.getClass());

        // constructs the headline of the question window
        fibIcon = new ImageIcon(imageURL);
        
        headlineLabel = new JLabel(fibIcon, SwingConstants.CENTER);

        // wraps the question, the answer input field and the 
        // submit button
        mainPanel = new JPanel(new GridLayout(4, 1, 5, 5));

        // the question, made scrollable using a JScrollPane
        questionOutput = new JTextArea(questionText);
        questionOutput.setEditable(false);
        questionOutput.setWrapStyleWord(true);
        questionOutput.setLineWrap(true);
        questionScroller = new JScrollPane(questionOutput,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        // the input field for the answer, described by a label
        answerInput = new JTextField();
        fieldPanel = new JPanel(new GridLayout(2, 1));
        fieldPanel.add(new JLabel("Enter your answer here: "));
        fieldPanel.add(answerInput);

        // add them to our main panel
        mainPanel.add(questionScroller);
        mainPanel.add(fieldPanel);

        submitButton = new JButton("Submit Answer");
        submitButton.addActionListener(this);

        // panel that takes care of the submit button
        buttonPanel = new JPanel();
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
        mainPanel.add(bottomPanel);
        mainPanel.add(feedbackScroller);
        add(BorderLayout.NORTH, headlineLabel);
        add(BorderLayout.CENTER, mainPanel);

        if (DEBUG)
            System.out.println("\t\tPanel created");

        guiBuilded = true;
    }

    /**
     * Return a comment-text or an empty string according to the
     * correctness of the questions answer.
     *
     * @return The comment if question answered wrong,  an empty
     * 		   string otherwise.
     */
    public String getComment() {
        if (!correct)
            return theComment;
        else
            return "";
    }

    /**
     * Sets the comment, that should be displayed if  the
     * question is answered wrong.
     *
     * @param c A string that contains the comment-text to use.
     */
    public void setComment(String c) {
        theComment = c;
    }

    /**
     * Retrieve the questions titlebar-text.
     *
     * @return A string to use for the questions titlebar.
     */
    public String getTitle() {
        return TITLE;
    }

    /**
     * Resets the interaction object so it can be called again
     * in it's original state.
     */
    public void rebuildQuestion() {
        answerInput.setText("");
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

        String answerFromBlank = (answerInput.getText()).trim();
        for (int i = 0; i < correctAnswers.size(); i++) {
            if (answerFromBlank.equalsIgnoreCase(((String) correctAnswers
                    .elementAt(i)).trim())) {
                correct = true;
                achievedPoints = points;

                fireSubmit(event);

                submitted = true;
                submitButton.setText("Close");
                submitButton.setEnabled(true);
                submitButton.setVisible(true);
                return;
            }
        }
        achievedPoints = 0;
        correct = false;
        fireSubmit(event);
        submitted = true;
        submitButton.setText("Close");
        submitButton.setEnabled(true);
        submitButton.setVisible(true);
    }
}
