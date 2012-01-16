package jeliot.theater;



/**
 * 
 * <p>
 * <code>AnimationEngine</code> schedules the animations represented
 * by instances of <code>Animation</code> class. The engine is given
 * an animation or an array of animations, and it plays those
 * animations. The speed and quality of the animation can be
 * controlled by setting its volume (speed) and FPS (Frames Per Second)
 * values. An engine's volume is the amount of action it gives to the
 * animation objects each second. The higher the volume, the faster
 * the animations will play.
 * </p>
 * 
 * <p>
 * An animation engine may be assigned a <code>ThreadController</code>
 * instance. In this case, the engine checks with the controller after
 * every step of animation calling its <code>checkPoint</code> method.
 * </p>
 *
 * @author Pekka Uronen
 * @author Niko Myller
 */
public class AnimationEngine implements Controlled {

    private boolean runUntil = false;
    
    /**
     * Amount of action for one second period
     */
    private double volume = 500.0;
    
    /**
     * Default amount of action for one second period
     */
    private double defaultVolume = 500.0;

    /**
     * Number of times to act per second
     */
    private double fps = 20.0;
    
    /**
     * Default number of times to act per second
     */
    private double defaultFPS = 20.0;

    /**
     * True if the animation engine is running.
     */
    private boolean running;

    /**
     * The theatre in which the animation takes place.
     */
    private Theater theatre;

    /**
     * The thread controller of this animation engine. If controller
     * null, it will be called after each step of animation.
     */
    private ThreadController controller;

    /** Constructs a new animation engine that will show its animations
      * in given theatre.
      */
    public AnimationEngine(Theater theatre) {
        this.theatre = theatre;
    }

    /**
     * Sets the thread controller of this animation engine.
     */
    public void setController(ThreadController controller) {
        this.controller = controller;
    }

    /**
     * Sets the number of frames that should be shown in one second.
     */
    public void setFPS(double fps) {
        this.fps = fps;
    }

    /**
     * Sets the amount of action that is given to each animation at
     * each step.
     */
    public void setVolume(double volume) {
        this.volume = volume;
    }

    /**
	 * 
	 */
	public void setDefaultValues() {
        this.fps = this.defaultFPS;
        this.volume = this.defaultVolume;
    }

    /**
     * Performs the given animation.
     */
    public void showAnimation(Animation animation) {
        showAnimation(new Animation [] {animation});
    }

    /**
     * Performs the animations in given array.
     */
    public void showAnimation(Animation[] animations) {
        int n = animations.length;
        int duration = 0;

        // Set the theatre for the animations
        for (int i = 0; i < n; ++i) {
            Animation anim = animations[i];
            anim.setTheatre(theatre);
            anim.init();
            duration = Math.max(duration,
                    anim.getStartTime() + anim.getDuration());
        }

        // if the theatre is not captured, do capture it.
        boolean capture = !theatre.isCaptured();
        if (capture) {
            theatre.capture();
        } else {
            theatre.updateCapture();
        }

        // The animation loop
        double amount = 0.0;
        long time = System.currentTimeMillis();
        while ( amount < duration ) {

            // Animate the animations.
            double work = volume/fps;
            amount += work;
            for (int i = 0; i < n; ++i) {
                Animation anim = animations[i];
                if (!anim.isFinished()) {
                    double dur = anim.getDuration();
                    if (dur > amount) {
                        anim.animate(work);
                    }
                    else {
                        anim.animate(amount - dur);
                        anim.doFinish();
                    }
                }
            }

            // Spend the excess time waiting.
            long waitTime = (long)(1000/fps) + time -
                    (time = System.currentTimeMillis());
            
            if (waitTime > 0 && !runUntil) {
                try {
                    Thread.sleep(waitTime);
                }
                catch (InterruptedException e) {  }
            }

            // If the engine is controlled, inform the controller.
            if (controller != null) {
                try {
                    controller.checkPoint(this, false);
                } catch (Exception e) {
                }
            }
        }

        for (int i = 0; i < n; ++i) {
            Animation anim = animations[i];
            anim.finalFinish();
        }

        // if theatre was captured before animation, release it.
        if (capture) {
            theatre.release();
        }

    }

    /**
     * Called by the thread controller when the animation is paused.
     */
    public void suspend() {
        theatre.release();
    }

    /**
     * Called by the thread controller when the animation is resumed.
     */
    public void resume() {
        theatre.capture();
    }

    public boolean isRunUntilEnabled() {
        return runUntil;
    }

    public void setRunUntilEnabled(boolean runUntil) {
        this.runUntil = runUntil;
    }
}
