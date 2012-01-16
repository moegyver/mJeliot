/**
 * The package that contains Jeliot 3's GUI
 */
package jeliot.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.net.URL;
import java.util.ResourceBundle;

import javax.swing.AbstractButton;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import jeliot.mcode.Highlight;
import jeliot.mcode.MCodeUtilities;
import jeliot.util.DebugUtil;
import jeliot.util.ResourceBundles;
import jeliot.util.UserProperties;
import jeliot.util.Util;

/**
 * NOT CURRENTLY USED IN JELIOT 3! CAN CONTAIN OLD AND NOT WORKING CODE!
 * The simple code editor for the users to code their algorithm.
 *
 * @deprecated
 * @author Pekka Uronen
 * @author Niko Myller
 */
public class CodeEditor extends JComponent {

    /**
     * The resource bundle for gui package
     */
    static private UserProperties propertiesbundle = ResourceBundles
            .getGuiUserProperties();

    static private ResourceBundle messagesBundle = ResourceBundles
            .getGuiMessageResourceBundle();

    /**
     * The String for the basic code template that is shown to the user in the beginning.
     */
    private String template = messagesBundle.getString("code_editor.template");

    private String title = propertiesbundle.getStringProperty("name")
            + propertiesbundle.getStringProperty("version");

    /**
     * Font for the editor area.
     */
    private Font areaFont = new Font(propertiesbundle
            .getStringProperty("font.code_editor.family"), Font.PLAIN, Integer
            .parseInt(propertiesbundle
                    .getStringProperty("font.code_editor.size")));

    /**
     * Insets for the text. Used for the layout.
     */
    private Insets insets = new Insets(5, 5, 5, 5);

    /**
     * Line numbering component that handles the correct line numbering in the editor view.
     */
    private LineNumbers nb;

    /**
     * Tells whether or not the current file is changed since last loading or saving.
     * Used to determine when the saving dialog should be popped up.
     */
    private boolean changed = false;

    /**
     * Pointing to the current file that is edited for saving before the compilation.
     */
    private File currentFile = null;

    /**
     * returns true if the document is changed and false
     * if it is not changed. This is the value of the changed
     * field.
     * @return if the document is changed or not.
     */
    public boolean isChanged() {
        return changed;
    }

    /**
     * Returns the current file that is loaded.
     * @return The File object pointing to the current file.
     */
    public File getCurrentFile() {
        return currentFile;
    }

    /**
     * Set wheter or not the document is changed or not.
     * @param changed if true the document is changed if 
     * false the document is not changed (means that it
     * is just loaded or saved).
     */
    public void setChanged(boolean changed) {
        if (changed && this.changed != changed && masterFrame != null) {
            masterFrame.setTitle(masterFrame.getTitle() + " *");
        }
        this.changed = changed;
    }

    /**
     * Document listener is used to handle the line numbering
     * correctly when the new line is created or the document
     * is scrolled by the user
     */
    private DocumentListener dcl = new DocumentListener() {

        public void changedUpdate(DocumentEvent e) {
            setChanged(true);
            validateScrollPane();
        }

        public void insertUpdate(DocumentEvent e) {
            setChanged(true);
            validateScrollPane();
        }

        public void removeUpdate(DocumentEvent e) {
            setChanged(true);
            validateScrollPane();
        }

    };

    private String udir;

    /**
     * Initialization of the text area for the user code.
     */
    JTextArea area = new JTextArea();
    {
        area.setMargin(insets);
        area.setFont(areaFont);
        area.setTabSize(4);
        area.getDocument().addDocumentListener(dcl); //Jeliot 3
        clearProgram();
    }

    /**
     * The file chooser in which the users can
     * load and save the program codes.
     */
    private JFileChooser fileChooser;

    /**
     * The master frame.
     */
    private JFrame masterFrame;

    /**
     * ActionListener that handles the saving of the program code from the code area.
     */
    private ActionListener saver = new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            saveProgram();
        }
    };

    /**
     * ActionListener that handles the loading of the program code to the code area.
     */
    private ActionListener loader = new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            loadProgram();
        }
    };

    /**
     * ActionListener that handels the clearing of the code area.
     */
    private ActionListener clearer = new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            clearProgram();
        }
    };

    /**
     * ActionListener that handels the clearing of the code area.
     */
    private ActionListener cutter = new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            area.cut();
        }
    };

    /**
     * ActionListener that handels the copying of the code area.
     */
    private ActionListener copyist = new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            area.copy();
        }
    };

    /**
     * ActionListener that handels the pasting of the code area.
     */
    private ActionListener pasteur = new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            area.paste();
        }
    };

    /**
     * ActionListener that handels the selection of the whole code area.
     */
    private ActionListener allSelector = new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            if (e.getSource() instanceof Component) {
                Component c = (Component) e.getSource();
                if (!c.isEnabled())
                    return;
            }
            area.requestFocusInWindow();
            area.selectAll();
        }
    };

    /**
     * Set the given frame as the masterFrame.
     *
     * @param frame The Frame that is set as
     * new masterFrame.
     */
    public void setMasterFrame(JFrame frame) {
        this.masterFrame = frame;
    }

    /**
     * Sets the layout and adds the
     * JScrollPane with JTextArea area and JToolbar in it.
     * Initializes the FileChooser.
     */
    public CodeEditor(String udir) {
        this.udir = udir;
        initFileChooser();
        setLayout(new BorderLayout());
        add("Center", makeScrollPane());
        add("North", makeToolBar());
        validateScrollPane();
    }

    /**
     * Creates a ScrollPane object with a
     * LineNumbers set as its left side.
     * @return a scroll pane with certain parameters
     * and line numbering
     * @see jeliot.gui.LineNumbers
     */
    public JComponent makeScrollPane() {
        JScrollPane jsp = new JScrollPane(area);
        nb = new LineNumbers(areaFont, insets);
        nb.setPreferredHeight(area.getSize().height);
        jsp.setRowHeaderView(nb);
        return jsp;
    }

    /**
     * Sets up the file chooser with the user's
     * working directory as default directory.
     */
    private void initFileChooser() {
        // set up the file chooser with user's working
        // directory as default directory
        //Properties prop = System.getProperties();
        //String wdname = prop.getProperty("user.dir");
        //File wd = new File(wdname);
        File wd = new File(udir);
        wd = new File(wd, "examples");
        fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(wd);
        fileChooser.setFileFilter(new JavaFileFilter());
    }

    /**
     * Makes the JButton from the parameters given.
     *
     * @param   label   The label of the button.
     * @param   iconName The name of the image for the button.
     * @param   listener The actionlistener for that button.
     * @return  The constructed button from the given parameters.
     */
    private JButton makeToolButton(String label, String iconName,
            ActionListener listener) {

        URL imageURL = Util.getResourceURL(propertiesbundle.getStringProperty("directory.images")
                + iconName, this.getClass());

        ImageIcon icon = new ImageIcon(imageURL);
        JButton b = new JButton(label, icon);
        b.setVerticalTextPosition(AbstractButton.BOTTOM);
        b.setHorizontalTextPosition(AbstractButton.CENTER);
        b.setMargin(new Insets(0, 0, 0, 0));
        b.addActionListener(listener);
        return b;
    }

    /**
     * The method makes the Buttons for the toolbar of the codearea.
     * Then it adds the button to the JToolBar and returns it.
     * Uses makeToolButton(String, String, ActionListener) -method.
     *
     * @return  The finished toolbar for the code editor.
     * @see #makeToolButton(String, String, ActionListener)
     */
    private JToolBar makeToolBar() {
        JButton loadButton = makeToolButton(messagesBundle
                .getString("button.open"), propertiesbundle
                .getStringProperty("image.open_icon"), loader);
        loadButton.setMnemonic(KeyEvent.VK_O);
        JButton saveButton = makeToolButton(messagesBundle
                .getString("button.save"), propertiesbundle
                .getStringProperty("image.save_icon"), saver);
        saveButton.setMnemonic(KeyEvent.VK_S);
        JButton clearButton = makeToolButton(messagesBundle
                .getString("button.new"), propertiesbundle
                .getStringProperty("image.new_icon"), clearer);
        clearButton.setMnemonic(KeyEvent.VK_N);

        JButton cutButton = makeToolButton(messagesBundle
                .getString("button.cut"), propertiesbundle
                .getStringProperty("image.cut_icon"), cutter);
        cutButton.setMnemonic(KeyEvent.VK_U);
        JButton copyButton = makeToolButton(messagesBundle
                .getString("button.copy"), propertiesbundle
                .getStringProperty("image.copy_icon"), copyist);
        copyButton.setMnemonic(KeyEvent.VK_Y);
        JButton pasteButton = makeToolButton(messagesBundle
                .getString("button.paste"), propertiesbundle
                .getStringProperty("image.paste_icon"), pasteur);
        pasteButton.setMnemonic(KeyEvent.VK_T);

        JToolBar p = new JToolBar();
        p.add(clearButton);
        p.add(loadButton);
        p.add(saveButton);
        p.addSeparator();
        p.add(cutButton);
        p.add(copyButton);
        p.add(pasteButton);
        return p;
    }

    /**
     * Constructs the Program menu.
     *
     * @return The Program menu
     */
    JMenu makeProgramMenu() {
        //TODO: add the mnemonics and accelerators to resources
        JMenu menu = new JMenu(messagesBundle.getString("menu.program"));
        menu.setMnemonic(KeyEvent.VK_P);
        JMenuItem menuItem;

        menuItem = new JMenuItem(messagesBundle.getString("menu.program.new"));
        menuItem.setMnemonic(KeyEvent.VK_N);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N,
                ActionEvent.CTRL_MASK));
        menuItem.addActionListener(clearer);
        menu.add(menuItem);

        menuItem = new JMenuItem(messagesBundle.getString("menu.program.open"));
        menuItem.setMnemonic(KeyEvent.VK_O);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O,
                ActionEvent.CTRL_MASK));
        menuItem.addActionListener(loader);
        menu.add(menuItem);

        menuItem = new JMenuItem(messagesBundle.getString("menu.program.save"));
        menuItem.setMnemonic(KeyEvent.VK_S);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S,
                ActionEvent.CTRL_MASK));
        menuItem.addActionListener(saver);
        menu.add(menuItem);

        return menu;
    }

    /**
     * Constructs the Edit menu.
     *
     * @return  The Edit menu
     */
    JMenu makeEditMenu() {
        JMenu menu = new JMenu(messagesBundle.getString("menu.edit"));
        menu.setMnemonic(KeyEvent.VK_E);
        JMenuItem menuItem;

        menuItem = new JMenuItem(messagesBundle.getString("menu.edit.cut"));
        menuItem.setMnemonic(KeyEvent.VK_U);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X,
                ActionEvent.CTRL_MASK));
        menuItem.addActionListener(cutter);
        menu.add(menuItem);

        menuItem = new JMenuItem(messagesBundle.getString("menu.edit.copy"));
        menuItem.setMnemonic(KeyEvent.VK_Y);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C,
                ActionEvent.CTRL_MASK));
        menuItem.addActionListener(copyist);
        menu.add(menuItem);

        menuItem = new JMenuItem(messagesBundle.getString("menu.edit.paste"));
        menuItem.setMnemonic(KeyEvent.VK_T);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V,
                ActionEvent.CTRL_MASK));
        menuItem.addActionListener(pasteur);
        menu.add(menuItem);

        menu.addSeparator();

        menuItem = new JMenuItem(messagesBundle
                .getString("menu.edit.select_all"));
        menuItem.setMnemonic(KeyEvent.VK_A);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A,
                ActionEvent.CTRL_MASK));
        menuItem.addActionListener(allSelector);
        menu.add(menuItem);

        return menu;
    }

    /**
     * Loads the program from a file to the JTextArea area.
     * Uses readProgram(File file) method to read the file.
     * Uses setProgram(String str) method to set the content of the file
     * into the JTextArea area.
     *
     * @see #readProgram(File)
     * @see #setProgram(String)
     */
    void loadProgram() {
        fileChooser.rescanCurrentDirectory();
        int returnVal = fileChooser.showOpenDialog(masterFrame);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            String program = readProgram(file);
            setProgram(program);

            currentFile = file; // Jeliot 3
            setChanged(false); //Jeliot 3
            setTitle(file.getName());
        }
    }

    /**
     * Saves the program from the JTextArea area to
     * the file. Uses writeProgram(File file) method
     * to write the code into a file.
     *
     * @see #writeProgram(File)
     */
    void saveProgram() {
        fileChooser.rescanCurrentDirectory();
        int returnVal = fileChooser.showSaveDialog(masterFrame);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            writeProgram(file);
        }

    }

    /**
     * Saves the content of the
     * JTextArea area to a given file.
     *
     * @param file The file where the content of
     * JTextArea is saved.
     * 
     * @see JeliotWindow#tryToEnterAnimate()
     */
    public void writeProgram(File file) {
        try {
            FileWriter w = new FileWriter(file);
            w.write(area.getText());
            w.close();

            /*
             * These are added here because
             * JeliotWindow.tryToEnterAnimate()
             * calls this method directly without calling
             * saveProgram method first.
             */
            currentFile = file; // Jeliot 3
            setChanged(false); //Jeliot 3
            setTitle(file.getName()); // Jeliot 3

        } catch (IOException e) {
            //e.printStackTrace();
            JOptionPane.showMessageDialog(masterFrame,
                    "File could not be saved.\n"
                            + "Please try to save it in a different location");
            saveProgram();
        }
    }

    /**
     * Reads the content of the given file and
     * returns the content of the file as String.
     *
     * @param file The file from which the content
     * is read and returned for the use of loadProgram()
     * method.
     * @return The content of the file that was given
     * as parameter.
     * @see #loadProgram()
     */
    String readProgram(File file) {
        try {
            Reader fr = new FileReader(file);
            BufferedReader r = new BufferedReader(fr);

            StringBuffer buff = new StringBuffer();
            String line;
            while ((line = r.readLine()) != null) {
                buff.append(line);
                buff.append("\n");
            }
            r.close();
            return buff.toString();
        } catch (IOException e) {
            //TODO: report to user that something went wrong!
            if (DebugUtil.DEBUGGING) {
                e.printStackTrace();
            }
            return null;
        }
    }

    /**
     * Sets in JTextAre area the default text as given in template.
     */
    void clearProgram() {
        setProgram(template);

        currentFile = null; //Jeliot 3
        setTitle("");
    }

    /**
     * 
     * @param filename
     */
    public void setTitle(String filename) {
        if (masterFrame != null) {
            if (filename != null && filename.equals("")) {
                masterFrame.setTitle(title);
            } else {
                masterFrame.setTitle(title + " - " + filename);
            }
        }
    }

    /**
     * The given String program object will be set as the text inside the JTextArea area.
     *
     * @param program The string that will be set in JTextArea area as the program code.
     */
    public void setProgram(String program) {
        area.setText(program);
        setChanged(false); //Jeliot 3
    }

    /**
     * Calculates the number of lines in the program source code.
     * @param text the program source code.
     * @return the number of lines in the given program source code
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
     * Validates the scroll pane's line numbering.
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
     * Method returns the program code inside the JTextArea as String -object
     * Tabulators are changed to spaces for uniform handling of white spaces.
     * One tabulator corresponds four ASCII white spaces.
     *
     * @return  The program code inside the JTextArea area.
     */
    public String getProgram() {
        String programCode = area.getText() + "\n";
        programCode = MCodeUtilities.replace(programCode, "\t", "    ");
        return programCode;
    }

    /**
     * Method highlights the specified Statement area by selecting it.
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
        } catch (Exception e) {
        }

        final int left = l - 1;
        final int right = r;

        Runnable updateAComponent = new Runnable() {
            public void run() {
                area.requestFocusInWindow();
                if (left != 0 && left == right) {
                    area.select(left, right + 1);
                } else {
                    area.select(left, right);
                }
            }
        };
        SwingUtilities.invokeLater(updateAComponent);
    }

    /**
     * Method highlights the specified code area by selecting it.
     * @param h contains the area that should be highlighted.
     */
    public void highlight(Highlight h) {
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
        } catch (Exception e) {
        }

        final int left = l - 1;
        final int right = r;

        area.requestFocusInWindow();
        if (left != 0 && left == right) {
            area.select(left, right + 1);
        } else {
            area.select(left, right);
        }
    }

}
