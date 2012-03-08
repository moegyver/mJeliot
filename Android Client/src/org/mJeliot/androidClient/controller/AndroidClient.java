package org.mJeliot.androidClient.controller;

import org.mJeliot.client.Client;
import org.mJeliot.client.ClientListener;
import org.mJeliot.model.Lecture;
import org.mJeliot.model.User;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;

public class AndroidClient extends BroadcastReceiver implements ClientListener {
	private AndroidClientListener listener = null;
	private Client client;

	public AndroidClient(AndroidClientListener listener, String url) {
		this.listener = listener;
		this.client = new Client(this, url);
	}

	public void connect() {
		System.out.println("AndroidClient: connect");
		this.client.connect(false);
	}

	public boolean isConnected() {
		return this.client != null &&  this.client.isConnected();
	}

	public void sendMessage(String generateLectureQuery) {
		this.client.sendMessage(generateLectureQuery);
	}

	public void disconnect() {
		this.client.disconnect(true, false);
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		boolean hasConnectivity = !intent.getBooleanExtra(
				ConnectivityManager.EXTRA_NO_CONNECTIVITY, false);
		if (this.client != null && !hasConnectivity) {
			// We don't do anything while not connected
		} else if (hasConnectivity){
			this.client.reconnect();
		}
	}


	@Override
	public void onMessageReceived(Client client, String message) {
		this.fireOnMessageReceived(client, message);
	}

	private void fireOnMessageReceived(Client client, String message) {
		listener.onMessageReceived(this, message);
	}

	@Override
	public void onClientDisconnected(Client client, boolean isIntentional, boolean isForced) {
		if (isIntentional || !isIntentional && isForced) {
			System.out.println("AndroidClient: user disconnected or disconnect forced");
			this.fireOnClientDisconnected(client);
		} 
	}

	private void fireOnClientDisconnected(Client client) {
		listener.onClientDisconnected(this);
	}

	@Override
	public void onClientConnected(Client client, boolean isReconnected) {
		fireOnClientConnected(client, isReconnected);
	}

	private void fireOnClientConnected(Client client, boolean isReconnected) {
		listener.onClientConnected(this, isReconnected);
	}

	@Override
	public User getUser() {
		return this.listener.getUser();
	}

	@Override
	public Lecture getLecture() {
		return this.listener.getLecture();
	}
}
