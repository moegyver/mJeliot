package org.mJeliot.server;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Timer;

import org.mJeliot.model.User;
import org.mJeliot.protocol.ParserCaller;

/**
 * @author Moritz Rogalli
 * A ServerThread keeps the connection to one of the connected clients. It is also
 * responsible for sending messages to that client.
 */
public class ServerThread implements Runnable, ParserCaller {

	protected static final long PING_INTERVAL = 3000;
	/**
	 * The corresponding Server.
	 */
	private Server server = null;
	/**
	 * The Socket is keeping the actual connection. 
	 */
	private Socket clientSocket = null;
	/**
	 * The corresponding Writer to the clientSocket. 
	 */
	private PrintWriter out = null;
	/**
	 * The corresponding Reader to the Socket. This is used by the ServerInputThread to
	 * listen to messages from the client. 
	 */
	private BufferedReader in = null;
	/**
	 * The controller to parse and create messages.
	 */
	private ServerController controller = null;
	/**
	 * The Thread that listens for incoming messages from the Socket.
	 */
	private ServerInputThread inputThread = null;
	/**
	 * The user associated with the connection
	 */
	private User user = null;
	private long lastPong = System.currentTimeMillis();
	private Timer pingTimer;

	/**
	 * Establishes asynchronous 2-way communication with a client over the socket.
	 * @param clientSocket the socket for the connection
	 * @param controller the controller that handles received messages and sends messages 
	 */
	public ServerThread(Server server, Socket clientSocket, ServerController controller) {
		this.server = server;
		this.controller = controller;
		this.clientSocket = clientSocket;
		try {
			this.out = new PrintWriter(clientSocket.getOutputStream(), true);
		    this.in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
		} catch (IOException e) {
			this.disconnectClient();
			e.printStackTrace();
			return;
		}
		this.inputThread = new ServerInputThread(this, in);
		Thread thread = new Thread(inputThread);
		thread.start();
		pingTimer = new Timer();
		pingTimer.schedule(new PingGenerator(this), 200, PING_INTERVAL);
		pingTimer.scheduleAtFixedRate(new CheckTimeoutTask(this), 200,  PING_INTERVAL);
	}

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
	}

	/**
	 * Disconnects the socket and informs both the Controller and the Server about the
	 * closed connection.
	 */
	protected void disconnectClient() {
		System.out.println("Server: disconnecting client... ");
		if (this.user != null) {
			this.controller.disconnectUser(this.user, this.user.getLecture().getId());
		}
		this.server.removeServerThread(this);
		this.controller.removeServerThread(this);
		try {
			this.out.close();
			this.in.close();
			this.clientSocket.close();
		} catch (IOException e) {
			System.err.println("Failed to close socket.");
		}
		System.out.println("Socket closed.");
		this.pingTimer.cancel();
	}
	
	/* (non-Javadoc)
	 * @see org.mJeliot.protocol.ParserCaller#sendMessage(java.lang.String)
	 */
	public void sendMessage(String message) {
		this.out.write(message);
		this.out.flush();
	}
	
	/**
	 * @return returns the controller
	 */
	protected ServerController getController() {
		return this.controller;
	}

	public void resetTimeout() {
		this.lastPong = System.currentTimeMillis();
		
	}

	/**
	 * @return the lastPong
	 */
	public long getLastPong() {
		return lastPong;
	}

	public void resetUserTimer(int lectureId, int userId) {
		this.controller.resetUserTimer(this, lectureId, userId);
	}
}