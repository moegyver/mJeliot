package avinteraction.parser;

import java.io.IOException;


/**
 * The Parser Interface
 */
public interface ParserInterface
{

	//~ Methods -------------------------------------------------

	/**
	 * Get a keyword
	 */
	public boolean getKeyword(String keyword)
		throws IOException, BadSyntaxException;

	/**
	 * Get an optional keyword
	 */
	public boolean getOptionalKeyword(String keyword)
		throws IOException;

	/**
	 * Get a number
	 */
	public int getNumber()
		throws IOException, BadSyntaxException;

	/**
	 * Get an optional number
	 */
	public int getOptionalNumber()
		throws IOException;

	/**
	 * Get a word
	 */
	public String getWord()
		throws IOException, BadSyntaxException;

	/**
	 * Get an optional word
	 */
	public String getOptionalWord()
		throws IOException;

	/**
	 * Get a quoted String
	 */
	public String getQuoted()
		throws IOException, BadSyntaxException;

	/**
	 * Get an optional quoted String
	 */
	public String getOptionalQuoted()
		throws IOException, BadSyntaxException;

	/**
	 * Get a single character
	 */
	public char getChar()
		throws IOException, BadSyntaxException;

	/**
	 * Get an optional single character
	 */
	public char getOptionalChar()
		throws IOException;

	/**
	 * Get an EOL
	 */
	public boolean getEOL()
		throws IOException, BadSyntaxException;

	/**
	 * Get an optional EOL
	 */
	public boolean getOptionalEOL()
		throws IOException;

	/**
	 * Get an EOL or an EOF
	 */
	public boolean getEOX()
		throws IOException, BadSyntaxException;

	/**
	 * Get an optional EOL or an optional EOF
	 */
	public boolean getOptionalEOX()
		throws IOException;

	/**
	 * Get whitespace
	 */
	public String getWhitespace()
		throws IOException, BadSyntaxException;

	/**
	 * Get optional whitespace
	 */
	public String getOptionalWhitespace()
		throws IOException;
}
