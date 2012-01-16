package avinteraction.parser;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StreamTokenizer;
import java.net.URL;
import java.util.Hashtable;

import avinteraction.FIBQuestion;
import avinteraction.GroupInfo;
import avinteraction.HTMLDocumentation;
import avinteraction.MCQuestion;
import avinteraction.TFQuestion;

/**
 * Parses interaction definition files written in animal script.
 *
 * @author Gina Haeussge
 */
public class AnimalscriptParser implements LanguageParserInterface {

    //~ Instance fields -----------------------------------------

    /** Name of the language the Parser parses. */
    private final String LANGUAGE = "AnimalScript";

    /**
     * Hash holding all interaction objects, identifying them by
     * their ID
     */
    private Hashtable interactionObjects = new Hashtable();

    /**
     * Hash holding all group object, identifying them by their
     * ID
     */
    private Hashtable groupInfo = new Hashtable();

    /** An instance of the Parser class, used for easy parsing */
    private Parser parser;

    /** The StreamTokenizer used for parsing the file */
    private StreamTokenizer stok;

    /** An integer counting the number of lines in the file */
    private int curLine = 1;

    /** The Debug level */
    private boolean DEBUG;

    //~ Constructors --------------------------------------------

    /**
     * Standard constructor, disables debugging.
     */
    public AnimalscriptParser() {
        this(false);
    }

    /**
     * Constructor, sets debugging to tf.
     *
     * @param tf The debug level.
     */
    public AnimalscriptParser(boolean tf) {
        DEBUG = tf;
    }

    //~ Methods -------------------------------------------------

    /**
     * Sets the debug level to tf.
     *
     * @param tf The debug level.
     */
    public void setDebug(boolean tf) {
        DEBUG = tf;
    }

    /**
     * The main parser routine.
     *
     * @param filename The filename of the file that should be
     * 		  parsed
     *
     * @return A hash containing all found objects.
     */
    public Hashtable parse(String filename) {
        URL theURL;

        if (DEBUG)
            System.out.println("\t\tparsing " + filename);

        String word;

        BufferedReader theReader;

        try {
            // if we have a valid URL
            if (filename.startsWith("http://") || filename.startsWith("ftp://")
                    || filename.startsWith("file://")) {
                theURL = new URL(filename);
                theReader = new BufferedReader(new InputStreamReader(theURL
                        .openStream()));
                // else we got a local file 
            } else {
                theReader = new BufferedReader(new FileReader(filename));
            }

            // create our Tokenizer
            stok = new StreamTokenizer(theReader);

            // create an instance of the parser
            parser = new Parser(stok);
            parser.setIgnoreCase(true);

            // parse the whole file
            while (stok.ttype != StreamTokenizer.TT_EOF) {
                while (getOptionalEOL())
                    ;

                //stok.pushBack();
                word = parser.getWord();

                if (word.equalsIgnoreCase("documentation")) {
                    parseDocumentation();
                } else if (word.equalsIgnoreCase("fibQuestion")) {
                    parseFIBQuestion();
                } else if (word.equalsIgnoreCase("mcQuestion")) {
                    parseMCQuestion();
                } else if (word.equalsIgnoreCase("tfQuestion")) {
                    parseTFQuestion();
                } else {
                    throw (new BadSyntaxException(
                            "Expected a valid interaction keyword,"
                                    + " but found \"" + word + "\" instead."));
                }
            }
        } catch (BadSyntaxException e) {
            throw new RuntimeException(e);
            //System.out.println("Syntax Error in line " + curLine + ": " + e + " - aborting parsing.");
        } catch (IOException e) {
            throw new RuntimeException(e);
            //System.out.println("Exception occured in AnimalscriptParser: " + e);
        }
        return interactionObjects;
    }

    /**
     * Returns a Hash containing the information about question
     * groups
     *
     * @return A Hash with the group information
     */
    public Hashtable getGroupInfo() {
        return groupInfo;
    }

    /**
     * returns the name of the language
     *
     * @return a String containing the languages name
     */
    public String toString() {
        return LANGUAGE;
    }

    /**
     * Parses the documentation keyword.
     *
     * @throws IOException
     * @throws BadSyntaxException
     */
    private void parseDocumentation() throws IOException, BadSyntaxException {
        HTMLDocumentation doc;
        String id;
        String url;

        parser.getWhitespace();
        id = parser.getQuoted();
        getEOL();
        url = parser.getQuoted();
        getEOL();
        parser.getKeyword("endtext");
        getEOX();

        doc = new HTMLDocumentation(id, DEBUG);
        doc.setURL(url);

        interactionObjects.put(id, doc);
    }

    /**
     * Parses the tfQuestion keyword.
     *
     * @throws IOException
     * @throws BadSyntaxException
     */
    private void parseTFQuestion() throws IOException, BadSyntaxException {
        TFQuestion question;
        String id;
        String groupID;
        String tmp;
        String questionText = "";
        String tf;
        String comment;
        int points;
        int repeat;
        boolean correct;

        // get the interactionID
        parser.getWhitespace();
        id = parser.getQuoted();
        getEOL();

        // initialize optional values
        groupID = "";
        points = 0;
        repeat = 0;

        // parse optional components
        tmp = parser.getOptionalWord();
        while (!tmp.equals("")) {
            if (tmp.equalsIgnoreCase("questiongroup")) {
                parser.getWhitespace();
                groupID = parser.getQuoted();
                if (groupInfo.get(groupID) == null)
                    groupInfo.put(groupID, new GroupInfo(repeat, 0));
            } else if (tmp.equalsIgnoreCase("nrRepeats")) {
                parser.getWhitespace();
                repeat = parser.getNumber();
                if ((groupInfo.get(groupID) != null)
                        && (((GroupInfo) groupInfo.get(groupID)).repeats == 0))
                    groupInfo.put(groupID, new GroupInfo(repeat, 0));
            } else if (tmp.equalsIgnoreCase("points")) {
                parser.getWhitespace();
                points = parser.getNumber();
            } else
                throw (new BadSyntaxException(
                        "Expected an optional component but found \"" + tmp
                                + "\" instead."));

            getEOL();
            tmp = parser.getOptionalWord();
        }

        // Get all question text there is.
        tmp = parser.getOptionalQuoted();

        while (!tmp.equals("")) {
            questionText += tmp;
            getEOL();
            tmp = parser.getOptionalQuoted();
        }

        parser.getKeyword("endtext");
        getEOL();

        // parse the answer part
        parser.getKeyword("answer");

        if (!getOptionalEOL()) {
            ;
        }

        tf = parser.getWord();

        if (tf.equals("t")) {
            correct = true;
        } else if (tf.equals("f")) {
            correct = false;
        } else {
            throw (new BadSyntaxException("'t' or 'f' expected but found '"
                    + tf + "' instead."));
        }

        getEOL();
        parser.getKeyword("endanswer");

        getEOX();

        comment = "";
        if (parser.getOptionalKeyword("comment")) {
            getEOL();
            while (!parser.getOptionalKeyword("endcomment")) {
                comment += parser.getQuoted();
                getEOL();
            }

            getEOX();
        }

        // create a TFQuestion object
        question = new TFQuestion(id, groupID, DEBUG);
        question.setQuestion(questionText);
        question.setAnswer(correct);
        question.setPoints(points);
        question.setComment(comment);

        // add it to the interactions
        interactionObjects.put(id, question);
    }

    /**
     * Parses the mcQuestion keyword.
     *
     * @throws IOException
     * @throws BadSyntaxException
     */
    private void parseMCQuestion() throws IOException, BadSyntaxException {
        MCQuestion question;
        String id;
        String groupID;
        String tmp;
        String questionText = "";
        String answer;
        String commentText = "";
        int correctAnswer;
        int points;
        int repeat;
        int subpoints;

        // get the interactionID
        parser.getWhitespace();
        id = parser.getQuoted();
        getEOL();

        // initialize optional values
        groupID = "";
        points = 0;
        repeat = 0;

        // parse optional components
        tmp = parser.getOptionalWord();
        while (!tmp.equals("")) {
            if (tmp.equalsIgnoreCase("questiongroup")) {
                parser.getWhitespace();
                groupID = parser.getQuoted();
                if (groupInfo.get(groupID) == null)
                    groupInfo.put(groupID, new GroupInfo(repeat, 0));
            } else if (tmp.equalsIgnoreCase("nrRepeats")) {
                parser.getWhitespace();
                repeat = parser.getNumber();
                if ((groupInfo.get(groupID) != null)
                        && (((GroupInfo) groupInfo.get(groupID)).repeats == 0))
                    groupInfo.put(groupID, new GroupInfo(repeat, 0));
            } else if (tmp.equalsIgnoreCase("points")) {
                parser.getWhitespace();
                points = parser.getNumber();
            } else
                throw (new BadSyntaxException(
                        "Expected an optional component but found \"" + tmp
                                + "\" instead."));

            getEOL();
            tmp = parser.getOptionalWord();
        }

        // Get all questionText there is
        tmp = parser.getOptionalQuoted();

        while (!tmp.equals("")) {
            questionText += tmp;
            getEOL();
            tmp = parser.getOptionalQuoted();
        }

        parser.getKeyword("endtext");
        getEOL();

        question = new MCQuestion(id, groupID, DEBUG);
        question.setQuestion(questionText);

        // get the possible answers
        while (!parser.getOptionalKeyword("answer")) {
            answer = parser.getQuoted();
            getEOL();
            parser.getKeyword("endchoice");
            getEOL();
            commentText = "";
            subpoints = 0;
            if (parser.getOptionalKeyword("points")) {
                parser.getWhitespace();
                subpoints = parser.getNumber();
                getEOL();
            }

            if (parser.getOptionalKeyword("comment")) {
                getEOL();
                while (!parser.getOptionalKeyword("endcomment")) {
                    commentText += parser.getQuoted();
                    getEOL();
                }

                getEOL();
            }

            question.addPossibleAnswer(answer, subpoints, commentText);
        }

        getEOL();

        // get the numbers of the correct answers
        while (!parser.getOptionalKeyword("endanswer")) {
            correctAnswer = parser.getNumber();
            getEOL();
            question.addCorrectAnswer(correctAnswer);
        }

        getEOX();

        // finish the new MCQuestion object
        question.randomize();
        question.setPoints(points);
        question.useCheckBoxes(true);

        // add it to the interactions
        interactionObjects.put(id, question);
    }

    /**
     * Parses the fibQuestion keyword.
     *
     * @throws IOException
     * @throws BadSyntaxException
     */
    private void parseFIBQuestion() throws IOException, BadSyntaxException {
        FIBQuestion question;
        String id;
        String groupID;
        String tmp;
        String answer;
        String comment;
        String questionText = "";
        int points = 0;
        int repeat = 0;

        // get the interactionID
        parser.getWhitespace();
        id = parser.getQuoted();
        getEOL();
        getOptionalEOL();
        
        // initialize optional values
        groupID = "";
        points = 0;
        repeat = 0;

        // parse optional components
        tmp = parser.getOptionalWord();
        while (!tmp.equals("")) {
            if (tmp.equalsIgnoreCase("questiongroup")) {
                parser.getWhitespace();
                groupID = parser.getQuoted();
                if (groupInfo.get(groupID) == null)
                    groupInfo.put(groupID, new GroupInfo(repeat, 0));
            } else if (tmp.equalsIgnoreCase("nrRepeats")) {
                parser.getWhitespace();
                repeat = parser.getNumber();
                if ((groupInfo.get(groupID) != null)
                        && (((GroupInfo) groupInfo.get(groupID)).repeats == 0))
                    groupInfo.put(groupID, new GroupInfo(repeat, 0));
            } else if (tmp.equalsIgnoreCase("points")) {
                parser.getWhitespace();
                points = parser.getNumber();
            } else
                throw (new BadSyntaxException(
                        "Expected an optional component but found \"" + tmp
                                + "\" instead."));

            getEOL();
            getOptionalEOL();
            tmp = parser.getOptionalWord();
        }

        // get all question text there is
        tmp = parser.getOptionalQuoted();

        while (!tmp.equals("")) {
            questionText += tmp;
            getEOL();
            getOptionalEOL();
            tmp = parser.getOptionalQuoted();
        }
        getOptionalEOL();
        getOptionalEOL();
        parser.getKeyword("endtext");
        getEOL();
        getOptionalEOL();

        question = new FIBQuestion(id, groupID, DEBUG);
        question.setQuestion(questionText);

        // get the different correct answers
        parser.getKeyword("answer");
        getEOL();
        getOptionalEOL();

        while (!parser.getOptionalKeyword("endanswer")) {
            answer = parser.getQuoted();
            getEOL();
            getOptionalEOL();
            question.addAnswer(answer);
        }

        getEOX();
        getOptionalEOL();

        comment = "";
        if (parser.getOptionalKeyword("comment")) {
            getEOL();
            getOptionalEOL();
            while (!parser.getOptionalKeyword("endcomment")) {
                comment += parser.getQuoted();
                getEOL();
                getOptionalEOL();
            }
            getEOX();
            getOptionalEOL();
        }

        question.setPoints(points);
        question.setComment(comment);

        // add the new FIBQuestion object to the interactions
        interactionObjects.put(id, question);
    }

    /**
     * Does an EOL lookup using the parser instance and calls
     * the lines of the parsed file by doing so.
     *
     * @return A boolean value indicating whether there was an
     * 		   eol or not.
     *
     * @throws IOException
     * @throws BadSyntaxException
     */
    private boolean getEOL() throws IOException, BadSyntaxException {
        parser.getOptionalWhitespace();
        if (parser.getEOL()) {
            parser.getOptionalEOL();
            curLine++;
            parser.getOptionalWhitespace();

            return true;
        } else {
            return false;
        }
    }

    /**
     * Does an EOL lookup using the parser instance and calls
     * the lines of the parsed file by doing so. The eol is
     * optional.
     *
     * @return A boolean value indicating whether there was an
     * 		   eol or not.
     *
     * @throws IOException
     */
    private boolean getOptionalEOL() throws IOException {
        parser.getOptionalWhitespace();
        if (parser.getOptionalEOL()) {
            parser.getOptionalEOL();
            curLine++;
            parser.getOptionalWhitespace();

            return true;
        } else {
            return false;
        }
    }

    /**
     * Does an EOL/EOF lookup using the parser instance and
     * calls the lines of the parsed file by doing so.
     *
     * @return A boolean value indicating whether there was an
     * 		   eol or an eof or not.
     *
     * @throws IOException
     * @throws BadSyntaxException
     */
    private boolean getEOX() throws IOException, BadSyntaxException {
        parser.getOptionalWhitespace();
        if (parser.getEOX()) {
            parser.getOptionalEOL();
            curLine++;
            parser.getOptionalWhitespace();

            return true;
        } else {
            return false;
        }
    }

    /**
     * Does an EOL/EOF lookup using the parser instance and
     * calls the lines of the parsed file by doing so. The
     * EOL/EOF is optional.
     *
     * @return A boolean value indicating whether there was an
     * 		   eol or an eof or not.
     *
     * @throws IOException
     */
    private boolean getOptionalEOX() throws IOException {
        parser.getOptionalWhitespace();
        if (parser.getOptionalEOX()) {
            curLine++;
            parser.getOptionalWhitespace();

            return true;
        } else {
            return false;
        }
    }
}
