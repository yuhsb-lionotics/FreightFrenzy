package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.robotcore.external.matrices.OpenGLMatrix;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;
import org.firstinspires.ftc.robotcore.internal.system.Assert;

@Autonomous(name="Red Carousel")
public class AutoCarouselRed extends AutoOpenCv {
    //switch left/right wheels, reverse direction of carousel,
    @Override
    public void hardwareSetup() {
        //initialize hardware devices
        frontRight = hardwareMap.dcMotor.get("frontLeft");
        frontLeft = hardwareMap.dcMotor.get("frontRight");
        backLeft = hardwareMap.dcMotor.get("backRight");
        backRight = hardwareMap.dcMotor.get("backLeft");
        clawPulley = hardwareMap.dcMotor.get("clawPulley");
        grabber = hardwareMap.crservo.get("grabber");
        carousel = hardwareMap.dcMotor.get("carousel");
        wheelTouchSensor = hardwareMap.touchSensor.get("wheel");


        frontRight.setDirection(DcMotor.Direction.REVERSE);
        backRight.setDirection(DcMotor.Direction.REVERSE);
        frontLeft.setDirection(DcMotor.Direction.FORWARD);
        backLeft.setDirection(DcMotor.Direction.FORWARD);
        grabber.setDirection(DcMotor.Direction.REVERSE);
        carousel.setDirection(DcMotor.Direction.REVERSE);


        frontRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        frontLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        carousel.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        clawPulley.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);


        frontLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        frontRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        backLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        backRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        clawPulley.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        carousel.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        //Use encoders
        frontLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        frontRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        backLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        backRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        clawPulley.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        carousel.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        telemetry.addData("Status:", "Hardware Setup Complete");
        telemetry.update();
    }

    //instead of giving the actual orientation as measured by the IMU, reverse the Z value
    @Override
    public Orientation getIMUOrientation() {
        //measure the orientation using the gyroscope
        Orientation actualOrientation = super.getIMUOrientation();
        //check the axes order
        Assert.assertTrue(actualOrientation.axesOrder == AxesOrder.ZYX);
        /*construct an orientation with the first angle reversed
        @TODO: mathematically, one of the other angles should be reversed too.
        We never use them anyway though so it doesn't really matter. If so, we should replace
        getIMUOrientation with getIMUFirstAngle() and the overriding method will be much simpler. */
        Orientation reversedOrientation = new Orientation(actualOrientation.axesReference,
                actualOrientation.axesOrder, actualOrientation.angleUnit,
                -actualOrientation.firstAngle, actualOrientation.secondAngle, actualOrientation.thirdAngle, System.nanoTime());
        return reversedOrientation;
    }
}
