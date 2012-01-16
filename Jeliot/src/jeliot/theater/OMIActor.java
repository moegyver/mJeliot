package jeliot.theater;

import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Point;

import jeliot.tracker.Tracker;
import jeliot.tracker.TrackerClock;

/**
 * OMIActor represents graphically the object method invocation. The
 * actor shows the object reference, the method name and the parameters
 * in a similar way as Java syntax just replaces the variable references with
 * their actual values.
 *  
 * @author Niko Myller
 * 
 * @see jeliot.theater.SMIActor
 * @see jeliot.theater.ObjectStage
 */
public class OMIActor extends MIActor {

    //DOC: Document!

    /**
     *
     */
    private Actor thisActor;

    /**
     *
     */
    private Point thisActorPoint;

    /**
     *
     */
    private boolean thisActorBound = false;

    /**
     *
     */
    private int thisActorw = 0;

    /**
     *
     */
    private int thisActorh = 0;

    /**
     * @param name
     * @param n
     */
    public OMIActor(String name, int n) {
        super(name, n);
        setDescription("object method invocation: " + name);
    }

    /**
     * @param actor
     * @return
     */
    public Point reserveThisActor(Actor actor) {
        thisActor = actor;

        int y = insets.top;
        int x = insets.left;

        thisActorPoint = new Point(x, y);
        Point rp = getRootLocation();
        rp.translate(x, y);
        return rp;
    }

    /**
     * 
     */
    public void bindThisActor() {
        thisActorBound = true;
        thisActor.setParent(this);
        thisActor.setLocation(thisActorPoint);

        if (getActorId() == -1) {
            //Tracker
            Point p = getRootLocation();
            Tracker.trackTheater(TrackerClock.currentTimeMillis(),
                    Tracker.APPEAR, getActorId(), Tracker.RECTANGLE,
                    new int[] { p.x }, new int[] { p.y }, getWidth(),
                    getHeight(), 0, -1, getDescription());
        } else {
            //Tracker
            Point p = getRootLocation();
            Tracker.trackTheater(TrackerClock.currentTimeMillis(),
                    Tracker.MODIFY, getActorId(), Tracker.RECTANGLE,
                    new int[] { p.x }, new int[] { p.y }, getWidth(),
                    getHeight(), 0, -1, getDescription());
        }

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
        int x = insets.left + thisActorw + margin + namew + margin;

        if (next > 0) {
            x = locs[next - 1].x + margin + getCommaMargin() + margin
                    + actors[next - 1].getWidth();

            if (actors[next - 1] instanceof ReferenceActor) {
                x += ((ReferenceActor) actors[next - 1]).getReferenceWidth();
            }

        }

        locs[next++] = new Point(x, y);
        Point rp = getRootLocation();
        rp.translate(x, y);
        return rp;
    }

    /**
     * @param g
     */
    public void paintActors(Graphics g) {
        super.paintActors(g);
        if (thisActorBound) {
            g.translate(thisActorPoint.x, thisActorPoint.y);
            thisActor.paintActor(g);
            g.translate(-thisActorPoint.x, -thisActorPoint.y);
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
                if (i != (next - 1)) {
                    if (actors[i] instanceof ReferenceActor) {
                        g.drawString(",", locs[i].x
                                + actors[i].getWidth()
                                + ((ReferenceActor) actors[i])
                                        .getReferenceWidth() + margin, namey);

                    } else {
                        g.drawString(",", locs[i].x + actors[i].getWidth()
                                + margin, namey);
                    }
                }
            }

            if (actors[next - 1] instanceof ReferenceActor) {
                g.drawString(")", locs[next - 1].x
                        + actors[next - 1].getWidth()
                        + ((ReferenceActor) actors[next - 1])
                                .getReferenceWidth() + margin, namey);

            } else {
                g.drawString(")", locs[next - 1].x
                        + actors[next - 1].getWidth() + margin, namey);
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
        int parenthesisw = fm.stringWidth("(");

        //        int thisActorw = 0;
        //        int thisActorh = 0;

        if (thisActor != null) {
            thisActorh = thisActor.getHeight();
            if (thisActor instanceof ReferenceActor) {
                thisActorw = thisActor.getWidth()
                        + ((ReferenceActor) thisActor).getReferenceWidth();
            } else {
                thisActorw = thisActor.getWidth();
            }
        }

        int n = next;
        int maxh = insets.top + titlemargin + nameh;
        maxh = (maxh > (insets.top + thisActorh)) ? maxh
                : (insets.top + thisActorh);
        int maxw = insets.left + thisActorw + namew + parenthesisw;
        for (int i = 0; i < n; ++i) {
            int h = locs[i].y + actors[i].getHeight();
            maxh = h > maxh ? h : maxh;
            int w = locs[i].x + actors[i].getWidth() + parenthesisw;
            maxw = w > maxw ? w : maxw;
        }
        namex = thisActorw + margin + insets.left;
        namey = insets.top + nameh;
        setSize(maxw + insets.right, maxh + insets.bottom);
    }

    /* (non-Javadoc)
     * @see jeliot.theater.ActorContainer#removeActor(jeliot.theater.Actor)
     */
    public void removeActor(Actor actor) {
        super.removeActor(actor);

        if (actor == thisActor) {
            thisActor = null;
            thisActorBound = false;
        }
    }

    /* (non-Javadoc)
     * @see jeliot.theater.Actor#setLight(int)
     */
    public void setLight(int light) {
        super.setLight(light);
        this.thisActor.setLight(light);
    }

    
    
    public Animation disappear() {
        if (thisActor != null) {
            thisActor.disappear();
        }
        Animation a = super.disappear();
        return a;
    }
    
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("Method of ");
        if (thisActorBound) {
            if (this.thisActor instanceof ReferenceActor) {
                sb.append(((ReferenceActor) this.thisActor).getInstanceActor().toString());
            } else if (this.thisActor instanceof ValueActor) {
                sb.append(this.thisActor.toString()); 
            }
            sb.append(" ");
        }
        sb.append(getName());
        sb.append(" is called ");
        sb.append(parametersToString());
        return sb.toString();
    }

}
