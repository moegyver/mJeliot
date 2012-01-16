package jeliot.calltree;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.JComponent;
import javax.swing.JScrollPane;

import jeliot.util.ResourceBundles;
import jeliot.util.UserProperties;

/**
 * @author Niko Myller
 */
public class TreeDraw extends JComponent {

    /**
     * 
     */
    protected static final UserProperties propertiesBundle = ResourceBundles
            .getCallTreeUserProperties();

    /**
     * 
     */
    private TreeBuilder builder = new TreeBuilder();

    /**
     * 
     */
    private Vector trees = new Vector();

    /**
     * 
     */
    private Tree tree;

    /**
     * 
     */
    private JScrollPane jsp;

    /**
     * Font to be used in the call tree
     */
    private static final Font FONT = new Font(propertiesBundle
            .getStringProperty("font.calltree"), Font.BOLD, Integer
            .parseInt(propertiesBundle.getStringProperty("font.calltree.size")));

    /**
     * 
     *
     */
    public TreeDraw() {
        //build the tree
        initialize();
        jsp = new JScrollPane(TreeDraw.this);
        jsp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        jsp
                .setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);

    }

    /**
     * 
     */
    public void initialize() {
        tree = builder.buildTree();
        this.trees.add(tree);
        repaint();
    }

    /**
     * 
     * @return
     */
    public JComponent getComponent() {
        return jsp;
    }

    /**
     * The paint method draws the tree. 
     * There are 2 steps: 
     * <UL>
     * <LI>The BoundingBoxCalculator determines the width of each subtree.</LI>
     * <LI>The TreeDrawer calculates the exact locations for labels and edges and draws the tree.</LI>
     * </UL>
     */
    public void paint(Graphics g) {
        int xOffset = 0;
        for (Iterator i = trees.iterator(); i.hasNext();) {
            Tree tre = (Tree) i.next();
            g.setFont(FONT);
            BoundingBoxCalculator calc = new BoundingBoxCalculator(g /*getGraphics()*/);
            calc.execute(tre);
            TreeDrawer drawer = new TreeDrawer(g);
            drawer.setXOffset(drawer.getXOffset() + xOffset);
            xOffset += calc.getMaxWidth() + 10;
            //System.out.println(xOffset);
            Dimension area = new Dimension(calc.getMaxWidth()
                    + drawer.getXOffset() + 10, calc.getMaxHeight()
                    + drawer.getYOffset() + 30);
            setPreferredSize(area);
            revalidate();
            drawer.execute(tre);
        }
    }

    /**
     * 
     * @param call
     */
    public void insertMethodCall(String call) {
        if (this.tree == null) {
            initialize();
        }
        builder.insertNode(call);
        repaint();

    }

    /**
     * 
     * @param returnValue
     */
    public void returnMethodCall(String returnValue) {
        builder.returnNode(returnValue);
        if (builder.currentPosition == null) {
            this.tree = null;
        }
        repaint();
    }

    public void cleanUp() {
        this.trees.clear();
        tree = builder.buildTree();
        this.trees.add(tree);
        repaint();
    }
}