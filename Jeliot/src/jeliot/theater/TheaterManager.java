package jeliot.theater;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Stack;
import java.util.Vector;

/**
 * <code>TheaterManager</code> allocates the space for all
 * <code>InstanceActor</code>s, <code>MethodStage</code>s,
 * <code>Scratch</code>es and constants (<code>ConstantBox</code>), and also listens the <code>Theater</code>
 * component for resizes so the the allocation of the space
 * is valid after resizing of the <code>Theater</code> component.
 *
 * @author Pekka Uronen
 * @author Niko Myller
 * 
 * @see jeliot.theater.InstanceActor
 * @see jeliot.theater.MethodStage
 * @see jeliot.theater.Scratch
 * @see jeliot.theater.ConstantBox
 * @see jeliot.theater.Theater
 */
public class TheaterManager implements ComponentListener {

    /**
     * Contains Points that are set as the rootlocations of the
     * new MethodStage instances. They are circulated so that
     * after the last value of the table the first value is used
     * again.
     */
    private static Point[] methodStagePoints = { new Point(10, 20),
            new Point(20, 30), new Point(30, 40), new Point(15, 50),
            new Point(25, 45) };

    /**
     * 
     */
    private static int maxMethodStageInsetX = 30;

    /**
     * 
     */
    public static final int CONSTANT_BOX_POSITION_X = 10;

    /**
     * 
     */
    public static final int EXTRA_SPACE = 40;

    /**
     * 
     */
    private static final int DISTANCE_BETWEEN_INSTANCES = 47;

    /**
     * Reference to the current Theatre instance.
     * Reference is need for two reasons:
     * Firstly, to set this TheatreManager instance as the
     * ComponentListener of the Theatre and then assign the actors in
     * correct places when the Theatre (<code>JComponent</code>)
     * resized. Secondly, for inserting new actors to the passive
     * (<code>pasAct</code>) and active (<code>actAct</code>) actors.
     */
    private Theater theatre;

    //DOC: Document!

    /**
     *
     */
    private Stack methods = new Stack();

    /**
     *
     */
    private Vector objects = new Vector();

    /**
     *
     */
    private Vector scratches = new Vector();

    /**
     * 
     */
    private Vector classes = new Vector();

    /**
     *
     */
    private ConstantBox constantBox;

    /**
     *
     */
    private static int maxLeftSideX = 250;

    /**
     *
     */
    private static int maxMethodStageY = 288;

    /**
     * 
     */
    private int maxY = 0;

    /**
     * 
     */
    private int maxX = 0;

    /**
     *
     */
    private Hashtable reservations = new Hashtable();

    /**
     *
     */
    private Point lrCorner;

    /**
     *
     */
    //private int minInstanceY = Integer.MAX_VALUE;
    /**
     *
     */
    //private int minInstanceX = Integer.MAX_VALUE;
    /**
     * @param theatre
     */
    public TheaterManager(Theater theatre) {
        this.theatre = theatre;
        theatre.addComponentListener(this);
        Dimension d = theatre.getSize();
        lrCorner = new Point(d.width, d.height);
    }

    /**
     * 
     * @return
     */
    public static int getMaxMethodInset() {
        return maxMethodStageInsetX;
    }

    /**
     * 
     */
    public void cleanUp() {
        methods.removeAllElements();
        objects.removeAllElements();
        scratches.removeAllElements();
        classes.removeAllElements();
        reservations.clear();
        constantBox = null;
        //minInstanceY = Integer.MAX_VALUE;
        //minInstanceX = Integer.MAX_VALUE;
    }

    /**
     * @param stage
     * @return
     */
    public Point reserve(MethodStage stage) {
        Point loc = methodStagePoints[methods.size() % methodStagePoints.length];
        reservations.put(stage, loc);
        return loc;
    }

    /**
     * @param stage
     */
    public void bind(MethodStage stage) {
        Point loc = (Point) reservations.remove(stage);
        methods.push(stage);
        stage.setLocation(loc);
        if (!theatre.getPasAct().contains(stage)) {
            theatre.passivate(stage);
        }

        /*
         maxMethodStageX = 0;
         int n = methods.size();
         for (int i = 0; i < n; ++i) {
         MethodStage s = (MethodStage) methods.elementAt(i);
         int wx = s.getX() + s.getWidth();
         if (wx > maxMethodStageX) {
         maxMethodStageX = wx;
         //System.out.println("Stage width: " + maxMethodStageX);
         }
         }
         */
    }

    /**
     * @param actor
     * @return
     */
    public Point reserve(InstanceActor actor) {
        //int w = actor.getWidth();
        //int h = actor.getHeight();

        // this places new objects in the beginning
        // see also: bind(InstanceActor) -> objects.add(0, actor);
        int x = getMinInstanceX();
        int y = getMinInstanceY();

        /*
         //this places new objects in the end
        // see also: bind(InstanceActor) -> objects.add(actor);
         int x = objects.isEmpty() ? getMinInstanceX() : ((Actor) objects
         .lastElement()).getX()
         + ((Actor) objects.lastElement()).getWidth()
         + DISTANCE_BETWEEN_INSTANCES;
         int y = getMinInstanceY();
         */

        /* very old
         int x = objects.isEmpty() ?
         theatre.getWidth() - w - 45 :
         ((Actor)objects.lastElement()).getX() - w - 45;
         int y = theatre.getHeight() - h - 10;
         */

        Point loc = new Point(x, y);
        reservations.put(actor, loc);

        actor.setReferencePosition(getFirstFreeReferencePosition());

        return loc;
    }

    private int getFirstFreeReferencePosition() {
        for (int i = 0; i <= objects.size(); i++) {
            if (!containsPosition(i)) {
                return i;
            }
        }
        return objects.size() + 2;
    }

    private boolean containsPosition(int i) {
        for (Iterator iter = objects.iterator(); iter.hasNext();) {
            InstanceActor ia = (InstanceActor) iter.next();
            if (ia.getReferencePosition() == i) {
                return true;
            }
        }
        return false;
    }

    /**
     * @param actor
     */
    public void bind(InstanceActor actor) {
        Point loc = (Point) reservations.remove(actor);
        //objects.add(actor); //places the object in the end of the row
        objects.add(0, actor); //places the object in the beginning of the row
        if (!theatre.getPasAct().contains(actor)) {
            theatre.passivate(actor);
        }
        actor.setLocation(loc);
        repositionInstanceActors();
        /*
         if (loc.y < minInstanceY) {
         minInstanceY = loc.y;
         }
         if (loc.x < minInstanceX) {
         minInstanceX = loc.x;
         }
         */

        Dimension d = theatre.getSize();

        if (d.width < loc.x + actor.getWidth() + EXTRA_SPACE) {
            d.width = loc.x + actor.getWidth() + EXTRA_SPACE;
        }
        if (d.height < loc.y + actor.getHeight() + EXTRA_SPACE) {
            d.height = loc.y + actor.getHeight() + EXTRA_SPACE;
        }
        theatre.setPreferredSize(d);
        theatre.revalidate();
    }

    /**
     * @param actor
     */
    public void removeInstance(InstanceActor actor) {
        //move also minInstanceX and -Y
        objects.removeElement(actor);
        theatre.removeActor(actor);
        //theatre.removeActor(actor);
        repositionInstanceActors();
        theatre.flush();
    }

    /**
     * @param stage
     */
    public void removeMethodStage(MethodStage stage) {
        methods.removeElement(stage);
        theatre.removePassive(stage);
    }

    /**
     * @param actor
     * @return
     */
    public Point reserve(ClassActor actor) {
        //int w = actor.getWidth();
        //int h = actor.getHeight();

        int x = constantBox.getX();
        int y = classes.isEmpty() ? constantBox.getY()
                + constantBox.getHeight() + 20
                : ((Actor) classes.lastElement()).getY()
                        + ((Actor) classes.lastElement()).getHeight() + 20;

        Point loc = new Point(x, y);
        reservations.put(actor, loc);

        return loc;
    }

    /**
     * @param actor
     */
    public void bind(ClassActor actor) {
        Point loc = (Point) reservations.remove(actor);
        classes.addElement(actor);
        if (!theatre.getPasAct().contains(actor)) {
            theatre.passivate(actor);
        }
        actor.setLocation(loc);

        Dimension d = theatre.getSize();

        if (d.width < loc.x + actor.getWidth() + EXTRA_SPACE) {
            d.width = loc.x + actor.getWidth() + EXTRA_SPACE;
        }

        if (d.height < loc.y + actor.getHeight() + EXTRA_SPACE) {
            d.height = loc.y + actor.getHeight() + EXTRA_SPACE;
        }

        theatre.setPreferredSize(d);
        theatre.revalidate();
    }

    public void validateTheater() {
        //theatre.setPreferredSize(new Dimension(0, 0));
        this.maxX = 0;
        this.maxY = 0;
        maxLeftSideX = 250;
        maxMethodStageY = 288;
        validateMethods();
        positionConstantBox();
        repositionClassActors();
        validateClasses();
        repositionScratches();
        validateScratches();
        repositionInstanceActors();
        validateObjects();
        theatre.setPreferredSize(new Dimension(maxX, maxY));
        theatre.revalidate();
        theatre.repaint();
    }

    private void validateMethods() {
        for (Iterator i = this.methods.iterator(); i.hasNext();) {
            Actor a = (Actor) i.next();
            validateSize(a, false);

            MethodStage s = (MethodStage) a;
            Point p = s.getRootLocation();
            int wx = p.x + s.getWidth();
            int wy = p.y + s.getHeight();
            if (wx > maxLeftSideX) {
                maxLeftSideX = wx;
            }
            if (wy > maxMethodStageY) {
                maxMethodStageY = wy;
            }
        }
    }

    private void validateScratches() {
        for (Iterator i = this.scratches.iterator(); i.hasNext();) {
            Actor a = (Actor) i.next();
            validateSize(a, false);
        }
    }

    private void validateClasses() {
        for (Iterator i = this.classes.iterator(); i.hasNext();) {
            Actor a = (Actor) i.next();
            validateSize(a, false);

            ClassActor s = (ClassActor) a;
            int wx = s.getX() + s.getWidth();
            int wy = s.getY() + s.getHeight();
            if (wx > maxLeftSideX) {
                maxLeftSideX = wx;
            }
        }
    }

    private void validateObjects() {
        for (Iterator i = this.objects.iterator(); i.hasNext();) {
            Actor a = (Actor) i.next();
            validateSize(a, false);
        }
    }

    private void validateSize(Actor actor, boolean validate) {
        Dimension d = theatre.getSize();

        Point loc = actor.getRootLocation();
        if (d.width < loc.x + actor.getWidth() + EXTRA_SPACE) {
            d.width = loc.x + actor.getWidth() + EXTRA_SPACE;
        }
        if (d.height < loc.y + actor.getHeight() + EXTRA_SPACE) {
            d.height = loc.y + actor.getHeight() + EXTRA_SPACE;
        }
        if (maxX < loc.x + actor.getWidth() + EXTRA_SPACE) {
            maxX = loc.x + actor.getWidth() + EXTRA_SPACE;
        }
        if (maxY < loc.y + actor.getHeight() + EXTRA_SPACE) {
            maxY = loc.y + actor.getHeight() + EXTRA_SPACE;
        }
        theatre.setPreferredSize(d);
        if (validate) {
            theatre.revalidate();
        }
    }

    /**
     * @param actor
     */
    public void removeClass(ClassActor actor) {
        //move also minInstanceX and -Y
        classes.removeElement(actor);
        theatre.removePassive(actor);
        theatre.flush();
    }

    /**
     * @param cbox
     */
    public void setConstantBox(ConstantBox cbox) {
        this.constantBox = cbox;
        positionConstantBox();
    }

    /**
     * @param scratch
     */
    public void addScratch(Scratch scratch) {
        scratches.addElement(scratch);
        scratch.setLocation(getScratchPositionX(), 20);
        theatre.addPassive(scratch);
    }

    /**
     * @return
     */
    public static int getScratchPositionX() {
        return maxLeftSideX + 20;
        /*
         if (!methods.empty()) {
         return 235;
         } else {
         return (ActorFactory.getMaxMethodStageWidth()) + 45;
         }
         */
    }

    /**
     * @param scratch
     */
    public void removeScratch(Scratch scratch) {
        scratches.removeElement(scratch);
        theatre.removePassive(scratch);
    }

    /**
     * 
     */
    private void positionConstantBox() {
        if (constantBox != null) {
            int x = CONSTANT_BOX_POSITION_X; //theatre.getWidth() - 10 - cbox.getWidth();
            int y = getConstantBoxPositionY();
            constantBox.setLocation(x, y);
        }
    }

    /**
     * @return
     */
    public static int getConstantBoxPositionY() {
        //Change this when static variables are visualized!
        return maxMethodStageY + 30;
    }

    /**
     * @return
     */
    public static int getMinInstanceY() {
        return 258 + 30;
        //return minInstanceY;
    }

    /**
     * @return
     */
    public static int getMinInstanceX() {
        //return 250 + 20;
        return maxLeftSideX + 25;
        //return minInstanceX;
    }

    /**
     * @param from
     * @param to
     */
    public void positionObjects(Point from, Point to) {
        /*
         Enumeration enum = objects.elements();
         while (enum.hasMoreElements()) {
         Actor actor = (Actor)enum.nextElement();
         Point loc = actor.getLocation();
         actor.setLocation(
         loc.x + to.x - from.x,
         loc.y + to.y - from.y);
         }
         */
    }

    /**
     * @param from
     * @param to
     */
    public void repositionInstanceActors() {
        for (int i = 0; i < objects.size(); i++) {
            InstanceActor inst = (InstanceActor) objects.get(i);
            if (inst != null) {
                int x = (i == 0 ? getMinInstanceX() : ((Actor) objects
                        .get(i - 1)).getX()
                        + ((Actor) objects.get(i - 1)).getWidth()
                        + DISTANCE_BETWEEN_INSTANCES);
                int y = getMinInstanceY();
                inst.setLocation(x, y);
            }
        }
    }

    public void repositionClassActors() {
        for (int i = 0; i < classes.size(); i++) {
            ClassActor inst = (ClassActor) classes.get(i);
            if (inst != null) {
                int y = (i == 0 ? constantBox.getY() + constantBox.getHeight()
                        + 20 : ((Actor) classes.get(i - 1)).getY()
                        + ((Actor) classes.get(i - 1)).getHeight() + 20);
                int x = constantBox.getX();
                inst.setLocation(x, y);
            }
        }
    }

    private void repositionScratches() {
        for (int i = 0; i < scratches.size(); i++) {
            Scratch scratch = (Scratch) scratches.get(i);
            if (scratch != null) {
                int x = getScratchPositionX();
                int y = scratch.getY();
                scratch.setLocation(x, y);
            }
        }
    }

    /**
     * 
     * @param actors
     * @param from
     * @param to
     */
    public void positionActors(Vector actors, Point from, Point to) {
        for (Iterator i = actors.iterator(); i.hasNext();) {
            Actor actor = (Actor) i.next();
            Point loc = actor.getLocation();
            actor.setLocation(loc.x + (to.x - from.x), loc.y + (to.y - from.y));
        }
    }

    /**
     * @return
     */
    public Point getOutputPoint() {
        int x = theatre.getScrollPane().getViewport().getViewPosition().x;
        int y = theatre.getScrollPane().getViewport().getViewPosition().y;
        Dimension d = theatre.getScrollPane().getViewport().getExtentSize();
        return new Point(x + d.width / 2, y + d.height);
    }

    /**
     * Draws the lines separating different areas
     * and writes texts on them.
     * @param lat
     */
    public void setLinesAndText(LinesAndText lat) {
        lat.setTheatre(theatre);
    }

    /** Called, when the theatre object is resized. Rearranges the
     * theatre after resizing.
     * @see java.awt.event.ComponentListener#componentResized(java.awt.event.ComponentEvent)
     */
    public void componentResized(ComponentEvent e) {
        positionConstantBox();
        Dimension d = theatre.getSize();
        positionObjects(lrCorner, lrCorner = new Point(d.width, d.height));
        theatre.repaint();
    }

    /* (non-Javadoc)
     * @see java.awt.event.ComponentListener#componentMoved(java.awt.event.ComponentEvent)
     */
    public void componentMoved(ComponentEvent e) {
    }

    /* (non-Javadoc)
     * @see java.awt.event.ComponentListener#componentShown(java.awt.event.ComponentEvent)
     */
    public void componentShown(ComponentEvent e) {
    }

    /* (non-Javadoc)
     * @see java.awt.event.ComponentListener#componentHidden(java.awt.event.ComponentEvent)
     */
    public void componentHidden(ComponentEvent e) {
    }

}
