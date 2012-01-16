package avinteraction;

import javax.swing.JPanel;

/**
 * Base class for all questionlike interactions. Current count
 * of instances is stored as well.
 *
 * @author Gina Haeussge, huge(at)rbg.informatik.tu-darmstadt.de
 */
public abstract class Documentation extends JPanel implements
        DocumentationInterface {

    //~ Instance fields -----------------------------------------

    /** ID of the documentation interaction */
    protected String interactionID;

    /** The url of the documentation */
    protected String docURL;

    //~ Constructors --------------------------------------------

    /**
     * standard constructor
     */
    public Documentation() {
    }

    //~ Methods -------------------------------------------------

    /**
     * Create the GUI of the documentation.
     */
    public abstract void makeGUI();

    /**
     * Sets the url of the documentation to be displayed
     */
    public abstract void setURL(String url);
}
