package jeliot.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import jeliot.Jeliot;
import jeliot.calltree.TreeDraw;
import jeliot.gui.mJeliot.UserSelection;
import jeliot.gui.mJeliot.PredictResultStats;
import jeliot.gui.mJeliot.PredictUsersStats;
import jeliot.gui.mJeliot.InteractButton;
import jeliot.historyview.HistoryView;
import jeliot.mJeliot.CodeButtonController;
import jeliot.mcode.InterpreterError;
import jeliot.printing.PrintingUtil;
import jeliot.theater.Animation;
import jeliot.theater.AnimationEngine;
import jeliot.theater.ImageLoader;
import jeliot.theater.PanelController;
import jeliot.theater.PauseListener;
import jeliot.theater.Theater;
import jeliot.tracker.Reminder;
import jeliot.tracker.Tracker;
import jeliot.tracker.TrackerClock;
import jeliot.util.DebugUtil;
import jeliot.util.ResourceBundles;
import jeliot.util.SourceCodeUtilities;
import jeliot.util.UserProperties;

import org.syntax.jeliot_jedit.JEditTextArea;

import edu.unika.aifb.components.JFontChooser;

/**
 * The main window of the Jeliot 3.
 * 
 * @author Pekka Uronen
 * @author Niko Myller
 */
public class JeliotWindow implements PauseListener, MouseListener {

    /**
     * The resource bundle for gui package
     */
    static private UserProperties propertiesBundle = ResourceBundles
            .getGuiUserProperties();

    /**
     * The resource bundle for gui package
     */
    static private ResourceBundle messageBundle = ResourceBundles
            .getGuiMessageResourceBundle();

    /**
     * User properties that were saved from previous run.
     */
    private UserProperties jeliotUserProperties = ResourceBundles
            .getJeliotUserProperties();
    {
        /* If a method call should be asked from the user */
        if (!jeliotUserProperties.containsKey("ask_for_method")) {
            jeliotUserProperties.setBooleanProperty("ask_for_method", false);
        }

        /* If the starting method call is main with command line parameters should the parameters be asked.*/
        if (!jeliotUserProperties.containsKey("ask_for_main_method_parameters")) {
            jeliotUserProperties.setBooleanProperty(
                    "ask_for_main_method_parameters", false);
        }

        /* if the new String[0] should be replaced with null in the main method call. */
        if (!jeliotUserProperties.containsKey("use_null_to_call_main")) {
            jeliotUserProperties.setBooleanProperty("use_null_to_call_main",
                    false);
        }

        /* Whether Jeliot should be asking questions during the animation */
        if (!jeliotUserProperties.containsKey("ask_questions")) {
            jeliotUserProperties.setBooleanProperty("ask_questions", false);
        }

        /* Should the messages during the program visualization be shown as message dialogs. */
        if (!jeliotUserProperties.containsKey("pause_on_message")) {
            jeliotUserProperties.setBooleanProperty("pause_on_message", false);
        }

        if (!jeliotUserProperties.containsKey("save_unicode")) {
            jeliotUserProperties.setBooleanProperty("save_unicode", false);
        }

        if (!jeliotUserProperties.containsKey("save_automatically")) {
            jeliotUserProperties
                    .setBooleanProperty("save_automatically", false);

        }

        if (!jeliotUserProperties.containsKey("CG")) {
            jeliotUserProperties.setBooleanProperty("CG", true);

        }
        /* Saves the state of the mJeliot View. */
        if (!jeliotUserProperties.containsKey("show_mjeliot_view")) {
            jeliotUserProperties.setBooleanProperty("show_mjeliot_view", false);

        }
    }

    /* If a method call should be asked from the user */
    //private boolean askForMethod = false;
    /* If the starting method call is main with command line parameters should the parameters be asked.*/
    //private boolean askForMainMethodParameters = false;
    /* if the new String[0] should be replaced with null in the main method call. */
    //private boolean useNullInMainMethodCall = false;
    /* Whether Jeliot should be asking questions during the animation */
    //private boolean askQuestions = false;
    /* Should the messages during the program visualization be shown as message dialogs. */
    //private boolean showMessagesInDialogs = false;
    /**
     * The version information about Jeliot from name and version from the
     * resource bundle.
     */
    private String jeliotVersion = messageBundle.getString("name") + " "
            + messageBundle.getString("version");

    /**
     * Helping to enable and disable the components.
     */
    private Vector editWidgets = new Vector();

    /**
     * Helping to enable and disable the components.
     */
    private Vector animWidgets = new Vector();

    /**
     * The user directory.
     */
    private String udir;

    /** Control panel */
    private JComponent conPan;

    /**
     * 
     */
    private int runUntilLine = -1;

    /**
     * True if an error has occured during the execution and false if no error
     * was encountered.
     */
    private boolean errorOccured = false;

    /**
     * The about window of Jeliot 3.
     */
    private InfoWindow aw = null;

    /**
     * The help window of Jeliot 3.
     */
    private InfoWindow hw = null;

    /** Color for highlighting a tab name when its content has changed. */
    private Color highlightTabColor = new Color(Integer.decode(
            propertiesBundle
                    .getStringProperty("color.tab.foreground.highlight"))
            .intValue());

    /** Color for highlighting a tab name when its content has changed. */
    private Color normalTabColor = new Color(Integer.decode(
            propertiesBundle.getStringProperty("color.tab.foreground.normal"))
            .intValue());

    /** The previous speed before the run until line is set. */
    private int previousSpeed;

    /** The frame in which all the action goes on. */
    private JFrame frame;

    /** Tabbed pane to show several visualization of the one program executions. */
    private JTabbedPane tabbedPane = new JTabbedPane();
    {
        tabbedPane.addChangeListener(new ChangeListener() {

            public void stateChanged(ChangeEvent e) {
                highlightTabTitle(false, tabbedPane.getSelectedIndex());
                codePane.clearHighlights();
                selectedIndexInTabbedPane = ((JTabbedPane) e.getSource())
                        .getSelectedIndex();
            }
        });
    }

    /**
     * 
     */
    private int selectedIndexInTabbedPane = 0;

    /** If animation is running until certain line */
    private boolean runningUntil = false;

    /** TreeDraw view that shows the call tree of the program execution. */
    private TreeDraw callTree;

    /** The theater in which the programs are animated. */
    private Theater theater;

    private JScrollPane theaterScrollPane;

    /** The animation engine that that will animate the code. */
    private AnimationEngine engine;

    /** The main program. */
    private Jeliot jeliot;
    
    /** The code pane where the code is shown during the animation. */
    private CodePane2 codePane;

    /** The code editor in which the users can write their code. */
    private CodeEditor2 editor;

    //private CodeEditor editor = new CodeEditor();

    /** The pane that splits the window. */
    private JSplitPane codeNest;

    /** The step button. */
    private JButton stepButton;

    /** The play button. */
    private JButton playButton;

    /** The pause button. */
    private JButton pauseButton;

    /** The rewind button. */
    private JButton rewindButton;

    /** The edit button. */
    private JButton editButton;

    /** The compile button. */
    private JButton compileButton;
    
    // MOE
    /** The interact button. */
    private InteractButton interactButton;
    // /MOE

    /** Slider tha controls the animation speed. */
    private JSlider speedSlider;

    /** In this text area will come the output of the user-made programs. */
    private JTextArea outputConsole;

    /**
     * Menu items that should be either enabled or disabled when the animation
     * mode is entered or exited respectively.
     */
    private Vector animationMenuItems = new Vector();

    /** This ImageLoader will load all the images. */
    private ImageLoader iLoad;

    /** This variable will control the panels. */
    private PanelController panelController;

    /** Showing the history of the program execution */
    private HistoryView hv;

    /** If user wants to record the mcode to the corresponding animation */

    /* private MCodeSaver mCodeSaver = null; */

    /** Keeps the previous value of the default duration of the Animation*/
    private int previousDefaultDuration;

    /**
     * This JEditorPane errorJEditorPane will show the error messages for the
     * users.
     */
    private JEditorPane errorJEditorPane = new JEditorPane();
    {
        errorJEditorPane.setEditable(false);
        errorJEditorPane.setContentType("text/html");
        errorJEditorPane.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLoweredBevelBorder(), BorderFactory
                        .createEmptyBorder(10, 10, 10, 10)));
        errorJEditorPane.setRequestFocusEnabled(false);
    }

    /**
     * Scroll pane that provides the scroll bars for the error pane's editor
     */
    private JScrollPane errorPane = new JScrollPane(errorJEditorPane);
    {
        errorPane
                .setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        //errorPane.setPreferredSize(new Dimension(250, 145));
        errorPane.setRequestFocusEnabled(false);
    }

    /**
     * This JPanel errorViewer will help the showing of the error messages for
     * the users.
     */
    private JPanel errorViewer = new JPanel() {

        private Image backImage;

        public void paintComponent(Graphics g) {
            Dimension d = getSize();
            int w = d.width;
            int h = d.height;
            if (backImage == null) {
                backImage = iLoad.getLogicalImage("image.panel");
            }
            int biw = backImage.getWidth(this);
            int bih = backImage.getHeight(this);

            if (biw >= 1 || bih >= 1) {
                for (int x = 0; x < w; x += biw) {
                    for (int y = 0; y < h; y += bih) {
                        g.drawImage(backImage, x, y, this);
                    }
                }
            }
        }
    };
    {
        errorViewer.setBorder(BorderFactory.createEmptyBorder(12, 12, 5, 12));
        errorViewer.setLayout(new BorderLayout());
        errorViewer.add("Center", errorPane);
        JPanel bp = new JPanel();
        bp.setOpaque(false);

        JButton ok = new JButton(messageBundle.getString("button.ok"));
        ok.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                changeTheatrePane(tabbedPane);
                //editButton.doClick();
            }
        });
        bp.add(ok);
        errorViewer.add("South", bp);
    }

    /**
     * AdjustmentListener is for Tracking purposes
     */
    private AdjustmentListener scrollPaneListener = new AdjustmentListener() {
        public void adjustmentValueChanged(AdjustmentEvent e) {
            if (theater.isShowing()) {
                Rectangle r = theaterScrollPane.getViewport().getViewRect();
                Point t = theater.getLocationOnScreen();
                Dimension d = theater.getSize();
                Tracker.trackEvent(TrackerClock.currentTimeMillis(),
                        Tracker.SCROLL, (int) (t.x + r.getX()), (int) (t.y + r
                                .getY()), (int) r.getWidth(), (int) r
                                .getHeight(), "Scroll: Theater: On screen at: "
                                + t.x + "," + t.y + "," + d.width + ","
                                + d.height);
            }
        }
    };

    private Action useNullInMainMethodCallAction = new AbstractAction(
            messageBundle.getString("menu.options.use_null_to_call_main")) {

        public void actionPerformed(ActionEvent e) {
            jeliotUserProperties.setBooleanProperty("use_null_to_call_main",
                    !jeliotUserProperties
                            .getBooleanProperty("use_null_to_call_main"));
        }

    };
    {
        useNullInMainMethodCallAction.putValue(Action.MNEMONIC_KEY,
                new Integer(KeyEvent.VK_N));
        useNullInMainMethodCallAction.putValue(Action.ACCELERATOR_KEY,
                KeyStroke.getKeyStroke(KeyEvent.VK_N, ActionEvent.CTRL_MASK
                        + ActionEvent.ALT_MASK));
        this.editWidgets.add(this.useNullInMainMethodCallAction);
    }
    
    
    /**
     * Shows and hides the mJeliotView Panel and sets the value in the properties. 
     */
    private Action showMJeliotViewAction = new AbstractAction(
            messageBundle.getString("menu.options.show_mjeliot_view")) {
				private static final long serialVersionUID = 4045617838736035072L;

		public void actionPerformed(ActionEvent e) {
            jeliotUserProperties.setBooleanProperty("show_mjeliot_view",
                    !jeliotUserProperties
                            .getBooleanProperty("show_mjeliot_view"));
            statsPane.setVisible(jeliotUserProperties
                            .getBooleanProperty("show_mjeliot_view"));
        }

    };

    //Save into unicode.
    private Action saveUnicodeAction = new AbstractAction(messageBundle
            .getString("menu.options.save_unicode")) {

        public void actionPerformed(ActionEvent e) {
            jeliotUserProperties.setBooleanProperty("save_unicode",
                    !jeliotUserProperties.getBooleanProperty("save_unicode"));
        }

    };
    {
        saveUnicodeAction.putValue(Action.MNEMONIC_KEY, new Integer(
                KeyEvent.VK_C));
        saveUnicodeAction.putValue(Action.ACCELERATOR_KEY, KeyStroke
                .getKeyStroke(KeyEvent.VK_C, ActionEvent.CTRL_MASK
                        + ActionEvent.ALT_MASK));
    }

    /**
     * Action listeners for the step- button.
     */
    private ActionListener stepAction = new ActionListener() {

        public void actionPerformed(ActionEvent e) {
            Tracker.trackEvent(TrackerClock.currentTimeMillis(),
                    Tracker.BUTTON, -1, -1, "StepButton");
            stepAnimation();
            Tracker.trackEvent(TrackerClock.currentTimeMillis(), Tracker.OTHER,
                    -1, -1, "AnimationStarted");
        }
    };

    /**
     * Action listeners for the play- button.
     */
    private ActionListener playAction = new ActionListener() {

        public void actionPerformed(ActionEvent e) {
            Tracker.trackEvent(TrackerClock.currentTimeMillis(),
                    Tracker.BUTTON, -1, -1, "PlayButton");
            playAnimation();
            Tracker.trackEvent(TrackerClock.currentTimeMillis(), Tracker.OTHER,
                    -1, -1, "AnimationStarted");
        }
    };

    /**
     * Action listeners for the pause- button.
     */
    private ActionListener pauseAction = new ActionListener() {

        public void actionPerformed(ActionEvent e) {
            Tracker.trackEvent(TrackerClock.currentTimeMillis(),
                    Tracker.BUTTON, -1, -1, "PauseButton");
            pauseAnimation();
        }
    };

    /**
     * Action listeners for the rewind- button.
     */
    private ActionListener rewindAction = new ActionListener() {

        public void actionPerformed(ActionEvent e) {
            Tracker.trackEvent(TrackerClock.currentTimeMillis(),
                    Tracker.BUTTON, -1, -1, "RewindButton");
            rewindAnimation();
        }
    };

    /**
     * Action listener for the exit.
     */
    private ActionListener exit = new ActionListener() {

        public void actionPerformed(ActionEvent e) {
            WindowListener[] wl = frame.getWindowListeners();
            int n = wl.length;
            for (int i = 0; i < n; i++) {
                wl[i].windowClosing(new WindowEvent(frame,
                        WindowEvent.WINDOW_CLOSING));
            }
            /*
             if (editor.isChanged()) {
             int n = JOptionPane.showConfirmDialog(frame, bundle
             .getString("quit.without.saving.message"), bundle
             .getString("quit.without.saving.title"),
             JOptionPane.YES_NO_OPTION);
             if (n == JOptionPane.YES_OPTION) {
             editor.saveProgram();
             }
             }

             Properties prop = System.getProperties();
             File f = new File(udir);
             prop.put("user.dir", f.toString());
             Jeliot.close();
             
             if (Jeliot.isnoSystemExit()) {
             frame.dispose();
             } else {
             //frame.dispose();
             System.exit(0);
             }
             */
        }
    };

	private JPanel statsPane;

	private JScrollPane userSelectionPane;

    /**
     * Assigns the values of the parameters in the object values. Constructs the
     * panelController with theater and iload.
     * 
     * @param jeliot
     *            The main program.
     * @param codePane
     *            The pane where all the code is shown while animated.
     * @param theater
     *            The theater where all the code is animated.
     * @param engine
     *            The engine that animates the code.
     * @param iLoad
     *            The imageloader that loads all the images.
     * @param udir
     *            The user directory
     */
    public JeliotWindow(Jeliot jeliot, CodePane2 codePane, Theater theatre,
            AnimationEngine engine, ImageLoader iLoad, String udir,
            TreeDraw td, HistoryView hv) {

        this.jeliot = jeliot;
        this.codePane = codePane;
        this.theater = theatre;
        this.engine = engine;
        this.iLoad = iLoad;
        this.udir = udir;
        this.callTree = td;
        this.hv = hv;

        this.frame = new JFrame(jeliotVersion);
        this.frame.addMouseListener(this);
        this.panelController = new PanelController(theatre, iLoad);
        //this.editor = new CodeEditor(this.udir);
        this.editor = new CodeEditor2(this.jeliot, this.udir, jeliot.getImportIOStatement());
        new CodeButtonController(this.jeliot.getMJeliotController(), editor);
        editor.setMasterFrame(frame);
        /*        this.mCodeSaver = new MCodeSaver();*/
    }

    public URL getURL(String filename) {
        URL soundURL = Thread.currentThread().getContextClassLoader()
                .getResource(
                        propertiesBundle.getStringProperty("directory.images")
                                + filename);
        if (soundURL == null) {
            soundURL = (this.getClass().getClassLoader()
                    .getResource(propertiesBundle
                            .getStringProperty("directory.images")
                            + filename));
        }
        if (soundURL == null) {
            soundURL = ClassLoader.getSystemResource(propertiesBundle
                    .getStringProperty("directory.images")
                    + filename);
        }
        return soundURL;
    }

    /**
     * Initializes the JFrame frame. Sets up all the basic things for the
     * window. (Panels, Panes, Menubars) Things for debugging.
     */
    public void setUp() {
        try {
            this.theaterScrollPane = new JScrollPane(this.theater);
            this.theaterScrollPane.getHorizontalScrollBar()
                    .addAdjustmentListener(scrollPaneListener);
            this.theaterScrollPane.getVerticalScrollBar()
                    .addAdjustmentListener(scrollPaneListener);
            this.theater.setScrollPane(this.theaterScrollPane);

            this.tabbedPane.addTab(
                    messageBundle.getString("tab.title.theater"),
                    theaterScrollPane);
            this.tabbedPane.setMnemonicAt(0, KeyEvent.VK_T);

            if (!jeliot.isExperiment()) {
                this.tabbedPane.addTab(messageBundle
                        .getString("tab.title.call_tree"), callTree
                        .getComponent());
                this.tabbedPane.setMnemonicAt(1, KeyEvent.VK_L);

                this.tabbedPane.addTab(messageBundle
                        .getString("tab.title.history"), hv);
                this.tabbedPane.setMnemonicAt(2, KeyEvent.VK_Y);
                this.tabbedPane.setEnabledAt(tabbedPane
                        .indexOfTab(messageBundle
                                .getString("tab.title.call_tree")), false);
                this.tabbedPane.setEnabledAt(tabbedPane
                        .indexOfTab(messageBundle
                                .getString("tab.title.history")), false);
            }
            userSelectionPane = new UserSelection(jeliot.getMJeliotController(), this);
            this.tabbedPane.addTab(messageBundle.getString("tab.title.userselection"), userSelectionPane);
            this.tabbedPane.setEnabledAt(tabbedPane.indexOfComponent(userSelectionPane), false);

            this.frame.setIconImage(iLoad.getImage(propertiesBundle
                    .getStringProperty("image.jeliot_icon")));

            frame.setJMenuBar(makeMenuBar());

            JSplitPane pane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                    editor, tabbedPane);

            if (jeliot.isExperiment()) {
                pane.setEnabled(false);
            } else {
                pane.setOneTouchExpandable(true);
            }

            codeNest = pane;

            Dimension minimumSize = new Dimension(0, 0);
            codePane.setMinimumSize(minimumSize);
            editor.setMinimumSize(minimumSize);

            JPanel bottomPane = new JPanel(new BorderLayout());
            conPan = makeControlPanel();
            bottomPane.add("West", conPan);

            OutputConsole oc = new OutputConsole(conPan);
            this.outputConsole = oc;

            bottomPane.add("Center", oc.container);
            // MOE
            this.statsPane = new JPanel();
            this.statsPane.setVisible(jeliotUserProperties.getBooleanProperty("show_mjeliot_view"));
            this.statsPane.setLayout(new BoxLayout(this.statsPane, BoxLayout.Y_AXIS));
            PredictUsersStats userStats = new PredictUsersStats(this.jeliot);
            this.jeliot.getMJeliotController().addMJeliotControllerListener(userStats);
            this.statsPane.add(userStats);
            if (this.statsPane.isVisible()) {
            	userStats.repaint();
            }
            PredictResultStats resultStats = new PredictResultStats();
            this.jeliot.getMJeliotController().addMJeliotControllerListener(resultStats);
            this.statsPane.add(resultStats);
            
            // /MOE

            JPanel rootPane = new JPanel(new BorderLayout());
            rootPane.add("Center", pane);
            rootPane.add("South", bottomPane);
            rootPane.add("West", this.statsPane);

            frame.setContentPane(rootPane);

            //Maximize the window.
            /*
             Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
             //frame.setSize(screenSize.width, screenSize.height - 30);
             frame.setSize(800, 600);
             Dimension frameSize = frame.getSize();
             if (frameSize.height > screenSize.height)
             frameSize.height = screenSize.height;
             if (frameSize.width > screenSize.width)
             frameSize.width = screenSize.width;

             frame.setLocation((screenSize.width - frameSize.width) / 2,
             (screenSize.height - frameSize.height) / 2);
             */

            frame.addWindowListener(new WindowAdapter() {

                public void windowClosing(WindowEvent e) {
                    closeWindow();
                }
            });

            enterEditTrue();
            pane.setDividerLocation((jeliot.isExperiment()) ? 512 : 300);

            //TheatrePopup popup = new TheatrePopup();
            //theater.addMouseListener(popup);
            //theater.addMouseMotionListener(popup);

            hw = new InfoWindow(messageBundle.getString("window.help.content"),
                    udir, iLoad.getImage(propertiesBundle
                            .getStringProperty("image.jeliot_icon")),
                    messageBundle.getString("window.help.title"));
            aw = new InfoWindow(
                    messageBundle.getString("window.about.content"), udir,
                    iLoad.getImage(propertiesBundle
                            .getStringProperty("image.jeliot_icon")),
                    messageBundle.getString("window.about.title"));

            frame.pack();
            frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
            frame.setVisible(true);
            //if (jeliot.isExperiment()) {
            //}

            //editor.requestFocus();
            //System.out.println(theater.getSize());

        } catch (Exception e) {
            e.printStackTrace(System.out);
        }

    }

    public void closeWindow() {
        if (editor.isChanged()) {
            int n = JOptionPane.showConfirmDialog(frame, messageBundle
                    .getString("quit.without.saving.message"), messageBundle
                    .getString("quit.without.saving.title"),
                    JOptionPane.YES_NO_OPTION);
            if (n == JOptionPane.YES_OPTION) {
                editor.saveProgram();
            }
        }

        ResourceBundles.saveAllUserProperties();
        Properties prop = System.getProperties();
        File f = new File(udir);
        prop.put("user.dir", f.toString());
        jeliot.close();

        if (Jeliot.isnoSystemExit()) {
            frame.dispose();
        } else {
            //frame.dispose();
            System.exit(0);
        }
    }

    public JFrame getFrame() {
        return frame;
    }

    /**
     * Makes and returns the menubar for the main frame.
     * Things for debugging.
     * 
     * @return The menubar for the main frame.
     */
    private JMenuBar makeMenuBar() {

        JMenuBar menuBar = new JMenuBar();

        JMenuItem menuItem;

        //a group of JMenuItems
        JMenu programMenu = editor.makeProgramMenu();

        menuItem = new JMenuItem(messageBundle.getString("menu.program.print"));
        menuItem.setMnemonic(KeyEvent.VK_P);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P,
                ActionEvent.CTRL_MASK));
        menuItem.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                if (e.getSource() instanceof Component) {
                    if (!((Component) e.getSource()).isEnabled()) {
                        return;
                    }
                }
                JEditTextArea area = editor.getTextArea();
                area.getPainter().setPrinting(true);
                int caretPosition = area.getCaretPosition();
                int selectionStart = area.getSelectionStart();
                int selectionEnd = area.getSelectionEnd();
                area.setCaretPosition(0);
                PrintingUtil.printComponent(area.getPainter(), area
                        .getTotalArea());
                area.setCaretPosition(caretPosition);
                if (selectionStart != selectionEnd) {
                    if (caretPosition == selectionStart) {
                        area.select(selectionEnd, selectionStart);
                    } else {
                        area.select(selectionStart, selectionEnd);
                    }
                } else {
                    area.setCaretPosition(caretPosition);
                }
                area.getPainter().setPrinting(false);
            }
        });
        programMenu.add(menuItem);

        programMenu.addSeparator();

        //TODO: MCodeSaver
        //programMenu.add(mCodeSaver.makeSaverMenuItem());
        //programMenu.add(mCodeSaver.makeOpenMCodeMenuItem());

        programMenu.addSeparator();

        menuItem = new JMenuItem(messageBundle.getString("menu.program.exit"));
        menuItem.setMnemonic(KeyEvent.VK_X);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q,
                ActionEvent.CTRL_MASK));
        menuItem.addActionListener(exit);
        programMenu.add(menuItem);

        addMenuIntoWidgets(programMenu, editWidgets);
        //editWidgets.addElement(programMenu);
        menuBar.add(programMenu);

        JMenu editMenu = editor.makeEditMenu();
        addMenuIntoWidgets(editMenu, editWidgets);
        //editWidgets.addElement(editMenu);
        menuBar.add(editMenu);

        JMenu controlMenu = makeControlMenu();
        menuBar.add(controlMenu);

        JMenu animationMenu = makeAnimationMenu();
        addMenuIntoWidgets(animationMenu, animWidgets);
        //animWidgets.addElement(animationMenu);
        menuBar.add(animationMenu);

        JMenu optionsMenu = makeOptionsMenu();
        menuBar.add(optionsMenu);

        JMenu helpMenu = makeHelpMenu();
        menuBar.add(helpMenu);

        JMenu[] jm = { controlMenu, animationMenu };
        addInAnimationMenuItems(jm);

        return menuBar;
    }

    /**
     * 
     * @param menu
     * @param widgets
     */
    private void addMenuIntoWidgets(JMenu menu, Vector widgets) {
        for (int i = 0; i < menu.getItemCount(); i++) {
            JMenuItem item = menu.getItem(i);
            if (item != null) {
                Action a = item.getAction();
                if (a != null) {
                    widgets.add(a);
                } else {
                    widgets.add(item);
                }
            }
        }
        Action a = menu.getAction();
        if (a != null) {
            widgets.add(a);
        } else {
            widgets.add(menu);
        }
    }

    /**
     * Adds the given JMenu's
     * JMenuItems into the Vector animationMenuItems.
     */
    private void addInAnimationMenuItems(JMenu[] jm) {
        for (int i = 0; i < jm.length; i++) {
            int length = jm[i].getItemCount();
            for (int j = 0; j < length; j++) {
                JMenuItem jmi = jm[i].getItem(j);
                if (jmi != null) {
                    Action a = jmi.getAction();
                    if (a != null) {
                        animationMenuItems.add(a);
                    } else {
                        animationMenuItems.add(jmi);
                    }
                }
            }
        }
    }

    private JMenu makeOptionsMenu() {

        JMenu menu = new JMenu(messageBundle.getString("menu.options"));
        menu.setMnemonic(KeyEvent.VK_I);

        //Save files into unicode
        final JCheckBoxMenuItem saveUnicodeItem = new JCheckBoxMenuItem(
                this.saveUnicodeAction);
        saveUnicodeItem.setState(jeliotUserProperties
                .getBooleanProperty("save_unicode"));
        menu.add(saveUnicodeItem);

        //Save Automatically on compilation.
        /*
         if (jeliotUserProperties.containsKey("save_automatically")) {
         editor.setSaveAutomatically(Boolean.valueOf(
         jeliotUserProperties
         .getStringProperty("save_automatically"))
         .booleanValue());
         } else {
         jeliotUserProperties.setStringProperty("save_automatically",
         Boolean.toString(editor.isSaveAutomatically()));
         }
         */
        final JCheckBoxMenuItem saveAutomaticallyOnCompilationMenuItem = new JCheckBoxMenuItem(
                messageBundle.getString("menu.options.save_automatically"),
                jeliotUserProperties.getBooleanProperty("save_automatically"));
        saveAutomaticallyOnCompilationMenuItem.setMnemonic(KeyEvent.VK_S);
        saveAutomaticallyOnCompilationMenuItem.setAccelerator(KeyStroke
                .getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK
                        + ActionEvent.ALT_MASK));

        saveAutomaticallyOnCompilationMenuItem
                .addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        jeliotUserProperties.setBooleanProperty(
                                "save_automatically",
                                saveAutomaticallyOnCompilationMenuItem
                                        .getState());
                    }
                });
        menu.add(saveAutomaticallyOnCompilationMenuItem);

        menu.addSeparator();

        //Show strings as objects
        final JCheckBoxMenuItem showStringsAsObjects = new JCheckBoxMenuItem(
                messageBundle.getString("menu.options.show_strings_as_objects"),
                jeliotUserProperties
                        .getBooleanProperty("show_strings_as_objects"));
        showStringsAsObjects.setMnemonic(KeyEvent.VK_O);
        showStringsAsObjects.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_O, ActionEvent.CTRL_MASK + ActionEvent.ALT_MASK));
        showStringsAsObjects.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                jeliotUserProperties.setBooleanProperty(
                        "show_strings_as_objects", showStringsAsObjects
                                .getState());
            }
        });
        menu.add(showStringsAsObjects);
        this.editWidgets.add(showStringsAsObjects);

        //Do CG
        final JCheckBoxMenuItem garbageCollection = new JCheckBoxMenuItem(
                messageBundle.getString("menu.options.CG"),
                jeliotUserProperties.getBooleanProperty("CG"));
        garbageCollection.setMnemonic(KeyEvent.VK_G);
        garbageCollection.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_G,
                ActionEvent.CTRL_MASK + ActionEvent.ALT_MASK));
        garbageCollection.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                jeliotUserProperties.setBooleanProperty("CG", garbageCollection
                        .getState());
            }
        });
        menu.add(garbageCollection);

        //Do CG
        final JMenuItem garbageCollectionNow = new JMenuItem(messageBundle
                .getString("menu.options.CG_now"));
        garbageCollectionNow.setMnemonic(KeyEvent.VK_G);
        garbageCollectionNow.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_G, ActionEvent.CTRL_MASK + ActionEvent.ALT_MASK));
        garbageCollectionNow.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                jeliot.collectGarbage();
                DebugUtil.printDebugInfo("garbage collected");
            }
        });
        menu.add(garbageCollectionNow);
        this.animWidgets.add(garbageCollectionNow);

        //Ask for main method/command line parameters
        final JCheckBoxMenuItem askForMainMethodParametersMenuItem = new JCheckBoxMenuItem(
                messageBundle
                        .getString("menu.options.ask_for_main_method_parameters"),
                jeliotUserProperties
                        .getBooleanProperty("ask_for_main_method_parameters"));
        askForMainMethodParametersMenuItem.setMnemonic(KeyEvent.VK_A);
        askForMainMethodParametersMenuItem.setAccelerator(KeyStroke
                .getKeyStroke(KeyEvent.VK_A, ActionEvent.CTRL_MASK
                        + ActionEvent.ALT_MASK));
        askForMainMethodParametersMenuItem
                .addActionListener(new ActionListener() {

                    public void actionPerformed(ActionEvent e) {
                        jeliotUserProperties.setBooleanProperty(
                                "ask_for_main_method_parameters",
                                askForMainMethodParametersMenuItem.getState());
                    }
                });
        menu.add(askForMainMethodParametersMenuItem);

        //Ask for method
        final JCheckBoxMenuItem askForMethodMenuItem = new JCheckBoxMenuItem(
                messageBundle.getString("menu.options.ask_for_method"),
                jeliotUserProperties.getBooleanProperty("ask_for_method"));
        askForMethodMenuItem.setMnemonic(KeyEvent.VK_M);
        askForMethodMenuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_M, ActionEvent.CTRL_MASK + ActionEvent.ALT_MASK));
        askForMethodMenuItem.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                jeliotUserProperties.setBooleanProperty("ask_for_method",
                        askForMethodMenuItem.getState());
            }
        });
        menu.add(askForMethodMenuItem);

        //Use null parameter when calling main(String[] args).
        final JCheckBoxMenuItem useNullInMainMethodCallMenuItem = new JCheckBoxMenuItem(
                this.useNullInMainMethodCallAction);
        useNullInMainMethodCallMenuItem.setState(jeliotUserProperties
                .getBooleanProperty("use_null_to_call_main"));
        menu.add(useNullInMainMethodCallMenuItem);

        menu.addSeparator();

        //If Jeliot should ask questions during animation
        final JCheckBoxMenuItem enableQuestionAskingMenuItem = new JCheckBoxMenuItem(
                messageBundle.getString("menu.options.ask_questions"),
                jeliotUserProperties.getBooleanProperty("ask_questions"));
        enableQuestionAskingMenuItem.setMnemonic(KeyEvent.VK_Q);
        enableQuestionAskingMenuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_Q, ActionEvent.CTRL_MASK + ActionEvent.ALT_MASK));
        enableQuestionAskingMenuItem.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                jeliotUserProperties.setBooleanProperty("ask_questions",
                        enableQuestionAskingMenuItem.getState());
            }
        });
        menu.add(enableQuestionAskingMenuItem);
        this.editWidgets.add(enableQuestionAskingMenuItem);

        //Show history view        
        final Jeliot j = jeliot;
        final JTabbedPane jtp = this.tabbedPane;
        final int index = jtp.indexOfTab(messageBundle
                .getString("tab.title.history"));

        if (jeliotUserProperties.containsKey("show_history_view")) {
            j.getHistoryView().setEnabled(
                    jeliotUserProperties
                            .getBooleanProperty("show_history_view"));
        } else {
            jeliotUserProperties.setBooleanProperty("show_history_view", j
                    .getHistoryView().isEnabled());
        }
        final JCheckBoxMenuItem enableHistoryViewMenuItem = new JCheckBoxMenuItem(
                messageBundle.getString("menu.options.show_history_view"),
                jeliot.getHistoryView().isEnabled());
        enableHistoryViewMenuItem.setMnemonic(KeyEvent.VK_H);
        enableHistoryViewMenuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_H, ActionEvent.CTRL_MASK + ActionEvent.ALT_MASK));
        enableHistoryViewMenuItem.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                boolean state = enableHistoryViewMenuItem.getState();
                j.getHistoryView().setEnabled(state);
                if (codeNest.getLeftComponent() instanceof CodePane2) {
                    jtp.setEnabledAt(index, state);
                }
                jeliotUserProperties.setBooleanProperty("show_history_view", j
                        .getHistoryView().isEnabled());
            }
        });
        menu.add(enableHistoryViewMenuItem);
        
        final JCheckBoxMenuItem showMJeliotView = new JCheckBoxMenuItem(
                this.showMJeliotViewAction);
        showMJeliotView.setState(jeliotUserProperties
                .getBooleanProperty("show_mjeliot_view"));
        menu.add(showMJeliotView);        

        //Pause on message
        final JCheckBoxMenuItem pauseOnMessageMenuItem = new JCheckBoxMenuItem(
                messageBundle.getString("menu.options.pause_on_message"),
                jeliotUserProperties.getBooleanProperty("pause_on_message"));
        pauseOnMessageMenuItem.setMnemonic(KeyEvent.VK_U);
        pauseOnMessageMenuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_U, ActionEvent.CTRL_MASK + ActionEvent.ALT_MASK));
        pauseOnMessageMenuItem.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                jeliotUserProperties.setBooleanProperty("pause_on_message",
                        pauseOnMessageMenuItem.getState());
            }
        });
        menu.add(pauseOnMessageMenuItem);

        menu.addSeparator();

        //Select font for editor and code pane.        
        JMenuItem menuItem = new JMenuItem(messageBundle
                .getString("menu.options.font_select"));
        menuItem.setMnemonic(KeyEvent.VK_F);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F,
                ActionEvent.CTRL_MASK + ActionEvent.ALT_MASK));
        menuItem.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                Font f = null;
                try {
                    f = JFontChooser.showDialog(frame, editor.getTextArea()
                            .getPainter().getFont());
                } catch (Exception e1) {
                    DebugUtil.handleThrowable(e1);
                    /*
                     if (DebugUtil.DEBUGGING) {
                     jeliot.output(e1.toString());
                     StackTraceElement[] s = e1.getStackTrace();
                     for (int i = 0; i < s.length; i++) {
                     jeliot.output(s[i].toString() + "\n");
                     }
                     }
                     */
                }
                if (f != null) {
                    getCodePane().setFont(f);
                    editor.setFont(f);
                    /*
                     getCodePane().getTextArea().getPainter().setFont(f);
                     propertiesBundle.setFontProperty("font.code_pane", f);
                     editor.getTextArea().getPainter().setFont(f);
                     propertiesBundle.setFontProperty("font.code_editor", f);
                     */
                }
                codeNest.getLeftComponent().requestFocusInWindow();
            }
        });
        menu.add(menuItem);

        return menu;
    }

    /**
     * Menu with the commands to enter to animate and edit.
     */
    private JMenu makeHelpMenu() {

        JMenu menu = new JMenu(messageBundle.getString("menu.help"));
        menu.setMnemonic(KeyEvent.VK_H);
        JMenuItem menuItem;

        menuItem = new JMenuItem(messageBundle.getString("menu.help.help"));
        menuItem.setMnemonic(KeyEvent.VK_H);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0));
        menuItem.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                if (hw != null) {
                    //hw.pack();
                    hw.reload();
                    hw.setVisible(true);
                }
            }
        });
        menu.add(menuItem);

        menuItem = new JMenuItem(messageBundle.getString("menu.help.about"));
        menuItem.setMnemonic(KeyEvent.VK_B);
        //menuItem.setAccelerator(KeyStroke.getKeyStroke(
        //KeyEvent.VK_M, ActionEvent.CTRL_MASK));
        menuItem.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {

                if (aw != null) {
                    //aw.pack();
                    aw.reload();
                    aw.setVisible(true);
                }
            }
        });
        menu.add(menuItem);
        if (jeliot.isExperiment()) {

            menuItem = new JMenuItem("Start Experiment");
            menuItem.setMnemonic(KeyEvent.VK_X);
            menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F2, 0));
            //menuItem.setAccelerator(KeyStroke.getKeyStroke(
            //KeyEvent.VK_M, ActionEvent.CTRL_MASK));

            menuItem.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    Object[] options = { "START" };

                    int n = JOptionPane
                            .showOptionDialog(
                                    frame,
                                    "Click START to proceed with the Experiment and start thinking aloud!",
                                    "Starting experiment",
                                    JOptionPane.OK_OPTION,
                                    JOptionPane.INFORMATION_MESSAGE, null,
                                    options, options[0]);
                    if (n == JOptionPane.OK_OPTION) {
                        Tracker.trackEvent(TrackerClock.currentTimeMillis(),
                                Tracker.OTHER, -1, -1, "Sound");
                        //This is faster but not very noisy hopefully it will be heard.
                        Toolkit.getDefaultToolkit().beep();
                        //The eexperiment is set up to last 15 min
                        Reminder warning = new Reminder(frame, 15 * 60);
                    }
                }
            });

        }
        menu.add(menuItem);

        return menu;
    }

    /**
     * Menu with the VCR commands
     */
    private JMenu makeAnimationMenu() {

        JMenu menu = new JMenu(messageBundle.getString("menu.animation"));
        menu.setMnemonic(KeyEvent.VK_A);
        JMenuItem menuItem;

        menuItem = new JMenuItem(messageBundle.getString("menu.animation.step"));
        menuItem.setMnemonic(KeyEvent.VK_S);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0));
        menuItem.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                stepButton.doClick();
            }
        });
        menu.add(menuItem);

        menuItem = new JMenuItem(messageBundle.getString("menu.animation.play"));
        menuItem.setMnemonic(KeyEvent.VK_L);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L,
                ActionEvent.CTRL_MASK));
        menuItem.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                playButton.doClick();
            }
        });
        menu.add(menuItem);

        menuItem = new JMenuItem(messageBundle
                .getString("menu.animation.pause"));
        menuItem.setMnemonic(KeyEvent.VK_U);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_U,
                ActionEvent.CTRL_MASK));
        menuItem.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                pauseButton.doClick();
            }
        });
        menu.add(menuItem);

        menuItem = new JMenuItem(messageBundle
                .getString("menu.animation.rewind"));
        menuItem.setMnemonic(KeyEvent.VK_R);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R,
                ActionEvent.CTRL_MASK));
        menuItem.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                rewindButton.doClick();
            }
        });
        menu.add(menuItem);

        menuItem = new JMenuItem(messageBundle
                .getString("menu.animation.faster"));
        menuItem.setMnemonic(KeyEvent.VK_F);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F,
                ActionEvent.CTRL_MASK));
        menuItem.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                speedSlider.setValue(speedSlider.getValue() + 1);
            }
        });
        menu.add(menuItem);

        menuItem = new JMenuItem(messageBundle
                .getString("menu.animation.slower"));
        menuItem.setMnemonic(KeyEvent.VK_O);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S,
                ActionEvent.CTRL_MASK));
        menuItem.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                speedSlider.setValue(speedSlider.getValue() - 1);
            }
        });
        menu.add(menuItem);

        menuItem = new JMenuItem(messageBundle
                .getString("menu.animation.run_until"));
        menuItem.setMnemonic(KeyEvent.VK_T);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_T,
                ActionEvent.CTRL_MASK));
        menuItem.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                runUntil();
            }
        });
        menu.add(menuItem);

        menu.addSeparator();

        menuItem = new JMenuItem(messageBundle
                .getString("menu.animation.print"));
        menuItem.setMnemonic(KeyEvent.VK_P);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P,
                ActionEvent.CTRL_MASK));
        menuItem.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                JComponent component = JeliotWindow.this.getTheaterPane();
                if (component instanceof JTabbedPane) {
                    Component componentForPrinting = ((JTabbedPane) component)
                            .getComponentAt(((JTabbedPane) component)
                                    .getSelectedIndex());
                    if (componentForPrinting instanceof JComponent) {
                        if (componentForPrinting instanceof JScrollPane) {
                            JScrollPane jsr = (JScrollPane) componentForPrinting;
                            Component comp = (Component) jsr.getViewport()
                                    .getView();
                            if (comp instanceof Theater) {
                                Theater t = (Theater) comp;
                                Rectangle r = new Rectangle();
                                r.height = t.getPreferredSize().height;
                                r.width = t.getPreferredSize().width;
                                PrintingUtil.printComponent(t, r);
                            } else if (comp instanceof TreeDraw) {
                                TreeDraw t = (TreeDraw) comp;
                                Rectangle r = new Rectangle();
                                r.height = t.getPreferredSize().height;
                                r.width = t.getPreferredSize().width;
                                PrintingUtil.printComponent(t, r);
                            }
                        } else if (componentForPrinting instanceof HistoryView) {
                            HistoryView h = (HistoryView) componentForPrinting;
                            Rectangle r = new Rectangle();
                            h.getImageCanvas().repaint();
                            r.height = h.getImageCanvas().getPreferredSize().height;
                            r.width = h.getImageCanvas().getPreferredSize().width;
                            PrintingUtil.printComponent(h.getImageCanvas(), r);
                        }
                    }
                }
            }
        });
        menu.add(menuItem);

        return menu;
    }

    /**
     * Menu with the commands to enter to animate and edit.
     */
    private JMenu makeControlMenu() {

        JMenu menu = new JMenu(messageBundle.getString("menu.control"));
        menu.setMnemonic(KeyEvent.VK_C);
        JMenuItem menuItem;

        menuItem = new JMenuItem(messageBundle.getString("menu.control.edit"));
        menuItem.setMnemonic(KeyEvent.VK_E);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E,
                ActionEvent.CTRL_MASK));
        menuItem.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                editButton.doClick();
            }
        });
        menu.add(menuItem);

        animWidgets.addElement(menuItem);

        menuItem = new JMenuItem(messageBundle
                .getString("menu.control.compile"));
        menuItem.setMnemonic(KeyEvent.VK_M);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_M,
                ActionEvent.CTRL_MASK));
        menuItem.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                compileButton.doClick();
            }
        });
        menu.add(menuItem);

        editWidgets.addElement(menuItem);

        return menu;
    }

    /**
     * Makes the control buttons for the control panel.
     * 
     * @param label
     *            The label for the button.
     * @param iconName
     *            The icon name for the icon on the button.
     * @return The control button for control panel.
     */
    private JButton makeControlButton(String label, String iconName) {

        URL imageURL = this.getClass().getClassLoader().getResource(
                propertiesBundle.getStringProperty("directory.images")
                        + iconName);
        if (imageURL == null) {
            imageURL = Thread.currentThread().getContextClassLoader()
                    .getResource(
                            propertiesBundle
                                    .getStringProperty("directory.images")
                                    + iconName);
        }
        ImageIcon icon = new ImageIcon(imageURL);
        //new ImageIcon(bundle.getString("directory.images")+ iconName);
        JButton b = new JButton(label, icon);
        b.setVerticalTextPosition(AbstractButton.BOTTOM);
        b.setHorizontalTextPosition(AbstractButton.CENTER);
        //  b.setBorder(BorderFactory.createEtchedBorder());
        b.setMargin(new Insets(0, 0, 0, 0));
        return b;
    }

    /**
     * Constructs the control panel. Uses makeControlButton(String, String)
     * 
     * @return The constructed control panel.
     * @see #makeControlButton(String, String)
     */
    private JPanel makeControlPanel() {

        editButton = makeControlButton(messageBundle.getString("button.edit"),
                propertiesBundle.getStringProperty("image.edit_icon"));
        compileButton = makeControlButton(messageBundle
                .getString("button.compile"), propertiesBundle
                .getStringProperty("image.compile_icon"));
        
        editButton.setMnemonic(KeyEvent.VK_E);
        compileButton.setMnemonic(KeyEvent.VK_M);
        
        editButton.setMargin(new Insets(0, 2, 0, 2));
        compileButton.setMargin(new Insets(0, 2, 0, 2));

        editButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                Tracker.trackEvent(TrackerClock.currentTimeMillis(),
                        Tracker.BUTTON, -1, -1, "EditButton");
                enterEdit();
            }
        });

        compileButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                Tracker.trackEvent(TrackerClock.currentTimeMillis(),
                        Tracker.BUTTON, -1, -1, "AnimationButton");
                tryToEnterAnimate();
            }
        });


        editWidgets.addElement(compileButton);
        animWidgets.addElement(editButton);


        JPanel statePane = new JPanel();
        statePane.setLayout(new GridLayout(1, 3));
        statePane.add(editButton);
        statePane.add(compileButton);

        // create animation control buttons
        // stepButton = makeControlButton("Step", "stepicon.gif");
        // playButton = makeControlButton("Play", "playicon.gif");
        // pauseButton = makeControlButton("Pause", "pauseicon.gif");
        // rewindButton = makeControlButton("Rewind", "rewindicon.gif");

        stepButton = makeControlButton(messageBundle.getString("button.step"),
                propertiesBundle.getStringProperty("image.step_icon"));
        stepButton.setMnemonic(KeyEvent.VK_S);
        playButton = makeControlButton(messageBundle.getString("button.play"),
                propertiesBundle.getStringProperty("image.play_icon"));
        playButton.setMnemonic(KeyEvent.VK_P);
        pauseButton = makeControlButton(
                messageBundle.getString("button.pause"), propertiesBundle
                        .getStringProperty("image.pause_icon"));
        pauseButton.setMnemonic(KeyEvent.VK_U);
        rewindButton = makeControlButton(messageBundle
                .getString("button.rewind"), propertiesBundle
                .getStringProperty("image.rewind_icon"));
        rewindButton.setMnemonic(KeyEvent.VK_R);

        stepButton.addActionListener(stepAction);
        playButton.addActionListener(playAction);
        pauseButton.addActionListener(pauseAction);
        rewindButton.addActionListener(rewindAction);

        animWidgets.addElement(stepButton);
        animWidgets.addElement(playButton);
        animWidgets.addElement(pauseButton);
        animWidgets.addElement(rewindButton);
        animWidgets.addElement(interactButton);
        
        // MOE
        this.interactButton = new InteractButton(this.jeliot, propertiesBundle, messageBundle);
        this.interactButton.setMargin(new Insets(0, 2, 0, 2));
        this.interactButton.setEnabled(true);
        // /MOE
        // create animation speed control slider
        speedSlider = new JSlider(JSlider.HORIZONTAL, 1, 100, 10);
        speedSlider.setMajorTickSpacing(10);
        speedSlider.setMinorTickSpacing(5);
        speedSlider.setPaintTicks(true);
        speedSlider.setPaintLabels(false);

        speedSlider.addChangeListener(new ChangeListener() {

            public void stateChanged(ChangeEvent e) {
                int volume = speedSlider.getValue();
                engine.setVolume(volume * 50.0);
                Tracker.trackEvent(TrackerClock.currentTimeMillis(),
                        Tracker.OTHER, -1, -1, "Slider:" + volume);
            }
        });

        animWidgets.addElement(speedSlider);

        JPanel bp = new JPanel();
        bp.setLayout(new GridLayout(1, 5));
        bp.add(stepButton);
        bp.add(playButton);
        bp.add(pauseButton);
        bp.add(rewindButton);
        // MOE
        bp.add(interactButton);
        // /MOE
        
        JPanel p = new JPanel();
        GridBagLayout pl = new GridBagLayout();
        p.setLayout(pl);

        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.insets = new Insets(0, 0, 4, 8);
        pl.setConstraints(statePane, c);
        p.add(statePane);

        URL imageURL = this.getClass().getClassLoader().getResource(
                propertiesBundle.getStringProperty("directory.images")
                        + propertiesBundle.getStringProperty("image.jeliot"));
        if (imageURL == null) {
            imageURL = Thread.currentThread().getContextClassLoader()
                    .getResource(
                            propertiesBundle
                                    .getStringProperty("directory.images")
                                    + propertiesBundle
                                            .getStringProperty("image.jeliot"));
        }

        JLabel jicon = new JLabel(new ImageIcon(imageURL));
        c.gridx = 0;
        c.gridy = 1;
        c.gridheight = 2;
        c.insets = new Insets(0, 0, 0, 0);
        pl.setConstraints(jicon, c);
        p.add(jicon);

        c.gridx = 1;
        c.gridy = 0;
        c.gridheight = 1;
        c.gridwidth = 2;
        c.fill = GridBagConstraints.BOTH;
        c.insets = new Insets(0, 0, 4, 0);
        pl.setConstraints(bp, c);
        p.add(bp);

        c.gridy = 1;
        c.gridwidth = 1;
        c.insets = new Insets(0, 0, 0, 0);
        c.anchor = GridBagConstraints.WEST;
        JLabel label = new JLabel(messageBundle
                .getString("label.animation_speed1"));
        pl.setConstraints(label, c);
        p.add(label);

        c.gridy = 2;
        label = new JLabel(messageBundle.getString("label.animation_speed2"));
        pl.setConstraints(label, c);
        p.add(label);

        c.gridx = 2;
        c.gridy = 1;
        c.gridheight = 2;
        pl.setConstraints(speedSlider, c);
        p.add(speedSlider);

        int bottomBorder = 2;
        if (jeliot.isExperiment()) {
            bottomBorder = 40;
        }
        p.setBorder(BorderFactory.createCompoundBorder(BorderFactory
                .createEmptyBorder(5, 2, bottomBorder, 10), BorderFactory
                .createEtchedBorder()));

        return p;
    }

	/**
     * Enables or disables the components depending on the second parameter.
     * 
     * @param enum
     *            The collection of components that are set enabled or disabled
     *            as the boolean enable is set.
     * @param enable
     *            Sets wheter the components are enabled or disabled.
     */
    private void enableWidgets(Enumeration enumeration, boolean enable) {
        while (enumeration.hasMoreElements()) {
            Object obj = enumeration.nextElement();
            if (obj instanceof Component) {
                Component comp = (Component) obj;
                comp.setEnabled(enable);
            } else if (obj instanceof Action) {
                Action act = (Action) obj;
                act.setEnabled(enable);
            }
        }
    }

    /**
     * Changes the code pane in the codeNest. Sets inside the codeNest the new
     * code pane.
     * 
     * @param comp
     *            The component that is changes in the code pane.
     */
    private void changeCodePane(JComponent comp) {

        int loc = codeNest.getDividerLocation();
        codeNest.setLeftComponent(comp);
        codeNest.setDividerLocation(loc);
        conPan.requestFocusInWindow();
    }

    /**
     * Changes the theater pane in the codeNest. Sets inside the codeNest the
     * new theater pane.
     * 
     * @param comp
     *            The component that is changes in the theater pane.
     */
    private void changeTheatrePane(JComponent comp) {

        int loc = codeNest.getDividerLocation();
        codeNest.setRightComponent(comp);
        codeNest.setDividerLocation(loc);
        conPan.requestFocusInWindow();
    }

    /**
     * 
     * @return
     */
    public JComponent getTheaterPane() {
        return (JComponent) codeNest.getRightComponent();
    }

    /**
     * This method is called when user clicks the "Edit" button.
     */
    void enterEdit() {

        //enableWidgets(editWidgets.elements(), true);
        enableWidgets(animWidgets.elements(), false);

        tabbedPane.setSelectedIndex(0);
        changeTheatrePane(tabbedPane);
        unhighlightTabTitles();
        callTree.initialize();
        int n = tabbedPane.getTabCount();
        for (int i = 1; i < n; i++) {
            tabbedPane.setEnabledAt(i, false);
        }
        
        this.runUntilLine = -1;
        if (runningUntil) {
            jeliot.runUntil(0);
            runUntilFinished();
        }
        
        panelController.slide(false, new Runnable() {

            public void run() {
                SwingUtilities.invokeLater(new Runnable() {

                    public void run() {
                        //jeliot.stopThreads();
                        enterEditTrue();
                    }
                });
            }
        }).start();
    }

    /**
     * Makes the user interface changes when user clicks the "Edit" button.
     */
    public void enterEditTrue() {
        jeliot.stopThreads();
        changeCodePane(editor);
        editor.requestFocusInWindow();
        enableWidgets(editWidgets.elements(), true);
        enableWidgets(animWidgets.elements(), false);
    }

    /**
     * Returns the program code from the CodeEditor -object.
     * 
     * @return The program code from the CodeEditor -object.
     */
    public String getProgram() {
        return editor.getProgram();
    }

    /**
     *
     */
    public void tryToEnterAnimate() {

        tryToEnterAnimate(null);
    }

    /**
     * Called when the user pushes the "Compile" button.
     * Gets the code from the CodeEditor2 -object.
     * Sends it to "compilation".
     */
    public void tryToEnterAnimate(String methodCall) {

        // Jeliot 3
        if (editor.isChanged()
                && this.jeliotUserProperties
                        .getBooleanProperty("save_automatically")) {
            editor.saveProgram();
        }

        try {
            enableWidgets(editWidgets.elements(), false);
            String programCode = editor.getProgram();

            final int line = editor.getTextArea().getVerticalScrollBar()
                    .getValue();
            //System.out.println(line);

            if (methodCall == null) {
                methodCall = SourceCodeUtilities
                        .findMainMethodCall(
                                programCode,
                                jeliotUserProperties
                                        .getBooleanProperty("ask_for_main_method_parameters"),
                                this.frame,
                                jeliotUserProperties
                                        .getBooleanProperty("use_null_to_call_main"));
                if (jeliotUserProperties.getBooleanProperty("ask_for_method")
                        || methodCall == null) {
                    methodCall = ((methodCall != null) ? methodCall : null);
                    String inputValue = JOptionPane
                            .showInputDialog(
                                    (methodCall != null) ? messageBundle
                                            .getString("dialog.ask_for_method")
                                            + "\n"
                                            + messageBundle
                                                    .getString("dialog.ask_for_method_extended_info")
                                            : messageBundle
                                                    .getString("dialog.ask_for_method_when_main_method_not_found")
                                                    + "\n"
                                                    + messageBundle
                                                            .getString("dialog.ask_for_method")
                                                    + "\n"
                                                    + messageBundle
                                                            .getString("dialog.ask_for_method_extended_info"),
                                    methodCall);
                    if (inputValue != null && !inputValue.trim().equals("")) {
                        methodCall = inputValue + ";";
                    }
                }
            }

            if (methodCall != null) {

                //Reader r = new BufferedReader(new
                // StringReader(programCode));
                //jeliot.createLauncher(r);
                //Reader s = new BufferedReader(new
                // StringReader(methodCall));

                changeTheatrePane(tabbedPane);
                tabbedPane.setSelectedIndex(0);
                jeliot.setSourceCode(programCode, methodCall);
                panelController.slide(true, new Runnable() {

                    public void run() {
                        SwingUtilities.invokeLater(new Runnable() {

                            public void run() {

                                enterAnimate(line);
                                //Buttons are enables just after the animation mode is not before
                                enableWidgets(animWidgets.elements(), true);
                                pauseButton.setEnabled(false);
                                rewindButton.setEnabled(false);

                                int index = tabbedPane.indexOfTab(messageBundle
                                        .getString("tab.title.call_tree"));
                                if (index > 0) {
                                    tabbedPane.setEnabledAt(index, true);
                                }
                                index = tabbedPane.indexOfTab(messageBundle
                                        .getString("tab.title.history"));
                                if (index > 0) {
                                    tabbedPane.setEnabledAt(index, jeliot
                                            .getHistoryView().isEnabled());
                                }
                            }
                        });
                    }
                }).start();
            } else {

                this.showErrorMessage(messageBundle
                        .getString("main_method_not_found.exception"));

                enableWidgets(editWidgets.elements(), true);
                enableWidgets(animWidgets.elements(), false);
            }
            /*
             * catch (InterpreterException e) { showErrorMessage(e); return; }
             * catch (SemanticException e) { showErrorMessage(e); return; }
             * catch (SyntaxErrorException e) { showErrorMessage(e); return; }
             */
        } catch (Exception e) {
            editButton.doClick();
            if (DebugUtil.DEBUGGING) {
                e.printStackTrace();
            }
        }
    }
    /*
     * public String replaceChar(String from, char c, String with) {
     * 
     * int index = from.indexOf(c); while(index != -1) { from =
     * from.substring(0,index) + with + from.substring(index+1,from.length());
     * index = from.indexOf(c); } return from; }
     */

    /**
     * Shows the given error message and sets the buttons and menuitems for
     * animation as disabled.
     * 
     * @param e
     *            the error message in String
     */
    public void showErrorMessageDuringAnimation(String e) {
        errorOccured = true;

        pauseButton.setEnabled(false);
        editButton.setEnabled(true);
        stepButton.setEnabled(false);
        playButton.setEnabled(false);
        pauseButton.setEnabled(false);
        rewindButton.setEnabled(true);

        String[] s1 = { messageBundle.getString("menu.control.edit"),
                messageBundle.getString("menu.animation.rewind") };
        setEnabledMenuItems(true, s1);
        String[] s2 = { messageBundle.getString("menu.animation.step"),
                messageBundle.getString("menu.animation.play"),
                messageBundle.getString("menu.animation.run_until"),
                messageBundle.getString("menu.animation.pause") };
        setEnabledMenuItems(false, s2);

        showErrorMessage(e);
    }

    /**
     * Shows the error message in the errorViewer in the right hand side pane.
     * 
     * @param e Error message
     */
    public void showErrorMessage(String e) {
        errorJEditorPane.setRequestFocusEnabled(false);
        errorJEditorPane.setText(e);
        errorJEditorPane.setCaretPosition(0);
        errorJEditorPane.setRequestFocusEnabled(true);
        changeTheatrePane(errorViewer);
    }

    /**
     * Shows the errormessage and highlights the source code.
     * 
     * @param e
     *            interpreter error that contains the error message and the
     *            highlighting information.
     * @see JeliotWindow#showErrorMessageDuringAnimation(String)
     */
    public void showErrorMessageDuringAnimation(InterpreterError e) {

        showErrorMessageDuringAnimation(e.getMessage());
        Component c = codeNest.getLeftComponent();

        if (e.getHighlight() != null) {
            if (c instanceof CodeEditor2) {
                ((CodeEditor2) c).highlight(e.getHighlight());
            }
            if (c instanceof CodePane2) {
                ((CodePane2) c).highlightStatement(e.getHighlight());
            }
        }
    }

    /**
     * Sets the given menu items contained in the second parameter either
     * enabled or disabled depending the value of the first parameter
     * 
     * @param enabled
     *            if true means that the given menu items should be enabled if
     *            false the menu items should be disabled.
     * @param menuItems
     *            the menu items to be enabled or disabled.
     */
    public void setEnabledMenuItems(boolean enabled, String[] menuItems) {
        for (int i = 0; i < animationMenuItems.size(); i++) {
            if (animationMenuItems.elementAt(i) instanceof JMenuItem) {
                JMenuItem jmi = (JMenuItem) animationMenuItems.elementAt(i);
                if (jmi != null) {
                    for (int j = 0; j < menuItems.length; j++) {
                        if (menuItems[j].equals(jmi.getText())) {
                            jmi.setEnabled(enabled);
                        }
                    }
                }
            } else if (animationMenuItems.elementAt(i) instanceof Action) {
                Action act = (Action) animationMenuItems.elementAt(i);
                if (act != null) {
                    for (int j = 0; j < menuItems.length; j++) {
                        if (menuItems[j].equals(act.getValue(Action.NAME))) {
                            act.setEnabled(enabled);
                        }
                    }
                }
            }
        }
    }

    /**
     * Changes the user interface when the "Compile" button is pressed. Rewinds
     * the animation.
     */
    public void enterAnimate(final int line) {
        //enableWidgets(editWidgets.elements(), false);
        //enableWidgets(animWidgets.elements(), true);
        changeCodePane(codePane);
        outputConsole.setText("");
        new Thread() {
            public void run() {
                while (!codePane.getTextArea().isScrollBarsInitialized()) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                    }
                }
                codePane.getTextArea().getVerticalScrollBar().setValue(line);
            }
        }.start();
        rewindAnimation();
    }

    /**
     * Changes the user interface when the "Step" button is pressed. Calls
     * jeliot.step() method.
     * 
     * @see jeliot.Jeliot#step()
     */
    public void stepAnimation() {

        rewindButton.setEnabled(false);
        editButton.setEnabled(false);
        playButton.setEnabled(false);
        stepButton.setEnabled(false);
        pauseButton.setEnabled(true);

        String[] s1 = { messageBundle.getString("menu.animation.pause") };
        setEnabledMenuItems(true, s1);
        String[] s2 = { messageBundle.getString("menu.animation.step"),
                messageBundle.getString("menu.animation.play"),
                messageBundle.getString("menu.animation.rewind"),
                messageBundle.getString("menu.control.edit"),
                messageBundle.getString("menu.animation.run_until") };
        setEnabledMenuItems(false, s2);

        try {
            jeliot.step();
        } catch (Exception e) {
        }
    }

    /**
     * Changes the user interface when the "Play" button is pressed. Calls
     * jeliot.play() method.
     * 
     * @see jeliot.Jeliot#play()
     */
    public void playAnimation() {

        stepButton.setEnabled(false);
        playButton.setEnabled(false);
        pauseButton.setEnabled(true);
        rewindButton.setEnabled(false);
        editButton.setEnabled(false);

        String[] s1 = { messageBundle.getString("menu.animation.pause") };
        setEnabledMenuItems(true, s1);
        String[] s2 = { messageBundle.getString("menu.animation.step"),
                messageBundle.getString("menu.animation.play"),
                messageBundle.getString("menu.animation.rewind"),
                messageBundle.getString("menu.control.edit"),
                messageBundle.getString("menu.animation.run_until") };
        setEnabledMenuItems(false, s2);

        try {
            jeliot.play();
        } catch (Exception e) {
        }
    }

    /**
     * Changes the user interface when the "Pause" button is pressed. Calls
     * jeliot.pause() method.
     * 
     * @see jeliot.Jeliot#pause()
     */
    public void pauseAnimation() {

        pauseButton.setEnabled(false);

        String[] s1 = { messageBundle.getString("menu.animation.pause") };
        setEnabledMenuItems(false, s1);

        try {
            jeliot.pause();
        } catch (Exception e) {
        }

    }

    /**
     * Changes the user interface when animation is resumed, for example,
     * after input request.
     */
    public void resumeAnimation() {

        stepButton.setEnabled(false);
        playButton.setEnabled(false);
        pauseButton.setEnabled(true);
        rewindButton.setEnabled(false);
        editButton.setEnabled(false);

        String[] s1 = { messageBundle.getString("menu.animation.pause") };
        setEnabledMenuItems(true, s1);
        String[] s2 = { messageBundle.getString("menu.animation.step"),
                messageBundle.getString("menu.animation.play"),
                messageBundle.getString("menu.animation.rewind"),
                messageBundle.getString("menu.control.edit"),
                messageBundle.getString("menu.animation.run_until") };
        setEnabledMenuItems(false, s2);
    }

    /**
     * Changes the user interface when the animation is freezed.
     */
    public void freezeAnimation() {

        stepButton.setEnabled(false);
        playButton.setEnabled(false);
        pauseButton.setEnabled(false);
        rewindButton.setEnabled(false);
        editButton.setEnabled(true);

        String[] s1 = { messageBundle.getString("menu.control.edit") };
        setEnabledMenuItems(true, s1);
        String[] s2 = { messageBundle.getString("menu.animation.step"),
                messageBundle.getString("menu.animation.play"),
                messageBundle.getString("menu.animation.rewind"),
                messageBundle.getString("menu.animation.pause"),
                messageBundle.getString("menu.animation.run_until") };
        setEnabledMenuItems(false, s2);
    }

    /**
     * Changes the user interface when the "Rewind" button is pressed. Calls
     * methods jeliot.rewind() and theater.repaint().
     * 
     * @see jeliot.Jeliot#rewind()
     * @see jeliot.theatre.Theatre#repaint()
     */
    public void rewindAnimation() {

        tabbedPane.setSelectedIndex(0);
        changeTheatrePane(tabbedPane);

        String[] s1 = { messageBundle.getString("menu.animation.step"),
                messageBundle.getString("menu.animation.play"),
                messageBundle.getString("menu.animation.run_until"),
                messageBundle.getString("menu.animation.rewind"),
                messageBundle.getString("menu.control.edit"),
                messageBundle.getString("menu.animation.pause") };
        setEnabledMenuItems(false, s1);

        editButton.setEnabled(false);
        stepButton.setEnabled(false);
        playButton.setEnabled(false);
        pauseButton.setEnabled(false);
        rewindButton.setEnabled(false);
        // MOE
        interactButton.reset();
        // /MOE
        
        unhighlightTabTitles();

        errorOccured = false;

        jeliot.cleanUp();
        theater.repaint();
        outputConsole.setText("");

        SwingUtilities.invokeLater(new Runnable() {

            public void run() {
                jeliot.stopThreads();
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                jeliot.compile();

                jeliot.rewind();
                theater.repaint();

                editButton.setEnabled(true);
                stepButton.setEnabled(true);
                playButton.setEnabled(true);
                pauseButton.setEnabled(false);
                rewindButton.setEnabled(false);

                if (runningUntil) {
                    //TODO: Here ask if the user wants to continue with run until.
                    jeliot.runUntil(0);
                    runUntilFinished();
                    new Thread() {
                        public void run() {
                            while (!codePane.getTextArea().isScrollBarsInitialized()) {
                                try {
                                    Thread.sleep(100);
                                } catch (InterruptedException e) {
                                }
                            }
                            codePane.getTextArea().getVerticalScrollBar().setValue(runUntilLine);
                            runUntil();
                        }
                    }.start();
                } else if (runUntilLine > 0) {
                    new Thread() {
                        public void run() {
                            while (!codePane.getTextArea().isScrollBarsInitialized()) {
                                try {
                                    Thread.sleep(100);
                                } catch (InterruptedException e) {
                                }
                            }
                            codePane.getTextArea().getVerticalScrollBar().setValue(runUntilLine);
                            runUntil();
                        }
                    }.start();
                }
                
                String[] s2 = { messageBundle.getString("menu.animation.step"),
                        messageBundle.getString("menu.animation.play"),
                        messageBundle.getString("menu.control.edit"),
                        messageBundle.getString("menu.animation.run_until") };
                setEnabledMenuItems(true, s2);
                String[] s3 = {
                        messageBundle.getString("menu.animation.rewind"),
                        messageBundle.getString("menu.animation.pause") };
                setEnabledMenuItems(false, s3);
            }
        });
    }

    /**
     * Changes the user interface when the animation is finished.
     */
    public void animationFinished() {
        if (!errorOccured) {

            stepButton.setEnabled(false);
            playButton.setEnabled(false);
            pauseButton.setEnabled(false);
            rewindButton.setEnabled(true);
            editButton.setEnabled(true);

            String[] s1 = { messageBundle.getString("menu.control.edit"),
                    messageBundle.getString("menu.animation.rewind") };
            setEnabledMenuItems(true, s1);
            String[] s2 = { messageBundle.getString("menu.animation.step"),
                    messageBundle.getString("menu.animation.play"),
                    messageBundle.getString("menu.animation.run_until"),
                    messageBundle.getString("menu.animation.pause") };
            setEnabledMenuItems(false, s2);

        } else {

            editButton.setEnabled(true);
            stepButton.setEnabled(false);
            playButton.setEnabled(false);
            pauseButton.setEnabled(false);
            rewindButton.setEnabled(true);

            String[] s1 = { messageBundle.getString("menu.control.edit"),
                    messageBundle.getString("menu.animation.rewind") };
            setEnabledMenuItems(true, s1);
            String[] s2 = { messageBundle.getString("menu.animation.step"),
                    messageBundle.getString("menu.animation.play"),
                    messageBundle.getString("menu.animation.run_until"),
                    messageBundle.getString("menu.animation.pause") };
            setEnabledMenuItems(false, s2);
        }
    }

    /**
     * Get the showMessagesInDialogs variables value
     * 
     * @return
     */
    public boolean showMessagesInDialogs() {
        return jeliotUserProperties.getBooleanProperty("pause_on_message");
    }

    /**
     * Writes the outputted string to the output console.
     * 
     * @param str
     *            String for output.
     */
    public void output(String str) {
        //System.out.println("This is output: " + str);
        outputConsole.append(str);
        outputConsole.setCaretPosition(outputConsole.getText().length());
    }

    /**
     * Method is used to implement the run until feature.
     */
    public void runUntil() {
        String inputValue = JOptionPane.showInputDialog(this.frame,
                messageBundle.getString("dialog.run_until"),
                runUntilLine > 0 ? new Integer(runUntilLine) : new Integer(0));
        int lineNumber = 0;

        try {
            lineNumber = Integer.parseInt(inputValue);
        } catch (Exception ex) {
        }

        if (lineNumber > 0) {
            this.runUntilLine = lineNumber;
            jeliot.runUntil(lineNumber);
            previousSpeed = speedSlider.getValue();
            speedSlider.setValue(speedSlider.getMaximum());

            //TODO: move these to Director as they are not a GUI's concern
            previousDefaultDuration = Animation.defaultDuration;
            Animation.defaultDuration = 1;
            Animation.disableDurationChanging = true;

            runningUntil = true;
            codePane.highlightLineNumber(lineNumber);
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    playButton.doClick();
                }
            });
        } else {
            runUntilLine = -1;
            if (runningUntil) {
                jeliot.runUntil(0);
                runUntilFinished();
            }
        }
    }

    /**
     * 
     *
     */
    public void runUntilFinished() {
        codePane.highlightLineNumber(-1);
        speedSlider.setValue(previousSpeed);

        //TODO: move these to Director as they are not a GUI's concern
        Animation.defaultDuration = previousDefaultDuration;
        Animation.disableDurationChanging = false;

        runningUntil = false;
    }

    /**
     * Invoked when the runUntil is done.
     */
    public void runUntilDone() {
        runUntilFinished();
        try {
            SwingUtilities.invokeAndWait(new Runnable() {
                public void run() {
                    pauseButton.doClick();
                }
            });
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    /**
     * Sets program to the editor pane.
     * 
     * @param program The program in String
     */
    public void setProgram(String program) {
        if (editButton.isEnabled()) {
            editButton.doClick();
        }
        editor.setProgram(program);
    }

    /**
     * Set the program in the file to the editor pane.
     * 
     * @param f The program file
     */
    public void setProgram(File f) {
        editor.loadProgram(f);
    }

    /**
     * Returns the code from the CodePane.
     * 
     * @return
     */
    public CodePane2 getCodePane() {
        return codePane;
    }

    /**
     * Highlight the given tabs text if the highlight parameter is true
     * otherwise sets it to the default color of the JTabbedPane
     * 
     * @param highlight
     * @param tabNumber
     */
    public void highlightTabTitle(boolean highlight, int tabNumber) {
        try {
            if (highlight) {
                if (tabbedPane.getSelectedIndex() != tabNumber) {
                    tabbedPane.setForegroundAt(tabNumber, highlightTabColor);
                }
            } else {
                tabbedPane.setForegroundAt(tabNumber, normalTabColor);
            }
        } catch (Exception e) {
        }
    }

    /**
     * 
     * @param title
     * @return
     */
    public int getTabNumber(String title) {
        return tabbedPane.indexOfTab(title);
    }

    /**
     * 
     *
     */
    public void unhighlightTabTitles() {
        int n = tabbedPane.getTabCount();
        for (int i = 0; i < n; i++) {
            tabbedPane.setForegroundAt(i, normalTabColor);
        }
    }

    public void paused() {
        stepButton.setEnabled(true);
        playButton.setEnabled(true);
        pauseButton.setEnabled(false);
        rewindButton.setEnabled(true);
        editButton.setEnabled(true);

        String[] s1 = { messageBundle.getString("menu.animation.pause") };
        setEnabledMenuItems(false, s1);
        String[] s2 = { messageBundle.getString("menu.animation.step"),
                messageBundle.getString("menu.animation.play"),
                messageBundle.getString("menu.animation.rewind"),
                messageBundle.getString("menu.control.edit"),
                messageBundle.getString("menu.animation.run_until") };
        setEnabledMenuItems(true, s2);
        Tracker.trackEvent(TrackerClock.currentTimeMillis(), Tracker.OTHER, -1,
                -1, "AnimationStopped");

    }

    // These methods are for testing
    public boolean isAskingQuestions() {
        return jeliotUserProperties.getBooleanProperty("ask_questions");
    }

    public JButton getPlayButton() {
        return playButton;
    }

    public JButton getRewindButton() {
        return rewindButton;
    }

    public JButton getEditButton() {
        return editButton;
    }
    // MOE
	public JButton getInteractButton() {
		return interactButton;
	}
	// /MOE	
    public JSlider getSpeedSlider() {
        return speedSlider;
    }

    /**
     * @return Returns the selectedIndexInTabbedPane.
     */
    public int getSelectedIndexInTabbedPane() {
        return selectedIndexInTabbedPane;
    }

    /* (non-Javadoc)
     * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
     */
    public void mouseClicked(MouseEvent arg0) {
    }

    /* (non-Javadoc)
     * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
     */
    public void mouseEntered(MouseEvent arg0) {
    }

    /* (non-Javadoc)
     * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
     */
    public void mouseExited(MouseEvent arg0) {
    }

    /* (non-Javadoc)
     * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
     */
    public void mousePressed(MouseEvent arg0) {
        Tracker.trackEvent(TrackerClock.currentTimeMillis(),
                Tracker.MOUSEBUTTON, arg0.getX(), arg0.getY(), "MouseButton "
                        + arg0.getButton() + " pressed");
    }

    /* (non-Javadoc)
     * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
     */
    public void mouseReleased(MouseEvent arg0) {
    }
    public CodeEditor2 getCodeEditor() {
    	return this.editor;
    }

    public void setUserSelectionEnabled(boolean b) {
    	this.tabbedPane.setEnabledAt(this.tabbedPane.indexOfComponent(this.userSelectionPane), b);
    }
	public void bringUserSelectionToForeground() {
		
		this.tabbedPane.setSelectedComponent(this.userSelectionPane);
	}
	public void bringTheaterToForeground() {
		this.tabbedPane.setSelectedComponent(this.theaterScrollPane);
	}
}