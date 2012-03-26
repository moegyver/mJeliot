package jeliot.gui.mJeliot;

import javax.swing.BoxLayout;
import javax.swing.JPanel;

import org.mJeliot.model.Lecture;
import org.mJeliot.model.User;
import org.mJeliot.model.coding.CodingTask;
import org.mJeliot.model.coding.CodingTaskListener;
import org.mJeliot.model.coding.CodingTaskUserCode;
import org.mJeliot.model.predict.Method;

import jeliot.mJeliot.MJeliotController;
import jeliot.mJeliot.MJeliotControllerListener;

public class UserSelection extends JPanel implements MJeliotControllerListener, CodingTaskListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6911785838601141857L;
	private JPanel panel;
	private final MJeliotController mJeliotController;

	public UserSelection(MJeliotController mJeliotController) {
		super();
		this.mJeliotController = mJeliotController;
		panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
		panel.setAlignmentY(LEFT_ALIGNMENT);
		mJeliotController.addMJeliotControllerListener(this);
		// TODO listen to CodingTask as well
		this.add(panel);
	}
	
	@Override
	public void onClientConnected(MJeliotController mJeliotController,
			boolean isReconnected) {
	}

	@Override
	public void onClientDisconnected(MJeliotController mJeliotController) {
		this.disableUserSelection();
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
	}

	@Override
	public void onLogout(MJeliotController mJeliotController, Lecture lecture) {
	}

	@Override
	public void onLectureUpdated(MJeliotController mJeliotController,
			Lecture lecture) {
	}

	@Override
	public void onLoggedOut(MJeliotController mJeliotController, Lecture lecture) {
		this.disableUserSelection();
	}

	private void disableUserSelection() {
		// TODO disable the tab when not logged in
		
	}

	@Override
	public void onUserLoggedIn(MJeliotController mJeliotController, User user,
			Lecture lecture) {
	}
	private void addUser(CodingTaskUserCode codingTaskUserCode) {
		if (!codingTaskUserCode.getUser().equals( this.mJeliotController.getUser())) {
			new UserButton(this, codingTaskUserCode);
			this.repaint();
		}
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
	public void onCodingTaskUserCodeAdded(CodingTask codingTask,
			CodingTaskUserCode userCode) {
		System.out.println("adding button for user " + userCode.getUser());
		this.addUser(userCode);
	}

	@Override
	public void onCodingTask(MJeliotController mJeliotController,
			CodingTask codingTask) {
		this.panel.removeAll();
		codingTask.addCodingTaskListener(this);
		// TODO enable
		for (User user : codingTask.getLecture().getUsers()) {
			CodingTaskUserCode userCode = codingTask.getUserCodeTask(user);
			this.addUser(userCode);
		}
	}

	@Override
	public void onCodingTaskEnded(CodingTask codingTask) {
		// TODO disable
		//this.panel.removeAll();
	}
	
	public void addToPanel(UserButton userButton) {
		this.panel.add(userButton);
		panel.repaint();
	}

	public void removeFromPanel(UserButton userButton) {
		this.panel.remove(userButton);
		this.repaint();
	}
	@Override
	public void repaint() {
		if (panel != null) {
			panel.repaint();
		}
		super.repaint();
	}

	@Override
	public void onUserCodeChanged(CodingTask codingTask,
			CodingTaskUserCode usercode) {
	}
}
