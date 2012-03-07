package org.mJeliot.androidClient.controller;

import org.mJeliot.client.Client;
import org.mJeliot.client.ClientListener;
import org.mJeliot.model.Lecture;
import org.mJeliot.model.User;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;

public class AndroidClient extends BroadcastReceiver implements Runnable,
		ClientListener {
	private String url;
	private AndroidClientListener listener = null;
	private Client client;
	private boolean stop = false;
	private boolean userDisconnected = false;
	private boolean reconnected = false;

	public AndroidClient(AndroidClientListener listener, String url) {
		this.listener = listener;
		this.url = url;
		this.client = new Client(this, url);
	}

	@Override
	public void run() {
		while(!stop) {
			
		}
	}

	public void connect() {
		System.out.println("AndroidClient: connect");
		if (this.client == null) {
			this.reconnect();
		} else {
			this.client.connect();
		}
	}

	public boolean isConnected() {
		return this.client != null &&  this.client.isConnected();
	}

	public void sendMessage(String generateLectureQuery) {
		if (this.client == null || !this.client.isConnected()) {
			this.reconnect();
		}
		this.client.sendMessage(generateLectureQuery);
	}

	public void disconnect() {
		this.userDisconnected  = true;
		this.client.disconnect();
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		boolean hasConnectivity = !intent.getBooleanExtra(
				ConnectivityManager.EXTRA_NO_CONNECTIVITY, false);
		if (this.client != null && !hasConnectivity) {
			this.client.disconnect();
		} else if (hasConnectivity){
			this.reconnect();
		}
	}

	private void reconnect() {
		if (this.client == null) {
			System.out.println("AndroidClient: reconnect");
			this.reconnected = true;
			this.client = new Client(this, url);
			this.client.connect();
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
	public void onClientDisconnected(Client client) {
		if (this.userDisconnected) {
			System.out.println("AndroidClient: user disconnected");
			this.fireOnClientDisconnected(client);
			this.stop = true;
			this.client = null;
		} else {
			System.out.println("AndroidClient: disconnected");
			this.client = null;
			this.reconnect();
		}
	}

	private void fireOnClientDisconnected(Client client) {
		listener.onClientDisconnected(this);
	}

	@Override
	public void onClientConnected(Client client) {
		fireOnClientConnected(client);
	}

	private void fireOnClientConnected(Client client) {
		listener.onClientConnected(this, this.reconnected);
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
