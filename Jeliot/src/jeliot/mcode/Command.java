package jeliot.mcode;

/**
 * Currently this class is not used in Jeliot 3.
 * 
 * @author Niko Myller
 */
public class Command {

//  DOC: document!
    /**
	 *
	 */
	private int expressionReference = 0;
    
    /**
	 *
	 */
	private int type = 0;

    /**
	 * 
	 */
	protected Command() { }

    /**
	 * @param t
	 * @param er
	 */
	public Command(int t, int er) {
        this.type = t;
        this.expressionReference = er;
    }

    /**
	 * @param er
	 */
	public void setExpressionReference(int er) {
        this.expressionReference = er;
    }

    /**
	 * @param t
	 */
	public void setType(int t) {
        this.type = t;
    }

    /**
	 * @return
	 */
	public int getExpressionReference() {
        return this.expressionReference;
    }

    /**
	 * @return
	 */
	public int getType() {
        return this.type;
    }

}