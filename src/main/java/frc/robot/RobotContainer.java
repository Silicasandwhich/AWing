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
import java.util.HashMap;
import java.util.Map;

import org.opencv.core.Rect;

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
import edu.wpi.first.wpilibj.shuffleboard.BuiltInWidgets;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.trajectory.Trajectory;
import edu.wpi.first.wpilibj.trajectory.TrajectoryUtil;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.RamseteCommand;
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
import frc.robot.util.TrajectoryLoader;


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
    private NetworkTableEntry controlEntry;

    private NetworkTableEntry cameraEntry;

    // Subsystems
    private Drive m_drive = new Drive();
    private Intake m_intake = new Intake();
    private Camera m_camera = new Camera();

    // Commands
    private Command teleopCommand = new TeleopCommand(m_drive);
    private IntakeCommand intakeForward = new IntakeCommand(m_intake, true);
    private Command intakeOut = new IntakeCommand(m_intake, false);

    private Map<String, String> paths = Map.of(
            "barrel", "metermeter/output/Barrel Path.wpilib.json",
            "bounce", "metermeter/output/Bounce Path.wpilib.json",
            "slalom","metermeter/output/Slalom Path.wpilib.json",
            "blue a","metermeter/output/Galactic Search Blue A.wpilib.json",
            "blue b","metermeter/output/Galactic Search Blue B.wpilib.json",
            "red a","metermeter/output/Galactic Search Red A.wpilib.json",
            "red b","metermeter/output/Galactic Search Red B.wpilib.json",
            "wonkey", "metermeter/output/wonkey.wpilib.json"
        );

    private Map<String, Trajectory> trajectories = new HashMap<String, Trajectory>();

    private RobotContainer() {

        //Setup Control Scheme Chooser
        controlScheme = new SendableChooser<String>();
        controlScheme.addOption(controlJoystick, controlJoystick);
        controlScheme.setDefaultOption(controlXbox, controlXbox);
        Shuffleboard.getTab("Teleop").add("Control Scheme", controlScheme);
        
        controlEntry = NetworkTableInstance.getDefault().getTable("Shuffleboard").getSubTable("Teleop").getSubTable("Control Scheme").getEntry("active");

        controlEntry.addListener(event -> {
            checkControls();
        }, EntryListenerFlags.kNew | EntryListenerFlags.kUpdate);
        
        cameraEntry = Shuffleboard.getTab("Vision").add("Vision Debug", false).withWidget(BuiltInWidgets.kToggleButton)
                .getEntry();
        cameraEntry.addListener(event -> {
            if (cameraEntry.getBoolean(false)) {
                m_camera.startCapture();
            } else {
                m_camera.stopCapture();
            }
        }, EntryListenerFlags.kNew | EntryListenerFlags.kUpdate);


        //Path Chooser Stuff
        pathChooser = new SendableChooser<String>();

        /**
         * Link to each of our paths here. Be sure to put them inside the
         * "deploy/paths/" folder. --------------------------------------------- FORMAT:
         * pathChooser.addOption("[name of path]", "[location of path]");
         */

        pathChooser.setDefaultOption("None", "none");

        pathChooser.addOption("Galactic Search", "galaxy");

        pathChooser.addOption("Barrel", "barrel");
        pathChooser.addOption("Bounce", "bounce");
        pathChooser.addOption("Slalom", "slalom");
        pathChooser.addOption("Blue A", "blue a");
        pathChooser.addOption("Blue B", "blue b");
        pathChooser.addOption("Red A",  "red a");
        pathChooser.addOption("Red B",  "red b");
        pathChooser.addOption("Wonkey", "wonkey");

        Map<String, TrajectoryLoader> loaders = new HashMap<String, TrajectoryLoader>();

        paths.keySet().forEach(key -> {
            String path = paths.get(key);
            loaders.put(key, new TrajectoryLoader(path));
        });

        System.out.println("Loading Trajectories");
        do {
            loaders.keySet().forEach(loaderKey -> {
                try {
                    System.out.println("Loading Trajectory: "+ loaderKey);
                    trajectories.put(loaderKey, loaders.get(loaderKey).getTrajectory());
                } catch (InterruptedException e) {
                    trajectories.clear();
                }
            }); 
        } while (trajectories.size() == 0);
      
        Shuffleboard.getTab("Auto").add("Auto Command", pathChooser);
        
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

        if(pathChooser.getSelected().equals("galaxy")) {
            //Return a galactic search to run with intake command, then stop robot.
            return (new GalacticSearch(m_drive, m_camera).raceWith(new IntakeCommand(m_intake, true))).andThen(new StopRobot(m_drive, m_intake));
        }

        System.out.println("=======================================================================");
        System.out.println("");
        System.out.println(" ██████╗ ██╗   ██╗███████╗██████╗ ██████╗ ██████╗ ██╗██╗   ██╗███████╗");
        System.out.println("██╔═══██╗██║   ██║██╔════╝██╔══██╗██╔══██╗██╔══██╗██║██║   ██║██╔════╝");
        System.out.println("██║   ██║██║   ██║█████╗  ██████╔╝██║  ██║██████╔╝██║██║   ██║█████╗  ");
        System.out.println("██║   ██║╚██╗ ██╔╝██╔══╝  ██╔══██╗██║  ██║██╔══██╗██║╚██╗ ██╔╝██╔══╝  ");
        System.out.println("╚██████╔╝ ╚████╔╝ ███████╗██║  ██║██████╔╝██║  ██║██║ ╚████╔╝ ███████╗");
        System.out.println(" ╚═════╝   ╚═══╝  ╚══════╝╚═╝  ╚═╝╚═════╝ ╚═╝  ╚═╝╚═╝  ╚═══╝  ╚══════╝");
        System.out.println("");
        System.out.println("=======================================================================");      

        try {
            System.out.println("=== Loading auto path. ===");
            Path trajectoryPath = Filesystem.getDeployDirectory().toPath().resolve(pathChooser.getSelected());
            Trajectory autoTrajectory = TrajectoryUtil.fromPathweaverJson(trajectoryPath);
            System.out.println("=== Auto using path "+pathChooser.getSelected()+ " ===");

            return generateRamseteCommand(autoTrajectory).andThen(new StopRobot(m_drive,m_intake));

        } catch (IOException e) {
            DriverStation.reportError("Could not find path", e.getStackTrace());
        }
        
        return new StopRobot(m_drive, m_intake);

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
                DriveConstants.kKinematics, // the robot's kinematics (not a function that returns the kinematics, just the kinematics itself)
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
    
    public void stopRobot() {
        Command stop = new StopRobot(m_drive, m_intake);
        stop.schedule();
    }

    public void testExMachina() {
        Rect[] rectangles = m_camera.processFrame();
        System.out.println(GalacticSearch.selectPathFromRects(rectangles));
    }

    public Map<String, String> getPaths() {
        return this.paths;
    }

    public Map<String, Trajectory> getTrajectories() {
        return this.trajectories;
    }

}

