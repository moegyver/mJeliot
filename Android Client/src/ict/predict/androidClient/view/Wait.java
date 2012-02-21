package ict.predict.androidClient.view;

import ict.model.Lecture;
import ict.predict.androidClient.controller.Controller;
import ict.predict.androidClient.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

/**
 * A Wait-Activity shows a wait message and reacts on new lectures and results. It 
 * finishes itself on logout and disconnect.
 * @author Moritz Rogalli
 *
 */
public class Wait extends AbstractPredictActivity {
    /* (non-Javadoc)
     * @see ict.predict.androidClient.view.AbstractPredictActivity#onCreate(android.os.Bundle)
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wait);
    }
	/* (non-Javadoc)
	 * @see ict.predict.androidClient.controller.ControllerListener#onCurrentActivityChanged(ict.predict.androidClient.controller.Controller, android.app.Activity)
	 */
	@Override
	public void onCurrentActivityChanged(Controller controller,
			Activity activity) {
	}
	/* (non-Javadoc)
	 * @see ict.predict.androidClient.controller.ControllerListener#onConnect(ict.predict.androidClient.controller.Controller)
	 */
	@Override
	public void onConnect(Controller controller) {
	}
	/* (non-Javadoc)
	 * @see ict.predict.androidClient.controller.ControllerListener#onConnected(ict.predict.androidClient.controller.Controller)
	 */
	@Override
	public void onConnected(Controller controller) {
	}
	/* (non-Javadoc)
	 * @see ict.predict.androidClient.controller.ControllerListener#onScanStart(ict.predict.androidClient.controller.Controller)
	 */
	@Override
	public void onScanStart(Controller controller) {
	}
	/* (non-Javadoc)
	 * @see ict.predict.androidClient.controller.ControllerListener#onScanFinished(ict.predict.androidClient.controller.Controller)
	 */
	@Override
	public void onScanFinished(Controller controller) {
	}
	/* (non-Javadoc)
	 * @see ict.predict.androidClient.controller.ControllerListener#onNewLecture(ict.predict.androidClient.controller.Controller, ict.model.Lecture)
	 */
	@Override
	public void onNewLecture(Controller controller, Lecture lecture) {
	}
	/* (non-Javadoc)
	 * @see ict.predict.androidClient.controller.ControllerListener#onLoggingIn(ict.predict.androidClient.controller.Controller)
	 */
	@Override
	public void onLoggingIn(Controller controller) {
	}
	/* (non-Javadoc)
	 * @see ict.predict.androidClient.controller.ControllerListener#onLoggedIn(ict.predict.androidClient.controller.Controller)
	 */
	@Override
	public void onLoggedIn(Controller controller) {
	}
	/* (non-Javadoc)
	 * @see ict.predict.androidClient.controller.ControllerListener#onNewMethod(ict.predict.androidClient.controller.Controller)
	 */
	@Override
	public void onNewMethod(Controller controller) {
		if (this.controller.getCurrentActivity() == this) {
			Intent newPrediction = new Intent();
			newPrediction.setClassName("ict.predict.androidClient", "ict.predict.androidClient.view.Predict");
	    	this.startActivity(newPrediction);
		}
	}
	/* (non-Javadoc)
	 * @see ict.predict.androidClient.controller.ControllerListener#onResult(ict.predict.androidClient.controller.Controller)
	 */
	@Override
	public void onResult(Controller controller) {
		if (controller.getCurrentActivity() == this) {
			Intent showResult = new Intent();
			showResult.setClassName("ict.predict.androidClient", "ict.predict.androidClient.view.ViewResult");
	    	this.startActivity(showResult);
		}
	}
	/* (non-Javadoc)
	 * @see ict.predict.androidClient.controller.ControllerListener#onLoggingOut(ict.predict.androidClient.controller.Controller)
	 */
	@Override
	public void onLoggingOut(Controller controller) {
		this.finish();
	}
	/* (non-Javadoc)
	 * @see ict.predict.androidClient.controller.ControllerListener#onLoggedOut(ict.predict.androidClient.controller.Controller)
	 */
	@Override
	public void onLoggedOut(Controller controller) {
		this.finish();
	}
	/* (non-Javadoc)
	 * @see ict.predict.androidClient.controller.ControllerListener#onDisconnected(ict.predict.androidClient.controller.Controller)
	 */
	@Override
	public void onDisconnected(Controller controller) {
		System.out.println("wait");
		this.finish();
	}
}