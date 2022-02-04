package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.openftc.easyopencv.OpenCvCamera;
import org.openftc.easyopencv.OpenCvCameraFactory;
import org.openftc.easyopencv.OpenCvCameraRotation;
import org.openftc.easyopencv.OpenCvInternalCamera;
import org.openftc.easyopencv.OpenCvWebcam;

@Autonomous(name = "Blue Warehouse")
public class AutoWarehouseBlue extends Hardware {
    OpenCvInternalCamera phoneCam;
    OpenCvWebcam webcam;
    OpenCvDetector pipeline = new OpenCvDetector();
    public OpenCvDetector.ElementLocation elementLocation = OpenCvDetector.ElementLocation.ERROR;
    int diff;
    ParkingPosition parkingPosition = ParkingPosition.WAREHOUSE_TOWARDS_SHARED_HUB;

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

        double forwardInches = 0;

        switch (elementLocation){
            case LEFT:
//                    raiseClawPosAndStop(LOW_POSITION, 0.7);
                raiseClawPos(LOW_POSITION,0.7);
                forwardInches = 0.7;
                break;
            case MIDDLE:
//                    raiseClawPosAndStop(MIDDLE_POSITION, 0.7);
                raiseClawPos(MIDDLE_POSITION,0.7);

                forwardInches = 2.5;
                break;
            case RIGHT:
            case ERROR:
//                    raiseClawPosAndStop(HIGH_POSITION, 0.7);
                raiseClawPos(HIGH_POSITION,0.7);
                forwardInches = 3;
                break;
        }

        //@TODO: Combine these two driving commands
        //move diagonally towards the Shipping Hub
        encoderDrive(0.6, 0, 33, 0, 33);
        //move forward a little
        encoderDriveAnd(0.3, forwardInches,  forwardInches, forwardInches, forwardInches);

        while(clawPulley.isBusy()) {
            telemetry.addData("Status","waiting for clawPulley");
            telemetry.update();
            idle();
        }
        telemetry.addData("Status","releasing pre-load box");
        telemetry.update();

        //release the pre-load box
        grabber.setPower(-1);
        sleep(1000);
        grabber.setPower(0);
        telemetry.update();
        // move back

        encoderDriveAnd(0.8, -(16 +forwardInches), -(16 +forwardInches), -(16 +forwardInches), -(16 +forwardInches));



        // Park in Warehouse
        rotateToPos(90,1);
        raiseClawPos(LOW_POSITION,0.6);
        encoderDrive(0.6,35,35,35,35);
        encoderDrive(0.6,-15,15,-15,15);

    }

}


