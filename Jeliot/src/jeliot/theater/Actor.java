package jeliot.theater;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Panel;
import java.awt.Point;
import java.awt.Shape;
import java.util.Vector;

import jeliot.tracker.Tracker;
import jeliot.tracker.TrackerClock;
import jeliot.util.DebugUtil;
import jeliot.util.ResourceBundles;
import jeliot.util.UserProperties;

/**
 * The base class for all the actors. The Actor class should not be
 * used directly but indirectly with it's subclasses.
 * 
 * @author Pekka Uronen
 * @author Niko Myller
 */
public abstract class Actor implements Cloneable {

    // DOC: Document!
    /**
     * The resource bundle for theater package.
     */
    private static UserProperties propertiesBundle = ResourceBundles.getTheaterUserProperties();

    /**
     *
     */
    public static Component dummy = new Panel();

    /**
     *
     */
    public static Image shadowImage;

    /**
     *
     */
    private static Font defaultFont = new Font(propertiesBundle
            .getStringProperty("font.actor.default.family"), Font.PLAIN, Integer
            .parseInt(propertiesBundle.getStringProperty("font.actor.default.size")));

    /**
     *
     */
    public static final int HIGHLIGHT = -1;

    /**
     *
     */
    public static final int NORMAL = 0;

    /**
     *
     */
    public static final int SHADED = 1;

    /**
     * Actor's x-coordinate in parent.
     */
    private int x;

    /**
     * Actor's y-coordinate in parent.
     */
    private int y;

    /**
     * Actor's width, not including shadow.
     */
    protected int width;

    /**
     * Actor's height, not including shadow.
     */
    protected int height;

    /**
     * Width of actor's border.
     */
    protected int borderWidth = Integer.parseInt(propertiesBundle
            .getStringProperty("actor.border_width"));

    /**
     *
     */
    protected int shadoww = 0;

    /**
     * Margin, not including border.
     */
    protected Insets insets = new Insets(0, 0, 0, 0);

    /**
     * Trace left by the actor.
     */
    private Trace trace;

    /**
     * HIGHLIGHT, NORMAL or SHADED.
     */
    protected int light = NORMAL;

    /**
     * Font used to draw text on the actor.
     */
    protected Font font = defaultFont;

    /**
     * Actor's background color.
     */
    protected Color bgcolor;

    /**
     * Actor's foreground color. Used to draw text, for example.
     */
    protected Color fgcolor = new Color(Integer.decode(
            propertiesBundle.getStringProperty("color.actor.default.foreground")).intValue());

    /**
     * Darker shade of background color.
     */
    protected Color darkColor;

    /**
     * Lighter shade of background color.
     */
    protected Color lightColor;

    /**
     * Color used for highlighting.
     */
    protected Color highColor = new Color(Integer.decode(
            propertiesBundle.getStringProperty("color.actor.default.highlight")).intValue());

    /**
     * Color used for painting borders.
     */
    protected Color borderColor = new Color(Integer.decode(
            propertiesBundle.getStringProperty("color.actor.default.border")).intValue());

    /**
     * Parent actor.
     */
    private ActorContainer parent;

    /**
     * Description used for tracking porpuses
     * @see tracker
     */
    private String description;

    /**
     * Id of an actor, used for tracking purposes
     * @see tracker
     */
    private long actorId = -1;

    public Actor() {
        super();
    }

    /**
     * @return
     */
    public ActorContainer getParent() {
        return parent;
    }

    /**
     * @return
     */
    public Color getBackground() {
        return bgcolor;
    }

    /**
     * @param parent
     */
    public void setParent(ActorContainer parent) {
        if (this.parent != null && this.parent != parent) {
            this.parent.removeActor(this);
        }
        this.parent = parent;
    }

    /**
     * @param s
     */
    public void setShadow(int s) {
        this.shadoww = s;
    }

    /**
     * @return
     */
    public int getShadow() {
        return this.shadoww;
    }

    /**
     * Paints the shadow of the actor. Override this in the subclasses
     * if needed.
     * @param g
     */
    public void paintShadow(Graphics g) {
        if (shadoww > 0 && shadowImage != null) {
            Shape clip = g.getClip();
            g.clipRect(shadoww, height, width, shadoww);
            for (int x = 0; x < width; x += 24) {
                g.drawImage(shadowImage, x + shadoww, height, dummy);
            }
            g.setClip(clip);
            g.clipRect(width, shadoww, shadoww, height);
            int b = (width + height) % 2;
            for (int y = -b; y < height; y += 24) {
                g.drawImage(shadowImage, width, y + shadoww, dummy);
            }
            g.setClip(clip);
        }
    }

    /**
     * Paints the actor on the given Graphics instance.
     * Override this in subclasses.
     * @param g The Graphics object 
     */
    public void paintActor(Graphics g) {
        g.setColor(Color.black);
        g.fillRect(0, 0, width, height);
    }

    /**
     * Paints the actors contained in the vector on this actor.
     * @param g
     * @param actors
     */
    protected void paintActors(Graphics g, Vector actors) {
        int n = actors.size();
        for (int i = 0; i < n; ++i) {
            Actor act = (Actor) actors.elementAt(i);
            int x = act.getX();
            int y = act.getY();
            g.translate(x, y);
            act.paintActor(g);
            g.translate(-x, -y);
        }
    }

    /**
     * @param x
     * @param y
     * @param w
     * @param h
     */
    public void setBounds(int x, int y, int w, int h) {
        setLocation(x, y);
        setSize(w, h);
    }

    /**
     * @param w
     */
    public void setBorderWidth(int w) {
        this.borderWidth = w;
    }

    /**
     * @param insets
     */
    public void setInsets(Insets insets) {
        this.insets = insets;
    }

    /**
     * @param x
     * @param y
     */
    public void setLocation(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * @param loc
     */
    public void setLocation(Point loc) {
        setLocation(loc.x, loc.y);
    }

    /**
     * @return
     */
    public Point getLocation() {
        return new Point(x, y);
    }

    /**
     * @return
     */
    public Point getRootLocation() {
        int x = this.x;
        int y = this.y;
        ActorContainer parent = this.parent;
        while (parent instanceof Actor) {
            Actor p = (Actor) parent;
            x += p.x;
            y += p.y;
            parent = p.parent;
        }
        return new Point(x, y);
    }

    /**
     * @return
     */
    public int getX() {
        return x;
    }

    /**
     * @return
     */
    public int getY() {
        return y;
    }

    /**
     * @return
     */
    public Dimension getSize() {
        return new Dimension(width, height);
    }

    /**
     * @param d
     */
    public void setSize(Dimension d) {
        setSize(d.width, d.height);
    }

    /**
     * @param w
     * @param h
     */
    public void setSize(int w, int h) {
        this.width = w;
        this.height = h;
    }

    /**
     * @return
     */
    public int getWidth() {
        return width;
    }

    /**
     * @return
     */
    public int getHeight() {
        return height;
    }

    /**
     * @return
     */
    protected FontMetrics getFontMetrics() {
        return dummy.getFontMetrics(this.font);
    }

    /**
     * @param font
     */
    public void setFont(Font font) {
        this.font = font;
    }

    /**
     * @return
     */
    public Font getFont() {
        return this.font;
    }

    /**
     * @param fgcolor
     */
    public void setForeground(Color fgcolor) {
        this.fgcolor = fgcolor;
    }

    /**
     * @return
     */
    public Color getForeground() {
        return fgcolor;
    }

    /**
     * @param si
     */
    public static void setShadowImage(Image si) {
        shadowImage = si;
    }

    /**
     * @param bgcolor
     */
    public void setBackground(Color bgcolor) {
        this.bgcolor = bgcolor;
        this.lightColor = bgcolor.brighter();
        this.darkColor = bgcolor.darker();
    }

    /**
     * @param light
     */
    public void setLight(int light) {
        this.light = light;
    }

    /**
     * 
     */
    public void calculateSize() {}

    /* (non-Javadoc)
     * @see java.lang.Object#clone()
     */
    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException e) {
            if (DebugUtil.DEBUGGING) {
                e.printStackTrace();
            }
            return null;
        }
    }

    /**
     * @param g
     * @param backImage
     * @param xx
     * @param yy
     * @param w
     * @param h
     */
    protected void paintBackground(Graphics g, Image backImage, int xx, int yy, int w, int h) {

        //Dimension d = getSize();
        int biw = backImage.getWidth(dummy);
        int bih = backImage.getHeight(dummy);
        Shape clip = g.getClip();
        g.clipRect(xx, yy, w, h);

        if (biw >= 1 && bih >= 1) {
            for (int x = 0; x < w; x += biw) {
                for (int y = 0; y < h; y += bih) {
                    g.drawImage(backImage, xx + x, yy + y, dummy);
                }
            }
        }
        g.setClip(clip);
    }

    /**
     * @param p
     * @return
     */
    public Animation fly(Point p) {
        return fly(p, 4);
    }

    /**
     * Makes the actor move to given point in given time (millis)
     * Returns a reference to the animation object.
     * 
     * @param p
     * @param shadow
     * 
     * @return
     */
    public Animation fly(Point p, final int shadow) {

        Point loc = getRootLocation();
        final double startx = loc.x;
        final double starty = loc.y;
        final double destx = p.x;
        final double desty = p.y;

        double xd = destx - startx;
        double yd = desty - starty;

        final double len = Math.sqrt(xd * xd + yd * yd);

        final double angle = Math.atan2(yd, xd);

        final double cos = Math.cos(angle);
        final double sin = Math.sin(angle);

        final int originalShadow = getShadow();

        return new Animation() {

            double x = startx;

            double y = starty;

            double l = 0;

            double step;

            double traceSpace = 20;

            double tracel = traceSpace;

            long id = -1;

            public void init() {
                this.addActor(Actor.this);
                step = len / getDuration();
                setShadow(shadow);
                x -= shadow;
                y -= shadow;
                if (getActorId() == -1) {
                    Point p = getRootLocation();
                    setActorId(Tracker.trackTheater(TrackerClock.currentTimeMillis(),
                            Tracker.APPEAR, getActorId(), Tracker.RECTANGLE, new int[] { p.x},
                            new int[] { p.y}, Actor.this.getWidth(), Actor.this.getHeight(), 0, -1,
                            getDescription()));
                }
            }

            public void animate(double pulse) {
                setLocation((int) x, (int) y);
                Point p = getRootLocation();
                Tracker.trackTheater(TrackerClock.currentTimeMillis(), Tracker.MODIFY,
                        getActorId(), Tracker.RECTANGLE, new int[] { p.x}, new int[] { p.y},
                        Actor.this.getWidth(), Actor.this.getHeight(), 0, -1, getDescription());
                //id = Tracker.writeToFile("Move", p.x, p.y, Actor.this.getWidth(),
                //        Actor.this.getHeight(), TrackerClock.currentTimeMillis(), id);

                x += pulse * step * cos;
                y += pulse * step * sin;
                l += pulse * step;

                if (trace != null) {
                    double ml = Math.min(l, len) - traceSpace;
                    while (tracel < ml) {
                        int trax = (int) (startx + width / 2 + tracel * cos);
                        int tray = (int) (starty + height / 2 + tracel * sin);
                        trace.putTrace(trax, tray);
                        tracel += traceSpace;
                    }
                }
                repaint();
            }

            public void finish() {
                setShadow(originalShadow);
                setLocation((int) destx, (int) desty);
                this.repaint();
            }

            public void finalFinish() {
                this.passivate(Actor.this);
            }
        };
    }

    public Animation disappear() {
        //Tracker
        Point p = getRootLocation();
        Tracker.trackTheater(TrackerClock.currentTimeMillis(), Tracker.DISAPPEAR, getActorId(), Tracker.RECTANGLE, new int[] {p.x}, new int[] {p.y}, getWidth(), getHeight(), 0, -1, getDescription());
        return null;
    }
    
    /**
     * Returns an animation that makes the actor appear. Default
     * implementation shows the actor highlighted for given number of
     * milliseconds.
     * 
     * @param loc
     * 
     * @return
     */
    public Animation appear(final Point loc) {
        return new Animation() {

            int x = loc.x;
            int y = loc.y;
            long id = -1;

            public void init() {
                this.addActor(Actor.this);
                setLocation(loc);
                setLight(HIGHLIGHT);
                this.repaint();
                Point p = getRootLocation();
                setActorId(Tracker.trackTheater(TrackerClock.currentTimeMillis(), Tracker.APPEAR,
                        getActorId(), Tracker.RECTANGLE, new int[] { p.x}, new int[] { p.y},
                        Actor.this.getWidth(), Actor.this.getHeight(), 0, -1, getDescription()));
            }

            public void animate(double pulse) {
            //id = Tracker.writeToFile("Appear", p.x, p.y, Actor.this.getWidth(), Actor.this
            //        .getHeight(), TrackerClock.currentTimeMillis(), id);
            }

            public void finish() {
                setLight(NORMAL);
            }

            public void finalFinish() {
                this.passivate(Actor.this);
            }
        };
    }

    /**
     * @param x
     * @param y
     * @return
     */
    public Actor getActorAt(int x, int y) {
        return (x >= 0 && x < width && y >= 0 && y < height) ? this : null;
    }

    /**
     * @return Returns the actorID.
     */
    public long getActorId() {
        return actorId;
    }

    /**
     * @param actorID The actorID to set.
     */
    public void setActorId(long actorId) {
        this.actorId = actorId;
    }

    /**
     * @return Returns the description.
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description The description to set.
     */
    public void setDescription(String description) {
        this.description = description;
    }
    
}