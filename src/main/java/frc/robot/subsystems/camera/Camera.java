package frc.robot.subsystems.camera;

import org.opencv.core.Core;
import org.opencv.core.KeyPoint;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import edu.wpi.cscore.CvSink;
import edu.wpi.cscore.CvSource;
import edu.wpi.cscore.UsbCamera;
import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class Camera extends SubsystemBase {

    private Thread visionThread;
    UsbCamera camera;

    public Camera() {
        camera = CameraServer.getInstance().startAutomaticCapture();

        visionThread = new Thread(() -> {
            camera.setResolution(640,480);

            CvSink cvDebug = CameraServer.getInstance().getVideo();
            CvSource cvDebugOut = CameraServer.getInstance().putVideo("CV Debug", 640, 480);

            Mat source = new Mat();

            LemonDetector pipeline = new LemonDetector();

            while(!Thread.interrupted()) {
                if(cvDebug.grabFrame(source) == 0){
                    System.out.println(cvDebug.getError());
                    continue;
                }

                pipeline.process(source);
                KeyPoint[] blobs = pipeline.findBlobsOutput().toArray();
                
                for(int i = 0; (i < 4) && (i < blobs.length); i++) {
                    Imgproc.circle(source, blobs[0].pt, (int) blobs[0].size, new Scalar(255,255,255));
                    Imgproc.putText(source, "X: "+ blobs[0].pt.x+"; Y:"+blobs[0].pt.y, new Point(blobs[0].pt.x, blobs[0].pt.y), Core.FONT_HERSHEY_PLAIN, 1, new Scalar(255,255,255));
                    Imgproc.putText(source, "Size: "+ blobs[0].size, new Point(blobs[0].pt.x, blobs[0].pt.y-20), Core.FONT_HERSHEY_PLAIN, 1, new Scalar(255,255,255));
                }
                cvDebugOut.putFrame(source);
            }
        });
    }

    public void startCapture() {
        try {
            visionThread.start();
        } catch (IllegalThreadStateException e) {
            visionThread.interrupt();
            visionThread.start();
        }
    }

    public void stopCapture() {
        visionThread.interrupt();
    }
    
}
