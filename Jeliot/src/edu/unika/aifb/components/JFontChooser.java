package edu.unika.aifb.components;

/*
 * Copyright (c) 2004 Roland Küstermann. All Rights Reserved.
 */

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import edu.unika.aifb.utils.Resources;

public class JFontChooser extends JComponent {

    private Resources resources = new Resources(getClass().getName().replace('.', '/')
            + "_messages");

    public static void main(String[] args) {
        JFontChooser.showDialog(null, Font.getFont("Arial"));
    }

    /**
     * Shows the dialog, with specified parent and title.
     */

    public static Font showDialog(Component parent, Font defaultFont) {
        final JFontChooser pane = new JFontChooser();

        FontTracker ok = new FontTracker(pane);

        JDialog dialog = pane.createDialog(parent, pane.resources
                .getResourceString("fontchooser_Dialog_Title"), true, // modal
                pane, ok, null // No cancel listener
                );
        dialog.addWindowListener(new JFontChooserDialog.Closer());
        dialog.addComponentListener(new JFontChooserDialog.DisposeOnClose());

        pane.setSelectedFont(defaultFont);

        dialog.setVisible(true); // Blocks until user dismisses dialog

        return ok.getSelectedFont();
    }

    public static Font showDialog(Component parent, String title) {
        final JFontChooser pane = new JFontChooser();

        FontTracker ok = new FontTracker(pane);

        JDialog dialog = pane.createDialog(parent, title, true, // modal
                pane, ok, null // No cancel listener
                );
        dialog.addWindowListener(new JFontChooserDialog.Closer());
        dialog.addComponentListener(new JFontChooserDialog.DisposeOnClose());

        dialog.setVisible(true); // Blocks until user dismisses dialog

        return ok.getSelectedFont();
    }

    /**
     * Creates and returns a new dialog containing the specified edu.unika.aifb.components.JFontChooser pane,
     * together with "BTN_OK" and "Cancel" buttons.
     * If the "BTN_OK" or "Cancel" buttons are pressed, the dialog is automatically
     * hidden (but not disposed).
     */
    public JDialog createDialog(Component parent, String title, boolean modal,
            JFontChooser chooserPane, ActionListener okListener, ActionListener cancelListener) {
        return new JFontChooserDialog(parent, title, modal, chooserPane, okListener,
                cancelListener, resources);
    }

    /**
     * Constructor: Creates a font chooser pane, which consists of an input
     * pane with three Lists for Font name, style and size, and a preview pane
     * which shows some text in the selected font.
     */
    public JFontChooser() {
        setLayout(new BorderLayout());

        PreviewPanel previewPane = new PreviewPanel();
        m_inputPane = new InputPanel(previewPane); // ListSelectionListener
        add(m_inputPane, BorderLayout.CENTER);
        add(previewPane, BorderLayout.SOUTH);
    }

    /**
     * Returns the current font from the font chooser.
     * (Delegates the work to the input pane's getFont() method.)
     */
    public Font getSelectedFont() {
        return m_inputPane.getSelectedFont();
    }

    public void setSelectedFont(Font f) {
        m_inputPane.setSelectedFont(f);
    }

    /////// Private data /////
    private InputPanel m_inputPane;

    /////// Inner classes /////

    /**
     * Class to present the user with a set of three lists, one each
     * for font name, style and size.
     */
    class InputPanel extends JPanel {

        /**
         * Constructor: Creates an instance of InputPanel.
         *
         * @param listener a list selection listener which will listen for
         *                 changes in the state of the lists.
         */
        public InputPanel(ListSelectionListener listener) {
            setLayout(new BorderLayout());

            // Font name
            Box nameBox = Box.createVerticalBox();
            nameBox.add(Box.createVerticalStrut(10));
            JLabel fontNameLabel = new JLabel(resources.getResourceString("fontchooser_Font_Name")
                    + ":");
            nameBox.add(fontNameLabel);
            if (listener != null)
                m_fontNameList.addListSelectionListener(listener);
            JScrollPane namePane = new JScrollPane(m_fontNameList);
            nameBox.add(namePane);
            nameBox.add(Box.createVerticalStrut(10));

            // Font style
            Box styleBox = Box.createVerticalBox();
            styleBox.add(Box.createVerticalStrut(10));
            JLabel fontStyleLabel = new JLabel(resources
                    .getResourceString("fontchooser_Font_Style")
                    + ":");
            styleBox.add(fontStyleLabel);
            if (listener != null)
                m_fontStyleList.addListSelectionListener(listener);
            JScrollPane stylePane = new JScrollPane(m_fontStyleList);
            styleBox.add(stylePane);
            styleBox.add(Box.createVerticalStrut(10));

            // Font size
            Box sizeBox = Box.createVerticalBox();
            sizeBox.add(Box.createVerticalStrut(10));
            JLabel fontSizeLabel = new JLabel(resources.getResourceString("fontchooser_Size") + ":");
            sizeBox.add(fontSizeLabel);
            if (listener != null)
                m_fontSizeList.addListSelectionListener(listener);
            JScrollPane sizePane = new JScrollPane(m_fontSizeList);
            sizeBox.add(sizePane);
            sizeBox.add(Box.createVerticalStrut(10));

            Box mainBox = Box.createHorizontalBox();
            mainBox.add(Box.createHorizontalStrut(10));
            mainBox.add(nameBox);
            mainBox.add(Box.createHorizontalStrut(10));
            mainBox.add(styleBox);
            mainBox.add(Box.createHorizontalStrut(10));
            mainBox.add(sizeBox);
            mainBox.add(Box.createHorizontalStrut(10));
            add(mainBox, BorderLayout.CENTER);
        }

        /**
         * Returns the selected font, derived from the user's list choices.
         */
        public Font getSelectedFont() {
            return new Font(m_fontNameList.getFontName(), m_fontStyleList.getFontStyle(),
                    m_fontSizeList.getFontSize());
        }

        public void setSelectedFont(Font f) {
            if (f == null)
                f = getFont();
            m_fontNameList.setSelectedFont(f.getName());
            m_fontStyleList.setSelectedStyle(f.getStyle());
            m_fontSizeList.setSelectedSize(f.getSize());

        }

        //////////// Private data ///////////////
        private FontNameList m_fontNameList = new FontNameList();;

        private FontStyleList m_fontStyleList = new FontStyleList();

        private FontSizeList m_fontSizeList = new FontSizeList();
    }

    /**
     * Class to present a preview panel containing text which shows the
     * selected font as the user chooses font attributes.
     */
    class PreviewPanel extends JPanel implements ListSelectionListener {

        /**
         * Constructor: Creates an instance of PreviewPanel.
         */
        public PreviewPanel() {
            setLayout(new FlowLayout());

            Box box = Box.createVerticalBox();
            JLabel previewLabel = new JLabel(resources.getResourceString("fontchooser_Preview")
                    + ":");
            box.add(previewLabel);
            m_text.setEditable(false);
            m_text.setBackground(Color.white);
            m_text.setForeground(Color.black);
            JScrollPane pane = new JScrollPane(m_text);
            pane.setPreferredSize(new Dimension(300, 80));
            box.add(pane);

            add(box);
        }

        /**
         * ListSelectionListener required method.
         */
        public void valueChanged(ListSelectionEvent ev) {
            m_text.setFont(JFontChooser.this.getSelectedFont());
        }

        /////// Private data ///////
        private JTextField m_text = new JTextField(resources
                .getResourceString("fontchooser_The_quick_brown_fox_jumps_over_the_lazy_dog")
                + ":");
    }
}

/**
 * Class to present the list of available font names.
 */

class FontNameList extends JList {

    /**
     * Constructor
     */
    FontNameList() {
        super(m_fontNames);
        setSelectedIndex(0);
        setVisibleRowCount(5);
    }

    /**
     * Returns the selected font name.
     */
    String getFontName() {
        String name = (String) getSelectedValue();
        return name;
    }

    public void setSelectedFont(String fontname) {
        for (int i = 0; i < m_fontNames.length; i++) {
            if (fontname.equalsIgnoreCase(m_fontNames[i])) {
                setSelectedValue(getModel().getElementAt(i), true);
                break;
            }
        }
    }

    ////////// Private data /////////
    private static final String[] m_fontNames = GraphicsEnvironment.getLocalGraphicsEnvironment()
            .getAvailableFontFamilyNames();
}

/**
 * Class to present the available font styles.
 */

class FontStyleList extends JList {

    /**
     * Constructor
     */
    FontStyleList() {
        super(m_fontStyles);
        setSelectedIndex(0);
        setVisibleRowCount(5);
    }

    /**
     * Returns the selected font style.
     */
    int getFontStyle() {
        int style = 0;
        String name = (String) getSelectedValue();
        if (name.equals("Regular"))
            style = Font.PLAIN;
        else if (name.equals("Italic"))
            style = Font.ITALIC;
        else if (name.equals("Bold"))
            style = Font.BOLD;
        else
            style = Font.BOLD + Font.ITALIC;
        return style;
    }

    public void setSelectedStyle(int style) {
        String stylename = "";
        switch (style) {
            case Font.PLAIN:
                setSelectedValue(getModel().getElementAt(0), true);
                break;
            case Font.BOLD:
                setSelectedValue(getModel().getElementAt(2), true);
                break;
            case Font.ITALIC:
                setSelectedValue(getModel().getElementAt(1), true);
                break;
            case Font.BOLD + Font.ITALIC:
                setSelectedValue(getModel().getElementAt(3), true);
            	break;
        }
    }

    /////// Private data //////////
    private static final String[] m_fontStyles = { "Regular", "Italic", "Bold", "Bold Italic"};
}

/**
 * Class to present the available Font sizes.
 */

class FontSizeList extends JList {

    /**
     * Constructor.
     */
    FontSizeList() {
        super(m_fontSizes);
        setSelectedIndex(4); // Default to 14 point
        setVisibleRowCount(5);
    }

    /**
     * Returns the selected font size.
     */
    int getFontSize() {
        int size = Integer.parseInt((String) getSelectedValue());
        return size;
    }

    public void setSelectedSize(int size) {
        for (int i = 0; i < m_fontSizes.length; i++) {
            if (size == Integer.parseInt(m_fontSizes[i])) {
                setSelectedValue(getModel().getElementAt(i), true);
                break;
            }
        }
    }

    ////////// Private data /////////
    private static final String[] m_fontSizes = { "6", "8", "10", "12", "14", "16", "18", "20",
            "22", "24", "36", "72"};
}

/**
 * Class to present a font chooser dialog, consisting of a edu.unika.aifb.components.JFontChooser panel
 * with "BTN_OK" and "Cancel" buttons.
 */

class JFontChooserDialog extends JDialog {

    /**
     * Constructor: Creates an instance of a edu.unika.aifb.components.JFontChooserDialog.
     *
     * @param component      the parent component of the dialog
     * @param title          the dialog title (for the title bar)
     * @param modal          whether the dialog is modal
     * @param chooserPane    the edu.unika.aifb.components.JFontChooser pane to be used
     * @param okListener     an ActionListener which listens to the BTN_OK button
     * @param cancelListener an ActionListener which listens to the cancel button
     */
    public JFontChooserDialog(Component component, String title, boolean modal,
            JFontChooser chooserPane, ActionListener okListener, ActionListener cancelListener,
            Resources resources) {
        // Invoke LanguageDialog's constructor, passing in the parent component's frame.
        super(JOptionPane.getFrameForComponent(component), title, modal);

        m_chooserPane = chooserPane;

        // Set contents of  dialog
        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());
        contentPane.add(m_chooserPane, BorderLayout.CENTER);

        // Create lower button panel
        JPanel buttonPane = new JPanel();
        buttonPane.setLayout(new FlowLayout(FlowLayout.CENTER));

        // BTN_OK Button
        JButton okButton = new JButton(resources.getResourceString("fontchooser_btn_ok"));
        getRootPane().setDefaultButton(okButton);
        okButton.setActionCommand("BTN_OK");
        if (okListener != null)
            okButton.addActionListener(okListener);
        okButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                setVisible(false); // just hide the dialog
            }
        });
        buttonPane.add(okButton);

        // Cancel button
        JButton cancelButton = new JButton(resources.getResourceString("fontchooser_btn_cancel"));
        cancelButton.setActionCommand("cancel");
        if (cancelListener != null)
            cancelButton.addActionListener(cancelListener);
        cancelButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                setVisible(false); // just hide the dialog\
            }
        });
        buttonPane.add(cancelButton);

        contentPane.add(buttonPane, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(component);
    }

    ////////// Static nested classes ///////////

    /**
     * Class to hide the dialog window on window closing event.
     */
    static class Closer extends WindowAdapter {

        public void windowClosing(WindowEvent e) {
            Window w = e.getWindow();
            w.setVisible(false);
        }
    }

    /**
     * Class to dispose of the dialog window when the dialog is hidden.
     */
    static class DisposeOnClose extends ComponentAdapter {

        public void componentHidden(ComponentEvent e) {
            Window w = (Window) e.getComponent();
            w.dispose();
        }
    }

    ////////// Private data for edu.unika.aifb.components.JFontChooserDialog /////////
    private JFontChooser m_chooserPane;
}

/**
 * Class to track changes in the selected font in the edu.unika.aifb.components.JFontChooser.
 */

class FontTracker implements ActionListener {

    public FontTracker(JFontChooser chooser) {
        m_chooser = chooser;
    }

    public void actionPerformed(ActionEvent e) {
        m_font = m_chooser.getSelectedFont();
    }

    public Font getSelectedFont() {
        return m_font;
    }

    ///////// private data /////////

    private JFontChooser m_chooser;

    private Font m_font;
}