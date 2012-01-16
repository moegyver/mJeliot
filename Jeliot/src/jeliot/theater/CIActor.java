/*
 * Created on 5.7.2006
 */
package jeliot.theater;

/**
 * @author nmyller
 */
public class CIActor extends MIActor {

    private String actualName;
    /**
     * @param name
     * @param n
     */
    public CIActor(String name, int n) {
        super("new " + name, n);
        this.actualName = name;
        setDescription("constructor invocation: " + name);
    }

    /* (non-Javadoc)
     * @see jeliot.theater.MIActor#toString()
     */
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("Constructor of the object of the class ");
        sb.append(getName());
        sb.append(" is called ");
        sb.append(parametersToString());
        return sb.toString();
    }

}
