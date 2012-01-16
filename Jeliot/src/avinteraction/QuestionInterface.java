package avinteraction;

import java.awt.event.ActionListener;

import javax.swing.JDialog;


/**
 * Interface for question related classes.
 *
 * @author Gina Haeussge, huge(at)rbg.informatik.tu-darmstadt.de
 */
public interface QuestionInterface
	extends ActionListener
{

	//~ Methods -------------------------------------------------

	/**
	 * Set the text of the feedback label.
	 */
	void setFeedback(String text);

	/**
	 * Create the GUI of the question.
	 */
	void makeGUI();

	/**
	 * Set the question's text.
	 */
	void setQuestion(String question);

	/**
	 * True if question was submitted, false otherwise
	 *
	 * @return Whether the question was submitted (true) or not
	 * 		   (false).
	 */
	boolean wasSubmitted();

	/**
	 * Use this for reenabling the submit button.
	 */
	void enableSubmit();

	/**
	 * Use this to set the points available by answering this
	 * question.
	 */
	void setPoints(int pts);

	/**
	 * Returns the points one gets for answering the question
	 * right.
	 *
	 * @return Points of the question.
	 */
	int getPoints();

	/**
	 * Returns the points achieved by answering the  question.
	 *
	 * @return Achieved points of the question.
	 */
	int getAchievedPoints();

	/**
	 * Gets the comment belonging to the submitted answer
	 *
	 * @return
	 */
	String getComment();

	/**
	 * Returns a string containing the title for the question
	 * window.
	 *
	 * @return The question-window's title.
	 */
	String getTitle();

	/**
	 * Reset the question
	 */
	void rebuildQuestion();

	/**
	 * Add a listener able to react on the submitbutton
	 */
	public void addSubmitListener(InteractionModule listener);

	/**
	 * Remove a submitbutton listener
	 */
	public void removeSubmitListener(InteractionModule listener);

	/**
	 * Save the frame object the interaction is displayed in
	 */
	public void setJDialog(JDialog frame);

	/**
	 * Return the frame object the interaction is displayed in
	 *
	 * @return The frame in which the interactio is placed in.
	 */
	public JDialog getJDialog();
}
