package jeliot.lang;

import jeliot.theater.*;
import jeliot.util.DebugUtil;

/**
 * Value represents any primitive type of value and a String type and
 * is the base class for reference values.
 *
 * @author Pekka Uronen
 * @author Niko Myller
 */
public class Value implements Cloneable {


    /**
	 * A String representation of the type of the value.
	 */
	private String type;

	/**
     * A String describing the represented value. Can be, for
     * example Integer, Char or if the value is actually a reference
     * it will be the hashcode that identifies the object.
     */
    private String val;
    
    /**
	 * The actor of this values.
	 */
	private ValueActor actor;

    /**
	 * The id of the value. This is used for searching a certain value.
	 */
	private int id;

//  DOC: document!
    /**
	 * @param val
	 * @param type
	 */
	public Value(String val, String type) {
        this.type = type;
        this.val = val;
    }

    /**
	 * @param val
	 * @param type
	 * @param id
	 */
	public Value(String val, String type, int id) {
        this.type = type;
        this.val = val;
        this.id = id;
    }

    /**
	 * @param id
	 */
	public void setId(int id) {
        this.id = id;
    }

    /**
	 * @return
	 */
	public int getId() {
        return id;
    }

    /**
	 * @param b
	 * @return
	 */
	public static Value newBoolean(boolean b) {
        String bool = b ? Boolean.TRUE.toString() : Boolean.FALSE.toString();
        return new Value(bool, boolean.class.toString());
    }

    /* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
        return type.toString() + " " + val.toString();
    }

    /**
	 * @return
	 */
	public ValueActor getActor() {
        return actor;
    }

    /**
	 * @return
	 */
	public String getValue() {
        return val;
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
	public void setActor(ValueActor actor) {
        this.actor = actor;
    }
    /**
     * @param actor
     */
    public void setValue(String value) {
        this.val = value;
    }
    /* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	public Object clone() {
        try {
            return super.clone();
        }
        catch (CloneNotSupportedException e) {
            //TODO: report to user that something went wrong!
            if (DebugUtil.DEBUGGING) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
