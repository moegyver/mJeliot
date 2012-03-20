package org.mJeliot.androidClient.view.predict;

import org.mJeliot.androidClient.R;
import org.mJeliot.androidClient.controller.Controller;
import org.mJeliot.androidClient.view.AbstractMJeliotActivity;
import org.mJeliot.model.Lecture;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;

/**
 * @author Moritz Rogalli
 * ViewResult is an Activity that shows a ResultList with the result for every parameter
 * and a "done"-button to return to the previous activity on the activity stack.
 */
public class ViewResult extends AbstractMJeliotActivity {
	/**
	 * The layout for the activity.
	 */
	private LinearLayout layout = null;

	/* (non-Javadoc)
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.layout = new LinearLayout(this);
        this.layout.setOrientation(LinearLayout.VERTICAL);
        layout.addView(new ResultList(this, this.controller.getUser(),
        		this.controller.getCurrentMethod()));
        Button doneButton = new Button(this);
        doneButton.setText(R.string.done);
        doneButton.setOnClickListener(
        		new OnClickListener() {
					@Override
					public void onClick(View v) {
						finish();
					}
        		}
        		);
        layout.addView(doneButton);
        setContentView(this.layout);
    }

    @Override
	public void onRestart() {
		super.onRestart();
		if (!controller.isLoggedIn()) {
			finish();
		}
	}
	@Override
	public void onResume() {
		super.onResume();
		if (!controller.isLoggedIn()) {
			finish();
		}
	}
    /* (non-Javadoc)
     * @see org.mJeliot.androidClient.controller.ControllerListener#onCurrentActivityChanged(org.mJeliot.androidClient.controller.Controller, android.app.Activity)
     */
    @Override
	public void onCurrentActivityChanged(Controller controller,
			Activity activity) {
		if (this != activity) {
			this.finish();
		}
	}

	/* (non-Javadoc)
	 * @see org.mJeliot.androidClient.controller.
	 * ControllerListener#onConnect(org.mJeliot.androidClient.controller.Controller)
	 */
	@Override
	public void onConnect(Controller controller) {
	}

	/* (non-Javadoc)
	 * @see org.mJeliot.androidClient.controller.
	 * ControllerListener#onConnected(org.mJeliot.androidClient.controller.Controller)
	 */
	@Override
	public void onConnected(Controller controller) {
	}

	/* (non-Javadoc)
	 * @see org.mJeliot.androidClient.controller.
	 * ControllerListener#onScanStart(org.mJeliot.androidClient.controller.Controller)
	 */
	@Override
	public void onScanStart(Controller controller) {
	}

	/* (non-Javadoc)
	 * @see org.mJeliot.androidClient.controller.
	 * ControllerListener#onScanFinished(org.mJeliot.androidClient.controller.Controller)
	 */
	@Override
	public void onScanFinished(Controller controller) {
	}

	/* (non-Javadoc)
	 * @see org.mJeliot.androidClient.controller.ControllerListener#onNewLecture(org.mJeliot.androidClient.controller.Controller, org.mJeliot.model.Lecture)
	 */
	@Override
	public void onNewLecture(Controller controller, Lecture lecture) {
	}

	/* (non-Javadoc)
	 * @see org.mJeliot.androidClient.controller.ControllerListener#onLoggingIn(org.mJeliot.androidClient.controller.Controller)
	 */
	@Override
	public void onLoggingIn(Controller controller) {
	}

	/* (non-Javadoc)
	 * @see org.mJeliot.androidClient.controller.ControllerListener#onLoggedIn(org.mJeliot.androidClient.controller.Controller)
	 */
	@Override
	public void onLoggedIn(Controller controller) {
	}

	/* (non-Javadoc)
	 * @see org.mJeliot.androidClient.controller.
	 * ControllerListener#onNewMethod(org.mJeliot.androidClient.controller.Controller)
	 */
	@Override
	public void onNewMethod(Controller controller) {
		if (this.controller.getCurrentActivity() == this) {
			Intent newPrediction = new Intent();
			newPrediction.setClassName("org.mJeliot.androidClient",
					"org.mJeliot.androidClient.view.Predict");
			this.startActivityIfNeeded(newPrediction, -1);
			this.finish();
		}
	}

	/* (non-Javadoc)
	 * @see org.mJeliot.androidClient.controller.
	 * ControllerListener#onResult(org.mJeliot.androidClient.controller.Controller)
	 */
	@Override
	public void onResult(Controller controller) {
	}

	/* (non-Javadoc)
	 * @see org.mJeliot.androidClient.controller.ControllerListener#onLoggingOut(org.mJeliot.androidClient.controller.Controller)
	 */
	@Override
	public void onLoggingOut(Controller controller) {
		this.finish();
	}

	/* (non-Javadoc)
	 * @see org.mJeliot.androidClient.controller.ControllerListener#onLoggedOut(org.mJeliot.androidClient.controller.Controller)
	 */
	@Override
	public void onLoggedOut(Controller controller) {
		this.finish();
	}

	/* (non-Javadoc)
	 * @see org.mJeliot.androidClient.controller.
	 * ControllerListener#onDisconnected(org.mJeliot.androidClient.controller.Controller)
	 */
	@Override
	public void onDisconnected(Controller controller, boolean isForced) {
		this.finish();
	}

	@Override
	public void onCodingTask(Controller controller, String code) {
		// TODO Auto-generated method stub
		
	}
}