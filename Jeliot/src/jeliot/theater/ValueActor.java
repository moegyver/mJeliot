package jeliot.theater;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;

import jeliot.util.ResourceBundles;
import jeliot.util.UserProperties;

/**
 * <code>ValueActor</code> is an actor that represents graphically
 * the language construct <code>Value</code>. The <code>Value</code>'s type is
 * represented by the colors of the ValueActor and the <code>Value</code>'s
 * value is printed out as the String representation of the value.
 * 
 * @author Pekka Uronen
 * @author Niko Myller
 * 
 * @see jeliot.lang.Value
 * @see jeliot.theater.ImageValueActor
 * @see jeliot.theater.ActorFactory
 */
public class ValueActor extends Actor {

   /**
     * The resource bundle for theater package
     */
    static private UserProperties propertiesBundle = ResourceBundles.getTheaterUserProperties();

//  DOC: Document!
    /**
	 *
	 */
	private String valstr;

    /**
	 *
	 */
	private int namex;
    
    /**
	 *
	 */
	private int namey;
    
    /**
	 *
	 */
	private int margin = 2;

	/**
	 * type contains the type of the value
	 */
	private String type;
	
    /**
	 * 
	 */
	public ValueActor() {
        setFont(new Font(propertiesBundle.getStringProperty("font.value_actor.family"),
        Font.BOLD,
        Integer.parseInt(propertiesBundle.getStringProperty("font.value_actor.size"))));
    }

    /**
	 * @param valstr
	 */
	public void setLabel(String valstr) {
        this.valstr = valstr;
    }

    /**
	 * @return
	 */
	public String getLabel() {
        return this.valstr;
    }

    /* (non-Javadoc)
	 * @see jeliot.theater.Actor#paintActor(java.awt.Graphics)
	 */
	public void paintActor(Graphics g) {
        int w = width;
        int h = height;
        int bw = borderWidth;

        // fill background
        Color bcol = null;
        Color lcol = null;
        switch (light) {
            case (HIGHLIGHT):
                bcol = lightColor;
                lcol = bcol;
                break;
            case (NORMAL):
                bcol = bgcolor;
                lcol = lightColor;
                break;
            case (SHADED):
                bcol = darkColor;
                lcol = bgcolor;
                break;
        }

        g.setColor(bcol);
        g.fillRect(bw, bw, w - 2 * bw, h - 2 * bw);

        // draw border
        g.setColor(fgcolor);
        g.drawRect(0, 0, w-1, h-1);
        g.setColor(darkColor);
        g.drawLine(1, h-2, w-2, h-2);
        g.drawLine(w-2, 2, w-2, h-2);
        g.setColor(lcol);
        g.drawLine(1, 1, w-2, 1);
        g.drawLine(1, 1, 1, h-2);

        paintValue(g);
    }

    /**
	 * @param g
	 */
	public void paintValue(Graphics g) {
        g.setFont(getFont());
        g.setColor(fgcolor);
        g.drawString(valstr, namex, namey-1);
        /*
         * This works if for some reason valstr would be null
         * which never should be a case but if there is a bug that
         * makes the situation so then this can help in the debugging.
         */
        //if (valstr != null) {
        //    g.drawString(valstr, namex, namey-1);
        //}
    }

    /* (non-Javadoc)
	 * @see jeliot.theater.Actor#setBounds(int, int, int, int)
	 */
	public void setBounds(int x, int y, int w, int h) {
        int oldw = getWidth();
        int oldh = getHeight();
        super.setBounds(x, y, w, h);

        if (w != oldw || h != oldh) {
            calcLabelPosition();
        }
    }

    /**
	 * 
	 */
	protected void calcLabelPosition() {
        FontMetrics fm = getFontMetrics();
        int sw = fm.stringWidth(valstr);
        int sh = fm.getHeight();

        namex = (getWidth()-sw)/2;
        namey = (getHeight()+sh)/2-2;

    }

    /* (non-Javadoc)
	 * @see jeliot.theater.Actor#calculateSize()
	 */
	public void calculateSize() {
        setSize(getPreferredSize());
        calcLabelPosition();
    }

    /**
	 * @return
	 */
	public Dimension getPreferredSize() {
        FontMetrics fm = getFontMetrics();
        int sw = fm.stringWidth(valstr);
        int sh = fm.getHeight();
        int h = sh+margin;
        int w = Math.max(h, sw+margin+6);
        return new Dimension(w, h);
    }
	/**
	 * @return Returns the type.
	 */
	public String getType() {
		return type;
	}
	/**
	 * @param type The type to set.
	 */
	public void setType(String type) {
		this.type = type;
	}
    
    public String toString() {
        return "(" + type + ") " + valstr;
    }

    public String getValstr() {
        return valstr;
    }

    public void setValstr(String valstr) {
        this.valstr = valstr;
    }
}
