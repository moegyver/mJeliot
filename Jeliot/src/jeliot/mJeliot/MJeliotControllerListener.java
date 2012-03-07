package jeliot.mJeliot;

import org.mJeliot.model.Lecture;
import org.mJeliot.model.predict.Method;

/**
 * @author Moritz Rogalli
 * An MJeliotControllerListener gets informed if properly registered to the controller on
 * events created by the MJeliotController. 
 */
public interface MJeliotControllerListener {
	/**
	 * Called when a controller's client is connected.
	 * @param ictController the controller
	 */
	public void onClientConnected(MJeliotController ictController);
	/**
	 * Called when a controller's client is disconnected.
	 * @param ictController the controller
	 */
	public void onClientDisconnected(MJeliotController ictController);
	/**
	 * Gets called when a new user is added to the Controller.
	 * @param ictController the controller the user got added to
	 */
	public void onUserCountChanged(MJeliotController ictController);
	/**
	 * Gets called whenever the number of received answers changes.
	 * @param ictController the controller
	 */
	public void onAnswerCountChanged(MJeliotController ictController);
	/**
	 * Gets called whenever there is a new assignment received by the MJeliotController.
	 * @param ictController the controller
	 * @param method the method that got assigned
	 */
	public void onNewMethod(MJeliotController ictController, Method method);
	/**
	 * Gets called whenever the result for the current assignment gets posted.
	 * @param ictController the controller
	 * @param method the method containing the result
	 */
	public void onResultPosted(MJeliotController ictController, Method method);
	/**
	 * Gets called when a method is called during Jeliot's program execution
	 * @param ictController the controller
	 * @param method the method called
	 */
	public void onMethodCalled(MJeliotController ictController, Method method);
	
	/**
	 * Gets called when a method returns during Jeliot's program execution
	 * @param ictController the controller
	 * @param method the returning message
	 */
	public void onMethodReturned(MJeliotController ictController, Method method);
	public void onNewLecture(MJeliotController ictController, Lecture lecture);
	public void onLogin(MJeliotController ictController, Lecture lecture);
	public void onLoggedIn(MJeliotController ictController, Lecture currentLecture);
	public void onLogout(MJeliotController ictController, Lecture lecture);
	public void onLectureUpdated(MJeliotController ictController, Lecture lecture);
	public void onLoggedOut(MJeliotController ictController, Lecture lecture);
}
