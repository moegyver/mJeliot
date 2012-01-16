package jeliot.lang;

import jeliot.theater.*;

/**
 * The base class for all the instances.
 * 
 * @author Pekka Uronen
 * @author Niko Myller
 */
public class Instance {

    //DOC: document!

    /**
     * 
     */
    public static final Instance OUTSIDE_OBJECT = new Instance(null,
            "java.lang.Object");
    static {
        OUTSIDE_OBJECT.setActor(ObjectStage.OUTSIDE_OBJECT);
    }

    /**
     *
     */
    private String type;

    /**
     *
     */
    private InstanceActor actor;

    /**
     *
     */
    private String hashCode;

    /**
     *
     */
    private int references = 0;

    /**
     * @param hashCode
     */
    protected Instance(String hashCode) {
        this.hashCode = hashCode;
    }

    /**
     * @param hashCode
     * @param type
     */
    protected Instance(String hashCode, String type) {
        this.hashCode = hashCode;
        this.type = type;
    }

    /**
     * @param type
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * @return
     */
    public String getType() {
        return type;
    }

    /**
     * @param actor
     */
    public void setActor(InstanceActor actor) {
        this.actor = actor;
    }

    /**
     * @return
     */
    public InstanceActor getActor() {
        return actor;
    }

    /**
     * @param hashCode
     */
    public void setHashCode(String hashCode) {
        this.hashCode = hashCode;
    }

    /**
     * @return
     */
    public String getHashCode() {
        return hashCode;
    }

    /**
     * 
     */
    public void reference() {
        references++;
    }

    /**
     * 
     */
    public void dereference() {
        references--;
    }

    /**
     * @return
     */
    public int getNumberOfReferences() {
        return references;
    }
}
