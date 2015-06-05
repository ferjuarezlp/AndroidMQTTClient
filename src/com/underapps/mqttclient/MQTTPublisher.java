package com.underapps.mqttclient;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;
import android.content.Context;
import android.util.Log;


public class MQTTPublisher {
	public static boolean PUBLISHING = false;
	private static String TAG = "MQTTPublisher";
	MqttClient client;
	MqttConnectOptions conOptions;
	int messageId = 1;
	MQTTAndroidCallback callback;
	private Context context;
	
	public MQTTPublisher(Context context) {
	
		try {

			String serverPort = MQTTConstants.MQTT_PORT;
			String serverHost = MQTTConstants.MQTT_SERVER;
			
			String uri = "tcp://" + serverHost + ":" + serverPort;
			String haURIs[] = new String[] { uri};
			
			this.context = context;
			this.client = new MqttClient(uri, "CLIENT_ID", null);
			this.callback = new MQTTAndroidCallback();
			client.setCallback(callback);
			
			this.conOptions = new MqttConnectOptions();
			this.conOptions.setServerURIs(haURIs);
			this.conOptions.setCleanSession(false);
			
			this.connect();
			
		} catch (MqttException e) {
			// Error handling goes here...
		}
	}

	public void publishMessages() {

		// check connection
		boolean connecting = false;

		while (!client.isConnected()) {
			if (!connecting) {
				this.connect();
				connecting = true;
			}
		}

		if (!client.isConnected()) {

		}

		PUBLISHING = true;
		try {
			client.publish("TOPIC NAME", new byte[2], 0, false);

		} catch (MqttPersistenceException e) {
			e.printStackTrace();
			PUBLISHING = false;
		} catch (MqttException e) {
			e.printStackTrace();
			PUBLISHING = false;
		}
	}

	private boolean connect() {
		// Let's try a cycle of reconnects. We rely on Paho's built-in HA code
		// to hunt out
		// the primary appliance for us.

		boolean tryConnecting = true;
		boolean connecting = false;
		int intents = 0;
		while (tryConnecting && intents < 2) {
			intents ++;
			try {
				if(!connecting){
					client.connect(conOptions);
					connecting = true;
				}
				
			} catch (Exception e1) {
				Log.e(TAG, e1.toString());
				PUBLISHING = false;
				/*
				 * We'll do nothing as we'll shortly try connecting again. You
				 * may wish to track the number of attempts to guard against
				 * long-term or permanent issues, for example, misconfigured
				 * URIs.
				 */
			}
			if (client.isConnected()) {
				 
				tryConnecting = false;
			} else {
				pause();
			}
		}
		return !tryConnecting;
	}
	
	public void disconnect() {
		if (client.isConnected())
			try {
				client.disconnect();
				PUBLISHING = false;
			} catch (MqttException e) {
				e.printStackTrace();
			}
	}
	
	private void pause() {
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
		}
	}

	private class MQTTAndroidCallback implements MqttCallback {

		public void connectionLost(Throwable cause) {
			Log.e(TAG, " Connection Lost ");
		}

		@Override
		public void deliveryComplete(IMqttDeliveryToken arg0) {
			// Not needed in this simple demo
		}

		@Override
		public void messageArrived(String arg0, MqttMessage arg1)
				throws Exception {
			// Not needed in this simple demo
		}
	}
}