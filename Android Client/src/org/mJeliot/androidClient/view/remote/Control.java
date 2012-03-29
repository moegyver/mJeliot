package org.mJeliot.androidClient.view.remote;

import org.mJeliot.androidClient.R;
import org.mJeliot.androidClient.controller.Controller;
import org.mJeliot.androidClient.view.AbstractMJeliotActivity;
import org.mJeliot.model.Lecture;
import org.mJeliot.model.RemoteController;
import org.mJeliot.model.RemoteControllerListener;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class Control extends AbstractMJeliotActivity implements RemoteControllerListener {

	private SeekBar speedSlider;




	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.remotecontrol);
		ImageButton returnButton = (ImageButton)findViewById(R.id.returnToEditButton);
		returnButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				getRemoteController().endControl();
				finish();
			}
		});
		ImageButton stepButton = (ImageButton)findViewById(R.id.remoteStepButton);
		stepButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				getRemoteController().step();
			}
		});
		ImageButton playButton = (ImageButton)findViewById(R.id.remotePlayButton);
		playButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				getRemoteController().play();
			}
		});
		ImageButton pauseButton = (ImageButton)findViewById(R.id.remotePauseButton);
		pauseButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				getRemoteController().pause();
			}
		});
		ImageButton rewindButton = (ImageButton)findViewById(R.id.remoteRewindButton);
		rewindButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				getRemoteController().rewind();
			}
		});
		speedSlider = (SeekBar)findViewById(R.id.animationSpeedBar);
		controller.getRemoteController().addRemoteControllerListener(this);
		speedSlider.setProgress(controller.getRemoteController().getAnimationSpeed());
		speedSlider.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				if (fromUser) {
					controller.getRemoteController().changeAnimationSpeed(progress);
				}
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
			}
		});
	}
	
	
	
	
	private RemoteController getRemoteController() {
		return this.controller.getRemoteController();
	}
	
	
	
	@Override
	public void onScanStart(Controller controller) {
	}

	@Override
	public void onScanFinished(Controller controller) {
	}

	@Override
	public void onConnect(Controller controller) {
	}

	@Override
	public void onConnected(Controller controller) {
	}

	@Override
	public void onLoggingIn(Controller controller) {
	}

	@Override
	public void onLoggedIn(Controller controller) {
	}

	@Override
	public void onLoggingOut(Controller controller) {
	}

	@Override
	public void onLoggedOut(Controller controller) {
	}

	@Override
	public void onDisconnected(Controller controller, boolean isForced) {
	}

	@Override
	public void onNewMethod(Controller controller) {
	}

	@Override
	public void onResult(Controller controller) {
	}

	@Override
	public void onCurrentActivityChanged(Controller controller,
			Activity activity) {
	}

	@Override
	public void onNewLecture(Controller controller, Lecture lecture) {
	}

	@Override
	public void onCodingTask(Controller controller, String code) {
	}

	@Override
	public void onLiveModeChanged(Controller controller, boolean liveMode) {
	}




	@Override
	public void onAnimationControlCommand(Controller controller, String command) {
		if (command.equals("endControl")) {
			this.finish();
		}
	}




	@Override
	public void onAnimationSpeedChanged(RemoteController controller,
			int animationSpeed) {
		this.speedSlider.setProgress(animationSpeed);
	}

}
