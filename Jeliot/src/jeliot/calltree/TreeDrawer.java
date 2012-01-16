package jeliot.calltree;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.geom.Rectangle2D;

import jeliot.util.ResourceBundles;
import jeliot.util.UserProperties;

/**
 * @author Niko Myller
 */
public class TreeDrawer extends EulerTour {

	protected static final UserProperties propertiesBundle = ResourceBundles.getCallTreeUserProperties();
	
    /**
     * Y offset from (0,0) (default 40)
     */
    protected int yOffset = Integer.parseInt(propertiesBundle.getStringProperty("y_offset"));

    /**
     * X offset from (0,0) (default 20)
     */
    protected int xOffset = Integer.parseInt(propertiesBundle.getStringProperty("x_offset"));

    /**
     * where to draw the tree
     */
    protected Graphics g;

    /**
     * fill color
     */
    private static final Color background = new Color(Integer.decode(propertiesBundle.getStringProperty("color.calltree.node.background")).intValue());

    /**
     * Edge color
     */
    private static final Color edgeColor = new Color(Integer.decode(propertiesBundle.getStringProperty("color.calltree.edge")).intValue());

    /**
     * Returned node's color
     */
	private static final Color returnedNodeColor = new Color(Integer.decode(propertiesBundle.getStringProperty("color.calltree.node_returned")).intValue());

	/**
	 * Current node's color
	 */
	private static final Color currentNodeColor = new Color(Integer.decode(propertiesBundle.getStringProperty("color.calltree.node_current")).intValue());
	
	/**
	 * Active node's color if not current
	 */
	private static final Color activeNodeColor = new Color(Integer.decode(propertiesBundle.getStringProperty("color.calltree.node_active")).intValue());
    
    /**
     * a running total to shift bounding boxes.  The shift
     * distance is the sum of the shifts stored at ancestors.
     * 
     */
    protected int totalShift;

    /**
     * 
     * @param gg
     */
    public TreeDrawer(Graphics gg) {
        g = gg;
    }

    /**
     * 
     * @return
     */
    public int getYOffset() {
        return yOffset;
    }

    /**
     * 
     * @return
     */
    public int getXOffset() {
        return xOffset;
    }

    /**
     * When visiting a node for the first time we shift x by totalShift
     * @param pos
     */
    protected void visitFirstTime(TreeNode pos) {
        if (pos.getProperty("x") != null) {
            int x = ((Integer) pos.getProperty("x")).intValue();
            int shift = ((Integer) pos.getProperty("shift")).intValue();
            pos.setProperty("x", new Integer(x + totalShift));
            totalShift += shift;
        }
    }

    /**
     * When visiting a node for the last time we draw the node.
     * @param pos
     */
    protected void visitLastTime(TreeNode pos) {
        int shift = ((Integer) pos.getProperty("shift")).intValue();
        if (!pos.isRoot()) {
            //Draw the edge to the parent
            g.setColor(edgeColor);
            g.drawLine(xPos(pos), yPos(pos), xPos(pos.getParent()), yPos(pos.getParent()));
        }

        Color strColor;

        if (pos.getProperty("return") != null) {
            strColor = returnedNodeColor;
        } else if (pos.getProperty("current") != null) {
            strColor = currentNodeColor;
        } else {
            strColor = activeNodeColor;
        }

        drawString(pos, strColor);
        totalShift -= shift;
        cleanup(pos);
    }

    /**
     * External nodes are drawn in the same manner as internal nodes
     * @param pos
     */
    protected void visitExternal(TreeNode pos) {
        visitFirstTime(pos);
        visitLastTime(pos);
    }

    /**
     * Draw the string at its proper location.
     * @param pos
     * @param strColor
     */
    private void drawString(TreeNode pos, Color strColor) {

        String str = "";
        String str2 = "";

        if (pos.getProperty("element") != null) {
            str = pos.getProperty("element").toString() + "\n ";
        }

        if (pos.getProperty("return") != null) {
            Object o = pos.getProperty("return");
            if (o != null && o != Util.nullObject) {
                str2 = "returned " + ((String) o).toString();
            } else {
                str2 = "returned no value";
            }
        }

        if (pos.getProperty("element") != null) {
            int ascent = ((Integer) pos.getProperty("ascent")).intValue();
            int descent = ((Integer) pos.getProperty("descent")).intValue();
            int leading = ((Integer) pos.getProperty("leading")).intValue();
            Rectangle2D bounds = ((Rectangle2D) pos.getProperty("bounds"));
            //int height = (int) bounds.getHeight();
            int width = (int) bounds.getWidth();
            int x = xPos(pos) - width / 2;
            int y = yPos(pos) - ascent / 2;
            g.setColor(background);
            g.fillRect(x - 4, y, width + 6, leading + ascent + descent + leading + ascent
                            + descent);

            g.setColor(strColor);
            g.drawRect(x - 4, y, width + 6, leading + ascent + descent + leading + ascent
                            + descent);

            g.setColor(strColor);
            y += leading + ascent;
            g.drawString(str, x, y);
            y += descent + leading + ascent;
            g.drawString(str2, x, y);
        }
    }

    /**
     * 
     * @param p
     * @return
     */
    private int xPos(TreeNode p) {
        int x = ((Integer) p.getProperty("x")).intValue();
        int width = ((Integer) p.getProperty("width")).intValue();
        return x + width / 2 + xOffset;
    }

    /**
     * 
     * @param p
     * @return
     */
    private int yPos(TreeNode p) {
        return ((Integer) p.getProperty("y")).intValue() + yOffset;
    }

    /**
     * 
     * @param p
     */
    private void cleanup(TreeNode p) {
        p.destroyProperty("x");
        p.destroyProperty("y");
        p.destroyProperty("shift");
        p.destroyProperty("ascent");
        p.destroyProperty("descent");
        p.destroyProperty("bounds");
    }

    public void setXOffset(int xoffset) {
        xOffset = xoffset;
    }

    public void setYOffset(int yoffset) {
        yOffset = yoffset;
    }
}