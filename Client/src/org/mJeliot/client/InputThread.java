package org.mJeliot.client;


import java.io.BufferedReader;
import java.io.IOException;

import org.mJeliot.protocol.ProtocolParser;

/**
 * @author Moritz Rogalli
 * The InputThread waits for incoming data and passes the data on as soon as the message
 * is complete.
 */
public class InputThread implements Runnable {
	private boolean stop = false;
	private BufferedReader in = null;
	private Client client = null;

	/**
	 * Creates a new InputThread.
	 * @param client the corresponding client
	 * @param in the BufferedReader for incoming data
	 */
	protected InputThread(Client client, BufferedReader in) {
		this.client = client;
		this.in = in;
	}

	/**
	 * Stops the thread in the next iteration.
	 */
	public void stop() {
		this.stop = true;
	}

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		String message = "";
		while (!stop) {
			String line = "";
			try {
				 line = this.in.readLine();
			} catch (IOException e) {
				System.out.println("error receiving data: " + e.getMessage());
				if (!stop) {
					this.client.disconnect(false, false);
					this.stop();
				}
			}
			if (line != null ) {
				message += line + "\n";
				if (line.contains(ProtocolParser.endActionTag)) {
					this.client.receive(message);
					message = "";
				} else if (line.contains("ping")) {
					message = "";
					client.sendPong();
				} else {
				}
			} else {
				System.out.println("got a null message");
				// when null then disconnect
//				try {
//					if (!this.in.ready()) {
//						System.out.println("socket not ready for receiving");
//						if (!stop) {
							this.client.disconnect(false, false);
							this.stop();
//						}
//					}
//				} catch (IOException e) {
//					System.out.println("error receiving: " + e.getMessage());
//					if (!stop) {
//						this.client.disconnect(false, false);
//						this.stop();
//					}
//				}
			}
			
		}
	}
}
