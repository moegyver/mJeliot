package ict.test;

import ict.model.User;
import ict.protocol.ParserCaller;
import ict.protocol.ProtocolParser;
import ict.protocol.ProtocolParserListener;

/**
 * @author Moritz Rogalli
 * Tests the functionality of the parser by calling it with reference messages and waiting
 * for the correct reply through the listener methods.
 */
public class ProtocolParserTester implements ProtocolParserListener, ParserCaller {
	
	private ProtocolParser parser = null;
	private static String login = "<?xml version=\"1.0\"?>\n" +
		"<ict action=\"userLogin\">\n" +
		"<lectureId>123456</lectureId>\n" + 
		"<userName>Moe</userName>\n" +
		"<userId>123456</userId>\n" +
		"<address>www.heise.de</address>\n" +
		"</ict>";
	private static String loggedIn = "<?xml version=\"1.0\"?>\n" +
	"<ict action=\"userLoggedIn\">\n" +
	"<lectureId>123456</lectureId>\n" +
	"<userName>Moe</userName>\n" +
	"<userId>123456</userId>\n" +
	"</ict>";
	private static String logout = "<?xml version=\"1.0\"?>\n" +
	"<ict action=\"userLogout\">\n" +
	"<lectureId>123456</lectureId>\n" +
	"<userId>123456</userId>\n" +
	"</ict>";
	private static String loggedOut = "<?xml version=\"1.0\"?>\n" +
	"<ict action=\"userLoggedOut\">\n" +
	"<lectureId>123456</lectureId>\n" +
	"<userId>123456</userId>\n" +
	"</ict>";
	private static String handin = "<?xml version=\"1.0\"?>\n" +
	"<ict action=\"predictHandin\">\n" +
	"<lectureId>123456</lectureId>\n" +
	"<userId>123456</userId>\n" +
	"<methodId>654321</methodId>\n" +
	"<parameterCount>2</parameterCount>\n" +
	"<parameter name=\"a\">1</parameter>\n" +
	"<parameter name=\"b\">2</parameter>\n" +
	"</ict>";
	private static String sendout = "<?xml version=\"1.0\"?>\n" +
	"<ict action=\"predictSendout\">\n" +
	"<lectureId>123456</lectureId>\n" +
	"<className>Foo</className>\n" +
	"<methodName>bar</methodName>\n" +
	"<methodId>654321</methodId>\n" +
	"<parameterCount>2</parameterCount>\n" +
	"<parameter name=\"a\" />\n" +
	"<parameter name=\"b\" />\n" +
	"</ict>";
	private static String result = "<?xml version=\"1.0\"?>\n" +
	"<ict action=\"predictResult\">\n" +
	"<lectureId>123456</lectureId>\n" +
	"<methodId>654321</methodId>\n" +
	"<parameterCount>2</parameterCount>\n" +
	"<parameter name=\"a\">1</parameter>\n" +
	"<parameter name=\"b\">2</parameter>\n" +
	"</ict>";
	private static String lecture = "<?xml version=\"1.0\"?>\n" +
	"<ict action=\"lecture\">\n" +
	"<lectureId>123456</lectureId>\n" +
	"<lectureName>654321</lectureName>\n" +
	"</ict>";
	private static String lectureList = "<?xml version=\"1.0\"?>\n" +
	"<ict action=\"lectureList\">\n" +
	"<lectureList length=\"2\">" + 
	"<lecture id=\"123456\" name=\"name1\" />\n" +
	"<lecture id=\"123458\" name=\"name2\" />\n" +
	"</lectureList>" +
	"</ict>";
	private static String userList = "<?xml version=\"1.0\"?>\n" +
	"<ict action=\"userList\">\n" +
	"<userList length=\"2\">" + 
	"<user id=\"123456\" name=\"name1\" />\n" +
	"<user id=\"123458\" name=\"name2\" />\n" +
	"</userList>" +
	"</ict>";
	private static String lectureQuery = "<?xml version=\"1.0\"?>\n" +
	"<ict action=\"lectureQuery\">\n" +
	"</ict>";
	public ProtocolParserTester() {
		this.parser = new ProtocolParser();
		this.parser.addProtocolParserListener(this);
		System.out.println(login);
		this.parser.parseMessage(login, this);
		this.parser.parseMessage(loggedIn, this);
		this.parser.parseMessage(logout, this);
		this.parser.parseMessage(loggedOut, this);
		this.parser.parseMessage(handin, this);
		this.parser.parseMessage(sendout, this);
		this.parser.parseMessage(result, this);
		this.parser.parseMessage(lecture, this);
		this.parser.parseMessage(lectureList, this);
		this.parser.parseMessage(lectureQuery, this);
		this.parser.parseMessage(userList, this);
	}
	public static void main(String[] args) {
		new ProtocolParserTester();
	}
	@Override
	public void onLogin(ProtocolParser protocolParser, ParserCaller parserCaller,
			int lectureId, String userName, int userId) {
		System.out.println("action=login, user=" + userName + ", userId=" + userId);
	}
	@Override
	public void onLoggedIn(ProtocolParser protocolParser, ParserCaller parserCaller,
			int lectureId, String userName, int userId) {
		System.out.println("action=loggedIn, user=" + userName + ", userId=" + userId);
	}
	@Override
	public void onUserLogout(ProtocolParser protocolParser, ParserCaller parserCaller,
			int lectureId, int userId) {
		System.out.println("action=logout, userId=" + userId);		
	}
	@Override
	public void onUserLoggedOut(ProtocolParser protocolParser, ParserCaller parserCaller,
			int lectureId, int userId) {
		System.out.println("action=loggedOut, userId=" + userId);
		
	}
	@Override
	public void onUserHandedInMethod(ProtocolParser protocolParser,
			ParserCaller parserCaller, int lectureId, int userId, int methodId,
			int parameterCount, String[] parameterNames,
			String[] predictedValues) {
		System.out.println("action=handedIn, userId=" + userId + ", methodId=" + methodId + ", parameterCount=" + parameterCount + ", parameter1: name=" + parameterNames[0] + ", value=" + predictedValues[0] + ", parameter2: name=" + parameterNames[1] + ", value=" + predictedValues[1]);
	}
	@Override
	public void onNewPredictMethod(ProtocolParser protocolParser,
			ParserCaller parserCaller, int lectureId, String className, String methodName,
			int methodId, int parameterCount, String[] parameterNames) {
		System.out.println("action=sentOut, className=" + className + ", methodName=" + methodName + ", methodId=" +  methodId + "parameterCount=" + parameterCount + ", parameter1: name=" + parameterNames[0] + ", parameter2: name=" + parameterNames[1]);	
	}
	@Override
	public void onPredictResult(ProtocolParser protocolParser, ParserCaller parserCaller,
			int lectureId, int methodId, int parameterCount, String[] parameterNames,
			String[] parameterValues) {
		System.out.println("action=sentResult, methodId=" + methodId + ", parameterCount=" + parameterCount + ", parameter1: name=" + parameterNames[0] + ", value=" + parameterValues[0] + ", parameter2: name=" + parameterNames[1] + ", value=" + parameterValues[1]);
	}
	@Override
	public void sendMessage(String message) {
	}
	@Override
	public User getUser() {
		return null;
	}
	@Override
	public void setUser(User user) {
	}
	@Override
	public void onNewLecture(ProtocolParser protocolParser,
			ParserCaller parserCaller, int lectureId, String lectureName) {
		System.out.println("action=lecture, lectureId=" + lectureId + ", lectureName=" + 
				lectureName);
	}
	@Override
	public void onLectureQuery(ProtocolParser protocolParser,
			ParserCaller parserCaller) {
		System.out.println("action=lectureQuery");
	}
	@Override
	public void onLectureList(ProtocolParser protocolParser,
			ParserCaller parserCaller, int lectureCount, int[] lectureIds,
			String[] lectureNames) {
		System.out.print("action=lectureList, lectureCount=" + lectureCount);
		for (int i = 0; i < lectureCount; i++) {
			System.out.print(", lectureId=" + lectureIds[i] + ", lectureName=" + lectureNames[i]);
		}
		System.out.println();
	}
	@Override
	public void onUserList(ProtocolParser protocolParser,
			ParserCaller parserCaller, int lectureId, int userCount, int[] userIds,
			String[] userNames) {
		System.out.print("action=userList, userCount=" + userCount + ", lectureId=" + lectureId);
		for (int i = 0; i < userCount; i++) {
			System.out.print(", userId=" + userIds[i] + ", userName=" + userNames[i]);
		}
		System.out.println();	
	}
}
