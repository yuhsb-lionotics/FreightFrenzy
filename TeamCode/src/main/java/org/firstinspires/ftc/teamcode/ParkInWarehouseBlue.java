package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

@Autonomous(name="Park In Warehouse Blue")
public class ParkInWarehouseBlue extends Hardware{
    public void runOpMode() {
        hardwareSetup();
        waitForStart();
        //close claw
        //a little to the left
        encoderDrive(0.3,1,-1,1,-1);
        //forward
        encoderDrive(0.7,17,17,17,17);
        //left
        encoderDrive(0.7,-10,10,-10,10);
    }
}
