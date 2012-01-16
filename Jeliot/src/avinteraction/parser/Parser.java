package avinteraction.parser;

import java.io.IOException;
import java.io.StreamTokenizer;

/**
 * The main parser routines are encapsulated in this class. Use
 * it for comfortably parsing a file structure using a given
 * StreamTokenizer.
 *
 * @author Gina Haeussge, huge(at)rbg.informatik.tu-darmstadt.de
 */
public class Parser implements ParserInterface {

    //~ Instance fields -----------------------------------------

    /** The StreamTokenizer */
    protected StreamTokenizer stok;

    /** The character used for quoting strings */
    private char quoteChar;

    /** Use case-insensitive parsing or not */
    private boolean ignoreCase;

    /** The character used for line-comments */
    private char commentChar;

    //~ Constructors --------------------------------------------

    /**
     * Constructor
     *
     * @param s The StreamTokenizer to use
     */
    public Parser(StreamTokenizer s) {
        stok = s;
        quoteChar = '"';
        commentChar = '#';
        resetParserSettings();
    }

    //~ Methods -------------------------------------------------

    /**
     * Sets the char used for quoting strings.
     *
     * @param quote The char that should indicate quoted
     * 		  strings.
     */
    public void setQuoteChar(char quote) {
        quoteChar = quote;
    }

    /**
     * Sets the char used for quoting strings.
     *
     * @param comment The char that should indicate
     * 		  one-line-comments.
     */
    public void setCommentChar(char comment) {
        commentChar = comment;
        resetParserSettings();
    }

    /**
     * Sets whether to do case-sensitive matching (false) or not
     * (true)
     *
     * @param tf Switches case-insensitive parsing on and off
     */
    public void setIgnoreCase(boolean tf) {
        ignoreCase = tf;
    }

    /**
     * Looks up a given keyword in the tokenizer. If it is NOT
     * there, an exception will be thrown to indicate wrong
     * syntax.
     *
     * @param keyword The keyword that should be matched
     * 		  against.
     *
     * @return true If keyword was found, otherwise false. Quite
     * 		   needless with this method throwing an exception
     * 		   if the keyword is not found, but it was
     * 		   implemented nevertheless to keep this method
     * 		   synchron with its getOptionalKeyword- twin.
     *
     * @throws IOException Thrown if something goes wrong with
     * 		   the Tokenizer.
     * @throws BadSyntaxException Thrown if the given keyword is
     * 		   not found.
     */
    public boolean getKeyword(String keyword) throws IOException,
            BadSyntaxException {
        stok.nextToken();
        if (stok.ttype == StreamTokenizer.TT_WORD) {
            if ((stok.sval).equals(keyword))
                return (true);
            else if ((stok.sval).equalsIgnoreCase(keyword) && ignoreCase)
                return (true);
            else
                throw (new BadSyntaxException("Keyword \"" + keyword
                        + "\" expected, but found \"" + stok.sval
                        + "\" instead"));
        } else
            throw (new BadSyntaxException(
                    "Keyword \""
                            + keyword
                            + "\" expected, but found something completely different instead. " + stok.ttype));
    }

    /**
     * Looks up a given optional keyword in the Tokenizer.
     *
     * @param keyword The keyword to match against
     *
     * @return True if the keyword was found, false otherwise.
     *
     * @throws IOException Thrown if something mad goes on with
     * 		   the Tokenizer.
     */
    public boolean getOptionalKeyword(String keyword) throws IOException {
        stok.nextToken();
        if (stok.ttype == StreamTokenizer.TT_WORD) {
            if ((stok.sval).equals(keyword))
                return true;
            else if ((stok.sval).equalsIgnoreCase(keyword) && ignoreCase)
                return true;
            else {
                stok.pushBack();
                return false;
            }
        } else {
            stok.pushBack();
            return false;
        }
    }

    /**
     * Checks for an integer in the tokenizer. Throws an
     * exception if something different than a number is found.
     *
     * @return The found integer value.
     *
     * @throws IOException Thrown if something goes wrong with
     * 		   the Tokenizer.
     * @throws BadSyntaxException Thrown if something different
     * 		   than a number was found.
     */
    public int getNumber() throws IOException, BadSyntaxException {
        stok.nextToken();
        if (stok.ttype == StreamTokenizer.TT_NUMBER) {
            return ((int) stok.nval);
        } else
            throw (new BadSyntaxException(
                    "Number expected but found something different instead"));
    }

    /**
     * Checks for an optional integer in the tokenizer. Because
     * this method returns only -1 if no intVal was found, use
     * it WITH CAUTION!
     *
     * @return The found integer value if any, or -1.
     *
     * @throws IOException Thrown if something goes wrong with
     * 		   the Tokenizer.
     */
    public int getOptionalNumber() throws IOException {
        stok.nextToken();
        if (stok.ttype == StreamTokenizer.TT_NUMBER) {
            return ((int) stok.nval);
        } else
            return -1;
    }

    /**
     * Checks for a word in the tokenizer. If none is found, an
     * exception is thrown.
     *
     * @return The word found.
     *
     * @throws IOException Thrown if something goes wrong with
     * 		   the Tokenizer.
     * @throws BadSyntaxException Thrown if no word but
     * 		   something different was found.
     */
    public String getWord() throws IOException, BadSyntaxException {
        stok.nextToken();
        if (stok.ttype == StreamTokenizer.TT_WORD) {
            return (stok.sval);
        } else
            throw (new BadSyntaxException(
                    "Word expected but found something different instead. " + stok.ttype));
    }

    /**
     * Checks for an optional word in the tokenizer.
     *
     * @return The word found.
     *
     * @throws IOException Thrown if something goes wrong with
     * 		   the Tokenizer.
     */
    public String getOptionalWord() throws IOException {
        stok.nextToken();
        if (stok.ttype == StreamTokenizer.TT_WORD) {
            return (stok.sval);
        } else {
            stok.pushBack();
            return "";
        }
    }

    /**
     * Checks for a quoted string in the tokenizer. If there is
     * none, an execption is thrown.
     *
     * @return The content of the quoted string.
     *
     * @throws IOException Thrown if something goes wrong with
     * 		   the Tokenizer.
     * @throws BadSyntaxException Thrown if something differeent
     * 		   than a quoted string is found, or there are no
     * 		   closing quotes before eof.
     */
    public String getQuoted() throws IOException, BadSyntaxException {
        String theString = "";

        //System.out.println("found: " + (char) stok.nextToken());
        stok.nextToken();
        if (stok.ttype == (int) quoteChar) {
            setQuotedSettings();
            stok.nextToken();
            while (stok.ttype != (int) quoteChar) {
                if (stok.ttype == StreamTokenizer.TT_WORD)
                    theString += stok.sval;
                else if (stok.ttype == StreamTokenizer.TT_NUMBER)
                    theString += (int) stok.nval;
                else if (stok.ttype == StreamTokenizer.TT_EOF)
                    throw (new BadSyntaxException(
                            "End of quote expected but found end of file instead"));
                else
                    theString += (char) stok.ttype;

                stok.nextToken();
            }
            
            resetParserSettings();
            //System.out.println("found in the end: " + theString);
            return theString;
        } else
            throw (new BadSyntaxException(
                    "Quoted String expected but found something different instead. " + stok.ttype));
    }

    /**
     * Checks for an optional quoted string in the tokenizer.
     *
     * @return The content of the quoted string.
     *
     * @throws IOException Thrown if something goes wrong with
     * 		   the Tokenizer.
     * @throws BadSyntaxException Thrown if no closing quotes
     * 		   are found before eof.
     */
    public String getOptionalQuoted() throws IOException, BadSyntaxException {
        String theString = "";

        stok.nextToken();
        if (stok.ttype == (int) quoteChar) {
            setQuotedSettings();
            stok.nextToken();
            while (stok.ttype != (int) quoteChar) {
                if (stok.ttype == StreamTokenizer.TT_WORD)
                    theString += stok.sval;
                else if (stok.ttype == StreamTokenizer.TT_NUMBER)
                    theString += (int) stok.nval;
                else if (stok.ttype == StreamTokenizer.TT_EOF)
                    throw (new BadSyntaxException(
                            "End of quote expected but found end of file instead"));
                else
                    theString += (char) stok.ttype;

                stok.nextToken();
            }

            resetParserSettings();
            return theString;
        } else {
            stok.pushBack();
            return "";
        }
    }

    /**
     * Looks for a character in the tokenizer. If none is found,
     * an exception is thrown.
     *
     * @return The found character.
     *
     * @throws IOException Thrown if something goes wrong with
     * 		   the Tokenizer.
     * @throws BadSyntaxException Thrown if something different
     * 		   than a single  character is found.
     */
    public char getChar() throws IOException, BadSyntaxException {
        stok.nextToken();
        if (stok.ttype == StreamTokenizer.TT_WORD)
            throw (new BadSyntaxException(
                    "Expected a single character but found a word instead. " + stok.sval));
        else if (stok.ttype == StreamTokenizer.TT_NUMBER)
            throw (new BadSyntaxException(
                    "Expected a single character but found a number instead."));
        else if (stok.ttype == StreamTokenizer.TT_EOL)
            throw (new BadSyntaxException(
                    "Expected a single character but found a eol instead."));
        else if (stok.ttype == StreamTokenizer.TT_EOF)
            throw (new BadSyntaxException(
                    "Expected a single character but found a eof instead."));

        return (char) stok.ttype;
    }

    /**
     * Looks for an optional character in the tokenizer.
     *
     * @return The found character.
     *
     * @throws IOException Thrown if something goes wrong with
     * 		   the Tokenizer.
     */
    public char getOptionalChar() throws IOException {
        stok.nextToken();
        if (stok.ttype == StreamTokenizer.TT_WORD) {
            stok.pushBack();
            return (char) 0;
        } else if (stok.ttype == StreamTokenizer.TT_NUMBER) {
            stok.pushBack();
            return (char) 0;
        } else if (stok.ttype == StreamTokenizer.TT_EOL) {
            stok.pushBack();
            return (char) 0;
        } else if (stok.ttype == StreamTokenizer.TT_EOF) {
            stok.pushBack();
            return (char) 0;
        }

        return (char) stok.ttype;
    }

    /**
     * Looks for an EOL in the tokenizer. If none is found, an
     * exception is thrown.
     *
     * @return A boolean value being true if an EOL was found.
     *
     * @throws IOException Thrown if something goes wrong with
     * 		   the Tokenizer.
     * @throws BadSyntaxException Thrown if no eol is found.
     */
    public boolean getEOL() throws IOException, BadSyntaxException {
        stok.nextToken();
        if (stok.ttype == StreamTokenizer.TT_EOL || stok.ttype == '\r')
            return true;
        else
            throw (new BadSyntaxException(
                    "Expected eol but found something different instead. " + stok.ttype));
    }

    /**
     * Looks for an optional EOL in the tokenizer.
     *
     * @return A boolean value being true if an EOL was found,
     * 		   false if otherwise.
     *
     * @throws IOException Thrown if something goes wrong with
     * 		   the Tokenizer.
     */
    public boolean getOptionalEOL() throws IOException {
        stok.nextToken();
        if (stok.ttype == StreamTokenizer.TT_EOL || stok.ttype == '\r')
            return true;
        else {
            stok.pushBack();
            return false;
        }
    }

    /**
     * Looks for an EOL or an EOF in the tokenizer. If neither
     * is found, an exception is thrown. If an EOF is found, it
     * is pushed back on the tokenizer.
     *
     * @return A boolean value being true if an EOL or EOF was
     * 		   found.
     *
     * @throws IOException Thrown if something goes wrong with
     * 		   the Tokenizer.
     * @throws BadSyntaxException Thrown if neither eol nor eof
     * 		   is found.
     */
    public boolean getEOX() throws IOException, BadSyntaxException {
        stok.nextToken();
        if (stok.ttype == StreamTokenizer.TT_EOL || stok.ttype == '\r') {
            return true;
        } else if (stok.ttype == StreamTokenizer.TT_EOF) {
            stok.pushBack();
            return true;
        } else
            throw (new BadSyntaxException(
                    "Expected eol but found something different instead."));
    }

    /**
     * Looks for an optional EOL or EOF in the tokenizer. If an
     * EOF is  found, it is pushed back on the tokenizer.
     *
     * @return A boolean value being true if an EOL or EOF was
     * 		   found.
     *
     * @throws IOException Thrown if something goes wrong with
     * 		   the Tokenizer.
     */
    public boolean getOptionalEOX() throws IOException {
        stok.nextToken();
        if (stok.ttype == StreamTokenizer.TT_EOL || stok.ttype == '\r') {
            //System.out.println("Found eol");
            return true;
        } else if (stok.ttype == StreamTokenizer.TT_EOF) {
            //System.out.println("Found eof");
            stok.pushBack();
            return true;
        } else {
            //System.out.println("Found something else: " + (char) stok.ttype);
            stok.pushBack();
            return false;
        }
    }

    /**
     * Looks for whitespace in the tokenizer. If none is found,
     * an exception is thrown. Whitespace is defined as ' ' or
     * '\t'
     *
     * @return A String consisting of the found whitespace.
     *
     * @throws IOException Thrown if something goes wrong with
     * 		   the Tokenizer.
     * @throws BadSyntaxException Thrown if no whitespace is
     * 		   found.
     */
    public String getWhitespace() throws IOException, BadSyntaxException {
        String whitespace = "";

        stok.nextToken();
        if ((stok.ttype == (int) ' ') || (stok.ttype == (int) '\t')) {
            while ((stok.ttype == (int) ' ') || (stok.ttype == (int) '\t')) {
                whitespace += (char) stok.ttype;
                stok.nextToken();
            }

            stok.pushBack();
            return whitespace;
        } else {
            stok.pushBack();
            throw (new BadSyntaxException(
                    "Expected Whitespace but found something completely different instead."));
        }
    }

    /**
     * Looks for optional whitespace in the tokenizer.
     * Whitespace is defined as ' ' or '\t'
     *
     * @return A String consisting of the found whitespace.
     *
     * @throws IOException Thrown if something goes wrong with
     * 		   the Tokenizer.
     */
    public String getOptionalWhitespace() throws IOException {
        String whitespace = "";

        stok.nextToken();
        if ((stok.ttype == (int) ' ') || (stok.ttype == (int) '\t')) {
            while ((stok.ttype == (int) ' ') || (stok.ttype == (int) '\t')) {
                whitespace += (char) stok.ttype;
                stok.nextToken();
            }

            stok.pushBack();
            //System.out.println("Found" + whitespace);
            return whitespace;
        } else {
            //System.out.println("Nothing found but " + (char) stok.ttype);
            stok.pushBack();
            return "";
        }
    }

    /**
     * Switch tokenizer to recognizing numbers as parts of
     * strings.
     */
    public void setQuotedSettings() {
        stok.resetSyntax();
        stok.wordChars('a', 'z');
        stok.wordChars('A', 'Z');
        stok.wordChars('0', '9');
        stok.eolIsSignificant(true);
        stok.ordinaryChar((int) quoteChar);
        stok.ordinaryChar((int) commentChar);
    }

    /**
     * Switch tokenizer to parsing numbers in its normal way.
     */
    public void resetParserSettings() {
        stok.resetSyntax();
        stok.wordChars('a', 'z');
        stok.wordChars('A', 'Z');
        stok.parseNumbers();
        stok.eolIsSignificant(true);
        stok.ordinaryChar((int) quoteChar);
        stok.commentChar((int) commentChar);
    }
}
