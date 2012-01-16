package jeliot.theater;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Polygon;

/**
 * ReferenceActor shows the reference to some InstanceActor (e.g. ArrayActor or
 * ObjectStage). They can be assigned to the ReferenceVariableActor instances or any
 * other instance that is derived from the ReferenceVariableActor.
 * 
 * @author Pekka Uronen
 * @author Niko Myller
 * 
 * @see jeliot.theater.ReferenceVariableActor
 */
public class ReferenceActor extends ValueActor {

    //  DOC: Document!

    /**
     *
     */
    private InstanceActor instance = null;

    /**
     *
     */
    private VariableActor variable = null;

    /**
     * Reference width is the width of the rectangle
     * in the variable end of the reference.
     */
    private static int refWidth = ActorFactory.typeValWidth[9];

    /**
     * Length of the null reference line and also the base for the
     * first part of the reference when not null.
     */
    private static int refLen = 18;

    /**
     * Used to make the first part of the reference line different in
     * length.
     */
    private int refWidthRandom = 12;

    /**
     *
     */
    private boolean instVarConnect = false;

    /**
     *
     */
    private Point[] bend;

    /**
     *
     */
    private Point[] arrowhead;

    /**
     *
     */
    private Polygon arrowheadPolygon1;

    /**
     *
     */
    private Polygon arrowheadPolygon2;

    /**
     * 
     */
    public ReferenceActor() {
        refWidthRandom += (int) (Math.random() * 15);
    }

    /**
     * @param inst
     */
    public ReferenceActor(InstanceActor inst) {
        this();
        this.instance = inst;
        if (inst != null) {
            inst.addReference(this);
        }
    }

    /**
     * @param inst
     * @param instVarConnect
     */
    public ReferenceActor(InstanceActor inst, boolean instVarConnect) {
        this(inst);
        this.instVarConnect = instVarConnect;
    }

    /**
     * @param inst
     * @param var
     */
    public ReferenceActor(InstanceActor inst, VariableActor var) {
        this(inst);
        this.variable = var;
    }

    /**
     * @param inst
     * @param var
     * @param instVarConnect
     */
    public ReferenceActor(InstanceActor inst, VariableActor var,
            boolean instVarConnect) {
        this(inst, var);
        this.instVarConnect = instVarConnect;
    }

    /**
     * @return
     */
    public InstanceActor getInstanceActor() {
        return this.instance;
    }

    /**
     * @param inst
     */
    public void setInstanceActor(InstanceActor inst) {
        this.instance = inst;
    }

    /**
     * @param var
     */
    public void setVariableActor(VariableActor var) {
        this.variable = var;
    }

    /**
     * @return
     */
    public VariableActor getVariableActor() {
        return this.variable;
    }

    /* (non-Javadoc)
     * @see jeliot.theater.Actor#paintActor(java.awt.Graphics)
     */
    public void paintActor(Graphics g) {

        Color bc = bgcolor;
        Color fc = fgcolor;

        int h = height;
        //Point p = getRootLocation();

        if (instance == ObjectStage.OUTSIDE_OBJECT) {
            // draw reference area
            g.setColor(darkColor);
            g.fillRect(0, 0, refWidth, h);
            //g.setColor(bc);
            //g.fillRect(p.x+1, p.y+1, refWidth-2, h-2);
            g.setColor(fc);
            g.drawRect(0, 0, refWidth, h);

            g.setColor(fgcolor);

            int a = refWidth - 3;
            int b = a + refLen;

            //Borders
            g.drawLine(a, h / 2 - 1, b, h / 2 - 1);
            g.drawLine(a, h / 2 + 1, b, h / 2 + 1);

            g.fillRect(b + 3, h / 2 - 1, 3, 3);
            g.fillRect(b + 7, h / 2 - 1, 3, 3);
            g.fillRect(b + 11, h / 2 - 1, 3, 3);
            g.fillRect(b + 15, h / 2 - 1, 3, 3);
            g.fillRect(b + 15, h / 2 + 3, 3, 3);
            g.fillRect(b + 15, h / 2 + 7, 3, 3);

            //Insides
            g.setColor(bgcolor);
            g.drawLine(a, h / 2, b, h / 2);

            g.drawLine(b + 4, h / 2, b + 4, h / 2);
            g.drawLine(b + 8, h / 2, b + 8, h / 2);
            g.drawLine(b + 12, h / 2, b + 12, h / 2);
            g.drawLine(b + 16, h / 2, b + 16, h / 2);
            g.drawLine(b + 16, h / 2 + 4, b + 16, h / 2 + 4);
            g.drawLine(b + 16, h / 2 + 8, b + 16, h / 2 + 8);

        } else if (instance != null) {

            bc = instance.bgcolor;
            fc = instance.fgcolor;

            // draw reference area
            g.setColor(darkColor);
            g.fillRect(0, 0, refWidth, h);
            //g.setColor(bc);
            //g.fillRect(p.x+1, p.y+1, refWidth-2, h-2);
            g.setColor(fc);
            g.drawRect(0, 0, refWidth, h);

            Point vp = this.getRootLocation();

            g.translate(-vp.x, -vp.y);

            calculateBends();

            int n = bend.length;

            for (int i = 1; i < n; ++i) {
                Point p1 = bend[i - 1];
                Point p2 = bend[i];

                g.setColor(fc);
                if (p1.y == p2.y) {
                    g.drawLine(p1.x, p1.y - 1, p2.x, p1.y - 1);
                    g.drawLine(p1.x, p1.y + 1, p2.x, p1.y + 1);
                } else if (p2.x == p1.x) {
                    g.drawLine(p1.x - 1, p1.y, p2.x - 1, p2.y);
                    g.drawLine(p1.x + 1, p1.y, p2.x + 1, p2.y);
                }
            }

            for (int i = 1; i < n; ++i) {
                Point p1 = bend[i - 1];
                Point p2 = bend[i];
                g.setColor(bc);
                g.drawLine(p1.x, p1.y, p2.x, p2.y);
            }

            //Here is drawn something that shows that
            //the reference is pointing to this exact instance.
            g.setColor(fc);
            g.fillPolygon(arrowheadPolygon1);
            g.setColor(bc);
            g.fillPolygon(arrowheadPolygon2);

            g.translate(vp.x, vp.y);

        } else {

            // draw reference area
            g.setColor(darkColor);
            g.fillRect(0, 0, refWidth, h);
            //g.setColor(bc);
            //g.fillRect(p.x+1, p.y+1, refWidth-2, h-2);
            g.setColor(fc);
            g.drawRect(0, 0, refWidth, h);

            g.setColor(fgcolor);

            int a = refWidth - 3;
            int b = a + refLen;
            //System.out.println("h = " +h);

            /*
             * There reference that is drawn below
             *        | .
             *  ------| | |
             *        | '
             */
            //Borders
            g.drawLine(a, h / 2 - 1, b, h / 2 - 1);
            g.drawLine(a, h / 2 + 1, b, h / 2 + 1);

            g.drawLine(b, h / 2 - 8, b, h / 2 + 8);
            g.drawLine(b + 2, h / 2 - 8, b + 2, h / 2 + 8);

            g.drawLine(b + 5, h / 2 - 5, b + 5, h / 2 + 5);
            g.drawLine(b + 7, h / 2 - 5, b + 7, h / 2 + 5);

            g.drawLine(b + 10, h / 2 - 2, b + 10, h / 2 + 2);
            g.drawLine(b + 12, h / 2 - 2, b + 12, h / 2 + 2);

            //Insides
            g.setColor(bgcolor);
            g.drawLine(a, h / 2, b, h / 2);

            g.drawLine(b + 1, h / 2 - 8, b + 1, h / 2 + 8);
            g.drawLine(b + 6, h / 2 - 5, b + 6, h / 2 + 5);
            g.drawLine(b + 11, h / 2 - 2, b + 11, h / 2 + 2);

        }
    }

    /**
     * 
     */
    public void calculateBends() {
        Point ip = instance.getRootLocation();
        Point vp = this.getRootLocation();

        int position = instance.getReferencePosition();

        int iy1 = ip.y;
        int iy2 = iy1 + instance.getHeight();
        int vy1 = vp.y;
        int vy2 = vy1 + height;

        int ix = ip.x;
        int ix2 = ix + instance.getWidth();
        int vx = vp.x + refWidth;

        int xp2 = vx + refLen + refWidthRandom;

        if (!instVarConnect) {
            if (xp2 < ix) {
                bend = new Point[5];
                bend[0] = new Point(vx - 3, (vy1 + vy2) / 2);
                bend[1] = new Point(xp2, bend[0].y);
                bend[2] = new Point(bend[1].x, iy1 - 20 - position * 10);
                bend[3] = new Point((ix + ix2) / 2, bend[2].y);
                bend[4] = new Point(bend[3].x, iy1);

                calculateArrowhead(3);
                //                bend = new Point[4];
                //                bend[0] = new Point(vx - 3, (vy1 + vy2) / 2);
                //                bend[1] = new Point(xp2 /*- (vy1/6)*/, bend[0].y);
                //                bend[2] = new Point(bend[1].x, iy1 + 12);
                //                bend[3] = new Point(ix - 3, bend[2].y);
                //                calculateArrowhead(2);
            } else if (xp2 > ix2) {
                bend = new Point[5];
                bend[0] = new Point(vx - 3, (vy1 + vy2) / 2);
                bend[1] = new Point(xp2, bend[0].y);
                bend[2] = new Point(bend[1].x, iy1 - 20 - position * 10);
                bend[3] = new Point((ix + ix2) / 2, bend[2].y);
                bend[4] = new Point(bend[3].x, iy1);

                calculateArrowhead(3);
                //                bend = new Point[4];
                //                bend[0] = new Point(vx - 3, (vy1 + vy2) / 2);
                //                bend[1] = new Point(xp2 /*- (vy1/6)*/, bend[0].y);
                //                bend[2] = new Point(bend[1].x, iy1 + 12);
                //                bend[3] = new Point(ix2 - 3, bend[2].y);
                //                calculateArrowhead(4);
            } else {
                bend = new Point[5];
                bend[0] = new Point(vx - 3, (vy1 + vy2) / 2);
                bend[1] = new Point(xp2, bend[0].y);
                bend[2] = new Point(bend[1].x, iy1 - 20 - position * 10);
                bend[3] = new Point((ix + ix2) / 2, bend[2].y);
                bend[4] = new Point(bend[3].x, iy1);

                calculateArrowhead(3);
                //                bend = new Point[3];
                //                bend[0] = new Point(vx - 3, (vy1 + vy2) / 2);
                //                bend[1] = new Point(xp2 /*- (vy1/6)*/, bend[0].y);
                //                bend[2] = new Point(bend[1].x, iy1);
                //                calculateArrowhead(3);
            }
        } else {
            bend = new Point[5];
            bend[0] = new Point(vx - 3, (vy1 + vy2) / 2);
            bend[1] = new Point(xp2, bend[0].y);
            bend[2] = new Point(bend[1].x, iy1 - 20 - position * 10);
            bend[3] = new Point((ix + ix2) / 2, bend[2].y);
            bend[4] = new Point(bend[3].x, iy1);

            calculateArrowhead(3);

            //bend = new Point[4];
            //bend[0] = new Point(vx - 3, (vy1 + vy2) / 2);
            //bend[1] = new Point(vx + refLen + refWidthRandom /*- (vy1/6)*/, bend[0].y);
            //bend[2] = new Point(bend[1].x, iy1 + 12);
            //bend[3] = new Point(ix + 3, bend[2].y);
            //calculateArrowhead(2);
        }

    }

    /**
     *  
     * dir is 1 up, 2 right, 3 down and 4 left.
     * @param dir
     */
    public void calculateArrowhead(int dir) {
        int n = bend.length - 1;

        switch (dir) {
        // left
        case 4: {
            arrowhead = new Point[3];
            arrowhead[0] = new Point(bend[n]);
            arrowhead[1] = new Point(bend[n]);
            arrowhead[2] = new Point(bend[n]);
            arrowhead[0].translate(-3, 0);
            arrowhead[1].translate(10, -7);
            arrowhead[2].translate(10, 7);

            arrowheadPolygon1 = new Polygon();
            for (int i = 0; i < 3; i++) {
                arrowheadPolygon1.addPoint(arrowhead[i].x, arrowhead[i].y);
            }

            arrowhead[0].translate(3, 0);
            arrowhead[1].translate(-2, 3);
            arrowhead[2].translate(-2, -3);

            arrowheadPolygon2 = new Polygon();
            for (int i = 0; i < 3; i++) {
                arrowheadPolygon2.addPoint(arrowhead[i].x, arrowhead[i].y);
            }
            break;
        }

            // right
        case 2: {
            arrowhead = new Point[3];
            arrowhead[0] = new Point(bend[n]);
            arrowhead[1] = new Point(bend[n]);
            arrowhead[2] = new Point(bend[n]);
            arrowhead[0].translate(3, 0);
            arrowhead[1].translate(-10, -7);
            arrowhead[2].translate(-10, 7);

            arrowheadPolygon1 = new Polygon();
            for (int i = 0; i < 3; i++) {
                arrowheadPolygon1.addPoint(arrowhead[i].x, arrowhead[i].y);
            }

            arrowhead[0].translate(-3, 0);
            arrowhead[1].translate(2, 3);
            arrowhead[2].translate(2, -3);

            arrowheadPolygon2 = new Polygon();
            for (int i = 0; i < 3; i++) {
                arrowheadPolygon2.addPoint(arrowhead[i].x, arrowhead[i].y);
            }
            break;
        }
            // down
        case 3: {
            arrowhead = new Point[3];
            arrowhead[0] = new Point(bend[n]);
            arrowhead[1] = new Point(bend[n]);
            arrowhead[2] = new Point(bend[n]);
            arrowhead[0].translate(0, 0);
            arrowhead[1].translate(-7, -13);
            arrowhead[2].translate(7, -13);

            arrowheadPolygon1 = new Polygon();
            for (int i = 0; i < 3; i++) {
                arrowheadPolygon1.addPoint(arrowhead[i].x, arrowhead[i].y);
            }

            arrowhead[0].translate(0, -3);
            arrowhead[1].translate(3, 2);
            arrowhead[2].translate(-3, 2);

            arrowheadPolygon2 = new Polygon();
            for (int i = 0; i < 3; i++) {
                arrowheadPolygon2.addPoint(arrowhead[i].x, arrowhead[i].y);
            }
            break;
        }

            // up
        case 1: {
            arrowhead = new Point[3];
            arrowhead[0] = new Point(bend[n - 1]);
            arrowhead[1] = new Point(bend[n - 1]);
            arrowhead[2] = new Point(bend[n - 1]);
            arrowhead[0].translate(0, 0);
            arrowhead[1].translate(-7, 13);
            arrowhead[2].translate(7, 13);

            arrowheadPolygon1 = new Polygon();
            for (int i = 0; i < 3; i++) {
                arrowheadPolygon1.addPoint(arrowhead[i].x, arrowhead[i].y);
            }

            arrowhead[0].translate(0, 3);
            arrowhead[1].translate(3, -2);
            arrowhead[2].translate(-3, -2);

            arrowheadPolygon2 = new Polygon();
            for (int i = 0; i < 3; i++) {
                arrowheadPolygon2.addPoint(arrowhead[i].x, arrowhead[i].y);
            }
            break;
        }
        }
    }

    /* (non-Javadoc)
     * @see jeliot.theater.Actor#calculateSize()
     */
    public void calculateSize() {
        setSize(getPreferredSize());
    }

    /**
     * @return
     */
    public int getReferenceWidth() {
        if (instance != null) {
            calculateBends();
            return bend[1].x - bend[0].x + 4;
        }
        //Null value is shown.
        return refLen + 15;
    }

    /* (non-Javadoc)
     * @see jeliot.theater.ValueActor#getPreferredSize()
     */
    public Dimension getPreferredSize() {
        FontMetrics fm = getFontMetrics();
        int h = fm.getHeight();
        int w = refWidth;
        return new Dimension(w, h);
    }

    public void setParent(ActorContainer parent) {
        super.setParent(parent);
        if (this.instance != null) {
            this.instance.addReference(this);
        }
    }

    /**
     * 
     */
    public String toString() {
        if (this.instance == null) {
            return "null";
        }
        return "Reference to " + instance.toString();
    }

    /**
     * 
     */
    public Animation disappear() {
        if (instance != null) {
            instance.removeReference(this);
        }
        return super.disappear();
    }

    /**
     * 
     */
    protected void finalize() throws Throwable {
        if (instance != null) {
            instance.removeReference(this);
        }
        super.finalize();
    }
}