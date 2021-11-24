package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.Range;

@TeleOp(name="Test Claw")
public class TestClaw extends Hardware{
    @Override
    public void runOpMode() throws InterruptedException {
        hardwareSetup();

        waitForStart();

        claw.setPosition(0);
        double position = 0;
        while (opModeIsActive()) {
            if (gamepad1.dpad_up) {
                position += .01;
            }
            if (gamepad1.dpad_down) {
                position -= .01;
            }
            position = Range.clip(position, 0,1);
            claw.setPosition(position);

            telemetry.addData("fingers' position", position);
            telemetry.update();

            sleep(10);
        }
    }
}
