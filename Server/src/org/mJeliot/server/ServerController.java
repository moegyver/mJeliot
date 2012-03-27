package org.mJeliot.server;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import org.mJeliot.helpers.Pair;
import org.mJeliot.model.Lecture;
import org.mJeliot.model.User;
import org.mJeliot.model.predict.Method;
import org.mJeliot.protocol.ParserCaller;
import org.mJeliot.protocol.ProtocolParser;
import org.mJeliot.protocol.ProtocolParserListener;

/**
 * @author Moritz Rogalli The ServerController keeps the state and acts on input
 *         from the clients.
 */
public class ServerController implements ProtocolParserListener {
	/**
	 * The parser to generate messages and to parse incoming messages.
	 */
	private ProtocolParser parser = new ProtocolParser();
	/**
	 * The available lectures.
	 */
	private HashMap<Integer, Lecture> lectures = new HashMap<Integer, Lecture>();
	private HashMap<Pair<Integer, Integer>, UserTimerTimeoutTask> userTimeoutTimers = new HashMap<Pair<Integer, Integer>, UserTimerTimeoutTask>();
	private HashMap<Integer, ServerThread> currentUserThreads = new HashMap<Integer, ServerThread>();
	/**
	 * The corresponding server.
	 */
	private Server server = null;
	private Vector<ServerControllerListener> listeners = new Vector<ServerControllerListener>();

	private Timer userScheduler = new Timer();
	/**
	 * Initialises a ServerController object
	 * 
	 * @param server
	 *            the Server that initiated the controller
	 */
	protected ServerController(Server server) {
		this.server = server;
		this.parser.addProtocolParserListener(this);
	}

	/**
	 * @return returns the parser
	 */
	public ProtocolParser getParser() {
		return parser;
	}

	/**
	 * Adds a new lecture to the server and informs all clients.
	 * 
	 * @param name
	 *            the lecture's name
	 */
	public Lecture addLecture(String name) {
		Lecture lecture = new Lecture(name);
		this.lectures.put(lecture.getId(), lecture);
		String message = parser.generateLecture(lecture);
		for (ServerThread serverThread : this.server.getServerThreads()) {
			serverThread.sendMessage(message);
		}
		this.fireOnLectureAdded(lecture);
		return lecture;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.mJeliot.protocol.ProtocolParserListener#onUserLogin(org.mJeliot.protocol
	 * .ProtocolParser, org.mJeliot.protocol.ParserCaller, java.lang.String,
	 * int)
	 */
	@Override
	public void onLogin(ProtocolParser protocolParser,
			ParserCaller returnSender, int lectureId, String userName,
			int userId) {
		System.out.println("User " + userName + " with ID " + userId
				+ " logged in to lecture " + lectureId);
		Lecture lecture = this.lectures.get(lectureId);
		if (lecture != null) {
			User user = new User(userName, userId, lecture);
			lecture.addUser(user);
			this.startUserTimeoutTask(user, lecture);
			this.sendLoginConfirmForUser(user, lectureId);
			this.sendCurrentState(returnSender, lectureId);
			this.currentUserThreads.put(userId, (ServerThread)returnSender);
			System.out.println("Added " + returnSender + "as user thread for user: " + userId);
		}
	}

	private void startUserTimeoutTask(User user, Lecture lecture) {
		UserTimerTimeoutTask userTimeoutTask = new UserTimerTimeoutTask(this, user);
		this.userTimeoutTimers.put(new Pair<Integer, Integer>(lecture.getId(), user.getId()), userTimeoutTask);
		this.userScheduler.schedule(userTimeoutTask, ServerThread.PING_INTERVAL, ServerThread.PING_INTERVAL);
	}

	/**
	 * Sends the current state to a ParserCaller
	 * 
	 * @param parserCaller
	 *            the caller that should receive the state
	 */
	private void sendCurrentState(ParserCaller parserCaller, int lectureId) {
		Lecture lecture = this.lectures.get(lectureId);
		String message = this.parser.generateUserList(
				this.lectures.get(lectureId).getUsers(), lectureId);
		if (lecture.getMethod() != null) {
			message += parser.generateNewMethodPredict(lecture);
		}
		parserCaller.sendMessage(message);
	}

	/**
	 * Sends a logged-in message to all connected clients to inform them of a
	 * new user.
	 * 
	 * @param user
	 *            the new user that logged in
	 */
	private void sendLoginConfirmForUser(User user, int lectureId) {
		String message = this.parser.generateUserLoggedIn(user,
				this.lectures.get(lectureId));
		for (ServerThread serverThread : this.server.getServerThreads()) {
			serverThread.sendMessage(message);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.mJeliot.protocol.ProtocolParserListener#onUserLoggedIn(org.mJeliot
	 * .protocol.ProtocolParser, org.mJeliot.protocol.ParserCaller,
	 * java.lang.String, int)
	 */
	@Override
	public void onLoggedIn(ProtocolParser protocolParser,
			ParserCaller returnSender, int lectureId, String userName,
			int userId) {
		// the Server should not receive that message, we work with a
		// TCP-connection so
		// the arrival of the message is guaranteed and no 3-way-handshake
		// required
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.mJeliot.protocol.ProtocolParserListener#onUserLogout(org.mJeliot.
	 * protocol.ProtocolParser, org.mJeliot.protocol.ParserCaller, int)
	 */
	@Override
	public void onUserLogout(ProtocolParser protocolParser,
			ParserCaller returnSender, int lectureId, int userId) {
		System.out.println("User with ID " + userId + " logged out of lecture "
				+ lectureId);
		User removeUser = null;
		if (this.lectures.get(lectureId) != null) {
			for (User user : this.lectures.get(lectureId).getUsers()) {
				if (user.getId() == userId) {
					removeUser = user;
				}
			}
		}
		if (removeUser != null && this.lectures.get(lectureId) != null) {
			this.disconnectUser(removeUser, lectureId);
		}
	}

	/**
	 * Logs a user out of the server and informs all connected clients.
	 * 
	 * @param user
	 *            the user to log out
	 */
	public void disconnectUser(User user, int lectureId) {
		System.out.println("disconnecting user " + user.getId() + " from lecture " + lectureId); 
		TimerTask userTimerTask = this.userTimeoutTimers.get(new Pair<Integer, Integer>(lectureId, user.getId()));
		if (userTimerTask != null) {
			userTimerTask.cancel();
			System.out.println("stopping timer task");
		}
		if (null == this.userTimeoutTimers.remove(new Pair<Integer, Integer>(lectureId, user.getId()))) {
			System.err.println("could not remove userTimeoutTimer");
		}
		this.currentUserThreads.remove(user.getId());
		Lecture lecture = this.lectures.get(lectureId);
		if (lecture.containsUser(user)) {
			System.out.println("removing user from lecture: " + user.getId());
			lecture.removeUser(user);
			String message = this.parser.generateUserLoggedOut(user, lecture);
			for (ServerThread serverThread : this.server.getServerThreads()) {
				serverThread.sendMessage(message);
			}
		} else {
			System.err.println("could not find user " + user + " for disconnecting.");
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.mJeliot.protocol.ProtocolParserListener#onUserLoggedOut(org.mJeliot
	 * .protocol.ProtocolParser, org.mJeliot.protocol.ParserCaller, int)
	 */
	@Override
	public void onUserLoggedOut(ProtocolParser protocolParser,
			ParserCaller returnSender, int lectureId, int userId) {
		System.out.println("got a logged out from user " + userId + " in lecture " + lectureId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.mJeliot.protocol.ProtocolParserListener#onUserHandedInMethod(org.
	 * mJeliot.protocol.ProtocolParser, org.mJeliot.protocol.ParserCaller, int,
	 * int, int, java.lang.String[], java.lang.String[])
	 */
	@Override
	public void onUserHandedInMethod(ProtocolParser protocolParser,
			ParserCaller returnSender, int lectureId, int userId, int methodId,
			int parameterCount, String[] parameterNames,
			String[] predictedValues) {
		System.out.println("User with ID " + userId
				+ " handed in an assignment " + methodId + " in lecture "
				+ lectureId);
		User currentUser = null;
		Method currentMethod = this.lectures.get(lectureId).getMethod();
		User[] users = this.lectures.get(lectureId).getUsers();
		for (User user : users) {
			if (user.getId() == userId) {
				currentUser = user;
			}
		}
		if (currentMethod != null) {
			if (currentMethod.getId() != methodId) {
				System.err
						.println("Got a handin with the wrong method ID from user: "
								+ userId);
			} else {
				for (int i = 0; i < parameterCount; i++) {
					currentMethod.getParameterByName(parameterNames[i])
							.setPredictedValue(currentUser, predictedValues[i]);
				}
				String message = parser.generatePredictHandIn(currentUser,
						this.lectures.get(lectureId));
				for (ServerThread serverThread : this.server.getServerThreads()) {
					serverThread.sendMessage(message);
				}
			}
		} else {
			System.err
					.println("Got a handin even though there is no current assignment");
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.mJeliot.protocol.ProtocolParserListener#onNewPredictMethod(org.mJeliot
	 * .protocol.ProtocolParser, org.mJeliot.protocol.ParserCaller,
	 * java.lang.String, java.lang.String, int, int, java.lang.String[])
	 */
	@Override
	public void onNewPredictMethod(ProtocolParser protocolParser,
			ParserCaller returnSender, int lectureId, String className,
			String methodName, int methodId, int parameterCount,
			String[] parameterNames) {
		System.out.println("new assignment for method " + className + "."
				+ methodName + " with ID " + methodId
				+ " got posted in lecture " + lectureId);
		Lecture lecture = lectures.get(lectureId);
		lecture.newMethod(className, methodName, methodId);
		for (int i = 0; i < parameterCount; i++) {
			lecture.getMethod().addParameter(parameterNames[i]);
		}
		String message = parser.generateNewMethodPredict(lecture);
		for (ServerThread serverThread : this.server.getServerThreads()) {
			serverThread.sendMessage(message);
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.mJeliot.protocol.ProtocolParserListener#onPredictResult(org.mJeliot
	 * .protocol.ProtocolParser, org.mJeliot.protocol.ParserCaller, int, int,
	 * java.lang.String[], java.lang.String[])
	 */
	@Override
	public void onPredictResult(ProtocolParser protocolParser,
			ParserCaller returnSender, int lectureId, int methodId,
			int parameterCount, String[] parameterNames,
			String[] parameterValues) {
		System.out.println("New result for method " + methodId
				+ " posted in lecture " + lectureId);
		Lecture currentLecture = this.lectures.get(lectureId);
		Method currentMethod = currentLecture.getMethod();
		if (currentMethod == null || currentMethod.getId() != methodId) {
			System.err.println("got the wrong methodId. " + currentMethod + " "
					+ currentMethod.getId() + " " + methodId);
		} else {
			for (int i = 0; i < parameterCount; i++) {
				currentMethod.getParameterByName(parameterNames[i])
						.setActualValue(parameterValues[i]);
			}
			String message = parser.generatePredictResult(currentLecture);
			for (ServerThread serverThread : this.server.getServerThreads()) {
				serverThread.sendMessage(message);
			}
		}
		currentLecture.removeCurrentMethod();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.mJeliot.protocol.ProtocolParserListener#onNewLecture(org.mJeliot.
	 * protocol.ProtocolParser, org.mJeliot.protocol.ParserCaller, int,
	 * java.lang.String)
	 */
	@Override
	public void onNewLecture(ProtocolParser protocolParser,
			ParserCaller parserCaller, int lectureId, String lectureName) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.mJeliot.protocol.ProtocolParserListener#onLectureQuery(org.mJeliot
	 * .protocol.ProtocolParser, org.mJeliot.protocol.ParserCaller)
	 */
	@Override
	public void onLectureQuery(ProtocolParser protocolParser,
			ParserCaller parserCaller) {
		System.out.println("lectureList got queried");
		String message = this.parser
				.generateLectureList(this.lectures.values());
		parserCaller.sendMessage(message);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.mJeliot.protocol.ProtocolParserListener#onLectureList(org.mJeliot
	 * .protocol.ProtocolParser, org.mJeliot.protocol.ParserCaller, int, int[],
	 * java.lang.String[])
	 */
	@Override
	public void onLectureList(ProtocolParser protocolParser,
			ParserCaller parserCaller, int lectureCount, int[] lectureIds,
			String[] lectureNames) {
		// we do not react on this.

	}

	/**
	 * @return the number of available Lectures
	 */
	public int numberOfLectures() {
		return this.lectures.size();
	}

	/**
	 * Deletes a lecture from the list of available Lectures.
	 * 
	 * @param lecture
	 *            The lecture to delete
	 */
	public void deleteLecture(Lecture lecture) {
		if (this.lectures.containsValue(lecture)) {
			this.lectures.remove(lecture.getId());
			this.fireOnLectureRemoved(lecture);
		}
	}

	/**
	 * Informs all registered ServerControllerListeners when a Lecture is
	 * deleted from the list.
	 * 
	 * @param lecture
	 *            the deleted lecture
	 */
	private void fireOnLectureRemoved(Lecture lecture) {
		for (ServerControllerListener listener : this.listeners) {
			listener.onLectureRemoved(this, lecture);
		}
	}

	/**
	 * Informs all registered ServerControllerListeners when a new Lecture is
	 * added.
	 * 
	 * @param lecture
	 *            the added Lecture
	 */
	private void fireOnLectureAdded(Lecture lecture) {
		for (ServerControllerListener listener : this.listeners) {
			listener.onLectureAdded(this, lecture);
		}
	}

	/**
	 * @param lectureId
	 *            the Lecture's id
	 * @return the Lecture with the id lectureId if available, null otherwise
	 */
	public Lecture getLecture(int lectureId) {
		return this.lectures.get(lectureId);
	}

	/**
	 * Adds a ServerControllerListener to the list of ServerControllerListeners.
	 * The function checks if the ServerControllerListener is already
	 * registered, so adding a ServerControllerListeners a second time will not
	 * result in it being informed twice.
	 * 
	 * @param serverControllerListener
	 *            the ServerControllerListeners to add
	 */
	public void addServerControllerListener(
			ServerControllerListener serverControllerListener) {
		if (!this.listeners.contains(serverControllerListener)) {
			this.listeners.add(serverControllerListener);
		}
	}

	/**
	 * Removes a ServerControllerListener from the list of
	 * ServerControllerListeners.
	 * 
	 * @param serverControllerListener
	 *            the ServerControllerListener to remove
	 */
	public void removeServerControllerListener(
			ServerControllerListener serverControllerListener) {
		this.listeners.remove(serverControllerListener);
	}

	/**
	 * Shuts down the Server. Since the Server disconnects all clients and exits
	 * the application we don't have to clean up.
	 */
	public void shutdown() {
		server.shutdown();
	}

	/**
	 * @return the available lectures
	 */
	public Lecture[] getLectures() {
		return this.lectures.values().toArray(new Lecture[0]);
	}

	@Override
	public void onUserList(ProtocolParser protocolParser,
			ParserCaller parserCaller, int lectureId, int userCount,
			int[] userIds, String[] userNames) {
		// The server does not care about UserLists
	}

	public void resetUserTimer(ServerThread serverThread, int lectureId, int userId) {
		UserTimerTimeoutTask userTimerTask = this.userTimeoutTimers
				.get(new Pair<Integer, Integer>(lectureId, userId));
		if (userTimerTask != null) {
			userTimerTask.resetTimer();
		} else {
			Lecture lecture = this.lectures.get(lectureId);
			if (lecture != null) {
				User user = lecture.getUser(userId);
				if (user != null) {
					this.startUserTimeoutTask(user, lecture);
				}
			}
		}
		this.currentUserThreads.put(userId, serverThread);
	}

	public void removeServerThread(ServerThread serverThread) {
		if (this.currentUserThreads.containsValue(serverThread)) {
			this.currentUserThreads.values().remove(serverThread);
		}
	}

	@Override
	public void onCodeUpdate(ProtocolParser protocolParser,
			ParserCaller parserCaller, int lectureId, int userId, String code,
			int cursorPosition, boolean done, boolean requestedAttention,
			int destUserId) {
		System.out.println("sending code update back to Jeliot" + destUserId);
		if (this.currentUserThreads.containsKey(destUserId)) {
			String message = parser.generateCodeUpdate(code, cursorPosition, done, requestedAttention, destUserId, userId, lectureId); 
			ServerThread serverThread = this.currentUserThreads.get(destUserId);
			serverThread.sendMessage(message);
		} else {
			System.out.println("could not find destination");
		}
	}

	@Override
	public void onCodingTask(ProtocolParser protocolParser,
			ParserCaller parserCaller, int lectureId, int from, Integer to,
			String unescapedCode) {
		String message = ProtocolParser.generateCodingTask(unescapedCode, from, to, lectureId);
		// send broadcast if there is no destination set
		if (to == null) {
			System.out.println("broadcast coding task");
			for (ServerThread serverThread : this.server.getServerThreads()) {
				serverThread.sendMessage(message);
			}
		// send to destination otherwise
		} else {
			System.out.println("unicast coding task for user: " + to);
			ServerThread serverThread = this.currentUserThreads.get(to);
			if (serverThread != null) {
				serverThread.sendMessage(message);
			} else {
				System.err.println("No server thread found for user " + to);
			}
		}
	}

	@Override
	public void onLiveModeChanged(ProtocolParser protocolParser,
			ParserCaller parserCaller, int lectureId, int from, int to, boolean liveMode) {
		if (this.currentUserThreads.containsKey(to)) {
			String message = ProtocolParser.generateLiveMode(liveMode, lectureId, from, to); 
			ServerThread serverThread = this.currentUserThreads.get(to);
			serverThread.sendMessage(message);
		}
	}
}
