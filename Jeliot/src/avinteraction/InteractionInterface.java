package avinteraction;

import java.awt.event.ActionListener;
import java.awt.event.WindowListener;

import avinteraction.backend.BackendInterface;
import avinteraction.parser.LanguageParserInterface;


/**
 * Represents the interface for communicating with the
 * AVInteraction module. Version 0.1, backend components
 * missing
 *
 * @author Gina Haeussge, huge(at)rbg.informatik.tu-darmstadt.de
 */
public interface InteractionInterface extends ActionListener
{

	//~ Methods -------------------------------------------------

	/**
	 * called one time, loads the interaction file and sends it
	 * to the given parser object
	 */
	public void interactionDefinition(String definitionFile,
		LanguageParserInterface parserObj);

	/**
	 * called one time, loads the interaction file and sends it
	 * to a new animalparser object
	 */
	public void interactionDefinition(String definitionFile);

	/**
	 * called one time, loads the interaction file and sends it
	 * to the given parser object defined by the parserType 
	 * given in the string.
	 */
	public void interactionDefinition(String definitionFile,
		String parserType) throws UnknownParserException;

	/**
	 * called each time a interaction should take place,
	 */
	public void interaction(String interactionID) throws UnknownInteractionException;

	/**
	 * used backend object is set to backendObj
	 */
	public void setBackend(BackendInterface backendObj);

	/**
	 * gets the JFrame used for displaying the interactions
	 *
	 * @return the JFrame object used to display the
	 * 		   interactions
	 */
	//public JFrame getFrame();
	
	/**
	 * Adds a window listener. The list of window listeners 
	 * maintained by the InteractionModule is given to
	 * every interaction-frame. This allows extern components
	 * to react to closing and opening of interaction windows.
	 * 
	 * @param listener The listener to be added.
	 */
	public void addWindowListener (WindowListener listener);
	
	/**
	 * Removes a window listener.
	 * 
	 * @param listener The listener to be removed.
	 */
	public void removeWindowListener (WindowListener listener);
}
