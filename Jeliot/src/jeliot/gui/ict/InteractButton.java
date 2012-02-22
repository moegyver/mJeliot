package jeliot.gui.ict;

import java.awt.Insets;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.net.URL;
import java.util.ResourceBundle;


import javax.swing.AbstractButton;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JOptionPane;

import org.mJeliot.model.Lecture;
import org.mJeliot.model.predict.Method;

import jeliot.Jeliot;
import jeliot.ict.ICTController;
import jeliot.ict.ICTControllerListener;
import jeliot.tracker.Tracker;
import jeliot.tracker.TrackerClock;
import jeliot.util.UserProperties;

/**
 * @author Moritz Rogalli
 * The InteractButton is a standard JButton that looks like the buttons built by the 
 * JeliotWindow class. However, it does implement a ClientListener to react on connect
 * and disconnect events from the Client that connects Jeliot to the ICT system.
 */
public class InteractButton extends JButton implements ICTControllerListener {
	private static final long serialVersionUID = -542245276585071664L;
	private final ResourceBundle messageBundle;
	private final ImageIcon connectIcon;
	private final ImageIcon interactIcon;
	private InteractMenu menu;
	private MouseListener mouseListener = null;
	private final ICTController controller;
	private Jeliot jeliot;

	/**
	 * Instantiates a new InteractButton. The InteractButton takes care of registering
	 * itself as a listener and reacting on events. There should be no need to call any
	 * other method except for reset(). It also builds the InteractMenu.
	 * @param jeliot The main jeliot object
	 * @param propertiesBundle The properties
	 * @param messageBundle The localised strings
	 */
	public InteractButton(Jeliot jeliot, UserProperties propertiesBundle, ResourceBundle messageBundle) {
		super();
		this.jeliot = jeliot;
		this.controller = jeliot.getIctController();
		this.buildMenu();
		this.messageBundle = messageBundle;
        URL imageURLConnect = this.getClass().getClassLoader().getResource(
        		propertiesBundle.getStringProperty("directory.images") + 
        		propertiesBundle.getStringProperty("image.connect_icon"));
        if (imageURLConnect == null) {
            imageURLConnect = Thread.currentThread().getContextClassLoader()
                    .getResource(
                    		propertiesBundle.getStringProperty("directory.images") + 
                    		propertiesBundle.getStringProperty("image.connect_icon"));
        }
        this.connectIcon = new ImageIcon(imageURLConnect);
        URL imageURLInteract = this.getClass().getClassLoader().getResource(
        		propertiesBundle.getStringProperty("directory.images") + 
        		propertiesBundle.getStringProperty("image.interact_icon"));
        if (imageURLInteract == null) {
        	imageURLInteract = Thread.currentThread().getContextClassLoader()
                    .getResource(
                    		propertiesBundle.getStringProperty("directory.images") + 
                    		propertiesBundle.getStringProperty("image.interact_icon"));
        }
        this.interactIcon = new ImageIcon(imageURLInteract);
        this.setText(messageBundle.getString("button.connect"));
        this.setIcon(this.connectIcon);
        this.setVerticalTextPosition(AbstractButton.BOTTOM);
        this.setHorizontalTextPosition(AbstractButton.CENTER);
        this.setMargin(new Insets(0, 0, 0, 0));
        this.setFunctionToConnect();
        this.controller.addICTControllerListener(this);
	}
	
	/**
	 * Sets the button to act as a connect-button.
	 */
	private void setFunctionToConnect() {
		if (this.mouseListener != null) {
			removeMouseListener(this.mouseListener);
		}
		this.mouseListener = new MouseListener() {
			@Override
			public void mouseClicked(MouseEvent e) {
				Tracker.trackEvent(TrackerClock.currentTimeMillis(),
						Tracker.BUTTON, -1, -1, "ConnectButton");
				String url = JOptionPane.showInputDialog("Enter server address:", "");
				controller.connectClient(url);
			}
			@Override
			public void mousePressed(MouseEvent e) {
			}
			@Override
			public void mouseReleased(MouseEvent e) {
			}
			@Override
			public void mouseEntered(MouseEvent e) {
			}

			@Override
			public void mouseExited(MouseEvent e) {
			}
		};
		this.addMouseListener(this.mouseListener);
	}
	
	/**
	 * Sets the button to work as an interact-button.
	 */
	private void setFunctionToInteract() {
		this.removeMouseListener(this.mouseListener);
		this.mouseListener = new MouseListener() {
			@Override
			public void mouseClicked(MouseEvent e) {
				Tracker.trackEvent(TrackerClock.currentTimeMillis(),
						Tracker.BUTTON, -1, -1, "InteractButton");
				menu.validate();
				menu.show(e.getComponent(), 0, - menu.getHeight());
			}

			@Override
			public void mousePressed(MouseEvent e) {
			}
			@Override
			public void mouseReleased(MouseEvent e) {
			}
			@Override
			public void mouseEntered(MouseEvent e) {
			}
			@Override
			public void mouseExited(MouseEvent e) {
			}
		};
		this.addMouseListener(this.mouseListener);
	}

	/* (non-Javadoc)
	 * @see jeliot.ict.ICTControllerListener#onUserCountChanged(jeliot.ict.ICTController)
	 */
	@Override
	public void onUserCountChanged(ICTController ictController) {
	}

	/* (non-Javadoc)
	 * @see jeliot.ict.ICTControllerListener#onAnswerCountChanged(jeliot.ict.ICTController)
	 */
	@Override
	public void onAnswerCountChanged(ICTController ictController) {
	}

	/* (non-Javadoc)
	 * @see jeliot.ict.ICTControllerListener#onNewMethod(jeliot.ict.ICTController, org.mJeliot.model.predict.Method)
	 */
	@Override
	public void onNewMethod(ICTController ictController, Method method) {
	}

	/* (non-Javadoc)
	 * @see jeliot.ict.ICTControllerListener#onResultPosted(jeliot.ict.ICTController, org.mJeliot.model.predict.Method)
	 */
	@Override
	public void onResultPosted(ICTController ictController, Method method) {
	}

	/* (non-Javadoc)
	 * @see jeliot.ict.ICTControllerListener#onMethodCalled(jeliot.ict.ICTController, org.mJeliot.model.predict.Method)
	 */
	@Override
	public void onMethodCalled(ICTController ictController, Method method) {
	}

	/* (non-Javadoc)
	 * @see jeliot.ict.ICTControllerListener#onMethodReturned(jeliot.ict.ICTController, org.mJeliot.model.predict.Method)
	 */
	@Override
	public void onMethodReturned(ICTController ictController, Method method) {
	}

	/* (non-Javadoc)
	 * @see jeliot.ict.ICTControllerListener#onClientDisconnected(jeliot.ict.ICTController)
	 */
	@Override
	public void onClientDisconnected(ICTController ictController) {
		this.setText(this.messageBundle.getString("button.connect"));
		this.setIcon(this.connectIcon);
		this.setFunctionToConnect();
	}

	/* (non-Javadoc)
	 * @see jeliot.ict.ICTControllerListener#onClientConnected(jeliot.ict.ICTController)
	 */
	@Override
	public void onClientConnected(ICTController ictController) {
		this.setText(this.messageBundle.getString("button.interact"));
		this.setIcon(this.interactIcon);
		reset();
		this.setFunctionToInteract();
	}
	
	/**
	 * Resets the Button and the menu.
	 */
	public void reset() {
		this.menu.setVisible(false);
		// this.controller.removeICTControllerListener(this.menu);
		this.buildMenu();
	}
	
	/**
	 * Builds the menu, called by the constructor and the reset-function.
	 */
	private void buildMenu() {
        this.menu = new InteractMenu(jeliot);
        /*this.menu.addFocusListener(new FocusListener(){
			@Override
			public void focusGained(FocusEvent e) {
			}
			@Override
			public void focusLost(FocusEvent e) {
				menu.setVisible(false);
			}
        });*/
	}

	@Override
	public void onNewLecture(ICTController ictController, Lecture lecture) {
	}

	@Override
	public void onLogin(ICTController ictController, Lecture lecture) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onLoggedIn(ICTController ictController, Lecture currentLecture) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onLogout(ICTController ictController, Lecture lecture) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onLectureUpdated(ICTController ictController, Lecture lecture) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onLoggedOut(ICTController ictController, Lecture lecture) {
		// TODO Auto-generated method stub
		
	}
}
