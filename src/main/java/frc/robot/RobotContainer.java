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
import java.util.List;

import edu.wpi.first.networktables.EntryListenerFlags;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Filesystem;
import edu.wpi.first.wpilibj.GenericHID;
import edu.wpi.first.wpilibj.GenericHID.Hand;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.controller.PIDController;
import edu.wpi.first.wpilibj.controller.RamseteController;
import edu.wpi.first.wpilibj.controller.SimpleMotorFeedforward;
import edu.wpi.first.wpilibj.geometry.Pose2d;
import edu.wpi.first.wpilibj.geometry.Rotation2d;
import edu.wpi.first.wpilibj.geometry.Translation2d;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInWidgets;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.trajectory.Trajectory;
import edu.wpi.first.wpilibj.trajectory.TrajectoryConfig;
import edu.wpi.first.wpilibj.trajectory.TrajectoryGenerator;
import edu.wpi.first.wpilibj.trajectory.TrajectoryUtil;
import edu.wpi.first.wpilibj.trajectory.TrajectoryParameterizer.TrajectoryGenerationException;
import edu.wpi.first.wpilibj.trajectory.constraint.DifferentialDriveVoltageConstraint;
import edu.wpi.first.wpilibj.util.Units;
// BEGIN AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=IMPORTS
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.ParallelRaceGroup;
import edu.wpi.first.wpilibj2.command.RamseteCommand;
import edu.wpi.first.wpilibj2.command.WaitCommand;
import edu.wpi.first.wpilibj2.command.button.JoystickButton;
import frc.robot.Constants.AutoConstants;
import frc.robot.Constants.DriveConstants;
import frc.robot.commands.GalacticSearch;
import frc.robot.commands.IntakeCommand;
import frc.robot.commands.StopRobot;
import frc.robot.commands.TeleopCommand;
import frc.robot.subsystems.Drive;
import frc.robot.subsystems.Intake;
import frc.robot.subsystems.camera.Camera;

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
    private NetworkTableEntry deadman;

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
    private boolean m_testStarted = false;

    private RobotContainer() {
        deadman = NetworkTableInstance.getDefault().getTable("Safety").getEntry("deadman");

        controlScheme = new SendableChooser<String>();
        controlScheme.addOption(controlJoystick, controlJoystick);
        controlScheme.setDefaultOption(controlXbox, controlXbox);
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
        pathChooser.addOption("Barrel Run", "stupid/output/barrel.wpilib.json");
        pathChooser.addOption("Bounce Path", "stupid/output/bounce.wpilib.json");
        pathChooser.addOption("Slalom Path", "slalom");
        pathChooser.addOption("Blue Path B", "stupid/output/blue_b.wpilib.json");
        pathChooser.addOption("Red Path A", "stupid/output/red_a.wpilib.json");
        pathChooser.addOption("Red Path B", "stupid/output/red_b.wpilib.json");
        pathChooser.addOption("Blue Path a", "stupid/output/blue_a.wpilib.json");
        pathChooser.addOption("Straight line", "straight");        

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
        DifferentialDriveVoltageConstraint voltageConstraint = new DifferentialDriveVoltageConstraint(
            new SimpleMotorFeedforward(
                DriveConstants.kSC,
                DriveConstants.kVC,
                DriveConstants.kAC
            ), m_drive.getKinematics(), 8);
        
        TrajectoryConfig config = 
            new TrajectoryConfig(AutoConstants.kMaxSpeed,AutoConstants.kMaxAcceleration).setKinematics(Drive.getKinematics()).addConstraint(voltageConstraint);

        // The selected command will be run in autonomous
        setAutoStatus(1);
        String pathString = pathChooser.getSelected();
        if (pathString == "none") {
            setAutoStatus(9999);
            return new WaitCommand(1);
        } else if (pathString == "galaxy") {
            //TODO GALACTIC SEARCH: 1000s
            return new GalacticSearch(m_drive, m_camera).raceWith(intakeForward).andThen(new StopRobot(m_drive,m_intake));
        } else if (pathString == "straight") {
            
            Trajectory autoTrajectory = TrajectoryGenerator.generateTrajectory(
                new Pose2d(0,0,new Rotation2d(0)), List.of(),
                new Pose2d(1,0,new Rotation2d(0)), config);

            RamseteCommand ramseteCommand = generateRamseteCommand(autoTrajectory);
            setAutoStatus(2003); //Ramsete Constructed
            m_drive.resetOdometry(autoTrajectory.getInitialPose());
            setAutoStatus(2004); //Reset Odometry
            return ramseteCommand.andThen(new StopRobot(m_drive,m_intake));
        } else if (pathString == "slalom") {
            //Slalom Trajectory
            Trajectory autoTrajectory = TrajectoryGenerator.generateTrajectory(
                List.of(
                    //Paths relative to field origin to front bumper.
                    //Starting Position
                    new Pose2d(Units.feetToMeters(2),Units.feetToMeters(5), new Rotation2d(0)), 
                    //First Cross
                    new Pose2d(Units.feetToMeters(5), Units.feetToMeters(7.5), new Rotation2d(Math.PI/4)),
                    //Straight
                    new Pose2d(Units.feetToMeters(8), Units.feetToMeters(10), new Rotation2d(0)),
                    new Pose2d(Units.feetToMeters(8), Units.feetToMeters(20), new Rotation2d(0)),
                    //Second Cross
                    new Pose2d(Units.feetToMeters(5), Units.feetToMeters(22.5), new Rotation2d(Math.PI/4)),
                    //Circle Back
                    new Pose2d(Units.feetToMeters(2),Units.feetToMeters(25), new Rotation2d(0)), 
                    //Vertical
                    new Pose2d(Units.feetToMeters(5), Units.feetToMeters(27.5), new Rotation2d(Math.PI/2)),
                    //Go Backwards
                    new Pose2d(Units.feetToMeters(8),Units.feetToMeters(25), new Rotation2d(Math.PI)), 
                    //Third Cross
                    new Pose2d(Units.feetToMeters(5), Units.feetToMeters(22.5), new Rotation2d(5*Math.PI/4)),
                    //Second Straight
                    new Pose2d(Units.feetToMeters(2), Units.feetToMeters(20), new Rotation2d(Math.PI)),
                    new Pose2d(Units.feetToMeters(2), Units.feetToMeters(10), new Rotation2d(Math.PI)),
                    //Fourth Cross
                    new Pose2d(Units.feetToMeters(5), Units.feetToMeters(7.5), new Rotation2d(Math.PI*3/4)),
                    //Finishing Pose
                    new Pose2d(Units.feetToMeters(8), Units.feetToMeters(2.5), new Rotation2d(Math.PI))
                ),
                config);
            RamseteCommand ramseteCommand = generateRamseteCommand(autoTrajectory);
            setAutoStatus(2003);
            m_drive.resetOdometry(autoTrajectory.getInitialPose());
            setAutoStatus(2004);
            return ramseteCommand.andThen(new StopRobot(m_drive, m_intake));
        }


        try {
            setAutoStatus(2000); //Started Path Only
            Trajectory autoTrajectory = new Trajectory();
            pathString = "paths/".concat(pathString);
            Path pathJSON = Filesystem.getDeployDirectory().toPath().resolve(pathString);
            System.out.println("Auto | "+ pathJSON);
            autoTrajectory = TrajectoryUtil.fromPathweaverJson(pathJSON);
            setAutoStatus(2002); //Trajectory Aquired
            //Auto Command
            RamseteCommand ramseteCommand = generateRamseteCommand(autoTrajectory);
            setAutoStatus(2003); //Ramsete Constructed
            m_drive.resetOdometry(autoTrajectory.getInitialPose());
            setAutoStatus(2004); //Reset Odometry

            return (new ParallelRaceGroup(ramseteCommand, new IntakeCommand(m_intake, true)).andThen(new StopRobot(m_drive, m_intake)));
            // Run path following command, then stop at the end.
            //return ramseteCommand.andThen(() -> m_drive.setRawVoltage(0, 0), m_drive).andThen(new WaitCommand(4));
        } catch (IOException ex) {
            DriverStation.reportError("Unable to open trajectory: " + pathString, ex.getStackTrace());
            setAutoStatus(2900);
            return new WaitCommand(1);
        }
    }
    
    public RamseteCommand generateRamseteCommand(Trajectory autoTrajectory) {
        // this is a really long and confusing constructor, so here's basically what it wants
        return new RamseteCommand(
                autoTrajectory, // the trajectory to follow
                m_drive::getPose, // a function that returns the robot's pose2d
                new RamseteController(AutoConstants.kRamseteB, AutoConstants.kRamseteZeta), // a ramsete controller (these B and Zeta values are basically universal)
                new SimpleMotorFeedforward(DriveConstants.kSC, // a feedforward that controls both sides of the drive
                                            DriveConstants.kVC, // (make sure you're using the combined characterization values, not individual ones)
                                            DriveConstants.kAC),
                Drive.getKinematics(), // the robot's kinematics (not a function that returns the kinematics, just the kinematics itself)
                m_drive::getRates, // a function that returns the encoder rates in the form of DifferentialDriveWheelSpeeds
                new PIDController(DriveConstants.kPL, 0, 0), // a PID controller for the left side
                new PIDController(DriveConstants.kPR, 0, 0), // a PID controller for the right side
                m_drive::setRawVoltage, // a function that controlls the voltage of the drive (look at subsystems/drive.java so see how this should be layed out)
                m_drive // the drive subsystem itself
            );
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
     * 90: Deadman Stop
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
            case 90:
                autoStatusString.setString("Deadman Check Failed");
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
            case 2005:
                autoStatusString.setString("Awaiting Deadman");
                break;
            case 2006:
                autoStatusString.setString("Running Path");
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

    public boolean getDeadman() {
        return deadman.getBoolean(false);
    }
    
    public void stopRobot() {
        Command stop = new StopRobot(m_drive, m_intake);
        stop.schedule();
    }

	public void startTest() {
        m_testStarted = true;
    }
    
    public void stopTest() {
        m_testStarted = false;
    }

	public boolean testStarted() {
		return m_testStarted;
	}

}

