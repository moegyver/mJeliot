package org.mJeliot.androidClient.controller;

import java.util.Vector;

import org.mJeliot.androidClient.tcp.Client;
import org.mJeliot.androidClient.tcp.ClientListener;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;

public class AndroidClient extends BroadcastReceiver  implements Runnable, ClientListener {
	private ConnectivityManager cm;
	private String url;
	private Vector<AndroidClientListener> listeners = new Vector<AndroidClientListener>();
	private Client client;

	public AndroidClient(String url, ConnectivityManager cm) {
		this.url = url;
		this.cm = cm;
		this.client = new Client(url);
		Thread clientThread = new Thread(this.client);
		clientThread.start();
		this.client.addClientListener(this);
	}

	public void addAndroidClientListener(AndroidClientListener listener) {
		this.listeners.add(listener);
	}

	@Override
	public void run() {
	}

	public void connect() {
		this.client.connect();
	}

	public boolean isConnected() {
		// TODO Auto-generated method stub
		return this.client.isConnected();
	}

	public void sendMessage(String generateLectureQuery) {
		this.client.sendMessage(generateLectureQuery);
	}

	public void disconnect() {
		this.client.disconnect();
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO here is the magic
		System.out.println("AndroidClient: got intent: " + intent);
	}

	@Override
	public void onMessageReceived(Client client, String message) {
		this.fireOnMessageReceived(client, message);
	}

	private void fireOnMessageReceived(Client client, String message) {
		for (AndroidClientListener listener : this.listeners) {
			listener.onMessageReceived(this, message);
		}
	}

	@Override
	public void onClientDisconnected(Client client) {
		// TODO: reconnect
	}

	@Override
	public void onClientConnected(Client client) {
		// TODO generate events for Controller
	}
}
