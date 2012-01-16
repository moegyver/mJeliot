package avinteraction;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.WindowListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StreamTokenizer;
import java.util.Hashtable;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.WindowConstants;

import avinteraction.backend.BackendInterface;
import avinteraction.backend.GenericBackend;
import avinteraction.parser.AnimalscriptParser;
import avinteraction.parser.BadSyntaxException;
import avinteraction.parser.LanguageParserInterface;
import avinteraction.parser.Parser;

/**
 * Main interaction module, interfaces to the parser and the
 * backend and wraps the whole interaction procedure.
 *
 * @author Gina Haeussge, huge(at)rbg.informatik.tu-darmstadt.de
 */
public class InteractionModule implements InteractionInterface {

    //~ Instance fields -----------------------------------------

    /** Switch for debug-output */
    private boolean DEBUG;

    /** Stores all the Interaction-Objects */
    private Hashtable allInteractions = new Hashtable();

    /** Stores the group infos */
    private Hashtable groupInfos = new Hashtable();

    /** Stores the parser information */
    private Hashtable availableParsers;

    /**
     * The one and only window used to display the interaction
     * objects.
     */
    private JFrame parent;

    /** The backend to use. */
    private BackendInterface backend;

    /** The window listeners listening to window events */
    private Vector windowListeners;

    //~ Constructors --------------------------------------------

    /**
     * Standard constructor, creates a generic backend module to
     * use and switches debugging mode off
     *
     * @throws BadSyntaxException Thrown if bad syntax is found in the definition file.
     * @throws IOException Thrown if loading of the definition file fails.
     */
    public InteractionModule() throws BadSyntaxException, IOException {
        this(new GenericBackend(), null, false);
    }

    /**
     * Useage of this constructor also allows a backend object
     * to use for evluating the answers. Switches debugging
     * mode off.
     *
     * @param backendObj The backend object to use
     *
     * @throws BadSyntaxException Thrown if bad syntax is found in the definition file.
     * @throws IOException Thrown if loading of the definition file fails.
     */
    public InteractionModule(BackendInterface backendObj)
            throws BadSyntaxException, IOException {
        this(backendObj, null, false);
    }

    /**
     * Standard constructor, creates a generic backend module to
     * use and switches debugging mode to boolean value of
     * parameter.
     *
     * @param tf Show debugmessages (true) or not (false)
     *
     * @throws BadSyntaxException Thrown if bad syntax is found in the definition file.
     * @throws IOException Thrown if loading of the definition file fails.
     */
    public InteractionModule(boolean tf) throws BadSyntaxException, IOException {
        this(new GenericBackend(), null, tf);
    }

    /**
     * Useage of this constructor also allows a backend object
     * to use for evluating the answers. Switches debugging
     * mode off.
     *
     * @param backendObj The backend object to use
     * @param tf Show debugmessages (true) or not (false)
     *
     * @throws BadSyntaxException Thrown if bad syntax is found in the definition file.
     * @throws IOException Thrown if loading of the definition file fails.
     */
    public InteractionModule(BackendInterface backendObj, JFrame parent,
            boolean tf) throws BadSyntaxException, IOException {
        super();
        DEBUG = tf;
        if (DEBUG)
            System.out.println("\nInteractionModule constructed");

        this.parent = parent;
        setBackend(backendObj);
        //createParserList();
        windowListeners = new Vector();
    }

    //~ Methods -------------------------------------------------

    /**
     * load the interaction definitionfile and give control to
     * the given parser object
     *
     * @param definitionFile the filename of the interaction
     * 		  definition
     * @param parser the parser that should be used to parse the
     * 		  interaction definition
     */
    public void interactionDefinition(String definitionFile,
            LanguageParserInterface parser) {
        if (DEBUG)
            System.out
                    .println("\ninteractionDefinition was called\n\tdefinitionFile=\""
                            + definitionFile
                            + "\"\n\tparser=\""
                            + parser.toString() + "\"");

        allInteractions = parser.parse(definitionFile);
        groupInfos = parser.getGroupInfo();
        return;
    }

    /**
     * load the interaction definitionfile and give control to
     * the given parser object
     *
     * @param definitionFile the filename of the interaction
     * 		  definition
     * @param parserType the type of parser that should be used,
     * 		  refer to parser.config for available types.
     *
     * @throws UnknownParserException Thrown if parserType is an unknown parser or something went wrong loading it.
     */
    public void interactionDefinition(String definitionFile, String parserType)
            throws UnknownParserException {
        String className = "";

        className = (String) (availableParsers.get(parserType));
        if (className.equals(""))
            throw (new UnknownParserException("Unknown Parser " + parserType));

        LanguageParserInterface parser;

        try {
            Class c = Class.forName(className);
            parser = (LanguageParserInterface) (c.newInstance());
        } catch (ClassNotFoundException e) {
            throw (new UnknownParserException("Parser " + parserType
                    + " not loaded : " + e));
        } catch (IllegalAccessException e) {
            throw (new UnknownParserException("Parser " + parserType
                    + " not loaded : " + e));
        } catch (InstantiationException e) {
            throw (new UnknownParserException("Parser " + parserType
                    + " not loaded : " + e));
        }

        parser.setDebug(DEBUG);

        if (DEBUG)
            System.out
                    .println("\ninteractionDefinition was called\n\tdefinitionFile=\""
                            + definitionFile
                            + "\"\n\tparser=\""
                            + parser.toString() + "\"");

        allInteractions = parser.parse(definitionFile);
        groupInfos = parser.getGroupInfo();
        return;
    }

    /**
     * Load the interaction definitionfile and give control to
     * an  AnimalScript parser object.
     *
     * @param definitionFile The filename of the interaction
     * 		  definition
     */
    public void interactionDefinition(String definitionFile) {
        AnimalscriptParser parser = new AnimalscriptParser(DEBUG);
        interactionDefinition(definitionFile, parser);
    }

    /**
     * Calling this method will execute the interaction with the
     * given id.
     *
     * @param interactionID The id that specifies the
     * 		  interaction
     *
     * @throws UnknownInteractionException Thrown if interactionID is an unknown id.
     */
    public void interaction(String interactionID)
            throws UnknownInteractionException {
        boolean displayAnswer;
        boolean guiBuilded;
        String groupID;
        GroupInfo group;
        Dimension theSize;
        int i;

        if (DEBUG)
            System.out.println("\ninteraction was called\n\tinteractionID=\""
                    + interactionID + "\"");

        if (!allInteractions.containsKey(interactionID))
            throw (new UnknownInteractionException("Interaction \""
                    + interactionID + "\" does not exist"));

        if (allInteractions.get(interactionID) instanceof Question) {
            Question aQuestion = (Question) allInteractions.get(interactionID);

            aQuestion.addSubmitListener(this);

            // get group id
            groupID = aQuestion.getGroupID();
            // lookup group infos
            group = (GroupInfo) groupInfos.get(groupID);
            if ((groupID != "") && (group != null)) {
                // if amount of correct questions of group
                // already reached we dont have to
                // continue with this interaction
                if ((group.processed >= group.repeats)
                        && (group.processed != 0) && (group.repeats != 0)) {
                    if (DEBUG)
                        System.out
                                .println("\t\tNecessary amount of questions of group "
                                        + "answered correct.");

                    return;
                }
            }

            guiBuilded = aQuestion.getGuiBuilded();
            if (guiBuilded)
                aQuestion.rebuildQuestion();
            else
                aQuestion.makeGUI();

            aQuestion.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

            JDialog jdialog = getJDialog(aQuestion.getTitle());
            //theFrame = new JFrame(aQuestion.getTitle());

            aQuestion.setJDialog(jdialog);

            for (i = 0; i < windowListeners.size(); i++)
                jdialog.addWindowListener((WindowListener) (windowListeners
                        .elementAt(i)));

            jdialog
                    .setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
            jdialog.setBackground(new Color(192,192,192));
            jdialog.getContentPane().add(aQuestion, BorderLayout.CENTER);
            jdialog.pack();
            theSize = jdialog.getSize();
            jdialog.setSize(350, theSize.height);
            //jdialog.pack();
            jdialog.setLocationRelativeTo(null);
            jdialog.setVisible(true);
            jdialog.requestFocus();
            //theFrame.show();

            // Processing of the interaction will be handled by
            // the event loop.
        } else if (allInteractions.get(interactionID) instanceof Documentation) {
            Documentation doc = (Documentation) allInteractions
                    .get(interactionID);
            doc.makeGUI();
            doc.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

            JFrame theFrame = new JFrame("Documentation");

            theFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

            theFrame.getContentPane().add(doc, BorderLayout.CENTER);

            for (i = 0; i < windowListeners.size(); i++)
                theFrame.addWindowListener((WindowListener) (windowListeners
                        .elementAt(i)));

            theFrame.setSize(400, 500);
            //theFrame.pack();
            theFrame.setLocationRelativeTo(null);
            theFrame.setVisible(true);
            theFrame.requestFocus();
            //theFrame.show();

            // no need to handle anything by the event loop.
        }
        return;
    }

    /**
     * Gets called if the Submitbutton of the given question was
     * pressed and therefore a processing method has to be
     * triggered.
     *
     * @param aQuestion The question which answer was submitted.
     */
    public void submitPressed(Question aQuestion) {
        String groupID;
        GroupInfo group;
        boolean displayAnswer;
        String interactionID;

        interactionID = aQuestion.objectID;

        // get group id
        groupID = aQuestion.getGroupID();
        // lookup group infos
        group = (GroupInfo) groupInfos.get(groupID);
        if ((groupID != "") && (group != null)) {
            // if amount of correct questions of group
            // already reached we dont have to
            // continue with this interaction
            if ((group.processed >= group.repeats) && (group.processed != 0)
                    && (group.repeats != 0)) {
                if (DEBUG)
                    System.out
                            .println("\t\tNecessary amount of questions of group "
                                    + "answered correct.");

                return;
            }
        }

        aQuestion.getJDialog().setDefaultCloseOperation(
                WindowConstants.DISPOSE_ON_CLOSE);

        displayAnswer = backend.submitAnswer(interactionID, aQuestion
                .isCorrect(), aQuestion.getPoints(), aQuestion
                .getAchievedPoints(), aQuestion.getConceptIdentifiers());

        // If question is part of an existing group
        // and question was answered correct, then
        // increment the number of correctly answered
        // questions in this group
        if ((!groupID.equals("")) && (group != null) && (aQuestion.isCorrect()))
            group.processed++;

        aQuestion.setFeedbackBlack();

        if (displayAnswer) {
            // make the interface display the answer or a comment, if
            // one exists.
            if (!(aQuestion.getComment().equals("")))
                aQuestion.setFeedback(aQuestion.getComment());
            else if (aQuestion.isCorrect())
                aQuestion.setFeedback("Yes, you are right!");
            else
                aQuestion.setFeedback("No, this is wrong!");
        } else {
            // wait till user closes the frame
            aQuestion
                    .setFeedback("Answer submitted. Please close the window now.");
        }
    }

    /**
     * Sets the backend to another object. Useful to  switch it
     * during running animation/interaction.
     *
     * @param backendObj A valid backend object to switch to.
     */
    public void setBackend(BackendInterface backendObj) {
        if (DEBUG)
            System.out.println("\nsetting new Backend to \""
                    + backendObj.toString() + "\"");

        backend = backendObj;

        return;
    }

    /**
     * Use this method to access the JFrame object used for
     * diplaying the interaction modules.
     *
     * @return the JFrame object used to display the
     * 		   interactions
     */
    /*
     public JFrame getFrame() {
     if (theFrame == null) {
     theFrame = new JFrame();
     theFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
     return theFrame;
     } else {
     return theFrame;
     }
     }
     */

    public JDialog getJDialog(String title) {
        //TODO get the owner here.
        return new JDialog(parent, title, true);
    }

    public void actionPerformed(ActionEvent event) {
        // Just a dummy method due to compatibility issues.
    }

    /**
     * Loads the available parser from parser.config into a
     * Hashtable.
     *
     * @throws BadSyntaxException Thrown if the format of
     * @throws IOException
     */
    protected void createParserList() throws BadSyntaxException, IOException {
        StreamTokenizer stok;
        Parser parser;
        String type;
        String parserClass;
        int aChar;

        // open parser.config
        stok = new StreamTokenizer(new BufferedReader(new InputStreamReader(
                InteractionModule.class.getResourceAsStream("parser.config"))));

        System.out.println("Parsing starts!");

        parser = new Parser(stok);
        stok.commentChar('#');

        availableParsers = new Hashtable();

        /*
         System.out.println("" + (int) '\r');
         System.out.println("" + (int) '\n');
         System.out.println("" + (int) '\t');
         System.out.println("" + (int) ' ');
         */

        try {
            // retrieve the parser key-class pairs from parser.config
            // and put them into the Hash
            while (stok.ttype != StreamTokenizer.TT_EOF) {
                type = "";
                parserClass = "";
                parser.getOptionalWhitespace();
                while (parser.getOptionalEOX()) {
                    if (stok.ttype == StreamTokenizer.TT_EOF) {
                        break;
                    }
                    parser.getOptionalWhitespace();
                }
                if (stok.ttype == StreamTokenizer.TT_EOF) {
                    break;
                }
                type = parser.getQuoted();
                /*
                 for (int i = 0; i < type.length(); i++) {
                 System.out.println((int) type.charAt(i));
                 }
                 */
                //System.out.println("type = " + type);
                parser.getOptionalWhitespace();
                aChar = parser.getChar();
                if (aChar != (int) '=')
                    throw (new BadSyntaxException("Expected '=' but found '"
                            + (char) aChar + "' instead"));

                parser.getOptionalWhitespace();
                parserClass = parser.getQuoted();
                parser.getOptionalWhitespace();
                parser.getEOX();

                availableParsers.put(type, parserClass);
                if (DEBUG)
                    System.out.println("Added support for " + type);
            }
        } catch (BadSyntaxException e) {
            throw e;
            //throw (new BadSyntaxException("ERROR with \"parser.config\" : " + e + " - aborting."));
        }
    }

    /**
     * Adds a window listener to the list of listeners that
     * should listen for window events triggered by the
     * interaction frames.
     *
     * @param listener The listener to be added.
     */
    public void addWindowListener(WindowListener listener) {
        windowListeners.add(listener);
    }

    /**
     * Removes a window listener.
     *
     * @param listener The listener to be removed.
     */
    public void removeWindowListener(WindowListener listener) {
        windowListeners.remove(listener);
    }

    public void removeInteractionObject(String interactionID) {
        allInteractions.remove(interactionID);
    }

    public void addInteractionObject(String id, Documentation doc) {
        allInteractions.put(id, doc);
    }

    public void addInteractionObject(String id, Question doc) {
        allInteractions.put(id, doc);
    }
}