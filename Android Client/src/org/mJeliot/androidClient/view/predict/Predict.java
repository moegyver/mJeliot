package org.mJeliot.androidClient.view.predict;

import org.mJeliot.androidClient.R;
import org.mJeliot.androidClient.controller.Controller;
import org.mJeliot.androidClient.view.AbstractMJeliotActivity;
import org.mJeliot.model.Lecture;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TextView;

/**
 * @author Moritz Rogalli The predict view presents a mask for entering
 *         predictions. It creates a TextEdit for every parameter of the current
 *         assignment. Users can lock their prediction which signals that the
 *         user is done predicting. As soon as a result is posted the ViewResult
 *         view is started.
 */
public class Predict extends AbstractMJeliotActivity {
	/**
	 * The grid for the form to make it prettier
	 */
	private TableLayout layout = null;

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		this.layout = new PredictList(this, controller.getUser(),
				this.controller.getCurrentMethod());
		this.setContentView(this.layout);

		final Button lockButton = new Button(this);
		lockButton.setText(R.string.lock);
		lockButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				lockButton.requestFocus();
				lockAndHandIn();
			}
		});
		this.layout.addView(lockButton);
		this.showToast(R.string.newassignment);
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

	@Override
	public void onConnect(Controller controller) {
	}

	@Override
	public void onConnected(Controller controller) {
	}

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

	@Override
	public void onLoggingIn(Controller controller) {
	}

	@Override
	public void onLoggedIn(Controller controller) {
	}

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

	@Override
	public void onResult(Controller controller) {
		if (this.controller.getCurrentActivity() == this) {
			Intent showResult = new Intent();
			showResult.setClassName("org.mJeliot.androidClient",
					"org.mJeliot.androidClient.view.predict.ViewResult");
			this.startActivity(showResult);
			this.finish();
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
		System.out.println("log");
		this.finish();
	}

	@Override
	public void onLoggedOut(Controller controller) {
		System.out.println("logged");
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

	/**
	 * Locks all the predictions and hands the predicted values in to the
	 * server.
	 */
	private void lockAndHandIn() {
		for (int i = 0; i < layout.getChildCount(); i++) {
			layout.getChildAt(i).setEnabled(false);
		}
		final Dialog waitDialog = createWaitOnResultDialog();
		this.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				waitDialog.show();
			}
		});
		controller.handInPrediction();
	}

	/**
	 * Creates a dialog that can be shown until the server posts the result.
	 * 
	 * @return the finished dialog
	 */
	private Dialog createWaitOnResultDialog() {
		Dialog waitDialog = new Dialog(this);
		TextView waitDialogText = new TextView(this);
		waitDialogText.setText(R.string.waitforresult);
		waitDialog.setContentView(waitDialogText);
		return waitDialog;
	}
}