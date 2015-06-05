package com.underapps.mqttclient;

public class MQTTConstants {
	
	public static boolean MQTT_CONNECT_FAILED = false;
	//public final static String MQTT_SERVER = "test.mosquitto.org";
	public final static String MQTT_SERVER = "host";
    public final static String MQTT_PORT = "1883";
    public final static String TOPIC = "name_of_topic";
    
    public final static String CONNECTIVITY_MESSAGE = "connectivityMessage";
    public final static String ACTION_INTENT_CONNECTIVITY_MESSAGE_RECEIVED = "CONNECTIVITY_MESSAGE_RECEIVED";
    
    public final static String INTENT_DATA_CONNECT = "connect";
    public final static String INTENT_DATA_DISCONNECT = "disconnect";
    public final static String INTENT_DATA_ERROR = "error";
    public final static String INTENT_DATA_PUBLISHED = "publish";
    
	public enum ActionStateStatus {
        CONNECTING, DISCONNECTING, SUBSCRIBE, UNSUBSCRIBE, PUBLISH
    }
	
	public final static String INTENT_DATA = "data";
	public final static int ERROR_BROKER_UNAVAILABLE = 3;
	
}
