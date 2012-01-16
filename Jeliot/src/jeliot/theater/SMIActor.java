package jeliot.theater;


/**
 * SMIActor represents graphically the static method invocation. The
 * actor shows the  method name and the parameters in a similar
 * way as Java syntax just replaces the variable references with
 * their actual values. 
 * 
 * @author Pekka Uronen
 * @author Niko Myller
 *
 * @see jeliot.theater.OMIActor 
 */
public class SMIActor extends MIActor {

    // DOC: Document!

    /**
     * @param name
     * @param n
     */
    public SMIActor(String name, int n) {
        super(name, n);
        setDescription("static method invocation: " + name);
    }


    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("Static method ");
        sb.append(getName());
        sb.append(" is called ");
        sb.append(parametersToString());
        return sb.toString();
    }
}
