package org.mJeliot.androidClient.view;

import org.mJeliot.androidClient.controller.Controller;

import ict.model.Lecture;
import ict.predict.androidClient.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

/**
 * The Start Activity shows a picture and a button to connect to the ICT server.
 * 
 * @author Moritz Rogalli
 * 
 */
public class Start extends AbstractMJeliotActivity {

	private Button loginButton = null;
	private String url = "";
	private EditText urlEditText;

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.start);
		this.loginButton = (Button) findViewById(R.id.buttonconnect);
		this.loginButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!controller.isConnected()) {
					controller.connect(url);
				} else {
					controller.disconnect();
				}
			}
		});
		this.urlEditText = (EditText) findViewById(R.id.editurl);
		this.urlEditText.addTextChangedListener(new TextWatcher() {
			@Override
			public void afterTextChanged(Editable s) {
				url = s.toString();
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
			}
		});
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mJeliot.androidClient.view.controller.ControllerListener#
	 * onCurrentActivityChanged
	 * (org.mJeliot.androidClient.view.controller.Controller,
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
	 * org.mJeliot.androidClient.view.controller.ControllerListener#onConnect
	 * (org.mJeliot.androidClient.view.controller.Controller)
	 */
	@Override
	public void onConnect(final Controller controller) {
		this.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				loginButton.setText(R.string.cancel);
			}
		});
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.mJeliot.androidClient.view.controller.ControllerListener#onConnected
	 * (org.mJeliot.androidClient.view.controller.Controller)
	 */
	@Override
	public void onConnected(Controller controller) {
		this.showToast(R.string.connected);
		this.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				loginButton.setText(R.string.disconnect);
			}
		});
		Intent loginIntent = new Intent();
		loginIntent.setClassName("ict.predict.androidClient",
				"org.mJeliot.androidClient.view.Login");
		loginIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		this.startActivity(loginIntent);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.mJeliot.androidClient.view.controller.ControllerListener#onScanStart
	 * (org.mJeliot.androidClient.view.controller.Controller)
	 */
	@Override
	public void onScanStart(Controller controller) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.mJeliot.androidClient.view.controller.ControllerListener#onScanFinished
	 * (org.mJeliot.androidClient.view.controller.Controller)
	 */
	@Override
	public void onScanFinished(Controller controller) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.mJeliot.androidClient.view.controller.ControllerListener#onNewLecture
	 * (org.mJeliot.androidClient.view.controller.Controller, ict.model.Lecture)
	 */
	@Override
	public void onNewLecture(Controller controller, Lecture lecture) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.mJeliot.androidClient.view.controller.ControllerListener#onLoggingIn
	 * (org.mJeliot.androidClient.view.controller.Controller)
	 */
	@Override
	public void onLoggingIn(Controller controller) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.mJeliot.androidClient.view.controller.ControllerListener#onLoggedIn
	 * (org.mJeliot.androidClient.view.controller.Controller)
	 */
	@Override
	public void onLoggedIn(Controller controller) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.mJeliot.androidClient.view.controller.ControllerListener#onNewMethod
	 * (org.mJeliot.androidClient.view.controller.Controller)
	 */
	@Override
	public void onNewMethod(Controller controller) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.mJeliot.androidClient.view.controller.ControllerListener#onResult
	 * (org.mJeliot.androidClient.view.controller.Controller)
	 */
	@Override
	public void onResult(Controller controller) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.mJeliot.androidClient.view.controller.ControllerListener#onLoggingOut
	 * (org.mJeliot.androidClient.view.controller.Controller)
	 */
	@Override
	public void onLoggingOut(Controller controller) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.mJeliot.androidClient.view.controller.ControllerListener#onLoggedOut
	 * (org.mJeliot.androidClient.view.controller.Controller)
	 */
	@Override
	public void onLoggedOut(Controller controller) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.mJeliot.androidClient.view.controller.ControllerListener#onDisconnected
	 * (org.mJeliot.androidClient.view.controller.Controller)
	 */
	@Override
	public void onDisconnected(Controller controller) {
		this.showToast(R.string.disconnected);
		this.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				loginButton.setText(R.string.connect);
			}

		});
	}
}