package frc.robot.commands;

import edu.wpi.first.wpilibj.geometry.Pose2d;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.subsystems.Drive;

public class Rotate extends CommandBase {
    Drive m_drive;
    double dist;
    public Rotate(Drive drive, double rotation) {
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
        m_drive.feedForwardTank(0.75,-0.75);
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
