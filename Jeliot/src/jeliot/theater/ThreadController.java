package jeliot.theater;

import java.util.Iterator;
import java.util.Vector;

import jeliot.mcode.StoppingRequestedError;

/**
 * <p>
 * <code>ThreadController</code> allows the execution of the
 * <code>Runnable</code> object controlled by it to be paused and
 * resumed in a safe way. The controller gets a <code>Runnable</code>
 * object in its constructor. After it has been constructed, the
 * controller can be called to start or pause the execution of its
 * runnable.
 * </p>
 * 
 * <p>
 * Calling the <code>pause</code> method does not pause the execution
 * immediately, but only when the <code>checkPoint</code> method is
 * next called in the controlled thread.
 * </p>
 * 
 * <p>
 * <b>Warning!</b> <code>ThreadController<code> does not check that
 * the <code>checkPoint</code> method is called from correct thread.
 *</P>
 *
 * @author Pekka Uronen
 * @author Niko Myller
 */
public class ThreadController {

    /**
     * Possible states of the controller.
     */
    private static final int RUNNING = 1;

    /**
     * Possible states of the controller.
     */
    private static final int PAUSEREQ = 2;

    /**
     * Possible states of the controller.
     */
    private static final int PAUSED = 0;

    /**
     * Current state of the controller.
     */
    private int status;

    /**
     * The Runnable object controled by this controller.
     */
    private Runnable runner;

    /**
     * A thread in which the Runnable is executed.
     */
    private Thread thread;

    /**
     * Indicates when this thread should be stopped.
     */
    private boolean stoppingRequested = false;

    /**
     * Pause listeners that need to be notified when the thread is paused.
     */
    private Vector pauseListeners = new Vector();
    
    /**
     * Constructs a new controller for given Runnable.
     * @param runner
     */
    public ThreadController(Runnable runner) {
        this.runner = runner;
    }

    /** 
     * Starts or resumes the Runnable immediately in its own thread.
     */
    public synchronized void start() {
        if (stoppingRequested == true) { throw new StoppingRequestedError(); }
        switch (status) {
            case (PAUSED):
                if (thread == null) {
                    thread = new Thread(runner);
                    thread.start();
                } else {
                    notify();
                }
                status = RUNNING;
                break;
            default:
                throw new RuntimeException("Wrong State of the Controller for start.");
        }
    }

    /**
     * Instructs the controller to pause execution in next check
     * point.
     */
    public synchronized void pause() {
        if (stoppingRequested == true) { throw new StoppingRequestedError(); }
        switch (status) {
            case (RUNNING):
                status = PAUSEREQ;
                break;
            case (PAUSEREQ):
                break;
            default:
                throw new RuntimeException("Wrong State of the Controller for pause.");
        }
    }

    /**
     * Pauses the execution, if pause() method has been called since
     * previous checkpoint.
     * @param cont the controlled thread. Null is also accepted
     * @param forInput if the pausing is for input when the pause listeners are not notified.
     */
    public synchronized void checkPoint(Controlled cont, boolean forInput) {
        if (stoppingRequested == true) { throw new StoppingRequestedError(); }
        switch (status) {
            case (RUNNING):
                break;
            case (PAUSEREQ):
                status = PAUSED;
                if (cont != null) {
                    cont.suspend();
                }
                if (!forInput) {
                    for (Iterator i = pauseListeners.iterator(); i.hasNext();) {
                        ((PauseListener) i.next()).paused();
                    }
                }
                try {
                    wait();
                } catch (InterruptedException e) {}
                if (stoppingRequested == true) { throw new StoppingRequestedError(); }
                if (cont != null) {
                    cont.resume();
                }
                status = RUNNING;
                break;
            default:
                throw new RuntimeException("Wrong State of the Controller for check point.");
        }
    }

    /**
     * Notify the thread controller that the thread should be finished.
     */
    public synchronized void quit() {
        stoppingRequested = true;
        notify();
    }

    /**
     * Calling the checkpoint(Controlled) method with null actual
     * parameter value.
     */
    public synchronized void checkPoint(boolean forInput) {
        checkPoint(null, forInput);
    }

    /**
     * Checkpoint reached.
     */
    public synchronized void checkPoint() {
        checkPoint(null, false);
    }
    
    /**
     * Add a pause listener.
     * @param p listener to be added.
     */
    public void addPauseListener(PauseListener p) {
        pauseListeners.add(p);
    }

    /**
     * Remove a pause listener.
     * @param p listener to be removed.
     */
    public void removePauseListener(PauseListener p) {
        pauseListeners.remove(p);
    }

}