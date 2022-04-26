package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.openftc.easyopencv.OpenCvCamera;
import org.openftc.easyopencv.OpenCvCameraFactory;
import org.openftc.easyopencv.OpenCvCameraRotation;
import org.openftc.easyopencv.OpenCvInternalCamera;
import org.openftc.easyopencv.OpenCvWebcam;

import java.util.ArrayList;
import java.util.List;

@Autonomous(name = "Blue Warehouse")
public class AutoWarehouseBlue extends Hardware {
    OpenCvInternalCamera phoneCam;
    OpenCvWebcam webcam;
    OpenCvDetector pipeline = new OpenCvDetector();
    public OpenCvDetector.ElementLocation elementLocation = OpenCvDetector.ElementLocation.ERROR;
    int diff;
    int delay = 0;
    public ParkingPosition parkingPosition = ParkingPosition.STORAGE_UNIT;

    @Override
    public void runOpMode(){
        selectParameters();
        hardwareSetup();
        imuSetup();
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
        sleep(delay * 1000);

        double forwardInches = 0;

        switch (elementLocation){
            case LEFT:
                raiseClawPos(LOW_POSITION,0.7);
                forwardInches = 0.9;
                break;
            case MIDDLE:
                raiseClawPos(MIDDLE_POSITION,0.7);
                forwardInches = 2.5;
                break;
            case RIGHT:
            case ERROR:
                raiseClawPos(HIGH_POSITION,0.7);
                forwardInches = 3.2 ;
                break;
        }

        //@TODO: Combine these two driving commands
        //move diagonally towards the Shipping Hub
        encoderDrive(0.6, 0, 33, 0, 33);
        while(clawPulley.isBusy()) {
            telemetry.addData("Status","waiting for clawPulley");
            telemetry.update();
            idle();
        }
        //move forward a little
        encoderDriveAnd(0.3, forwardInches,  forwardInches, forwardInches, forwardInches);

        telemetry.addData("Status","releasing pre-load box");
        telemetry.update();

        //release the pre-load box
        grabberL.setPower(-1);
        grabberR.setPower(-1);
        sleep(1000);
        grabberL.setPower(0);
        grabberR.setPower(0);

        // move back
        raiseClawPos(LOW_POSITION,0.6);
        encoderDriveAnd(0.8, -(17 +forwardInches), -(17 +forwardInches), -(17 +forwardInches), -(17 +forwardInches));

        // Park in Warehouse
        rotateToPos(90,1);
        encoderDriveAnd(0.6,37,37,37,37);

        if(parkingPosition == ParkingPosition.WAREHOUSE_TOWARDS_SHARED_HUB || parkingPosition == ParkingPosition.WAREHOUSE_AGAINST_BACK_WALL) {
            encoderDriveAnd(0.7, -19, 19, -19, 19);
        }
        if(parkingPosition == ParkingPosition.WAREHOUSE_AGAINST_BACK_WALL){
            encoderDriveAnd(0.7,12,12,12,12);
        }

        // When done put the pulley all the way back down so teleop starts with it at 0
        raiseClawPosAndStop(0,0.8);

    }

    public void selectParameters(){
        boolean delaySelected = false;
        boolean parkingSelected = false;
        Button dpadUp = new Button(false);
        Button dpadDown = new Button(false);
        Button a = new Button(false);

        // Delay
        // Parking Position
        // Start location ?
        while(!delaySelected && !isStopRequested()){
            dpadUp.update(gamepad1.dpad_up);
            dpadDown.update(gamepad1.dpad_down);
            a.update(gamepad1.a);

            telemetry.update();
            if(dpadUp.isNewlyReleased()){
                delay++;
            } else if(dpadDown.isNewlyReleased() && delay > 0){
                delay--;
            } else if (a.isNewlyPressed()){
                delaySelected = true;

            }
            idle();

            telemetry.addData("Currently Selecting", "Delay");
            telemetry.addData("DelaySelected",delaySelected);
            telemetry.addData("Press Dpad up to raise value","Press Dpad down to lower value");
            telemetry.addData("Press A to select","!");
            telemetry.addData("Current selection", delay);
            telemetry.update();
        }
        List<ParkingPosition> positions = new ArrayList<>();
        positions.add(ParkingPosition.STORAGE_UNIT);
        positions.add(ParkingPosition.WAREHOUSE_JUST_INSIDE);
        positions.add(ParkingPosition.WAREHOUSE_TOWARDS_SHARED_HUB);
        positions.add(ParkingPosition.WAREHOUSE_AGAINST_BACK_WALL);
        int num = 0;

        while(!parkingSelected && !isStopRequested()){
            dpadUp.update(gamepad1.dpad_up);
            dpadDown.update(gamepad1.dpad_down);
            a.update(gamepad1.a);

            parkingPosition = positions.get(num);
            if(dpadUp.isNewlyPressed()){
                if(num < positions.size() - 1){
                    num++;
                } else {
                    num = 0;
                }
            } else if (dpadDown.isNewlyPressed()){
                if(num >= 1){
                    num--;
                } else {
                    num = positions.size() -1 ;
                }
            } else if(a.isNewlyPressed()){
                parkingSelected = true;
            }

            telemetry.addData("Selected Delay",delay);
            telemetry.addData("Currently Selecting", "Parking Position");
            telemetry.addData("Press Dpad up to raise value","Press Dpad down to lower value");
            telemetry.addData("Press A to make final","!");
            telemetry.addData("Current selection", parkingPosition);
            telemetry.update();

            idle();
        }
        telemetry.addData("Selected Delay",delay);
        telemetry.addData("Selected Parking Position", parkingPosition);
        telemetry.update();

    }

}


