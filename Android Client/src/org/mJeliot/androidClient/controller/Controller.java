package org.mJeliot.androidClient.controller;

import java.util.Collection;
import java.util.HashMap;
import java.util.Vector;

import org.mJeliot.model.Lecture;
import org.mJeliot.model.User;
import org.mJeliot.model.predict.Method;
import org.mJeliot.protocol.ParserCaller;
import org.mJeliot.protocol.ProtocolParser;
import org.mJeliot.protocol.ProtocolParserListener;

import android.app.Activity;
import android.app.Application;
import android.content.IntentFilter;
import android.net.ConnectivityManager;

/**
 * The Controller is the main application and is in charge of:
 * - keeping the connection to the server through the client
 * - keeping track of all the data related to the current assignment
 * - making sure that there is only one current assignment at all times
 * - informing the running activities on changes 
 * @author Moritz Rogalli
 *
 */
/**
 * @author Moritz Rogalli
 * 
 */
public class Controller extends Application implements AndroidClientListener,
		ProtocolParserListener, ParserCaller {
	// state-variables
	private Lecture lecture = null;
	private User user = null;
	private AndroidClient client = null;
	private Activity currentActivity = null;
	private Vector<ControllerListener> listeners = new Vector<ControllerListener>();
	// parser
	private ProtocolParser parser = new ProtocolParser();
	private HashMap<Integer, Lecture> availableLectures = new HashMap<Integer, Lecture>();

	public Controller() {
		parser.addProtocolParserListener(this);
	}

	/**
	 * Adds a ControllerListener to the Controller, however only if the listener
	 * is not already in the list of listeners.
	 * 
	 * @param controllerListener
	 *            the listener to add
	 */
	public void addControllerListener(ControllerListener controllerListener) {
		synchronized (this.listeners) {
			if (!this.listeners.contains(controllerListener)) {
				this.listeners.add(controllerListener);
			}
		}
	}

	/**
	 * Removes a listener from the list of ControllerListeners.
	 * 
	 * @param controllerListener
	 *            the controllerListener to remove
	 */
	public void removeControllerListener(ControllerListener controllerListener) {
		synchronized (this.listeners) {
			this.listeners.remove(controllerListener);
		}
	}

	/**
	 * @return the currentActivity
	 */
	public Activity getCurrentActivity() {
		return currentActivity;
	}

	/**
	 * @param currentActivity
	 *            the currentActivity to set
	 */
	public void setCurrentActivity(Activity currentActivity) {
		this.currentActivity = currentActivity;
		this.fireOnCurrentActivityChanged(currentActivity);
	}

	/**
	 * Gets the current method.
	 * 
	 * @return the current method to be solved.
	 */
	public Method getCurrentMethod() {
		return this.lecture.getMethod();
	}

	/**
	 * Sets a new assignment for a method and informs the listeners of the
	 * controller. This function however does not set any kind of values.
	 * 
	 * @param className
	 *            the name of the class of the method
	 * @param methodName
	 *            the name of the method
	 * @param methodId
	 *            the id of the method to identify it when handing in results
	 * @param parameterNames
	 *            the names of the parameters
	 */
	public void setNewMethod(String className, String methodName, int methodId,
			String[] parameterNames) {
		Method currentMethod = new Method(className, methodName, methodId);
		this.lecture.setMethod(currentMethod);
		synchronized (currentMethod) {
			for (String parameterName : parameterNames) {
				currentMethod.addParameter(parameterName);
			}
			this.fireOnNewMethod();
		}
	}

	/**
	 * Whether or not there is a method to be solved.
	 * 
	 * @return true if there is a method to be solved, false otherwise
	 */
	public boolean hasCurrentMethod() {
		return this.lecture.getMethod() != null;
	}

	/**
	 * Connects the client to the server. The function does however not log a
	 * user in.
	 */
	public void connect(String url) {
		if (this.client == null) {
			System.out.println("mJeliot Controller: connect");
			this.fireOnConnect();
			this.client = new AndroidClient(this, url);
			Thread clientThread = new Thread(this.client);
			clientThread.start();
			registerReceiver(this.client, new IntentFilter(
					ConnectivityManager.CONNECTIVITY_ACTION));
			this.client.connect();
		}
	}

	/**
	 * @return wether or not the client is connected to the server
	 */
	public boolean isConnected() {
		return this.client != null && this.client.isConnected();
	}

	/**
	 * Calls the Android-API and gets all available wireless networks.
	 */
	public void scanForNetworks() {
		this.client.sendMessage(parser.generateLectureQuery());
		this.fireOnScanStart();
	}

	/**
	 * Generates the handin-message and sends it to the server.
	 */
	public void handInPrediction() {
		this.client.sendMessage(this.parser.generatePredictHandIn(this.user,
				this.lecture));
	}

	/**
	 * Logs a user in.
	 * 
	 * @param userName
	 *            the user's alias
	 * @param lecture
	 *            the lecture to log into
	 */
	public void login(String userName, Lecture lecture) {
		this.user = new User(userName);
		this.lecture = lecture;
		this.client.sendMessage(this.parser.generateUserLogin(this.user,
				lecture.getId()));
		this.fireOnLogin();
	}

	/**
	 * Wether or not the user is logged in to the server.
	 * 
	 * @return
	 */
	public boolean isLoggedIn() {
		return this.user != null && this.lecture != null
				&& this.client.isConnected();
	}

	/**
	 * Logs out the user
	 */
	public void logout() {
		if (this.isLoggedIn()) {
			this.client.sendMessage(this.parser.generateUserLogout(this.user,
					this.lecture));
		}
		// this.user = null;
		// this.lecture = null;
		this.fireOnLogout();
	}

	public void disconnect() {
		System.out.println("mJeliot Controller: disconnect");
		this.client.disconnect();
	}

	public User getUser() {
		return this.user;
	}

	/**
	 * @return the available lectures
	 */
	public Collection<Lecture> getAvailableLectures() {
		return this.availableLectures.values();
	}

	@Override
	public void sendMessage(String message) {
		client.sendMessage(message);
	}

	@Override
	public void onClientConnected(AndroidClient client, boolean reconnected) {
		if (!reconnected) {
			this.fireOnConnected();
			this.scanForNetworks();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.mJeliot.androidClient.tcp.ClientListener#onMessageReceived(org.mJeliot
	 * .androidClient.tcp.Client, java.lang.String)
	 */
	@Override
	public void onMessageReceived(AndroidClient client, String message) {
		parser.parseMessage(message, this);
	}

	@Override
	public void onLectureQuery(ProtocolParser protocolParser,
			ParserCaller parserCaller) {
	}

	@Override
	public void onNewLecture(ProtocolParser protocolParser,
			ParserCaller parserCaller, int lectureId, String lectureName) {
		Lecture lecture = new Lecture(lectureId, lectureName);
		this.availableLectures.put(lectureId, lecture);
		this.fireOnNewLecture(lecture);
	}

	@Override
	public void onLectureList(ProtocolParser protocolParser,
			ParserCaller parserCaller, int lectureCount, int[] lectureIds,
			String[] lectureNames) {
		for (int i = 0; i < lectureCount; i++) {
			this.availableLectures.put(lectureIds[i], new Lecture(
					lectureIds[i], lectureNames[i]));
		}
		this.fireOnScanFinished();
	}

	@Override
	public void onLogin(ProtocolParser protocolParser,
			ParserCaller parserCaller, int lectureId, String userName,
			int userId) {
	}

	@Override
	public void onLoggedIn(ProtocolParser protocolParser,
			ParserCaller parserCaller, int lectureId, String userName,
			int userId) {
		if (this.user != null && this.user.getId() == userId
				&& this.lecture.getId() == lectureId) {
			this.fireOnLoggedIn();
		}
	}

	@Override
	public void onUserList(ProtocolParser protocolParser,
			ParserCaller parserCaller, int lectureId, int userCount,
			int[] userIds, String[] userNames) {
	}

	@Override
	public void onNewPredictMethod(ProtocolParser protocolParser,
			ParserCaller parserCaller, int lectureId, String className,
			String methodName, int methodId, int parameterCount,
			String[] parameterNames) {
		if (this.lecture.getId() == lectureId) {
			this.setNewMethod(className, methodName, methodId, parameterNames);
		}
	}

	@Override
	public void onUserHandedInMethod(ProtocolParser protocolParser,
			ParserCaller parserCaller, int lectureId, int userId, int methodId,
			int parameterCount, String[] parameterNames,
			String[] predictedValues) {
		// won't happen
	}

	@Override
	public void onPredictResult(ProtocolParser protocolParser,
			ParserCaller parserCaller, int lectureId, int methodId,
			int parameterCount, String[] parameterNames,
			String[] parameterValues) {
		if (this.lecture.getId() == lectureId) {
			Method currentMethod = this.lecture.getMethod();
			synchronized (currentMethod) {
				if (currentMethod != null && currentMethod.getId() == methodId) {
					for (int i = 0; i < parameterCount; i++) {
						currentMethod.getParameterByName(parameterNames[i])
								.setActualValue(parameterValues[i]);
					}
					this.fireOnResult();
				}
			}
		}
	}

	@Override
	public void onUserLogout(ProtocolParser protocolParser,
			ParserCaller parserCaller, int lectureId, int userId) {
		// we already know that we want to log out and we don't care about
		// others
	}

	@Override
	public void onUserLoggedOut(ProtocolParser protocolParser,
			ParserCaller parserCaller, int lectureId, int userId) {
		if (this.user.getId() == userId && this.lecture.getId() == lectureId) {
			this.fireOnLoggedOut();
		}
		this.user = null;
		this.lecture = null;
	}

	@Override
	public void onClientDisconnected(AndroidClient client) {
		this.fireonDisconnected();
		this.client = null;
	}

	/**
	 * Informs all registered ControllerListeners whenever the current Actitity
	 * changes
	 * 
	 * @param currentActivity
	 *            the new current activity
	 */
	private void fireOnCurrentActivityChanged(Activity currentActivity) {
		synchronized (this.listeners) {
			for (ControllerListener listener : this.listeners) {
				listener.onCurrentActivityChanged(this, currentActivity);
			}
		}
	}

	/**
	 * Informs all the listeners when the controller is connecting to the
	 * server.
	 */
	private void fireOnConnect() {
		synchronized (this.listeners) {
			for (ControllerListener listener : this.listeners) {
				listener.onConnect(this);
			}
		}
	}

	/**
	 * Informs all the listeners when the controller is connected to the server.
	 */
	private void fireOnConnected() {
		synchronized (this.listeners) {
			for (ControllerListener listener : this.listeners) {
				listener.onConnected(this);
			}
		}
	}

	/**
	 * Informs all the listeners when a scan is started.
	 */
	private void fireOnScanStart() {
		synchronized (this.listeners) {
			for (ControllerListener listener : this.listeners) {
				listener.onScanStart(this);
			}
		}
	}

	/**
	 * Informs all the listeners when a scan is finished.
	 */
	private void fireOnScanFinished() {
		synchronized (this.listeners) {
			for (ControllerListener listener : this.listeners) {
				listener.onScanFinished(this);
			}
		}
	}

	/**
	 * Informs listeners when a lecture gets added.
	 * 
	 * @param lecture
	 *            the added lecture
	 */
	private void fireOnNewLecture(Lecture lecture) {
		synchronized (this.listeners) {
			for (ControllerListener listener : this.listeners) {
				listener.onNewLecture(this, lecture);
			}
		}
	}

	private void fireOnLogin() {
		synchronized (this.listeners) {
			for (ControllerListener listener : this.listeners) {
				listener.onLoggingIn(this);
			}
		}
	}

	private void fireOnLoggedIn() {
		synchronized (this.listeners) {
			for (ControllerListener listener : this.listeners) {
				listener.onLoggedIn(this);
			}
		}
	}

	/**
	 * Informs all the listeners when a new assignment is posted.
	 */
	private void fireOnNewMethod() {
		synchronized (this.listeners) {
			for (ControllerListener listener : this.listeners) {
				listener.onNewMethod(this);
			}
		}
	}

	/**
	 * Informs all the listeners when a result gets posted.
	 */
	private void fireOnResult() {
		synchronized (this.listeners) {
			for (ControllerListener listener : this.listeners) {
				listener.onResult(this);
			}
		}
	}

	/**
	 * Informs all the listeners when the controller is logging out
	 */
	private void fireOnLogout() {
		for (int i = 0; i < this.listeners.size(); i++) {
			this.listeners.get(i).onLoggingOut(this);
		}
	}

	/**
	 * Informs all the listeners when the controller is logged out
	 */
	private void fireOnLoggedOut() {
		synchronized (this.listeners) {
			for (ControllerListener listener : this.listeners) {
				listener.onLoggedOut(this);
			}
		}
	}

	/**
	 * Informs all the listeners when the controller got disconnected.
	 */
	private void fireonDisconnected() {
		for (int i = 0; i < this.listeners.size(); i++) {
			this.listeners.get(i).onDisconnected(this);
		}
		this.user = null;
		this.lecture = null;
		this.client = null;
	}

	@Override
	public Lecture getLecture() {
		return this.lecture;
	}

	public void sendCode(CharSequence s, int cursorPosition) {
		this.client.sendMessage(this.parser.generateCodeUpdate(s, cursorPosition, user, lecture));
	}

	@Override
	public void onCodeUpdate(ProtocolParser protocolParser,
			ParserCaller parserCaller, Integer lectureId, Integer userId,
			String code, Integer cursorPosition) {
	}
}