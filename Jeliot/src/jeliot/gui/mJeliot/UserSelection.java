package jeliot.gui.mJeliot;

import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.mJeliot.model.Lecture;
import org.mJeliot.model.User;
import org.mJeliot.model.coding.CodingTask;
import org.mJeliot.model.coding.CodingTaskListener;
import org.mJeliot.model.coding.CodingTaskUserCode;
import org.mJeliot.model.predict.Method;

import jeliot.gui.JeliotWindow;
import jeliot.mJeliot.MJeliotController;
import jeliot.mJeliot.MJeliotControllerListener;

public class UserSelection extends JScrollPane implements MJeliotControllerListener, CodingTaskListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6911785838601141857L;
	private JPanel panel;
	private final MJeliotController mJeliotController;
	private final JeliotWindow gui;

	public UserSelection(MJeliotController mJeliotController, JeliotWindow gui) {
		super();
		this.mJeliotController = mJeliotController;
		this.gui = gui;
		this.setAlignmentY(LEFT_ALIGNMENT);
		this.setAlignmentX(TOP_ALIGNMENT);
		//this.setHorizontalScrollBarPolicy(HORIZONTAL_SCROLLBAR_NEVER);
		panel = new JPanel();
		panel.setLayout(new FlowLayout(FlowLayout.LEFT));
		//this.panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
		this.panel.setAlignmentY(LEFT_ALIGNMENT);
		this.panel.setAlignmentX(TOP_ALIGNMENT);
		this.panel.setPreferredSize(getSize());
		mJeliotController.addMJeliotControllerListener(this);
		this.setViewportView(panel);
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
		for (User user : codingTask.getLecture().getUsers()) {
			CodingTaskUserCode userCode = codingTask.getUserCodeTask(user);
			this.addUser(userCode);
		}
		gui.setUserSelectionEnabled(true);
		gui.bringUserSelectionToForeground();
	}

	@Override
	public void onCodingTaskEnded(CodingTask codingTask) {
		gui.setUserSelectionEnabled(false);
		gui.bringTheaterToForeground();
	}
	
	public void addToPanel(UserButton userButton) {
		System.out.println("Added button to panel");
		this.panel.add(userButton);
		repaint();
	}

	public void removeFromPanel(UserButton userButton) {
		this.panel.remove(userButton);
		repaint();
	}
	@Override
	public void repaint() {
		this.validate();
		if (panel != null) {
			panel.validate();
			panel.repaint();
		}
		super.repaint();
	}

	@Override
	public void onUserCodeChanged(CodingTask codingTask,
			CodingTaskUserCode usercode) {
	}
	@Override
	public void setPreferredSize(Dimension d) {
		super.setPreferredSize(d);
		panel.setPreferredSize(d);
	}
}
