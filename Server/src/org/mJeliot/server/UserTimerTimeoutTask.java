package org.mJeliot.server;

import java.util.TimerTask;

import org.mJeliot.model.User;

public class UserTimerTimeoutTask extends TimerTask {
	private long lastSeen = System.currentTimeMillis();
	private ServerController controller;
	private User user;

	public UserTimerTimeoutTask(ServerController controller, User user) {
		this.controller = controller;
		this.user = user;
	}

	@Override
	public void run() {
		if (lastSeen + 6 * ServerThread.PING_INTERVAL
				- System.currentTimeMillis() < 0) {
			System.out.println("User timeout, User " + user.getId()
					+ " disconnecting from lecture " + user.getLecture().getId());
			controller.disconnectUser(user, user.getLecture().getId());
		}
	}

	public void resetTimer() {
		this.lastSeen = System.currentTimeMillis();
	}
}
