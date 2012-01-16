/*
 * Created on 15.2.2006
 */
package jeliot.lang;

/**
 * @author nmyller
 */
public class LocalVariableNotFoundException extends RuntimeException {

    /**
     * 
     */
    public LocalVariableNotFoundException() {
        super();
    }

    /**
     * @param message
     */
    public LocalVariableNotFoundException(String message) {
        super(message);
    }

    /**
     * @param message
     * @param cause
     */
    public LocalVariableNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * @param cause
     */
    public LocalVariableNotFoundException(Throwable cause) {
        super(cause);
    }

}
