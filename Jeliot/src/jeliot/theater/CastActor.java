package jeliot.theater;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.Shape;

import jeliot.tracker.Tracker;
import jeliot.tracker.TrackerClock;

/**
 * CastActor handles the animation of the casting of the
 * primitive values.
 * 
 * @author Pekka Uronen
 * @author Niko Myller
 */
public class CastActor extends Actor {

    /**
	 *
	 */
	ValueActor fromActor;
    
    /**
	 *
	 */
	ValueActor toActor;
    
    /**
	 *
	 */
	int line;

	/**
	 *
	 */
	int linex;
    
    /**
     *
     */    
    int linew;
    
    /**
	 * @param fromActor
	 * @param toActor
	 */
	public CastActor(ValueActor fromActor, ValueActor toActor) {
        this.fromActor = fromActor;
        this.toActor = toActor;
        setDescription("variable casted from " + fromActor.getType() +
        			   " to " + toActor.getType());
        linex = 0;
        linew = Math.max(fromActor.getWidth(), toActor.getWidth());
        
        setSize(Math.max(fromActor.getHeight(), toActor.getHeight()),
            linew);
    }
    
    /* (non-Javadoc)
	 * @see jeliot.theater.Actor#paintActor(java.awt.Graphics)
	 */
	public void paintActor(Graphics g) {
        if (line <= 0) {
            fromActor.paintActor(g);
        } else if (line > toActor.getHeight()) {
            toActor.paintActor(g);
        } else {
            Shape clip = g.getClip();
            
            
            
            g.clipRect(-1000, -1000, 1000, line);
            fromActor.paintActor(g);
            
            g.setClip(clip);
            g.clipRect(-1000, line, 1000, 1000);
            toActor.paintActor(g);
            
            g.setClip(clip);
            g.setColor(fgcolor);
            g.fillRect(linex, line-1, linew, 3); 
        }
    }
    
    /**
	 * @return
	 */
	public Animation cast() {
        return new Animation() {
            double plus;
            double h;
            int id = -1;
            
            public void init() {
                plus = (double)getHeight() / getDuration();
                
                //TRACKER
                Point p = getRootLocation();
                //id = Tracker.writeToFile("Cast", p.x, p.y, CastActor.this.getWidth(), CastActor.this.getHeight(), TrackerClock.currentTimeMillis(), id);
                setActorId(Tracker.trackTheater(TrackerClock.currentTimeMillis(), Tracker.APPEAR, getActorId(), Tracker.RECTANGLE, new int[] {p.x}, new int[] {p.y}, getWidth(), getHeight(), 0, -1, getDescription()));
            }
            
            public void animate(double pulse) { 

                h += plus * pulse;
                line = (int)h;
                
                //TRACKER
                Point p = getRootLocation();
                //id = Tracker.writeToFile("Cast", p.x, p.y, CastActor.this.getWidth(), CastActor.this.getHeight(), TrackerClock.currentTimeMillis(), id);
                Tracker.trackTheater(TrackerClock.currentTimeMillis(), Tracker.MODIFY, getActorId(), Tracker.RECTANGLE, new int[] {p.x}, new int[] {p.y}, getWidth(), getHeight(), 0, -1, getDescription());

                this.repaint();
            }
            
            public void finish() {
                //Tracker
                Point p = getRootLocation();
                Tracker.trackTheater(TrackerClock.currentTimeMillis(), Tracker.DISAPPEAR, getActorId(), Tracker.RECTANGLE, new int[] {p.x}, new int[] {p.y}, getWidth(), getHeight(), 0, -1, getDescription());
            }
        };
    }
    
}

