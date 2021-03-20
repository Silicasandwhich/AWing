package frc.robot.subsystems.camera;

import java.nio.file.InvalidPathException;

import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.objdetect.CascadeClassifier;

import edu.wpi.cscore.UsbCamera;
import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Filesystem;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class Camera extends SubsystemBase {

    private Thread visionThread;
    private ExMachina cascade;

    public Camera() {
        CameraServer.getInstance().startAutomaticCapture();
        cascade = new ExMachina();
        /**
        visionThread = new Thread(() -> {
            camera.setResolution(640,480);

            CvSink cvDebug = CameraServer.getInstance().getVideo();

            Mat source = new Mat();

            LemonDetector pipeline = new LemonDetector();

            while(!Thread.interrupted()) {
                if(cvDebug.grabFrame(source) == 0){
                    System.out.println(cvDebug.getError());
                    continue;
                }
                
                
            }
        });
        **/
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

    public long getFrame(Mat source) {
        return CameraServer.getInstance().getVideo().grabFrame(source);
    }

    public Rect[] processFrame() {
        Mat source = new Mat();
        getFrame(source);
        try {
            cascade.process(source, new CascadeClassifier(Filesystem.getDeployDirectory().toPath().resolve("ExMachina.xml").toString()));
            return cascade.cascadeClassifierOutput().toArray();
            
        } catch (InvalidPathException e) {
            DriverStation.reportError("Could not open path", e.getStackTrace());
        }
        return new Rect[]{};
    }
    
}
