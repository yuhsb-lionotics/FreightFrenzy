package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.util.Range;
@Autonomous(name = "Auto")
public class Auto extends Hardware {

    public int delaySeconds = 0; //seconds to wait for alliance
    public String startingPosition = "Carousel";

    @Override
    public void runOpMode(){
        hardwareSetup();

        selectParameters();
        telemetry.addData("Status","Waiting for Start");
        //Display all parameter values (so far delaySeconds and startingPosition)
        telemetry.addData("delaySeconds: ", delaySeconds);
        telemetry.addData("startingPosition: ", startingPosition);
        telemetry.update();

        sleep(delaySeconds * 100); //What is this for?

        waitForStart();
        telemetry.update();
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
        if(startingPosition.equals("Carousel")){
           idle();
        } else if (startingPosition.equals("Warehouse")){
            idle();
        }
        //encoderDrive(0.4,10,-10,10,-10);
        sleep(100); //Why?
        encoderDrive(0.4,14.14,14.14,14.14,14.14);
        // Fake values for now. Just showing how to call it

        // Then move to the place.


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

                    if(gamepad1.x) {
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
