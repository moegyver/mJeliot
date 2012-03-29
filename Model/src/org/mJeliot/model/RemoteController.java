package org.mJeliot.model;

import java.util.Vector;

import org.mJeliot.protocol.Route;
import org.mJeliot.protocol.ProtocolParser;

public class RemoteController {

	private final Route messageRouter;
	private int animationSpeed;
	private Vector<RemoteControllerListener> listeners = new Vector<RemoteControllerListener>();

	public RemoteController(Route messageRouter) {
		this.messageRouter = messageRouter;

	}

	public void step() {
		this.sendControl(-1, "step");
	}

	public void play() {
		this.sendControl(-1, "play");
	}

	public void pause() {
		this.sendControl(-1, "pause");
	}

	public void rewind() {
		this.sendControl(-1, "rewind");
	}

	public void control() {
		this.sendControl(-1, "control");
	}
	public void endControl() {
		this.sendControl(-1, "endControl");
	}

	private void sendControl(int animationSpeed, String command) {
		messageRouter.sendMessage(ProtocolParser.generateRemoteCommand(
				messageRouter.getLecture().getId(), messageRouter.getUser()
						.getId(), messageRouter.getDestination(), animationSpeed, command));
	}

	public void changeAnimationSpeed(int animationSpeed) {
		this.sendControl(animationSpeed, "changeAnimationSpeed");
	}

	public void setAnimationSpeed(int animationSpeed) {
		if (animationSpeed != -1) {
			this.animationSpeed = animationSpeed;
			this.fireOnAnimationSpeedChanged();
		}
	}

	private void fireOnAnimationSpeedChanged() {
		for (RemoteControllerListener listener : this.listeners ) {
			listener.onAnimationSpeedChanged(this, this.animationSpeed);
		}
	}

	public int getAnimationSpeed() {
		return this.animationSpeed;
	}

	public void addRemoteControllerListener(RemoteControllerListener remoteControllerListener) {
		if (!this.listeners.contains(remoteControllerListener)) {
			this.listeners.add(remoteControllerListener);
		}
	}
	public void removeRemoteControllerListener(RemoteControllerListener remoteControllerListener) {
		this.listeners.remove(remoteControllerListener);
	}
}
