package jeliot.theater;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Point;
import java.util.Iterator;
import java.util.Stack;

import jeliot.tracker.Tracker;
import jeliot.tracker.TrackerClock;

/**
 * MethodStage is the graphical representation of the MethodFrame.
 * It contains the local <code>VariableActor</code>s and handles the 
 * scope changes.
 * 
 * @author Pekka Uronen
 * @author Niko Myller
 *
 * @see jeliot.lang.MethodFrame 
 * @see jeliot.theater.VariableActor
 * @see jeliot.lang.Variable
 */
public class MethodStage extends Actor implements ActorContainer {

    /**
     * Indicates how many variables is defined in this scope.
     */
    private int scopeVarCount = 0;

    /**
     * Keeps track of scopes and the amount of variables in each scope.
     */
    private Stack scopes = new Stack();

    /**
     * Variable actors in this MethodStage.
     */
    private Stack variables = new Stack();

    /**
     * Name of the method or MethodStage.
     */
    private String name;

    /**
     * Height of the name.
     */
    private int nheight;

    /**
     * Width of the name.
     */
    private int nwidth;

    /**
     * Number of pixels around the divider line.
     */
    private int margin = 2;

    /**
     * Number of pixels between actors.
     */
    private int actorMargin = 3;

    /**
     * Maximum possible number of variables on the MethodStage at the moment.
     */
    private int varCount = 1;

    /**
     * How many variables actually is on the Methodstage at the moment.
     */
    private int totalVarCount = 0;

    /**
     * Actors Width
     */
    private int actWidth;

    /**
     * Actors Height
     */
    private int actHeight;

    /**
     * Value is true if the variables of the method are supposed to be shown.
     * Normally, the value is true only on some animations variables are not shown.
     * For example during appearing and disappearing this variable is set to false.
     */
    private boolean paintVars = true;

    /**
     * Actor that is going to be added to the MethodStage but is not yet bind on it.
     * For example an actor that is animated at the moment and then added to the MethodStage
     * (e.g. variable appearing).
     */
    private Actor reserved;

    /**
     * The location where reserved actor is reserved.
     */
    private Point resLoc;

    /**
     * @param name
     */
    public MethodStage(String name) {
        this.name = name;
        insets = new Insets(2, 6, 4, 6);
        setDescription("method frame" + name + "creatd");
    }

    /**
     * @param name
     * @return
     */
    public VariableActor findVariableActor(String name) {

        //Find the variable with the given name.
        for (int i = 0; i < variables.size(); i++) {
            VariableActor va = (VariableActor) variables.elementAt(i);
            if (name.equals(va.getName()) || va.getLabel().equals(name)) {
                return va;
            }
        }

        return null;
    }

    /**
     * @param maxActWidth
     * @param actHeight
     */
    public void calculateSize(int maxActWidth, int actHeight) {

        this.actWidth = maxActWidth;
        this.actHeight = actHeight;

        Dimension d = calculateSizeDimensions();

        setSize(d.width, d.height);
    }

    /**
     * 
     */
    public void calculateSize() {
        for (Iterator i = variables.iterator(); i.hasNext();) {
            Actor a = (Actor) i.next();
            if (this.actHeight < a.getHeight()) {
                this.actHeight = a.getHeight();
            }
            if (this.actWidth < a.getWidth()) {
                this.actWidth = a.getWidth();
            }
        }
        if (reserved != null) {
            if (this.actHeight < reserved.getHeight()) {
                this.actHeight = reserved.getHeight();
            }
            if (this.actWidth < reserved.getWidth()) {
                this.actWidth = reserved.getWidth();
            }
        }
        Dimension d = calculateSizeDimensions();
        setSize(d.width, d.height);
        repositionVariableActors();
    }

    /**
     * @return
     */
    public Dimension calculateSizeDimensions() {
        return calculateSizeDimensions(this.varCount);
    }

    /**
     * @param varCount
     * @return
     */
    public Dimension calculateSizeDimensions(int varCount) {

        int w = borderWidth * 2 + insets.right + insets.left
                + Math.max(actWidth, nwidth) + 2 * margin;

        int h = borderWidth * 2 + insets.top + insets.bottom + nheight + 2
                * margin + actorMargin + (actorMargin + actHeight) * varCount;

        return new Dimension(w, h);
    }

    /* (non-Javadoc)
     * @see jeliot.theater.Actor#paintActor(java.awt.Graphics)
     */
    public void paintActor(Graphics g) {
        int w = width;
        int h = height;
        int bw = borderWidth;

        int hgh = nheight + margin * 3 / 2;

        // fill background
        g.setColor(light == HIGHLIGHT ? lightColor : bgcolor);
        g.fillRect(bw, hgh + 2, w - 2 * bw, h - 2 * bw - hgh);
        g.setColor(lightColor);
        g.fillRect(bw, bw, w - 2 * bw, hgh - bw);

        // draw border
        g.setColor(darkColor);
        for (int i = 1; i < bw; ++i) {
            g.drawRect(i, i, w - i * 2 - 1, h - i * 2 - 1);
        }

        g.setColor(borderColor);
        g.drawRect(0, 0, w - 1, h - 1);

        // draw line
        g.drawRect(1, hgh, w - 2, 1);

        // draw name
        g.setFont(font);
        g.setColor(fgcolor);
        g.drawString(name, insets.left, insets.top + (nheight * 4 / 5)); //insets.top + nheight);

        if (paintVars) {
            paintActors(g, variables);
        }
    }

    /* (non-Javadoc)
     * @see jeliot.theater.Actor#setFont(java.awt.Font)
     */
    public void setFont(Font font) {
        super.setFont(font);
        FontMetrics fm = getFontMetrics();
        nheight = fm.getHeight();
        nwidth = fm.stringWidth(name);
    }

    /**
     * @param actor
     * @return
     */
    public Point reserve(Actor actor) {
        reserved = actor;
        calculateSize();

        Actor prev = (variables.isEmpty()) ? null : (Actor) variables
                .lastElement();

        int y = ((prev == null) ? insets.top + nheight + margin * 2
                + borderWidth : prev.getHeight() + prev.getY())
                + actorMargin;

        int x = getWidth() - insets.right - actor.getWidth();

        resLoc = new Point(x, y);
        Point rp = getRootLocation();
        rp.translate(x, y);
        return rp;
    }

    /**
     * 
     */
    public void bind() {
        reserved.setLocation(resLoc);
        variables.push(reserved);
        reserved.setParent(this);

        //Added for Jeliot 3
        totalVarCount++;
        scopeVarCount++;
        calculateSize();
    }

    public void repositionVariableActors() {
        for (int i = 0; i < variables.size(); i++) {
            Actor prev = i == 0 ? null : (Actor) variables.get(i - 1);
            Actor actor = (Actor) variables.get(i);
            int y = ((prev == null) ? insets.top + nheight + margin * 2
                    + borderWidth : prev.getHeight() + prev.getY())
                    + actorMargin;

            int x = getWidth() - insets.right - actor.getWidth();
            actor.setLocation(x, y);
        }
    }

    /**
     * 
     */
    //Added for Jeliot 3
    public void openScope() {
        scopes.push(new Integer(scopeVarCount));
        scopeVarCount = 0;
    }

    /**
     * 
     */
    //Added for Jeliot 3
    public void closeScope() {
        for (int i = 0; i < scopeVarCount; i++) {
            variables.pop();
        }
        totalVarCount -= scopeVarCount;
        scopeVarCount = ((Integer) scopes.pop()).intValue();
        calculateSize();
    }

    /* (non-Javadoc)
     * @see jeliot.theater.ActorContainer#removeActor(jeliot.theater.Actor)
     */
    public void removeActor(Actor actor) {
        variables.removeElement(actor);
    }

    /* (non-Javadoc)
     * @see jeliot.theater.Actor#getActorAt(int, int)
     */
    public Actor getActorAt(int xc, int yc) {

        int n = variables.size();

        for (int i = n - 1; i >= 0; --i) {
            Actor actor = (Actor) variables.elementAt(i);
            Actor at = actor.getActorAt(xc - actor.getX(), yc - actor.getY());
            if (at != null) {
                return at;
            }
        }
        return super.getActorAt(xc, yc);
    }

    /* (non-Javadoc)
     * @see jeliot.theater.Actor#appear(java.awt.Point)
     */
    public Animation appear(final Point loc) {

        return new Animation() {
            Dimension size;

            double h;

            double plus;

            int full;

            int id = -1;

            public void init() {
                size = new Dimension(getWidth(), nheight + margin * 3);
                h = size.height;
                full = getHeight();
                plus = (full - h) / getDuration();
                this.addActor(MethodStage.this);
                setLocation(loc);
                setSize(size);
                setLight(HIGHLIGHT);
                paintVars = false;
                //Tracker
                Point p = getRootLocation();
                setActorId(Tracker.trackTheater(TrackerClock
                        .currentTimeMillis(), Tracker.APPEAR, getActorId(),
                        Tracker.RECTANGLE, new int[] { p.x },
                        new int[] { p.y }, getWidth(), getHeight(), 0, -1,
                        getDescription()));
                repaint();
            }

            public void animate(double pulse) {
                h += plus * pulse;
                size.height = (int) h;
                setSize(size);

                //TRACKER
                Point p = getRootLocation();
                //id = Tracker.writeToFile("Appear", p.x, p.y, MethodStage.this.getWidth(), MethodStage.this.getHeight(), TrackerClock.currentTimeMillis(), id);
                Tracker.trackTheater(TrackerClock.currentTimeMillis(),
                        Tracker.MODIFY, getActorId(), Tracker.RECTANGLE,
                        new int[] { p.x }, new int[] { p.y }, getWidth(),
                        getHeight(), 0, -1, getDescription());

                this.repaint();
            }

            public void finish() {
                setLight(NORMAL);
                size.height = full;
                setSize(size);
                paintVars = true;
            }

            public void finalFinish() {
                this.passivate(MethodStage.this);
            }
        };
    }

    /**
     * @return
     */
    public Animation disappear() {

        return new Animation() {
            Dimension size;

            double h;

            double plus;

            int full;

            int id = -1;

            public void init() {
                size = getSize();
                full = nheight + margin * 3;
                h = getHeight();
                plus = (full - h) / getDuration();
                this.addActor(MethodStage.this);
                setSize(size);
                paintVars = false;
                repaint();
            }

            public void animate(double pulse) {

                h += plus * pulse;
                size.height = (int) h;
                setSize(size);

                //TRACKER
                Point p = getRootLocation();
                //id = Tracker.writeToFile("Disappear", p.x, p.y, MethodStage.this.getWidth(), MethodStage.this.getHeight(), TrackerClock.currentTimeMillis(), id);
                Tracker.trackTheater(TrackerClock.currentTimeMillis(),
                        Tracker.MODIFY, getActorId(), Tracker.RECTANGLE,
                        new int[] { p.x }, new int[] { p.y }, getWidth(),
                        getHeight(), 0, -1, getDescription());

                this.repaint();
            }

            public void finish() {
                //Tracker
                for (Iterator i = variables.iterator(); i.hasNext();) {
                    Actor a = (Actor) i.next();
                    if (a != null) {
                        a.disappear();
                    }
                }
                Point p = getRootLocation();
                Tracker.trackTheater(TrackerClock.currentTimeMillis(),
                        Tracker.DISAPPEAR, getActorId(), Tracker.RECTANGLE,
                        new int[] { p.x }, new int[] { p.y }, getWidth(),
                        getHeight(), 0, -1, getDescription());

                this.removeActor(MethodStage.this);
            }
        };
    }

    /**
     * @return
     */
    public Animation extend() {

        if ((totalVarCount + 1) > varCount) {

            varCount = totalVarCount + 1;

            return new Animation() {

                Dimension size, newSize;

                double h;

                double plus;

                int full;

                int id = -1;

                public void init() {
                    size = getSize();
                    h = size.height;
                    newSize = calculateSizeDimensions(totalVarCount + 1);
                    full = newSize.height;
                    plus = (full - h) / getDuration();
                    //setLight(HIGHLIGHT);
                    this.repaint();
                }

                public void animate(double pulse) {

                    h += plus * pulse;
                    size.height = (int) h;
                    setSize(size);
                    this.repaint();

                    //TRACKER
                    Point p = getRootLocation();
                    //id = Tracker.writeToFile("Disappear", p.x, p.y, MethodStage.this.getWidth(), MethodStage.this.getHeight(), TrackerClock.currentTimeMillis(), id);
                    Tracker.trackTheater(TrackerClock.currentTimeMillis(),
                            Tracker.MODIFY, getActorId(), Tracker.RECTANGLE,
                            new int[] { p.x }, new int[] { p.y }, getWidth(),
                            getHeight(), 0, -1, getDescription());
                }

                public void finish() {
                    setLight(NORMAL);
                    size.height = full;
                    setSize(size);
                    paintVars = true;
                }

                public void finalFinish() {
                    //this.passivate((Actor)MethodStage.this);
                }
            };

        }
        return null;
    }

    /**
     * @return Returns the name.
     */
    public String getName() {
        return name;
    }

    /**
     * @param name The name to set.
     */
    public void setName(String name) {
        this.name = name;
    }
}
