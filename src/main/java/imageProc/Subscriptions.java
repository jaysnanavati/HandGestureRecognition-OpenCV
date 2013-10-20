package imageProc;

import java.util.List;

public class Subscriptions {
	
	private List<HandGestures> handGestures;

	public Subscriptions(){}
	
	public Subscriptions(List<HandGestures> handGestures) {
		super();
		this.handGestures = handGestures;
	}

	public List<HandGestures> getHandGestures() {
		return handGestures;
	}

	public void setHandGestures(List<HandGestures> handGestures) {
		this.handGestures = handGestures;
	}

}
