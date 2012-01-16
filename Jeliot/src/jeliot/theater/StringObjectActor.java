/*
 * Created on 25.4.2007
 */
package jeliot.theater;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Insets;

/**
 * @author nmyller
 */
public class StringObjectActor extends InstanceActor {

    private String name;

    private ValueActor value;

    /** Height of the name. */
    private int nheight;

    /** Width of the name. */
    private int nwidth;

    /** Number of pixels around the divider line. */
    private int margin = 2;

    /**
     * 
     */
    public StringObjectActor(String name, ValueActor value) {
        this.name = name;
        this.value = value;
        this.value.setParent(this);
        insets = new Insets(2, 6, 4, 6);
        setDescription("String object: " + name);
    }

    public ValueActor getValue() {
        return (ValueActor) value.clone();
    }

    /**
     * 
     */
    public void calculateSize() {
        Dimension d = calculateSizeDimensions();
        setSize(d.width, d.height);
        this.value.setLocation(borderWidth, nheight + margin * 3 / 2 + 2);
        this.value.setSize(d.width - 2 * borderWidth, value.getHeight());
    }

    /**
     * @param varCount
     * @return
     */
    private Dimension calculateSizeDimensions() {

        int w = borderWidth * 2 + insets.right + insets.left
                + Math.max(value.getWidth(), nwidth);

        int h = borderWidth * 2 + insets.top + /*insets.bottom +*/ nheight
                + value.getHeight();

        return new Dimension(w, h);
    }

    /* (non-Javadoc)
     * @see jeliot.theater.Actor#paintActor(java.awt.Graphics)
     */
    public void paintActor(Graphics g) {
        int w = width;
        int h = height;
        int bw = borderWidth;

        int hgh = nheight + margin * 3 / 2;

        // fill background
        g.setColor(light == HIGHLIGHT ? lightColor : bgcolor);
        g.fillRect(bw, hgh + 2, w - 2 * bw, h - 2 * bw - hgh);
        g.setColor(Color.WHITE);
        g.fillRect(bw, bw, w - 2 * bw, hgh - bw);

        // draw border
        g.setColor(darkColor);
        for (int i = 1; i < bw; ++i) {
            g.drawRect(i, i, w - i * 2 - 1, h - i * 2 - 1);
        }

        g.setColor(borderColor);
        g.drawRect(0, 0, w - 1, h - 1);

        // draw line
        g.drawRect(1, hgh, w - 2, 1);

        // draw name
        g.setFont(font);
        g.setColor(fgcolor);
        g.drawString(name, insets.left, insets.top + (nheight * 4 / 5));

        int x = this.value.getX();
        int y = this.value.getY();
        g.translate(x, y);
        this.value.paintActor(g);
        g.translate(-x, -y);
    }

    /* (non-Javadoc)
     * @see jeliot.theater.Actor#setFont(java.awt.Font)
     */
    public void setFont(Font font) {
        super.setFont(font);
        FontMetrics fm = getFontMetrics();
        nheight = fm.getHeight();
        nwidth = fm.stringWidth(name);
    }
}
