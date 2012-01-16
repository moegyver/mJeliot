package avinteraction;

/**
 * Interface for documentation related classes.
 *
 * @author Gina Haeussge, huge(at)rbg.informatik.tu-darmstadt.de
 */
interface DocumentationInterface
{

	//~ Methods -------------------------------------------------

	/**
	 * Set the url of the documentation to display
	 */
	public void setURL(String url);

	/**
	 * Create the GUI of the documentation
	 */
	public void makeGUI();
}
