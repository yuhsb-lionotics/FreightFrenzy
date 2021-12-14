package org.firstinspires.ftc.teamcode;

import android.util.Log;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.openftc.easyopencv.OpenCvCamera;
import org.openftc.easyopencv.OpenCvCameraFactory;
import org.openftc.easyopencv.OpenCvCameraRotation;
import org.openftc.easyopencv.OpenCvInternalCamera;
@Autonomous(name = "AutoCameraTesting")
public class AutoOpenCv extends Hardware {
    OpenCvInternalCamera phoneCam;
    OpenCvDetector pipeline = new OpenCvDetector();



    @Override
    public void runOpMode(){
        Log.w("AutoOpMode", "Running opmode! :-)");
        int cameraMonitorViewId = hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        phoneCam = OpenCvCameraFactory.getInstance().createInternalCamera(OpenCvInternalCamera.CameraDirection.BACK, cameraMonitorViewId);

        phoneCam.setPipeline(pipeline);
        phoneCam.setViewportRenderingPolicy(OpenCvCamera.ViewportRenderingPolicy.OPTIMIZE_VIEW);
        phoneCam.setViewportRenderer(OpenCvCamera.ViewportRenderer.GPU_ACCELERATED);
        phoneCam.openCameraDeviceAsync(new OpenCvCamera.AsyncCameraOpenListener()
        {
            @Override
            public void onOpened()
            {
                phoneCam.startStreaming(320,240, OpenCvCameraRotation.SIDEWAYS_LEFT);
            }

            @Override
            public void onError(int errorCode) {

            }
        });

        waitForStart();

        while (opModeIsActive())
        {
            // Don't burn CPU cycles busy-looping in this sample
            sleep(50);
            telemetry.addData("Location", pipeline.getLocation());
            telemetry.update();
            // Close camera and pipeline
//            phoneCam.closeCameraDeviceAsync(new OpenCvCamera.AsyncCameraCloseListener()
//            {
//                @Override
//                public void onClose()
//                {
//                    phoneCam.stopStreaming();
//                }
//
//            });
        }

    }


        // Then move to the place.

    }


