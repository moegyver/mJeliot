package org.mJeliot.androidClient.controller;

import org.mJeliot.model.Lecture;

import android.app.Activity;
import android.content.Intent;

public class CodingTaskController implements ControllerListener {
	
	private final Controller controller;

	public CodingTaskController(Controller controller) {
		this.controller = controller;
		controller.addControllerListener(this);
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
		Intent editor = new Intent();
		editor.setClassName("org.mJeliot.androidClient",
				"org.mJeliot.androidClient.view.edit.CodeEditor");
		this.controller.startActivity(editor);
	}

	@Override
	public void onLiveModeChanged(Controller controller, boolean liveMode) {
	}

	@Override
	public void onAnimationControlCommand(Controller controller, String command) {
	}
}
