package imageProc.eventHandling;

import java.awt.AWTException;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.Robot;
import java.awt.event.InputEvent;

import com.googlecode.javacv.cpp.opencv_core.IplImage;
/**
 * 
 * @author Jay
 *
 */
public class SystemEvents {
	
	private static Robot robot;
	
	static{
		try {
			robot = new Robot();
		} catch (AWTException e) {
			e.printStackTrace();
		}
	}
	
	public static void moveMouse(Point targetPoint,IplImage referenceImage){
		GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment()
				.getDefaultScreenDevice();
		int width = gd.getDisplayMode().getWidth();
		int height = gd.getDisplayMode().getHeight();

		double xPerc = ((double) targetPoint.x / (double) referenceImage.width());
		double yPerc = ((double) targetPoint.y / (double) referenceImage.height());

		robot.mouseMove((int) (width * xPerc), (int) (height * yPerc));
	}
	
	public static void clickMouseLeft(){
		robot.mousePress(InputEvent.BUTTON1_MASK);
		robot.mouseRelease(InputEvent.BUTTON1_MASK);
	}
	
	public static void clickMouseRight(){
		robot.mousePress(InputEvent.BUTTON3_MASK);
		robot.mouseRelease(InputEvent.BUTTON3_MASK);
	}
	
	public static void startMouseDrag(){
		robot.mousePress(InputEvent.BUTTON1_MASK);
	}
	
	public static void endMouseDrag(){
		robot.mouseRelease(InputEvent.BUTTON1_MASK);
	}
	
}
