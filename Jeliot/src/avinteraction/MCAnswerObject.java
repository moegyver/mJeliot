package avinteraction;

/**
 * Small helper class to encapsulate MCAnswer data.
 *
 * @author Gina Haeussge
 */
public class MCAnswerObject
	extends Object
{

	//~ Instance fields -----------------------------------------

	/** The answers text */
	public String answer;

	/** The text of a comment related to the answer */
	public String comment;

	/** The point parts available for this answer */
	public int pointparts;

	//~ Constructors --------------------------------------------

	/**
	 * Constructs an object assigning the given parameters to
	 * answer and comment.
	 *
	 * @param a The text the answer should have.
	 * @param p The points available for this answer.
	 * @param c The comment's text.
	 */
	public MCAnswerObject(String a, int p, String c)
	{
		answer = a;
		comment = c;
		pointparts = p;
	}
}
