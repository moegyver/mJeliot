package jeliot.gui;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JPanel;

/**
  * Not currently used in Jeliot 3.
  * The design of the draggable component that perhaps will be added inside Jeliot's
  * next version.
  *
  * @author Pekka Uronen
  * @author Niko Myller
  */
public class DraggableComponent extends JPanel implements MouseListener,
        MouseMotionListener {


	/**
	 * A constant variable to describe the state of the draggable component.
	 */
    final static int FREE   = 0;

	/**
	 * A constant variable to describe the state of the draggable component.
	 */
    final static int DRAG   = 1;

	/**
	 * A constant variable to describe the state of the draggable component.
	 */
    final static int RESIZE = 2;

	/**
	 * A constant variable to describe the state of the draggable component.
	 */
    final static int FIXED  = 3;
    

	/**
	 * This variable shows the state of the draggable component.
	 */    
    int mode = FREE;

	/**
	 * This variable will keep the starting point of dragging.
	 */
    Point dragPoint;

	/**
	 * This variable will hold the original size of the draggable component.
	 */
    Dimension origSize;

	/**
	 * Constructs the DraggableComponent -objects.
	 * Adds MouseListener and MouseMotionListener.
	 * Sets new cursor for the component area.
	 */
    protected DraggableComponent() {
        addMouseListener(this);
        addMouseMotionListener(this);
        
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

	/**
	 * Checks if the given Point p is inside that area where you resize the component.
	 *
	 * @param p	The point consist of the coordinates of the cursor when the mouse was clicked.
	 * @return	True if the cursor is on the resize area and false when it isn't on that area.
	 */
    boolean inStretchArea(Point p) {
        Dimension d = getSize();
        return (p.x > d.width - 8) && (p.y > d.height-8);
    }

	/**
	 * Handles the event when the mouse's left button is pressed.
	 * Tests if user wants to resize or drag the component and changes the 
	 * mode in that way.
	 *
	 * @param	e	Mouse event of the mouse button pressing.
	 */
    public void mousePressed(MouseEvent e) {
        dragPoint = new Point(e.getX(), e.getY());
        origSize = getSize();
        mode = inStretchArea(dragPoint) ? RESIZE : DRAG;
       
        repaint();
    }


	/**
	 * Handles the mouse event when the mouse is dragged.
	 * If the mode is DRAG then this method moves the component as dragged.
	 * If the mode is RESIZE then this method resizes the component as 
	 * dragged.
	 *
	 * @param	e	Mouse event of the mouse dragging.
	 */
    public void mouseDragged(MouseEvent e) {
        switch (mode) {
            case (DRAG):
                Point loc = getLocation();
                loc.translate(e.getX(), e.getY());
                loc.translate(-dragPoint.x, -dragPoint.y);
                setLocation(loc);
                break;
            case (RESIZE):
                int w = e.getX() - dragPoint.x + origSize.width;
                int h = e.getY() - dragPoint.y + origSize.height;
                setSize(w, h);
                break;
        }
                
    }

	/**
	 * Changes the mode back to FREE and revalidetes the screen.
	 *
	 * @param	e	Mouse event of the mouse released.
	 */
    public void mouseReleased(MouseEvent e) {
        mode = FREE;
        revalidate();
    }

    
	/**
	 * Returns the value wheter the mode -variable is FREE (True) or 
	 * something else. (False)
	 * 
	 * @ return	Wheter the mode -variable is FREE or not.
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
