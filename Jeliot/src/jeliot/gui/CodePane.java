package jeliot.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Insets;

import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

import jeliot.mcode.Highlight;
import jeliot.util.ResourceBundles;
import jeliot.util.UserProperties;

/**
 * NOT CURRENTLY USED IN JELIOT 3! CAN CONTAIN OLD AND NOT WORKING CODE!
 * This is the component that shows and highlights the program while
 * Jeliot is animating, called also code view.
 *
 * @deprecated
 * @author Pekka Uronen
 * @author Niko Myller
 */
public class CodePane extends JComponent {

	/**
	 * The resource bundle for gui package.
	 */
	static private UserProperties propertiesBundle =
		ResourceBundles.getGuiUserProperties();

	/**
	 * Line numbering component that handles the correct
     * line numbering in the code view.
	 */
	private LineNumbers nb;

	/**
	 * Font for the code view area.
	 */
	private Font font =
		new Font(
			propertiesBundle.getStringProperty("font.code_pane.family"),
			Font.PLAIN,
			Integer.parseInt(propertiesBundle.getStringProperty("font.code_pane.size")));
	/**
	 * Insets for the text. Used for the layout.
	 */
	private Insets insets = new Insets(5, 5, 5, 5);

	/**
	 * Pane that handles the scrolling of the code view or the
     * TextArea. 
	 */
	private JScrollPane jsp;

	/**
	 * The text area where the program code is shown and
     * highlighted.
	 */
	JTextArea area = new JTextArea();
	{
		area.setMargin(insets);
		area.setFont(font);
		area.setTabSize(4);
		area.setBackground(
			new Color(
				Integer
					.decode(propertiesBundle.getStringProperty("color.code_pane.background"))
					.intValue()));
		area.setSelectionColor(
			new Color(
				Integer
					.decode(propertiesBundle.getStringProperty("color.code_pane.selection"))
					.intValue()));
		area.setSelectedTextColor(
			new Color(
				Integer
					.decode(propertiesBundle.getStringProperty("color.code_pane.selection.text"))
					.intValue()));
		area.setEditable(false);
	}

	/**
	 * Constructs the CodePane -object, sets the layout and
	 * adds the JScrollPane with JTextArea in the layout.
	 */
	public CodePane() {
		setLayout(new BorderLayout());
		add("Center", makeScrollPane());
		validateScrollPane();
	}

	/**
	 * Creates the ScrollPane that shows the line
	 * numbering on the left side and the text area
	 * in the center. 
	 * @return the set up scrollpane.
	 */
	public JComponent makeScrollPane() {
		jsp = new JScrollPane(area);
		nb = new LineNumbers(font, insets);
		jsp.setRowHeaderView(nb);
		validateScrollPane();
		return jsp;
	}

	/**
	 * Sets the given program code String text into
	 * the JTextArea area.
	 *
	 * @param text The program code to be set in the
	 * JTextArea area.
	 */
	public void installProgram(String text) {
		area.setText(text);
		validateScrollPane();
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
	 * Validates the scroll pane by setting the correct
     * number of lines to the LineNumbers component. 
	 */
	public void validateScrollPane() {
		final int lines = calculateLines(area.getText());

		if (nb != null) {
			Runnable updateAComponent = new Runnable() {
				public void run() {
					nb.setHeightByLines(lines);
				}
			};
			SwingUtilities.invokeLater(updateAComponent);
		}
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

			if (h.getEndLine() > 0) {
				r = area.getLineStartOffset(h.getEndLine() - 1);
			}
			r += h.getEndColumn();
		} catch (Exception e) {}

		final int left = l - 1;
		final int right = r;

		Runnable updateAComponent = new Runnable() {
			public void run() {
				//area.requestFocus();
				area.setCaretPosition(left + 1);
				if (left != 0 && left == right) {
					area.select(left, right + 1);
				} else {
					area.select(left, right);
				}
			}
		};
		SwingUtilities.invokeLater(updateAComponent);
	}

}
