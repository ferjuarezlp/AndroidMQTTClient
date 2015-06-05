/*******************************************************************************
 * Copyright (c) 2014 IBM Corp.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v1.0 which accompany this distribution.
 *
 * The Eclipse Public License is available at
 *   http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 *   http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *    Mike Robertson - initial contribution
 *******************************************************************************/
package com.underapps.mqttclient;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

/**
 * This class provides a wrapper around the MQTT client API's and implements
 * the MqttCallback interface.
 */
public class MQTTHandler implements MqttCallback{

    private final static String TAG = MQTTHandler.class.getName();
    private static MQTTHandler instance;
    public MqttAndroidClient client;
    private Context context;
    public static boolean MQTTCONNECTING = false;
    
    private MQTTHandler(Context context) {
        this.context = context;
        this.client = null;
    }
    
    /**
     * @param context The application context for the object.
     * @return The MqttHandler object for the application.
     */
    public static MQTTHandler getInstance(Context context) {
        Log.d(TAG, ".getInstance() entered");
        if (instance == null) {
            instance = new MQTTHandler(context);
        }
        return instance;
    }
    
    /**
     * Connect MqttAndroidClient to the MQTT server
     */
    public void connect() {
        Log.d(TAG, ".connect() entered");

        // check if client is already connected
        if (!isMqttConnected()) {

            String serverPort = MQTTConstants.MQTT_PORT;
            String serverHost = MQTTConstants.MQTT_SERVER;
            
            MQTTActionListener listener = new MQTTActionListener(context, MQTTConstants.ActionStateStatus.CONNECTING);
            String connectionUri = "tcp://" + serverHost + ":" + serverPort;
            
           client = new MqttAndroidClient(context, connectionUri, "CLIENT ID", new MemoryPersistence());
           client.setCallback(this);

            // create MqttConnectOptions and set the clean session flag
            MqttConnectOptions options = new MqttConnectOptions();
            options.setCleanSession(false);
            
            
            /*
             * Set user / pass 
             * 
             *   options.setUserName("username");
             *   options.setPassword("pass");
             *   
             * */
            
            try {
            	MQTTCONNECTING = true;
                client.connect(options, listener);
               
                Intent actionIntent = new Intent(MQTTConstants.INTENT_DATA_CONNECT);
				LocalBroadcastManager.getInstance(context).sendBroadcast(actionIntent);
	            
            } catch (MqttException e) {
            	MQTTCONNECTING = false;
            	Log.e("MQTTHandler /////////// "," Exception in Connect " + e.toString());
            }
        }
    }

    /**
     * Disconnect MqttAndroidClient from the MQTT server
     */
    public void disconnect() {
        Log.d(TAG, ".disconnect() entered");
        if (isMqttConnected()) {
        	try {
                // disconnect
                client.disconnect();
                Intent actionIntent = new Intent(MQTTConstants.INTENT_DATA_DISCONNECT);
				LocalBroadcastManager.getInstance(context).sendBroadcast(actionIntent);
				
                
            } catch (MqttException e) {
            	Log.e("=======MQTT=======", "Disconnect error " + e.toString());
     			
            }
        }
        client = null;
        
    }

    

    /**
     * Publish message to a topic
     *
     * @param topic    to publish the message to
     * @param message  JSON object representation as a string
     * @param retained true if retained flag is requred
     * @param qos      quality of service (0, 1, 2)
     */
	public void publish(String topic, String message, boolean retained, int qos) {
		Log.d(TAG, ".publish() entered");

			// create a new MqttMessage from the message string
			MqttMessage mqttMsg = new MqttMessage(message.getBytes());
			// set retained flag
			mqttMsg.setRetained(retained);
			// set quality of service
			mqttMsg.setQos(qos);
			try {
				// create ActionListener to handle message published results
				
				Log.d(TAG, ".publish() - Publishing " + message + " to: "
						+ topic + ", with QoS: " + qos
						+ " with retained flag set to " + retained);
			
				client.publish(topic, mqttMsg);
 
			} catch (MqttPersistenceException e) {
				Log.e("=======MQTT=======", "MqttPersistenceException " + e.toString());
				//Intent actionIntent = new Intent(MQTTConstants.INTENT_DATA_ERROR);
				//LocalBroadcastManager.getInstance(context).sendBroadcast(actionIntent);
				
			} catch (MqttException e) {
				Log.e("=======MQTT=======", "MqttException " + e.toString());
				//Intent actionIntent = new Intent(MQTTConstants.INTENT_DATA_ERROR);
				//LocalBroadcastManager.getInstance(context).sendBroadcast(actionIntent);
			}
	}

    /**
     * Handle loss of connection from the MQTT server.
     * @param throwable
     */
    @Override
    public void connectionLost(Throwable throwable) {
    	 Log.e("=======MQTT=======", "Connection lost, reconnect ");
        if (throwable != null) {
            throwable.printStackTrace();
        }
        
        /*if(client != null && !client.isConnected()){
        	try {
				client.connect();
			} catch (MqttException e) {
				e.printStackTrace();
			}
        } else if(client != null && client.isConnected()){
        	try {
				client.disconnect();
				client.connect();
			} catch (MqttException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        	
        }*/
        
        //Intent actionIntent = new Intent(MQTTConstants.INTENT_DATA_ERROR);
		//LocalBroadcastManager.getInstance(context).sendBroadcast(actionIntent);
       
        /*Intent actionIntent = new Intent(MQTTConstants.APP_ID + MQTTConstants.INTENT_SENSORS);
        actionIntent.putExtra(MQTTConstants.INTENT_DATA, MQTTConstants.INTENT_DATA_DISCONNECT);
        context.sendBroadcast(actionIntent);*/
    }

    /**
     * Process incoming messages to the MQTT client.
     *
     * @param topic       The topic the message was received on.
     * @param mqttMessage The message that was received
     * @throws Exception  Exception that is thrown if the message is to be rejected.
     */
    @Override
    public void messageArrived(String topic, MqttMessage mqttMessage) throws Exception {
        Log.d(TAG, ".messageArrived() entered");
    }

    /**
     * Handle notification that message delivery completed successfully.
     *
     * @param iMqttDeliveryToken The token corresponding to the message which was delivered.
     */
    @Override
    public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
        Log.d(TAG, ".deliveryComplete() entered");
    }

    /**
     * Checks if the MQTT client has an active connection
     *
     * @return True if client is connected, false if not.
     */
    public boolean isMqttConnected() {
        Log.d(TAG, ".isMqttConnected() entered");
        boolean connected = false;
        try {
            if ((client != null) && (client.isConnected())) {
                connected = true;
            }
        } catch (Exception e) {
            // swallowing the exception as it means the client is not connected
        }
        Log.d(TAG, ".isMqttConnected() - returning " + connected);
        return connected;
    }
}
