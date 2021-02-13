// RobotBuilder Version: 3.1
//
// This file was generated by RobotBuilder. It contains sections of
// code that are automatically generated and assigned by robotbuilder.
// These sections will be updated in the future when you export to
// Java from RobotBuilder. Do not put any code or make any change in
// the blocks indicating autogenerated code or it will be lost on an
// update. Deleting the comments indicating the section will prevent
// it from being updated in the future.

package frc.robot.subsystems;

// BEGIN AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=IMPORTS
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

import org.opencv.core.*;

import edu.wpi.cscore.CvSink;
import edu.wpi.cscore.CvSource;
import edu.wpi.cscore.UsbCamera;
import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.vision.VisionRunner;
import edu.wpi.first.vision.VisionThread;
import edu.wpi.first.vision.VisionPipeline;

import frc.robot.GripPipeline;

    // END AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=IMPORTS


/**
 *
 */
public class Camera extends SubsystemBase {
    private VisionThread m_visionThread;
    private UsbCamera camera;
    private double x = 1;
    private NetworkTableEntry lemons;
    public Camera() {
        // BEGIN AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=CONSTRUCTORS
        NetworkTableInstance ntinst = NetworkTableInstance.getDefault();
        NetworkTable vision = ntinst.getTable("vision");
        lemons = vision.getEntry("lemons");

        camera = CameraServer.getInstance().startAutomaticCapture();

        m_visionThread = new VisionThread(camera, new GripPipeline(), pipeline -> {
            List<String> lemonList = new ArrayList<String>();
            KeyPoint[] blobs = pipeline.findBlobsOutput().toArray();

            CvSink cvSink = CameraServer.getInstance().getVideo();
            CvSource outputStream = CameraServer.getInstance().putVideo("Blur", 640, 480);

            Mat source = new Mat();
            while(!Thread.interrupted()) {
                if (cvSink.grabFrame(source) == 0) {
                  continue;
                }
            for(int i = 0; i <blobs.length; i++) {
                String data = blobs[i].pt.x+ " "+ blobs[i].pt.y+" "+ blobs[i].size;
                System.out.println(data);
                lemonList.add(data);
                Imgproc.circle(source, blobs[0].pt, (int) blobs[0].size, new Scalar(255,255,255));
                Imgproc.putText(source, "X: "+ blobs[0].pt.x+"; Y:"+blobs[0].pt.y, new Point(blobs[0].pt.x, blobs[0].pt.y), Core.FONT_HERSHEY_PLAIN, 1, new Scalar(255,255,255));
                Imgproc.putText(source, "Size: "+ blobs[0].size, new Point(blobs[0].pt.x, blobs[0].pt.y), Core.FONT_HERSHEY_PLAIN, 1, new Scalar(255,255,255));
            }
            outputStream.putFrame(source);
            lemons.setStringArray(lemonList.toArray(new String[4]));
        }


    // END AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=CONSTRUCTORS
    });

    m_visionThread.start();
}


    @Override
    public void periodic() {
        // This method will be called once per scheduler run

    }

    @Override
    public void simulationPeriodic() {
        // This method will be called once per scheduler run when in simulation

    }

    // Put methods for controlling this subsystem
    // here. Call these from Commands.

}

