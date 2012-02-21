package ict.client;

import ict.protocol.ProtocolParser;

import java.io.BufferedReader;
import java.io.IOException;

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
				this.client.disconnect();
				this.stop();
				e.printStackTrace();
			}
			if (line != null ) {
				message += line + "\n";
				if (line.contains(ProtocolParser.endActionTag)) {
					this.client.receive(message);
					message = "";
				}
			} else {
				try {
					if (!this.in.ready()) {
						this.client.disconnect();
					}
				} catch (IOException e) {
					this.client.disconnect();
					e.printStackTrace();
				}
			}
			
		}
	}
}
