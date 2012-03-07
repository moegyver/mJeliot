package org.mJeliot.server;

import java.util.TimerTask;


public class PingGenerator extends TimerTask {
	private ServerThread server;

	public PingGenerator(ServerThread server) {
		this.server = server;
	}

	@Override
	public void run() {
		this.server.sendMessage("ping\n");
	}

}
