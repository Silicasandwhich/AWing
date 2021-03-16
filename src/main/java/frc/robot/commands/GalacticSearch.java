package frc.robot.commands;

import java.io.IOException;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

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
import frc.robot.Constants;
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
            Path trajectoryPath = Filesystem.getDeployDirectory().toPath().resolve("paths/stupid/output/" + selection);
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

        for(int i = 0; i < tris.size(); i++) {
            for(int r = 0; r < 3; r++) {
                //For each reference triangle, see if it is within tolerance
                boolean correctPath = triangleWithinTolerance(tris.get(i), Constants.Vision.realTris[r], 1.01);
                if(!correctPath) {
                    continue;
                }
                if(r == 0) {
                    return "blue_a.wpilib.json";
                } else if(r==1) {
                    return "blue_b.wpilib.json";
                } else if(r==2) {
                    return "red_a.wpilib.json";
                } else if(r==3) {
                    return "red_b.wpilib.json";
                }
            }
            
        }
        return "";
    }

    public static boolean triangleWithinTolerance(Rect[] tri, Rect[] reference, double scale) {

        //Sort triangles
        Arrays.sort(tri, Comparator.comparingDouble(Rect::area));
        Arrays.sort(reference, Comparator.comparingDouble(Rect::area));


        //Make sure scaling is correct, otherwise its a different path.
        for(int i = 0; i < 3; i++) {
            if(tri[i].area() > reference[i].area()*scale || tri[i].area() < (reference[i].area()*scale)-reference[i].area()) {
                return false;
            }

            if (i < 2) {
                //find the difference between the tri[0].x and tri[1].x and ref[0].x and ref[1].x
                int tri_dif = Math.abs(tri[i].x - tri[i+1].x);
                int ref_dif = Math.abs(reference[i].x - reference[i+1].x);

                if(tri_dif > ref_dif*scale || tri_dif < ref_dif-((ref_dif*scale)-ref_dif)) {
                    return false;
                }
            }
        }
        
        return true;
    }

    private void setAutoStatus(int status) {
        RobotContainer.getInstance().setAutoStatus(1000+status);
    }
}
