package websocketClient;

import imageProc.FrameManager;

import java.io.IOException;
import java.util.UUID;

import org.eclipse.jetty.websocket.WebSocket;
import org.eclipse.jetty.websocket.WebSocket.Connection;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;

public class WSClient<T extends WebSocketMessage, E extends MessageAdapter> implements WebSocket.OnTextMessage {

	private Connection connection;
	private UUID clientUUID;
	private ConfigMessage config;
	public static boolean debugMode;
	
	private FrameManager frameManager;

	Gson gson = null;
	Gson gson_simpleMessage = null;
	private final Class<T> type;
	
	public WSClient(Class<T> messageClass, E messageAdapter) {
		GsonBuilder builder = new GsonBuilder();
		builder.registerTypeAdapter(messageClass, messageAdapter);
		gson = builder.create();
		type = messageClass;
		gson_simpleMessage = new Gson();
	}
	
	public void onOpen(Connection connection) {
		this.connection = connection;
	}

	public void onMessage(String data) {
		Object object = null;
		try {
			object = gson.fromJson(data, type);

			if (!(object instanceof ConfigMessage)) {
				if (config == null) {
					sendMessage(generateSimpleMessage(
							MessageAction.SERVER_NOT_CONFIGURED,
							" Server requires a messageAction: SERVER_INIT_REQUEST"
									+ "and a valid ConfigMessage in order to start accepting requests"));
					return;
				}
			} else if (object instanceof ConfigMessage) {
				ConfigMessage ccm = (ConfigMessage) object;
				MessageAction ccfMessageAction = ccm.getMessageAction();
				if (config == null
						&& !(ccfMessageAction == MessageAction.SERVER_INIT_REQUEST)) {
					sendMessage(generateSimpleMessage(
							MessageAction.SERVER_NOT_CONFIGURED,
							" Server requires a messageAction: SERVER_INIT_REQUEST"
									+ "and a valid ConfigMessage in order to start accepting requests"));
					return;
				}
				if (ccfMessageAction == MessageAction.SERVER_INIT_REQUEST) {
					config = ccm;
					if (frameManager != null) {
						frameManager.updateSettings(ccm);
						sendMessage(generateSimpleMessage(
								MessageAction.SERVER_CONFIG_UPDATE,
								"Server was already configured and hence the configurations have been updated"));
						System.out.println(clientUUID
								+ " updated configuration of the server!");

					} else{
						frameManager = FrameManager.build(ccm);

						sendMessage(generateSimpleMessage(MessageAction.SERVER_INITIALIZED));
						System.out
								.println(clientUUID
										+ " has successfully configered the server!");

						System.out.println("starting OpenCV services");

						System.out.println("OpenCV services are running!");
					}

					return;
					
				}
				if (ccfMessageAction == MessageAction.SET_SERVER_DEBUG_MODE_ON
						|| ccfMessageAction == MessageAction.SET_SERVER_DEBUG_MODE_OFF) {
					WSClient.debugMode = (ccfMessageAction == MessageAction.SET_SERVER_DEBUG_MODE_ON) ? true
							: false;
				}
			}
		} catch (Exception e) {
			if (e instanceof JsonParseException) {
				sendMessage(generateSimpleMessage(
						MessageAction.SERVER_RECEIVED_MALFORMED_JSON,
						e.getMessage()));
			}
		}
	}

	public void onClose(int closeCode, String message) {
		// TODO Auto-generated method stub
		
	}
	
	private String generateSimpleMessage(MessageAction messageAction,
			String data) {
		return gson_simpleMessage.toJson(new WebSocketMessage(messageAction,
				data));
	}

	private String generateSimpleMessage(MessageAction messageAction) {
		return gson_simpleMessage.toJson(new WebSocketMessage(messageAction));

	}
	
	private void sendMessage(String msg) {
		try {
			connection.sendMessage(msg);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

