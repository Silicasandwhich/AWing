package frc.robot.commands;

import java.util.Map;

import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.wpilibj.geometry.Pose2d;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInWidgets;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.RobotContainer;
import frc.robot.subsystems.Drive;

public class TeleopCommand extends CommandBase {

    Drive m_drive;

    // Allow for corrections.
    NetworkTableEntry enableCorrections;
    // Shows that corrections are on.
    NetworkTableEntry correctionsEnabled;
    NetworkTableEntry m_deadzone;

    NetworkTableEntry maxSpeed;

    public TeleopCommand(Drive drive) {

        this.m_drive = drive;
        addRequirements(drive);

        enableCorrections = Shuffleboard.getTab("Teleop").add("Enable Corrections", false)
            .withWidget(BuiltInWidgets.kToggleButton).getEntry();
        correctionsEnabled = Shuffleboard.getTab("Teleop").add("Corrections Enabled", false)
            .withWidget(BuiltInWidgets.kBooleanBox).getEntry();

        m_deadzone = Shuffleboard.getTab("Teleop").add("Deadzone", 0.04)
            .withWidget(BuiltInWidgets.kNumberSlider).withProperties(Map.of("Min", 0,"Max",1)).getEntry();

        maxSpeed = Shuffleboard.getTab("Teleop").add("Max Speed", 2.0)
            .withWidget(BuiltInWidgets.kNumberSlider).withProperties(Map.of("Min",0.5,"Max",2.75)).getEntry();

    }

    @Override
    public void initialize() {
        m_drive.resetOdometry(new Pose2d());
    }

    @Override
    public void execute() {
        double[] joys = RobotContainer.getInstance().getSticks();
        double deadzone = m_deadzone.getDouble(0.02);
        double xSpeed = maxSpeed.getDouble(0.5);
        if(enableCorrections.getBoolean(false) && (Math.abs(joys[0]) > deadzone || Math.abs(joys[1]) > deadzone)) {
            correctionsEnabled.setBoolean(true);
            m_drive.feedForwardTank(joys[0] * xSpeed, joys[1] * xSpeed);
        } else {
            // simple tank
            m_drive.simpleTank(joys[0], joys[1]);
            // the cooler simple tank
            // m_drive.simpleTank(joys[0] * xSpeed/5, joys[1] * xSpeed/5);
            correctionsEnabled.setBoolean(false);
        }
    }

}
