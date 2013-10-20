package websocketClient;

public class WebSocketMessage {

	private MessageAction messageAction;
	private String DATA;
		
	public WebSocketMessage(MessageAction messageAction){
		super();
		this.messageAction = messageAction;
	}
	
	public WebSocketMessage(MessageAction messageAction,String data) {
		super();
		this.messageAction = messageAction;
		this.DATA = data;
	}

	public MessageAction getMessageAction() {
		return messageAction;
	}

	public void setMessageAction(MessageAction messageAction) {
		this.messageAction = messageAction;
	}
		
}
