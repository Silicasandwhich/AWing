// RobotBuilder Version: 3.1
//
// This file was generated by RobotBuilder. It contains sections of
// code that are automatically generated and assigned by robotbuilder.
// These sections will be updated in the future when you export to
// Java from RobotBuilder. Do not put any code or make any change in
// the blocks indicating autogenerated code or it will be lost on an
// update. Deleting the comments indicating the section will prevent
// it from being updated in the future.

package frc.robot;

import edu.wpi.first.wpilibj.GenericHID;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.XboxController.Button;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
// BEGIN AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=IMPORTS
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import edu.wpi.first.wpilibj2.command.button.JoystickButton;
import frc.robot.commands.*;
import frc.robot.subsystems.*;
import frc.robot.subsystems.Camera.Camera;

// END AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=IMPORTS

/**
 * This class is where the bulk of the robot should be declared. Since
 * Command-based is a "declarative" paradigm, very little robot logic should
 * actually be handled in the {@link Robot} periodic methods (other than the
 * scheduler calls). Instead, the structure of the robot (including subsystems,
 * commands, and button mappings) should be declared here.
 */
public class RobotContainer {

    private static RobotContainer m_robotContainer = new RobotContainer();

    // BEGIN AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=DECLARATIONS
    // The robot's subsystems
    private final Drive m_drive = new Drive();
    private final Intake m_intake = new Intake();
    private final Camera m_camera = new Camera();

    // Joysticks
    private final Joystick rightStick = new Joystick(1);
    private final Joystick leftStick = new Joystick(0);
    
    private final JoystickButton intakeButton = new JoystickButton(rightStick, 1);
    private final JoystickButton outtakeButton = new JoystickButton(leftStick, 1);

    private final XboxController xbox = new XboxController(4);
    private final JoystickButton xIntakeButton = new JoystickButton(xbox, Button.kBumperRight.value);
    private final JoystickButton xOuttakeButton = new JoystickButton(xbox, Button.kBumperLeft.value);
    // A chooser for autonomous commands
    SendableChooser<Command> m_autoChooser = new SendableChooser<>();
    SendableChooser<String> m_controlChooser = new SendableChooser<String>();

    private Command m_teleop = new TeleopCommand(m_drive);

    /**
     * The container for the robot. Contains subsystems, OI devices, and commands.
     */

    private RobotContainer() {
        // BEGIN AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=SMARTDASHBOARD
        // Smartdashboard Subsystems
        Shuffleboard.getTab("Drive").add(m_drive);

        // SmartDashboard Buttons
        Shuffleboard.getTab("Autonomous").add("Autonomous Command", new AutonomousCommand());


        // END AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=SMARTDASHBOARD
        // Configure the button bindings
        configureButtonBindings();

        // Configure default commands
        // BEGIN AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=SUBSYSTEM_DEFAULT_COMMAND

        // END AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=SUBSYSTEM_DEFAULT_COMMAND

        // Configure autonomous sendable chooser
        // BEGIN AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=AUTONOMOUS

        m_autoChooser.setDefaultOption("Autonomous Command", new AutonomousCommand());

        m_controlChooser.setDefaultOption("Joysticks", "joysticks");
        m_controlChooser.addOption("Xbox Controller", "xbox");

        // END AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=AUTONOMOUS

        SmartDashboard.putData("Auto Mode", m_autoChooser);

        Shuffleboard.getTab("Teleop").add("Input Option", m_controlChooser);

        
    }

    public static RobotContainer getInstance() {
        return m_robotContainer;
    }

    /**
     * Use this method to define your button->command mappings. Buttons can be
     * created by instantiating a {@link GenericHID} or one of its subclasses
     * ({@link edu.wpi.first.wpilibj.Joystick} or {@link XboxController}), and then
     * passing it to a {@link edu.wpi.first.wpilibj2.command.button.JoystickButton}.
     */
    private void configureButtonBindings() {
        // BEGIN AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=BUTTONS
        // Create some buttons

        // END AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=BUTTONS
        
        intakeButton.whileHeld(new RunIntake(m_intake,false,"joysticks"));
        outtakeButton.whileHeld(new RunIntake(m_intake,true,"joysticks"));

        xIntakeButton.whileHeld(new RunIntake(m_intake,false,"xbox"));
        xOuttakeButton.whileHeld(new RunIntake(m_intake,true,"xbox"));

    }

    // BEGIN AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=FUNCTIONS
    public Joystick getLeftStick() {
        return leftStick;
    }

    public Joystick getRightStick() {
        return rightStick;
    }

    // END AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=FUNCTIONS

    /**
     * Use this to pass the autonomous command to the main {@link Robot} class.
     *
     * @return the command to run in autonomous
     */
    public Command getAutonomousCommand() {
        // The selected command will be run in autonomous
        return m_autoChooser.getSelected();
    }

    public String getDriveOption() {
        return m_controlChooser.getSelected();
    }

    public void startTeleop() {
        CommandScheduler.getInstance().schedule(m_teleop);
    }

	public XboxController getXboxController() {
		return xbox;
	}
}
