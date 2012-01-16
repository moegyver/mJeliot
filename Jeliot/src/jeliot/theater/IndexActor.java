package jeliot.theater;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;

import jeliot.tracker.Tracker;
import jeliot.tracker.TrackerClock;

/**
 * IndexActor shows the line between the array access' indexing
 * expression result value and the array's actual index. 
 * 
 * @author Pekka Uronen
 * @author Niko Myller
 */
public class IndexActor extends Actor {
    
//  DOC: Document!

    /**
	 *
	 */
	private Actor source;

    /**
	 *
	 */
	private Point startPoint;
    
    /**
	 *
	 */
	private Point endPoint;

    /**
	 * @param source
	 */
	public IndexActor(Actor source) {
        this.source = source;
        this.fgcolor = Color.white;
        setDescription("indexing array");
    }

    /* (non-Javadoc)
	 * @see jeliot.theater.Actor#paintActor(java.awt.Graphics)
	 */
	public void paintActor(Graphics g) {
        g.setColor(fgcolor);
        g.drawLine(startPoint.x, startPoint.y, endPoint.x, endPoint.y);
        g.drawLine(startPoint.x, startPoint.y+1, endPoint.x, endPoint.y+1);
        g.drawLine(startPoint.x, startPoint.y-1, endPoint.x, endPoint.y-1);
    }

    /**
	 * @param varAct
	 * @return
	 */
	public Animation index(VariableInArrayActor varAct) {

        final Point finalPoint = varAct.getRootLocation();
        finalPoint.translate(0, varAct.getHeight()/2);

        Point sLoc = source.getRootLocation();
        Dimension sSize = source.getSize();
        startPoint = new Point(
                sLoc.x + sSize.width,
                sLoc.y + sSize.height / 2);

        int dx = finalPoint.x - startPoint.x;
        int dy = finalPoint.y - startPoint.y;
        final double len = Math.sqrt(dx*dx + dy*dy);
        double angle = Math.atan2(dy, dx);
        final double cos = Math.cos(angle);
        final double sin = Math.sin(angle);

        return new Animation() {
            double xp = startPoint.x;
            double yp = startPoint.y;
            double l = 0;
            double step;
            long id = -1;
            public void init() {
                this.addActor(IndexActor.this);
                step = len / getDuration();
                endPoint = new Point(startPoint);
                setActorId(Tracker.trackTheater(TrackerClock.currentTimeMillis(), Tracker.APPEAR, getActorId(), Tracker.POLYGON, new int[] {IndexActor.this.startPoint.x + 2, IndexActor.this.startPoint.x - 2, IndexActor.this.endPoint.x + 2, IndexActor.this.endPoint.x - 2}, new int[] {IndexActor.this.startPoint.y - 2, IndexActor.this.startPoint.y + 2, IndexActor.this.endPoint.y - 2, IndexActor.this.endPoint.x + 2}, getWidth(), getHeight(), 0, -1, getDescription()));
            }

            public void animate(double pulse) {
                xp += pulse * step * cos;
                yp += pulse * step * sin;
                l += pulse * step;

                endPoint.x = (int)xp;
                endPoint.y = (int)yp;
                
                Tracker.trackTheater(TrackerClock.currentTimeMillis(), Tracker.MODIFY, getActorId(), Tracker.POLYGON, new int[] {IndexActor.this.startPoint.x + 2, IndexActor.this.startPoint.x - 2, IndexActor.this.endPoint.x + 2, IndexActor.this.endPoint.x - 2}, new int[] {IndexActor.this.startPoint.y - 2, IndexActor.this.startPoint.y + 2, IndexActor.this.endPoint.y - 2, IndexActor.this.endPoint.x + 2}, -1, -1, 0, -1, getDescription());

                repaint();
            }

            public void finish() {
                endPoint = finalPoint;
                //this.repaint();
            }

            public void finalFinish() {
                this.passivate(IndexActor.this);
            }
        };
    }
    
    public Animation disappear() {
        Tracker.trackTheater(TrackerClock.currentTimeMillis(), Tracker.DISAPPEAR, getActorId(), Tracker.POLYGON, new int[] {IndexActor.this.startPoint.x + 2, IndexActor.this.startPoint.x - 2, IndexActor.this.endPoint.x + 2, IndexActor.this.endPoint.x - 2}, new int[] {IndexActor.this.startPoint.y - 2, IndexActor.this.startPoint.y + 2, IndexActor.this.endPoint.y - 2, IndexActor.this.endPoint.x + 2}, -1, -1, 0, -1, getDescription());
        return null;
    }
}
