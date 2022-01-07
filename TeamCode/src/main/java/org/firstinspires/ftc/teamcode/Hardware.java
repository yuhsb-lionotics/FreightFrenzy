package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.TouchSensor;
import com.qualcomm.robotcore.util.ElapsedTime;

public class Hardware extends LinearOpMode {


    // Good Luck!
    //You should put constants here

    protected DcMotor frontLeft, frontRight, backLeft, backRight, clawPulley, carousel ;
    protected CRServo grabber;
    protected TouchSensor wheelTouchSensor;
    public boolean tryingToGrab = false;
    private double grabberPower = 0;


    static final double     COUNTS_PER_MOTOR_REV    = 1120 ;    // CHECK THIS
    static final double     DRIVE_GEAR_REDUCTION    = 1.0 ;     // This is < 1.0 if geared UP
    static final double     WHEEL_DIAMETER_INCHES   = 4.0 ;     // For figuring circumference. Not sure what it is
    static final double     COUNTS_PER_INCH         = (COUNTS_PER_MOTOR_REV * DRIVE_GEAR_REDUCTION) /
            (WHEEL_DIAMETER_INCHES * Math.PI);

    public ElapsedTime runtime = new ElapsedTime();

    // Setup your drivetrain (Define your motors etc.)
    public void hardwareSetup() {

        //initialize hardware devices
        frontLeft = hardwareMap.dcMotor.get("frontLeft");
        frontRight = hardwareMap.dcMotor.get("frontRight");
        backRight = hardwareMap.dcMotor.get("backRight");
        backLeft = hardwareMap.dcMotor.get("backLeft");
        clawPulley = hardwareMap.dcMotor.get("clawPulley");
        grabber = hardwareMap.crservo.get("grabber");
        carousel = hardwareMap.dcMotor.get("carousel");
        wheelTouchSensor = hardwareMap.touchSensor.get("wheel");


        frontLeft.setDirection(DcMotor.Direction.REVERSE);
        backLeft.setDirection(DcMotor.Direction.REVERSE);
        frontRight.setDirection(DcMotor.Direction.FORWARD);
        backRight.setDirection(DcMotor.Direction.FORWARD);
        grabber.setDirection(DcMotor.Direction.REVERSE);


        frontRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        frontLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        carousel.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        clawPulley.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);


        frontLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        frontRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        backLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        backRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        clawPulley.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        carousel.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        //Use encoders
        frontLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        frontRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        backLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        backRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        clawPulley.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        carousel.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        telemetry.addData("Status:", "Setup Complete");
        telemetry.update();
    }

    public void rotateClockwise(double power) {
        frontLeft.setPower(power);
        backLeft.setPower(power);
        frontRight.setPower(-power);
        backRight.setPower(-power);
    }

    public void strafe(double forwardLeftPower, double forwardRightPower) {
        frontLeft.setPower(forwardRightPower);
        backLeft.setPower(forwardLeftPower);
        frontRight.setPower(forwardLeftPower);
        backRight.setPower(forwardRightPower);
    }
    public void driveForward(final double power) {
        strafe(power, power);
    }
    public void strafeRight(final double power) { strafe(-power, power); }

    public int encoderUntilHit(double maxPower, double frontRightInches, double frontLeftInches, double backLeftInches, double backRightInches, TouchSensor touchSensor){
        double newFRTarget;
        double newFLTarget;
        double newBLTarget;
        double newBRTarget;

        if (opModeIsActive()){
            //calculate and set target positions
            frontLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            frontRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            backLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            backRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

            newFRTarget = frontRight.getCurrentPosition()     +  (frontRightInches * COUNTS_PER_INCH);
            newFLTarget = frontLeft.getCurrentPosition()     +  (frontLeftInches * COUNTS_PER_INCH);
            newBLTarget = backLeft.getCurrentPosition()     +  (backLeftInches * COUNTS_PER_INCH);
            newBRTarget = backRight.getCurrentPosition()     + (backRightInches * COUNTS_PER_INCH);

            backRight.setTargetPosition((int)(newBRTarget));
            frontRight.setTargetPosition((int)(newFRTarget));
            frontLeft.setTargetPosition((int)(newFLTarget));
            backLeft.setTargetPosition((int)(newBLTarget));

            // Run to position
            frontRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            frontLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            backRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            backLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);

            // Set powers. For now I'm setting to maxPower, so be careful.
            // In the future I'd like to add some acceleration control through powers, which
            // should help with encoder accuracy. Stay tuned.
            runtime.reset();
            frontRight.setPower(maxPower);
            frontLeft.setPower(maxPower);
            backRight.setPower(maxPower);
            backLeft.setPower(maxPower);

            while (opModeIsActive() &&
                    (frontRight.isBusy() && frontLeft.isBusy() && backRight.isBusy() && backLeft.isBusy() )) {
                if (touchSensor.isPressed()){
                    frontRight.setPower(0);
                    frontLeft.setPower(0);
                    backRight.setPower(0);
                    backLeft.setPower(0);

                    frontLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
                    frontRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
                    backLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
                    backRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
                    return (int) (newBRTarget - frontLeft.getCurrentPosition());

                }
            }
            // Set Zero Power
            frontRight.setPower(0);
            frontLeft.setPower(0);
            backRight.setPower(0);
            backLeft.setPower(0);

            // Go back to Run_Using_Encoder
            frontLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            frontRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            backLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            backRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        }
        return 0;

    }
    // Pinchas should make an encoder drive
    public void encoderDrive(double maxPower, double frontRightInches, double frontLeftInches, double backLeftInches, double backRightInches){
        // stop and reset the encoders? Maybe not. Might want to get position and add from there
        double newFRTarget;
        double newFLTarget;
        double newBLTarget;
        double newBRTarget;

        if (opModeIsActive()){
            //calculate and set target positions

            newFRTarget = frontRight.getCurrentPosition()     +  (frontRightInches * COUNTS_PER_INCH);
            newFLTarget = frontLeft.getCurrentPosition()     +  (frontLeftInches * COUNTS_PER_INCH);
            newBLTarget = backLeft.getCurrentPosition()     +  (backLeftInches * COUNTS_PER_INCH);
            newBRTarget = backRight.getCurrentPosition()     + (backRightInches * COUNTS_PER_INCH);

            backRight.setTargetPosition((int)(newBRTarget));
            frontRight.setTargetPosition((int)(newFRTarget));
            frontLeft.setTargetPosition((int)(newFLTarget));
            backLeft.setTargetPosition((int)(newBLTarget));

            // Run to position
            frontRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            frontLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            backRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            backLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);

            // Set powers. For now I'm setting to maxPower, so be careful.
            // In the future I'd like to add some acceleration control through powers, which
            // should help with encoder accuracy. Stay tuned.
            runtime.reset();
            frontRight.setPower(maxPower);
            frontLeft.setPower(maxPower);
            backRight.setPower(maxPower);
            backLeft.setPower(maxPower);

            //

            while (opModeIsActive() &&
                    (frontRight.isBusy() && frontLeft.isBusy() && backRight.isBusy() && backLeft.isBusy() )) {
                idle();
            }
            // Set Zero Power
            frontRight.setPower(0);
            frontLeft.setPower(0);
            backRight.setPower(0);
            backLeft.setPower(0);

            // Go back to Run_Using_Encoder
            frontLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            frontRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            backLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            backRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);


        }


    }

    public void encoderToSpecificPos(DcMotor motor, int pos , double power){
        motor.setTargetPosition(pos);
        motor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        motor.setPower(power);

        while (motor.isBusy() && opModeIsActive()){
            idle();
        }
        motor.setPower(0);
        motor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }
    public void raiseClawPos(int pos, double power){
//        encoderToSpecificPos(clawPulley, pos, power);
        clawPulley.setTargetPosition(pos);
        clawPulley.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        clawPulley.setPower(power);
    }

    //Start the process of turning the servo to grab cargo until the touch sensor is pressed
    public void startGrabbing(double power) {
        if (wheelTouchSensor.isPressed()) {
            stopGrabbing();
        } else {
            tryingToGrab = true;
            grabberPower = power;
            grabber.setPower(grabberPower);
        }
    }

    //Check if the touch sensor is pressed, and stop if so
    public void updateGrabbing() {
        if (tryingToGrab && wheelTouchSensor.isPressed()) {
            stopGrabbing();
        }
    }

    //Stop the process of turning the servo to grab cargo
    public void stopGrabbing() {
        tryingToGrab = false;
        grabberPower = 0;
        grabber.setPower(0);
    }

    // Last thing is an empty runOpMode because it's a linearopmode
    @Override
    public void runOpMode() throws InterruptedException {

    }
}
