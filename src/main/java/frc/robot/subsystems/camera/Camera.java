package frc.robot.subsystems.camera;

import java.io.File;
import java.nio.file.InvalidPathException;

import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.objdetect.CascadeClassifier;

import edu.wpi.cscore.UsbCamera;
import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Filesystem;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class Camera extends SubsystemBase {

    private Thread visionThread;
    private ExMachina cascade;

    NetworkTableEntry writeImage;

    public Camera() {
        CameraServer.getInstance().startAutomaticCapture();
        cascade = new ExMachina();
        writeImage = Shuffleboard.getTab("Auto").add("Write Debug Image", true).getEntry();
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

        if(writeImage.getBoolean(true)) {
            File debugFile = new File(Filesystem.getDeployDirectory().getAbsolutePath()+"cameraDebug");
            debugFile.mkdir();
            String file = debugFile.getAbsolutePath() + System.currentTimeMillis() + ".jpg";
            boolean imageSuccess = Imgcodecs.imwrite(file, source);
            System.out.println(" . . Image Writing Status: "+imageSuccess+" . . ");
        }

        try {
            cascade.process(source, new CascadeClassifier(Filesystem.getDeployDirectory().toPath().resolve("ExMachina.xml").toString()));
            return cascade.cascadeClassifierOutput().toArray();
            
        } catch (InvalidPathException e) {
            DriverStation.reportError("Could not open path", e.getStackTrace());
        }
        return new Rect[]{};
    }
    
}
