package jeliot.theater;

import java.awt.Graphics;
import java.awt.Image;

/**
 * ImageValueActor is an actor that is used when a
 * value actor should be an image. At the moment this only
 * happens when the value of a variable is unknown visualized
 * as "???". 
 * 
 * @author Pekka Uronen
 * @author Niko Myller
 * 
 * @see jeliot.lang.Value
 */
public class ImageValueActor extends ValueActor {

    /**
	 *
	 */
	private Image image;

    /**
	 * @param image
	 */
	public ImageValueActor(Image image) {
        this.image = image;
	}

    /* (non-Javadoc)
	 * @see jeliot.theater.ValueActor#paintValue(java.awt.Graphics)
	 */
	public void paintValue(Graphics g) {
        //g.drawImage(image, 0, 0, null);
        g.drawImage(image, 0, 0, dummy);
    }

    /* (non-Javadoc)
	 * @see jeliot.theater.ValueActor#calcLabelPosition()
	 */
	protected void calcLabelPosition() { }
    
    /* (non-Javadoc)
	 * @see jeliot.theater.Actor#calculateSize()
	 */
	public void calculateSize() {
        //setSize(image.getWidth(null), image.getHeight(null));
        setSize(image.getWidth(dummy), image.getHeight(dummy));
    }
}