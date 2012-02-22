package org.mJeliot.androidClient.controller;

/**
 * @author Moritz Rogalli A ClientListener can be registered to a client and
 *         gets event notifications when the client's state changes and when a
 *         message is received.
 */
public interface AndroidClientListener {
	/**
	 * Called when a client receives a message.
	 * 
	 * @param client
	 *            The client
	 * @param message
	 *            The received message
	 */
	public void onMessageReceived(AndroidClient client, String message);

	/**
	 * Called when a client gets disconnected.
	 * 
	 * @param client
	 *            The client
	 */
	public void onClientDisconnected(AndroidClient client);

	/**
	 * Called when a client gets connected.
	 * 
	 * @param client
	 *            The client
	 */
	public void onClientConnected(AndroidClient client);
}
