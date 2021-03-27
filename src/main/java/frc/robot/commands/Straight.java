package frc.robot.commands;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.geometry.Pose2d;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.subsystems.Drive;

public class Straight extends CommandBase {
    Drive m_drive;
    double dist;
    NetworkTableInstance n_inst = NetworkTableInstance.getDefault();
    NetworkTable t_table = n_inst.getTable("Shuffleboard").getSubTable("Teleop");

    NetworkTableEntry maxSpeed = t_table.getEntry("Max Speed");

    public Straight(Drive drive, double dist) {
        addRequirements(drive);
        m_drive = drive;
        this.dist = dist;
    }

    @Override
    public void initialize() {
        m_drive.simpleTank(0,0);
        m_drive.resetOdometry(new Pose2d());
    }

    @Override
    public void execute() {
        m_drive.feedForwardTank(0.75 * maxSpeed.getDouble(1) , 0.75 * maxSpeed.getDouble(1));
    }

    @Override
    public boolean isFinished() {
        if(m_drive.getPose().getY() >= dist) {
            return true;
        }
        return false;
    }

    @Override
    public void end(boolean interrupted) {
        m_drive.simpleTank(0,0);
    }

}
