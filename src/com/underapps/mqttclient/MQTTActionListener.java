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
import android.util.Log;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;

/**
 * This class implements the IMqttActionListener interface of the MQTT Client.
 * It provides the functionality for handling the success or failure of MQTT API calls.
 */
public class MQTTActionListener implements IMqttActionListener {

    private final static String TAG = MQTTActionListener.class.getName();
    private MQTTConstants.ActionStateStatus action;
    
    
    public MQTTActionListener(Context context, MQTTConstants.ActionStateStatus action) {
        this.action = action;
    }

    /**
     * Determine the type of callback that completed successfully.
     * @param token The MQTT Token for the completed action.
     */
    @Override
    public void onSuccess(IMqttToken token) {
        Log.d(TAG, ".onSuccess() entered");
        switch (action) {
            case CONNECTING:
                handleConnectSuccess();
                break;

            case SUBSCRIBE:
                handleSubscribeSuccess();
                break;

            case PUBLISH:
                handlePublishSuccess();
                break;

            case DISCONNECTING:
                handleDisconnectSuccess();
                break;

            default:
                break;
        }
    }

    /**
     * Determine the type of callback that failed.
     * @param token The MQTT Token for the completed action.
     * @param throwable The exception corresponding to the failure.
     */
    @Override
    public void onFailure(IMqttToken token, Throwable throwable) {
        Log.e(TAG, ".onFailure() entered");
        switch (action) {
            case CONNECTING:
            	
                handleConnectFailure(throwable);
                break;

            case SUBSCRIBE:
                handleSubscribeFailure(throwable);
                break;

            case PUBLISH:
                handlePublishFailure(throwable);
                break;

            case DISCONNECTING:
                handleDisconnectFailure(throwable);
                break;

            default:
                break;
        }
    }

    /**
     * Called on successful connection to the MQTT broker.
     */
    private void handleConnectSuccess() {
    	Log.d(TAG, ".handleConnectSuccess() entered");
    }

    /**
     * Called on successful subscription to the MQTT topic.
     */
    private void handleSubscribeSuccess() {
        Log.d(TAG, ".handleSubscribeSuccess() entered");
    }

    /**
     * Called on successful publish to the MQTT topic.
     */
    private void handlePublishSuccess() {
        Log.d(TAG, ".handlePublishSuccess() entered");
    }

    /**
     * Called on successful disconnect from the MQTT server.
     */
    private void handleDisconnectSuccess() {
    	Log.d(TAG, ".handleDisconnectSuccess() entered");
    }

    /**
     * Called on failure to connect to the MQTT server.
     * @param throwable The exception corresponding to the failure.
     */
    private void handleConnectFailure(Throwable throwable) {
        Log.e(TAG, ".handleConnectFailure() entered");
        Log.e(TAG, ".handleConnectFailure() - Failed with exception", throwable.getCause());
        throwable.printStackTrace();
    }

    /**
     * Called on failure to subscribe to the MQTT topic.
     * @param throwable The exception corresponding to the failure.
     */
    private void handleSubscribeFailure(Throwable throwable) {
        Log.e(TAG, ".handleSubscribeFailure() entered");
        Log.e(TAG, ".handleSubscribeFailure() - Failed with exception", throwable.getCause());
    }

    /**
     * Called on failure to publish to the MQTT topic.
     * @param throwable The exception corresponding to the failure.
     */
    private void handlePublishFailure(Throwable throwable) {
        Log.e(TAG, ".handlePublishFailure() entered");
        Log.e(TAG, ".handlePublishFailure() - Failed with exception", throwable.getCause());
    }

    /**
     * Called on failure to disconnect from the MQTT server.
     * @param throwable The exception corresponding to the failure.
     */
    private void handleDisconnectFailure(Throwable throwable) {
    	//MQTTHandler.MQTTCONNECTING = false;
        Log.e(TAG, ".handleDisconnectFailure() entered");
        Log.e(TAG, ".handleDisconnectFailure() - Failed with exception", throwable.getCause());
    }

}
