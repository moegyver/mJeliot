package jeliot.mcode;

/**
 * Highlight represents a single highlight of the source code
 * during the animation. This class contains the information 
 * about the beginning and ending line and column.
 * 
 * @author Niko Myller
 */
public class Highlight {

//  DOC: Document!
    /**
	 *
	 */
    private int beginLine;
    
	/**
	 *
	 */
	private int beginColumn;
    
    /**
     *
     */
    private int endLine;
    
    /**
     *
     */
    private int endColumn;

    /**
	 * 
	 */
	protected Highlight() { }

    /**
	 * @param bl
	 * @param bc
	 * @param el
	 * @param ec
	 */
	public Highlight(int bl, int bc, int el, int ec) {
        this.beginLine = bl;
        this.beginColumn = bc;
        this.endLine = el;
        this.endColumn = ec;
    }

    /**
	 * @return
	 */
	public int getBeginLine() {
        return this.beginLine;
    }

    /**
	 * @return
	 */
	public int getBeginColumn() {
        return this.beginColumn;
    }

    /**
	 * @return
	 */
	public int getEndLine() {
        return this.endLine;
    }

    /**
	 * @return
	 */
	public int getEndColumn() {
        return this.endColumn;
    }
    
    public boolean equals(Object o) {
        if (!(o instanceof Highlight)) {
            return false;
        }
        Highlight h = (Highlight) o;
        if (h.beginColumn == this.beginColumn &&
            h.endColumn == this.endColumn &&
            h.beginLine == this.beginLine &&
            h.endLine == this.endLine) {
            return true;
        }
        return false;
    }
    
    public String toString() {
        StringBuffer stringBuilder = new StringBuffer();
        stringBuilder.append("{");
        stringBuilder.append(this.beginLine);
        stringBuilder.append(",");
        stringBuilder.append(this.beginColumn);
        stringBuilder.append(",");
        stringBuilder.append(this.endLine);
        stringBuilder.append(",");
        stringBuilder.append(this.endColumn);
        stringBuilder.append("}");
        return stringBuilder.toString();
    }
}