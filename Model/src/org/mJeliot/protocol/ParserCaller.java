package org.mJeliot.protocol;

import org.mJeliot.model.User;

/**
 * @author Moritz Rogalli
 * A class that wants to call a parser has to implement. This is done to make direct
 * answers to the source of the parsed message possible.
 */
public interface ParserCaller {
	/**
	 * Sends an answer back to the source. The ParserCaller has to make sure that the
	 * message reaches its destination.
	 * @param message
	 */
	public void sendMessage(String message);
	/**
	 * Gets the user associated with the source of the call.
	 * @return the user
	 */
	public User getUser();
	/**
	 * Enables us to set the user associated with the source. 
	 * @param user the user we want to associate
	 */
	public void setUser(User user);
}
