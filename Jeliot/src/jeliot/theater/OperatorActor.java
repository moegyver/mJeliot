package jeliot.theater;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;

import jeliot.tracker.Tracker;
import jeliot.tracker.TrackerClock;

/**
 * An instance of the OperatorActor class represents a operator in 
 * the expressions. It can be a binary or unary operator and it is shown
 * in the ExpressionActor with the operands. 
 * 
 * @author Pekka Uronen
 * @author Niko Myller 
 * 
 * @see jeliot.theater.ExpressionActor
 */
public class OperatorActor extends Actor {

//  DOC: Document!

    /**
	 *
	 */
	private Image image;
    
    /**
	 *
	 */
	private Image darkImage;

    /**
	 * @param image
	 * @param dark
	 */
	public OperatorActor(Image image, Image dark, String description) {
        this.image = image;
        this.darkImage = dark;
        
        setDescription(description);
    }

    /* (non-Javadoc)
	 * @see jeliot.theater.Actor#paintActor(java.awt.Graphics)
	 */
	public void paintActor(Graphics g) {
        g.drawImage(
                ( (light == SHADED) ?
                    darkImage:
                    image),
                insets.left, insets.top, dummy);
    }

    /* (non-Javadoc)
	 * @see jeliot.theater.Actor#calculateSize()
	 */
	public void calculateSize() {
        setSize(image.getWidth(dummy) + insets.left + insets.right,
                 image.getHeight(dummy) + insets.top + insets.bottom);
    }

    /**
     * Returns an animation that makes the actor appear. Default
     * implementation shows the actor highlighted for given number of
     * milliseconds.
     * 
	 * @see jeliot.theater.Actor#appear(java.awt.Point)
	 */
    public Animation appear(final Point loc) {
        return new Animation() {
            int id = -1;
            
            public void init() {
                this.addActor(OperatorActor.this);
                setLocation(loc);
                setLight(NORMAL);
                this.repaint();
                //TRACKER
                Point p = getRootLocation();
                //id = Tracker.writeToFile("Appear", p.x, p.y, OperatorActor.this.getWidth(), OperatorActor.this.getHeight(), TrackerClock.currentTimeMillis(), id);
                setActorId(Tracker.trackTheater(TrackerClock.currentTimeMillis(), Tracker.APPEAR, getActorId(), Tracker.RECTANGLE, new int[] {p.x}, new int[] {p.y}, getWidth(), getHeight(), 0, -1, getDescription()));
            }

            public void animate(double pulse) {
            }

            public void finish() {
                setLight(NORMAL);
            }

            public void finalFinish() {
                this.passivate(OperatorActor.this);
            }
        };
    }
    
    public String toString() {
        return getDescription();
    }
}