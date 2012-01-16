package jeliot.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;

import javax.swing.JComponent;

import jeliot.util.ResourceBundles;
import jeliot.util.UserProperties;

/**
 * The LineNumbers component is used to show the line numbers in the scroll
 * panes left side in the code view and code editor.
 * 
 * @author Niko Myller
 * @see jeliot.gui.CodePane2
 * @see jelio.gui.CodeEditor2
 */
public class LineNumbers extends JComponent {

    /**
     * The resource bundle for gui package
     */
    static private UserProperties propertiesBundle = ResourceBundles.getGuiUserProperties();
    
    /**
     * The width of the component.
     */
    private int size = 35;

    /**
     * The font for this component.
     */
    private Font font;

    /**
     * The ascent of the font.
     */
    private int ascent;

    /**
     * The increment between two lines.
     */
    private int increment;

    /**
     * insets in the component.
     */
    private Insets insets;

    /**
     * 
     */
    private int startLine = 0;

    /**
     * 
     */
    private Color normalColor = new Color(
			Integer
			.decode(propertiesBundle.getStringProperty("color.line_numbers.normal"))
			.intValue());
    
    /**
     * 
     */
    private Color highlightColor = new Color(
			Integer
			.decode(propertiesBundle.getStringProperty("color.line_numbers.highlight"))
			.intValue());
    
    /**
     * 
     */
    private Color backGroundColor = new Color(
			Integer
			.decode(propertiesBundle.getStringProperty("color.line_numbers.background"))
			.intValue());
			
    /**
     * Highlights the line during drawing if
     * it is set to some line
     */
    private int highlightedLine = -1;
    
    /**
     * Sets the font and the insets and the determines the size increment and
     * ascent from the font's font metrics.
     * 
     * @param font
     *            the font to be used in the component
     * @param insets
     *            the insets for the layout.
     */
    public LineNumbers(Font font, Insets insets) {
        this.font = font;
        this.insets = insets;
        calculateFontMetrics();
    }

    public void calculateFontMetrics() {
        FontMetrics fm = getFontMetrics(font);
        size = fm.stringWidth("000") + 6;
        increment = fm.getHeight();
        ascent = fm.getAscent();
        setPreferredHeight(400);
    }
    
    /**
     * Sets the preferred height of the component.
     * 
     * @param ph
     */
    public void setPreferredHeight(int ph) {
        setPreferredSize(new Dimension(size, ph));
        revalidate();
    }

    /**
     * sets the height by the given number of lines that should be shown.
     * 
     * @param lines
     */
    public void setHeightByLines(int lines) {
        int height = insets.top + ascent + (lines * increment) + insets.bottom;
        //System.out.println("CodePane: " + height);
        setPreferredSize(new Dimension(size, height));
        revalidate();
    }

    /*
     * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
     */
    public void paintComponent(Graphics g) {
        
        Rectangle drawHere = g.getClipBounds();
        int lineNumber = 1;
        
        if (startLine != 0) {
            lineNumber = startLine;
        } else {
            lineNumber = (int) Math.floor(drawHere.y / increment) + 1;
        }

        g.setColor(backGroundColor);
        g.fillRect(drawHere.x, drawHere.y, drawHere.width, drawHere.height);

        // Do the ruler labels in the font that's black.
        g.setFont(font);
        g.setColor(normalColor);

        // Some vars we need.
        int end = 0;
        int start = 0;

        start = (drawHere.y / increment) * increment;

        end = (((drawHere.y + drawHere.height) / increment) + 1) * increment;

        start += insets.top + ascent;
        end += insets.top + ascent;

        //labels
        for (int i = start; i < end; i += increment) {
        	if (lineNumber == highlightedLine) {
        		g.setColor(highlightColor);
        		g.fillRect(0, i - ascent, size, increment);
        		g.setColor(normalColor);        		
        	}
        	g.drawString(Integer.toString(lineNumber), 3, i);
            lineNumber++;
        }
    }

    /**
     * 
     * @param firstLine
     * @param height
     */
    public void setLineNumbersByFirstLine(int firstLine, int height) {
        startLine = firstLine;
        //System.out.println("CodePane: " + height);
        setPreferredSize(new Dimension(size, height));
        revalidate();
    }
    
    /**
     * 
     * @param line the number of the line to be highlighted.
     */
    public void setHighlightedLine(int line) {
    	highlightedLine = line;
    	repaint();
    }
    
    /**
     * @param font the font to be set
     */
    public void setFont(Font font) {
        super.setFont(font);
        this.font = font;
        calculateFontMetrics();        
    }
}