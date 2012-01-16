package jeliot.theater;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;

import jeliot.tracker.Tracker;
import jeliot.tracker.TrackerClock;

/**
 * 
 * Animating actor is a actor that may be used to show frame based
 * animation.
 *
 * @author Pekka Uronen
 * @author Niko Myller
 */
public class AnimatingActor extends Actor {

//  DOC: document!
    /**
	 *
	 */
	private Image image;

    /**
	 * @param image
	 */
	public AnimatingActor(Image image, String description) {
        this.image = image;
        setShadow(0);
        
        setDescription(description);
	}

    /* (non-Javadoc)
	 * @see jeliot.theater.Actor#paintActor(java.awt.Graphics)
	 */
	public void paintActor(Graphics g) {
        //g.drawImage(image, insets.left, insets.top, null);
        g.drawImage(image, insets.left, insets.top, dummy);
    }

    /* (non-Javadoc)
	 * @see jeliot.theater.Actor#calculateSize()
	 */
	public void calculateSize() {
        setSize(image.getWidth(dummy) + insets.left + insets.right,
                 image.getHeight(dummy) + insets.top + insets.bottom);
    }

    /**
	 * @param image
	 */
	public void setImage(Image image) {
        this.image = image;
    }

    /**
     * Returns an animation object that changes the image of this
     * actor to the given image.
	 * @param chim
	 * @return
	 */
    public Animation changeImage(final Image chim) {
        return new Animation() {
                     
            int id = -1;
            
            public void animate(double pulse) { }

            public void finish() {
                //TRACKER
                Point p = getRootLocation();
                //id = Tracker.writeToFile("ChangeImage", p.x, p.y, chim.getWidth(null), chim.getHeight(null), TrackerClock.currentTimeMillis(), id);
                if (getActorId() == -1) {
                    setActorId(Tracker.trackTheater(TrackerClock.currentTimeMillis(), Tracker.APPEAR, getActorId(), Tracker.RECTANGLE, new int[] {p.x}, new int[] {p.y}, getWidth(), getHeight(), 0, -1, getDescription()));
                } else {
                    Tracker.trackTheater(TrackerClock.currentTimeMillis(), Tracker.MODIFY, getActorId(), Tracker.RECTANGLE, new int[] {p.x}, new int[] {p.y}, getWidth(), getHeight(), 0, -1, getDescription());
                }
                setImage(chim);
            }
        };
    }
}
