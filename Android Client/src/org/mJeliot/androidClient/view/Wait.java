package org.mJeliot.androidClient.view;

import org.mJeliot.androidClient.R;
import org.mJeliot.androidClient.controller.Controller;
import org.mJeliot.model.Lecture;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

/**
 * A Wait-Activity shows a wait message and reacts on new lectures and results.
 * It finishes itself on logout and disconnect.
 * 
 * @author Moritz Rogalli
 * 
 */
public class Wait extends AbstractMJeliotActivity {
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.mJeliot.androidClient.view.AbstractMJeliotActivity#onCreate(android
	 * .os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.wait);
		Button fakeedit = (Button)findViewById(R.id.fakeedit);
		fakeedit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent editor = new Intent();
				editor.setClassName("org.mJeliot.androidClient",
						"org.mJeliot.androidClient.view.edit.CodeEditor");
				startActivity(editor);
			}
		});
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mJeliot.androidClient.controller.ControllerListener#
	 * onCurrentActivityChanged(org.mJeliot.androidClient.controller.Controller,
	 * android.app.Activity)
	 */
	@Override
	public void onCurrentActivityChanged(Controller controller,
			Activity activity) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.mJeliot.androidClient.controller.ControllerListener#onConnect(org
	 * .mJeliot.androidClient.controller.Controller)
	 */
	@Override
	public void onConnect(Controller controller) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.mJeliot.androidClient.controller.ControllerListener#onConnected(org
	 * .mJeliot.androidClient.controller.Controller)
	 */
	@Override
	public void onConnected(Controller controller) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.mJeliot.androidClient.controller.ControllerListener#onScanStart(org
	 * .mJeliot.androidClient.controller.Controller)
	 */
	@Override
	public void onScanStart(Controller controller) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.mJeliot.androidClient.controller.ControllerListener#onScanFinished
	 * (org.mJeliot.androidClient.controller.Controller)
	 */
	@Override
	public void onScanFinished(Controller controller) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.mJeliot.androidClient.controller.ControllerListener#onNewLecture(
	 * org.mJeliot.androidClient.controller.Controller, org.mJeliot.model.Lecture)
	 */
	@Override
	public void onNewLecture(Controller controller, Lecture lecture) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.mJeliot.androidClient.controller.ControllerListener#onLoggingIn(org
	 * .mJeliot.androidClient.controller.Controller)
	 */
	@Override
	public void onLoggingIn(Controller controller) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.mJeliot.androidClient.controller.ControllerListener#onLoggedIn(org
	 * .mJeliot.androidClient.controller.Controller)
	 */
	@Override
	public void onLoggedIn(Controller controller) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.mJeliot.androidClient.controller.ControllerListener#onNewMethod(org
	 * .mJeliot.androidClient.controller.Controller)
	 */
	@Override
	public void onNewMethod(Controller controller) {
		if (this.controller.getCurrentActivity() == this) {
			Intent newPrediction = new Intent();
			newPrediction.setClassName("org.mJeliot.androidClient",
					"org.mJeliot.androidClient.view.predict.Predict");
			this.startActivity(newPrediction);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.mJeliot.androidClient.controller.ControllerListener#onResult(org.
	 * mJeliot.androidClient.controller.Controller)
	 */
	@Override
	public void onResult(Controller controller) {
		if (controller.getCurrentActivity() == this) {
			Intent showResult = new Intent();
			showResult.setClassName("org.mJeliot.androidClient",
					"org.mJeliot.androidClient.view.predict.ViewResult");
			this.startActivity(showResult);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.mJeliot.androidClient.controller.ControllerListener#onLoggingOut(
	 * org.mJeliot.androidClient.controller.Controller)
	 */
	@Override
	public void onLoggingOut(Controller controller) {
		this.finish();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.mJeliot.androidClient.controller.ControllerListener#onLoggedOut(org
	 * .mJeliot.androidClient.controller.Controller)
	 */
	@Override
	public void onLoggedOut(Controller controller) {
		this.finish();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.mJeliot.androidClient.controller.ControllerListener#onDisconnected
	 * (org.mJeliot.androidClient.controller.Controller)
	 */
	@Override
	public void onDisconnected(Controller controller, boolean isForced) {
		this.finish();
	}
}