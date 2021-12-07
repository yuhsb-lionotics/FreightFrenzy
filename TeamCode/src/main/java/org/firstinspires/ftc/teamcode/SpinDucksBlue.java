package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

@Autonomous(name="Spin Ducks Blue")
public class SpinDucksBlue extends Hardware {
    @Override
    public void runOpMode() throws InterruptedException {
        hardwareSetup();
        waitForStart();
        //back up slightly
        encoderDrive(0.3,-1,-1,-1,-1);
        carousel.setPower(0.7);
        sleep(3000);
        carousel.setPower(0);
        //encoderDrive(0.5,130,130,130,130);

    }
}
