package jeliot.theater;

import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.util.ResourceBundle;

import jeliot.util.ResourceBundles;

/**
 * This actor draws the dashed lines and titles to separate explicitly the
 * theater (animation frame) into four areas: constant area, method area,
 * object and array area and expression evaluation area.
 * 
 * @author Niko Myller
 */
public class LinesAndText extends Actor {

    /**
     * The resource bundle for theater package
     */
    static private ResourceBundle bundle = ResourceBundles
            .getTheaterMessageResourceBundle();

    //DOC: Document!                                      

    /**
     *
     */
    private Theater theatre;

    /**
     *
     */
    private String constantArea = bundle.getString("string.constant_area");

    /**
     *
     */
    private String methodArea = bundle.getString("string.method_area");

    /**
     *
     */
    private String instanceArea = bundle.getString("string.instance_area");

    /**
     *
     */
    private String evaluationArea = bundle.getString("string.evaluation_area");

    /**
     *
     */
    private int constantAreaWidth;

    /**
     *
     */
    private int methodAreaWidth;

    /**
     *
     */
    private int instanceAreaWidth;

    /**
     *
     */
    private int evaluationAreaWidth;

    /**
     * 
     */
    public LinesAndText() {
        FontMetrics fm = dummy.getFontMetrics(font);
        constantAreaWidth = fm.stringWidth(constantArea);
        methodAreaWidth = fm.stringWidth(methodArea);
        instanceAreaWidth = fm.stringWidth(instanceArea);
        evaluationAreaWidth = fm.stringWidth(evaluationArea);
    }

    /**
     * @param t
     * @param tm
     */
    public LinesAndText(Theater t) {
        super();
        this.theatre = t;
    }

    /**
     * @param t
     */
    public void setTheatre(Theater t) {
        this.theatre = t;
    }

    /**
     * Draws the lines separating different areas
     * and writes texts on them.
     * @see jeliot.theater.Actor#paintActor(java.awt.Graphics)
     */
    public void paintActor(Graphics g) {
        Dimension d = theatre.getSize();
        int w = d.width;
        int h = d.height;
        int methodX = TheaterManager.getScratchPositionX();
        int constantY = TheaterManager.getConstantBoxPositionY();
        int instanceY = TheaterManager.getMinInstanceY();
        int instanceX = TheaterManager.getMinInstanceX();

        /*
         if (constantY < instanceY) {
         instanceY = constantY;
         }
         */

        if (methodX < instanceX) {
            instanceX = methodX;
        }

        g.setColor(bgcolor);

        for (int i = instanceY - 10; i > 0; i -= 40) {
            g.drawLine(methodX - 5, i, methodX - 5, i - 20);
            g.drawLine(methodX - 6, i, methodX - 6, i - 20);
        }

        for (int i = instanceY + 10; i < h; i += 40) {
            g.drawLine(instanceX - 5, i, instanceX - 5, i + 20);
            g.drawLine(instanceX - 6, i, instanceX - 6, i + 20);
        }

        for (int i = instanceX - 10; i > 0; i -= 40) {
            g.drawLine(i, constantY - 10, i - 20, constantY - 10);
            g.drawLine(i, constantY - 11, i - 20, constantY - 11);
        }

        for (int i = instanceX + 10; i < w; i += 40) {
            g.drawLine(i, instanceY - 10, i + 20, instanceY - 10);
            g.drawLine(i, instanceY - 11, i + 20, instanceY - 11);
        }

        g.setColor(fgcolor);
        g.setFont(font);
        g.drawString(constantArea, 40 /*(instanceX - constantAreaWidth) / 2 */,
                constantY - 12);
        g.drawString(methodArea, 40 /*(methodX - methodAreaWidth) / 2*/, 15);
        g.drawString(instanceArea, instanceX + 40
                /* + (w - instanceX - instanceAreaWidth) / 2 */, instanceY - 12);
        g.drawString(evaluationArea, methodX + 40
                /* + (w - methodX - evaluationAreaWidth) / 2 */, 15);

    }
}