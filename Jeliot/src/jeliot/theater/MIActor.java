/*
 * Created on 5.7.2006
 */
package jeliot.theater;

import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Point;

import jeliot.tracker.Tracker;
import jeliot.tracker.TrackerClock;

/**
 * MIActor represents different method invocations (static, object, constructors).
 * The actor shows the method name and the parameters in a similar
 * way as Java syntax just replaces the variable references with
 * their actual values. 
 * 
 * @author nmyller
 */
public abstract class MIActor extends Actor implements ActorContainer {

    /**
     *
     */
    private String name;

    /**
     *
     */
    protected Actor[] actors;

    /**
     *
     */
    protected Point[] locs;

    /**
     *
     */
    protected boolean[] bound;

    /**
     *
     */
    protected int next = 0;

    /**
     *
     */
    protected int margin = 2;

    /**
     *
     */
    protected int titlemargin = 4;

    /**
     *
     */
    protected int namey;

    /**
     *
     */
    protected int namex;

    /**
     *
     */
    protected int namew;

    /**
     *
     */
    protected int nameh;

    /**
     *
     */
    private int commaMargin;

    private int parameterCount;
    
    /**
     * 
     */
    public MIActor(String name, int n) {
        super();
        setName(name);
        this.parameterCount = n;
        actors = new Actor[n];
        locs = new Point[n];
        bound = new boolean[n];
        FontMetrics fm = getFontMetrics();
        setCommaMargin(fm.stringWidth(","));
    }

    public int getCommaMargin() {
        return commaMargin;
    }

    public void setCommaMargin(int commaMargin) {
        this.commaMargin = commaMargin;
    }

    public int getMargin() {
        return margin;
    }

    public void setMargin(int margin) {
        this.margin = margin;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getNameh() {
        return nameh;
    }

    public void setNameh(int nameh) {
        this.nameh = nameh;
    }

    public int getNamew() {
        return namew;
    }

    public void setNamew(int namew) {
        this.namew = namew;
    }

    public int getNamex() {
        return namex;
    }

    public void setNamex(int namex) {
        this.namex = namex;
    }

    public int getNamey() {
        return namey;
    }

    public void setNamey(int namey) {
        this.namey = namey;
    }

    public int getNext() {
        return next;
    }

    public void setNext(int next) {
        this.next = next;
    }

    public int getTitlemargin() {
        return titlemargin;
    }

    public void setTitlemargin(int titlemargin) {
        this.titlemargin = titlemargin;
    }

    /**
     * @param actor
     * @return
     */
    public Point reserve(Actor actor) {
        actors[next] = actor;
        //int y = insets.top + namey + titlemargin;
        //int x = insets.left;
        int y = insets.top; 
        int x = insets.left + namew + margin;

        if (next > 0) {
            if (actors[next-1] instanceof ReferenceActor) {
                x = locs[next - 1].x +
                    margin + getCommaMargin() + margin +
                    actors[next - 1].getWidth() +
                    ((ReferenceActor) actors[next - 1]).getReferenceWidth();
            } else {
                x = locs[next - 1].x +
                    margin + getCommaMargin() + margin +
                    actors[next -1].getWidth();
            }
        }

        locs[next++] = new Point(x, y);
        Point rp = getRootLocation();
        rp.translate(x, y);
        return rp;
    }
    
    /**
     * @param actor
     */
    public void bind(Actor actor) {
        for (int i = 0; i < next; ++i) {
            if (actors[i] == actor) {
                bound[i] = true;
                actor.setParent(this);
                actor.setLocation(locs[i]);
                
                if (getActorId() == -1) {
                    //Tracker
                    Point p = getRootLocation();
                    Tracker.trackTheater(TrackerClock.currentTimeMillis(), Tracker.APPEAR, getActorId(), Tracker.RECTANGLE, new int[] {p.x}, new int[] {p.y}, getWidth(), getHeight(), 0, -1, getDescription());
                } else {
                    //Tracker
                    Point p = getRootLocation();
                    Tracker.trackTheater(TrackerClock.currentTimeMillis(), Tracker.MODIFY, getActorId(), Tracker.RECTANGLE, new int[] {p.x}, new int[] {p.y}, getWidth(), getHeight(), 0, -1, getDescription());
                }

                return;
            }
        }
        throw new RuntimeException("Couldn't bind Actor in Method Invocation");
    }

    /**
     * @param g
     */
    public void paintActors(Graphics g) {
        int n = next;
        for (int i = 0; i < n; ++i) {
            if (bound[i]) {
                g.translate(locs[i].x, locs[i].y);
                actors[i].paintActor(g);
                g.translate(-locs[i].x, -locs[i].y);
            }
        }
    }

    /* (non-Javadoc)
     * @see jeliot.theater.Actor#paintActor(java.awt.Graphics)
     */
    public void paintActor(Graphics g) {
        //int w = getWidth();
        //int h = getHeight();

        // draw background
        //g.setColor(bgcolor);
        //g.fillRect(2, 2, w-4, h-4);

        // draw border
        //g.setColor(darkColor);
        //g.drawRect(1, 1, w-3, h-3);
        g.setColor(fgcolor);
        //g.drawRect(0, 0, w-1, h-1);

        // draw text
        g.setFont(getFont());
                
        if (next > 0) {        
            g.drawString(getName() + "(", namex, namey);
            
            for (int i = 0; i < next; i++) {
                if (i != (next-1)) {
                    if (actors[i] instanceof ReferenceActor) {
                        g.drawString(",",
                                 locs[i].x +
                                 actors[i].getWidth() +
                                 ((ReferenceActor) actors[i]).getReferenceWidth() +
                                 margin,
                                 namey);
                    
                    } else {
                        g.drawString(",",
                                 locs[i].x +
                                 actors[i].getWidth() +
                                 margin,
                                 namey);
                    }
                }
            }
                   
            if (actors[next-1] instanceof ReferenceActor) {
                g.drawString(")",
                             locs[next-1].x +
                             actors[next-1].getWidth() +
                             ((ReferenceActor) actors[next-1]).getReferenceWidth() +
                             margin,
                             namey);
                    
            } else {
                g.drawString(")",
                             locs[next-1].x +
                             actors[next-1].getWidth() +
                             margin,
                             namey);
                    }
        } else {
            g.drawString(getName() + "()", namex, namey);           
        }

        paintActors(g);
    }
    
    /* (non-Javadoc)
     * @see jeliot.theater.Actor#calculateSize()
     */
    public void calculateSize() {
        // Get the size of the name.
        FontMetrics fm = getFontMetrics();
        nameh = fm.getHeight();
        namew = fm.stringWidth(getName() + "(");

        int n = next;
        int maxh = insets.top + titlemargin + nameh;
        int maxw = insets.left + namew;
        for (int i = 0; i < n; ++i) {
            int h = locs[i].y + actors[i].getHeight();
            maxh = h > maxh ? h : maxh;
            int w = locs[i].x + actors[i].getWidth();
            maxw = w > maxw ? w : maxw;
        }
        namex = insets.left;
        namey = insets.top + nameh;
        setSize(maxw + insets.right, maxh + insets.bottom);
    }

    /* (non-Javadoc)
     * @see jeliot.theater.ActorContainer#removeActor(jeliot.theater.Actor)
     */
    public void removeActor(Actor actor) {
        int n = next;
        for (int i = 0; i < n; ++i) {
            if (actors[i] == actor) {
                bound[i] = false;
            }
        }
    }
    
    /* (non-Javadoc)
     * @see jeliot.theater.Actor#setLight(int)
     */
    public void setLight(int light) {
        super.setLight(light);
        int n = next;
        for (int i = 0; i < n; ++i) {
            actors[i].setLight(light);
        }
    }
    
    public Animation disappear() {

        //Tracker
        for (int i = 0; i < actors.length; i++) {
            if (actors[i] != null) {
                actors[i].disappear();
            }
        }
        Point p = getRootLocation();
        Tracker.trackTheater(TrackerClock.currentTimeMillis(),
                Tracker.DISAPPEAR, getActorId(), Tracker.RECTANGLE,
                new int[] { p.x }, new int[] { p.y }, getWidth(), getHeight(),
                0, -1, getDescription());

        return null;
    }

    public abstract String toString();

    public String parametersToString() {
        StringBuffer sb = new StringBuffer();
        if (this.getParameterCount() == 0) {
            sb.append("without parameters.");
        } if (this.getParameterCount() > 0) {
            if (this.getParameterCount() == 1) {
                sb.append("with parameter: ");
            }
            if (this.getParameterCount() > 1) {
                sb.append("with parameters: ");
            }
            for (int i = 0; i < this.actors.length && this.actors[i] != null; i++) {
                sb.append(actors.toString());
                sb.append(", ");
            }
        }
        return sb.toString();
    }
    
    public int getParameterCount() {
        return parameterCount;
    }

}
