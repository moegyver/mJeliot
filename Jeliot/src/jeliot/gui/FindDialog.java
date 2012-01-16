/*
 * Created on 7.2.2005
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package jeliot.gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ResourceBundle;

import javax.swing.ButtonGroup;
import javax.swing.ButtonModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

import jeliot.util.ResourceBundles;

import org.syntax.jeliot_jedit.JEditTextArea;

/**
 * @author Niko Myller
 *
 */
public class FindDialog extends JDialog {

    /**
     * The resource bundle for gui package
     */
    static private ResourceBundle messageBundle = ResourceBundles
            .getGuiMessageResourceBundle();

    protected JFrame frame;

    protected CodeEditor2 m_owner;

    protected JTabbedPane m_tb;

    protected JTextField m_txtFind1;

    protected JTextField m_txtFind2;

    protected Document m_docFind;

    protected Document m_docReplace;

    //protected ButtonModel m_modelWord;
    protected ButtonModel m_modelCase;

    protected ButtonModel m_modelUp;

    protected ButtonModel m_modelDown;
    
    protected int m_searchIndex = -1;

    protected boolean m_searchUp = false;

    protected String m_searchData;

    public FindDialog(CodeEditor2 owner, int index, JFrame frame) {
        super(frame, messageBundle.getString("dialog.find_and_replace.title"),
                false);
        this.frame = frame;
        m_owner = owner;
        m_tb = new JTabbedPane();
        // "Find" panel
        JPanel p1 = new JPanel(new BorderLayout());
        JPanel pc1 = new JPanel(new BorderLayout());
        JPanel pf = new JPanel();
        pf.setLayout(new DialogLayout(20, 5));
        pf.setBorder(new EmptyBorder(8, 5, 8, 0));
        pf.add(new JLabel(messageBundle
                .getString("dialog.find_and_replace.label.find_what")));
        m_txtFind1 = new JTextField();
        m_docFind = m_txtFind1.getDocument();
        pf.add(m_txtFind1);
        pc1.add(pf, BorderLayout.CENTER);
        JPanel po = new JPanel(new GridLayout(2, 2, 8, 2));
        po.setBorder(new TitledBorder(new EtchedBorder(), messageBundle
                .getString("dialog.find_and_replace.border.title")));
        //JCheckBox chkWord = new JCheckBox("Whole words only");
        //chkWord.setMnemonic('w');
        //m_modelWord = chkWord.getModel();
        //po.add(chkWord);
        ButtonGroup bg = new ButtonGroup();
        JRadioButton rdUp = new JRadioButton(messageBundle
                .getString("dialog.find_and_replace.radio.search_up"));
        rdUp.setMnemonic('u');
        m_modelUp = rdUp.getModel();
        bg.add(rdUp);
        po.add(rdUp);
        JCheckBox chkCase = new JCheckBox(messageBundle
                .getString("dialog.find_and_replace.check.match_case"));
        chkCase.setMnemonic('c');
        m_modelCase = chkCase.getModel();
        po.add(chkCase);
        JRadioButton rdDown = new JRadioButton(messageBundle
                .getString("dialog.find_and_replace.radio.search_down"), true);
        rdDown.setMnemonic('d');
        m_modelDown = rdDown.getModel();
        bg.add(rdDown);
        po.add(rdDown);
        pc1.add(po, BorderLayout.SOUTH);
        p1.add(pc1, BorderLayout.CENTER);
        JPanel p01 = new JPanel(new FlowLayout());
        JPanel p = new JPanel(new GridLayout(2, 1, 2, 8));
        ActionListener findAction = new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                findNext(false, true);
                m_owner.getTextArea().repaint();
                FindDialog.this.requestFocusInWindow();
            }
        };

        JButton btFind = new JButton(messageBundle
                .getString("dialog.find_and_replace.button.find_next"));
        btFind.addActionListener(findAction);
        btFind.setMnemonic('f');
        p.add(btFind);
        ActionListener closeAction = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
                m_owner.requestFocusInWindow();
            }
        };
        JButton btClose = new JButton(messageBundle
                .getString("dialog.find_and_replace.button.close"));
        btClose.addActionListener(closeAction);
        btClose.setDefaultCapable(true);
        p.add(btClose);
        p01.add(p);
        p1.add(p01, BorderLayout.EAST);
        m_tb.addTab(messageBundle
                .getString("dialog.find_and_replace.tab.title.find"), p1);
        // "Replace" panel
        JPanel p2 = new JPanel(new BorderLayout());
        JPanel pc2 = new JPanel(new BorderLayout());
        JPanel pc = new JPanel();
        pc.setLayout(new DialogLayout(20, 5));
        pc.setBorder(new EmptyBorder(8, 5, 8, 0));
        pc.add(new JLabel(messageBundle
                .getString("dialog.find_and_replace.label.find_what")));
        m_txtFind2 = new JTextField();
        m_txtFind2.setDocument(m_docFind);
        pc.add(m_txtFind2);
        pc.add(new JLabel(messageBundle
                .getString("dialog.find_and_replace.label.replace")));
        JTextField txtReplace = new JTextField();
        m_docReplace = txtReplace.getDocument();
        pc.add(txtReplace);
        pc2.add(pc, BorderLayout.CENTER);
        po = new JPanel(new GridLayout(2, 2, 8, 2));
        po.setBorder(new TitledBorder(new EtchedBorder(), messageBundle
                .getString("dialog.find_and_replace.border.title")));
        //chkWord = new JCheckBox("Whole words only");
        //chkWord.setMnemonic('w');
        //chkWord.setModel(m_modelWord);
        //po.add(chkWord);
        bg = new ButtonGroup();
        rdUp = new JRadioButton(messageBundle
                .getString("dialog.find_and_replace.radio.search_up"));
        rdUp.setMnemonic('u');
        rdUp.setModel(m_modelUp);
        bg.add(rdUp);
        po.add(rdUp);
        chkCase = new JCheckBox(messageBundle
                .getString("dialog.find_and_replace.check.match_case"));
        chkCase.setMnemonic('c');
        chkCase.setModel(m_modelCase);
        po.add(chkCase);
        rdDown = new JRadioButton(messageBundle
                .getString("dialog.find_and_replace.radio.search_down"), true);
        rdDown.setMnemonic('d');
        rdDown.setModel(m_modelDown);
        bg.add(rdDown);
        po.add(rdDown);
        pc2.add(po, BorderLayout.SOUTH);
        p2.add(pc2, BorderLayout.CENTER);
        JPanel p02 = new JPanel(new FlowLayout());
        p = new JPanel(new GridLayout(3, 1, 2, 8));
        ActionListener replaceAction = new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                findNext(true, true);
                m_owner.getTextArea().repaint();
                FindDialog.this.requestFocusInWindow();
            }
        };
        JButton btReplace = new JButton(messageBundle
                .getString("dialog.find_and_replace.button.replace"));
        btReplace.addActionListener(replaceAction);
        btReplace.setMnemonic('r');
        p.add(btReplace);
        ActionListener replaceAllAction = new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                int counter = 0;
                int pos = m_owner.getTextArea().getCaretPosition();
                boolean up = m_modelUp.isSelected();
                while (true) {
                    int result = findNext(true, false);
                    if (result < 0) // error
                        return;
                    else if (result == 0) // no more
                        break;
                    counter++;
                }
                m_owner.getTextArea().setCaretPosition(pos);
                //Switch the selection
                if (up) {
                    m_modelDown.setSelected(true);
                } else {
                    m_modelUp.setSelected(true);
                }
                while (true) {
                    int result = findNext(true, false);
                    if (result < 0) // error
                        return;
                    else if (result == 0) // no more
                        break;
                    counter++;
                }
                m_owner.getTextArea().setCaretPosition(pos);
                //Return to the original state
                if (up) {
                    m_modelUp.setSelected(true);
                } else {
                    m_modelDown.setSelected(true);
                }
                m_owner.getTextArea().repaint();
                JOptionPane
                        .showMessageDialog(
                                FindDialog.this.frame,
                                counter
                                        + " "
                                        + messageBundle
                                                .getString("dialog.find_and_replace.replaced.message"),
                                "Info", JOptionPane.INFORMATION_MESSAGE);
                FindDialog.this.m_txtFind1.grabFocus();
            }
        };

        JButton btReplaceAll = new JButton(messageBundle
                .getString("dialog.find_and_replace.button.replace_all"));
        btReplaceAll.addActionListener(replaceAllAction);
        btReplaceAll.setMnemonic('a');
        p.add(btReplaceAll);
        btClose = new JButton(messageBundle
                .getString("dialog.find_and_replace.button.close"));
        btClose.addActionListener(closeAction);
        btClose.setDefaultCapable(true);
        p.add(btClose);
        p02.add(p);
        p2.add(p02, BorderLayout.EAST);
        // Make button columns the same size
        p01.setPreferredSize(p02.getPreferredSize());
        m_tb.addTab(messageBundle
                .getString("dialog.find_and_replace.tab.title.replace"), p2);
        m_tb.setSelectedIndex(index);
        getContentPane().add(m_tb, BorderLayout.CENTER);
        WindowListener flst = new WindowAdapter() {

            public void windowActivated(WindowEvent e) {
                m_searchIndex = -1;
                if (m_tb.getSelectedIndex() == 0)
                    m_txtFind1.grabFocus();
                else
                    m_txtFind2.grabFocus();
            }

            public void windowDeactivated(WindowEvent e) {
                m_searchData = null;
                m_searchIndex = -1;
            }
        };

        addWindowListener(flst);
        pack();
        setResizable(false);
    }

    public void setSelectedIndex(int index) {
        m_tb.setSelectedIndex(index);
        setVisible(true);
        m_searchIndex = -1;
    }

    public int findNext(boolean doReplace, boolean showWarnings) {
        JEditTextArea monitor = m_owner.getTextArea();
        int pos = monitor.getCaretPosition();
        if (m_modelUp.isSelected() != m_searchUp) {
            m_searchUp = m_modelUp.isSelected();
            m_searchIndex = -1;
        }
        //m_searchUp = m_modelUp.isSelected();
        if (m_searchIndex == -1) {
            try {
                Document doc = monitor.getDocument();
                if (m_searchUp) {
                    m_searchData = doc.getText(0, pos);
                } else {
                    m_searchData = doc.getText(pos, doc.getLength() - pos);
                }
                m_searchIndex = pos;
            } catch (BadLocationException ex) {
                warning(ex.toString());
                return -1;
            }
        }

        String key = "";
        try {
            key = m_docFind.getText(0, m_docFind.getLength());
        } catch (BadLocationException ex) {
        }

        if (key.length() == 0) {
            warning("Please enter the target to search");
            return -1;
        }

        if (!m_modelCase.isSelected()) {
            m_searchData = m_searchData.toLowerCase();
            key = key.toLowerCase();
        }

        /*
         if (m_modelWord.isSelected()) {
         for (int k = 0; k < FindReplaceUtil.WORD_SEPARATORS.length; k++) {
         if (key.indexOf(FindReplaceUtil.WORD_SEPARATORS[k]) >= 0) {
         warning("The text target contains an illegal " + "character \'"
         + FindReplaceUtil.WORD_SEPARATORS[k] + "\'");
         return -1;
         }
         }
         }
         */

        String replacement = "";
        if (doReplace) {
            try {
                replacement = m_docReplace.getText(0, m_docReplace.getLength());
            } catch (BadLocationException ex) {
            }
        }

        int xStart = -1;
        int xFinish = -1;
        //while (true) {
        if (m_searchUp)
            xStart = m_searchData.lastIndexOf(key, pos - 1);
        else
            xStart = m_searchData.indexOf(key, pos - m_searchIndex);
        if (xStart < 0) {
            if (showWarnings) {
                warning("Text not found");
            }
            return 0;
        }

        xFinish = xStart + key.length();

        /*
         if (m_modelWord.isSelected()) {
         boolean s1 = xStart > 0;
         boolean b1 = s1 && !FindReplaceUtil.isSeparator(m_searchData.charAt(xStart - 1));
         boolean s2 = xFinish < m_searchData.length();
         boolean b2 = s2 && !FindReplaceUtil.isSeparator(m_searchData.charAt(xFinish));
         if (b1 || b2) { // Not a whole word
         if (m_searchUp && s1) { // Can continue up
         pos = xStart;
         continue;
         }
         if (!m_searchUp && s2) { // Can continue down
         pos = xFinish;
         continue;
         }
         // Found, but not a whole word, and we cannot continue
         if (showWarnings) {
         warning("Text not found");
         }
         return 0;
         }
         }
         */

        //break;
        //}
        
        if (!m_searchUp) {
            xStart += m_searchIndex;
            xFinish += m_searchIndex;
        }

        if (doReplace) {
            //m_owner.setSelection(xStart, xFinish, m_searchUp);
            monitor.select(xFinish, xStart);
            monitor.setSelectedText(replacement);
            //monitor.setSelection(xStart, xStart + replacement.length(), m_searchUp);
            if (m_searchUp) {
                monitor.select(xStart + replacement.length(), xStart);
            } else {
                monitor.select(xStart, xStart + replacement.length());
            }
            m_searchIndex = -1;
        } else {
            //m_owner.setSelection(xStart, xFinish, m_searchUp);
            monitor.select(xFinish, xStart);
        }
        //monitor.requestFocusInWindow();
        this.requestFocusInWindow();
        return 1;
    }

    protected void warning(String message) {
        JOptionPane.showMessageDialog(frame, message, "Warning",
                JOptionPane.INFORMATION_MESSAGE);
    }

}