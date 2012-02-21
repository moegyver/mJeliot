package ict.predict.androidClient.view;

import ict.predict.androidClient.controller.Controller;
import ict.predict.androidClient.controller.ControllerListener;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.widget.Toast;

public abstract class AbstractPredictActivity extends Activity 
implements ControllerListener {
	protected Controller controller = null;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.controller  = (Controller)this.getApplication();
		this.controller.addControllerListener(this);
	}
	@Override
	public void onStart() {
		super.onStart();
		this.controller.setCurrentActivity(this);
	}
	@Override
	public void finish() {
		super.finish();
		this.controller.removeControllerListener(this);
	}
	public void showToast(final int resource) {
		this.runOnUiThread(new Runnable(){
			@Override
			public void run() {
				Context context = getApplicationContext();
				Toast toast = Toast.makeText(context, resource, Toast.LENGTH_SHORT);
				toast.show();
			}
		});
	}
}
