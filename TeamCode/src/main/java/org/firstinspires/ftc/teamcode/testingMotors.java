package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.TouchSensor;

@TeleOp(name = "Testing")
public class testingMotors extends LinearOpMode {

//    DcMotor motorToTest;
    TouchSensor wheel;

    @Override
    public void runOpMode() throws InterruptedException {
        wheel = hardwareMap.touchSensor.get("wheel");
        waitForStart();
//        motorToTest =  hardwareMap.dcMotor.get("frontLeft");
//        motorToTest.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
//        sleep(1000);
//        motorToTest.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
//        motorToTest.setPower(0.5);

        while(opModeIsActive()) {
//            telemetry.addData("Position", motorToTest.getCurrentPosition());
//            telemetry.update();
            telemetry.addData("Pushed:", wheel.isPressed());
            telemetry.update();
            idle();
        }

    }


}