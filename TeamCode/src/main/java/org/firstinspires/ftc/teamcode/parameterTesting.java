package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import java.util.ArrayList;
import java.util.List;

@TeleOp
@Disabled
public class parameterTesting extends LinearOpMode {
    public ParkingPosition parkingPosition = ParkingPosition.STORAGE_UNIT;
    public int delay = 0;

    @Override
    public void runOpMode(){
        selectParameters();
        waitForStart();
        telemetry.addData("Final Delay",delay);
        telemetry.addData("Final Parking Position", parkingPosition);
        telemetry.update();
        sleep(5000);

    }
    public void selectParameters(){
        boolean delaySelected = false;
        boolean parkingSelected = false;
        Button dpadUp = new Button(false);
        Button dpadDown = new Button(false);
        Button a = new Button(false);

        // Delay
        // Parking Position
        // Start location ?
        while(!delaySelected && !isStopRequested()){
            dpadUp.update(gamepad1.dpad_up);
            dpadDown.update(gamepad1.dpad_down);
            a.update(gamepad1.a);

            telemetry.update();
            if(dpadUp.isNewlyReleased()){
                delay++;
            } else if(dpadDown.isNewlyReleased()){
                delay--;
            } else if (a.isNewlyPressed()){
                delaySelected = true;

            }
            idle();

            telemetry.addData("Currently Selecting", "Delay");
            telemetry.addData("DelaySelected",delaySelected);
            telemetry.addData("Press Dpad up to raise value","Press Dpad down to lower value");
            telemetry.addData("Press A to select","!");
            telemetry.addData("Current selection", delay);
            telemetry.update();
        }
        List<ParkingPosition> positions = new ArrayList<>();
        positions.add(ParkingPosition.STORAGE_UNIT);
        positions.add(ParkingPosition.WAREHOUSE_JUST_INSIDE);
        positions.add(ParkingPosition.WAREHOUSE_TOWARDS_SHARED_HUB);
        positions.add(ParkingPosition.WAREHOUSE_AGAINST_BACK_WALL);
        int num = 0;

        while(!parkingSelected && !isStopRequested()){
            dpadUp.update(gamepad1.dpad_up);
            dpadDown.update(gamepad1.dpad_down);
            a.update(gamepad1.a);

            parkingPosition = positions.get(num);
            if(dpadUp.isNewlyPressed()){
                if(num < positions.size() - 1){
                    num++;
                } else {
                    num = 0;
                }
            } else if (dpadDown.isNewlyPressed()){
                if(num >= 1){
                    num--;
                } else {
                    num = positions.size() -1 ;
                }
            } else if(a.isNewlyPressed()){
                parkingSelected = true;
            }



            telemetry.addData("Selected Delay",delay);
            telemetry.addData("Currently Selecting", "Parking Position");
            telemetry.addData("Press Dpad up to raise value","Press Dpad down to lower value");
            telemetry.addData("Press A to make final","!");
            telemetry.addData("Current selection", parkingPosition);
            telemetry.update();

            idle();
        }
        telemetry.addData("Selected Delay",delay);
        telemetry.addData("Selected Parking Position", parkingPosition);
        telemetry.update();

    }
}
