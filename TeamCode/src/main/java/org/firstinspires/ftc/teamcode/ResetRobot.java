package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.Range;

@com.qualcomm.robotcore.eventloop.opmode.TeleOp(name="Reset Robot")
public class ResetRobot extends Hardware{
    double maxDrivingPower = 0.3;
    Button gamepad1a = new Button(false);

    @Override
    public void runOpMode() throws InterruptedException {
        hardwareSetup();
        frontLeft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        frontRight.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        backLeft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        backRight.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        int clawPos = 1;
        waitForStart();

        //Update the button object with the newest value
        gamepad1a.update(gamepad1.a);
        while (opModeIsActive()) {
            gamepad1a.update(gamepad1.a);
            telemetry.addData("maxDrivingPower", maxDrivingPower);
            tankControl(maxDrivingPower);

            //turn grabbing on/off when button A is pressed
            if (gamepad1a.isNewlyPressed()) {
                if (tryingToGrab) stopGrabbing();
                else startGrabbing(0.5);
            }

            //check if grabbing needs to be stopped, and stop if so
            updateGrabbing();

            // Eject the cube if b is pressed
            if(gamepad1.b && !tryingToGrab){
                grabberL.setPower(-0.9);
                grabberR.setPower(-0.9);
            } else if (!tryingToGrab){
                grabberL.setPower(0);
                grabberR.setPower(0);
            }
            telemetry.addData("TryingToGrab:",tryingToGrab);
            telemetry.addData("Pressed",wheelTouchSensor.isPressed());
            telemetry.addData("gamepad1.b",gamepad1.b);

            //Control clawPulley
            if (gamepad1.dpad_up) {
                clawPulley.setPower(0.2);
                sleep(50);
            } else if (gamepad1.dpad_down) {
                clawPulley.setPower(-0.25);
            } else {
                clawPulley.setPower(0);
            }
            //stop the claw pulley if it's at its destination
            if(clawPulley.getMode() == DcMotor.RunMode.RUN_TO_POSITION && !clawPulley.isBusy()) {
                clawPulley.setPower(0);
                clawPulley.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            }

            //control carousel
            if (gamepad1.x) {
                carousel.setPower(1);
            } else if (gamepad1.y){
                carousel.setPower(-1);
            } else{
                carousel.setPower(0);
            }

            telemetry.addData("ClawPos", clawPos);
            telemetry.addData("clawPulley mode", clawPulley.getMode());
            telemetry.addData("clawPulley position", clawPulley.getCurrentPosition());
            telemetry.addData("clawPulley power", clawPulley.getPower());
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
