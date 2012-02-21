package ict.server;

import java.util.HashMap;
import java.util.Vector;

import ict.model.Lecture;
import ict.model.User;
import ict.model.predict.Method;
import ict.protocol.ParserCaller;
import ict.protocol.ProtocolParser;
import ict.protocol.ProtocolParserListener;

/**
 * @author Moritz Rogalli
 * The ServerController keeps the state and acts on input from the clients.
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
	/**
	 * The corresponding server.
	 */
	private Server server = null;
	private Vector<ServerControllerListener> listeners = new Vector<ServerControllerListener>();

	/**
	 * Initialises a ServerController object 
	 * @param server the Server that initiated the controller
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
	 * @param name the lecture's name
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
	/* (non-Javadoc)
	 * @see ict.protocol.ProtocolParserListener#onUserLogin(ict.protocol.ProtocolParser, ict.protocol.ParserCaller, java.lang.String, int)
	 */
	@Override
	public void onLogin(ProtocolParser protocolParser, ParserCaller returnSender, 
			int lectureId, String userName, int userId) {
		System.out.println("User " + userName + " with ID " + userId + " logged in to lecture " + lectureId);
		Lecture lecture = this.lectures.get(lectureId);
		User user = new User(userName, userId, lecture);
		lecture.addUser(user);
		returnSender.setUser(user);
		this.sendLoginConfirmForUser(user, lectureId);
		this.sendCurrentState(returnSender, lectureId);
	}
	
	/**
	 * Sends the current state to a ParserCaller
	 * @param parserCaller the caller that should receive the state
	 */
	private void sendCurrentState(ParserCaller parserCaller, int lectureId) {
		Lecture lecture = this.lectures.get(lectureId);
		String message = this.parser.generateUserList(this.lectures.get(lectureId).getUsers(), lectureId);
		if (lecture.getMethod() != null) {
			message += parser.generateNewMethodPredict(lecture);
		}
		parserCaller.sendMessage(message);
	}

	/**
	 * Sends a logged-in message to all connected clients to inform them of a new user.
	 * @param user the new user that logged in
	 */
	private void sendLoginConfirmForUser(User user, int lectureId) {
		String message = this.parser.generateUserLoggedIn(user, this.lectures.get(lectureId));
		for (ServerThread serverThread : this.server.getServerThreads()) {
			serverThread.sendMessage(message);
		}
	}
	/* (non-Javadoc)
	 * @see ict.protocol.ProtocolParserListener#onUserLoggedIn(ict.protocol.ProtocolParser, ict.protocol.ParserCaller, java.lang.String, int)
	 */
	@Override
	public void onLoggedIn(ProtocolParser protocolParser, ParserCaller returnSender,
			int lectureId, String userName, int userId) {
		// the Server should not receive that message, we work with a TCP-connection so 
		// the arrival of the message is guaranteed and no 3-way-handshake required
	}

	/* (non-Javadoc)
	 * @see ict.protocol.ProtocolParserListener#onUserLogout(ict.protocol.ProtocolParser, ict.protocol.ParserCaller, int)
	 */
	@Override
	public void onUserLogout(ProtocolParser protocolParser, ParserCaller returnSender,
			int lectureId, int userId) {
		System.out.println("User with ID " + userId + " logged out of lecture " + lectureId);
		User removeUser = null;
		for (User user : this.lectures.get(lectureId).getUsers()) {
			if (user.getId() == userId) {
				removeUser = user;
			}
		}
		if (removeUser != null && this.lectures.get(lectureId) != null) {
			this.disconnectUser(removeUser, this.lectures.get(lectureId));
		}
	}

	/**
	 * Logs a user out of the server and informs all connected clients.
	 * @param user the user to log out
	 */
	public void disconnectUser(User user, Lecture lecture) {
		if (lecture.getUsers().contains(user)) {
			lecture.removeUser(user);
			String message = this.parser.generateUserLoggedOut(user, lecture);
			for (ServerThread serverThread : this.server.getServerThreads()) {
				serverThread.sendMessage(message);
			}
		}
	}
	/* (non-Javadoc)
	 * @see ict.protocol.ProtocolParserListener#onUserLoggedOut(ict.protocol.ProtocolParser, ict.protocol.ParserCaller, int)
	 */
	@Override
	public void onUserLoggedOut(ProtocolParser protocolParser, ParserCaller returnSender,
			int lectureId, int userId) {
		// again, no 3-way-stuff needed
	}

	/* (non-Javadoc)
	 * @see ict.protocol.ProtocolParserListener#onUserHandedInMethod(ict.protocol.ProtocolParser, ict.protocol.ParserCaller, int, int, int, java.lang.String[], java.lang.String[])
	 */
	@Override
	public void onUserHandedInMethod(ProtocolParser protocolParser,
			ParserCaller returnSender, int lectureId, int userId, int methodId,
			int parameterCount,	String[] parameterNames, String[] predictedValues) {
		System.out.println("User with ID " + userId + " handed in an assignment " + methodId + " in lecture " + lectureId);
		User currentUser = null;
		Method currentMethod = this.lectures.get(lectureId).getMethod();
		Vector<User> users = this.lectures.get(lectureId).getUsers();
		for (int i = 0; i < users.size(); i++) {
			if (users.get(i).getId() == userId) {
				currentUser = users.get(i);
			}
		}
		if (currentMethod != null) {
			if (currentMethod.getId() != methodId) {
				System.err.println("Got a handin with the wrong method ID from user: " + userId);
			} else {
				for (int i = 0; i < parameterCount; i++) {
					currentMethod.getParameterByName(parameterNames[i]).setPredictedValue(currentUser, predictedValues[i]);
				}
				String message = parser.generatePredictHandIn(currentUser, this.lectures.get(lectureId));
				for (ServerThread serverThread : this.server.getServerThreads()) {
					serverThread.sendMessage(message);
				}
			}
		} else {
			System.err.println("Got a handin even though there is no current assignment");
		}
	}

	/* (non-Javadoc)
	 * @see ict.protocol.ProtocolParserListener#onNewPredictMethod(ict.protocol.ProtocolParser, ict.protocol.ParserCaller, java.lang.String, java.lang.String, int, int, java.lang.String[])
	 */
	@Override
	public void onNewPredictMethod(ProtocolParser protocolParser, 
			ParserCaller returnSender, int lectureId, String className, String methodName,
			int methodId, int parameterCount, String[] parameterNames) {
		System.out.println("new assignment for method " + className + "." + methodName + " with ID " + methodId + " got posted in lecture " + lectureId);
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

	/* (non-Javadoc)
	 * @see ict.protocol.ProtocolParserListener#onPredictResult(ict.protocol.ProtocolParser, ict.protocol.ParserCaller, int, int, java.lang.String[], java.lang.String[])
	 */
	@Override
	public void onPredictResult(ProtocolParser protocolParser, ParserCaller returnSender,
			int lectureId, int methodId, int parameterCount, String[] parameterNames,
			String[] parameterValues) {
		System.out.println("New result for method " + methodId + " posted in lecture " + lectureId);
		Lecture currentLecture = this.lectures.get(lectureId);
		Method currentMethod = currentLecture.getMethod();
		if (currentMethod == null || currentMethod.getId() != methodId) {
			System.err.println("got the wrong methodId. " + currentMethod + " " + currentMethod.getId() + " " + methodId);
		} else {
			for (int i = 0; i < parameterCount; i++) {
				currentMethod.getParameterByName(parameterNames[i]).setActualValue(parameterValues[i]);
			}
			String message = parser.generatePredictResult(currentLecture);
			for (ServerThread serverThread : this.server.getServerThreads()) {
				serverThread.sendMessage(message);
			}
		}
		currentLecture.removeCurrentMethod();
	}
	/* (non-Javadoc)
	 * @see ict.protocol.ProtocolParserListener#onNewLecture(ict.protocol.ProtocolParser, ict.protocol.ParserCaller, int, java.lang.String)
	 */
	@Override
	public void onNewLecture(ProtocolParser protocolParser,
			ParserCaller parserCaller, int lectureId, String lectureName) {
	}
	/* (non-Javadoc)
	 * @see ict.protocol.ProtocolParserListener#onLectureQuery(ict.protocol.ProtocolParser, ict.protocol.ParserCaller)
	 */
	@Override
	public void onLectureQuery(ProtocolParser protocolParser, ParserCaller parserCaller) {
		System.out.println("lectureList got queried");
		String message = this.parser.generateLectureList(this.lectures.values());
		parserCaller.sendMessage(message);
	}
	/* (non-Javadoc)
	 * @see ict.protocol.ProtocolParserListener#onLectureList(ict.protocol.ProtocolParser, ict.protocol.ParserCaller, int, int[], java.lang.String[])
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
	 * @param lecture The lecture to delete
	 */
	public void deleteLecture(Lecture lecture) {
		if (this.lectures.containsValue(lecture)) {
			this.lectures.remove(lecture.getId());
			this.fireOnLectureRemoved(lecture);
		}
	}
	/**
	 * Informs all registered ServerControllerListeners when a Lecture is deleted from the
	 * list.
	 * @param lecture the deleted lecture
	 */
	private void fireOnLectureRemoved(Lecture lecture) {
		for (ServerControllerListener listener : this.listeners) {
			listener.onLectureRemoved(this, lecture);
		}
	}
	/**
	 * Informs all registered ServerControllerListeners when a new Lecture is added.
	 * @param lecture the added Lecture
	 */
	private void fireOnLectureAdded(Lecture lecture) {
		for (ServerControllerListener listener : this.listeners) {
			listener.onLectureAdded(this, lecture);
		}
	}
	/**
	 * @param lectureId the Lecture's id
	 * @return the Lecture with the id lectureId if available, null otherwise
	 */
	public Lecture getLecture(int lectureId) {
		return this.lectures.get(lectureId);
	}
	/**
	 * Adds a ServerControllerListener to the list of ServerControllerListeners. The
	 * function checks if the ServerControllerListener is already registered, so adding
	 * a ServerControllerListeners a second time will not result in it being informed
	 * twice.
	 * @param serverControllerListener the ServerControllerListeners to add
	 */
	public void addServerControllerListener(ServerControllerListener serverControllerListener) {
		if (!this.listeners.contains(serverControllerListener)) {
			this.listeners.add(serverControllerListener);
		}
	}
	/**
	 * Removes a ServerControllerListener from the list of ServerControllerListeners.
	 * @param serverControllerListener the ServerControllerListener to remove
	 */
	public void removeServerControllerListener(ServerControllerListener serverControllerListener) {
		this.listeners.remove(serverControllerListener);
	}
	/**
	 * Shuts down the Server. Since the Server disconnects all clients and exits the 
	 * application we don't have to clean up.
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
			ParserCaller parserCaller, int lectureId, int userCount, int[] userIds,
			String[] userNames) {
		// The server does not care about UserLists
	}
}
