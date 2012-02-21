package ict.protocol;

import ict.model.Lecture;
import ict.model.User;
import ict.model.predict.Method;
import ict.model.predict.Parameter;

import java.io.IOException;
import java.io.StringReader;
import java.util.Collection;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * @author Moritz Rogalli
 * A protocol parser is responsible for generating xml-messages that can be sent and for
 * parsing them and informing all the listeners of understood messages.  
 */
public class ProtocolParser {
	
	// the building-blocks for the protocol
	private static final String xmlHeader = "<?xml version=\"1.0\" ?>";
	private static final String startActionBegin = "<ict version=\"1.0\" action=\"";
	public static final String endActionTag = "</ict>";
	public static final String endAction = endActionTag + "\n";
	private static final String startUserName = "<userName>";
	private static final String endUserName = "</userName>";
	private static final String startUserId = "<userId>";
	private static final String endUserId = "</userId>";
	private static final String startClassName = "<className>";
	private static final String startMethodName = "<methodName>";
	private static final String endClassName = "</className>";
	private static final String endMethodName = "</methodName>";
	private static final String endParameterCount = "</parameterCount>";
	private static final String startParameterCount = "<parameterCount>";
	private static final String startParameterStart = "<parameter name=\"";
	private static final String end = ">";
	private static final String endParameter = "</parameter>";
	private static final String startMethodId = "<methodId>";
	private static final String endMethodId = "</methodId>";
	private static final String startLectureId = "<lectureId>";
	private static final String endLectureId = "</lectureId>";
	private static final String lectureBegin = "<lecture ";
	private static final String endShortTag = " />";
	private static final String endLectureName = "</lectureName>";
	private static final String startLectureName = "<lectureName>";
	private static final String userBegin = "<user ";
	
	/**
	 * A list of listeners to inform on understood messages.
	 */
	private Vector<ProtocolParserListener> listeners = new Vector<ProtocolParserListener>();
	/**
	 * The DocumentBuilder is in charge for building a dom-tree out of the received xml-
	 * messages.
	 */
	private DocumentBuilder documentBuilder = null;
	
	/**
	 * This constructor creates a parser and makes it ready for parsing messages and
	 * generating them.
	 */
	public ProtocolParser() {
		try {
			this.documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Parses a message and dispatches events for all understood messages. Prints
	 * stackTraces or an error-message on std.err if the message is not a valid xml-
	 * document or the message is not understood by the protocol  
	 * @param message the message to parse
	 * @param parserCaller the source of the message
	 */
	/**
	 * @param message
	 * @param parserCaller
	 */
	public void parseMessage(String message, ParserCaller parserCaller) {
		Document document = null;
		String action = null;
		String className = null;
		String methodName = null;
		Integer methodId = null;
		String userName = null;
		Integer userId = null;
		String lectureName = null;
		Integer lectureId = null;
		Integer parameterCount = null;
		String[] parameterNames = null;
		String[] parameterValues = null;
		Integer lectureCount = null;
		int[] lectureIds = null;
		String[] lectureNames = null;
		Integer userCount = null;
		int[] userIds = null;
		String[] userNames = null;
		try {
			document = this.documentBuilder.parse(new InputSource(new StringReader(message)));
		} catch (SAXException e) {
			e.printStackTrace();
			return;
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		try {
			if (document.getElementsByTagName("ict") != null) {
				Node node = document.getElementsByTagName("ict").item(0);
				if (node != null) {
					action = node.getAttributes().getNamedItem("action").getNodeValue();
				}
				node = document.getElementsByTagName("className").item(0);
				if (node != null) {
					className = node.getTextContent();
				}
				node = document.getElementsByTagName("methodName").item(0);
				if (node != null) {
					methodName = node.getTextContent();
				}
				node = document.getElementsByTagName("methodId").item(0);
				if (node != null) {
					methodId = Integer.parseInt(node.getTextContent());
				}
				node = document.getElementsByTagName("userName").item(0);
				if (node != null) {
					userName = node.getTextContent();
				}
				node = document.getElementsByTagName("userId").item(0);
				if (node != null) {
					userId = Integer.parseInt(node.getTextContent());
				}
				node = document.getElementsByTagName("lectureName").item(0);
				if (node != null) {
					lectureName = node.getTextContent();
				}
				node = document.getElementsByTagName("lectureId").item(0);
				if (node != null) {
					lectureId = Integer.parseInt(node.getTextContent());
				}
				node = document.getElementsByTagName("parameterCount").item(0);
				if (node != null) {
					parameterCount = Integer.parseInt(node.getTextContent());
					parameterNames = new String[parameterCount];
					parameterValues = new String[parameterCount];
					for (int i = 0; i < parameterCount; i++) {
						node = document.getElementsByTagName("parameter").item(i);
						if (node != null) {
							parameterValues[i] = node.getTextContent();
							node = node.getAttributes().getNamedItem("name");
							if (node != null) {
								parameterNames[i] = node.getNodeValue();
							}
						}
					}
				}
				node = document.getElementsByTagName("lectureList").item(0);
				if (node != null) {
					node =node.getAttributes().getNamedItem("length");
					if (node != null) {
						int i = 0;
						lectureCount = Integer.parseInt(node.getNodeValue());
						lectureIds = new int[lectureCount];
						lectureNames = new String[lectureCount];
						while (document.getElementsByTagName("lecture").item(i) != null) {
							node = document.getElementsByTagName("lecture").item(i);
							if (node.getAttributes().getNamedItem("id") != null) {
								lectureIds[i] = Integer.parseInt(node.getAttributes().getNamedItem("id").getNodeValue());
							}
							if (node.getAttributes().getNamedItem("name") != null) {
								lectureNames[i] = node.getAttributes().getNamedItem("name").getNodeValue();
							}
							i++;
						}
					}
				}
				node = document.getElementsByTagName("userList").item(0);
				if (node != null) {
					node = node.getAttributes().getNamedItem("length");
					if (node != null) {
						int i = 0;
						userCount = Integer.parseInt(node.getNodeValue());
						userIds = new int[userCount];
						userNames = new String[userCount];
						while (document.getElementsByTagName("user").item(i) != null) {
							node = document.getElementsByTagName("user").item(i);
							if (node.getAttributes().getNamedItem("id") != null) {
								userIds[i] = Integer.parseInt(node.getAttributes().getNamedItem("id").getNodeValue());
							}
							if (node.getAttributes().getNamedItem("name") != null) {
								userNames[i] = node.getAttributes().getNamedItem("name").getNodeValue();
							}
							i++;
						}
					}
				}
				
				if (action.equalsIgnoreCase("lecture") && lectureId != null && lectureName !=null) {
					this.fireOnNewLecture(parserCaller, lectureId, lectureName);
				} else if (action.equalsIgnoreCase("userLogin") && lectureId != null && userName !=null
						&& userId != null) {
					this.fireOnUserLogin(parserCaller, lectureId, userName, userId);
				} else if (action.equalsIgnoreCase("userLoggedIn") && lectureId != null &&
						userName != null && userId != null) {
					this.fireOnUserLoggedIn(parserCaller, lectureId, userName, userId);
				} else if (action.equalsIgnoreCase("userLogout") && lectureId != null &&
						userId != null) {
					this.fireOnUserLogout(parserCaller, lectureId, userId);
				} else if (action.equalsIgnoreCase("userLoggedOut")	&& userId != null) {
					this.fireOnUserLoggedOut(parserCaller, lectureId, userId);
				} else if (action.equalsIgnoreCase("predictHandin") && lectureId != null
						&& userId != null && methodId != null && parameterCount != null
						&& parameterNames != null && parameterValues != null) {
					this.fireOnUserHandedInMethodPredict(parserCaller, lectureId, userId,
							methodId, parameterCount, parameterNames, parameterValues);
				} else if (action.equalsIgnoreCase("predictSendout") && lectureId != null
						&& className != null && methodName != null && methodId != null
						&& parameterCount != null && parameterNames != null) {
					this.fireOnNewMethodPredict(parserCaller, lectureId, className, methodName,
							methodId, parameterCount, parameterNames);
				} else if (action.equalsIgnoreCase("predictResult") && lectureId != null
						&& methodId != null && parameterCount != null && parameterNames != null
						&& parameterValues != null) {
					this.fireOnPredictResult(parserCaller, lectureId, methodId, parameterCount,
							parameterNames, parameterValues);
				} else if (action.equalsIgnoreCase("lectureQuery")) {
					this.fireOnLectureQuery(parserCaller);
				} else if (action.equalsIgnoreCase("lectureList") && lectureIds != null && lectureNames != null) {
					this.fireOnLectureList(parserCaller, lectureCount, lectureIds, lectureNames);
				} else if (action.equalsIgnoreCase("userList") && userIds != null && userNames != null) {
					this.fireOnUserList(parserCaller, lectureId, userCount, userIds, userNames);
				} else {
					System.err.println("Something missing in the parser or error in the message.");			
				}
			} else {
				System.err.println("Message not understood.");
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("Message not understood.");
		}
	}
	private void fireOnUserList(ParserCaller parserCaller, int lectureId, 
			Integer userCount, int[] userIds, String[] userNames) {
		for (ProtocolParserListener listener : this.listeners) {
			listener.onUserList(this, parserCaller, lectureId, userCount, userIds, userNames);
		}
		
	}
	/**
	 * Calls the event handlers of all the listeners on a received lecture list.
	 * @param parserCaller the source of the parser call
	 * @param lectureCount the number of lectures
	 * @param lectureIds the lectures' ids
	 * @param lectureNames the lectures' names
	 */
	private void fireOnLectureList(ParserCaller parserCaller,
			Integer lectureCount, int[] lectureIds, String[] lectureNames) {
		for (ProtocolParserListener listener : this.listeners) {
			listener.onLectureList(this, parserCaller, lectureCount, lectureIds, lectureNames);
		}
		
	}

	/**
	 * Adds a listener to the ProtocolParser if not yet in the list.
	 * @param ppl the listener to add
	 */
	public void addProtocolParserListener(ProtocolParserListener ppl) {
		if (!this.listeners.contains(ppl)) {
			this.listeners.add(ppl);
		}
	}
	/**
	 * Removes a listener from the Parser. 
	 * @param ppl the listener to remove
	 */
	public void removeProtocolParserListener(ProtocolParserListener ppl) {
		this.listeners.remove(ppl);
	}
	/**
	 * Dispatches an event to all listeners whenever a user queries the available
	 * lectures.
	 * @param parserCaller the source of the query
	 */
	private void fireOnLectureQuery(ParserCaller parserCaller) {
		for (ProtocolParserListener listener : this.listeners) {
			listener.onLectureQuery(this, parserCaller);
		}
	}
	/**
	 * Dispatches an event to all listeners whenever the parser parses a lecture
	 * message.
	 * @param parserCaller the source of the parser call
	 * @param lectureId the lecture's id
	 * @param lectureName the lecture's name
	 */
	private void fireOnNewLecture(ParserCaller parserCaller, int lectureId,
			String lectureName) {
		for (ProtocolParserListener listener : listeners) {
			listener.onNewLecture(this, parserCaller, lectureId, lectureName);
		}
	}
	/**
	 * Dispatches an event to all listeners whenever the parser parses a user login
	 * message.
	 * @param parserCaller the source of the parser call
	 * @param lectureId the lecture's id
	 * @param userName the user's alias from the parsed message
	 * @param userId the user's id from the parsed message
	 */
	private void fireOnUserLogin(ParserCaller parserCaller, int lectureId,
			String userName, int userId) {
		for (ProtocolParserListener listener : listeners) {
			listener.onLogin(this, parserCaller, lectureId, userName, userId);
		}
	}
	/**
	 * Dispatches an event to all listeners whenever the parser parses a user logged in
	 * message.
	 * @param parserCaller the source of the parser call
	 * @param lectureId the lecture's id
	 * @param userName the user's alias from the parsed message
	 * @param userId the user's id from the parsed message
	 */
	private void fireOnUserLoggedIn(ParserCaller parserCaller, int lectureId,
			String userName, int userId) {
		for (ProtocolParserListener listener : listeners) {
			listener.onLoggedIn(this, parserCaller, lectureId, userName, userId);
		}
	}
	/**
	 * Dispatches an event to all listeners whenever the parser parses a user logout
	 * message.
	 * @param parserCaller the source of the parser call
	 * @param lectureId the lecture's id
	 * @param userId the user's id from the parsed message
	 */
	private void fireOnUserLogout(ParserCaller parserCaller, int lectureId, int userId) {
		for (ProtocolParserListener listener : listeners) {
			listener.onUserLogout(this, parserCaller, lectureId, userId);
		}		
	}
	/**
	 * Dispatches an event to all listeners whenever the parser parses a user logged out
	 * message.
	 * @param parserCaller the source of the parser call
	 * @param lectureId the lecture's id
	 * @param userId the user's id from the parsed message
	 */
	private void fireOnUserLoggedOut(ParserCaller parserCaller, int lectureId, int userId) {
		for (ProtocolParserListener listener : listeners) {
			listener.onUserLoggedOut(this, parserCaller, lectureId, userId);
		}		
	}
	/**
	 * Dispatches an event to all listeners whenever the parser parses a hand-in from a
	 * user.
	 * @param parserCaller the source of the parser call
	 * @param lectureId the lecture's id
	 * @param userId the user's id
	 * @param methodId the id of the method the hand-in applied to
	 * @param parameterCount the number of parameters
	 * @param parameterNames the names of the parameters
	 * @param predictedValues the predicted values made by the user
	 */
	private void fireOnUserHandedInMethodPredict(ParserCaller parserCaller, int lectureId, 
			int userId, int methodId, int parameterCount, String[] parameterNames,
			String[] predictedValues) {
		for (ProtocolParserListener listener : listeners) {
			listener.onUserHandedInMethod(this, parserCaller, lectureId, userId, methodId,
					parameterCount, parameterNames, predictedValues);
		}
	}
	/**
	 * Called whenever the parser receives a new predict assignment.
	 * @param parserCaller the source of the parser call
	 * @param lectureId the lecture's id
	 * @param className the class name of the method
	 * @param methodName the name of the method
	 * @param methodId the method's id
	 * @param parameterCount the number of parameters
	 * @param parameterNames the names of the parameters
	 */
	private void fireOnNewMethodPredict(ParserCaller parserCaller, int lectureId, 
			String className, String methodName, int methodId, int parameterCount,
			String[] parameterNames) {
		for (ProtocolParserListener listener : listeners) {
			listener.onNewPredictMethod(this, parserCaller, lectureId, className,
					methodName, methodId, parameterCount, parameterNames);
		}
	}
	/**
	 * Called whenever the parser receives a result for a predict assignment.
	 * @param parserCaller the source of the parser call
	 * @param lectureId the lecture's id
	 * @param methodId the method's id
	 * @param parameterCount the number of parameters
	 * @param parameterNames the parameters' names
	 * @param parameterValues the parameters' values
	 */
	private void fireOnPredictResult(ParserCaller parserCaller, int lectureId, 
			int methodId, int parameterCount, String[] parameterNames,
			String[] parameterValues) {
		for (ProtocolParserListener listener : listeners) {
			listener.onPredictResult(this, parserCaller, lectureId, methodId,
					parameterCount, parameterNames, parameterValues);
		}		
	}
	/**
	 * Generates an xml-message that can be sent to inform other parsers of a user logging
	 * in.
	 * @param user the user that logs in
	 * @return the formatted xml-String
	 */
	public String generateUserLogin(User user, int lectureId) {
		return xmlHeader + "\n" + 
		startActionBegin + "userLogin\"" + end + "\n" + 
		startLectureId + lectureId + endLectureId + "\n" +
		startUserName + user.getName() + endUserName + "\n" +
		startUserId + user.getId() + endUserId + "\n" + 
		endAction;
	}
	/**
	 * Generates an xml-message that can be sent to inform other parsers of a user that
	 * logged in.
	 * @param user the user that logged in
	 * @return the formatted xml-String
	 */
	public String generateUserLoggedIn(User user, Lecture lecture) {
		return xmlHeader + "\n" + 
		startActionBegin + "userLoggedIn\"" + end + "\n" + 
		startLectureId + lecture.getId() + endLectureId + "\n" +
		startUserName + user.getName() + endUserName + "\n" +
		startUserId + user.getId() + endUserId + "\n" + 
		endAction;
	}
	/**
	 * Generates an xml-message that can be sent to inform other parsers of a user logging
	 * out.
	 * @param user the user that logs out
	 * @return the formatted xml-String
	 */
	public String generateUserLogout(User user, Lecture lecture) {
		return xmlHeader + "\n" + 
		startActionBegin + "userLogout\"" + end + "\n" + 
		startLectureId + lecture.getId() + endLectureId + "\n" +
		startUserId + user.getId() + endUserId + "\n" + 
		endAction;
	}
	/**
	 * Generates an xml-message that can be sent to inform other parsers of a user that
	 * logged out.
	 * @param user the user that logged out
	 * @return the formatted xml-String
	 */
	public String generateUserLoggedOut(User user, Lecture lecture) {
		String result = xmlHeader + "\n" + 
		startActionBegin + "userLoggedOut\"" + end + "\n";
		if (lecture != null) {
			result += startLectureId + lecture.getId() + endLectureId + "\n";
		}
		result += startUserId + user.getId() + endUserId + "\n" + 
		endAction;
		return result;
	}
	/**
	 * Generates an xml-message that can be sent to inform other parsers of a result
	 * handed in by a user.
	 * @param user the user that logs in
	 * @param method the method the user wants to hand in
	 * @return the formatted xml-String
	 */
	public String generatePredictHandIn(User user, Lecture lecture) {
		Method method = lecture.getMethod();
		String result = xmlHeader + "\n";
		result += startActionBegin + "predictHandin\"" + end + "\n";
		result += startLectureId + lecture.getId() + endLectureId + "\n";
		result += startUserId + user.getId() + endUserId + "\n";
		result += startMethodId + method.getId() + endMethodId + "\n";
		result += startParameterCount + method.getParameters().size() + endParameterCount + "\n";
		for (Parameter parameter : method.getParameters()) {
			result += startParameterStart + parameter.getName() + "\"" + end + parameter.getPredictedValueForUser(user) + endParameter + "\n";
		}
		result += endAction;
		return result;
	}
	/**
	 * Generates an xml-message to send for a new predict assignment.
	 * @param method the method to predict
	 * @return the formatted xml-String
	 */
	public String generateNewMethodPredict(Lecture lecture) {
		Method method = lecture.getMethod();
		String result = xmlHeader + "\n";
		result += startActionBegin + "predictSendout\"" + end + "\n";
		result += startLectureId + lecture.getId() + endLectureId + "\n";
		result += startClassName + method.getClassName() + endClassName + "\n";
		result += startMethodName + method.getMethodName() + endMethodName + "\n";
		result += startMethodId + method.getId() + endMethodId + "\n";
		result += startParameterCount + method.getParameters().size() + endParameterCount + "\n";
		for (Parameter parameter : method.getParameters()) {
			result += startParameterStart + parameter.getName() + "\"" + end + endParameter + "\n";
		}
		result += endAction;
		return result;
	}
	/**
	 * Generates a formatted xml-String which represents the result for a prediction.
	 * @param method the method that was predicted
	 * @return the formatted xml-String
	 */
	public String generatePredictResult(Lecture lecture) {
		Method method = lecture.getMethod();
		String result = xmlHeader + "\n";
		result += startActionBegin + "predictResult\"" + end + "\n";
		result += startLectureId + lecture.getId() + endLectureId + "\n";
		result += startMethodId + method.getId() + endMethodId + "\n";
		result += startParameterCount + method.getParameters().size() + endParameterCount + "\n";
		for (Parameter parameter : method.getParameters()) {
			result += startParameterStart + parameter.getName() + "\"" + end + parameter.getActualValue() + endParameter + "\n";
		}
		result += endAction;
		return result;
	}
	public String generateLecture(Lecture lecture) {
		String result = xmlHeader + "\n";
		result += startActionBegin + "lecture\"" + end + "\n";
		result += startLectureId + lecture.getId() + endLectureId + "\n";
		result += startLectureName + lecture.getName() + endLectureName + "\n";
		result += endAction;
		if (lecture.getMethod() != null) {
			result += "\n" + this.generateNewMethodPredict(lecture);
		}
		return result;
	}

	public String generateLectureQuery() {
		String result = xmlHeader + "\n";
		result += startActionBegin + "lectureQuery\"" + end + "\n";
		result += endAction;
		return result;
	}

	public String generateLectureList(Collection<Lecture> lectures) {
		String result = xmlHeader + "\n";
		result += startActionBegin + "lectureList\"" + end + "\n";
		result += "<lectureList length=\"" + lectures.size() + "\">\n";
		for (Lecture lecture : lectures) {
			result += ProtocolParser.lectureBegin + "id=\"" + lecture.getId() +
			"\" name=\"" + lecture.getName() + "\"" +	ProtocolParser.endShortTag + "\n";
		}
		result +=  "</lectureList>\n";
		result += endAction;
		return result;
	}

	public String generateUserList(Vector<User> users, int lectureId) {
		String result = xmlHeader + "\n";
		result += startActionBegin + "userList\"" + end + "\n";
		result += startLectureId + lectureId + endLectureId + "\n";
		result += "<userList length=\"" + users.size() + "\" lectureId=\"" + lectureId + "\">\n";
		for (User user : users) {
			result += ProtocolParser.userBegin + "id=\"" + user.getId() +
			"\" name=\"" + user.getName() + "\"" +	endShortTag + "\n";
		}
		result +=  "</userList>\n";
		result += endAction;
		return result;
	}
}
