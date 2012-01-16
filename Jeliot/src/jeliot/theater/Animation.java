package jeliot.theater;

import java.awt.Point;

/**
 * <p>
 * <code>Animation</code> class represents one atomic animation in
 * <code>Jeliot</code>. Animation means here any event that includes
 * movement of actors or that is otherwise dependent of time. Examples
 * of animation include moving an actor from one place to another or
 * flashing the colors of an actor as it is introduced to the theatre.
 * </p>
 * 
 * <p>
 * The animation is played by an instance of
 * <code>AnimationEngine</code> class. The animation engine takes care
 * of scheduling the animation. Animation class is the abstract
 * superclass of various specialized animation classes. These
 * subclasses must implement the <code>animate</code> method in which
 * they make their changes to actors. The animation engine calls this
 * method at even time intervals. If the animation has to do something
 * in prior to starting the animation, especially if it has to set any
 * parameters that depend on the duration of the animation or it has
 * to add any actors to the theatre, it may do this in its
 * <code>init()</code> method. When the animation finishes, the engine
 * calls its <code>finish</code> method.
 * </p>
 *
 * @author Pekka Uronen
 * @author Niko Myller
 */
public abstract class Animation {

    /**
     * Desired duration that is set to be the duration of the animation
     * as a defaul value. This will be changed when a run until is set to
     * speed up the execution.
     */
    public static int defaultDuration = 1000;
    
    /**
     * 
     */
    public static boolean disableDurationChanging = false;
    
	/**
     * This flag is set by the doFinish() method when the animation
     * is at end.
     */
    private boolean finished;

	/**
     * The theatre in which the animation takes place.
     */
    private Theater theatre;

	/**
     * The starting time for the animation, in milliseconds.
     */
    private int startTime = 0;

	/**
     * Desired duration of the animation, in milliseconds.
     */
    private int duration = defaultDuration;

	/**
     * An actor associated with this animation -- the actor that gets
     * animated.
     */
    private Actor actor;

    /**
     * 
     */
    public Animation() {
        //duration = defaultDuration;
    }
    
	/** Initializes the animation. This method is called by the
      * animation engine before starting the animation, after duration
      * and theatre have been set. To be overriden by subclasses;
      * default implementation does nothing.
      */
    public void init() { }

	/** This method performs the animation. It is abstract, so it must
      * be implemented by the subclasses.
      *
      * @param p Amount of work to do in this animation step. P of 1000
      *        Measured in milliseconds; the animation will get roughly
      *        1000 units of work per second if the animation engine is in
      *        its default configuration.
      */
    public abstract void animate(double p);

	/** Finishes the animation. This method may be overriden by
      * subclasses to do something after the animation has finished.
      */
    protected void finish() { }

	/** Finishes the animation.
      */
    public void doFinish() {
        finish();
        finished = true;
    }

	/** Sets the starting time for this animation.
      *
      *@param startTime The desired starting time in milliseconds.
      */
    public void setStartTime(int startTime) {
         this.startTime = startTime;
    }

	/** Returns the starting time of this animation in milliseconds.
      *
      * @return the starting time of this animation in milliseconds.
      */
    public int getStartTime() {
        return startTime;
    }

	/** Sets the desired duration for this animation. The actual
      * duration depends on the speed of the animation engine playing
      * the animation.
      *
      * @param duration The desired duration in milliseconds.
      */
    public void setDuration(int duration) {
         if (defaultDuration > 0 && !disableDurationChanging) {
             this.duration = duration;
         }
    }

	/** Returns the desired duration of this animation in milliseconds.
      *
      * @return the desired duration of this animation in milliseconds.
      */
    public int getDuration() {
        return duration;
    }

	/** Returns true if the animation has already finished. That is, it
      * has set its finished-flag.
      *
      * @return true if the animation has already finished. That is, it
      * has set its finished-flag.
      */
    public boolean isFinished() {
        return finished;
    }

    /**
     * Sets the theatre in which this animation is played. This method
     * is called by the animation engine before performing the
     * animation.
	 * @param theatre
	 */
    public void setTheatre(Theater theatre) {
        this.theatre = theatre;
    }

    /**
     * Adds an actor to the theatre in which the animation is
     * performed. Called by subclasses.
	 * @param actor
	 */
    protected void addActor(Actor actor) {
        if (actor.getParent() != theatre) {
            Point p = actor.getRootLocation();
            theatre.addActor(actor);
            actor.setLocation(p);
        }
        else {
            theatre.promote(actor);
        }
    }

    /**
	 * @param actor
	 */
	protected void passivate(Actor actor) {
        theatre.passivate(actor);
    }

    /**
	 * @param actor
	 */
	protected void removeActor(Actor actor) {
        theatre.removeActor(actor);
    }

	/**
     * Repaints the theatre. This method is called by subclasses
     * between animation steps.
     */
    protected void repaint() {
        theatre.repaint();
    }

    /**
	 * This method is called when the animation is finished.
     * Should be overridden.
	 */
	public void finalFinish() { }

}
