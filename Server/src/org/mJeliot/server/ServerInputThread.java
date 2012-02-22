package org.mJeliot.server;


import java.io.BufferedReader;
import java.io.IOException;

import org.mJeliot.protocol.ProtocolParser;

/**
 * @author Moritz Rogalli
 * The ServerInputThread listens on a socket and passes received messages on to its 
 * corresponding ServerThread.
 */
public class ServerInputThread implements Runnable {

	/**
	 * The corresponding ServerThread.
	 */
	private ServerThread serverThread = null;
	/**
	 * The reader from which the messages arrive.
	 */
	private BufferedReader in = null;
	/**
	 * When set to true the Thread stops in the next iteration.
	 */
	private boolean stop = false;

	/**
	 * Creates a ServerInputThread.
	 * @param serverThread the corresponding ServerThread
	 * @param in the Reader from the Socket
	 */
	protected ServerInputThread(ServerThread serverThread, BufferedReader in) {
		this.serverThread = serverThread;
		this.in = in;
	}

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		String message = "";
		while (!stop) {
			String input = "";
			try {
				input = this.in.readLine();
			} catch (IOException e) {
				serverThread.disconnectClient();
				this.stop = true;
				return;
			}
			if (input != null) {
				message += input;
				if (input.contains(ProtocolParser.endActionTag)) {
					serverThread.getController().getParser().parseMessage(message, serverThread);
					message = "";
				} else {
				}
			} else {
				try {
					if (!this.in.ready()) {
						this.serverThread.disconnectClient();
						this.stop = true;
						return;
					}
				} catch (IOException e) {
					this.serverThread.disconnectClient();
					this.stop = true;
					return;
				}
				
			}
		}
	}

}
