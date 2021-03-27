package frc.robot.commands;

import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.geometry.Pose2d;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.subsystems.Drive;

public class Rotate extends CommandBase {
    Drive m_drive;
    double dist;
    NetworkTableEntry maxSpeed;
    public Rotate(Drive drive, double rotation) {
        maxSpeed = NetworkTableInstance.getDefault().getTable("Shuffleboard").getSubTable("Teleop").getEntry("Max Speed");
        addRequirements(drive);
        m_drive = drive;
        this.dist = rotation;
    }

    @Override
    public void initialize() {
        m_drive.simpleTank(0,0);
        m_drive.resetOdometry(new Pose2d());
    }

    @Override
    public void execute() {
        m_drive.feedForwardTank(0.75 * maxSpeed.getDouble(1),-0.75 * maxSpeed.getDouble(1));
    }

    @Override
    public boolean isFinished() {
        if(dist > 0) {
            if(m_drive.getHeading() >= dist) {
                return true;
            }
        } else  {
            if(m_drive.getHeading() <= dist) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void end(boolean interrupted) {
        m_drive.simpleTank(0,0);
    }

}
