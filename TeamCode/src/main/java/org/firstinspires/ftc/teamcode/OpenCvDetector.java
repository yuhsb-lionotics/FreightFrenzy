package org.firstinspires.ftc.teamcode;

import android.util.Log;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.openftc.easyopencv.OpenCvPipeline;

import java.util.ArrayList;
import java.util.List;

// https://gist.github.com/oakrc/12a7b5223df0cb55d7c1288ce96a6ab7.
// Thank you Team Wolf Corp (#12525)
public class OpenCvDetector extends OpenCvPipeline {

    public enum ElementLocation {
        LEFT,
        RIGHT,
        MIDDLE,
        ERROR
    }

    public ElementLocation location;
    // Places within the frame to not care about colored objects
    private static final int TOP_EXCLUDE = 80;
    private static final int BOTTOM_EXCLUDE = 240;
    public final int LEFT_BOUND = 106;
    public final int RIGHT_BOUND = 212;
    public OpenCvDetector(){
        location = ElementLocation.ERROR;
    }

    @Override
    public Mat processFrame(Mat input) {
        // "Mat" stands for matrix, which is basically the image that the detector will process
        // the input matrix is the image coming from the camera
        // the function will return a matrix to be drawn on your phone's screen

//        Log.w("OpenCv Pipeline","Starting frame proccessing.");
        // Make a working copy of the input matrix in HSV
        Mat mat = new Mat();
        Imgproc.cvtColor(input, mat, Imgproc.COLOR_RGB2HSV);

//        if (mat.empty()) {
//            location = ElementLocation.ERROR;
//            return input;
//        }


        // We create a HSV range for yellow to detect ducks / TSE
        // NOTE: In OpenCV's implementation, Hue values are half the real value
        // Use an HSV color picker for a different color.
        Scalar lowHSV = new Scalar(20, 100, 100); // lower bound HSV for yellow
        Scalar highHSV = new Scalar(30, 255, 255); // higher bound HSV for yellow


        // Narrow down the image to the part where we are actually looking for stuff.
        // Right now just limiting vertical space. We can also do horizontal space though.
        // This gets tuned!
        Mat portion = mat.submat(TOP_EXCLUDE, BOTTOM_EXCLUDE,0,320);

        // Find things in our yellow range and put them in thresh
        Mat thresh = new Mat();
        Core.inRange(portion, lowHSV, highHSV, thresh);
        // Use canny edge detection to find edges of threshold objects
        Mat edges = new Mat();
        Imgproc.Canny(thresh, edges, 100, 300);
        // Use findCountours to smooth it out
        List<MatOfPoint> contours = new ArrayList<>();
        Mat hierarchy = new Mat();
        Imgproc.findContours(edges, contours, hierarchy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);
        // Parse said contours to make boxes
        MatOfPoint2f[] contoursPoly  = new MatOfPoint2f[contours.size()];
        Rect[] boundRect = new Rect[contours.size()];
        for (int i = 0; i < contours.size(); i++) {
            contoursPoly[i] = new MatOfPoint2f();
            Imgproc.approxPolyDP(new MatOfPoint2f(contours.get(i).toArray()), contoursPoly[i], 3, true);
            boundRect[i] = Imgproc.boundingRect(new MatOfPoint(contoursPoly[i].toArray()));
        }
        for (int i = 0; i != boundRect.length; i++) {
            // draw red bounding rectangles on mat
            // the mat has been converted to HSV so we need to use HSV as well
            Imgproc.rectangle(mat, boundRect[i], new Scalar(0.5, 76.9, 89.8),3);

            //Log.i("Item Location", String.valueOf(boundRect[i]));
            // The frame is 320 wide. For now just splitting it into three.
            // Also only accepting from the middle part of the frame, to help filter out other yellow things
            // TODO: Tune the boundaries for the different places, and also where within the frame to not look

            if (boundRect[i].x < LEFT_BOUND){
                this.location = ElementLocation.LEFT;
                Log.i("LOCATION", String.valueOf(this.getLocation()));
            } else if (boundRect[i].x < RIGHT_BOUND){
                this.location = ElementLocation.MIDDLE;
                Log.i("LOCATION","MIDDLE");
            } else if (boundRect[i].x > RIGHT_BOUND) {
                this.location = ElementLocation.RIGHT;
                Log.i("LOCATION","RIGHT");

            }

        }
        // Draw on bounds to be helpful

        Imgproc.rectangle(mat, new Point(0,0), new Point(320, TOP_EXCLUDE), new Scalar(0.5, 76.9, 89.8), -1);
        Imgproc.rectangle(mat, new Point(0,240), new Point(320, BOTTOM_EXCLUDE), new Scalar(0.5, 76.9, 89.8), -1);
        Imgproc.line(mat, new Point(LEFT_BOUND, 0), new Point(LEFT_BOUND, 240), new Scalar(0.5, 76.9, 89.8), 2 );
        Imgproc.line(mat, new Point(RIGHT_BOUND, 0), new Point(RIGHT_BOUND, 240), new Scalar(0.5, 76.9, 89.8), 2 );


        // Convert back to RGB for the camera frame
        Mat output = new Mat();
        Imgproc.cvtColor(mat, output, Imgproc.COLOR_HSV2RGB);
        return output;
    }

    public ElementLocation getLocation() {
        return this.location;
    }
}