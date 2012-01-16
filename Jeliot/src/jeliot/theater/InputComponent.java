package jeliot.theater;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import jeliot.mcode.Highlight;
import jeliot.tracker.Tracker;
import jeliot.tracker.TrackerClock;

/**
 * InputComponent is shown when ever the executed program
 * requests input. The InputComponent is rendered as a message
 * label and a text field that collects the input. 
 * 
 * @author Pekka Uronen
 * @author Niko Myller
 * 
 * @see jeliot.theater.Director#animateInputHandling(String,Highlight)
 */
public class InputComponent extends JPanel implements ActionListener {

    //  DOC: Document!

    /**
     *
     */
    private InputValidator validator;

    /**
     *
     */
    private JTextField field;

    /**
     *
     */
    private JLabel label;

    /**
     *
     */
    private Actor bgActor;

    /**
     * 
     */
    private String prompt;
    
    /**
     * 
     */
    public static boolean showComponents = true;

    /**
     * @param prompt
     * @param validator
     */
    public InputComponent(String prompt, InputValidator validator) {
        this.validator = validator;
        this.prompt = prompt;
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        field = new JTextField(8);
        add(field);

        label = new JLabel(prompt);
        add(label);

        field.addActionListener(this);
    }

    public void paint(Graphics g) {
        if (showComponents) {
            super.paint(g);
        } else {

            Font prevFont = g.getFont();
            Font newFont = new Font("Arial", Font.BOLD, 14);
            g.setFont(newFont);
            FontMetrics fm = g.getFontMetrics();
            int promptWidth = fm.stringWidth(this.prompt);
            Color prevColor = g.getColor();
            g.setColor(Color.LIGHT_GRAY);
            g.fillRect(0, 0, promptWidth + 16, 60);
            g.setColor(Color.WHITE);
            g.fillRect(8, 5, promptWidth, 30);
            g.setColor(Color.BLACK);
            g.drawRect(8, 5, promptWidth, 30);
            g.drawRect(0, 0, promptWidth + 16, 60);

            g.drawString(this.prompt, 8, 53);

            g.setFont(prevFont);
            g.setColor(prevColor);
        }
    }

    /* (non-Javadoc)
     * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
     */
    public void paintComponent(Graphics g) {
        if (bgActor == null) {
            super.paintComponent(g);
        } else {
            bgActor.paintActor(g);
            bgActor.paintShadow(g);
        }

        //Tracker
        if (bgActor.getActorId() == -1) {
            Point p = bgActor.getRootLocation();
            bgActor.setActorId(Tracker.trackTheater(TrackerClock
                    .currentTimeMillis(), Tracker.APPEAR, -1,
                    Tracker.RECTANGLE, new int[] { p.x }, new int[] { p.y },
                    bgActor.getWidth(), bgActor.getHeight(), 0, -1, "Input: "));
        }
    }

    /**
     * @param actor
     */
    public void setBgActor(Actor actor) {
        this.bgActor = actor;
        Font font = actor.getFont();
        label.setFont(font);
        field.setFont(font);
    }

    /**
     * 
     */
    public void popup() {
        field.requestFocusInWindow();
        bgActor.setSize(getSize());
    }

    /* (non-Javadoc)
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent evt) {
        String text = field.getText();
        validator.validate(text);
    }

    /**
     * 
     */
    public void requestFocusForInputField() {
        field.requestFocusInWindow();
    }

    /**
     * 
     */
    public void disappear() {
        Point p = bgActor.getRootLocation();
        bgActor.setActorId(Tracker.trackTheater(TrackerClock
                .currentTimeMillis(), Tracker.DISAPPEAR, -1, Tracker.RECTANGLE,
                new int[] { p.x }, new int[] { p.y }, bgActor.getWidth(),
                bgActor.getHeight(), 0, -1, "Input: "));
    }
}
