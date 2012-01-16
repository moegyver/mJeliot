package jeliot;

/**
  * This exception is thrown when the parser discovers use of a language
  * feature that is not (yet) implemented in Jeliot.
  *
  * @author Pekka Uronen
  * @author Niko Myller
  */
public class FeatureNotImplementedException extends RuntimeException {

    /**
	* The only constructor of the FeatureNoeImplementedException.
	*
	* @param s The info string of the exception.
	*
	*/
    public FeatureNotImplementedException(String s) {
        super(s);
    }
    
}
