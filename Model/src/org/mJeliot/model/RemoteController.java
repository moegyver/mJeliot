package org.mJeliot.model;

import org.mJeliot.protocol.Route;
import org.mJeliot.protocol.ProtocolParser;

public class RemoteController {

	private final Route messageRouter;

	public RemoteController(Route messageRouter) {
		this.messageRouter = messageRouter;

	}

	public void step() {
		this.sendControl("step");
	}

	public void play() {
		this.sendControl("play");
	}

	public void pause() {
		this.sendControl("pause");
	}

	public void rewind() {
		this.sendControl("rewind");
	}

	public void control() {
		this.sendControl("control");
	}
	public void endControl() {
		this.sendControl("endControl");
	}

	private void sendControl(String command) {
		messageRouter.sendMessage(ProtocolParser.generateRemoteCommand(
				messageRouter.getLecture().getId(), messageRouter.getUser()
						.getId(), messageRouter.getDestination(), command));
	}
}
