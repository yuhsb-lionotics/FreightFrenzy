package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.Range;

@com.qualcomm.robotcore.eventloop.opmode.TeleOp(name="TeleOp")
public class TeleOp extends Hardware{
    double maxDrivingPower = 0.8;
    Button gamepad1back = new Button(false);
    Button gamepad1a = new Button(false);
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

        //Update the button objects with the newest value
        gamepad1back.update(gamepad1.back);
        gamepad1a.update(gamepad1.a);
        gamepad2a.update(gamepad2.a);
        while (opModeIsActive()) {
            gamepad1back.update(gamepad1.back);
            gamepad1a.update(gamepad1.a);
            gamepad2a.update(gamepad2.a);

            if(gamepad1back.isNewlyPressed()) {
                if (maxDrivingPower == 0.8) {
                    maxDrivingPower = 0.4;
                } else {
                    maxDrivingPower = 0.8;
                }
            }

            //driving
            if(gamepad1.dpad_up) {
                //move forward
                setPowers(0.3,0.3,0.3,0.3);
            } else if (gamepad1.dpad_down) {
                //move backward
                setPowers(-0.3,-0.3,-0.3,-0.3);
            } else if (gamepad1.dpad_right) {
                //rotate in a circle around the shipping hub
                double MAX_POWER = 0.5;
                //@TODO: adjust the following value. It should be a little too wide on purpose
                double POWER_RATIO = 0.6; //larger for a wider circle
                setPowers(POWER_RATIO * MAX_POWER, -POWER_RATIO * MAX_POWER, -MAX_POWER, MAX_POWER);
            } else if (gamepad1.dpad_left) {
                //rotate in a circle around the shipping hub
                double MAX_POWER = 0.5;
                double POWER_RATIO = 0.6; //larger for a wider circle
                setPowers(-POWER_RATIO * MAX_POWER, POWER_RATIO * MAX_POWER, MAX_POWER, -MAX_POWER);
            } else if (gamepad1.right_bumper) {
                //rotate clockwise in place
                setPowers(0.3,-0.3,0.3,-0.3);
            } else if (gamepad1.left_bumper) {
                //rotate cc in place
                setPowers(-0.3,0.3,-0.3,0.3);
            } else {
                telemetry.addData("tank control maxDrivingPower", maxDrivingPower);
                tankControl(maxDrivingPower);
            }

            //turn grabbing on/off when button A is pressed
            if(gamepad2a.isNewlyPressed() || gamepad1a.isNewlyPressed()) {
                if(tryingToGrab) stopGrabbing();
                else startGrabbing(0.5);
            }

            //check if grabbing needs to be stopped, and stop if so
            updateGrabbing();

            // Eject the cube if b is pressed
            if((gamepad1.b || gamepad2.b)) {
                if (tryingToGrab) stopGrabbing();
                grabber.setPower(-0.9);
            } else if (!tryingToGrab){
                grabber.setPower(0);
            }
            telemetry.addData("TryingToGrab:",tryingToGrab);
            telemetry.addData("Pressed",wheelTouchSensor.isPressed());
            telemetry.addData("gamepad2b",gamepad2.b);

            //Control clawPulley
            if (gamepad2.dpad_up) {
                clawPulley.setPower(0.6);
                sleep(50);
            } else if (gamepad2.dpad_down) {
                clawPulley.setPower(-0.7);
            } else if (gamepad2.dpad_left) {
                //raise claw to highest level of shipping hub
                raiseClawPos(HIGH_POSITION,0.7);
            } else if (gamepad2.dpad_right) {
                raiseClawPos(SHARED_HUB_POSITION, 0.6);
            } else if(!clawPulley.isBusy()){
                clawPulley.setPower(0);
            }
            //stop the claw pulley if it's at its destination
            updateRaise();

            //control carousel
            if (gamepad2.x) {
                carousel.setPower(1);
            } else if (gamepad2.y){
                carousel.setPower(-1);
            } else if (gamepad1.x) {
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
