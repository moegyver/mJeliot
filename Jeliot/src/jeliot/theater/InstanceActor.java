package jeliot.theater;

import java.util.Enumeration;
import java.util.Vector;

/**
 * InstanceActor is a base class for all the instances: ArrayActors and
 * ObjectStage. An instance of this class should not be instantiated.
 * 
 * @author Pekka Uronen
 * @author Niko Myller 
 * 
 * @see jeliot.lang.Instance
 */
public abstract class InstanceActor extends Actor implements ActorContainer {

    //  DOC: Document!

    /**
     * Comment for <code>references</code>
     */
    private Vector references = new Vector();

    /**
     * Comment for <code>position</code>
     */
    private int referencePosition;

    /**
     * 
     */
    protected InstanceActor() {
    }

    /**
     * @param ref
     */
    public void addReference(ReferenceActor ref) {
        if (!references.contains(ref)) {
            references.addElement(ref);
        }
    }

    /**
     * @param ref
     */
    public void removeReference(ReferenceActor ref) {
        references.removeElement(ref);
    }

    /**
     * @return
     */
    public int getNumberOfReferences() {
        return references.size();
    }

    /* (non-Javadoc)
     * @see jeliot.theater.Actor#setLocation(int, int)
     */
    public void setLocation(int x, int y) {
        super.setLocation(x, y);
        Enumeration enumeration = references.elements();
        while (enumeration.hasMoreElements()) {
            ReferenceActor actor = (ReferenceActor) enumeration.nextElement();
            actor.calculateBends();
        }
    }

    /* (non-Javadoc)
     * @see jeliot.theater.ActorContainer#removeActor(jeliot.theater.Actor)
     */
    public void removeActor(Actor actor) {
    }

    /**
     * @return Returns the position.
     */
    public int getReferencePosition() {
        return referencePosition;
    }

    /**
     * @param position The position to set.
     */
    public void setReferencePosition(int position) {
        this.referencePosition = position;
    }
}
