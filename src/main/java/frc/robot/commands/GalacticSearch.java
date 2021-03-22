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
import frc.robot.Constants;
import frc.robot.Constants.AutoConstants;
import frc.robot.Constants.DriveConstants;
import frc.robot.subsystems.Drive;
import frc.robot.subsystems.camera.Camera;

public class GalacticSearch extends ParallelRaceGroup {

    public GalacticSearch(Drive drive, Camera camera) {
        
        System.out.println("");
        System.out.println(" ▄▄ •  ▄▄▄· ▄▄▌   ▄▄▄·  ▄▄· ▄▄▄▄▄▪   ▄▄·     .▄▄ · ▄▄▄ . ▄▄▄· ▄▄▄   ▄▄·  ▄ .▄");
        System.out.println("▐█ ▀ ▪▐█ ▀█ ██•  ▐█ ▀█ ▐█ ▌▪•██  ██ ▐█ ▌▪    ▐█ ▀. ▀▄.▀·▐█ ▀█ ▀▄ █·▐█ ▌▪██▪▐█");
        System.out.println("▄█ ▀█▄▄█▀▀█ ██▪  ▄█▀▀█ ██ ▄▄ ▐█.▪▐█·██ ▄▄    ▄▀▀▀█▄▐▀▀▪▄▄█▀▀█ ▐▀▀▄ ██ ▄▄██▀▐█");
        System.out.println("▐█▄▪▐█▐█ ▪▐▌▐█▌▐▌▐█ ▪▐▌▐███▌ ▐█▌·▐█▌▐███▌    ▐█▄▪▐█▐█▄▄▌▐█ ▪▐▌▐█•█▌▐███▌██▌▐▀");
        System.out.println("·▀▀▀▀  ▀  ▀ .▀▀▀  ▀  ▀ ·▀▀▀  ▀▀▀ ▀▀▀·▀▀▀      ▀▀▀▀  ▀▀▀  ▀  ▀ .▀  ▀·▀▀▀ ▀▀▀ ·");
        System.out.println("");

        Rect[] lemons = camera.processFrame();
        if (lemons.length == 0) {
            System.out.println("Failed to get lemons.");
            return;
        }
        
        String selection = selectPathFromRects(lemons);
        Trajectory trajectory = getTrajectory(selection);

        drive.resetOdometry(new Pose2d());

        RamseteCommand follower = new RamseteCommand(trajectory, drive::getPose,
                new RamseteController(AutoConstants.kRamseteB, AutoConstants.kRamseteZeta),
                new SimpleMotorFeedforward(DriveConstants.kSC, DriveConstants.kVC, DriveConstants.kAC),
                DriveConstants.kKinematics, drive::getRates, new PIDController(DriveConstants.kPL, 0, 0),
                new PIDController(DriveConstants.kPR, 0, 0), drive::setRawVoltage, drive);

        addCommands(follower);
    }

    private Trajectory getTrajectory(String selection) {
        Trajectory trajectory = new Trajectory();
        try {
            Path trajectoryPath = Filesystem.getDeployDirectory().toPath().resolve("paths/metermeter/output" + selection+".wpilib.json");
            trajectory = TrajectoryUtil.fromPathweaverJson(trajectoryPath);
        } catch (InvalidPathException ex) {
            DriverStation.reportError("Unable to open trajectory: " + selection, ex.getStackTrace());
        } catch (IOException ex) {
            DriverStation.reportError("Unable to open trajectory: " + selection, ex.getStackTrace());
        }
        return trajectory;
    }

    public static String selectPathFromRects(Rect[] lemons) {
        ArrayList<Rect[]> tris  = new ArrayList<Rect[]>();
        for(int r = 0; r < lemons.length-2; r++) {
            tris.add(new Rect[] {lemons[r], lemons[r+1], lemons[r+2]});
        }

        for(int i = 0; i < tris.size(); i++) {
            for(int r = 0; r < 4; r++) {
                //For each reference triangle, see if it is within tolerance
                boolean correctPath = triangleWithinTolerance(tris.get(i), Constants.Vision.realTris[r], 1.50);
                if(!correctPath) {
                    continue;
                }
                if(r == 0) {
                    return "Galactic Search Blue A";
                } else if(r==1) {
                    return "Galactic Search Blue B";
                } else if(r==2) {
                    return "Galactic Search Red A";
                } else if(r==3) {
                    return "Galactic Search Red B";
                }
            }
            
        }
        return "none";
    }

    public static boolean triangleWithinTolerance(Rect[] tri, Rect[] reference, double scale) {

        //Sort triangles
        Arrays.sort(tri, Comparator.comparingDouble(Rect::area));
        Arrays.sort(reference, Comparator.comparingDouble(Rect::area));


        //Make sure scaling is correct, otherwise its a different path.
        for(int i = 0; i < 3; i++) {
            //Checks the height of the biggest rect
            if(tri[i].height > reference[i].height*scale || tri[i].height < reference[i].height-((reference[i].height*scale)-reference[i].height)) {
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

}
