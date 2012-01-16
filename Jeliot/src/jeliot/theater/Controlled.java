package jeliot.theater;

/**
 * Controlled interface is implemented by a class whose instances should be
 * controlled by the ThreadController instance.
 * 
 * @author Pekka Uronen
 * @author Niko Myller
 * 
 * @see jeliot.theater.ThreadController
 * @see jeliot.theater.AnimationEngine
 * @see jeliot.theater.Director#getInput(String,InputValidator)
 */
public interface Controlled {
    
    /**
	 * Suspend the action that is currently done. 
	 */
	public abstract void suspend();
    
    /**
	 * Continue the action that was suspended.
	 */
	public abstract void resume();
}
