package jeliot.gui;

import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;

import javax.swing.SwingUtilities;

import org.syntax.jeliot_jedit.JEditTextArea;

/**
 * Handles line number validations once the JEditTextArea is scrolled.
 * 
 * @author nmyller
 */
class LineNumbersAdjustmentHandler implements AdjustmentListener {

	/**
	 * 
	 */
    private JEditTextArea jedit;
    
    /**
     * 
     */
    private LineNumbers ln;
    
    /**
     * 
     * @param jedit
     * @param ln
     */
    public LineNumbersAdjustmentHandler(JEditTextArea jedit, LineNumbers ln) {
        this.jedit = jedit;
        this.ln = ln;
    }
    
    /* (non-Javadoc)
     * @see java.awt.event.AdjustmentListener#adjustmentValueChanged(java.awt.event.AdjustmentEvent)
     */
    public void adjustmentValueChanged(final AdjustmentEvent evt) {
        if (!jedit.isScrollBarsInitialized()) return;

        // If this is not done, mousePressed events accumilate
        // and the result is that scrolling doesn't stop after
        // the mouse is released
        SwingUtilities.invokeLater(new Runnable() {

            public void run() {
                ln.setLineNumbersByFirstLine(jedit.getVerticalScrollBar().getValue()+1,jedit.getHeight());
                ln.repaint();
            }
        });
    }
}