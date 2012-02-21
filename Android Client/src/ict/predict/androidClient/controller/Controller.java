package ict.predict.androidClient.controller;

import java.util.Collection;
import java.util.HashMap;
import java.util.Vector;

import android.app.Activity;
import android.app.Application;
import ict.client.Client;
import ict.client.ClientListener;
import ict.model.Lecture;
import ict.model.User;
import ict.model.predict.Method;
import ict.protocol.ParserCaller;
import ict.protocol.ProtocolParser;
import ict.protocol.ProtocolParserListener;

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
public class Controller extends Application implements ClientListener,
	ProtocolParserListener, ParserCaller {
	// state-variables
	private Lecture lecture = null;
	private User user = null;
	private Client client = null;
	private Activity currentActivity = null;
	private Vector<ControllerListener> listeners = new Vector<ControllerListener>();
	// parser
	private ProtocolParser parser = new ProtocolParser();
	private HashMap<Integer, Lecture> availableLectures = new HashMap<Integer, Lecture>();
	
	public Controller () {
		parser.addProtocolParserListener(this);
		//this.addControllerListener(new ToastGenerator(this));
	}
	/**
	 * Adds a ControllerListener to the Controller, however only if the listener is not already in the list of listeners.
	 * @param controllerListener the listener to add
	 */
	public void addControllerListener(ControllerListener controllerListener) {
		synchronized(this.listeners) {
			if (!this.listeners.contains(controllerListener)) {
				this.listeners.add(controllerListener);
			}
		}
	}
	/**
	 * Removes a listener from the list of ControllerListeners.
	 * @param controllerListener the controllerListener to remove
	 */
	public void removeControllerListener(ControllerListener controllerListener) {
		synchronized(this.listeners) {
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
	 * @param currentActivity the currentActivity to set
	 */
	public void setCurrentActivity(Activity currentActivity) {
		this.currentActivity = currentActivity;
		this.fireOnCurrentActivityChanged(currentActivity);
	}
	/**
	 * Gets the current method.
	 * @return the current method to be solved.
	 */
	public Method getCurrentMethod() {
		return this.lecture.getMethod();
	}

	/**
	 * Sets a new assignment for a method and informs the listeners of the controller.
	 * This function however does not set any kind of values.
	 * @param className the name of the class of the method
	 * @param methodName the name of the method
	 * @param methodId the id of the method to identify it when handing in results
	 * @param parameterNames the names of the parameters
	 */
	public void setNewMethod(String className, String methodName, int methodId,
			String[] parameterNames) {
		Method currentMethod = new Method(className, methodName, methodId);
		this.lecture.setMethod(currentMethod);
		synchronized (currentMethod) {
			for(String parameterName : parameterNames) {
				currentMethod.addParameter(parameterName);
			}
			this.fireOnNewMethod();
		}
	}
	/**
	 * Whether or not there is a method to be solved.
	 * @return true if there is a method to be solved, false otherwise
	 */
	public boolean hasCurrentMethod() {
		return this.lecture.getMethod() != null;
	}
	
	/**
	 * Connects the client to the server. The function does however not log a user in.
	 */
	public void connect(String url) {
		this.fireOnConnect();
		this.client = new Client(url);
		this.client.addClientListener(this);
		Thread clientThread = new Thread(this.client);
		clientThread.start();
		this.client.connect();
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
		this.client.sendMessage(this.parser.generatePredictHandIn(this.user, this.lecture));
	}
	
	/**
	 * Logs a user in.
	 * @param userName the user's alias
	 * @param lecture the lecture to log into
	 */
	public void login(String userName, Lecture lecture) {
		this.user = new User(userName);
		this.lecture = lecture;
		this.client.sendMessage(this.parser.generateUserLogin(this.user, lecture.getId()));
		this.fireOnLogin();
	}
	/**
	 * Wether or not the user is logged in to the server.
	 * @return
	 */
	public boolean isLoggedIn() {
		return this.user != null && this.lecture != null && this.client.isConnected();
	}
	
	/**
	 * Logs out the user
	 */
	public void logout() {
		if (this.isLoggedIn()) {
			this.client.sendMessage(this.parser.generateUserLogout(this.user, this.lecture));
		}
		//this.user = null;
		//this.lecture = null;
		this.fireOnLogout();
	}
	
	public void disconnect() {
		this.client.disconnect();
	}
	/* (non-Javadoc)
	 * @see ict.protocol.ParserCaller#getUser()
	 */
	public User getUser() {
		return this.user;
	}
	/* (non-Javadoc)
	 * @see ict.protocol.ParserCaller#setUser(ict.model.User)
	 */
	@Override
	public void setUser(User user) {
		this.user = user;
	}
	/**
	 * @return the available lectures
	 */
	public Collection<Lecture> getAvailableLectures() {
		return this.availableLectures.values();
	}
	/* (non-Javadoc)
	 * @see ict.protocol.ParserCaller#sendMessage(java.lang.String)
	 */
	@Override
	public void sendMessage(String message) {
		client.sendMessage(message);
	}
	/* (non-Javadoc)
	 * @see ict.client.ClientListener#onClientConnected(ict.client.Client)
	 */
	@Override
	public void onClientConnected(Client client) {
		this.fireOnConnected();
		this.scanForNetworks();
	}
	/* (non-Javadoc)
	 * @see ict.client.ClientListener#onMessageReceived(ict.client.Client, java.lang.String)
	 */
	@Override
	public void onMessageReceived(Client client, String message) {
		parser.parseMessage(message, this);
	}
	
	/* (non-Javadoc)
	 * @see ict.protocol.ProtocolParserListener#onLectureQuery(ict.protocol.ProtocolParser, ict.protocol.ParserCaller)
	 */
	@Override
	public void onLectureQuery(ProtocolParser protocolParser,
			ParserCaller parserCaller) {
	}
	
	/* (non-Javadoc)
	 * @see ict.protocol.ProtocolParserListener#onNewLecture(ict.protocol.ProtocolParser, ict.protocol.ParserCaller, int, java.lang.String)
	 */
	@Override
	public void onNewLecture(ProtocolParser protocolParser,
			ParserCaller parserCaller, int lectureId, String lectureName) {
		Lecture lecture = new Lecture(lectureId, lectureName);
		this.availableLectures.put(lectureId, lecture);
		this.fireOnNewLecture(lecture);
	}
	
	/* (non-Javadoc)
	 * @see ict.protocol.ProtocolParserListener#onLectureList(ict.protocol.ProtocolParser, ict.protocol.ParserCaller, int, int[], java.lang.String[])
	 */
	@Override
	public void onLectureList(ProtocolParser protocolParser,
			ParserCaller parserCaller, int lectureCount, int[] lectureIds,
			String[] lectureNames) {
		for (int i = 0; i < lectureCount; i++) {
			this.availableLectures.put(lectureIds[i], new Lecture(lectureIds[i], lectureNames[i]));
		}
		this.fireOnScanFinished();
	}
	/* (non-Javadoc)
	 * @see ict.protocol.ProtocolParserListener#onUserLogin(ict.protocol.ProtocolParser, ict.protocol.ParserCaller, java.lang.String, int)
	 */
	@Override
	public void onLogin(ProtocolParser protocolParser, ParserCaller parserCaller,
			int lectureId, String userName,	int userId) {
	}
	/* (non-Javadoc)
	 * @see ict.protocol.ProtocolParserListener#onUserLoggedIn(ict.protocol.ProtocolParser, ict.protocol.ParserCaller, java.lang.String, int)
	 */
	@Override
	public void onLoggedIn(ProtocolParser protocolParser, ParserCaller parserCaller,
			int lectureId, String userName,	int userId) {
		if (this.user != null && this.user.getId() == userId && this.lecture.getId() == lectureId) {
			this.fireOnLoggedIn();
		}
	}
	/* (non-Javadoc)
	 * @see ict.protocol.ProtocolParserListener#onUserList(ict.protocol.ProtocolParser, ict.protocol.ParserCaller, int, int, int[], java.lang.String[])
	 */
	@Override
	public void onUserList(ProtocolParser protocolParser,
			ParserCaller parserCaller, int lectureId, int userCount, int[] userIds,
			String[] userNames) {
	}
	/* (non-Javadoc)
	 * @see ict.protocol.ProtocolParserListener#onNewPredictMethod(ict.protocol.ProtocolParser, ict.protocol.ParserCaller, java.lang.String, java.lang.String, int, int, java.lang.String[])
	 */
	@Override
	public void onNewPredictMethod(ProtocolParser protocolParser, 
			ParserCaller parserCaller, int lectureId, String className, String methodName, 
			int methodId, int parameterCount,	String[] parameterNames) {
		if (this.lecture.getId() == lectureId) {
			this.setNewMethod(className, methodName, methodId, parameterNames);
		}
	}
	/* (non-Javadoc)
	 * @see ict.protocol.ProtocolParserListener#onUserHandedInMethod(ict.protocol.ProtocolParser, ict.protocol.ParserCaller, int, int, int, java.lang.String[], java.lang.String[])
	 */
	@Override
	public void onUserHandedInMethod(ProtocolParser protocolParser,
			ParserCaller parserCaller, int lectureId, int userId, int methodId,
			int parameterCount, String[] parameterNames, String[] predictedValues) {
		// won't happen
	}
	/* (non-Javadoc)
	 * @see ict.protocol.ProtocolParserListener#onMasterSentOutPredictResult(ict.protocol.ProtocolParser, ict.protocol.ParserCaller, int, int, java.lang.String[], java.lang.String[])
	 */
	@Override
	public void onPredictResult(ProtocolParser protocolParser, ParserCaller parserCaller,
			int lectureId, int methodId, int parameterCount, String[] parameterNames,
			String[] parameterValues) {
		if (this.lecture.getId() == lectureId) {
			Method currentMethod = this.lecture.getMethod();
			synchronized (currentMethod) {
				if (currentMethod != null && currentMethod.getId() == methodId) {
					for (int i = 0; i < parameterCount; i++) {
						currentMethod.getParameterByName(parameterNames[i]).setActualValue(parameterValues[i]);
					}
					this.fireOnResult();
				}
			}
		}
	}
	/* (non-Javadoc)
	 * @see ict.protocol.ProtocolParserListener#onUserLogout(ict.protocol.ProtocolParser, ict.protocol.ParserCaller, int)
	 */
	@Override
	public void onUserLogout(ProtocolParser protocolParser, ParserCaller parserCaller,
			int lectureId, int userId) {
		// we already know that we want to log out and we don't care about others
	}
	/* (non-Javadoc)
	 * @see ict.protocol.ProtocolParserListener#onUserLoggedOut(ict.protocol.ProtocolParser, ict.protocol.ParserCaller, int)
	 */
	@Override
	public void onUserLoggedOut(ProtocolParser protocolParser, ParserCaller parserCaller,
			int lectureId, int userId) {
		System.out.println("params: " + lectureId + " " + userId + " objs: " + this.user + " " + this.lecture);
		if (this.user.getId() == userId && this.lecture.getId() == lectureId) {
			this.fireOnLoggedOut();
		}
		this.user = null;
		this.lecture = null;
	}
	/* (non-Javadoc)
	 * @see ict.client.ClientListener#onClientDisconnected(ict.client.Client)
	 */
	@Override
	public void onClientDisconnected(Client client) {
		this.fireonDisconnected();
	}
	/**
	 * Informs all registered ControllerListeners whenever the current Actitity changes
	 * @param currentActivity the new current activity
	 */
	private void fireOnCurrentActivityChanged(Activity currentActivity) {
		synchronized(this.listeners) {
			for (ControllerListener listener : this.listeners) {
				listener.onCurrentActivityChanged(this, currentActivity);
			}
		}
	}
	/**
	 * Informs all the listeners when the controller is connecting to the server.
	 */
	private void fireOnConnect() {
		synchronized (this.listeners) {
			for(ControllerListener listener: this.listeners) {
				listener.onConnect(this);	
			}
		}
	}
	/**
	 * Informs all the listeners when the controller is connected to the server.
	 */
	private void fireOnConnected() {
		synchronized (this.listeners) {
			for(ControllerListener listener: this.listeners) {
				listener.onConnected(this);	
			}
		}
	}
	/**
	 * Informs all the listeners when a scan is started.
	 */
	private void fireOnScanStart() {
		synchronized (this.listeners) {
			for(ControllerListener listener: this.listeners) {
				listener.onScanStart(this);
			}
		}
	}
	/**
	 * Informs all the listeners when a scan is finished.
	 */
	private void fireOnScanFinished() {
		synchronized (this.listeners) {
			for(ControllerListener listener: this.listeners) {
				listener.onScanFinished(this);	
			}
		}
	}
	/**
	 * Informs listeners when a lecture gets added.
	 * @param lecture the added lecture
	 */
	private void fireOnNewLecture(Lecture lecture) {
		synchronized (this.listeners) {
			for(ControllerListener listener: this.listeners) {
				listener.onNewLecture(this, lecture);	
			}
		}
	}
	private void fireOnLogin() {
		synchronized (this.listeners) {
			for(ControllerListener listener: this.listeners) {
				listener.onLoggingIn(this);	
			}
		}
	}
	private void fireOnLoggedIn() {
		synchronized (this.listeners) {
			for(ControllerListener listener: this.listeners) {
				listener.onLoggedIn(this);	
			}
		}
	}
	/**
	 * Informs all the listeners when a new assignment is posted.
	 */
	private void fireOnNewMethod() {
		synchronized (this.listeners) {
			for(ControllerListener listener: this.listeners) {
				listener.onNewMethod(this);
			}
		}
	}
	/**
	 * Informs all the listeners when a result gets posted.
	 */
	private void fireOnResult() {
		synchronized(this.listeners) {
			for(ControllerListener listener: this.listeners) {
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
		synchronized(this.listeners) {
			for(ControllerListener listener: this.listeners) {
				listener.onLoggedOut(this);	
			}
		}	
	}
	/**
	 * Informs all the listeners when the controller got disconnected.
	 */
	private void fireonDisconnected() {
		for(int i = 0; i < this.listeners.size(); i++) {
			this.listeners.get(i).onDisconnected(this);	
		}
		this.user = null;
		this.lecture = null;
		this.client = null;
	}
}