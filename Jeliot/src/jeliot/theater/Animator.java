package jeliot.theater;

import jeliot.lang.Value;

/**
 * An abstract class that handles the animation. Currectly, it is
 * only used in the input handling in Theater class. 
 * 
 * @author Pekka Uronen
 * @author Niko Myller
 * 
 * @see jeliot.theater.Director.InputAnimator
 * @see jeliot.theater.Director#readChar()
 * @see jeliot.theater.Director#readDouble()
 * @see jeliot.theater.Director#readInt()
 * @see jeliot.theater.Director#readString()
 * @see jeliot.theater.Director#animateInputHandling(String,Highlight)
 */
public abstract class Animator {

//DOC: Document!

    /**
	 *
	 */
	private Value[] args;
    
    /**
	 *
	 */
	private ValueActor[] argact;
    
    /**
	 *
	 */
	private Value returnValue;

    /**
	 * @param args
	 */
	public void setArguments(Value[] args) {
        this.args = args;
    }

    /**
	 * @param argact
	 */
	public void setArgumentActors(ValueActor[] argact) {
        this.argact = argact;
    }

    /**
	 * @return
	 */
	public Value getReturnValue() {
        return returnValue;
    }

    /**
	 * @param v
	 */
	public void setReturnValue(Value v) {
        this.returnValue = v;
    }

    /**
	 * @param i
	 * @return
	 */
	public Value getArgument(int i) {
        return args[i];
    }

    /**
	 * @param i
	 * @return
	 */
	protected Actor getArgumentActor(int i) {
        return argact[i];
    }

    /**
	 * @param director
	 */
	public abstract void animate(Director director);
}
