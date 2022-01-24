package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.openftc.easyopencv.OpenCvCamera;
import org.openftc.easyopencv.OpenCvCameraFactory;
import org.openftc.easyopencv.OpenCvCameraRotation;
import org.openftc.easyopencv.OpenCvInternalCamera;
import org.openftc.easyopencv.OpenCvWebcam;
@Disabled
@Autonomous(name = "AutoForDinner")
public class AutoForDinner extends Hardware {
    OpenCvInternalCamera phoneCam;
    OpenCvWebcam webcam;
    OpenCvDetector pipeline = new OpenCvDetector();
    public OpenCvDetector.ElementLocation elementLocation = OpenCvDetector.ElementLocation.ERROR;
    static final double FORWARD_WHEEL_INCHES = 20/Math.sqrt(2);
    static final double SIDE_WHEEL_INCHES = 8.4/Math.sqrt(2);

    @Override
    public void runOpMode(){
        int cameraMonitorViewId = hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        webcam = OpenCvCameraFactory.getInstance().createWebcam(hardwareMap.get(WebcamName.class, "Webcam 1"), cameraMonitorViewId);
        webcam.setPipeline(pipeline);
        webcam.setMillisecondsPermissionTimeout(2500); // Timeout for obtaining permission is configurable. Set before opening.
        webcam.openCameraDeviceAsync(new OpenCvCamera.AsyncCameraOpenListener()
        {
            @Override
            public void onOpened()
            {
                webcam.startStreaming(320, 240, OpenCvCameraRotation.UPRIGHT);
            }

            @Override
            public void onError(int errorCode)
            {
                /*
                 * This will be called if the camera could not be opened
                 */
                elementLocation = OpenCvDetector.ElementLocation.ERROR;
            }
        });
        hardwareSetup();
        imuSetup();
        /*
         * The INIT-loop:
         * This REPLACES waitForStart!
         */
        while (!isStarted() && !isStopRequested())
        {
            elementLocation = pipeline.getLocation();
            telemetry.addData("Realtime analysis", elementLocation);
            telemetry.addData("Frame Count", webcam.getFrameCount());
            telemetry.update();

            // Don't burn CPU cycles busy-looping in this sample
            sleep(100);
        }
        // When we hit this point start has been pressed. First thing to do is save where we have the
        // marker as being

        elementLocation = pipeline.getLocation();
        webcam.stopStreaming();
        telemetry.addData("Final:", elementLocation);
        telemetry.update();
        startGrabbing(0.9);
        switch (elementLocation){
            case LEFT:
                encoderDrive(0.4,SIDE_WHEEL_INCHES,-SIDE_WHEEL_INCHES,SIDE_WHEEL_INCHES,-SIDE_WHEEL_INCHES);
                encoderDrive(0.4,24,24,24,24);
                break;

            case MIDDLE:
                encoderDrive(0.4,FORWARD_WHEEL_INCHES,FORWARD_WHEEL_INCHES,FORWARD_WHEEL_INCHES,FORWARD_WHEEL_INCHES);
                break;

            case RIGHT:
                encoderDrive(0.4,-SIDE_WHEEL_INCHES,SIDE_WHEEL_INCHES,-SIDE_WHEEL_INCHES,SIDE_WHEEL_INCHES);
                encoderDrive(0.4,FORWARD_WHEEL_INCHES,FORWARD_WHEEL_INCHES,FORWARD_WHEEL_INCHES,FORWARD_WHEEL_INCHES);
                break;

            case ERROR:
                break;
        }
        sleep(1000);
        stopGrabbing();
    }

}


