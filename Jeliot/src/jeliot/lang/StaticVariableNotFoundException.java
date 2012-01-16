/*
 * Created on 8.1.2005
 */
package jeliot.lang;

/**
 * @author nmyller
 */
public class StaticVariableNotFoundException extends RuntimeException {

    /**
     * 
     */
    public StaticVariableNotFoundException() {
        super();
    }

    /**
     * @param arg0
     */
    public StaticVariableNotFoundException(String arg0) {
        super(arg0);
    }

    /**
     * @param arg0
     */
    public StaticVariableNotFoundException(Throwable arg0) {
        super(arg0);
    }

    /**
     * @param arg0
     * @param arg1
     */
    public StaticVariableNotFoundException(String arg0, Throwable arg1) {
        super(arg0, arg1);
    }

}
