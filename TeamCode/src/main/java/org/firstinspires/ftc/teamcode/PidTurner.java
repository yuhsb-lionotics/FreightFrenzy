//package org.firstinspires.ftc.teamcode;
//
//import com.qualcomm.hardware.bosch.BNO055IMU;
//import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
//
//import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
//import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
//import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
//import org.firstinspires.ftc.robotcore.external.navigation.Orientation;
//@TeleOp(name = "Imu stuff")
//public class PidTurner extends Hardware {
//    PIDController pidRotate;
//    BNO055IMU imu;
//    double globalAngle, rotation;
//    Orientation lastAngles = new Orientation();
//
//    public PidTurner() {
//
//        BNO055IMU.Parameters parameters = new BNO055IMU.Parameters();
//        parameters.mode = BNO055IMU.SensorMode.IMU;
//        parameters.angleUnit = BNO055IMU.AngleUnit.DEGREES;
//        parameters.accelUnit = BNO055IMU.AccelUnit.METERS_PERSEC_PERSEC;
//        parameters.loggingEnabled = false;
//        // Retrieve and initialize the IMU. We expect the IMU to be attached to an I2C port
//        // on a Core Device Interface Module, configured to be a sensor of type "AdaFruit IMU",
//        // and named "imu".
////        imu = hardwareMap.get(BNO055IMU.class, "imu");
////        imu = hardwareMap.i2cDevice.get("imu");
//        imu = hardwareMap.get(BNO055IMU.class,"imu");
//        imu.initialize(parameters);
//
//        // Create a pid controller with some guess values
//        pidRotate = new PIDController(.003, .00003, 0);
//        // make sure the imu gyro is calibrated before continuing.
//        while (!isStopRequested() && !imu.isGyroCalibrated()) {
//            sleep(50);
//            idle();
//        }
//    }
//
//    private void rotate(int degrees, double power) {
//        // restart imu angle tracking.
//        resetAngle();
//
//        // if degrees > 359 we cap at 359 with same sign as original degrees.
//        if (Math.abs(degrees) > 359) degrees = (int) Math.copySign(359, degrees);
//
//        // start pid controller. PID controller will monitor the turn angle with respect to the
//        // target angle and reduce power as we approach the target angle. This is to prevent the
//        // robots momentum from overshooting the turn after we turn off the power. The PID controller
//        // reports onTarget() = true when the difference between turn angle and target angle is within
//        // 1% of target (tolerance) which is about 1 degree. This helps prevent overshoot. Overshoot is
//        // dependant on the motor and gearing configuration, starting power, weight of the robot and the
//        // on target tolerance. If the controller overshoots, it will reverse the sign of the output
//        // turning the robot back toward the setpoint value.
//
//        pidRotate.reset();
//        pidRotate.setSetpoint(degrees);
//        pidRotate.setInputRange(0, degrees);
//        pidRotate.setOutputRange(0, power);
//        pidRotate.setTolerance(1);
//        pidRotate.enable();
//
//        // getAngle() returns + when rotating counter clockwise (left) and - when rotating
//        // clockwise (right).
//
//        // rotate until turn is completed.
//
//        if (degrees < 0) {
//            // On right turn we have to get off zero first.
//            while (opModeIsActive() && getAngle() == 0) {
//
//                setPowers(power, -power, power, -power);
//                sleep(100);
//            }
//
//            do {
//                power = pidRotate.performPID(getAngle()); // power will be - on right turn.
//                setPowers(-power, power, -power, power);
//            } while (opModeIsActive() && !pidRotate.onTarget());
//        } else    // left turn.
//            do {
//                power = pidRotate.performPID(getAngle()); // power will be + on left turn.
//
//                setPowers(-power, power, -power, power);
//            } while (opModeIsActive() && !pidRotate.onTarget());
//
//        // turn the motors off.
//        setPowers(0, 0, 0, 0);
//
//        rotation = getAngle();
//
//        // wait for rotation to stop.
//        sleep(500);
//
//        // reset angle tracking on new heading.
//        resetAngle();
//    }
//
//
//    private void resetAngle()
//    {
//        lastAngles = imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES);
//
//        globalAngle = 0;
//    }
//
//    /**
//     * Get current cumulative angle rotation from last reset.
//     * @return Angle in degrees. + = left, - = right from zero point.
//     */
//    private double getAngle()
//    {
//        // This is assuming that we want the Z axis for the angle. If that's not true then change this.
//        // We have to process the angle because the imu works in euler angles so the Z axis is
//        // returned as 0 to +180 or 0 to -180 rolling back to -179 or +179 when rotation passes
//        // 180 degrees. We detect this transition and track the total cumulative angle of rotation.
//
//        Orientation angles = imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES);
//
//        double deltaAngle = angles.firstAngle - lastAngles.firstAngle;
//
//        if (deltaAngle < -180)
//            deltaAngle += 360;
//        else if (deltaAngle > 180)
//            deltaAngle -= 360;
//
//        globalAngle += deltaAngle;
//
//        lastAngles = angles;
//
//        return globalAngle;
//    }
//    public String getAllAngles(){
//        Orientation angles = this.imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES);
//        return "Z: " + angles.firstAngle + " Y: " + angles.secondAngle + " X: " + angles.thirdAngle;
//    }
//
//    @Override
//    public void runOpMode() throws InterruptedException {
//        PidTurner pid = new PidTurner();
//        while (opModeIsActive()){
//            telemetry.addData("angles", pid.getAllAngles());
//        }
//    }
//}
