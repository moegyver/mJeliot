package jeliot.ict;

import ict.model.predict.Method;
import ict.model.Lecture;

/**
 * @author Moritz Rogalli
 * An ICTControllerListener gets informed if properly registered to the controller on
 * events created by the ICTController. 
 */
public interface ICTControllerListener {
	/**
	 * Called when a controller's client is connected.
	 * @param ictController the controller
	 */
	public void onClientConnected(ICTController ictController);
	/**
	 * Called when a controller's client is disconnected.
	 * @param ictController the controller
	 */
	public void onClientDisconnected(ICTController ictController);
	/**
	 * Gets called when a new user is added to the Controller.
	 * @param ictController the controller the user got added to
	 */
	public void onUserCountChanged(ICTController ictController);
	/**
	 * Gets called whenever the number of received answers changes.
	 * @param ictController the controller
	 */
	public void onAnswerCountChanged(ICTController ictController);
	/**
	 * Gets called whenever there is a new assignment received by the ICTController.
	 * @param ictController the controller
	 * @param method the method that got assigned
	 */
	public void onNewMethod(ICTController ictController, Method method);
	/**
	 * Gets called whenever the result for the current assignment gets posted.
	 * @param ictController the controller
	 * @param method the method containing the result
	 */
	public void onResultPosted(ICTController ictController, Method method);
	/**
	 * Gets called when a method is called during Jeliot's program execution
	 * @param ictController the controller
	 * @param method the method called
	 */
	public void onMethodCalled(ICTController ictController, Method method);
	
	/**
	 * Gets called when a method returns during Jeliot's program execution
	 * @param ictController the controller
	 * @param method the returning message
	 */
	public void onMethodReturned(ICTController ictController, Method method);
	public void onNewLecture(ICTController ictController, Lecture lecture);
	public void onLogin(ICTController ictController, Lecture lecture);
	public void onLoggedIn(ICTController ictController, Lecture currentLecture);
	public void onLogout(ICTController ictController, Lecture lecture);
	public void onLectureUpdated(ICTController ictController, Lecture lecture);
	public void onLoggedOut(ICTController ictController, Lecture lecture);
}
