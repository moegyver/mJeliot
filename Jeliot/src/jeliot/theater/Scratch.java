package jeliot.theater;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.util.Iterator;
import java.util.Vector;

/**
 * Scratch controls the expression evaluation area. It
 * allocates the space for each <code>ExpressionEvaluationActor</code>
 * and possible other <code>Actor</code>s that area there
 * temporarily.
 * 
 * @author Pekka Uronen
 * @author Niko Myller
 */
public class Scratch extends Actor implements ActorContainer {

    //  DOC: Document!
    /**
     *
     */
    private Vector exprs = new Vector();

    /**
     *
     */
    private Vector crap = new Vector();

    /**
     *
     */
    private Point memloc;
    
    
    /**
     *
     */
    private Vector crapRemovers = new Vector();

    /**
     * @param number
     * @return
     */
    public ExpressionActor findActor(long number) {
        for (int i = 0; i < exprs.size(); i++) {
            ExpressionActor actor = (ExpressionActor) exprs.elementAt(i);
            if (actor.getId() == number) {
                return actor;
            }
        }
        return null;
    }

    /**
     * Second parameter added for Jeliot 3 to identify the expressions.
     * @param n
     * @param id
     * @return
     */
    public ExpressionActor getExpression(int n, long id) {
        ExpressionActor ea = new ExpressionActor(n, id);
        accommodate(ea);
        return ea;
    }

    /* (non-Javadoc)
     * @see jeliot.theater.Actor#fly(java.awt.Point)
     */
    public Animation fly(Point p) {
        return this.fly(p, 0);
    }

    /**
     * @param actor
     * @return
     */
    public Point accommodate(Actor actor) {
        actor.setParent(this);
        int size = exprs.size();
        int y = 0;
        if (size > 0) {
            Actor prev = (Actor) exprs.lastElement();
            y = prev.getY() + prev.getHeight() + 4;
        }
        actor.setLocation(0, y);
        exprs.addElement(actor);
        //setSize(getWidth(), y + actor.getHeight());
        return actor.getRootLocation();
    }

    /**
     * @return
     */
    public Dimension getSize() {
        int w = 0;
        int h = 0;
        for (Iterator i = exprs.iterator(); i.hasNext();) {
            Actor actor = (Actor) i.next();
            if (w < actor.getWidth()) {
                w = actor.getWidth();
            }
            if (h < actor.getY() + actor.getHeight()) {
                h = actor.getY() + actor.getHeight();
            }
        }
        return new Dimension(w, h);
        //return new Dimension(width, height);
    }

    /**
     * @param d
     */
    public void setSize(Dimension d) {
        //setSize(d.width, d.height);
    }

    /**
     * @param w
     * @param h
     */
    public void setSize(int w, int h) {
        //this.width = w;
        //this.height = h;
    }

    /**
     * @return
     */
    public int getWidth() {
        return getSize().width;
    }

    /**
     * @return
     */
    public int getHeight() {
        return getSize().height;
    }

    /**
     * @return
     */
    public Point getSpot() {
        int y = 0;
        int size = exprs.size();
        if (size > 0) {
            Actor prev = (Actor) exprs.elementAt(size - 1);
            y = prev.getY() + prev.getHeight() + 4;
        }
        return new Point(getX(), y + getY());
    }

    /**
     * 
     */
    public void memorizeLocation() {
        memloc = getLocation();
    }

    /**
     * @return
     */
    public Point recallLocation() {
        return memloc;
    }

    /* (non-Javadoc)
     * @see jeliot.theater.Actor#paintActor(java.awt.Graphics)
     */
    public void paintActor(Graphics g) {
        paintActors(g, exprs);
    }

    /**
     * @param actor
     */
    public void registerCrap(Actor actor) {
        crap.addElement(actor);
    }

    /**
     * @param remover
     */
    public void registerCrapRemover(Runnable remover) {
        crapRemovers.addElement(remover);
    }

    /**
     * @param actor
     */
    public void removeCrap(Actor actor) {
        crap.removeElement(actor);
        ActorContainer cont = actor.getParent();
        if (cont instanceof Theater) {
            cont.removeActor(actor);
            ((Theater) cont).removePassive(actor);
        }
        actor.disappear();
    }

    /**
     * 
     */
    public void removeCrap() {
        int n = crap.size();
        for (int i = 0; i < n; ++i) {
            Actor a = (Actor) crap.elementAt(i);
            ActorContainer cont = a.getParent();
            if (cont instanceof Theater) {
                cont.removeActor(a); //Maybe this should be done even though the container is not a theater object.
                ((Theater) cont).removePassive(a);
            }
            a.disappear();
        }
        crap.removeAllElements();

        int m = crapRemovers.size();
        for (int i = 0; i < m; ++i) {
            Runnable r = (Runnable) crapRemovers.elementAt(i);
            r.run();
        }
        crapRemovers.removeAllElements();
    }

    /* (non-Javadoc)
     * @see jeliot.theater.ActorContainer#removeActor(jeliot.theater.Actor)
     */
    public void removeActor(Actor actor) {
        exprs.removeElement(actor);
    }

    /**
     * 
     */
    public void clean() {
        for (Iterator i = exprs.iterator(); i.hasNext();) {
            ((Actor) i.next()).disappear();
        }
        exprs.removeAllElements();
        removeCrap();

    }

    /* (non-Javadoc)
     * @see jeliot.theater.Actor#disappear()
     */
    public Animation disappear() {
        for (Iterator i = exprs.iterator(); i.hasNext();) {
            ((Actor) i.next()).disappear();
        }

        //Should the scratch also disappear?

        return null;
    }
}
