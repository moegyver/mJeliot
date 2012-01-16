/*
 * Created on Jun 26, 2004
 */
package jeliot.theater;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;

/**
 * @author nmyller
 */
public class ReferenceVariableInArrayActor extends VariableInArrayActor {

    //DOC: Document!

    /**
     *
     */
    private int refWidth = ActorFactory.typeValWidth[9];

    /**
     *
     */
    private int refLen = 18;

    /**
     *
     */
    private ReferenceActor refActor;

    /**
     *
     */
    private ReferenceActor reservedRefActor;

    /**
     * @param arrayActor
     * @param name
     */
    public ReferenceVariableInArrayActor(ArrayActor arrayActor, String name) {
        super(arrayActor, name);
        //setParent(arrayActor);
        //this.name = name;
    }

    /* (non-Javadoc)
     * @see jeliot.theater.Actor#paintActor(java.awt.Graphics)
     */
    public void paintActor(Graphics g) {
        int w = width;
        int h = height;
        int bw = borderWidth;

        ArrayActor array = (ArrayActor) getParent();

        // fill background
        g.setColor((light == HIGHLIGHT) ? array.darkColor : array.bgcolor);
        g.fillRect(0, 0, getIndexWidth(), height);
        g.setColor(valueColor);
        g.fillRect(getIndexWidth() + 2, 0, width - 2 - getIndexWidth(), height);

        // draw indices
        g.setFont(array.getFont());
        g.setColor((light == HIGHLIGHT) ? Color.white : array.darkColor);
        g.drawString(getLabel(), namex, namey);

        // draw value
        //int x = value.getX();
        //int y = value.getY();
        //g.translate(x, y);
        //value.paintValue(g);
        //g.translate(-x, -y);

        // draw link
        if (refActor == null) {

            // draw reference area
            g.setColor(darkColor);
            g.fillRect(w - bw - refWidth, bw, refWidth, h - 2 * bw);

            g.setColor(fgcolor);
            int a = w - 2 + refLen;

            //System.out.println("w = "+w +", h = " +h);
            g.drawLine(w - 2, h / 2 - 1, a, h / 2 - 1);
            g.drawLine(w - 2, h / 2 + 1, a, h / 2 + 1);
            g.drawLine(a, h / 2 - 6, a, h / 2 + 6);
            g.drawLine(a + 2, h / 2 - 6, a + 2, h / 2 + 6);

            g.setColor(bgcolor);
            g.drawLine(w - 2, valueh / 2, a, valueh / 2);
            g.drawLine(a + 1, valueh / 2 - 6, a + 1, valueh / 2 + 6);

        } else {

            int actx = refActor.getX();
            int acty = refActor.getY();
            g.translate(actx, acty);
            refActor.paintActor(g);
            g.translate(-actx, -acty);

            /*
             Point p = getRootLocation();
             g.translate(-p.x, -p.y);
             refActor.paintActor(g);
             g.translate(p.x, p.y);
             */

        }
    }

    /* (non-Javadoc)
     * @see jeliot.theater.Actor#setBounds(int, int, int, int)
     */
    public void setBounds(int x, int y, int w, int h) {
        int oldw = getWidth();
        int oldh = getHeight();
        super.setBounds(x, y, w, h);

        if (w != oldw || h != oldh) {
            calcLabelPosition();
        }
    }

    /**
     * @param refActor
     */
    public void setReference(ReferenceActor refActor) {
        this.refActor = refActor;
    }

    /* (non-Javadoc)
     * @see jeliot.theater.VariableActor#reserve(jeliot.theater.ValueActor)
     */
    public Point reserve(ValueActor actor) {
        if (actor instanceof ReferenceActor) {
            return reserve((ReferenceActor) actor);
        }
        return super.reserve(actor);
    }

    /**
     * @param actor
     * @return
     */
    public Point reserve(ReferenceActor actor) {
        this.reservedRefActor = actor;
        Point rp = getRootLocation();
        int w = actor.width;
        int h = actor.height;
        rp.translate(width - borderWidth - refWidth - 3, (height - actor.height) / 2 /*+ borderWidth / 2*/);
        //rp.translate(valuex + (valuew - w) / 2, valuey + (valueh - h) / 2);
        return rp;
    }

    /* (non-Javadoc)
     * @see jeliot.theater.VariableActor#bind()
     */
    public void bind() {
        this.refActor = this.reservedRefActor;
        refActor.setParent(this);

        refActor.setLocation(width - borderWidth - refWidth - 3, (height - refActor.height) / 2 /*+ borderWidth / 2 */);
        //refActor.setLocation(valuex + (valuew - refActor.width) / 2, valuey + (valueh - refActor.height) / 2);
    }

    /**
     * @param actor
     */
    public void setValue(ReferenceActor actor) {
        this.reservedRefActor = actor;
        bind();
    }

    /* (non-Javadoc)
     * @see jeliot.theater.VariableActor#getValue()
     */
    public ValueActor getValue() {
        ValueActor act = (ReferenceActor) this.refActor.clone();
        return act;
    }

    /**
     * 
     */
    public void theaterResized() {
        if (refActor != null) {
            refActor.calculateBends();
        }
    }

    /* (non-Javadoc)
     * @see jeliot.theater.VariableActor#calcLabelPosition()
     */
    protected void calcLabelPosition() {
    }

}
