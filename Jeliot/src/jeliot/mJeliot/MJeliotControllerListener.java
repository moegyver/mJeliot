package jeliot.mJeliot;

import org.mJeliot.model.Lecture;
import org.mJeliot.model.User;
import org.mJeliot.model.coding.CodingTask;
import org.mJeliot.model.predict.Method;

/**
 * @author Moritz Rogalli
 * An MJeliotControllerListener gets informed if properly registered to the controller on
 * events created by the MJeliotController. 
 */
public interface MJeliotControllerListener {
	/**
	 * Called when a controller's client is connected.
	 * @param mJeliotController the controller
	 */
	public void onClientConnected(MJeliotController mJeliotController, boolean isReconnected);
	/**
	 * Called when a controller's client is disconnected.
	 * @param mJeliotController the controller
	 */
	public void onClientDisconnected(MJeliotController mJeliotController);
	/**
	 * Gets called when a new user is added to the Controller.
	 * @param mJeliotController the controller the user got added to
	 */
	public void onUserCountChanged(MJeliotController mJeliotController);
	/**
	 * Gets called whenever the number of received answers changes.
	 * @param mJeliotController the controller
	 */
	public void onAnswerCountChanged(MJeliotController mJeliotController);
	/**
	 * Gets called whenever there is a new assignment received by the MJeliotController.
	 * @param mJeliotController the controller
	 * @param method the method that got assigned
	 */
	public void onNewMethod(MJeliotController mJeliotController, Method method);
	/**
	 * Gets called whenever the result for the current assignment gets posted.
	 * @param mJeliotController the controller
	 * @param method the method containing the result
	 */
	public void onResultPosted(MJeliotController mJeliotController, Method method);
	/**
	 * Gets called when a method is called during Jeliot's program execution
	 * @param mJeliotController the controller
	 * @param method the method called
	 */
	public void onMethodCalled(MJeliotController mJeliotController, Method method);
	
	/**
	 * Gets called when a method returns during Jeliot's program execution
	 * @param mJeliotController the controller
	 * @param method the returning message
	 */
	public void onMethodReturned(MJeliotController mJeliotController, Method method);
	public void onNewLecture(MJeliotController mJeliotController, Lecture lecture);
	public void onLogin(MJeliotController mJeliotController, Lecture lecture);
	public void onLoggedIn(MJeliotController mJeliotController, Lecture currentLecture);
	public void onLogout(MJeliotController mJeliotController, Lecture lecture);
	public void onLectureUpdated(MJeliotController mJeliotController, Lecture lecture);
	public void onLoggedOut(MJeliotController mJeliotController, Lecture lecture);
	public void onUserLoggedIn(MJeliotController mJeliotController, User user,
			Lecture lecture);
	public void onUserLoggedOut(MJeliotController mJeliotController, User user,
			Lecture lecture);
	public void onCodeUpdate(Lecture lecture, User user, String code,
			int cursorPosition, boolean isDone, boolean requestedAttention);
	public void onCodingTask(MJeliotController mJeliotController,
			CodingTask codingTask);
}
