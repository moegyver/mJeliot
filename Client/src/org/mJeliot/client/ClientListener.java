package org.mJeliot.client;

import org.mJeliot.model.Lecture;
import org.mJeliot.model.User;

/**
 * @author Moritz Rogalli
 * A ClientListener can be registered to a client and gets event notifications when the
 * client's state changes and when a message is received.
 */
public interface ClientListener {
	/**
	 * Called when a client receives a message.
	 * @param client The client
	 * @param message The received message
	 */
	public void onMessageReceived(Client client, String message);
	/**
	 * Called when a client gets disconnected.
	 * @param client The client
	 * @param isIntentional 
	 * @param isForced 
	 */
	public void onClientDisconnected(Client client, boolean isIntentional, boolean isForced);
	/**
	 * Called when a client gets connected.
	 * @param client The client
	 */
	public void onClientConnected(Client client, boolean isReconnected);
	
	/**
	 * Returns the user associated with the listener
	 * @return 
	 */
	public User getUser();
	/**
	 * Returns the lecture associated with the listener
	 * @return
	 */
	public Lecture getLecture();
	
	public boolean isNetworkReady();
}
