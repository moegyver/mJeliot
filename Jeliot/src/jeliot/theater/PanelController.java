package jeliot.theater;

import java.awt.Image;

import jeliot.util.ResourceBundles;
import jeliot.util.UserProperties;

/**
 * PanelController handles the curtains of the theater (PanelActor) by
 * controlling the opening and closing of the curtains and showing the
 * panel and background images.
 * 
 * @author Pekka Uronen
 * @author Niko Myller
 * 
 * @see jeliot.theater.PanelActor
 */
public class PanelController {

    /**
     * The resource bundle for theater package
     */
    static private UserProperties propertiesBundle = ResourceBundles
            .getTheaterUserProperties();

    //  DOC: Document!

    /**
     *
     */
    private PanelActor panel;

    /**
     *
     */
    private Theater theatre;

    /**
     *
     */
    private AnimationEngine engine;

    /**
     *
     */
    private Image bgImage;

    /**
     *
     */
    private Image panelImage;

    /**
     *
     */
    private int openDur = Integer.parseInt(propertiesBundle.getStringProperty("curtain.open_duration"));

    /**
     *
     */
    private int closeDur = Integer.parseInt(propertiesBundle.getStringProperty("curtain.close_duration"));

    /**
     * @param theatre
     * @param iLoad
     */
    public PanelController(Theater theatre, ImageLoader iLoad) {
        this.theatre = theatre;
        this.engine = new AnimationEngine(theatre);
        this.bgImage = iLoad.getImage(propertiesBundle.getStringProperty("image.background"));
        this.panelImage = iLoad.getImage(propertiesBundle.getStringProperty("image.panel"));
        this.panel = new PanelActor(panelImage, iLoad
                .getImage(propertiesBundle.getStringProperty("image.panel.left")), iLoad.getImage(propertiesBundle
                .getStringProperty("image.panel.right")), 62);
    }

    /**
     * @param open
     * @param next
     * @return
     */
    public Thread slide(final boolean open, final Runnable next) {
        return new Thread() {

            public void run() {
                int dur;
                if (open) {
                    panel.setGap(0, 0);
                    dur = openDur;
                } else {
                    panel.setGap(1000, 1000);
                    dur = closeDur;
                }
                // animation
                panel.setSize(theatre.getWidth(), theatre.getHeight());
                theatre.addActor(panel);
                if (open) {
                    theatre.setBackground(bgImage);
                }
                theatre.capture();
                Animation anim = panel.slide(open);
                anim.setDuration(dur);
                engine.showAnimation(anim);
                theatre.removeActor(panel);
                if (!open) {
                    theatre.setBackground(panelImage);
                    theatre.cleanUp();
                }
                theatre.release();
                next.run();
            }
        };
    }
}