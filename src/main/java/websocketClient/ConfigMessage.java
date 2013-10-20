package websocketClient;

import imageProc.HandGestures;
import imageProc.ProcOptions;
import imageProc.Subscriptions;

import java.util.List;
import java.util.Map;

public class ConfigMessage extends WebSocketMessage {

	private List<ProcOptions> procOptions;
	private Subscriptions subscriptions;
	private Integer numberOfScreensRequired, maxTrackedObjects;
	private Map<HandGestures, String> controlMap;

	public ConfigMessage(MessageAction messageAction) {
		super(messageAction);
	}

	public ConfigMessage(MessageAction messageAction,
			List<ProcOptions> procOptions, Subscriptions subscriptions,
			int numberOfScreensRequired, int maxTrackedObjects,
			Map<HandGestures, String> controlMap) {
		super(messageAction);
		this.procOptions = procOptions;
		this.subscriptions = subscriptions;
		this.numberOfScreensRequired = numberOfScreensRequired;
		this.maxTrackedObjects = maxTrackedObjects;
		this.controlMap = controlMap;
	}

	public List<ProcOptions> getProcOptions() {
		return procOptions;
	}

	public void setProcOptions(List<ProcOptions> procOptions) {
		this.procOptions = procOptions;
	}
	

	public Subscriptions getSubscriptions() {
		return subscriptions;
	}

	public void setSubscriptions(Subscriptions subscriptions) {
		this.subscriptions = subscriptions;
	}

	public int getNumberOfScreensRequired() {
		return numberOfScreensRequired;
	}

	public void setNumberOfScreensRequired(int numberOfScreensRequired) {
		this.numberOfScreensRequired = numberOfScreensRequired;
	}

	public int getMaxTrackedObjects() {
		return maxTrackedObjects;
	}

	public void setMaxTrackedObjects(int maxTrackedObjects) {
		this.maxTrackedObjects = maxTrackedObjects;
	}

	public Map<HandGestures, String> getControlMap() {
		return controlMap;
	}

	public void setControlMap(Map<HandGestures, String> controlMap) {
		this.controlMap = controlMap;
	}

}
