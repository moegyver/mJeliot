package jeliot.lang;


/**
 * Reference is a value of a reference type meaning that all the
 * references to instances are objects of this class. 
 * 
 * @author Pekka Uronen
 * @author Niko Myller
 */
public class Reference extends Value {

//  DOC: document!
    /**
	 *
	 */
	private Instance instance;
	/**
	 *
	 */
	private boolean referenced = false;
    
    /**
	 * 
	 */
    /*
	public Reference() {
        super("null", "null");
    }
	*/
    
    /**
     * 
     * @param type
     */
	public Reference(String type) {
		super("null", type);
	}

    /**
	 * @param instance
	 */
	public Reference(Instance instance) {
        super(instance.getHashCode(), instance.getType());
        this.instance = instance;
        //this.instance.reference();
    }

    /* (non-Javadoc)
	 * @see java.lang.Object#finalize()
	 */
	protected void finalize() {
	    unmakeReference();
    }

    /**
	 * 
	 */
	public void makeReference() {
	    if (instance != null && !referenced) {
		    instance.reference();
		    referenced = true;
	    }
    }
    
    /**
	 * 
	 */
	public void unmakeReference() {
	 	if (instance != null && referenced) {
		 	instance.dereference();
		 	referenced = false;
	 	}   
    }
    
    /**
	 * @param inst
	 */
	public void setInstance(Instance inst) {
        this.instance = inst;
    }

    /**
	 * @return
	 */
	public Instance getInstance() {
        return this.instance;
    }
    
    /**
	 * @param value
	 */
	public void setReferenced(boolean value) {
		referenced = value;
	}
	    
    /* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	public Object clone() {
	    Object obj = super.clone();
        //instance.reference();
        if (obj != null) {
	        ((Reference)obj).setReferenced(false);
        }
        return obj;
    }
}
