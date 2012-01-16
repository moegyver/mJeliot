package jeliot.theater;

import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Point;

/**
 * ReferenceVariableActor represents graphically the
 * variables of the reference type. It can bind
 * ReferenceActor instances and render them.  
 * 
 * @author Pekka Uronen
 * @author Niko Myller
 * 
 * @see jeliot.lang.Variable
 * @see jeliot.theater.VariableActor
 * @see jeliot.theater.VariableInArrayActor
 * @see jeliot.theater.MethodStage
 * @see jeliot.theater.ObjectStage
 */
public class ReferenceVariableActor extends VariableActor {

//  DOC: Document!

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

    /* (non-Javadoc)
	 * @see jeliot.theater.Actor#paintActor(java.awt.Graphics)
	 */
	public void paintActor(Graphics g) {
        int w = width;
        int h = height;
        int bw = borderWidth;

        // fill background
        g.setColor( (light == HIGHLIGHT) ?
                darkColor :
                bgcolor );
        g.fillRect(bw, bw, w - 2 * bw, h - 2 * bw);

        // draw the name
        g.setFont(font);
        g.setColor((light == HIGHLIGHT) ?
                lightColor :
                fgcolor);
        g.drawString(getLabel(), namex, namey);

        // draw border
        ActorContainer parent = getParent();
        g.setColor( (parent instanceof Actor)   ?
                ( (Actor)parent ).darkColor     :
                fgcolor );
        g.drawLine(0, 0, w-1, 0);
        g.drawLine(0, 0, 0, h-1);
        g.setColor( (parent instanceof Actor)   ?
                ( (Actor)parent ).lightColor     :
                fgcolor );
        g.drawLine(1, h-1, w-1, h-1);
        g.drawLine(w-1, 1, w-1, h-1);

        g.setColor(fgcolor);
        g.drawRect(1, 1, w-3, h-3);
        g.setColor(darkColor);
        g.drawLine(2, h-3, w-3, h-3);
        g.drawLine(w-3, 2, w-3, h-3);
        g.setColor(lightColor);
        g.drawLine(2, 2, w-3, 2);
        g.drawLine(2, 2, 2, h-3);

        // draw link
        if (refActor == null) {

            // draw reference area
            g.setColor(darkColor);
            g.fillRect(w - bw - refWidth, bw, refWidth, h - 2 * bw);

            g.setColor(fgcolor);
            int a = w - 2 + refLen;

            //System.out.println("w = "+w +", h = " +h);
            g.drawLine(w-2, h/2-1, a, h/2-1);
            g.drawLine(w-2, h/2+1, a, h/2+1);
            g.drawLine(a, h/2 - 6, a, h/2 + 6);
            g.drawLine(a + 2, h/2 - 6, a + 2, h/2 + 6);

            g.setColor(bgcolor);
            g.drawLine(w-2, valueh/2, a, valueh/2);
            g.drawLine(a+1, valueh/2 - 6, a+1, valueh/2 + 6);

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

    /* (non-Javadoc)
	 * @see jeliot.theater.Actor#calculateSize()
	 */
	public void calculateSize() {
        FontMetrics fm = getFontMetrics();
        int sw = fm.stringWidth(getLabel());
        int sh = fm.getHeight();

        setSize(2 * borderWidth + insets.right + insets.left +
                refWidth + sw,
                insets.top + insets.bottom + 4 * borderWidth +
                Math.max(valueh, sh));
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
        //int w = actor.width;
        //int h = actor.height;
        rp.translate(width - borderWidth - refWidth - 3,
                    (height - actor.height)/2 + borderWidth);
        return rp;
    }

    /* (non-Javadoc)
	 * @see jeliot.theater.VariableActor#bind()
	 */
	public void bind() {
        this.refActor = this.reservedRefActor;
        refActor.setParent(this);

        refActor.setLocation(width - borderWidth - refWidth - 3,
                            (height - refActor.height)/2 + borderWidth);
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
	public void theatreResized() {
        if (refActor != null) {
            refActor.calculateBends();
        }
    }



}
