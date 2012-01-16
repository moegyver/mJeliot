package avinteraction;

/**
 * Interface describing True-False-Question interfaces.
 *
 * @author Gina Haeussge
 */
public interface TFQuestionInterface
	extends QuestionInterface
{

	//~ Instance fields -----------------------------------------

	/** The interaction window's title */
	public final String TITLE = "True-or-False-Question";

	//~ Methods -------------------------------------------------

	/**
	 * Sets whether the correct answer is "right" or "wrong"
	 */
	void setAnswer(boolean isTrue);

	/**
	 * Sets the comment to display when a wrong answer is given
	 */
	void setComment(String comment);
}
