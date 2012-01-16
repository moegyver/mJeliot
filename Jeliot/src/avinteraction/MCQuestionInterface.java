package avinteraction;

/**
 * DOCUMENT ME!
 *
 * @author Gina Haeussge
 */
public interface MCQuestionInterface
	extends QuestionInterface
{

	//~ Instance fields -----------------------------------------

	/** The interaction windows title */
	public final String TITLE = "Multiple-Choice-Question";

	//~ Methods -------------------------------------------------

	/**
	 * Adds an possible answer to the set of possible answers.
	 */
	void addPossibleAnswer(String answerText, int subpoints,
		String commentText);

	/**
	 * Adds the index to the correct answers. Index counting
	 * from 1
	 */
	void addCorrectAnswer(int answerIndex);

	/**
	 * Determine whethe to use CheckBoxes or RadioButtons for
	 * choosing the answers.
	 */
	void useCheckBoxes(boolean flag);

	/**
	 * Randomize the order of the possible answers
	 */
	void randomize();
}
