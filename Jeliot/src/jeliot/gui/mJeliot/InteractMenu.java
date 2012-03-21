package jeliot.gui.mJeliot;


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import org.mJeliot.model.Lecture;
import org.mJeliot.model.User;
import org.mJeliot.model.predict.Method;
import org.mJeliot.model.predict.Parameter;

import jeliot.Jeliot;
import jeliot.mJeliot.MJeliotController;
import jeliot.mJeliot.MJeliotControllerListener;

/**
 * @author Moritz Rogalli
 * The InteractMenu presents the user with a list of methods that can be predicted at the 
 * moment. When clicked the controller is informed. 
 */
public class InteractMenu extends JPopupMenu implements MJeliotControllerListener {
	private static final long serialVersionUID = 4156769001504511178L;
	/**
	 * The available methods.
	 */
	private Vector<Method> methods = new Vector<Method>();
	/**
	 * The corresponding menu items.
	 */
	private Vector<JMenuItem> menuItems = new Vector<JMenuItem>();
	/**
	 * The controller to inform when a menu item is clicked.
	 */
	private MJeliotController controller = null;
	
	private LectureMenu lectureMenu = null;

	/**
	 * Builds an InteractMenu. The menu registers itself to the MJeliotController. No other
	 * methods have to be called to make it work, everything is managed by the event-
	 * handlers. 
	 * @param jeliot The main jeliot class
	 */
	public InteractMenu(final Jeliot jeliot) {
		super();
		this.controller = jeliot.getMJeliotController();
		this.controller.addMJeliotControllerListener(this);
		this.lectureMenu = new LectureMenu("Available Lectures", controller);
		this.add(lectureMenu);
		
		JMenuItem disconnectItem = new JMenuItem("Disconnect");
    	disconnectItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
				jeliot.getMJeliotController().disconnectClient();
			}		
    	});
		this.add(disconnectItem);
	}
	
	/**
	 * Adds a method to the list.
	 * @param method the method to add
	 */
	private void addMethod(final Method method) {
		this.methods.add(method);
		String parameters = "";
		for (Parameter p : method.getParameters()) {
			if (!p.getName().equals("return")) {
				parameters += p.getActualValue() + ",";
			}
		}
		parameters = parameters.substring(0, Math.max(parameters.length() - 1, 0));
		JMenuItem sendToAudienceMenuItem = new JMenuItem("Predict " + method.getClassName() + "." + method.getMethodName() + "(" + parameters + ")");
    	sendToAudienceMenuItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
				interact(method);
			}		
    	});
    	this.menuItems.add(sendToAudienceMenuItem);
    	this.add(sendToAudienceMenuItem, this.getComponentCount() - 1);
	}

	/**
	 * Called when a menu item is clicked. Invokes the controller's ability to send a
	 * new assignment to the Server.
	 * @param method the method to predict
	 */
	private void interact(Method method) {
		this.controller.sendMethodToPredict(method);
	}

	/**
	 * Removes a method from the list.
	 * @param method method to remove
	 */
	private void removeMethod(Method method) {
		int index = this.methods.indexOf(method);
		if (index >= 0) {
			this.methods.remove(index);
			JMenuItem menuItem = this.menuItems.get(index);
			this.menuItems.remove(index);
			this.remove(menuItem);
			if (this.isVisible()) {
				repaint();
			}
		}
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
	public void onResultPosted(MJeliotController mJeliotController, Method method) {
	}

	@Override
	public void onMethodCalled(MJeliotController mJeliotController, Method method) {
		this.addMethod(method);
	}

	@Override
	public void onMethodReturned(MJeliotController mJeliotController, Method method) {
		this.removeMethod(method);
	}

	@Override
	public void onClientConnected(MJeliotController mJeliotController, boolean isReconnected) {
	}

	@Override
	public void onClientDisconnected(MJeliotController mJeliotController) {
	}

	@Override
	public void onNewLecture(MJeliotController mJeliotController, Lecture lecture) {
		this.lectureMenu.addLecture(lecture);
	}

	@Override
	public void onLogin(MJeliotController mJeliotController, Lecture lecture) {
	}

	@Override
	public void onLoggedIn(MJeliotController mJeliotController, Lecture currentLecture) {
		this.lectureMenu.onLoggedIn();
	}

	@Override
	public void onLogout(MJeliotController mJeliotController, Lecture lecture) {
	}

	@Override
	public void onLectureUpdated(MJeliotController mJeliotController, Lecture lecture) {
		this.lectureMenu.updateLecture(lecture);
	}

	@Override
	public void onLoggedOut(MJeliotController mJeliotController, Lecture lecture) {
		this.remove(0);
		this.lectureMenu = new LectureMenu("Available lectures", mJeliotController);
		this.add(lectureMenu, 0);
	}

	@Override
	public void onUserLoggedIn(MJeliotController mJeliotController, User user,
			Lecture lecture) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onUserLoggedOut(MJeliotController mJeliotController, User user,
			Lecture lecture) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onCodeUpdate(Lecture lecture, User user, String code,
			int cursorPosition, boolean isDone, boolean requestedAttention) {
		// TODO Auto-generated method stub
		
	}
}
