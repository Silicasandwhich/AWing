package frc.robot.util;

import java.io.IOException;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.util.concurrent.Semaphore;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Filesystem;
import edu.wpi.first.wpilibj.trajectory.Trajectory;
import edu.wpi.first.wpilibj.trajectory.TrajectoryUtil;

public class TrajectoryLoader implements Runnable {
    private Semaphore done = new Semaphore(1);
    private Thread loader;

    private String pathName;
    private Trajectory trajectory;

    public TrajectoryLoader(String name) {
        
        loader = new Thread(this);
        this.pathName = name;
        loader.start();
    }

    public synchronized Trajectory getTrajectory() throws InterruptedException {
        done.acquire();
        return trajectory;
    }

    public void run() {
        try {
            done.acquire();
            Path trajectoryPath = Filesystem.getDeployDirectory().toPath().resolve(this.pathName);
            this.trajectory = TrajectoryUtil.fromPathweaverJson(trajectoryPath);
        } catch (IOException ex) {
            DriverStation.reportError("Unable to open trajectory: " + this.pathName, ex.getStackTrace());
        } catch (InterruptedException e) {
            DriverStation.reportError("Semaphore could not be aquired while loading path: "+this.pathName, e.getStackTrace());
        } finally {
            System.out.println("Releaseing Semaphore");
            done.release();
        }
    }
}
