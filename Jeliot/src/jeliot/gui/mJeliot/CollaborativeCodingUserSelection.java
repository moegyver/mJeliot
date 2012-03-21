package jeliot.gui.mJeliot;

import java.util.HashMap;

import javax.swing.BoxLayout;
import javax.swing.JPanel;

import org.mJeliot.model.Lecture;
import org.mJeliot.model.User;
import org.mJeliot.model.predict.Method;

import jeliot.mJeliot.MJeliotController;
import jeliot.mJeliot.MJeliotControllerListener;

public class CollaborativeCodingUserSelection extends JPanel implements MJeliotControllerListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6911785838601141857L;
	private final MJeliotController mJeliotController;
	private JPanel panel;
	private HashMap<Integer, UserButton> buttons = new HashMap<Integer, UserButton>();

	public CollaborativeCodingUserSelection(MJeliotController mJeliotController) {
		super();
		panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
		panel.setAlignmentY(LEFT_ALIGNMENT);
		this.mJeliotController = mJeliotController;
		mJeliotController.addMJeliotControllerListener(this);
		this.build();
		this.add(panel);
	}

	private void build() {
		if (mJeliotController.getLecture() != null) {
			for (User user : mJeliotController.getLecture().getUsers()) {
				if (!user.equals(mJeliotController.getUser())) {
					this.addUser(user);
				}
			}
		}
	}
	
	@Override
	public void onClientConnected(MJeliotController mJeliotController,
			boolean isReconnected) {
	}

	@Override
	public void onClientDisconnected(MJeliotController mJeliotController) {
		this.reset();
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
		this.reset();
		this.disableUserSelection();
	}

	private void reset() {
		this.removeAll();
		this.buttons = new HashMap<Integer, UserButton>();
		this.repaint();
	}

	private void disableUserSelection() {
		// TODO disable the tab when not logged in
		
	}

	@Override
	public void onUserLoggedIn(MJeliotController mJeliotController, User user,
			Lecture lecture) {
		this.addUser(user);
	}

	private void addUser(User user) {
		if (!user.equals(mJeliotController.getUser()) && !this.buttons.containsKey(user.getId())) {
			System.out.println("mJeliotUserList: added user + " + user);
			UserButton userbutton = new UserButton(user);
			this.add(userbutton);
			this.buttons.put(user.getId(), userbutton);
			this.validate();
			this.repaint();
		}
	}
	
	private void removeUser(User user) {
		UserButton userbutton = this.buttons.remove(user.getId());
		if (userbutton != null) {
			System.out.println("mJeliotUserList: removed user + " + user);
			this.remove(userbutton);
			this.validate();
			this.repaint();
		}
	}

	@Override
	public void onUserLoggedOut(MJeliotController mJeliotController, User user,
			Lecture lecture) {
		this.removeUser(user);
	}

	@Override
	public void onCodeUpdate(Lecture lecture, User user, String code,
			int cursorPosition, boolean isDone, boolean requestedAttention) {
		if (this.buttons.get(user.getId()) != null) {
			UserButton button = this.buttons.get(user.getId());
			boolean hasCoded = !code.equals(this.mJeliotController.getOriginalCode());
			button.updateButton(hasCoded, isDone, requestedAttention);
		}
	}
	
}
