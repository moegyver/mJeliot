package ict.server;

import ict.server.gui.ServerUI;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;

/**
 * @author Moritz Rogalli
 * The Server listens for incoming TCP connections on the default port defined by 
 * ict.server.Defaults.PORT. It starts ServerThreads for every incoming connection.
 * This class is usually called by its main-method.
 */
public class Server {
	
	/**
	 * The started threads for established connections. Kept to be able to close
	 * all connections on exit.
	 */
	private Vector<ServerThread> serverThreads = new Vector<ServerThread>();
	/**
	 * The controller acts on inputs and keeps the state of the server.
	 */
	private ServerController controller = new ServerController(this);
	private boolean stop = false;
	private ServerSocket socket = null;

	/**
	 * Creates a new Server and binds to the default port.
	 */
	public Server() {
		Thread ui = new Thread(new ServerUI(this.controller));
		ui.start();
        try {
            socket = new ServerSocket(Defaults.PORT);
        } catch (IOException e) {
            System.err.println("Could not listen on port: " + Defaults.PORT + ".");
            System.exit(1);
        }
        while (!this.stop) {
            Socket clientSocket = null;
            try {
                clientSocket = socket.accept();
            } catch (IOException e) {
                System.err.println("Accept failed.");
                System.exit(1);
            }
            ServerThread serverThread = new ServerThread(this, clientSocket, controller);
            this.serverThreads.add(serverThread);
            new Thread(serverThread).start();
        }
	}
	
	/**
	 * Main method to start the Server.
	 * @param args arguments, not used
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		new Server();
	}

	/**
	 * Closes the open sockets and shuts the server down. 
	 */
	public void shutdown() {
		this.stop = true;
		for (int i = 0; i < this.serverThreads.size(); i++) {
		//for (ServerThread serverThread : this.serverThreads) {
			this.serverThreads.get(i).disconnectClient();
		}
		try {
			this.socket.close();
		} catch (IOException e) {
			System.err.println("Failed to close the server-socket. Exiting anyway...");
			System.exit(1);
		}
		System.out.println("Exiting server...");
		System.exit(0);
	}

	/**
	 * @return the serverThreads that are connected to a client.
	 */
	protected Vector<ServerThread> getServerThreads() {
		return this.serverThreads;
	}

	/**
	 * removes a serverThread from the list. This should only be called from a serverThread
	 * to remove itself after closing the connection.
	 * @param serverThread
	 */
	protected void removeServerThread(ServerThread serverThread) {
		this.serverThreads.remove(serverThread);
	}

	public ServerController getController() {
		return this.controller;
	}
}
