package jeliot.calltree;

import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.geom.Rectangle2D;

import jeliot.util.ResourceBundles;
import jeliot.util.UserProperties;

/**
 * @author Niko Myller
 */
public class BoundingBoxCalculator extends EulerTour {

    protected static final UserProperties propertiesBundle = ResourceBundles
            .getCallTreeUserProperties();

    /**
     * size of the grid squares (default 50)
     */
    protected int offset = Integer.parseInt(propertiesBundle
            .getStringProperty("offset"));

    /**
     * a running total width of the explored tree drawing
     */
    protected int width = 0;

    /**
     * a running total depth of the explored tree drawing
     */
    protected int depth = 0;

    /**
     * the graphics object where the tree will be drawn
     */
    protected Graphics g;

    /**
     * the fontMetrics object for g
     */
    protected FontMetrics fm;

    /**
     * the distance to separate bounding boxes (default 15)
     */
    protected int pad = Integer.parseInt(propertiesBundle
            .getStringProperty("pad"));

    /**
     * Comment for <code>maxWidth</code>
     */
    protected int maxWidth = 0;

    /**
     * Comment for <code>maxHeight</code>
     */
    protected int maxHeight = 0;

    /**
     * @return
     */
    public int getMaxWidth() {
        return maxWidth;
    }

    /**
     * @return
     */
    public int getMaxHeight() {
        return maxHeight;
    }

    /**
     * When a node is visited first in the Euler Tour we know the upper-left hand
     * corner of its bounding box, which we store as decorations.
     * 
     * @param pos
     */
    protected void visitFirstTime(TreeNode pos) {
        pos.setProperty("y", new Integer(depth));
        pos.setProperty("x", new Integer(width));
        depth += offset;

        if (maxHeight < depth) {
            maxHeight = depth;
        }
        if (maxWidth < width) {
            maxWidth = width;
        }
    }

    /**
     * When a node is visited last in the Euler Tour we know the width
     * of its bounding box, which we store as a decoration.
     * 
     * @param pos
     */
    protected void visitLastTime(TreeNode pos) {
        int textWidth = textWidth(pos);
        int shift = 0;
        int x = ((Integer) pos.getProperty("x")).intValue();
        int boxWidth = width - x;

        if (textWidth > boxWidth) {
            int delta = textWidth - boxWidth;
            boxWidth = textWidth;
            width += delta;
            shift = delta / 2;
        }

        pos.setProperty("width", new Integer(boxWidth));
        // The distance that the children's bounding boxes need to
        // be shifted in the drawing.
        pos.setProperty("shift", new Integer(shift));

        if (maxHeight < depth) {
            maxHeight = depth;
        }
        if (maxWidth < width) {
            maxWidth = width;
        }

        depth -= offset;
    }

    /**
     * Sets all attributes for an external node
     * 
     * @param pos
     */
    protected void visitExternal(TreeNode pos) {
        pos.setProperty("y", new Integer(depth));
        pos.setProperty("x", new Integer(width));
        int textWidth = textWidth(pos);
        width += textWidth;
        pos.setProperty("width", new Integer(textWidth));
        pos.setProperty("shift", new Integer(0));

        if (maxHeight < depth) {
            maxHeight = depth;
        }
        if (maxWidth < width) {
            maxWidth = width;
        }
    }

    /**
     * 
     * @param gg
     */
    public BoundingBoxCalculator(Graphics gg) {
        g = gg;
        fm = g.getFontMetrics();
    }

    /**
     * Calculates the width of the drawing of the node label.  Stores
     * the attributes needed to calculate the exact position to draw the
     * label.
     * 
     * @param pos
     * @return
     */
    protected int textWidth(TreeNode pos) {
        String str = "";
        String str2 = "";
        String element = (String) pos.getProperty("element");
        if (element != null) {
            str = element.toString();
        }

        if (pos.getProperty("return") != null) {
            Object o = pos.getProperty("return");
            if (o != null && o != Util.nullObject) {
                str2 = "returned " + ((String) o).toString();
            } else {
                str2 = "returned no value";
            }
        }

        Rectangle2D bounds = fm.getStringBounds(str, g);
        Rectangle2D bounds2 = fm.getStringBounds(str2, g);
        bounds.setRect(bounds.getX(), bounds.getY(), Math.max(
                bounds.getWidth(), bounds2.getWidth()), bounds.getHeight()
                + bounds2.getHeight());
        pos.setProperty("bounds", bounds);
        pos.setProperty("ascent", new Integer(fm.getMaxAscent()));
        pos.setProperty("leading", new Integer(fm.getLeading()));
        pos.setProperty("descent", new Integer(fm.getMaxDescent()));
        return (int) bounds.getWidth() + pad;
    }
}
