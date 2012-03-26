package org.mJeliot.androidClient.controller;

import org.mJeliot.client.Client;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;

public class NetworkChangeBroadcastReceiver extends BroadcastReceiver {
	private Client client;

	public NetworkChangeBroadcastReceiver(Client client) {
		this.client = client;
	}
	@Override
	public void onReceive(Context context, Intent intent) {
		boolean hasConnectivity = !intent.getBooleanExtra(
				ConnectivityManager.EXTRA_NO_CONNECTIVITY, false);
		if (hasConnectivity) {
			client.forceReconnect();
		}
	}
}