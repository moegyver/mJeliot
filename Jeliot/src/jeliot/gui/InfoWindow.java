/*
 * Created on 28.10.2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package jeliot.gui;

import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

import jeliot.util.DebugUtil;
import jeliot.util.ResourceBundles;

/**
 * When creating a subclass of infoWindow you should create a public
 * constructor that populates the udir and fileName fields and calls
 * reload() method.
 * 
 * @author Niko Myller
 */
public class InfoWindow extends JFrame implements HyperlinkListener {

    /**
     * The resource bundle for gui package.
     */
    static protected ResourceBundle messageBundle = ResourceBundles
            .getGuiMessageResourceBundle();

    /**
     * The pane where helping information will be shown.
     */
    protected JEditorPane editorPane = new JEditorPane();

    /**
     * The pane that handles the scrolling of the editor pane showing the content.
     */
    protected JScrollPane jsp;

    /**
     * User directory where Jeliot was loaded.
     */
    protected String udir;

    /**
     * File name where the content should be read.
     */
    protected String fileName;

    /**
     * constructs the HelpWindow by creating a JFrame.
     * Sets inside the JFrame JScrollPane with JEditorPane editorPane.
     * Sets the size of the JFrame as 400 x 600
     * 
     * @param fileName file where the content is loaded
     * @param udir directory of the current invocation
     * @param icon Icon to be shown in the upper right corner of the window.
     * @param title title of the JFrame
     */
    public InfoWindow(String fileName, String udir, Image icon, String title) {
        super(title);

        this.fileName = fileName;
        this.udir = udir;

        editorPane.setEditable(false);
        editorPane.addHyperlinkListener(this);

        jsp = new JScrollPane(editorPane);
        jsp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        getContentPane().add(jsp);
        
        setIconImage(icon);

        reload();
        setSize(600, 600);

    }

    /**
     * 
     */
    public void reload() {
        try {
            File f = new File(udir, fileName);
            showURL(f.toURI().toURL());
        } catch (Exception e) {
            if (DebugUtil.DEBUGGING) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Shows the given url in the editor pane.
     *
     * @param   url The document in the url will be showed in JEditorPane editorPane.
     */
    public boolean showURL(URL url) {
        try {
            editorPane.setPage(url);
        } catch (IOException e) {
            try {
                editorPane.setPage(Thread.currentThread()
                        .getContextClassLoader().getResource(fileName));
            } catch (IOException e1) {
                try {
                    editorPane.setPage(this.getClass().getClassLoader()
                            .getResource(fileName));
                } catch (IOException e2) {
                    try {
                        editorPane.setPage(Thread.currentThread()
                                .getContextClassLoader().getResource(fileName.substring(fileName.lastIndexOf("/") + 1)));
                    } catch (IOException e3) {
                        try {
                            editorPane.setPage(this.getClass().getClassLoader()
                                    .getResource(fileName.substring(fileName.lastIndexOf("/") + 1)));
                        } catch (IOException e4) {

                            System.err.println(messageBundle
                                    .getString("bad.URL")
                                    + " " + url);
                            if (DebugUtil.DEBUGGING) {
                                e1.printStackTrace();
                            }
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }

    /* (non-Javadoc)
     * @see javax.swing.event.HyperlinkListener#hyperlinkUpdate(javax.swing.event.HyperlinkEvent)
     */
    public void hyperlinkUpdate(HyperlinkEvent e) {
        if (e.getEventType().toString().equals(
                HyperlinkEvent.EventType.ACTIVATED.toString())) {
            showURL(e.getURL());
        }
    }
}
