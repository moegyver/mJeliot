package jeliot.ict;

import java.util.HashMap;
import java.util.Vector;

import org.mJeliot.client.Client;
import org.mJeliot.client.ClientListener;
import org.mJeliot.model.Lecture;
import org.mJeliot.model.User;
import org.mJeliot.model.predict.Method;
import org.mJeliot.model.predict.Parameter;
import org.mJeliot.model.predict.ParameterPrediction;
import org.mJeliot.protocol.ParserCaller;
import org.mJeliot.protocol.ProtocolParser;
import org.mJeliot.protocol.ProtocolParserListener;


/**
 * @author Moritz Rogalli
 * The ICTController keeps track of the state, sends notifications and messages to the
 * server.
 */
public class ICTController implements ClientListener, ProtocolParserListener,
		ParserCaller {
	private Lecture currentLecture = null;
	private HashMap<Integer, Lecture> availableLectures = new HashMap<Integer, Lecture>();
	/**
	 * The client keeps the connection to the Server
	 */
	private Client client = null;
	/**
	 * This user identifies the controller, default name is Jeliot.
	 */
	private User user = new User("Jeliot");
	/**
	 * The parser generates and parses messages.
	 */
	private ProtocolParser parser = new ProtocolParser();
	/**
	 * The listeners to be informed whenever there is something happening.
	 */
	private Vector<ICTControllerListener> listeners = new Vector<ICTControllerListener>();
	private Thread clientThread = null;
	/**
	 * Creates an ICTController and sets up all the necessary structures.
	 */
	public ICTController() {
		this.parser.addProtocolParserListener(this);
		reset();
	}
	
	/**
	 * Adds a listener to the ICTController. If the listener is already registered it is
	 * not added again.
	 * @param listener the listener to add
	 */
	public void addICTControllerListener(ICTControllerListener listener) {
		if (!this.listeners.contains(listener)) {
			this.listeners.add(listener);
		}
	}

	/**
	 * Unregisters a listener from the ICTController.
	 * @param listener The listener to remove
	 */
	public void removeICTControllerListener(ICTControllerListener listener) {
		this.listeners.remove(listener);
	}

	private void reset() {
		this.currentLecture = null;
		this.availableLectures = new HashMap<Integer, Lecture>();
	}
	
	/**
	 * @return the client
	 */
	public Client getClient() {
		return client;
	}

	/**
	 * Connects the controller's client.
	 */
	public void connectClient(String url) {
		this.client = new Client(url);
		this.client.setUser(this.user);
		this.client.addClientListener(this);
		this.clientThread = new Thread(this.client);
		this.clientThread.start();
		this.client.connect();
	}
	
	/* (non-Javadoc)
	 * @see org.mJeliot.client.ClientListener#onClientConnected(org.mJeliot.client.Client)
	 */
	@Override
	public void onClientConnected(Client client) {
		this.client.sendMessage(this.parser.generateLectureQuery());
		this.fireOnClientConnected();
	}

	/**
	 * Notifies all the registered listeners when the client gets connected.
	 */
	private void fireOnClientConnected() {
		synchronized (this.listeners) {
			for (int i = 0; i < this.listeners.size(); i++) {
				this.listeners.get(i).onClientConnected(this);
			}
		}
	}

	/* (non-Javadoc)
	 * @see org.mJeliot.client.ClientListener#onMessageReceived(org.mJeliot.client.Client, java.lang.String)
	 */
	@Override
	public void onMessageReceived(Client client, String message) {
		parser.parseMessage(message, this);
	}

	/* (non-Javadoc)
	 * @see org.mJeliot.protocol.ProtocolParserListener#onUserLogin(org.mJeliot.protocol.ProtocolParser, org.mJeliot.protocol.ParserCaller, java.lang.String, int)
	 */
	@Override
	public void onLogin(ProtocolParser protocolParser,
			ParserCaller parserCaller, int lectureId, String userName, int userId) {
	}

	private void fireOnLogin(Lecture lecture) {
		synchronized (this.listeners) {
			for(ICTControllerListener listener : this.listeners) {
				listener.onLogin(this, lecture);
			}
		}
	}

	/* (non-Javadoc)
	 * @see org.mJeliot.protocol.ProtocolParserListener#onUserLoggedIn(org.mJeliot.protocol.ProtocolParser, org.mJeliot.protocol.ParserCaller, java.lang.String, int)
	 */
	@Override
	public void onLoggedIn(ProtocolParser protocolParser,
			ParserCaller parserCaller, int lectureId, String userName, int userId) {
		if (this.user.getId() != userId) {
			User user = new User(userName, userId);
			this.addUser(user, lectureId);
		} else if(this.user.getId() == userId) {
			this.currentLecture = this.availableLectures.get(lectureId);
			this.fireOnLoggedIn();
		}
	}

	private void fireOnLoggedIn() {
		synchronized (this.listeners) {
			for(ICTControllerListener listener : this.listeners) {
				listener.onLoggedIn(this, this.currentLecture);
			}
		}
	}

	/**
	 * Sends a message with the new assignment to the client.
	 * @param method the method that is 
	 */
	public void sendMethodToPredict(Method method) {
		this.currentLecture.setMethod(method);
		this.client.sendMessage(parser.generateNewMethodPredict(this.currentLecture));
	}

	/* (non-Javadoc)
	 * @see org.mJeliot.protocol.ProtocolParserListener#onNewPredictMethod(org.mJeliot.protocol.ProtocolParser, org.mJeliot.protocol.ParserCaller, java.lang.String, java.lang.String, int, int, java.lang.String[])
	 */
	@Override
	public void onNewPredictMethod(ProtocolParser protocolParser,
			ParserCaller parserCaller, int lectureId, String className, String methodName,
			int methodId, int parameterCount, String[] parameterNames) {
		if (this.currentLecture != null) {
			Method method = new Method(className, methodName, methodId);
			this.currentLecture.setMethod(method);
			for (int i = 0; i < parameterCount; i++) {
				this.currentLecture.getMethod().addParameter(parameterNames[i]);
			}
			this.fireOnNewMethod(method);
		}
	}

	private void fireOnNewMethod(Method method) {
		synchronized (this.listeners) {
			for (ICTControllerListener listener : this.listeners) {
				listener.onNewMethod(this, method);
			}
		}
	}

	/* (non-Javadoc)
	 * @see org.mJeliot.protocol.ProtocolParserListener#onUserHandedInMethod(org.mJeliot.protocol.ProtocolParser, org.mJeliot.protocol.ParserCaller, int, int, int, java.lang.String[], java.lang.String[])
	 */
	@Override
	public void onUserHandedInMethod(ProtocolParser protocolParser,
			ParserCaller parserCaller, int lectureId, int userId, int methodId,
			int parameterCount, String[] parameterNames,
			String[] predictedValues) {
		User user = null;
		for (User u : this.currentLecture.getUsers()) {
			if (u.getId() == userId) {
				user = u;
			}
		}
		if (this.currentLecture.getId() == lectureId && this.currentLecture.getMethod().getId() == methodId && user != null) {
			for (int i = 0; i < parameterCount; i++) {
				this.currentLecture.getMethod().getParameterByName(parameterNames[i]).
				setPredictedValue(user, predictedValues[i]);
			}
		}
		this.fireOnAnswerCountChanged();
	}

	/* (non-Javadoc)
	 * @see org.mJeliot.protocol.ProtocolParserListener#onPredictResult(org.mJeliot.protocol.ProtocolParser, org.mJeliot.protocol.ParserCaller, int, int, java.lang.String[], java.lang.String[])
	 */
	@Override
	public void onPredictResult(ProtocolParser protocolParser,
			ParserCaller parserCaller, int lectureId, int methodId, int parameterCount,
			String[] parameterNames, String[] parameterValues) {
		for (int i = 0; i < parameterCount; i++) {
			if (this.currentLecture.getMethod() != null) {
				this.currentLecture.getMethod().getParameterByName(parameterNames[i]).
				setActualValue(parameterValues[i]);
			}
		}
		this.fireOnPredictResult();
	}

	/**
	 * Sends a notification to all listeners when the result for an assignment gets posted.
	 */
	private void fireOnPredictResult() {
		synchronized (this.listeners) {
			for (ICTControllerListener listener : this.listeners) {
				listener.onResultPosted(this, this.currentLecture.getMethod());
			}
		}
	}

	/**
	 * Sends a notification to all listeners when the number of clients that have answered
	 * changes.
	 */
	private void fireOnAnswerCountChanged() {
		synchronized (this.listeners) {
			for (ICTControllerListener listener : listeners) {
				listener.onAnswerCountChanged(this);
			}
		}
	}

	/* (non-Javadoc)
	 * @see org.mJeliot.protocol.ProtocolParserListener#onUserLogout(org.mJeliot.protocol.ProtocolParser, org.mJeliot.protocol.ParserCaller, int)
	 */
	@Override
	public void onUserLogout(ProtocolParser protocolParser,
			ParserCaller parserCaller, int lectureId, int userId) {
		if (userId == this.user.getId()) {
			System.err.println("someone is logging us out.");
		}
	}

	/* (non-Javadoc)
	 * @see org.mJeliot.protocol.ProtocolParserListener#onUserLoggedOut(org.mJeliot.protocol.ProtocolParser, org.mJeliot.protocol.ParserCaller, int)
	 */
	@Override
	public void onUserLoggedOut(ProtocolParser protocolParser,
			ParserCaller parserCaller, int lectureId, int userId) {
		if (this.currentLecture.getId() == lectureId && this.user.getId() == userId) {
			this.fireOnLoggedOut(this.availableLectures.get(lectureId));
			this.reset();
		} else if (this.currentLecture.getId() == lectureId){
			User user = null;
			for (int i = 0; i < this.currentLecture.getUsers().size(); i++) {
				if (this.currentLecture.getUsers().get(i).getId() == userId) {
					user = this.currentLecture.getUsers().get(i);
					this.currentLecture.removeUser(user);
				}
			}
			if (this.currentLecture.getMethod() != null) {
				for (int i = 0; i < this.currentLecture.getMethod().getParameters().size(); i++) {
					ParameterPrediction prediction = this.currentLecture.getMethod().getParameters().get(i).getPredictionForUser(user);
					this.currentLecture.getMethod().getParameters().get(i).removePrediction(prediction);
				}
			}
		}
		this.fireOnUserCountChanged();
		this.fireOnAnswerCountChanged();
	}

	/**
	 * Disconnects the controller's client.
	 */
	public void disconnectClient() {
		if (this.currentLecture != null) {
			this.sendMessage(this.parser.generateUserLogout(this.user, this.currentLecture));
		}
		this.client.disconnect();
	}
	
	/* (non-Javadoc)
	 * @see org.mJeliot.client.ClientListener#onClientDisconnected(org.mJeliot.client.Client)
	 */
	@Override
	public void onClientDisconnected(Client client) {
		this.reset();
		this.fireOnClientDisconnected();
	}

	/**
	 * Notifies all the registered listeners when the client gets disconnected.
	 */
	private void fireOnClientDisconnected() {
		synchronized (this.listeners) {
			for (ICTControllerListener listener : this.listeners) {
				listener.onClientDisconnected(this);
			}
		}
	}

	private void addUser(User user, int lectureId) {
		if (this.currentLecture != null && this.currentLecture.getId() == lectureId) {
			
			if (!currentLecture.getUsers().contains(user)) {
				this.currentLecture.addUser(user);
				this.fireOnUserCountChanged();
			}
		}
	}

	/* (non-Javadoc)
	 * @see org.mJeliot.protocol.ParserCaller#getUser()
	 */
	@Override
	public User getUser() {
		return this.user;
	}

	/* (non-Javadoc)
	 * @see org.mJeliot.protocol.ParserCaller#setUser(org.mJeliot.model.User)
	 */
	@Override
	public void setUser(User user) {
		// we don't have to change the user
	}

	/**
	 * The number of users registered to the controller without the controller itself
	 * @return the user count
	 */
	public int getUserCount() {
		if (this.currentLecture != null) {
			return this.currentLecture.getUsers().size();
		} else {
			return 0;
		}
	}

	/**
	 * Sends an event notification to all the listeners when the number of connected
	 * users change.
	 */
	private void fireOnUserCountChanged() {
		synchronized (this.listeners) {
			for (ICTControllerListener listener : this.listeners) {
				listener.onUserCountChanged(this);
			}
		}
	}

	/* (non-Javadoc)
	 * @see org.mJeliot.protocol.ParserCaller#sendMessage(java.lang.String)
	 */
	@Override
	public void sendMessage(String message) {
		this.client.sendMessage(message);
	}

	/**
	 * The number of users that answered on an assignment.
	 * @return the answer count
	 */
	public int getReceivedAnswerCount() {
		if (this.currentLecture != null && this.currentLecture.getMethod() != null &&
				this.currentLecture.getMethod().getParameters().get(0) != null) {
			Parameter parameter = this.currentLecture.getMethod().getParameters().get(0);
			return parameter.getPredictions().size();
		} else {
			return 0;
		}
	}

	/**
	 * @return the current method
	 */
	public Method getCurrentMethod() {
		return this.currentLecture.getMethod();
	}

	/**
	 * Registers the call of a method.
	 * @param method The called method
	 */
	public void methodCalled(Method method) {
		this.fireOnMethodCalled(method);
	}

	/**
	 * Informs all the listeners about a called method.
	 * @param method The called method
	 */
	private void fireOnMethodCalled(Method method) {
		synchronized (this.listeners) {
			for (ICTControllerListener listener : this.listeners) {
				listener.onMethodCalled(this, method);
			}
		}
	}

	/**
	 * Informs the server about a method that returned.
	 * @param method The method that returned.
	 */
	public void methodReturned(Method method) {
		if (this.currentLecture.getMethod() != null) {
			if (this.currentLecture.getMethod().equals(method)) {
				for (Parameter parameter : this.currentLecture.getMethod().getParameters()) {
					parameter.setActualValue(method.getParameterByName(parameter.getName()).getActualValue());
				}
				this.client.sendMessage(this.parser.generatePredictResult(this.currentLecture));
			} else {
				System.out.println(this.currentLecture.getMethod());
				System.out.println(method);
			}
		}
		this.fireOnMethodReturned(method);
	}
	

	/**
	 * Informs all the listeners that a method returned.
	 * @param method The returned method.
	 */
	private void fireOnMethodReturned(Method method) {
		synchronized (this.listeners) {
			for (ICTControllerListener listener : this.listeners) {
				listener.onMethodReturned(this, method);
			}
		}
	}

	@Override
	public void onLectureQuery(ProtocolParser protocolParser,
			ParserCaller parserCaller) {
	}

	@Override
	public void onUserList(ProtocolParser protocolParser,
			ParserCaller parserCaller, int lectureId, int userCount, int[] userIds,
			String[] userNames) {
		if (this.currentLecture != null && this.currentLecture.getId() == lectureId) {
			for (int i = 0; i < userCount; i++) {
				this.addUser(new User(userNames[i], userIds[i]), lectureId);
			}
		}
		
	}

	@Override
	public void onNewLecture(ProtocolParser protocolParser,
			ParserCaller parserCaller, int lectureId, String lectureName) {
		if (!this.availableLectures.containsKey(lectureId)) {
			Lecture lecture = new Lecture(lectureId, lectureName);
			this.availableLectures.put(lectureId,
					lecture);
			this.fireOnNewLecture(lecture);
		} else {
			this.fireOnLectureUpdated(this.availableLectures.get(lectureId));
		}
	}

	@Override
	public void onLectureList(ProtocolParser protocolParser,
			ParserCaller parserCaller, int lectureCount, int[] lectureIds,
			String[] lectureNames) {
		for (int i = 0; i < lectureCount; i++) {
			if (!this.availableLectures.containsKey(lectureIds[i])) {
				Lecture lecture = new Lecture(lectureIds[i], lectureNames[i]);
				this.availableLectures.put(lectureIds[i],
						lecture);
				this.fireOnNewLecture(lecture);
			} else {
				this.fireOnLectureUpdated(this.availableLectures.get(lectureIds[i]));
			}
		}
	}

	private void fireOnLectureUpdated(Lecture lecture) {
		synchronized (this.listeners) {
			for(ICTControllerListener listener : this.listeners) {
				listener.onLectureUpdated(this, lecture);
			}
		}
	}

	private void fireOnNewLecture(Lecture lecture) {
		synchronized (this.listeners) {
			for(ICTControllerListener listener : this.listeners) {
				listener.onNewLecture(this, lecture);
			}
		}
	}

	public Lecture getCurrentLecture() {
		return this.currentLecture;
	}

	public Lecture[] getAvailableLectures() {
		return this.availableLectures.values().toArray(new Lecture[0]);
	}

	public void setLecture(Lecture lecture) {
		this.client.sendMessage(this.parser.generateUserLogin(this.user, lecture.getId()));
		this.fireOnLogin(lecture);
	}

	public void logout() {
		Lecture lecture = this.currentLecture;
		this.client.sendMessage(this.parser.generateUserLogout(this.user, this.currentLecture));
		this.fireOnLogout(lecture);
	}

	private void fireOnLogout(Lecture lecture) {
		synchronized (this.listeners) {
			for(ICTControllerListener listener : this.listeners) {
				listener.onLogout(this, lecture);
			}
		}
	}
	private void fireOnLoggedOut(Lecture lecture) {
		synchronized (this.listeners) {
			for(ICTControllerListener listener : this.listeners) {
				listener.onLoggedOut(this, lecture);
			}
		}
	}
}
