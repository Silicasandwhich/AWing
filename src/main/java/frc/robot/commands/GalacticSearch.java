package frc.robot.commands;

import java.io.IOException;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.util.ArrayList;

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
import frc.robot.Constants.AutoConstants;
import frc.robot.Constants.DriveConstants;
import frc.robot.RobotContainer;
import frc.robot.subsystems.Drive;
import frc.robot.subsystems.camera.Camera;

public class GalacticSearch extends ParallelRaceGroup {

    public GalacticSearch(Drive drive, IntakeCommand intake, Camera camera) {
        setAutoStatus(0);
        Rect[] lemons = camera.processFrame();
        if (lemons.length == 0) {
            System.out.println("Failed to get lemons.");
            setAutoStatus(900);
            return;
        }
        
        setAutoStatus(1);
        String selection = selectPathFromRects(lemons);
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

    public static String selectPathFromRects(Rect[] lemons) {
        ArrayList<Rect[]> tris  = new ArrayList<Rect[]>();
        for(int r = 0; r < lemons.length-2; r++) {
            tris.add(new Rect[] {lemons[r], lemons[r+1], lemons[r+2]});
        }

        String selection = "";

        return selection;
    }

    private void setAutoStatus(int status) {
        RobotContainer.getInstance().setAutoStatus(1000+status);
    }
}
