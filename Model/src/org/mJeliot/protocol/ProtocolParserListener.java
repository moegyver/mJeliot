package org.mJeliot.protocol;

/**
 * @author Moritz Rogalli A ProtocolParserListener can be registered as a
 *         listener to a ProtocolParser. The parser then calls the
 *         eventHandlers.
 */
public interface ProtocolParserListener {
	/**
	 * Gets called when a parser receives a lecture query message.
	 * 
	 * @param protocolParser
	 *            the parser that parsed the message
	 * @param parserCaller
	 *            the source of the parser call
	 */
	public void onLectureQuery(ProtocolParser protocolParser,
			ParserCaller parserCaller);

	public void onLectureList(ProtocolParser protocolParser,
			ParserCaller parserCaller, int lectureCount, int[] lectureIds,
			String[] lectureNames);

	/**
	 * Gets called when a parser receives a new lecture message.
	 * 
	 * @param protocolParser
	 *            the parser that parsed the message
	 * @param parserCaller
	 *            the source of the parser call
	 * @param lectureId
	 *            the lecture's id
	 * @param lectureName
	 *            the lecture's name
	 */
	public void onNewLecture(ProtocolParser protocolParser,
			ParserCaller parserCaller, int lectureId, String lectureName);

	/**
	 * Gets called when a parser receives a login message.
	 * 
	 * @param protocolParser
	 *            the parser that parsed the message
	 * @param parserCaller
	 *            the source of the parser call
	 * @param lectureId
	 *            the lecture's id
	 * @param userName
	 *            the user's alias
	 * @param userId
	 *            the user's id
	 */
	public void onLogin(ProtocolParser protocolParser,
			ParserCaller parserCaller, int lectureId, String userName,
			int userId);

	/**
	 * Gets called when a parser receives a logged in message.
	 * 
	 * @param protocolParser
	 *            the parser that parsed the message
	 * @param parserCaller
	 *            the source of the parser call
	 * @param lectureId
	 *            the lecture's id
	 * @param userName
	 *            the user's alias
	 * @param userId
	 *            the user's id
	 */
	public void onLoggedIn(ProtocolParser protocolParser,
			ParserCaller parserCaller, int lectureId, String userName,
			int userId);

	/**
	 * Gets called when a parser receives a logout message.
	 * 
	 * @param protocolParser
	 *            the parser that parsed the message
	 * @param parserCaller
	 *            the source of the parser call
	 * @param lectureId
	 *            the lecture's id
	 * @param userId
	 *            the user's id
	 */
	public void onUserLogout(ProtocolParser protocolParser,
			ParserCaller parserCaller, int lectureId, int userId);

	/**
	 * Gets called when a parser receives a logged out message.
	 * 
	 * @param protocolParser
	 *            the parser that parsed the message
	 * @param parserCaller
	 *            the source of the parser call
	 * @param userId
	 *            the user's id
	 */
	public void onUserLoggedOut(ProtocolParser protocolParser,
			ParserCaller parserCaller, int lectureId, int userId);

	/**
	 * Gets called when a parser receives a hand-in for an assignment
	 * 
	 * @param protocolParser
	 *            the parser that parsed the message
	 * @param parserCaller
	 *            the source of the parser call
	 * @param lectureId
	 *            the lecture's id
	 * @param userId
	 *            the user's id
	 * @param methodId
	 *            the method's id
	 * @param parameterCount
	 *            the number of parameters
	 * @param parameterNames
	 *            the parameters' names
	 * @param predictedValues
	 *            the user's predictions for the parameters
	 */
	public void onUserHandedInMethod(ProtocolParser protocolParser,
			ParserCaller parserCaller, int lectureId, int userId, int methodId,
			int parameterCount, String[] parameterNames,
			String[] predictedValues);

	/**
	 * Gets called when a parser receives a new assignment.
	 * 
	 * @param protocolParser
	 *            the parser that parsed the message
	 * @param parserCaller
	 *            the source of the parser call
	 * @param lectureId
	 *            the lecture's id
	 * @param className
	 *            the method's class name
	 * @param methodName
	 *            the method's name
	 * @param methodId
	 *            the method's id
	 * @param parameterCount
	 *            the number of parameters
	 * @param parameterNames
	 *            the parameters' names
	 */
	public void onNewPredictMethod(ProtocolParser protocolParser,
			ParserCaller parserCaller, int lectureId, String className,
			String methodName, int methodId, int parameterCount,
			String[] parameterNames);

	/**
	 * Gets called when a parser receives a result for a predict assignment.
	 * 
	 * @param protocolParser
	 *            the parser that parsed the message
	 * @param parserCaller
	 *            the source of the parser call
	 * @param lectureId
	 *            the lecture's id
	 * @param methodId
	 *            the method's id
	 * @param parameterCount
	 *            the number of parameters
	 * @param parameterNames
	 *            the parameters' names
	 * @param parameterValues
	 *            the parameters' values
	 */
	public void onPredictResult(ProtocolParser protocolParser,
			ParserCaller parserCaller, int lectureId, int methodId,
			int parameterCount, String[] parameterNames,
			String[] parameterValues);

	public void onUserList(ProtocolParser protocolParser,
			ParserCaller parserCaller, int lectureId, int userCount,
			int[] userIds, String[] userNames);

	public void onCodeUpdate(ProtocolParser protocolParser,
			ParserCaller parserCaller, int lectureId, int userId,
			String code, int cursorPosition, boolean done,
			boolean requestedAttention, int destUserId);

	public void onCodingTask(ProtocolParser protocolParser,
			ParserCaller parserCaller, int lectureId, int from,
			String unescapedCode);
}
