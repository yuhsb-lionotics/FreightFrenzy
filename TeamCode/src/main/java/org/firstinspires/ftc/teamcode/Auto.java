package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.util.Range;
@Autonomous(name = "Auto")
public class Auto extends Hardware {
    public int delaySeconds = 0; //seconds to wait for alliance
    public String startingPosition = "Carousel";

    public int duckStartLocation = 1; //ilegal
    public int drivers = 1; //number of drivers on team
    public String carouselStatus = "Waiting for start"; // status of variable selector 'carousel'

    @Override
    public void runOpMode(){
        hardwareSetup();
        //selectParameters();
        //TensorflowDetector recognizer = new TensorflowDetector(10,20);
        waitForStart();
        telemetry.update();
        encoderDrive(0.7,10,-10,10,-10);
        // Fake values for now. Just showing how to call it

        while(opModeIsActive()){
           /// recognizer.listObjects();
            sleep(20);
        }

        // Then move to the place.
        // front backwarsd to the righjt
        //

    }

    public void selectParameters() {
        String currentParameter = "Delay";
        while (!gamepad1.a) { //pressing 'a' will end the selection
            telemetry.addLine("Select " + currentParameter);

            switch (currentParameter) {
                case "Delay":
                    if (gamepad1.dpad_up) {
                        delaySeconds++;
                    }
                    if (gamepad1.dpad_down) {
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
