package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.util.Range;
@Autonomous(name = "Auto")
@Disabled
public class Auto extends Hardware {

    public int delaySeconds = 0; //seconds to wait for alliance
    public String startingPosition = "Carousel";
    public int iterator = 0;
    public int duckPosition = 0;
    double inchesToGo;
    public int drivers = 1; //number of drivers on team
    public String carouselStatus = "Waiting for start"; // status of variable selector 'carousel'

    @Override
    public void runOpMode() {
        hardwareSetup();
        imuSetup();
//        //selectParameters();
//        telemetry.addData("Status","Waiting for Start");
//        //Display all parameter values (so far delaySeconds and startingPosition)
//        telemetry.addData("delaySeconds: ", delaySeconds);
//        telemetry.addData("startingPosition: ", startingPosition);
//        telemetry.update();
//
//        //Delay the start of the program according to the specified setting
//        sleep(delaySeconds * 1000);
//        telemetry.addLine("Loading preload box. Press dpad up to close");
//        telemetry.update();
//        claw.setPosition(1);
//        while(!gamepad1.dpad_up){
//            idle();
//        }


        waitForStart();
        // Move forward
        // Move to each place of the duck, checking the color sensor
        // claw to correct position
        // move to the Shipping Hub (Changes based on starting position)
        // open claw
        // Move back
        // move to carousel, rotate to get the motor on the carousel
        // spin carousel

        // Move to final place for points

        // Place in the correct location
        if (startingPosition.equals("Carousel")) {
            idle();
        } else if (startingPosition.equals("Warehouse")) {
            idle();
        }

        encoderDrive(0.4, 1, 1, 1, 1);
        encoderDrive(0.4, 10.5, -10.5, 10.5, -10.5);
        encoderDrive(0.4, 10.5, 10.5, 10.5, 10.5);
//        int preHitPos = frontRight.getCurrentPosition();
//        iterator = encoderUntilHit(0.5, -20, 20, -20, 20);
//        if(iterator != 0) {
//            iterator = preHitPos - iterator;
//        }
//
//
//        // NEEDS UPDATE FOR NEW FUNCTION
//        if (iterator <= 700){
//            duckPosition = 0;
//            // LEFT -- Bottom
//        } else if (iterator < 1200){
//            duckPosition = 1;
//            // MIDDLE
//        } else if (iterator > 1200 || iterator == 0){
//            duckPosition = 2;
//            // RIGHT -- top
//        }
//        telemetry.addData("iterator", iterator);
//        telemetry.addData("DuckPosition",duckPosition);
//        telemetry.update();
//        sleep(2000);
//
//
//        raiseClawPos(800, 0.6);
//        if (iterator != 0) {
//            inchesToGo = (iterator / COUNTS_PER_INCH) + 6;
//        } else{
//            inchesToGo = 25;
//        }

        inchesToGo = 6;
        // move to the goal
        encoderDrive(0.6, inchesToGo, -inchesToGo, inchesToGo, -inchesToGo);

        // raise the arm
//        if (duckPosition == 0 ) {
//            raiseClawPos(800, 0.6);
//        } else if (duckPosition == 1){
//            raiseClawPos(1800, 0.6);
//        } else if (duckPosition == 2) {
//            raiseClawPos(2610, 0.6);
//        }

        raiseClawPos(2600,0.6);
        encoderDrive(0.6, 6,6,6,6);
        // TODO: Release claw here
        encoderDrive(0.6, -6,-6,-6,-6);
        encoderDrive(0.6, -10.5,-10.5,-10.5,-10.5);




    }

    public void selectParameters() {
        String currentParameter = "Delay";
        while (!gamepad1.a) { //pressing 'a' will end the selection
            telemetry.addLine("Select " + currentParameter);
            telemetry.addLine("To end the selection press 'a'");
            switch (currentParameter) {
                case "Delay":
                    if (gamepad1.dpad_up) {
                        delaySeconds++;
                    } else if (gamepad1.dpad_down) {
                        delaySeconds--;
                    }
                    delaySeconds = Range.clip(delaySeconds, 0, 30);
                    telemetry.addLine("delaySeconds = " + delaySeconds);
                    if (gamepad1.x) { // pressing 'x' sends selector to the next variable
                        currentParameter = "Starting Position";
                    }
                break;

                case "Starting Position":
                    //Choose between "Carousel" and "Warehouse"
                    if (gamepad1.dpad_up) {
                        startingPosition = "Carousel";
                    } else if (gamepad1.dpad_down) {
                        startingPosition = "Warehouse";
                    }
                    telemetry.addLine("startingPosition = " + startingPosition);
                    telemetry.update();
                    if(gamepad1.x) {
                        currentParameter = "Load";
                    }
                    break;
                case "Load":
                    telemetry.addLine("Loading preload box. Press dpad up to close");
                    telemetry.update();
                    //claw.setPosition(1);
                    while(!gamepad1.dpad_up){
                        idle();
                    }
                    //claw.setPosition(0.55);
                    if (gamepad1.x){
                        currentParameter = "Delay";
                    }
                break;


            }

            //Output to telemetry and sleep
            telemetry.update();
            sleep(200);
        }
    }
}
