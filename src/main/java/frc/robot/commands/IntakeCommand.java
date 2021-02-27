package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.subsystems.Intake;

public class IntakeCommand extends CommandBase {

    private Intake m_intake;
    private boolean forward;

    public IntakeCommand(Intake intake, boolean forward) {
        m_intake = intake;
        addRequirements(intake);
        this.forward = forward;
    }

    @Override
    public void execute() {
        m_intake.startSpeed(forward);
    }

    @Override
    public boolean isFinished() {
        return false;
    }

    @Override
    public void end(boolean interrupted) {
        m_intake.stop();
    }
}
