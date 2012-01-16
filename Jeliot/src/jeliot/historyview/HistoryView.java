/*
 * Created on Jul 1, 2004
 */
package jeliot.historyview;

import java.awt.BorderLayout;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import jeliot.gui.CodePane2;
import jeliot.mcode.Highlight;
import jeliot.util.DebugUtil;
import jeliot.util.Util;

/**
 * @author nmyller
 */
public class HistoryView extends JComponent implements ActionListener {

    /**
     * Not currently used
     * Comment for <code>HISTORY_SIZE</code>
     */
    private static final int HISTORY_SIZE = 10;

    /**
     * Not currently used
     * Comment for <code>LIMIT_HISTORY_SIZE</code>
     */
    private static final boolean LIMIT_HISTORY_SIZE = true;

    /**
     * Comment for <code>images</code>
     */
    private Vector imageFiles = new Vector();

    /**
     * Comment for <code>highlights</code>
     */
    private Vector highlights = new Vector();

    /**
     * Comment for <code>buttonL</code>
     */
    private final JButton buttonL = new JButton("<");

    /**
     * Comment for <code>buttonR</code>
     */
    private final JButton buttonR = new JButton(">");

    /**
     * Comment for <code>slider</code>
     */
    private JSlider slider = new JSlider(JSlider.HORIZONTAL, 0, 0, 0);

    /**
     * Comment for <code>ic</code>
     */
    private ImageCanvas imageCanvas = new ImageCanvas();

    /**
     * Comment for <code>codePane</code>
     */
    private CodePane2 codePane;

    /**
     * Comment for <code>bottomComponent</code>
     */
    private JPanel bottomComponent = new JPanel(new BorderLayout());

    /**
     * Comment for <code>imageTemp</code>
     */
    private File imageTemp;

    /**
     * Comment for <code>imageNumber</code>
     */
    private int imageNumber;

    /**
     * Comment for <code>current</code>
     */
    private BufferedImage current;

    /**
     * Comment for <code>newImageAdded</code>
     */
    private boolean newImageAdded = false;

    /**
     * Comment for <code>enabled</code>
     */
    private boolean enabled = false;

    /**
     * 
     * @param c
     * @param udir
     */
    public HistoryView(final CodePane2 c, String udir) {
        //TODO: add some of the literals below to a resourceBundle.
        this.codePane = c;
        File userPath = Util.createUserPath();

        do {
            String dirName = "images" + System.currentTimeMillis();
            imageTemp = new File(userPath, dirName);
        } while (imageTemp.exists());
        imageTemp.mkdir();

        initialize();
        setLayout(new BorderLayout());

        slider.setMajorTickSpacing(5);
        slider.setMinorTickSpacing(1);
        slider.setPaintTicks(true);
        slider.setPaintLabels(false);
        slider.setSnapToTicks(true);

        bottomComponent.add("Center", slider);
        bottomComponent.add("West", buttonL);
        bottomComponent.add("East", buttonR);

        add("Center", new JScrollPane(imageCanvas));
        add("South", bottomComponent);

        buttonL.setEnabled(false);
        buttonR.setEnabled(false);
        buttonL.addActionListener(this);
        buttonR.addActionListener(this);
        buttonR.setMnemonic(KeyEvent.VK_GREATER);
        buttonL.setMnemonic(KeyEvent.VK_LESS);

        slider.addChangeListener(new ChangeListener() {

            public void stateChanged(ChangeEvent e) {
                try {
                    //previousImageNumber = imageNumber;
                    imageNumber = slider.getValue();

                    if (imageNumber == slider.getMaximum()) {
                        buttonR.setEnabled(false);
                    } else {
                        buttonR.setEnabled(true);
                    }
                    if (imageNumber == slider.getMinimum()) {
                        buttonL.setEnabled(false);
                    } else {
                        buttonL.setEnabled(true);
                    }
                    if (imageNumber < imageFiles.size() && imageNumber >= 0) {
                        if (newImageAdded) {
                            imageCanvas.setImage(current);
                        } else {
                            try {
                                current = ImageIO.read((File) imageFiles.get(imageNumber));
                            } catch (IOException e1) {
                                //TODO: report to user that something went wrong!
                                if (DebugUtil.DEBUGGING) {
                                    e1.printStackTrace();
                                }
                            }
                            imageCanvas.setImage(current);
                        }
                    }

                    if (highlights.size() > imageNumber && highlights.get(imageNumber) != null) {
                        if (HistoryView.this.isVisible()) {
                            c.highlightStatement((Highlight) highlights.get(imageNumber));
                        }
                    }
                    newImageAdded = false;
                    imageCanvas.repaint();
                    validate();
                } catch (Exception e1) {
                    if (DebugUtil.DEBUGGING) {
                        e1.printStackTrace();
                    }
                }
            }
        });
    }

    /**
     * 
     */
    public void initialize() {
        //next = null;
        //previous = null;
        current = null;
        imageFiles.removeAllElements();
        highlights.removeAllElements();
        imageCanvas.setImage(null);
        File[] files = imageTemp.listFiles();
        int n = files.length;
        for (int i = 0; i < n; i++) {
            files[i].delete();
        }
        slider.setEnabled(false);
        buttonL.setEnabled(false);
        buttonR.setEnabled(false);
        imageCanvas.repaint();
        validate();
    }

    public void close() {
        initialize();
        imageTemp.delete();
    }

    /**
     * @param i
     * @param h
     */
    public void addImage(final Image i, final Highlight h) {
        if (enabled) {
            if (!slider.isEnabled()) {
                slider.setEnabled(true);
            }
            int size = imageFiles.size();
            BufferedImage newImage = getBufferedImage(i);
            File imageFile = new File(imageTemp, "image" + size + ".png");

            //TODO: This could be done in a thread to maybe increase the performance
            try {
                ImageIO.write(newImage, "png", imageFile);
            } catch (IOException e1) {
                //TODO: report to user that something went wrong!
                if (DebugUtil.DEBUGGING) {
                    e1.printStackTrace();
                }
            }
            imageFiles.add(imageFile);
            highlights.add(h);

            current = newImage;
            newImageAdded = true;
            size = imageFiles.size() - 1;
            slider.setMaximum(size);
            slider.setValue(size);
        }
    }

    /* (non-Javadoc)
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent arg0) {
        if (arg0.getSource().equals(buttonL)) {
            slider.setValue(slider.getValue() - 1);
        } else if (arg0.getSource().equals(buttonR)) {
            slider.setValue(slider.getValue() + 1);
        }
    }

    /**
     * 
     * @param img
     * @return
     */
    public static BufferedImage getBufferedImage(Image img) {
        // if the image is already a BufferedImage, cast and return it
        if ((img instanceof BufferedImage)) {
            return (BufferedImage) img;
        }
        // otherwise, create a new BufferedImage and draw the original 
        // image on it
        int w = img.getWidth(null);
        int h = img.getHeight(null);
        BufferedImage bi = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = bi.createGraphics();
        g2d.drawImage(img, 0, 0, w, h, null);
        g2d.dispose();
        return bi;
    }

    /**
     * @return Returns the enabled.
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * @param enabled The enabled to set.
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        if (enabled) {
            initialize();
        }
    }

    /**
     * @return Returns the imageCanvas.
     */
    public ImageCanvas getImageCanvas() {
        return imageCanvas;
    }
}