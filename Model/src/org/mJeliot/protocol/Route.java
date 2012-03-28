package org.mJeliot.protocol;

import org.mJeliot.model.Lecture;
import org.mJeliot.model.User;

/**
 * @author Moritz Rogalli
 * Interface to provide functions to route messages and send them
 */
public interface Route {
	/**
	 * Sends an answer back to the source. The Route has to make sure that the
	 * message reaches its destination.
	 * @param message
	 */
	public void sendMessage(String message);

	public Lecture getLecture();

	public User getUser();

	public int getDestination();
}
