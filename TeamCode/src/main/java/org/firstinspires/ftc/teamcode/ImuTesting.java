package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

@TeleOp
public class ImuTesting extends Hardware{
    @Override
    public void runOpMode() throws InterruptedException {
        hardwareSetup();
        imuSetup();
        waitForStart();
        rotate(90,0.8,true);
    }
}