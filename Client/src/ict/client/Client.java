package ict.client;

import ict.model.User;
import ict.server.Defaults;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Vector;

/**
 * @author Moritz Rogalli
 * A client is a basic class to manage a connection with the server. It passes all
 * received messages on to its listeners as soon as they are complete and informs the
 * listeners when the state of the connection changes. It does not parse the messages.
 */
public class Client implements Runnable {
	/**
	 * The actual socket that keeps the connection with the server.
	 */
	private Socket socket = null;
	/**
	 * The listeners are informed whenever the client receives a message or the state
	 * of the socket changes.
	 */
	private Vector<ClientListener> listeners = new Vector<ClientListener>();
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
	 * The user associated with the client for user-specific messages.
	 */
	private User user = null;
	/**
	 * The server's uri. Usually defined by the Defaults class.
	 */
	private String uri = null;
	/**
	 * @param uri The uri to use to connect the socket.
	 */
	public Client(String uri) {
		this.uri = uri;
	}
	/**
	 * For adding a ClientListener that should be informed when a message is received
	 * or the Client state changes.
	 * @param clientListener the listener that should be added
	 */
	public void addClientListener(ClientListener clientListener) {
		if (!listeners.contains(clientListener)) {
			this.listeners.add(clientListener);
		}
	}
	/**
	 * This function removes a Listener so that it no longer receives notifications.
	 * @param clientListener listener to remove.
	 */
	public void removeClientListener(ClientListener clientListener) {
		this.listeners.remove(clientListener);
	}
	/**
	 * Passes a message on to the server. This function does not check any kind of state
	 * information. Calling classes have to make sure the connection is established before
	 * sending.
	 * @param message the message to send
	 */
	public void sendMessage(String message) {
		System.out.println(message);
		this.out.write(message);
		this.out.flush();
	}
	/**
	 * Gets called every time the inputThread receives a message. Helper function to stick
	 * to the ObserverPattern. All it does is calling the fireOnMessageReceived function.
	 * @param message The received message
	 */
	protected void receive(String message) {
		this.fireOnMessageReceived(message);
	}
	/**
	 * Informs all listeners when a message is received.
	 * @param message
	 */
	private void fireOnMessageReceived(String message) {
		synchronized (this.listeners) {
			for (ClientListener listener : this.listeners) {
				listener.onMessageReceived(this, message);
			}
		}
	}
	/**
	 * Connects the socket, handles Exceptions that might occur on establishing the 
	 * connection and initialises the inputThread. 
	 */
	public void connect() {
		try {
			this.socket = new Socket(this.uri, Defaults.PORT);
			this.out = new PrintWriter(socket.getOutputStream(), true);
		    this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		} catch (UnknownHostException e) {
			this.disconnect();
			return;
		} catch (IOException e) {
			this.disconnect();
			return;
		}
		this.inputThread = new InputThread(this, in);
		Thread thread = new Thread(this.inputThread);
		thread.start();
		this.fireOnClientConnected();
	}
	/**
	 * Informs the listeners when the connection is established.
	 */
	private void fireOnClientConnected() {
		synchronized (this.listeners) {
			for (ClientListener listener : this.listeners) {
				listener.onClientConnected(this);
			}
		}
	}
	/**
	 * Disconnects the socket, closes all the open resources
	 */
	public void disconnect() {
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
		}
		fireOnClientDisconnected();
	}
	
	/**
	 * Informs all the listeners when the connection is no longer available.
	 */
	private void fireOnClientDisconnected() {
		synchronized (this.listeners) {
			for (ClientListener listener : this.listeners) {
				listener.onClientDisconnected(this);
			}
		}
	}
	/**
	 * Sets the user associated with the Client. Might be needed by the listeners to build
	 * user-specific messages.
	 * @param user the user associated with the client
	 */
	public void setUser(User user) {
		this.user = user;
	}

	/**
	 * Returns the user associated to the Client.
	 * @return a valid user if set, null otherwise
	 */
	public User getUser() {
		return user;
	}

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
	}
	/**
	 * Returns the state of the client.
	 * @return true if the socket is connected, false otherwise
	 */
	public boolean isConnected() {
		return this.socket.isConnected();
	}
}
