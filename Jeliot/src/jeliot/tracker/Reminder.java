package jeliot.tracker;	

import java.awt.Toolkit;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 * Simple demo that uses java.util.Timer to schedule a task 
 * to execute once 5 seconds have passed.
 */

public class Reminder {
    Timer timer;
    Timer  timer2;
    JFrame frame;
    
    
    /**
     * Seconds until the end of the experiment.
     * @param seconds
     */
    public Reminder(JFrame frame, int seconds) {
        timer = new Timer();
        timer2 = new Timer();
        this.frame = frame;
        
        //A beep reminder will sound 2 minutes before the end of the experiment
        timer.schedule(new RemindTask(), (seconds*1000)-(120*1000));
        //End happens 
        timer.schedule(new ExitTask(),(seconds*1000));
}

    class RemindTask extends TimerTask {
        public void run() {
        	Tracker.trackEvent(TrackerClock.currentTimeMillis(), Tracker.OTHER, -1, -1, "Sound");
        	Toolkit.getDefaultToolkit().beep();
        }
    }
    class ExitTask extends TimerTask{
    	public void run(){
    		Object[] options = { "OK" };
    		Tracker.trackEvent(TrackerClock.currentTimeMillis(), Tracker.OTHER, -1, -1, "End of experiment");
    		Toolkit.getDefaultToolkit().beep();
    		Tracker.trackEvent(TrackerClock.currentTimeMillis(), Tracker.OTHER, -1, -1, "Sound");
            int n = JOptionPane.showOptionDialog(frame, "Experiment is OVER, thanks for collaborating!",
            		"Experiment Finished", JOptionPane.OK_OPTION, 
					JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);            
    		
    	}
    }

 }