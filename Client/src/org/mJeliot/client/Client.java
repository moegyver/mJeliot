package org.mJeliot.client;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import org.mJeliot.model.Lecture;
import org.mJeliot.model.User;
import org.mJeliot.server.Defaults;

/**
 * @author Moritz Rogalli
 * A client is a basic class to manage a connection with the server. It passes all
 * received messages on to its listeners as soon as they are complete and informs the
 * listeners when the state of the connection changes. It does not parse the messages.
 */
public class Client {
	/**
	 * The actual socket that keeps the connection with the server.
	 */
	private Socket socket = null;
	/**
	 * The listeners are informed whenever the client receives a message or the state
	 * of the socket changes.
	 */
	private ClientListener clientListener = null;
	/**
	 * The input thread takes care of the receiving from the socket and passes messages
	 * on as soon as they are complete.
	 */
	private InputThread inputThread = null;
	/**
	 * The PrintWriter is for sending Strings to the server.
	 */
	private PrintWriter out = null;
	/**
	 * The BufferedReader for the receiving thread.
	 */
	private BufferedReader in = null;
	/**
	 * The server's uri. Usually defined by the Defaults class.
	 */
	private String uri = null;
	private int reconnectCounter = 0;
	private boolean isNetworkReady;
	protected static int RECONNECT_BACKOFF = 1000;

	/**
	 * @param uri The uri to use to connect the socket.
	 */
	public Client(ClientListener listener) {
		this.clientListener = listener;
	}
	/**
	 * Passes a message on to the server. This function does not check any kind of state
	 * information. Calling classes have to make sure the connection is established before
	 * sending.
	 * @param message the message to send
	 */
	public void sendMessage(String message) {
		if (this.out != null) {
		System.out.println("new message to send: " + message);
		this.out.write(message);
		new Thread(new Runnable(){

			@Override
			public void run() {
				out.flush();
			}}).start();
		}
	}
	/**
	 * Gets called every time the inputThread receives a message. Helper function to stick
	 * to the ObserverPattern. All it does is calling the fireOnMessageReceived function.
	 * @param message The received message
	 */
	protected void receive(String message) {
		System.out.println("message received: " + message);
		this.fireOnMessageReceived(message);
	}
	/**
	 * Informs all listeners when a message is received.
	 * @param message
	 */
	private void fireOnMessageReceived(String message) {
			clientListener.onMessageReceived(this, message);
	}
	/**
	 * Connects the socket, handles Exceptions that might occur on establishing the 
	 * connection and initialises the inputThread. 
	 */
	public void connect(final boolean isReconnected) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					System.out.println("trying to connect the client: " + this);
					socket = new Socket(uri, Defaults.PORT);
					out = new PrintWriter(socket.getOutputStream(), false);
				    in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				    reconnectCounter = 0;
				} catch (UnknownHostException e) {
					System.out.println("error connecting client: " + e.getMessage());
					disconnect(false, !isReconnected);
					return;
				} catch (IOException e) {
					System.out.println("error connecting client: " + e.getMessage());
					disconnect(false, !isReconnected);
					return;
				}
				System.out.println("new input thread starting");
				inputThread = new InputThread(Client.this, in);
				Thread thread = new Thread(inputThread);
				thread.start();
				System.out.println("client connected");
				fireOnClientConnected(isReconnected);
			}
		}).start();
	}
	public void reconnect() {
		reconnectCounter++;
		if (reconnectCounter > 4) {
			disconnect(true, true);
		} else {
			new Thread(new Runnable(){
				@Override
				public void run() {
					try {
						Thread.sleep(reconnectCounter * RECONNECT_BACKOFF );
						int i = 0;
						while (!isNetworkReady && i < 4) {
							i++;
							Thread.sleep(reconnectCounter * RECONNECT_BACKOFF);
						}
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					System.out.println("AndroidClient: reconnect");
					connect(true);
				}}).start();
		}
	}
	/**
	 * Informs the listeners when the connection is established.
	 */
	private void fireOnClientConnected(boolean isReconnected) {
		clientListener.onClientConnected(this, isReconnected);
	}
	/**
	 * Disconnects the socket, closes all the open resources
	 */
	public void disconnect(boolean isIntentional, boolean isForced) {
		System.out.println("client disconnecting");
		if (this.inputThread != null) {
			this.inputThread.stop();
		}
		if (this.out != null) {
			this.out.close();
		}
		try {
			this.in.close();
			this.socket.close();
		} catch (Exception e) {
			System.err.println("error disconnecting: " + e.getMessage());
		}
		this.socket = null;
		if (!isIntentional) {
			reconnect();
		}
		fireOnClientDisconnected(isIntentional, isForced);
	}
	
	/**
	 * Informs all the listeners when the connection is no longer available.
	 */
	private void fireOnClientDisconnected(boolean isIntentional, boolean isForced) {
		clientListener.onClientDisconnected(this, isIntentional, isForced);
	}

	/**
	 * Returns the state of the client.
	 * @return true if the socket is connected, false otherwise
	 */
	public boolean isConnected() {
		return this.socket != null && this.socket.isConnected();
	}
	/**
	 * @return the user
	 */
	public User getUser() {
		return this.clientListener.getUser();
	}
	public Lecture getLecture() {
		return this.clientListener.getLecture();
	}
	public void setUri(String url) {
		this.uri = url;
	}
	public boolean hasUri() {
		return this.uri != null;
	}
	public void setNetworkReady(boolean isNetworkReady) {
		this.isNetworkReady = isNetworkReady;
	}
}
