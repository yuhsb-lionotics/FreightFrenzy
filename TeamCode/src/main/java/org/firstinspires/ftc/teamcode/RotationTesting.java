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
        }
        rotate(90,0.6,true);
    }
}
