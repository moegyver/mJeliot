package jeliot.theater;

import java.awt.Graphics;

/**
 * The trace that an actor leaves when moving.
 * Currently this is just a stub implementation.
 * What could be done is to show the path that
 * Actor was moving along. 
 * 
 * @author Pekka Uronen
 * @author Niko Myller
 * 
 * @see jeliot.theater.Actor
 * @see jeliot.theater.Actor#fly(Point,int)
 */
public class Trace {

//  DOC: Document!

    /**
     * Paint the path that the trace contains.
     * 
	 * @param g
	 */
	public void paint(Graphics g) {
    }

    /**
     * Store the points along which the actor is moving.
     * 
	 * @param x
	 * @param y
	 */
	public void putTrace(int x, int y) {
    }
}
