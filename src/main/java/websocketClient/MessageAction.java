package websocketClient;

public enum MessageAction {

	SERVER_INIT_REQUEST,
	SERVER_INITIALIZED,
	SERVER_CONFIG_UPDATE,
	SERVER_NOT_CONFIGURED,
	SERVER_CONFIG_BUSY_CONFIG_ENQUED,
	SERVER_RECEIVED_MALFORMED_JSON,
	SET_SERVER_DEBUG_MODE_ON,
	SET_SERVER_DEBUG_MODE_OFF,
	
	GESTURE_DETECTED,
	
}