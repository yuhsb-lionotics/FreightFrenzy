package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

@TeleOp
public class RotationTesting extends Hardware{
    @Override
    public void runOpMode() throws InterruptedException {
        hardwareSetup();
        imuSetup();
        while(!isStopRequested() && !isStarted()) {
            telemetry.addData("heading",getHeading());
            telemetry.update();
        }
        telemetry.addData("status","rotating");
        telemetry.update();
        rotateToPos(getHeading() - 5, 1);
//        telemetry.addData("pid setpoint", pidRotate.getSetpoint());
        telemetry.addData("current angle", getHeading());
//        telemetry.addData("pid error (setpoint minus current angle)", pidRotate.getError());
        telemetry.update();
        sleep(3000);
    }
}
