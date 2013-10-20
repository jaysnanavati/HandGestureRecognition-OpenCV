package imageProc;

import static com.googlecode.javacv.cpp.opencv_core.cvFlip;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.SwingWorker;

import websocketClient.ConfigMessage;
import websocketClient.WSClient;

import com.googlecode.javacv.FrameGrabber.Exception;
import com.googlecode.javacv.OpenCVFrameGrabber;
import com.googlecode.javacv.cpp.opencv_core.IplImage;

import exceptions.ObjectDetectionException;

/**
 * @author Jay
 * 
 */

public class FrameManager {

	private OpenCVFrameGrabber grabber;
	private IplImage grabbedImage;
	private HSVSettings hsvSettings;
	private List<ProcOptions> procOptions;
	private Subscriptions subscriptions;
	private ImageProcessor imgProc;

	private FrameGUI frameGui;

	public static FrameManager build(List<ProcOptions> procOptions,
			Subscriptions subscriptions) {
		return new FrameManager(procOptions, subscriptions);
	}

	public static FrameManager build(ConfigMessage ccm) {
		return new FrameManager(ccm.getProcOptions(), ccm.getSubscriptions());
	}

	public void updateSettings(ConfigMessage ccm) {
		procOptions = ccm.getProcOptions();
		subscriptions = ccm.getSubscriptions();
		if (frameGui == null && WSClient.debugMode == true) {
			frameGui = new FrameGUI(hsvSettings);
		} else if (frameGui != null && WSClient.debugMode == false) {
			frameGui.dispose();
		}
	}

	private FrameManager(List<ProcOptions> procOptions,
			Subscriptions subscriptions) {
		HSVSettings hsvSettings = new HSVSettings(180, 255, 255);
		hsvSettings.setHueRange(0, 35);
		hsvSettings.setSaturationRange(93, 255);
		hsvSettings.setValueRange(0, 255);

		this.hsvSettings = hsvSettings;
		this.procOptions = procOptions;
		this.subscriptions = subscriptions;
		frameGui = WSClient.debugMode == true ? new FrameGUI(hsvSettings)
				: null;
		(new CameraSwingWorker()).execute();
	}

	public void startCamera() throws Exception {
		grabber = new OpenCVFrameGrabber(0);
		grabber.start();
		grabbedImage = grabber.grab();
		try {

			while ((grabbedImage = grabber.grab()) != null) {
				cvFlip(grabbedImage, grabbedImage, 1);
				if (imgProc == null) {
					imgProc = new ImageProcessor(grabbedImage, hsvSettings,
							WSClient.debugMode);
				}
				if (procOptions.contains(ProcOptions.ENABLE_HAND_GESTURES)
						|| procOptions.contains(ProcOptions.SINGLE_OBJECT_MODE)
						|| procOptions
								.contains(ProcOptions.MULTIPLE_OBJECT_MODE)) {
					imgProc.activateHandFunctions(procOptions,
							subscriptions.getHandGestures());
				}

				if (WSClient.debugMode) {
					frameGui.getRawFrame().showImage(grabbedImage);
					frameGui.getThresholdFrame().showImage(
							imgProc.getThreshImage());
				}
			}
		} catch (ObjectDetectionException e) {
			// TODO Auto-generated catch block
			System.out.println(e.getMessage());
			return;
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	public void stopCamera() throws Exception {
		grabber.stop();
	}

	class CameraSwingWorker extends SwingWorker<String, Object> {
		@Override
		public String doInBackground() {
			try {
				startCamera();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return "0";
		}
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

	public static void noWebSocketClientMode(String args[]) {

		List<ProcOptions>userProcOptions = new ArrayList<ProcOptions>();
		List<ProcOptions>procOptions = Arrays.asList(ProcOptions.values());
		
		for(int i=1;i<args.length;i++){
			ProcOptions procOption;
			procOption = ProcOptions.valueOf(args[i]);
			if(procOptions.contains(procOption)){
				userProcOptions.add(procOption);
			}
		}
		
		build(userProcOptions,
				new Subscriptions(Arrays
						.asList(new HandGestures[] { HandGestures.ALL })));
	}
}
