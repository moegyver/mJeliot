package jeliot.gui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ResourceBundle;

import javax.swing.BorderFactory;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.TitledBorder;

import jeliot.util.ResourceBundles;
import jeliot.util.UserProperties;

/**
 * OutputConsole is a text area on which the output of a user's
 * program is printed.
 *
 * @author Pekka Uronen
 * @author Niko Myller
 */
public class OutputConsole extends JTextArea {

    /**
     * The resource bundle for gui package
     */
    static private UserProperties propertiesBundle = ResourceBundles.getGuiUserProperties();
    
    /**
     * The resource bundle for gui package
     */
    static private ResourceBundle messageBundle = ResourceBundles.getGuiMessageResourceBundle();


    /** A scroll pane that contains the output console. */
    public final JScrollPane container = new JScrollPane(this) {

            // If these methods didn't exist, the preferred size of
            // the console would grow as it is filled with text.
            // This would result to an ugly layout when the window is
            // resized. Hence this little hack.
            public Dimension getMaximumSize() {
                Dimension sms = super.getMaximumSize();
                return new Dimension(
                        sms.width,
                        model == null ?
                                sms.height :
                                model.getMaximumSize().height
                );
            }

            public Dimension getPreferredSize() {
                Dimension sps = super.getPreferredSize();
                return new Dimension(
                        sps.width,
                        model == null ?
                                sps.height :
                                model.getPreferredSize().height
                );
            }
        };

    /** A component that is queried for the preferred and maximum height of the console. */
    private Component model;

    /** The console's popup menu has one choice for emptying the console. */
    private JPopupMenu menu = new JPopupMenu(); {
        JMenuItem menuItem;
        menuItem = new JMenuItem(messageBundle.getString("popup_menu.clear"));
        menu.add(menuItem);
        menuItem.addActionListener(
            new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    setText("");
                }
            }
        );
    }

    /** Creates a new output console.
      *
      *@param   model   The model is a component that is queried to set
      *         console's preferred and maximum height. May be null, in
      *         which case it has no effect (no error to be null).
      */
    public OutputConsole(Component model) {

        this.model = model;

        setFont(new Font(propertiesBundle.getStringProperty("font.output.family"),
                Font.BOLD,
                Integer.parseInt(propertiesBundle.getStringProperty("font.output.size"))));
        setEditable(false);

        // create titled border
        TitledBorder title = BorderFactory.createTitledBorder(
                BorderFactory.createLoweredBevelBorder(), messageBundle.getString("title.output"));
        title.setTitlePosition(TitledBorder.ABOVE_TOP);
        container.setBorder(title);

        addMouseListener(
            new MouseListener() {
                public void mousePressed(MouseEvent evt) {
                    maybeShowPopup(evt);
                }
                
                public void mouseReleased(MouseEvent evt) {
                    maybeShowPopup(evt);
                }

                public void mouseClicked(MouseEvent evt) {                    
                    maybeShowPopup(evt);
                }

                public void mouseEntered(MouseEvent arg0) {
                }

                public void mouseExited(MouseEvent arg0) {
                }
            }
        );
    }

    /**
      * Checks if a mouse event should pop up the popup menu. A bit of
      * a hack, because InputEvent.isPopupTrigger() doesn't seem to
      * work on Windows95/jdk1.1.7a/swing1.1.1.
      *
      * @param  evt The mouse event that is supposed to be a popup menu trigger.
      */
    /*
    private boolean isPopupTrigger(MouseEvent evt) {
        return evt.isPopupTrigger() ||
                (evt.getModifiers() | InputEvent.BUTTON2_MASK) != 0;
    }
    */
    
    /** Checks if a mouse click is a popup menu trigger and if
      * it is, shows the popup menu.
      *
      * @param  evt The mouse event that is supposed to be a popup menu trigger.
      */
    private void maybeShowPopup(MouseEvent evt) {
        //System.err.println(evt);
        if (!evt.isPopupTrigger()) {
            return;
        }
        menu.show(this, evt.getX(), evt.getY());
    }

}