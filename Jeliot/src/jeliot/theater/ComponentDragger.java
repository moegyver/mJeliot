package jeliot.theater;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

/**
 * This class is not currently used by Jeliot.
 * ComponentDragger is helping the dragging of the Theater's components.
 *
 * @author Pekka Uronen
 * @author Niko Myller
 */
public class ComponentDragger implements MouseListener,
                                         MouseMotionListener {
                                             
//DOC: Document!
                                             
    /**
	 *
	 */
	final static int FREE   = 0;
    /**
	 *
	 */
	final static int DRAG   = 1;
    /**
	 *
	 */
	final static int RESIZE = 2;
    /**
	 *
	 */
	final static int FIXED  = 3;

    /**
	 *
	 */
	int mode = FREE;
    /**
	 *
	 */
	Point dragPoint;
    /**
	 *
	 */
	Dimension origSize;
    /**
	 *
	 */
	Component comp;

    /**
	 * @param comp
	 */
	public ComponentDragger(Component comp) {
        this.comp = comp;
        comp.addMouseListener(this);
        comp.addMouseMotionListener(this);
        comp.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }


    /**
	 * @param p
	 * @return
	 */
	boolean inStretchArea(Point p) {
        Dimension d = comp.getSize();
        return (p.x > d.width - 8) && (p.y > d.height-8);
    }

    /* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
	 */
	public void mousePressed(MouseEvent e) {
        dragPoint = new Point(e.getX(), e.getY());
        origSize = comp.getSize();
        mode = inStretchArea(dragPoint) ? RESIZE : DRAG;

    }

    /* (non-Javadoc)
	 * @see java.awt.event.MouseMotionListener#mouseDragged(java.awt.event.MouseEvent)
	 */
	public void mouseDragged(MouseEvent e) {
        switch (mode) {
            case (DRAG):
                Point loc = comp.getLocation();
                loc.translate(e.getX(), e.getY());
                loc.translate(-dragPoint.x, -dragPoint.y);
                comp.setLocation(loc);
                break;
            case (RESIZE):
                int w = e.getX() - dragPoint.x + origSize.width;
                int h = e.getY() - dragPoint.y + origSize.height;
                comp.setSize(w, h);
                break;
        }
        comp.getParent().repaint();

    }

    /* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
	 */
	public void mouseReleased(MouseEvent e) {
        mode = FREE;
        comp.invalidate();
        comp.validate();
    }

    /**
	 * @return
	 */
	public boolean isFree() {
        return mode == FREE;
    }

    /* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
	 */
	public void mouseClicked(MouseEvent e) {}
    /* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
	 */
	public void mouseExited(MouseEvent e) {}
    /* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
	 */
	public void mouseEntered(MouseEvent e) {}
    /* (non-Javadoc)
	 * @see java.awt.event.MouseMotionListener#mouseMoved(java.awt.event.MouseEvent)
	 */
	public void mouseMoved(MouseEvent e) {}
}
