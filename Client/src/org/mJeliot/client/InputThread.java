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
				 System.out.println("line: " + line);
			} catch (IOException e) {
				System.out.println("error receiving data: " + e.getMessage());
				this.client.disconnect();
				this.stop();
				e.printStackTrace();
			}
			if (line != null ) {
				message += line + "\n";
				System.out.println("got line: " + line);
				if (line.contains(ProtocolParser.endActionTag)) {
					this.client.receive(message);
					message = "";
				} else if (line.contains("ping")) {
					System.out.println("received ping, sending pong");
					String userId = "";
					if (this.client.getUser() != null) {
						userId += this.client.getUser().getId();
					}
					String lectureId = "";
					if (this.client.getLecture() != null) {
						lectureId += this.client.getLecture().getId();
					}
					message = "";
					this.client.sendMessage("pong " + lectureId + " " + userId + " \n");
				} else {
				}
			} else {
				System.out.println("got a null message");
				try {
					if (!this.in.ready()) {
						System.out.println("socket not ready for receiving");
						this.client.disconnect();
						this.stop();
					}
				} catch (IOException e) {
					System.out.println("error receiving: " + e.getMessage());
					this.client.disconnect();
					this.stop();
					e.printStackTrace();
				}
			}
			
		}
	}
}
