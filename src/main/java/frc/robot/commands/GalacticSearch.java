package frc.robot.commands;

import java.io.IOException;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;

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
import edu.wpi.first.wpilibj.trajectory.TrajectoryUtil;
import edu.wpi.first.wpilibj2.command.ParallelRaceGroup;
import edu.wpi.first.wpilibj2.command.RamseteCommand;
import frc.robot.RobotContainer;
import frc.robot.Constants.AutoConstants;
import frc.robot.Constants.DriveConstants;
import frc.robot.subsystems.Drive;
import frc.robot.subsystems.camera.Camera;

public class GalacticSearch extends ParallelRaceGroup {

    public GalacticSearch(Drive drive, IntakeCommand intake, Camera camera) {
        setAutoStatus(0);
        Mat image = new Mat();
        if (camera.getFrame(image) == 0) {
            System.out.println("Failed to grab frame.");
            setAutoStatus(900);
            return;
        }

        String selection = selectPathFromImage(image);
        RobotContainer.getInstance().setPath(selection);
        Trajectory trajectory = getTrajectory(selection);

        drive.resetOdometry(new Pose2d());
        setAutoStatus(3);

        RamseteCommand follower = new RamseteCommand(trajectory, drive::getPose,
                new RamseteController(AutoConstants.kRamseteB, AutoConstants.kRamseteZeta),
                new SimpleMotorFeedforward(DriveConstants.kSC, DriveConstants.kVC, DriveConstants.kAC),
                drive.getKinematics(), drive::getRates, new PIDController(DriveConstants.kPL, 0, 0),
                new PIDController(DriveConstants.kPR, 0, 0), drive::setRawVoltage, drive);
        setAutoStatus(4);

        addCommands(follower, intake);
        setAutoStatus(5);
    }

    private Trajectory getTrajectory(String selection) {
        Trajectory trajectory = new Trajectory();
        try {
            Path trajectoryPath = Filesystem.getDeployDirectory().toPath().resolve("paths/" + selection);
            trajectory = TrajectoryUtil.fromPathweaverJson(trajectoryPath);
        } catch (InvalidPathException ex) {
            DriverStation.reportError("Unable to open trajectory: " + selection, ex.getStackTrace());
            setAutoStatus(902);
        } catch (IOException ex) {
            DriverStation.reportError("Unable to open trajectory: " + selection, ex.getStackTrace());
            setAutoStatus(902);
        }
        setAutoStatus(2);
        return trajectory;
    }

    private void setAutoStatus(int status) {
        RobotContainer.getInstance().setAutoStatus(1000+status);
    }

    public String selectPathFromImage(Mat image) {
        setAutoStatus(1);
        Rect[] classLemons = {}; //TODO Process images with Grip/Classifier
        KeyPoint[] gripLemons = {};

        String selection = "";

        if(classLemons.length > 0) {

        } else if (gripLemons.length > 0) {

        } else {
            setAutoStatus(901);
        }

        return selection;
    }

}
