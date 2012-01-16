package jeliot.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Insets;

import javax.swing.JComponent;
import javax.swing.SwingUtilities;
import javax.swing.text.PlainDocument;

import jeliot.mcode.Highlight;
import jeliot.util.ResourceBundles;
import jeliot.util.UserProperties;

import org.syntax.jeliot_jedit.JEditTextArea;
import org.syntax.jeliot_jedit.tokenmarker.JavaTokenMarker;

/**
 * This is the component that shows and highlights the program while
 * Jeliot is animating, called also code view.
 *
 * @author Pekka Uronen
 * @author Niko Myller
 */
public class CodePane2 extends JComponent {

    /**
     * The resource bundle for gui package.
     */
    static private UserProperties propertiesBundle = ResourceBundles
            .getGuiUserProperties();

    /**
     * Text area that is used from the JEdit project.
     */
    private JEditTextArea area;

    /**
     * Line numbering component that handles the correct
     * line numbering in the code view.
     */
    private LineNumbers ln;

    /**
     * Font for the code view area.
     */
    private Font font = new Font(propertiesBundle
            .getStringProperty("font.code_pane.family"), Font.PLAIN,
            Integer.parseInt(propertiesBundle
                    .getStringProperty("font.code_pane.size")));

    /**
     * 
     */
    private LineNumbersAdjustmentHandler lnah;

    /**
     * Constructs the CodePane -object, sets the layout and
     * adds the JScrollPane with JTextArea in the layout.
     */
    public CodePane2() {
        setLayout(new BorderLayout());

        //Special for JEditTextArea for syntax highlighting
        area = new JEditTextArea(false);
        area.setTokenMarker(new JavaTokenMarker());
        area.getPainter().setFont(font);
        area.getDocument().getDocumentProperties().put(
                PlainDocument.tabSizeAttribute, new Integer(3));
        area.setHorizontalOffsetForScrollBar(5);
        area.setHorizontalOffset(5);
        ln = new LineNumbers(font, new Insets(1, 0, 0, 0));
        area.addToLeft(ln);
        lnah = new LineNumbersAdjustmentHandler(area, ln);
        area.addAdjustListernerForVertical(lnah);
        add("Center", area);
        area
                .getPainter()
                .setBackground(
                        new Color(
                                Integer
                                        .decode(
                                                propertiesBundle
                                                        .getStringProperty("color.code_pane.background"))
                                        .intValue()));
        area.getPainter().setLineHighlightEnabled(false);
        /*
         area.getPainter().setSelectionColor(
         new Color(
         Integer
         .decode(bundle.getString("color.code_pane.selection"))
         .intValue()));
         
         area.getPainter().setSelectedTextColor(
         new Color(
         Integer
         .decode(bundle.getString("color.code_pane.selection.text"))
         .intValue()));
         */
        lnah.adjustmentValueChanged(null);
        area.setEditable(false);
        area.setCaretReallyVisible(false);
        area.getPainter().setBracketHighlightEnabled(false);
        area.revalidate();
    }

    /**
     * Sets the given program code <code>String text</code> into
     * the JTextArea area.
     *
     * @param text The program code to be set in the JTextArea area.
     */
    public void installProgram(String text) {
        area.setText(text);
        area.setCaretPosition(0);
    }

    /**
     * Counts how many lines the current program code is taking.
     * @param text the program code
     * @return the number of lines the given program code takes.
     */
    public int calculateLines(String text) {
        int lines = 1;
        int index = text.indexOf("\n");
        while (index >= 0) {
            lines++;
            index++;
            index = text.indexOf("\n", index);
        }
        return lines;
    }

    /**
     * Method highlights the specified Statement area
     * by selecting it.
     * 
     * @param h contains the area that should be highlighted.
     */
    public void highlightStatement(Highlight h) {

        int l = 0, r = 0;

        try {
            if (h.getBeginLine() > 0) {
                l = area.getLineStartOffset(h.getBeginLine() - 1);
            }
            l += h.getBeginColumn();

            int nextLineStart = -1;
            if (h.getEndLine() > 0) {
                r = area.getLineStartOffset(h.getEndLine() - 1);
                nextLineStart = area.getLineStartOffset(h.getEndLine());
            }
            r += h.getEndColumn();
            if (nextLineStart > 0) {
                r = Math.min(r, nextLineStart);
            }
        } catch (Exception e) {
        }

        final int left = l - 1;
        final int right = r;

        //Tracker.writeToFileFromCodeView("Highlight", left, right, TrackerClock.currentTimeMillis(), -1);

        Runnable updateAComponent = new Runnable() {

            public void run() {
                //area.requestFocusInWindow();
                area.setCaretPosition(left + 1);
                if (left >= 0) {
                    if (left != 0 && left == right) {
                        area.select(left, right + 1);
                    } else {
                        area.select(left, right);
                    }
                }
            }
        };
        SwingUtilities.invokeLater(updateAComponent);
    }

    /**
     * @return
     */
    public JEditTextArea getTextArea() {
        return area;
    }

    /**
     * @param line
     */
    public void highlightLineNumber(int line) {
        ln.setHighlightedLine(line);
    }

    /**
     * 
     */
    public void setFont(Font font) {
        super.setFont(font);
        ln.setFont(font);
        this.font = font;
        getTextArea().getPainter().setFont(font);
        propertiesBundle.setFontProperty("font.code_pane", font);
    }

    /**
     * 
     */
    public void clearHighlights() {
        highlightStatement(new Highlight(0, 0, 0, 0));
    }

    /**
     * 
     */
    public boolean requestFocusInWindow() {
        //requestFocusInWindow();
        return this.area.requestFocusInWindow();
    }
}