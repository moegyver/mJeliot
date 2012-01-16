package jeliot.theater;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;

import jeliot.tracker.Tracker;
import jeliot.tracker.TrackerClock;

/**
 * Constant box instance represents a place where all the literal constants
 * appear during the animation.
 * 
 * @author Pekka Uronen
 * @author Niko Myller
 */
public class ConstantBox extends Actor {

//  DOC: Document!

    /**
	 *
	 */
	private Image image;
    
    /**
	 * @param image
	 */
	public ConstantBox(Image image) {
		this.image = image;
		setShadow(4);
        
        setDescription("ConstantBox");
	}

    /* (non-Javadoc)
	 * @see jeliot.theater.Actor#paintActor(java.awt.Graphics)
	 */
	public void paintActor(Graphics g) {
        //g.drawImage(image, 0, 0, null);
        g.drawImage(image, 0, 0, dummy);

        //Tracker
        if (getActorId() == -1) {
        Point p = getRootLocation();
        setActorId(Tracker.trackTheater(TrackerClock.currentTimeMillis(),
                Tracker.APPEAR, getActorId(), Tracker.RECTANGLE, new int[] { p.x},
                new int[] { p.y}, getWidth(), getHeight(), 0, -1,
                getDescription()));
        }
    }
    
    /* (non-Javadoc)
	 * @see jeliot.theater.Actor#calculateSize()
	 */
	public void calculateSize() {
        //setSize(image.getWidth(null), image.getHeight(null));
        setSize(image.getWidth(dummy), image.getHeight(dummy));
    }
}
