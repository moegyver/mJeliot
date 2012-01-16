package jeliot.theater;

import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Point;

import jeliot.tracker.Tracker;
import jeliot.tracker.TrackerClock;

/**
 * Array Creation actor shows the "new Type[n]" is shown before the
 * array is created. The structure is similar to SMIActor.
 *  
 * @author Niko Myller
 * @see jeliot.theater.SMIActor
 */
public class ACActor extends Actor implements ActorContainer{

//  DOC: document!
    /**
	 *
	 */
	private String name;

    /**
	 *
	 */
    private Actor[] actors;
    
    /**
	 *
	 */
    private Point[] locs;
    
    /**
	 *
	 */
    private boolean[] bound;
    
    /**
	 *
	 */
    private int next = 0;

    /**
	 *
	 */
    private int margin = 2;
    
    
    /**
	 *
	 */
    private int namey;
    
    /**
	 *
	 */
    private int namex;
    
	/**
	 *
	 */
    private int namew;
    
	/**
	 *
	 */
    private int nameh;
    
    /**
	 *
	 */
    private int bracketMargin;

    /**
     * If the dimension of the array is greater than the currently allocated part then empty brackets are shown in the end.
     */
    private String emptyBrackets;
    
    /**
	 * @param name
	 * @param n
	 */
	public ACActor(String name, int n, int emptyBracketsCount) {
        this.name = name;
        actors = new Actor[n];
        locs = new Point[n];
        bound = new boolean[n];
        bracketMargin = getFontMetrics().stringWidth("][") + 4;
        this.emptyBrackets = "";
        for (int i = 0; i < emptyBracketsCount; i++) {
            this.emptyBrackets += "[ ]";
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
		int x = insets.left + namew + margin;

        if (next > 0) {
            if (actors[next-1] instanceof ReferenceActor) {
                x = locs[next - 1].x +
                    actors[next - 1].getWidth() +
                    ((ReferenceActor) actors[next - 1]).getReferenceWidth() +
                    margin + 
                    bracketMargin +
                    margin;
            } else {
                x = locs[next - 1].x +
                    actors[next - 1].getWidth() +
                    margin +
                    bracketMargin +
                    margin;
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
        throw new RuntimeException("This actor " + actor.getClass().getName() + " was not reserved.");
    }

    /**
	 * @param g
	 */
	public void paintActors(Graphics g) {
        int n = next;
        for (int i = 0; i < n; i++) {
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
        	g.drawString(name + "[", namex, namey);
        	
        	for (int i = 0; i < next; i++) {
	        	if (i != (next-1)) {
		        	if (actors[i] instanceof ReferenceActor) {
			        	g.drawString("][",
	        					 locs[i].x +
	        					 actors[i].getWidth() +
	        					 ((ReferenceActor) actors[i]).getReferenceWidth() +
	        					 margin,
	        					 namey);
                    
	            	} else {
			        	g.drawString("][",
	        					 locs[i].x +
	        					 actors[i].getWidth() +
	        					 margin,
	        					 namey);
    	        	}
    			}
        	}
        	       
        	if (actors[next-1] instanceof ReferenceActor) {
	        	g.drawString("]",
	     					 locs[next-1].x +
	      					 actors[next-1].getWidth() +
	       					 ((ReferenceActor) actors[next-1]).getReferenceWidth() +
	       					 margin,
	       					 namey);
                    
	       	} else {
		       	g.drawString("]" + emptyBrackets,
	       					 locs[next-1].x +
	       					 actors[next-1].getWidth() +
	       					 margin,
	       					 namey);
    	        	}
		} else {
        	g.drawString(name + emptyBrackets, namex, namey);			
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
        namew = fm.stringWidth(this.name + "[");

        int n = next;
        int maxh = insets.top + nameh;
        int maxw = insets.left + namew;
        for (int i = 0; i < n; ++i) {
            int h = locs[i].y + actors[i].getHeight();
            maxh = h > maxh ? h : maxh;
            int w = locs[i].x + actors[i].getWidth();
            maxw = w > maxw ? w : maxw;
        }
        namex = insets.left;
        namey = insets.top + nameh;
        setSize(maxw + insets.right + fm.stringWidth("]"), maxh + insets.bottom);
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
        
        for (int i = 0; i < actors.length; i++) {
            if (actors[i] != null) {
                actors[i].disappear();
            }
        }
        
        //Tracker
        Point p = getRootLocation();
        Tracker.trackTheater(TrackerClock.currentTimeMillis(), Tracker.DISAPPEAR, getActorId(), Tracker.RECTANGLE, new int[] {p.x}, new int[] {p.y}, getWidth(), getHeight(), 0, -1, getDescription());

        return null;
    }

}
