package ict.predict.androidClient.view;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;
import ict.model.Lecture;
import ict.predict.androidClient.R;
import ict.predict.androidClient.controller.Controller;
import ict.predict.androidClient.controller.ControllerListener;

public class ToastGenerator implements ControllerListener {
	
	private Context context;
	
	public ToastGenerator(Context context) {
		this.context = context;
	}
	
	private void showToast(int message) {
		Toast toast = Toast.makeText(this.context.getApplicationContext(), message, Toast.LENGTH_SHORT);
		toast.show();
	}

	@Override
	public void onScanStart(Controller controller) {
		this.showToast(R.string.scanning);
		
	}

	@Override
	public void onScanFinished(Controller controller) {
		this.showToast(R.string.scanfinished);
	}

	@Override
	public void onConnect(Controller controller) {
		this.showToast(R.string.connect);
	}

	@Override
	public void onConnected(Controller controller) {
		this.showToast(R.string.connected);
	}

	@Override
	public void onLoggingIn(Controller controller) {
		this.showToast(R.string.login);
	}

	@Override
	public void onLoggedIn(Controller controller) {
		this.showToast(R.string.loggedin);
	}

	@Override
	public void onLoggingOut(Controller controller) {
		this.showToast(R.string.logout);
	}

	@Override
	public void onLoggedOut(Controller controller) {
		this.showToast(R.string.loggedout);
	}

	@Override
	public void onDisconnected(Controller controller) {
		this.showToast(R.string.disconnected);
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
}
