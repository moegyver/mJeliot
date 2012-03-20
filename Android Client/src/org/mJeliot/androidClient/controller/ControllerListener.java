package org.mJeliot.androidClient.controller;

import org.mJeliot.model.Lecture;

import android.app.Activity;

/**
 * A ControllerListener can be registered to a Controller to get Events whenever the state of the controller changes.
 * @author Moritz Rogalli
 *
 */
public interface ControllerListener {
	/**
	 * Executed when a controller initiates a network scan.
	 * @param controller the controller that initiated the scan
	 */
	public void onScanStart(Controller controller);
	/**
	 * Executed when a controller receives the result of a network scan.
	 * @param controller the controller that initiated the scan
	 */
	public void onScanFinished(Controller controller);
	/**
	 * Executed when a controller initiates a connection to a server.
	 * @param controller the controller that initiates the connection
	 */
	public void onConnect(Controller controller);
	/**
	 * Executed when a controller is connected to a server. 
	 * @param controller the controller that got connected
	 */
	public void onConnected(Controller controller);
	/**
	 * Executed when a controller is logging in.
	 * @param controller the controller that is logging in
	 */
	public void onLoggingIn(Controller controller);
	/**
	 * Executed when a controller is logged in.
	 * @param controller the controller that logged in
	 */
	public void onLoggedIn(Controller controller);
	/**
	 * Executed when a controller is disconnecting from a server.
	 * @param controller the controller that is disconnecting
	 */
	public void onLoggingOut(Controller controller);
	/**
	 * Executed when a controller is logged in.
	 * @param controller the controller that logged in
	 */
	public void onLoggedOut(Controller controller);
	/**
	 * Executed when a controller gets disconnected from a server.
	 * @param controller the controller that got disconnected
	 */
	public void onDisconnected(Controller controller, boolean isForced);
	/**
	 * Executed when a controller receives a new assignment.
	 * @param controller the controller that received the assignment.
	 */
	public void onNewMethod(Controller controller);
	/**
	 * Executed when a controller receives a result.
	 * @param controller the controller that got the result.
	 */
	public void onResult(Controller controller);
	
	/**
	 * Executed when a controller changed its activity.
	 * @param controller the controller that changed activity
	 * @param activity the activity the controller changed to
	 */
	public void onCurrentActivityChanged(Controller controller, Activity activity);
	
	/**
	 * Executed when a controller got a new lecture.
	 * @param controller the controller that got the new lecture
	 * @param lecture the received lecture
	 */
	public void onNewLecture(Controller controller, Lecture lecture);
	public void onCodingTask(Controller controller, String code);
}
