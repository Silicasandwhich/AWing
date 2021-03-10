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

import java.io.IOException;
import java.nio.file.Path;
import java.util.Set;

import edu.wpi.first.networktables.EntryListenerFlags;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Filesystem;
import edu.wpi.first.wpilibj.GenericHID;
import edu.wpi.first.wpilibj.GenericHID.Hand;
import edu.wpi.first.wpilibj.controller.PIDController;
import edu.wpi.first.wpilibj.controller.RamseteController;
import edu.wpi.first.wpilibj.controller.SimpleMotorFeedforward;
import edu.wpi.first.wpilibj.geometry.Pose2d;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInWidgets;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.trajectory.Trajectory;
import edu.wpi.first.wpilibj.trajectory.TrajectoryUtil;
// BEGIN AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=IMPORTS
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandBase;
import edu.wpi.first.wpilibj2.command.RamseteCommand;
import edu.wpi.first.wpilibj2.command.Subsystem;
import edu.wpi.first.wpilibj2.command.WaitCommand;
import edu.wpi.first.wpilibj2.command.button.JoystickButton;
import frc.robot.subsystems.*;
import frc.robot.subsystems.camera.Camera;
import frc.robot.Constants.AutoConstants;
import frc.robot.Constants.DriveConstants;
import frc.robot.commands.*;

// END AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=IMPORTS

/**
 * This class is where the bulk of the robot should be declared. Since
 * Command-based is a "declarative" paradigm, very little robot logic should
 * actually be handled in the {@link Robot} periodic methods (other than the
 * scheduler calls). Instead, the structure of the robot (including subsystems,
 * commands, and button mappings) should be declared here.
 */
public class RobotContainer {

    public static final String controlJoystick = "Joysticks";
    public static final String controlXbox = "Xbox";
    private String currentScheme = "";

    private GenericHID leftStick;
    private GenericHID rightStick;

    private JoystickButton intakeButton;
    private JoystickButton outtakeButton;

    private static RobotContainer m_robotContainer = new RobotContainer();

    private SendableChooser<String> controlScheme;

    private SendableChooser<String> pathChooser;

    private NetworkTableEntry cameraEntry;
    private NetworkTableEntry autoStatusEntry;
    private NetworkTableEntry autoStatusString;
    private static int autoStatus = -1;

    private NetworkTableEntry galacticPathString;

    // Subsystems
    private Drive m_drive = new Drive();
    private Intake m_intake = new Intake();
    private Camera m_camera = new Camera();

    // Commands
    private Command teleopCommand = new TeleopCommand(m_drive);
    private IntakeCommand intakeForward = new IntakeCommand(m_intake, true);
    private Command intakeOut = new IntakeCommand(m_intake, false);

    private RobotContainer() {
        controlScheme = new SendableChooser<String>();
        controlScheme.setDefaultOption(controlJoystick, controlJoystick);
        controlScheme.addOption(controlXbox, controlXbox);
        Shuffleboard.getTab("Teleop").add("Control Scheme", controlScheme);

        cameraEntry = Shuffleboard.getTab("Vision").add("Vision Debug", false).withWidget(BuiltInWidgets.kToggleButton)
                .getEntry();
        cameraEntry.addListener(event -> {
            if (cameraEntry.getBoolean(false)) {
                m_camera.startCapture();
            } else {
                m_camera.stopCapture();
            }
        }, EntryListenerFlags.kNew | EntryListenerFlags.kImmediate | EntryListenerFlags.kUpdate);

        pathChooser = new SendableChooser<String>();

        /**
         * Link to each of our paths here. Be sure to put them inside the
         * "deploy/paths/" folder. --------------------------------------------- FORMAT:
         * pathChooser.addOption("[name of path]", "[location of path]";
         */

        pathChooser.setDefaultOption("None", "none");
        pathChooser.addOption("Rectangle Yay!", "rectangle yay.wpilib.json");
        pathChooser.addOption("Galactic Search", "galaxy");
        pathChooser.addOption("Barrel Run", "barrel.wpilib.json");
        pathChooser.addOption("Bounce Path", "bounce.wpilib.json");
        pathChooser.addOption("Slalom Path", "slalom.wpilib.json");
        pathChooser.addOption("Blue Path A", "blue_path_a.wpilib.json");
        pathChooser.addOption("Blue Path B", "blue_path_b.wpilib.json");
        pathChooser.addOption("Red Path A", "red_path_a.wpilib.json");
        pathChooser.addOption("Red Path B", "red_path_b.wpilib.json");
        pathChooser.addOption("Wonkey Donkey", "weird.wpilib.json");
        pathChooser.addOption("Big Wonkey Donkey", "output/Big Wonkey.wpilib.json");
        pathChooser.addOption("Please just work", "Unnamed.wpilib.json");
        pathChooser.addOption("Please just work 2", "output/Difinitive.wpilib.json");
        

        Shuffleboard.getTab("Auto").add("Auto Command", pathChooser);
        autoStatusEntry = Shuffleboard.getTab("Auto").add("Status", autoStatus).withWidget(BuiltInWidgets.kGraph).getEntry();
        autoStatusString = Shuffleboard.getTab("Auto").add("Info", "Constructing").getEntry();
        
        galacticPathString = Shuffleboard.getTab("Auto").add("Galactic Path","Not Set").getEntry();
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
        intakeButton.whileHeld(intakeForward);
        outtakeButton.whileHeld(intakeOut);
    }

    /**
     * Use this to pass the autonomous command to the main {@link Robot} class.
     *
     * @return the command to run in autonomous
     */
    public Command getAutonomousCommand() {
        m_drive.resetOdometry(new Pose2d());
        // The selected command will be run in autonomous
        setAutoStatus(1);
        String pathString = pathChooser.getSelected();
        if (pathString == "none") {
            setAutoStatus(9999);
            return new WaitCommand(1);
        } else if (pathString == "galaxy") {
            //TODO GALACTIC SEARCH: 1000s
            return new GalacticSearch(m_drive, intakeForward, m_camera);
        }
        try {
            setAutoStatus(2000); //Started Path Only
            Trajectory autoTrajectory = new Trajectory();
            pathString = "paths/".concat(pathString);
            Path PathJSON = Filesystem.getDeployDirectory().toPath().resolve(pathString);
            System.out.println("Auto | "+ PathJSON);
            autoTrajectory = TrajectoryUtil.fromPathweaverJson(PathJSON);
            setAutoStatus(2002); //Trajectory Aquired
            //Auto Command
            // this is a really long and confusing constructor, so here's basically what it wants
            RamseteCommand ramseteCommand = new RamseteCommand(
                autoTrajectory, // the trajectory to follow
                m_drive::getPose, // a function that returns the robot's pose2d
                new RamseteController(AutoConstants.kRamseteB, AutoConstants.kRamseteZeta), // a ramsete controller (these B and Zeta values are basically universal)
                new SimpleMotorFeedforward(DriveConstants.kSC, // a feedforward that controls both sides of the drive
                                            DriveConstants.kVC, // (make sure you're using the combined characterization values, not individual ones)
                                            DriveConstants.kAC),
                m_drive.getKinematics(), // the robot's kinematics (not a function that returns the kinematics, just the kinematics itself)
                m_drive::getRates, // a function that returns the encoder rates in the form of DifferentialDriveWheelSpeeds
                new PIDController(DriveConstants.kPL, 0, 0), // a PID controller for the left side
                new PIDController(DriveConstants.kPR, 0, 0), // a PID controller for the right side
                m_drive::setRawVoltage, // a function that controlls the voltage of the drive (look at subsystems/drive.java so see how this should be layed out)
                m_drive // the drive subsystem itself
            );
            setAutoStatus(2003); //Ramsete Constructed
            m_drive.resetOdometry(autoTrajectory.getInitialPose());
            setAutoStatus(2004); //Reset Odometry

            // Run path following command, then stop at the end.
            return ramseteCommand.andThen(() -> m_drive.setRawVoltage(0, 0), m_drive).andThen(new WaitCommand(4));
        } catch (IOException ex) {
            DriverStation.reportError("Unable to open trajectory: " + pathString, ex.getStackTrace());
            setAutoStatus(2900);
            return new WaitCommand(1);
        }
    }

    public void checkControls() {
        String selected = controlScheme.getSelected();
        if(selected.equals(currentScheme)) {
            return;
        }

        if(selected.equals(controlJoystick)) {
            leftStick = new Joystick(0);
            rightStick = new Joystick(1);
            intakeButton = new JoystickButton(rightStick, 1);
            outtakeButton = new JoystickButton(leftStick, 1);
        } else {
            leftStick = new XboxController(4);
            rightStick = null;
            intakeButton = new JoystickButton(leftStick, 6);
            outtakeButton = new JoystickButton(leftStick, 5);
        }
        
        configureButtonBindings();

        currentScheme = selected;
    }

	public Command getTeleopCommand() {
		return teleopCommand;
	}

    public double[] getSticks() {
        if(currentScheme.equals(controlJoystick)) {
            return new double[] {-leftStick.getY(), -rightStick.getY()};
        } else {
            return new double[] {-leftStick.getY(Hand.kLeft), -leftStick.getY(Hand.kRight)};
        }
    }

    /**Sets the Auto Status Debug Intger:
     * Reasons for use: Graph easy to see what is happening on a time interval, not possible with just a string.
     * Code references:
     * -1: Autonomous not selected
     * ~0000: Global Statuses
     * 0: Initializing Autonomous
     * 1: Getting Command
     * 2: Command Aquired
     * ~ 1000's: Galactic Search Statuses
     * 0: Constructing Galactic Search
     * 1: Getting Path from Image
     * 2: Trajectory Set
     * 3: Reset odometry
     * 4: Ramsete Constructed
     * 5: ParallelRaceGroup satisfied.
     * ~900s Errors:
     * 0: Could not get Camera
     * 1: No Lemons Found
     * 2: Could not open trajectory.
     * ~ 2000's: Path Only Statuses
     * 0: Path Only Selected
     * 2: Trajectory Aquired
     * 3: Ramsete Constructed
     * 4: Reset Odometry
     * ~ 900s Errors:
     * 0: Could not open selected trajectory.
     * 1: Could not find any lemons. 🚫🍋
     */
    public void setAutoStatus(int status) {
        switch(status) {
            case -1:
                autoStatusString.setString("Not Started");
                break;
            case 0:
                autoStatusString.setString("Initializing");
                break;
            case 1:
                autoStatusString.setString("Getting Command");
                break;
            case 2:
                autoStatusString.setString("Command Aquired");
                break;
            
            //Galactic Search Statuses
            case 1000:
                autoStatusString.setString("Galactic Search");
                break;
            case 1001:
                autoStatusString.setString("Getting Path from Image");
            case 1002:
                autoStatusString.setString("Trajectory Set");
                break;
            case 1003:
                autoStatusString.setString("Reset Odometry");
                break;
            case 1004:
                autoStatusString.setString("Ramsete Constructed");
                break;
            case 1005:
                autoStatusString.setString("Parallel Race Group Satsified");
                break;
            //ERRORS
            case 1900:
                autoStatusString.setString("Could not get camera/lemons.");
                break;
            case 1902:
                autoStatusString.setString("Could not open trajectory");
                break;

            //PATH ONLY STATUSES
            case 2000:
                autoStatusString.setString("Only Path Selected");
                break;
            case 2002:
                autoStatusString.setString("Trajectory Aquired");
                break;
            case 2003:
                autoStatusString.setString("Ramsete Constructed");
                break;
            case 2004:
                autoStatusString.setString("Only Path Selected");
                break;
            case 2900:
                autoStatusString.setString("Could not open trajectory.");
            
            //UNKOWN This is bad if this happens
            default:
                autoStatusString.setString("Unknown");
                break;
        }
        autoStatus = status;
        autoStatusEntry.setNumber(autoStatus);
    }

    public int getAutoStatus() {
        return autoStatus;
    }

    public void setPath(String path) {
        galacticPathString.setString(path);
    }

}
