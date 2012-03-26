package org.mJeliot.androidClient.view.edit;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;

import org.mJeliot.androidClient.R;
import org.mJeliot.androidClient.controller.Controller;
import org.mJeliot.androidClient.view.AbstractMJeliotActivity;
import org.mJeliot.model.Lecture;

public class CodeEditor extends AbstractMJeliotActivity {
	

		private static final long NOT_LIVE_UPDATE_INTERVAL = 5000;
		private static final long LIVE_UPDATE_INTERVAL = 1000;
		private long lastUpdate = System.currentTimeMillis();
		private EditText editor;
		private int cursorPosition = 0;
		private boolean liveMode = false;

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
			editor = (EditText)findViewById(R.id.codeEditor);
			editor.setText(controller.getOriginalCode());
			editor.addTextChangedListener(new TextWatcher() {
					@Override
					public void onTextChanged(CharSequence s, int start, int before, int count) {
						CodeEditor.this.onCodeUpdate(s, start);
					}
					@Override
					public void afterTextChanged(Editable arg0) {
					}
					@Override
					public void beforeTextChanged(CharSequence arg0, int arg1,
							int arg2, int arg3) {
					}
			});
			
			ImageButton discardButton = (ImageButton) findViewById(R.id.discardbutton);
			discardButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					AlertDialog.Builder builder = new AlertDialog.Builder(CodeEditor.this);
					builder.setMessage(R.string.reallydiscard)
					       .setCancelable(false)
					       .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
					           public void onClick(DialogInterface dialog, int id) {
					        	   editor.setText(controller.getOriginalCode());
					        	   dialog.dismiss();
					           }
					       })
					       .setNegativeButton("No", new DialogInterface.OnClickListener() {
					           public void onClick(DialogInterface dialog, int id) {
					                dialog.cancel();
					           }
					       });
					AlertDialog alert = builder.create();
					alert.show();
				}
			});
			
			ImageButton attentionButton = (ImageButton) findViewById(R.id.attentionbutton);
			attentionButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					requestAttention();
				}
			});
			
			ImageButton doneButton = (ImageButton) findViewById(R.id.donebutton);
			doneButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					done();
				}
			});
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

		protected void done() {
			this.controller.sendCodeUpdate(this.getCode(), this.cursorPosition, true, false);
			System.out.println("CodeEditor: user is done");
		}

		protected void requestAttention() {
			this.controller.sendCodeUpdate(this.getCode(), this.cursorPosition, false, true);
			System.out.println("CodeEditor: user requested attention");
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
			System.out.println("CodeEditor: closing activity, user logged out.");
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
			System.out.println("CodeEditor: closing activity, user logged out.");
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
			System.out.println("CodeEditor: closing activity, disconnected.");
			this.finish();
		}

		public void onCodeUpdate(CharSequence s, int cursorPosition) {
			this.cursorPosition = cursorPosition;
			if (this.controller.isEditorInLiveMode() || this.isUpdateNeeded()) {
				this.lastUpdate = System.currentTimeMillis();
				this.controller.sendCodeUpdate(s.toString(), this.cursorPosition, false, false);	
			}
		}

		private boolean isUpdateNeeded() {
			long updateInterval = CodeEditor.NOT_LIVE_UPDATE_INTERVAL;
			//if (liveMode) {
				//updateInterval = CodeEditor.LIVE_UPDATE_INTERVAL;
			//}
			return liveMode || lastUpdate  + updateInterval < System.currentTimeMillis();
		}
		
		public String getCode() {
			return this.editor.getText().toString();
		}
		
		public void setCode(String code) {
			this.editor.setText(code);
		}

		@Override
		public void onCodingTask(Controller controller, String code) {
		}

		@Override
		public void onLiveModeChanged(Controller controller, boolean liveMode) {
			this.liveMode  = liveMode;
		}
}
