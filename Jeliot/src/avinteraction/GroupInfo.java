package avinteraction;

/**
 * A small helper class to encapsulate question group related
 * information, being the number of questions that should be
 * repeated out of this group and the count of already
 * correctly answered questions.
 *
 * @author Gina Haeussge
 */
public class GroupInfo
	extends Object
{

	//~ Instance fields -----------------------------------------

	/**
	 * How many times questions of this group have to be
	 * answered correctly
	 */
	public int repeats = 0;

	/** How many questions already were answered correctly. */
	public int processed = 0;

	//~ Constructors --------------------------------------------

	/**
	 * Constructs a new Object, assigning repeats and processed
	 *
	 * @param r Number of repeats
	 * @param p Number of already correct processed questions
	 * 		  (normally this would be 0)
	 */
	public GroupInfo(int r, int p)
	{
		repeats = r;
		processed = p;
	}
}
