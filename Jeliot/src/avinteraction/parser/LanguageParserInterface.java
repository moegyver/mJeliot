package avinteraction.parser;

import java.util.Hashtable;


/**
 * Interface of parser usable with AVInteraction.
 */
public interface LanguageParserInterface
{

	//~ Methods -------------------------------------------------

	/**
	 * Parse the interactionDefinition and return a Hash
	 * containing the interaction objects.
	 *
	 * @param filename The filename of the definitionFile
	 *
	 * @return A Hash containing the interaction objects.
	 */
	public Hashtable parse(String filename);

	/**
	 * Retrieve the information about questionGroups defined in
	 * the definitionFile.
	 *
	 * @return A Hash containg the groups-information.
	 */
	public Hashtable getGroupInfo();

	/**
	 * Return a string representation of the object, normally a
	 * name for the parser.
	 *
	 * @return A string representation of the parser-object.
	 */
	public String toString();

	/**
	 * Sets the debugging level to tf.
	 *
	 * @param tf The debug level.
	 */
	public abstract void setDebug(boolean tf);
}
