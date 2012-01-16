package avinteraction;

/**
 * Exception being thrown when
 * InteractionModule#interactionDefinition(String, String) gets
 * an unknown parsertype-String as second argument or the class
 * the parsertype is refering to cannot be instanciated.
 *
 * @author Gina Haeussge, huge(at)rbg.informatik.tu-darmstadt.de
 */
public class UnknownParserException
	extends Exception
{

	//~ Constructors --------------------------------------------

	/**
	 * Standard Constructor.
	 *
	 * @param e The exceptions text.
	 */
	public UnknownParserException(String e)
	{
		super(e);
	}
}
