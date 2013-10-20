package imageProc;

import static com.googlecode.javacv.cpp.opencv_core.CV_AA;
import static com.googlecode.javacv.cpp.opencv_core.CV_RGB;
import static com.googlecode.javacv.cpp.opencv_core.cvCircle;
import static com.googlecode.javacv.cpp.opencv_core.cvGetSeqElem;
import static com.googlecode.javacv.cpp.opencv_core.cvPoint;
import static com.googlecode.javacv.cpp.opencv_imgproc.CV_COUNTER_CLOCKWISE;
import static com.googlecode.javacv.cpp.opencv_imgproc.CV_POLY_APPROX_DP;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvApproxPoly;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvConvexHull2;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvConvexityDefects;
import imageProc.eventHandling.SystemEvents;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import websocketClient.WSClient;

import com.googlecode.javacpp.Loader;
import com.googlecode.javacpp.Pointer;
import com.googlecode.javacv.cpp.opencv_core.CvContour;
import com.googlecode.javacv.cpp.opencv_core.CvMemStorage;
import com.googlecode.javacv.cpp.opencv_core.CvPoint;
import com.googlecode.javacv.cpp.opencv_core.CvRect;
import com.googlecode.javacv.cpp.opencv_core.CvSeq;
import com.googlecode.javacv.cpp.opencv_core.IplImage;
import com.googlecode.javacv.cpp.opencv_imgproc.CvConvexityDefect;

public class HandProc {

	private CvSeq bigContour;
	private CvRect boundbox;
	private IplImage grabbedImage;

	private int MIN_FINGER_DEPTH = 20;
	private int MAX_FINGER_ANGLE = 60; // degrees
	private List<Point> tipPts, foldPts;
	private List<Float> depths;
	private List<Point> fingerTips;
	private CvMemStorage hullStorage, approxStorage, defectsStorage;

	private HandGestures lastHandState;
	private Point centre;

	public HandProc() {
		this.hullStorage = CvMemStorage.create();
		this.approxStorage = CvMemStorage.create();
		this.defectsStorage = CvMemStorage.create();
		this.fingerTips = new ArrayList<Point>();
		tipPts = new ArrayList<Point>();
		foldPts = new ArrayList<Point>();
		depths = new ArrayList<Float>();
	}

	public void HandProcUpdate(List<ProcOptions> procOptions,
			List<HandGestures> handGestures, CvSeq bigContour, CvRect boundbox,
			Point centre, IplImage grabbedImage) {
		this.bigContour = bigContour;
		this.grabbedImage = grabbedImage;
		this.boundbox = boundbox;
		this.centre = centre;

		detectFingerTips();
		if (procOptions.contains(ProcOptions.ENABLE_HAND_GESTURES)) {
			evaluateHandState(handGestures);
		}
	}

	public void HandProcUpdate(CvSeq bigContour, CvRect boundbox, Point centre) {
		this.bigContour = bigContour;
		this.boundbox = boundbox;
		this.centre = centre;

	}

	public void detectFingerTips() {
		fingerTips.clear();
		tipPts.clear();
		foldPts.clear();
		depths.clear();

		CvSeq reduceContour = cvApproxPoly(bigContour,
				Loader.sizeof(CvContour.class), approxStorage,
				CV_POLY_APPROX_DP, 3, 1);
		// reduce the number of points in the contour in order to increase
		// performance as we only require
		// an outline of the contour in order to apoximate the convexhull

		CvSeq defects = cvConvexityDefects(
				reduceContour,
				cvConvexHull2(reduceContour, hullStorage, CV_COUNTER_CLOCKWISE,
						0), defectsStorage);
		// find the convex hull and defect differences between the contour and
		// convex hull in order to identify the defects
		// eventually to identify the fold points

		int defectsTotal = defects.total();

		for (int i = 0; i < defectsTotal; i++) {
			Pointer pntr = cvGetSeqElem(defects, i);
			CvConvexityDefect cdf = new CvConvexityDefect(pntr);

			CvPoint startPt = cdf.start();
			tipPts.add(new Point((int) Math.round(startPt.x()), (int) Math
					.round(startPt.y())));

			CvPoint depthPt = cdf.depth_point();
			foldPts.add(new Point((int) Math.round(depthPt.x()), (int) Math
					.round(depthPt.y())));

			depths.add(cdf.depth());
		}

		reduceDefects();

		if (WSClient.debugMode == true && grabbedImage != null) {
			for (Point p : fingerTips) {
				cvCircle(grabbedImage, cvPoint(p.x, p.y), 10,
						CV_RGB(255, 130, 0), 5, CV_AA, 0);
			}
		}

	}

	private void reduceDefects() {
		int target = Math.min(Math.min(depths.size(), tipPts.size()),
				foldPts.size());
		for (int i = 0; i < target; i++) {
			if (depths.get(i) < MIN_FINGER_DEPTH) // defect too shallow
				continue;
			// look at fold points on either side of a tip
			int pdx = (i == 0) ? (target - 1) : (i - 1); // predecessor of i
			int sdx = (i == target - 1) ? 0 : (i + 1); // successor of i

			int angle = angleBetween(tipPts.get(i), foldPts.get(pdx),
					foldPts.get(sdx));
			if (angle >= MAX_FINGER_ANGLE)
				continue; // angle between finger and folds too wide

			// this point is probably a fingertip, so add to list
			fingerTips.add(tipPts.get(i));
		}
	}

	private int angleBetween(Point tip, Point next, Point prev)
	// calculate the angle between the tip and its neighboring folds
	// (in integer degrees)
	{
		return Math.abs((int) Math.round(Math.toDegrees(Math.atan2(next.x
				- tip.x, next.y - tip.y)
				- Math.atan2(prev.x - tip.x, prev.y - tip.y))));
	}

	private void evaluateHandState(List<HandGestures> handGestures) {

		if ((handGestures.contains(HandGestures.ALL) || handGestures
				.contains(HandGestures.OPEN_PALM))
				&& (fingerTips.size() >= 4 && fingerTips.size() <= 5)) {
			lastHandState = HandGestures.OPEN_PALM;
			System.out.println("open palm!");
			SystemEvents.endMouseDrag();
		}

		if ((handGestures.contains(HandGestures.ALL) || handGestures
				.contains(HandGestures.GRAB))
				&& (lastHandState == HandGestures.OPEN_PALM && fingerTips
						.size() == 0)) {
			lastHandState = HandGestures.GRAB;
			SystemEvents.startMouseDrag();
			SystemEvents.moveMouse(centre, grabbedImage);
			System.out.println("GRAB!");
		}
	}

	public int getMIN_FINGER_DEPTH() {
		return MIN_FINGER_DEPTH;
	}

	public void setMIN_FINGER_DEPTH(int mIN_FINGER_DEPTH) {
		MIN_FINGER_DEPTH = mIN_FINGER_DEPTH;
	}

	public int getMAX_FINGER_ANGLE() {
		return MAX_FINGER_ANGLE;
	}

	public void setMAX_FINGER_ANGLE(int mAX_FINGER_ANGLE) {
		MAX_FINGER_ANGLE = mAX_FINGER_ANGLE;
	}

	public List<Point> getFingerTips() {
		return fingerTips;
	}

}
