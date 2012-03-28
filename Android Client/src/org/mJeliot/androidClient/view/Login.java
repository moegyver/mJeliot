package org.mJeliot.androidClient.view;

import java.util.Vector;

import org.mJeliot.androidClient.R;
import org.mJeliot.androidClient.controller.Controller;
import org.mJeliot.model.Lecture;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.AdapterView.OnItemSelectedListener;

/**
 * @author Moritz Rogalli The Login-class displays a login-screen where users
 *         can select an alias and the network they want to connect to. It lets
 *         users scan for available networks and lets them log out from the
 *         server.
 */
public class Login extends AbstractMJeliotActivity {
	/**
	 * The input field for the user alias.
	 */
	private EditText aliasInput = null;
	/**
	 * The input list for the available networks.
	 */
	private Spinner networksInput = null;
	/**
	 * Button to start a new scan for networks.
	 */
	private Button rescanButton = null;
	/**
	 * Button to log in/log out.
	 */
	private Button loginButton = null;
	
	private ProgressDialog scanWaitProgressDialog = null;
	/**
	 * The available Lectures
	 */
	private Vector<Lecture> lectures = null;
	private ProgressDialog loggingInWaitProgressDialog;

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (controller.isConnected()) {
			setContentView(R.layout.login);
			this.loginButton = (Button) findViewById(R.id.buttonlogingo);
			this.loginButton.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					if (!controller.isLoggedIn()) {
						controller.login(aliasInput.getText().toString(), lectures
								.get(networksInput.getSelectedItemPosition()));
					} else {
						controller.logout();
					}
				}
			});
			this.loginButton.setEnabled(false);
			Resources res = getResources();
			this.scanWaitProgressDialog = new ProgressDialog(this);
			this.scanWaitProgressDialog.setMessage(res.getString(R.string.scanning));
			this.scanWaitProgressDialog.show();
			this.controller.scanForLectures();
			this.loggingInWaitProgressDialog = new ProgressDialog(this);
			this.loggingInWaitProgressDialog.setMessage(res.getString(R.string.login));
			this.rescanButton = (Button) findViewById(R.id.buttonloginrescan);
			this.rescanButton.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					scanWaitProgressDialog.show();
					controller.scanForLectures();
				}
			});
	
			this.aliasInput = (EditText) findViewById(R.id.editloginalias);
			this.aliasInput.addTextChangedListener(new TextWatcher() {
				@Override
				public void afterTextChanged(Editable s) {
					if (aliasInput.getText().toString().length() != 0
							&& networksInput.getSelectedItem() != null) {
						loginButton.setEnabled(true);
					} else {
						loginButton.setEnabled(false);
					}
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
			this.networksInput = (Spinner) findViewById(R.id.spinnerloginnetworks);
			this.networksInput
					.setOnItemSelectedListener(new OnItemSelectedListener() {
	
						@Override
						public void onItemSelected(AdapterView<?> arg0, View arg1,
								int arg2, long arg3) {
							if (aliasInput.getText().toString().length() > 0)
								loginButton.setEnabled(true);
						}
	
						@Override
						public void onNothingSelected(AdapterView<?> arg0) {
							loginButton.setEnabled(false);
	
						}
	
					});
			this.fillLectures();
		} else {
			System.err.println("Activity: Login, not connected");
			this.finish();
		}
	}
	@Override
	public void onRestart() {
		super.onRestart();
		if (!controller.isConnected()) {
			finish();
		}
	}
	@Override
	public void onResume() {
		super.onResume();
		if (!controller.isConnected()) {
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

	@Override
	public void onScanFinished(Controller controller) {
		this.scanWaitProgressDialog.dismiss();
		this.fillLectures();
	}

	@Override
	public void onNewLecture(Controller controller, Lecture lecture) {
		this.fillLectures();
	}

	@Override
	public void onLoggingIn(Controller controller) {
		this.loggingInWaitProgressDialog.show();
	}

	@Override
	public void onLoggedIn(Controller controller) {
		this.runOnUiThread(new Runnable() {
			public void run() {
				loggingInWaitProgressDialog.dismiss();
				loginButton.setText(R.string.logout);
			}
		});
		if (this.controller.getCurrentActivity() == this) {
			this.showToast(R.string.loggedin);
			Intent waitIntent = new Intent();
			waitIntent.setClassName("org.mJeliot.androidClient",
					"org.mJeliot.androidClient.view.Wait");
			waitIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			this.startActivity(waitIntent);
		}
	}

	@Override
	public void onNewMethod(Controller controller) {
		if (this.controller.getCurrentActivity() == this) {
			Intent newPrediction = new Intent();
			newPrediction.setClassName("org.mJeliot.androidClient",
					"org.mJeliot.androidClient.view.Predict");
			this.startActivity(newPrediction);
		}
	}

	@Override
	public void onResult(Controller controller) {
		if (this.controller.hasCurrentMethod()
				&& this.controller.getCurrentActivity() == this) {
			Intent showResult = new Intent();
			showResult.setClassName("org.mJeliot.androidClient",
					"org.mJeliot.androidClient.view.ViewResult");
			this.startActivity(showResult);
		}
	}

	@Override
	public void onLoggingOut(Controller controller) {
	}

	@Override
	public void onLoggedOut(Controller controller) {
		this.showToast(R.string.loggedout);
		this.runOnUiThread(new Runnable() {
			public void run() {
				loginButton.setText(R.string.login);
			}
		});
	}

	@Override
	public void onDisconnected(Controller controller, boolean isForced) {
		this.finish();
	}

	/**
	 * Enables the controls of the login view. The login button is not enabled
	 * unless there is a valid alias entered and an available network selected.
	 */
	private void enableControls() {
		this.rescanButton.setEnabled(true);
		this.networksInput.setEnabled(true);
		this.aliasInput.setEnabled(true);
		if (this.networksInput.getSelectedItemPosition() != Spinner.INVALID_POSITION
				&& this.aliasInput.getText().toString().length() > 0) {
			this.loginButton.setEnabled(true);
		}
	}

	/**
	 * Disables the controls of the login view. The login button has to be
	 * handed separately to enable the user to logout.
	 */
	/*private void disableControls() {
		this.aliasInput.setEnabled(false);
		this.networksInput.setEnabled(false);
		this.rescanButton.setEnabled(false);
	}*/

	private void fillLectures() {
		final ArrayAdapter<CharSequence> content = new ArrayAdapter<CharSequence>(
				this, android.R.layout.simple_spinner_item);
		this.lectures = new Vector<Lecture>();
		for (Lecture lecture : this.controller.getAvailableLectures()) {
			content.add(lecture.getName());
			this.lectures.add(lecture);
		}
		content.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		this.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				networksInput.setAdapter(content);
			}
		});
		this.enableControls();
	}
	@Override
	public void onCodingTask(Controller controller, String code) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onLiveModeChanged(Controller controller, boolean liveMode) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onAnimationControlCommand(Controller controller, String command) {
		// TODO Auto-generated method stub
		
	}
}