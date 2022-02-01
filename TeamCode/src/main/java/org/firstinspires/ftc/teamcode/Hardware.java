package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.TouchSensor;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;

import java.util.ArrayList;

public class Hardware extends LinearOpMode {

    // Good Luck!
    //You should put constants here
    protected DcMotor frontLeft, frontRight, backLeft, backRight, clawPulley, carousel ;
    protected CRServo grabber;
    protected TouchSensor wheelTouchSensor;
    protected Servo lift;
    public boolean tryingToGrab = false;
    private double grabberPower = 0;
    PIDController pidRotate;
    private BNO055IMU imu;
    double globalAngle, rotation;
    //encoder positions for clawPulley
    //3500 for top, 1700 for middle, 7700 for low, 0 for ground
    public static final int  LOW_POSITION =  770;
    public static final int  MIDDLE_POSITION =  1680;
    public static final int  HIGH_POSITION =  3470;
    public static final int  SHARED_HUB_POSITION = 770; //@TODO: check this value

    static final double     COUNTS_PER_MOTOR_REV    = 1120 ;    // CHECK THIS
    static final double     DRIVE_GEAR_REDUCTION    = 1.0 ;     // This is < 1.0 if geared UP
    static final double     WHEEL_DIAMETER_INCHES   = 4.0 ;     // For figuring circumference. Not sure what it is
    static final double     COUNTS_PER_INCH         = (COUNTS_PER_MOTOR_REV * DRIVE_GEAR_REDUCTION) /
            (WHEEL_DIAMETER_INCHES * Math.PI);

    public ElapsedTime runtime = new ElapsedTime();
    Orientation lastAngles = new Orientation();

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
        lift = hardwareMap.servo.get("lift");


        frontLeft.setDirection(DcMotor.Direction.REVERSE);
        backLeft.setDirection(DcMotor.Direction.REVERSE);
        frontRight.setDirection(DcMotor.Direction.FORWARD);
        backRight.setDirection(DcMotor.Direction.FORWARD);
        grabber.setDirection(DcMotor.Direction.REVERSE);
        carousel.setDirection(DcMotor.Direction.FORWARD);


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

        telemetry.addData("Status:", "Hardware Setup Complete");
        telemetry.update();
    }

    public void imuSetup() {
        // Retrieve and initialize the IMU. The IMU is expected to be attached to an I2C port
        // on a Core Device Interface Module, configured to be a sensor of type "IMU",
        // and named "imu".
        // This is built in to Control Hubs and Expansion Hubs, and should be in the config by default
        telemetry.addData("Status","Setting Up IMU");
        telemetry.update();
        BNO055IMU.Parameters parameters = new BNO055IMU.Parameters();
        parameters.mode = BNO055IMU.SensorMode.IMU;
        parameters.angleUnit = BNO055IMU.AngleUnit.DEGREES;
        parameters.accelUnit = BNO055IMU.AccelUnit.METERS_PERSEC_PERSEC;
        parameters.loggingEnabled = false;
        imu = hardwareMap.get(BNO055IMU.class,"imu");
        imu.initialize(parameters);


        // TODO: PID Tuning! Everybody's favorite!
        // Create a pid controller
        pidRotate = new PIDController(.005, 0, 0);

        // make sure the imu gyro is calibrated before continuing.
        telemetry.addData("Status","Calibrating Gyro");
        telemetry.update();
        while (!isStopRequested() && !imu.isGyroCalibrated()) {
            sleep(50);
            idle();
        }

        telemetry.addData("Status:", "IMU Setup Complete");
        telemetry.update();
    }

    public Orientation getIMUOrientation() {
        return this.imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES);
    }

    //TODO: test to see if this works
    public void rotateToPos(double degrees, double power) {
        pidRotate.reset();
        pidRotate.setSetpoint(degrees);
        pidRotate.setInputRange(0, 360);
        pidRotate.setOutputRange(0, power);
        pidRotate.setTolerance(0.5);
        pidRotate.setContinuous();
        pidRotate.enable();
        pidRotate.performPID();
        while(opModeIsActive() && !pidRotate.onTarget()) {
            power = pidRotate.performPID(getIMUOrientation().firstAngle);
        }
    }

    public void rotate(int degrees, double power, boolean reset) {
        // restart imu angle tracking.
        if(reset) { resetAngle(); }

        // if degrees > 359 we cap at 359 with same sign as original degrees.
        if (Math.abs(degrees) > 359) degrees = (int) Math.copySign(359, degrees);

        // start pid controller. PID controller will monitor the turn angle with respect to the
        // target angle and reduce power as we approach the target angle. This is to prevent the
        // robots momentum from overshooting the turn after we turn off the power. The PID controller
        // reports onTarget() = true when the difference between turn angle and target angle is within
        // 1% of target (tolerance) which is about 1 degree. This helps prevent overshoot. Overshoot is
        // dependant on the motor and gearing configuration, starting power, weight of the robot and the
        // on target tolerance. If the controller overshoots, it will reverse the sign of the output
        // turning the robot back toward the setpoint value.
        /* frontLeft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        frontRight.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        backLeft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        backRight.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER); */
        pidRotate.reset();
        pidRotate.setSetpoint(degrees);
        pidRotate.setInputRange(0, 360);
        pidRotate.setOutputRange(0, power);
        pidRotate.setTolerance(0.5);
        pidRotate.enable();

        // getAngle() returns + when rotating counter clockwise (left) and - when rotating
        // clockwise (right).

        // rotate until turn is completed.

        if (degrees < 0) {
            // On right turn we have to get off zero first.
            while (opModeIsActive() && getAngle() == 0) {

                setDrivingPowers(power, -power, power, -power);
                sleep(100);
            }

            do {
                power = pidRotate.performPID(getAngle()); // power will be - on right turn.
                setDrivingPowers(-power, power, -power, power);
            } while (opModeIsActive() && !pidRotate.onTarget());
        } else    // left turn.
            do {
                power = pidRotate.performPID(getAngle()); // power will be + on left turn.

                setDrivingPowers(-power, power, -power, power);
            } while (opModeIsActive() && !pidRotate.onTarget());

        // turn the motors off.
        setDrivingPowers(0, 0, 0, 0);

        rotation = getAngle();

        // reset angle tracking on new heading.
        if(reset) {
            // wait for rotation to stop.
            sleep(500);
            resetAngle();
        }
        /*
        frontLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        frontRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        backLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        backRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER); */
    }


    private void resetAngle()
    {
        lastAngles = getIMUOrientation();

        globalAngle = 0;
    }

    /**
     * Get current cumulative angle rotation from last reset.
     * @return Angle in degrees. + = left, - = right from zero point.
     */
    public double getAngle()
    {
        // This is assuming that we want the Z axis for the angle. If that's not true then change this.
        // We have to process the angle because the imu works in euler angles so the Z axis is
        // returned as 0 to +180 or 0 to -180 rolling back to -179 or +179 when rotation passes
        // 180 degrees. We detect this transition and track the total cumulative angle of rotation.

        Orientation angles = getIMUOrientation();

        double deltaAngle = angles.firstAngle - lastAngles.firstAngle;

        if (deltaAngle < -180)
            deltaAngle += 360;
        else if (deltaAngle > 180)
            deltaAngle -= 360;

        globalAngle += deltaAngle;

        lastAngles = angles;

        return globalAngle;
    }

    public void setDrivingPowers(double frontLeftP, double frontRightP, double backLeftP, double backRightP){
        frontLeft.setPower(frontLeftP);
        frontRight.setPower(frontRightP);
        backLeft.setPower(backLeftP);
        backRight.setPower(backRightP);
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

            while (opModeIsActive() &&
                    (frontRight.isBusy() || frontLeft.isBusy() || backRight.isBusy() || backLeft.isBusy() )) {
                idle();
                updateGrabbing();
                updateRaise();
                if(!frontRight.isBusy()){
                    frontRight.setPower(0);
                    frontRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
                }
                if(!frontLeft.isBusy()){
                    frontLeft.setPower(0);
                    frontLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
                }
                if(!backRight.isBusy()){
                    backRight.setPower(0);
                    backRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

                }
                if(!backLeft.isBusy()){
                    backLeft.setPower(0);
                    backLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

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
    }

    public void encoderDriveAnd(double maxPower, double frontRightInches, double frontLeftInches, double backLeftInches, double backRightInches){
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

            while (opModeIsActive() &&
                    (frontRight.isBusy() && frontLeft.isBusy() && backRight.isBusy() && backLeft.isBusy() )) {
                idle();
                updateGrabbing();
                updateRaise();
//                if(!frontRight.isBusy()){
//                    frontLeft.setPower(0);
//                }
//                if(!frontLeft.isBusy()){
//                    frontLeft.setPower(0);
//                }
//                if(!backRight.isBusy()){
//                    backRight.setPower(0);
//                }
//                if(!backLeft.isBusy()){
//                    backLeft.setPower(0);
//                }

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
    public void pidEncoderDrive(double power, double frontRightInches, double frontLeftInches, double backLeftInches, double backRightInches){

        if (opModeIsActive()){
            // RULE: ArrayLists will be of the following format:
            // index 0: frontRight
            // index 1: frontLeft
            // index 2: backRight when call set input do startpos - currentpos
            // index 3: backLeft
            ArrayList<DcMotor> motors = new ArrayList<DcMotor>();
            ArrayList<Double> inches = new ArrayList<Double>();
            ArrayList<PIDController> pids = new ArrayList<PIDController>();
            ArrayList<Double> startPosition = new ArrayList<Double>();
            ArrayList<Double> newTargets = new ArrayList<Double>();

            motors.add(frontRight);
            motors.add(frontLeft);
            motors.add(backRight);
            motors.add(backLeft);

            inches.add(frontRightInches);
            inches.add(frontLeftInches);
            inches.add(backRightInches);
            inches.add(backLeftInches);

            // adds start position for all 4 motors (fr, fl, br, bl)
            for(int i = 0; i < 4; i++){startPosition.add((double) motors.get(i).getCurrentPosition());}

            // adds pid for all 4 motors (fr, fl, br, bl)
            for(int i = 0; i < 4; i++){pids.add(new PIDController(5,0,0));} //(5,0,0) is not a set-in-stone thing

            // sets newTarget for all 4 motors (fr, fl, br, bl)
            for(int i = 0; i < 4; i++){newTargets.add(inches.get(i) * COUNTS_PER_INCH);}

            // does pid stuff for all 4 motors (fr, fl, br, bl)
            for(int i = 0; i < 4; i++){
                pids.get(i).reset();
                pids.get(i).setSetpoint(newTargets.get(i));
                pids.get(i).setInputRange(0, newTargets.get(i) * 1.5);
                pids.get(i).setOutputRange(0, 1);
                pids.get(i).setTolerance(1.00);
                pids.get(i).enable();
            }

            // Run to position
            // does all 4 motors (fr, fl, br, bl)
            for(int i = 0; i < 4; i++){motors.get(i).setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);}

            // Set powers. For now I'm setting to maxPower, so be careful.
            // In the future I'd like to add some acceleration control through powers, which
            // should help with encoder accuracy. Stay tuned.
            runtime.reset();

            //requires documentation
            do{
                for(int i = 0; i < 4; i++){
                    motors.get(i).setPower(pids.get(i).performPID((motors.get(i).getCurrentPosition() - startPosition.get(i))));
                    updateGrabbing();
                }
            } while(opModeIsActive() && (!pids.get(0).onTarget() && !pids.get(1).onTarget() && !pids.get(2).onTarget() && !pids.get(3).onTarget()));

            // Set Zero Power
            // does for all 4 motors (fr, fl, br, bl)
            for(int i = 0; i < 4; i++){motors.get(i).setPower(0);}

            // Go back to Run_Using_Encoder
            // does for all 4 motors (fr, fl, br, bl)
            for(int i = 0; i < 4; i++){motors.get(i).setMode(DcMotor.RunMode.RUN_USING_ENCODER);}
        }


    }

    public void encoderStrafeAndRotate (double maxPower, double forwardRightInches, double forwardLeftInches, double ccRotation) {
        encoderDrive(maxPower, forwardLeftInches + ccRotation, forwardRightInches - ccRotation, forwardLeftInches - ccRotation, forwardRightInches + ccRotation);
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

    //send the pulley to a specific height
    public void raiseClawPos(int pos, double power){
//        encoderToSpecificPos(clawPulley, pos, power);
        clawPulley.setTargetPosition(pos);
        clawPulley.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        clawPulley.setPower(power);
    }

    //send the pulley to a specific height, and wait for it to reach that height
    public void raiseClawPosAndStop(int pos, double power){
//        encoderToSpecificPos(clawPulley, pos, power);
        clawPulley.setTargetPosition(pos);
        clawPulley.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        clawPulley.setPower(power);
        while(clawPulley.isBusy()){
            idle();
        }
        clawPulley.setPower(0);
        clawPulley.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
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
    public void updateRaise(){
        if(clawPulley.getMode() == DcMotor.RunMode.RUN_TO_POSITION && !clawPulley.isBusy()){
            clawPulley.setPower(0);
            clawPulley.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
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
