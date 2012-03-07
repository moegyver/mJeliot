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

	/**
	 * @param uri The uri to use to connect the socket.
	 */
	public Client(ClientListener listener, String uri) {
		this.uri = uri;
		this.clientListener = listener;
		System.out.println("new client created");
	    //Timer timer = new Timer();
	    //timer.schedule(new PingGenerator(this), 5 * 1000);
	}
	/**
	 * Passes a message on to the server. This function does not check any kind of state
	 * information. Calling classes have to make sure the connection is established before
	 * sending.
	 * @param message the message to send
	 */
	public void sendMessage(String message) {
		System.out.println("new message to send: " + message);
		this.out.write(message);
		this.out.flush();
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
	public void connect() {
		try {
			System.out.println("trying to connect the client: " + this);
			this.socket = new Socket(this.uri, Defaults.PORT);
			this.out = new PrintWriter(socket.getOutputStream(), true);
		    this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		} catch (UnknownHostException e) {
			System.out.println("error connecting client: " + e.getMessage());
			this.disconnect();
			return;
		} catch (IOException e) {
			System.out.println("error connecting client: " + e.getMessage());
			this.disconnect();
			return;
		}
		System.out.println("new input thread starting");
		this.inputThread = new InputThread(this, in);
		Thread thread = new Thread(this.inputThread);
		thread.start();
		System.out.println("client connected");
		this.fireOnClientConnected();
	}
	/**
	 * Informs the listeners when the connection is established.
	 */
	private void fireOnClientConnected() {
		clientListener.onClientConnected(this);
	}
	/**
	 * Disconnects the socket, closes all the open resources
	 */
	public void disconnect() {
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
			System.out.println("error disconnecting");
			e.printStackTrace();
		}
		System.out.println("client disconnected");
		fireOnClientDisconnected();
	}
	
	/**
	 * Informs all the listeners when the connection is no longer available.
	 */
	private void fireOnClientDisconnected() {
		clientListener.onClientDisconnected(this);
	}

	/**
	 * Returns the state of the client.
	 * @return true if the socket is connected, false otherwise
	 */
	public boolean isConnected() {
		return this.socket.isConnected();
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
}
