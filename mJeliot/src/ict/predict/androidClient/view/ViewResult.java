package ict.predict.androidClient.view;

import ict.model.Lecture;
import ict.predict.androidClient.R;
import ict.predict.androidClient.controller.Controller;
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
public class ViewResult extends AbstractPredictActivity {
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

    /* (non-Javadoc)
     * @see ict.predict.androidClient.controller.ControllerListener#onCurrentActivityChanged(ict.predict.androidClient.controller.Controller, android.app.Activity)
     */
    @Override
	public void onCurrentActivityChanged(Controller controller,
			Activity activity) {
		if (this != activity) {
			this.finish();
		}
	}

	/* (non-Javadoc)
	 * @see ict.predict.androidClient.controller.
	 * ControllerListener#onConnect(ict.predict.androidClient.controller.Controller)
	 */
	@Override
	public void onConnect(Controller controller) {
	}

	/* (non-Javadoc)
	 * @see ict.predict.androidClient.controller.
	 * ControllerListener#onConnected(ict.predict.androidClient.controller.Controller)
	 */
	@Override
	public void onConnected(Controller controller) {
	}

	/* (non-Javadoc)
	 * @see ict.predict.androidClient.controller.
	 * ControllerListener#onScanStart(ict.predict.androidClient.controller.Controller)
	 */
	@Override
	public void onScanStart(Controller controller) {
	}

	/* (non-Javadoc)
	 * @see ict.predict.androidClient.controller.
	 * ControllerListener#onScanFinished(ict.predict.androidClient.controller.Controller)
	 */
	@Override
	public void onScanFinished(Controller controller) {
	}

	/* (non-Javadoc)
	 * @see ict.predict.androidClient.controller.ControllerListener#onNewLecture(ict.predict.androidClient.controller.Controller, ict.model.Lecture)
	 */
	@Override
	public void onNewLecture(Controller controller, Lecture lecture) {
	}

	/* (non-Javadoc)
	 * @see ict.predict.androidClient.controller.ControllerListener#onLoggingIn(ict.predict.androidClient.controller.Controller)
	 */
	@Override
	public void onLoggingIn(Controller controller) {
	}

	/* (non-Javadoc)
	 * @see ict.predict.androidClient.controller.ControllerListener#onLoggedIn(ict.predict.androidClient.controller.Controller)
	 */
	@Override
	public void onLoggedIn(Controller controller) {
	}

	/* (non-Javadoc)
	 * @see ict.predict.androidClient.controller.
	 * ControllerListener#onNewMethod(ict.predict.androidClient.controller.Controller)
	 */
	@Override
	public void onNewMethod(Controller controller) {
		if (this.controller.getCurrentActivity() == this) {
			Intent newPrediction = new Intent();
			newPrediction.setClassName("ict.predict.androidClient",
					"ict.predict.androidClient.view.Predict");
			this.startActivityIfNeeded(newPrediction, -1);
			this.finish();
		}
	}

	/* (non-Javadoc)
	 * @see ict.predict.androidClient.controller.
	 * ControllerListener#onResult(ict.predict.androidClient.controller.Controller)
	 */
	@Override
	public void onResult(Controller controller) {
	}

	/* (non-Javadoc)
	 * @see ict.predict.androidClient.controller.ControllerListener#onLoggingOut(ict.predict.androidClient.controller.Controller)
	 */
	@Override
	public void onLoggingOut(Controller controller) {
		this.finish();
	}

	/* (non-Javadoc)
	 * @see ict.predict.androidClient.controller.ControllerListener#onLoggedOut(ict.predict.androidClient.controller.Controller)
	 */
	@Override
	public void onLoggedOut(Controller controller) {
		this.finish();
	}

	/* (non-Javadoc)
	 * @see ict.predict.androidClient.controller.
	 * ControllerListener#onDisconnected(ict.predict.androidClient.controller.Controller)
	 */
	@Override
	public void onDisconnected(Controller controller) {
		this.finish();
	}
}