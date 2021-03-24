package frc.robot.commands;

import edu.wpi.first.wpilibj.geometry.Pose2d;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.subsystems.Drive;

public class Straight extends CommandBase {
    Drive m_drive;
    double dist;
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
        m_drive.simpleTank(0.5,0.5);
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
