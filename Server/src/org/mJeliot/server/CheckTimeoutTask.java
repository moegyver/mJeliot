package org.mJeliot.server;

import java.util.TimerTask;

public class CheckTimeoutTask extends TimerTask {
	private ServerThread serverThread;
	public CheckTimeoutTask(ServerThread serverThread) {
		this.serverThread = serverThread;
	}
	@Override
	public void run() {
		if (this.serverThread.getLastPong() + 3 * ServerThread.PING_INTERVAL - System.currentTimeMillis() <= 0) {
			System.out.println("disconnecting after timeout:" + serverThread.getLastPong() + " now:" + System.currentTimeMillis());
			this.serverThread.disconnectClient();
		}
	}

}
