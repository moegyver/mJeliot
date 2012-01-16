package jeliot.gui.ict;

import ict.model.Lecture;
import ict.model.predict.Method;
import ict.model.predict.Parameter;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import jeliot.Jeliot;
import jeliot.ict.ICTController;
import jeliot.ict.ICTControllerListener;

/**
 * @author Moritz Rogalli
 * The InteractMenu presents the user with a list of methods that can be predicted at the 
 * moment. When clicked the controller is informed. 
 */
public class InteractMenu extends JPopupMenu implements ICTControllerListener {
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
	private ICTController controller = null;
	
	private LectureMenu lectureMenu = null;

	/**
	 * Builds an InteractMenu. The menu registers itself to the ICTController. No other
	 * methods have to be called to make it work, everything is managed by the event-
	 * handlers. 
	 * @param jeliot The main jeliot class
	 */
	public InteractMenu(final Jeliot jeliot) {
		super();
		this.controller = jeliot.getIctController();
		this.controller.addICTControllerListener(this);
		this.lectureMenu = new LectureMenu("Available Lectures", controller);
		this.add(lectureMenu);
		
		JMenuItem disconnectItem = new JMenuItem("Disconnect");
    	disconnectItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
				jeliot.getIctController().disconnectClient();
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
	public void onUserCountChanged(ICTController ictController) {
	}

	@Override
	public void onAnswerCountChanged(ICTController ictController) {
	}

	@Override
	public void onNewMethod(ICTController ictController, Method method) {
	}

	@Override
	public void onResultPosted(ICTController ictController, Method method) {
	}

	@Override
	public void onMethodCalled(ICTController ictController, Method method) {
		this.addMethod(method);
	}

	@Override
	public void onMethodReturned(ICTController ictController, Method method) {
		this.removeMethod(method);
	}

	@Override
	public void onClientConnected(ICTController ictController) {
	}

	@Override
	public void onClientDisconnected(ICTController ictController) {
	}

	@Override
	public void onNewLecture(ICTController ictController, Lecture lecture) {
		this.lectureMenu.addLecture(lecture);
	}

	@Override
	public void onLogin(ICTController ictController, Lecture lecture) {
	}

	@Override
	public void onLoggedIn(ICTController ictController, Lecture currentLecture) {
		this.lectureMenu.onLoggedIn();
	}

	@Override
	public void onLogout(ICTController ictController, Lecture lecture) {
	}

	@Override
	public void onLectureUpdated(ICTController ictController, Lecture lecture) {
		this.lectureMenu.updateLecture(lecture);
	}

	@Override
	public void onLoggedOut(ICTController ictController, Lecture lecture) {
		this.remove(0);
		this.lectureMenu = new LectureMenu("Available lectures", ictController);
		this.add(lectureMenu, 0);
	}
}
