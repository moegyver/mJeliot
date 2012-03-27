package jeliot.mJeliot;

import jeliot.gui.CodeEditor2;

import org.mJeliot.model.Lecture;
import org.mJeliot.model.User;
import org.mJeliot.model.coding.CodingTask;
import org.mJeliot.model.predict.Method;

public class CodeButtonController implements MJeliotControllerListener {
	
	private final CodeEditor2 codeEditor;

	public CodeButtonController(MJeliotController controller, CodeEditor2 codeEditor) {
		controller.addMJeliotControllerListener(this);
		this.codeEditor = codeEditor;
	}

	@Override
	public void onClientConnected(MJeliotController mJeliotController,
			boolean isReconnected) {
	}

	@Override
	public void onClientDisconnected(MJeliotController mJeliotController) {
		codeEditor.setCodeButtonEnabled(false);
	}

	@Override
	public void onUserCountChanged(MJeliotController mJeliotController) {
	}

	@Override
	public void onAnswerCountChanged(MJeliotController mJeliotController) {
	}

	@Override
	public void onNewMethod(MJeliotController mJeliotController, Method method) {
	}

	@Override
	public void onResultPosted(MJeliotController mJeliotController,
			Method method) {
	}

	@Override
	public void onMethodCalled(MJeliotController mJeliotController,
			Method method) {
	}

	@Override
	public void onMethodReturned(MJeliotController mJeliotController,
			Method method) {
	}

	@Override
	public void onNewLecture(MJeliotController mJeliotController,
			Lecture lecture) {
	}

	@Override
	public void onLogin(MJeliotController mJeliotController, Lecture lecture) {
	}

	@Override
	public void onLoggedIn(MJeliotController mJeliotController,
			Lecture currentLecture) {
		System.out.println("logged in, enabling button");
		codeEditor.setCodeButtonEnabled(true);
	}

	@Override
	public void onLogout(MJeliotController mJeliotController, Lecture lecture) {
		codeEditor.setCodeButtonEnabled(false);
	}

	@Override
	public void onLectureUpdated(MJeliotController mJeliotController,
			Lecture lecture) {
	}

	@Override
	public void onLoggedOut(MJeliotController mJeliotController, Lecture lecture) {
		codeEditor.setCodeButtonEnabled(false);
	}

	@Override
	public void onUserLoggedIn(MJeliotController mJeliotController, User user,
			Lecture lecture) {
	}

	@Override
	public void onUserLoggedOut(MJeliotController mJeliotController, User user,
			Lecture lecture) {
	}

	@Override
	public void onCodeUpdate(Lecture lecture, User user, String code,
			int cursorPosition, boolean isDone, boolean requestedAttention) {
	}

	@Override
	public void onCodingTask(MJeliotController mJeliotController,
			CodingTask codingTask) {
	}
}
