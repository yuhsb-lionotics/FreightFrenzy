package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

@TeleOp
public class RotationTesting extends Hardware{
    @Override
    public void runOpMode() throws InterruptedException {
        hardwareSetup();
        imuSetup();
        while(!isStopRequested() && !isStarted()) {
            telemetry.addData("orientation",getIMUOrientation());
            telemetry.update();
        }
        telemetry.addData("status","rotating");
        telemetry.update();
        rotate(-5,0.6,true);
        telemetry.addData("orientation",getIMUOrientation());
        telemetry.addData("pid setpoint", pidRotate.getSetpoint());
        telemetry.addData("current angle", getAngle());
        telemetry.addData("pid error (setpoint minus current angle)", pidRotate.getError());
        telemetry.update();
        sleep(3000);
    }
}
