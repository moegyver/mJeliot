package avinteraction.parser;

/**
 * BadSyntaxException is an Exception thrown by the Parser to
 * indicate syntax errors in a parsed file.
 *
 * @author Gina Haeussge
 */
public class BadSyntaxException
	extends Exception
{

	//~ Instance fields -----------------------------------------

	/** Text containing a description of the error that occured */
	private String errortext = "";

	//~ Constructors --------------------------------------------

	/**
	 * Constructor
	 *
	 * @param e A String containing a description of the error
	 */
	public BadSyntaxException(String e)
	{
		super(e);
		errortext = e;
	}

	//~ Methods -------------------------------------------------

	/**
	 * Output the errortext
	 *
	 * @return DOCUMENT ME!
	 */
	public String toString()
	{
		return errortext;
	}
}
