package avinteraction;

/**
 * Exception being thrown when
 * InteractionModule#interaction(String) gets a non existing
 * interactionID as parameter.
 *
 * @author Gina Haeussge, huge(at)rbg.informatik.tu-darmstadt.de
 */
public class UnknownInteractionException
	extends Exception
{

	//~ Constructors --------------------------------------------

	/**
	 * Standard Constructor.
	 *
	 * @param e The exceptions text.
	 */
	public UnknownInteractionException(String e)
	{
		super(e);
	}
}
