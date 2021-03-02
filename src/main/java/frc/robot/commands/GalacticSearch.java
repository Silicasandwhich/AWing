package frc.robot.commands;

import java.io.IOError;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Set;

import org.opencv.core.KeyPoint;
import org.opencv.core.Mat;
import org.opencv.core.Rect;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Filesystem;
import edu.wpi.first.wpilibj.controller.PIDController;
import edu.wpi.first.wpilibj.controller.RamseteController;
import edu.wpi.first.wpilibj.controller.SimpleMotorFeedforward;
import edu.wpi.first.wpilibj.geometry.Pose2d;
import edu.wpi.first.wpilibj.trajectory.Trajectory;
import edu.wpi.first.wpilibj2.command.CommandBase;
import edu.wpi.first.wpilibj2.command.ParallelRaceGroup;
import edu.wpi.first.wpilibj2.command.RamseteCommand;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import frc.robot.subsystems.Drive;
import frc.robot.subsystems.Intake;
import frc.robot.subsystems.camera.Camera;
import frc.robot.Constants.*;

public class GalacticSearch extends ParallelRaceGroup {
    
    public GalacticSearch(Drive drive, IntakeCommand intake, Camera camera) {
        
        Mat image = new Mat();
        if(camera.getFrame(image) == 0) {
            System.out.println("Failed to grab frame.");
            return;
        }

        String selection = selectPathFromImage(image);
        Trajectory trajectory = getTrajectory(selection);
        
        drive.resetOdometry(new Pose2d());

        RamseteCommand follower = new RamseteCommand(
            trajectory,
            drive::getPose,
            new RamseteController(AutoConstants.kRamseteB, AutoConstants.kRamseteZeta),
            new SimpleMotorFeedforward(DriveConstants.kSC, DriveConstants.kVC, DriveConstants.kAC),
            drive.getKinematics(),
            drive::getRates,
            new PIDController(DriveConstants.kPL, 0, 0),
            new PIDController(DriveConstants.kPR, 0, 0),
            drive::setRawVoltage,
            drive);
        
        addCommands(
            follower,
            intake
            );
    }

    private Trajectory getTrajectory(String selection) {
        Trajectory trajectory = new Trajectory();
        try {
            Path trajectoryPath = Filesystem.getDeployDirectory().toPath().resolve("paths/"+selection);
        } catch (IOException ex) {
            DriverStation.reportError("Unable to open trajectory: "+selection, ex.getStackTrace());
        } 
        return trajectory;
    }

    public String selectPathFromImage(Mat image) {
        Rect[] classLemons = {}; //TODO Process images with Grip/Classifier
        KeyPoint[] gripLemons = {};

        String selection = "";

        if(classLemons.length > 0) {

        } else if {gripLemons.length > 0} {

        }

        return "";
    }

}
