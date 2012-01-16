package avinteraction;

/**
 * Interface for Fill-in-blanks-questions.
 *
 * @author Gina Haeussge, huge(at)rbg.informatik.tu-darmstadt.de
 */
public interface FIBQuestionInterface
	extends QuestionInterface
{

	//~ Instance fields -----------------------------------------

	/** The interaction window's title */
	public final String TITLE = "Fill-in-Blanks-Question";

	//~ Methods -------------------------------------------------

	/**
	 * Adds an correct answer to the set of correct answers.
	 *
	 * @param answer The answer to add.
	 */
	public void addAnswer(String answer);

	/**
	 * Sets the comment to display when a wrong answer is given
	 */
	void setComment(String comment);
}
