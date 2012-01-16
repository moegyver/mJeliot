package avinteraction;

import java.awt.BorderLayout;
import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.swing.ImageIcon;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;

import jeliot.util.DebugUtil;
import jeliot.util.Util;

/**
 * Documentation in HTML format. Encapsulates the data and
 * visualizes the question.
 *
 * @author Gina Haeussge, huge(at)rbg.informatik.tu-darmstadt.de
 */
public class HTMLDocumentation extends Documentation {

    private static ResourceBundle avInteractionProperties = ResourceBundle
            .getBundle("avinteraction.resources.properties", Locale
                    .getDefault());

    //~ Static fields/initializers ------------------------------

    /** The Debug level */
    private static boolean DEBUG;

    //~ Instance fields -----------------------------------------

    /** HTML renderer component */
    private JEditorPane htmlView;

    //~ Constructors --------------------------------------------

    /**
     * Constructor, disabling debugging.
     *
     * @param id The interactions ID
     */
    public HTMLDocumentation(String id) {
        this(id, false);
    }

    /**
     * Constructor, setting debugging to tf.
     *
     * @param id The interactions ID
     * @param tf The debug level.
     */
    public HTMLDocumentation(String id, boolean tf) {
        interactionID = id;
        DEBUG = tf;
    }

    //~ Methods -------------------------------------------------

    /**
     * Sets the url of the documentation
     *
     * @param url The url.
     */
    public void setURL(String url) {
        docURL = url;
    }

    /**
     * Create the GUI of the documentation object
     */
    public void makeGUI() {
        ImageIcon docIcon;
        JLabel headlineLabel;
        JScrollPane documentationScroller;

        try {
            htmlView = new JEditorPane(docURL);
            htmlView.setEditable(false);
        } catch (Exception exception) {
            DebugUtil.handleThrowable(exception);
            DebugUtil.printDebugInfo("IOException in Documentation.makeGUI()");
        }

        // sets the layout manager of the object
        setLayout(new BorderLayout());

        URL imageURL = Util.getResourceURL(avInteractionProperties
                .getString("directory.images")
                + avInteractionProperties.getString("image.html_documentation"), this.getClass());
        
        // constructs the headline of the documentation window
        docIcon = new ImageIcon(imageURL);
        headlineLabel = new JLabel(docIcon, SwingConstants.CENTER);

        documentationScroller = new JScrollPane(htmlView,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        add(BorderLayout.NORTH, headlineLabel);
        add(BorderLayout.CENTER, documentationScroller);

        if (DEBUG)
            System.out.println("\t\tPanel created");

        return;
    }
}
