package org.mJeliot.androidClient.view.edit;

import android.app.Activity;
import android.os.Bundle;
import android.widget.EditText;
import org.mJeliot.androidClient.R;
import org.mJeliot.androidClient.controller.Controller;
import org.mJeliot.androidClient.edit.CodeChangeWatcher;
import org.mJeliot.androidClient.view.AbstractMJeliotActivity;
import org.mJeliot.model.Lecture;

public class CodeEditor extends AbstractMJeliotActivity {
	

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
			setContentView(R.layout.codeeditor);
			EditText editor = (EditText)findViewById(R.id.codeEditor);
			final CodeChangeWatcher codeChangeWatcher = new CodeChangeWatcher(this);
			editor.addTextChangedListener(codeChangeWatcher);
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
		public void onDisconnected(Controller controller) {
			this.finish();
		}

		public void updateText(CharSequence s, int cursorPosition) {
			this.controller.sendCode(s, cursorPosition);
		}

}
