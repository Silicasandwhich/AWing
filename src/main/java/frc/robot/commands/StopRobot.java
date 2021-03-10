package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.subsystems.Drive;
import frc.robot.subsystems.Intake;

public class StopRobot extends CommandBase {

    Drive m_drive;
    Intake m_intake;

    public StopRobot(Drive drive,Intake intake) {

        this.m_drive = drive;
        this.m_intake = intake;
        addRequirements(drive, intake);
    
    }

    @Override
    public void initialize() {
    }

    @Override
    public boolean isFinished() {
        return false;
    }

    @Override
    public void execute() {
        m_drive.simpleTank(0,0);
        m_intake.stop();
    }

}
