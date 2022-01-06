package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.Range;

@com.qualcomm.robotcore.eventloop.opmode.TeleOp(name="TeleOp")
public class TeleOp extends Hardware{
    Button gamepad2a = new Button(false);

    @Override
    public void runOpMode() throws InterruptedException {
        hardwareSetup();
        frontLeft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        frontRight.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        backLeft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        backRight.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        int clawPos = 1;
        waitForStart();
        //Update the gamepad2a button object with the newest value
        gamepad2a.update(gamepad2.a);
        while (opModeIsActive()) {
            gamepad2a.update(gamepad2.a);
            tankControl(0.8); //CHANGE THIS

            //start grabbing if button a is pressed on gamepad2
            if(gamepad2a.isNewlyPressed()) {
                if(tryingToGrab) stopGrabbing();
                else startGrabbing(0.5);
            }

            //check if grabbing needs to be stopped, and stop if so
            updateGrabbing();

            // Eject the cube if b is pressed
            if(gamepad2.b && !tryingToGrab){
                grabber.setPower(-0.9);
            } else if (!tryingToGrab){
                grabber.setPower(0);
            }
            telemetry.addData("TryingToGrab:",tryingToGrab);
            telemetry.addData("Pressed",wheelTouchSensor.isPressed());
            telemetry.addData("gamepad2b",gamepad2.b);
            //Control clawPulley
            if (gamepad2.dpad_up) {
                clawPulley.setPower(0.8);
                sleep(50);
            } else if (gamepad2.dpad_down) {
                clawPulley.setPower(-0.5);
            } else if (gamepad2.dpad_left) {
                //raise claw to highest level of shipping hub
                clawPulley.setTargetPosition(2590);
                clawPulley.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                clawPulley.setPower(0.5);

                /*Thread openThread = new Thread(() -> {raiseClawPos(2590, 0.5);
                });
//                raiseClawPos(2590, 0.5);
                openThread.start(); */
            } else if (gamepad2.dpad_right) {
                Thread closeThread = new Thread(() -> {raiseClawPos(0, 0.5);
                });
                closeThread.start();
            } else {
                clawPulley.setPower(0);
            }
            //stop the claw pulley if it's at its destination
            if(clawPulley.getMode() == DcMotor.RunMode.RUN_TO_POSITION && !clawPulley.isBusy()) {
                clawPulley.setPower(0);
                clawPulley.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            }

            //control carousel
            if (gamepad2.x) {
                carousel.setPower(0.9);
            } else if (gamepad2.y){
                carousel.setPower(-0.9);
            }
            else{
                carousel.setPower(0);
            }

            telemetry.addData("ClawPos", clawPos);
            telemetry.addData("clawPulley mode", clawPulley.getMode());
            telemetry.addData("clawPulley position", clawPulley.getCurrentPosition());
            telemetry.update();
            }
        }

    public void tankControl(double maxPower) /* 0 < maxPower <= 1 */ {
        double leftPower = -gamepad1.left_stick_y * maxPower;
        double rightPower = -gamepad1.right_stick_y * maxPower;
        double strafePower = (gamepad1.left_stick_x + gamepad1.right_stick_x)/2 * maxPower;

        //double strafePower = (gamepad1.right_trigger - gamepad1.left_trigger) * maxPower; //positive is to the right

        double strafePowerLimit = Math.min(1 - Math.abs(rightPower) , 1 - Math.abs(leftPower));
        strafePower = Range.clip(strafePower, -strafePowerLimit, strafePowerLimit);

        // This will set each motor to a power between -1 and +1 such that the equation for
        // holonomic wheels works.
        frontLeft.setPower(leftPower  + strafePower);
        backLeft.setPower(leftPower  - strafePower);
        frontRight.setPower(rightPower - strafePower);
        backRight.setPower(rightPower + strafePower);
    }
}
