package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

@Autonomous(name="Park In Warehouse Red")
public class ParkInWarehouse extends Hardware {
    @Override
    public void runOpMode() {
        hardwareSetup();
        waitForStart();
        //close claw
        //a little to the right
        encoderDrive(0.3,-1,1,-1,1);
        //forward
        encoderDrive(0.7,17,17,17,17);
        //left
        encoderDrive(0.7,10,-10,10,-10);
    }
}