package imageProc;

import static com.googlecode.javacv.cpp.opencv_core.CV_AA;
import static com.googlecode.javacv.cpp.opencv_core.CV_RGB;
import static com.googlecode.javacv.cpp.opencv_core.cvCircle;
import static com.googlecode.javacv.cpp.opencv_core.cvClearMemStorage;
import static com.googlecode.javacv.cpp.opencv_core.cvCreateImage;
import static com.googlecode.javacv.cpp.opencv_core.cvGetSize;
import static com.googlecode.javacv.cpp.opencv_core.cvInRangeS;
import static com.googlecode.javacv.cpp.opencv_core.cvPoint;
import static com.googlecode.javacv.cpp.opencv_core.cvRectangle;
import static com.googlecode.javacv.cpp.opencv_core.cvScalar;
import static com.googlecode.javacv.cpp.opencv_imgproc.CV_BGR2HSV;
import static com.googlecode.javacv.cpp.opencv_imgproc.CV_CHAIN_APPROX_SIMPLE;
import static com.googlecode.javacv.cpp.opencv_imgproc.CV_MOP_OPEN;
import static com.googlecode.javacv.cpp.opencv_imgproc.CV_RETR_LIST;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvBoundingRect;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvCvtColor;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvFindContours;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvGetSpatialMoment;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvMoments;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvMorphologyEx;
import imageProc.eventHandling.SystemEvents;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import websocketClient.WSClient;

import com.googlecode.javacpp.Loader;
import com.googlecode.javacv.cpp.opencv_core.CvContour;
import com.googlecode.javacv.cpp.opencv_core.CvMemStorage;
import com.googlecode.javacv.cpp.opencv_core.CvRect;
import com.googlecode.javacv.cpp.opencv_core.CvSeq;
import com.googlecode.javacv.cpp.opencv_core.IplImage;
import com.googlecode.javacv.cpp.opencv_imgproc.CvMoments;

import exceptions.ObjectDetectionException;

/**
 * @author Jay
 * 
 */
public class ImageProcessor {

	private IplImage threshImage, grabbedImage;
	private HSVSettings hsvSettings;
	private int MAX_CONTOUR_SIZE;

	private CvMemStorage mem;

	// Feature Processors
	HandProc handProc;

	public ImageProcessor(IplImage grabbedImage, HSVSettings hsvSettings,
			boolean debugMode) {
		this.grabbedImage = grabbedImage;
		this.hsvSettings = hsvSettings;
		mem = CvMemStorage.create();
	}

	private void performThresholding() {
		IplImage hsv = IplImage.createFrom(grabbedImage.getBufferedImage());
		if (threshImage == null) {
			threshImage = cvCreateImage(cvGetSize(grabbedImage), 8, 1);
		}
		cvCvtColor(hsv, hsv, CV_BGR2HSV);

		cvInRangeS(
				hsv,
				cvScalar(hsvSettings.getHueLower(),
						hsvSettings.getSaturationLower(),
						hsvSettings.getValueLower(), 0),
				cvScalar(hsvSettings.getHueUpper(),
						hsvSettings.getSaturationUpper(),
						hsvSettings.getValueUpper(), 0), threshImage);// red

		cvMorphologyEx(threshImage, threshImage, null, null, CV_MOP_OPEN, 1);
	}

	private List<CvSeq> detectBigContour(int MAX_CONTOUR_SIZE,
			ProcOptions detectionMode) throws ObjectDetectionException {
		this.setMAX_CONTOUR_SIZE(MAX_CONTOUR_SIZE);
		cvClearMemStorage(mem);
		List<CvSeq> bigContours = new ArrayList<CvSeq>();

		CvSeq contours = new CvSeq(null);
		CvSeq ptr = new CvSeq(null);

		cvFindContours(threshImage, mem, contours,
				Loader.sizeof(CvContour.class), CV_RETR_LIST,
				CV_CHAIN_APPROX_SIMPLE, cvPoint(0, 0));

		CvRect boundbox;
		CvSeq bigContour = new CvSeq(null);

		int maxArea = 0;
		for (ptr = contours; ptr != null && !ptr.isNull(); ptr = ptr.h_next()) {
			if (ptr.elem_size() > 0) {
				boundbox = cvBoundingRect(ptr, 0);
				int areaBox = boundbox.height() * boundbox.width();
				if (detectionMode.equals(ProcOptions.MULTIPLE_OBJECT_MODE)) {
					if (areaBox > MAX_CONTOUR_SIZE) {
						bigContours.add(ptr);
					}
				} else if (detectionMode.equals(ProcOptions.SINGLE_OBJECT_MODE)) {
					if (areaBox > maxArea && areaBox > MAX_CONTOUR_SIZE) {
						bigContour = ptr;
						maxArea = areaBox;
					}
				} else {
					throw new ObjectDetectionException(
							"Unknow ProcOption for this method");
				}
			}
		}

		if (bigContours.isEmpty() && bigContour != null && !bigContour.isNull()) {
			bigContours.add(bigContour);
		}

		return bigContours;
	}

	private void drawBigContour(CvSeq bigContour) {
		CvRect bigBox = cvBoundingRect(bigContour, 0);
		cvRectangle(
				grabbedImage,
				cvPoint(bigBox.x(), bigBox.y()),
				cvPoint(bigBox.x() + bigBox.width(),
						bigBox.y() + bigBox.height()), cvScalar(0, 255, 0, 0),
				3, 0, 0);

	}

	public void activateHandFunctions(List<ProcOptions> procOptions,
			List<HandGestures> handGestures) throws ObjectDetectionException {

		if (handProc == null) {
			handProc = new HandProc();
		}

		List<CvSeq> bigContours;

		performThresholding();

		if (procOptions.contains(ProcOptions.MULTIPLE_OBJECT_MODE)
				&& procOptions.contains(ProcOptions.SINGLE_OBJECT_MODE)) {
			throw new ObjectDetectionException(
					"Can only have one of MULTIPLE_OBJECT_MODE or SINGLE_ONJECT_MODE in procOptions");
		} else if (procOptions.contains(ProcOptions.MULTIPLE_OBJECT_MODE)) {

			bigContours = detectBigContour(17000,
					ProcOptions.MULTIPLE_OBJECT_MODE);
		} else if (procOptions.contains(ProcOptions.SINGLE_OBJECT_MODE)) {
			bigContours = detectBigContour(17000,
					ProcOptions.SINGLE_OBJECT_MODE);
		} else {
			throw new ObjectDetectionException(
					"Please specify detection mode in procOptions");
		}

		if (bigContours != null && !bigContours.isEmpty()) {
			for (CvSeq bigContour : bigContours) {
				Point centre = getBigContourCentre(bigContour);

				if (WSClient.debugMode) {
					drawBigContour(bigContour);
					if (centre != null) {
						cvCircle(grabbedImage, cvPoint(centre.x, centre.y), 10,
								CV_RGB(255, 130, 0), 1, CV_AA, 0);
					}

				}

				handProc.HandProcUpdate(procOptions,handGestures, bigContour,
						cvBoundingRect(bigContour, 0), centre, grabbedImage);

				if (procOptions.contains(ProcOptions.TRACK_MOUSE_WITH_OBJECT)
						&& procOptions.contains(ProcOptions.SINGLE_OBJECT_MODE)) {
					SystemEvents.moveMouse(centre, grabbedImage);
				}
			}
		}
	}

	private Point getBigContourCentre(CvSeq bigContour) {
		Point centre = null;
		CvMoments moments = new CvMoments();
		cvMoments(bigContour, moments, 1);
		double m00 = cvGetSpatialMoment(moments, 0, 0);
		double m10 = cvGetSpatialMoment(moments, 1, 0);
		double m01 = cvGetSpatialMoment(moments, 0, 1);

		if (m00 != 0) { // calculate center
			int xCenter = (int) Math.round(m10 / m00);
			int yCenter = (int) Math.round(m01 / m00);
			centre = new Point(xCenter, yCenter);
		}
		return centre;
	}

	public IplImage getThreshImage() {
		return threshImage;
	}

	public int getMAX_CONTOUR_SIZE() {
		return MAX_CONTOUR_SIZE;
	}

	public void setMAX_CONTOUR_SIZE(int mAX_CONTOUR_SIZE) {
		MAX_CONTOUR_SIZE = mAX_CONTOUR_SIZE;
	}

}
