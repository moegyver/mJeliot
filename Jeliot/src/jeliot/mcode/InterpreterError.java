package jeliot.mcode;


/**
 * This class encapsulates the errors related
 * to the interpretation of the user program.
 * 
 * @author Niko Myller
 */
public class InterpreterError {

	/**
	 * The detailed message
	 */
	protected String message;

//  DOC: document!
	/**
	 *
	 */
	protected Highlight highlight;

	/**
     * Constructs an <code>InterpreterException</code> from an error message and
     * a highlighting information.
     * 
	 * @param message
	 * @param h
	 */
	public InterpreterError(String message, Highlight h) {
		this.message = message;
		this.highlight = h;
	}

	/**
	 * @param message
	 */
	public InterpreterError(String message) {
		this(message, null);
	}

	/**
	 * @return
	 */
	public Highlight getHighlight() {
		return highlight;
	}

	/**
	 * Returns the detailed message
	 * @return
	 */
	public String getMessage() {
		return message;
	}
}
