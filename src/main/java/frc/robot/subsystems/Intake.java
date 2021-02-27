package frc.robot.subsystems;

import java.util.Map;

import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.wpilibj.Spark;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInWidgets;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants.IntakeConstants;;

public class Intake extends SubsystemBase {
    
    private Spark m_motor;
    private NetworkTableEntry intakeSpeed;

    public Intake() {
        m_motor = new Spark(IntakeConstants.kIntake);

        setupNetworkTables();
    }

    public void setupNetworkTables() {
        intakeSpeed = Shuffleboard.getTab("Intake").addPersistent("Intake Speed", 1)
            .withWidget(BuiltInWidgets.kNumberSlider).withProperties(Map.of("Min",-1, "Max",1)).getEntry();
    }

    public void startSpeed(boolean forward) {
        if(forward) {
            m_motor.set(intakeSpeed.getDouble(1));
        } else {
            m_motor.set(-intakeSpeed.getDouble(1));
        }
    }

    public void stop() {
        m_motor.set(0);
    }

}
