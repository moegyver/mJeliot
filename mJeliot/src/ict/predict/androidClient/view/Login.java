package ict.predict.androidClient.view;

import java.util.Vector;

import ict.model.Lecture;
import ict.predict.androidClient.controller.Controller;
import ict.predict.androidClient.R;
import android.app.Activity;
import android.content.Intent;
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
 * @author Moritz Rogalli
 * The Login-class displays a login-screen where users can select an alias
 * and the network they want to connect to. It lets users scan for available networks
 * and lets them log out from the server.
 */
public class Login extends AbstractPredictActivity {
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
	/**
	 * The available Lectures
	 */
	private Vector<Lecture> lectures = null;
    
    /* (non-Javadoc)
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        this.loginButton = (Button) findViewById(R.id.buttonlogingo);
        this.loginButton.setOnClickListener(
        	new OnClickListener() {
        		public void onClick(View v) {
        			if (!controller.isLoggedIn()) {
        				controller.login(aliasInput.getText().toString(),
        						lectures.get(networksInput.getSelectedItemPosition()));
        			} else {
        				controller.logout();
        			}
        		}
        	}
        );
        this.loginButton.setEnabled(false);
        this.rescanButton = (Button) findViewById(R.id.buttonloginrescan);
        this.rescanButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            	controller.scanForNetworks();
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
        this.networksInput.setOnItemSelectedListener(new OnItemSelectedListener() {

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
		this.showTextToastOnUi(this, R.string.scanning);
	}

	/* (non-Javadoc)
	 * @see ict.predict.androidClient.controller.ControllerListener#onScanFinished(ict.predict.androidClient.controller.Controller)
	 */
	@Override
	public void onScanFinished(Controller controller) {
		this.showTextToastOnUi(this, R.string.scanfinished);
		this.fillLectures();
	}

	/* (non-Javadoc)
	 * @see ict.predict.androidClient.controller.ControllerListener#onNewLecture(ict.predict.androidClient.controller.Controller, ict.model.Lecture)
	 */
	@Override
	public void onNewLecture(Controller controller, Lecture lecture) {
		this.fillLectures();
	}

	/* (non-Javadoc)
	 * @see ict.predict.androidClient.controller.ControllerListener#onLoggingIn(ict.predict.androidClient.controller.Controller)
	 */
	@Override
	public void onLoggingIn(Controller controller) {
		this.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				disableControls();
				loginButton.setText(R.string.cancel);
			}
		});
	}

	/* (non-Javadoc)
	 * @see ict.predict.androidClient.controller.ControllerListener#onLoggedIn(ict.predict.androidClient.controller.Controller)
	 */
	@Override
	public void onLoggedIn(Controller controller) {
		this.runOnUiThread(new Runnable() {
			public void run() {
				loginButton.setText(R.string.logout);
			}
		});	
		if (this.controller.getCurrentActivity() == this) {
			this.showTextToastOnUi(this, R.string.loggedin);
			Intent waitIntent = new Intent();
			waitIntent.setClassName("ict.predict.androidClient",
					"ict.predict.androidClient.view.Wait");
			waitIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			this.startActivity(waitIntent);
		}
	}	
	/* (non-Javadoc)
	 * @see ict.predict.androidClient.controller.ControllerListener#onNewMethod(ict.predict.androidClient.controller.Controller)
	 */
	@Override
	public void onNewMethod(Controller controller) {
		if (this.controller.getCurrentActivity() == this) {
			Intent newPrediction = new Intent();
			newPrediction.setClassName("ict.predict.androidClient",
					"ict.predict.androidClient.view.Predict");
			this.startActivity(newPrediction);
		}
	}

	/* (non-Javadoc)
	 * @see ict.predict.androidClient.controller.ControllerListener#onResult(ict.predict.androidClient.controller.Controller)
	 */
	@Override
	public void onResult(Controller controller) {
		if (this.controller.hasCurrentMethod()
				&& this.controller.getCurrentActivity() == this) {
			Intent showResult = new Intent();
			showResult.setClassName("ict.predict.androidClient",
					"ict.predict.androidClient.view.ViewResult");
			this.startActivity(showResult);
		}
	}

	@Override
	public void onLoggingOut(Controller controller) {
	}

	@Override
	public void onLoggedOut(Controller controller) {
		this.showTextToastOnUi(this, R.string.loggedout);
		this.runOnUiThread(new Runnable() {
			public void run() {
				loginButton.setText(R.string.login);
				enableControls();
			}
		});
	}
	
	/* (non-Javadoc)
	 * @see ict.predict.androidClient.controller.ControllerListener#onDisconnected(ict.predict.androidClient.controller.Controller)
	 */
	@Override
	public void onDisconnected(Controller controller) {
		this.finish();
	}
	
	/**
	 * Enables the controls of the login view. The login button is not enabled unless
	 * there is a valid alias entered and an available network selected. 
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
	 * Disables the controls of the login view. The login button has to be handed
	 * separately to enable the user to logout.
	 */
	private void disableControls() {
    	this.aliasInput.setEnabled(false);
    	this.networksInput.setEnabled(false);
    	this.rescanButton.setEnabled(false);
	}

	private void fillLectures() {
		final ArrayAdapter<CharSequence> content = new ArrayAdapter<CharSequence>(this,
				android.R.layout.simple_spinner_item);
		this.lectures = new Vector<Lecture>();
		for(Lecture lecture : this.controller.getAvailableLectures()) {
			content.add(lecture.getName());
			this.lectures.add(lecture);
		}
		content.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		this.runOnUiThread(new Runnable(){
			@Override
			public void run() {
				networksInput.setAdapter(content);
			}
		});
		this.enableControls();
	}
}