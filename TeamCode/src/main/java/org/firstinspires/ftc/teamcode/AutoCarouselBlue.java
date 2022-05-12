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

@Autonomous(name = "Blue Carousel")
public class AutoCarouselBlue extends Hardware {

    OpenCvInternalCamera phoneCam;
    OpenCvWebcam webcam;
    OpenCvDetector pipeline = new OpenCvDetector();
    public OpenCvDetector.ElementLocation elementLocation = OpenCvDetector.ElementLocation.ERROR;
    double forwardInches  = 0;
    int delay = 0;
    public ParkingPosition parkingPosition = ParkingPosition.STORAGE_UNIT;

        @Override
        public void runOpMode(){
            // Setup OpenCV pipeline
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
            // Select parameters for the match
            selectParameters();

            /*
             * The INIT-loop:
             * This REPLACES waitForStart!
             */
            int i = 0; // make sure that this runs for at least one second even if play has already been pressed during selectParameters()
            while ((i < 10 || !isStarted()) && !isStopRequested())
            {
                elementLocation = pipeline.getLocation();
                telemetry.addData("Realtime analysis", elementLocation);
                telemetry.addData("Frame Count", webcam.getFrameCount());
                telemetry.addData("Selected Delay",delay);
                telemetry.addData("Selected Parking Position", parkingPosition);
                telemetry.update();

                // Don't burn CPU cycles busy-looping in this sample
                sleep(100);
                i++;
            }

           // When we hit this point start has been pressed. First thing to do is save where we have the marker as being
            elementLocation = pipeline.getLocation();
            webcam.stopStreaming();
            telemetry.addData("Final location:", elementLocation);
            telemetry.update();
            sleep(delay * 1000);

            switch (elementLocation){
                case LEFT:
                    raiseClawPos(LOW_POSITION,0.7);
//                    forwardInches = 1.1;
                    forwardInches = 1.6;
                    break;
                case MIDDLE:
                    raiseClawPos(MIDDLE_POSITION,0.7);
//                    forwardInches = 2.5;
                    forwardInches = 3;
                    break;
                case RIGHT:
                case ERROR:
                    raiseClawPos(HIGH_POSITION,0.7);
//                    forwardInches = 3.1;
                    forwardInches = 3.6;
                    break;
            }
            //@TODO: combine these two driving commands
            //move diagonally towards the Shipping Hub
            encoderDrive(0.5, 33, 0, 33, 0);
            while(clawPulley.isBusy()) {
                telemetry.addData("Status","waiting for clawPulley");
                telemetry.update();
                idle();
            }

            //move forward a little
            encoderDriveAnd(0.3, forwardInches,  forwardInches, forwardInches, forwardInches);
            sleep(1000);
            //release the pre-load box
            telemetry.addData("Status","releasing pre-load box");
            telemetry.update();
            grabberL.setPower(-1);
            grabberR.setPower(-1);
            sleep(1200);
            grabberL.setPower(0);
            grabberR.setPower(0);

            // move back
            telemetry.addData("Status","Going to Carousel");
            telemetry.update();
            encoderDriveAnd(0.7, -(12.5 +forwardInches), -(12.5 +forwardInches), -(12.5 +forwardInches), -(12.5 +forwardInches));
            rotateToPos(90, 1);
            raiseClawPos(LOW_POSITION,0.6);
            encoderDriveAnd(0.7,-32,-32,-32,-32);
            double before = getHeading();
            //approach the carousel diagonally
            encoderDrive(0.3,0,-7,0,-7);


            // Spin duck
            telemetry.addData("Status","Spinning Duck");
            telemetry.update();
            carousel.setPower(1); // the direction was flipped because it was inexplicably going the wrong way
            sleep(3000);
            carousel.setPower(0);


            // GO TO Storage unit:
            // move away from the carousel
            telemetry.addData("Status", "Parking in " + parkingPosition);
            telemetry.update();
            //this needs to move a little to the right too so the duck doesn't get in its way
            //or Raphi can add something to stop the duck from going underneath the robot
            encoderDriveAnd(0.7, 3, 3, 3, 3);

            telemetry.addData("status", "rotating");
            telemetry.update();
            rotateToPos(90, 1);

            // Go to storage unit
            if(parkingPosition == ParkingPosition.STORAGE_UNIT) {
                encoderDriveAnd(0.8, -22, 22, -22, 22);
                encoderDriveAnd(0.6, -5, -5, -5, -5);
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
        while(!delaySelected && !isStarted() && !isStopRequested()){
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

            telemetry.addData("Currently Selecting", "Delay");
            telemetry.addData("Press Dpad up to raise value","Press Dpad down to lower value");
            telemetry.addData("Press A to select","!");
            telemetry.addData("Current selection", delay);
            telemetry.update();
            idle();

        }

        // Make a list to iterate through with the options
        List<ParkingPosition> positions = new ArrayList<>();
        positions.add(ParkingPosition.STORAGE_UNIT);
        positions.add(ParkingPosition.WAREHOUSE_JUST_INSIDE);
        positions.add(ParkingPosition.WAREHOUSE_TOWARDS_SHARED_HUB);
        positions.add(ParkingPosition.WAREHOUSE_AGAINST_BACK_WALL);
        int num = 0;

        while(!parkingSelected && !isStarted() && !isStopRequested()){
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


