package frc.robot.subsystems;

import com.kauailabs.navx.frc.AHRS;

import edu.wpi.first.networktables.EntryListenerFlags;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.Spark;
import edu.wpi.first.wpilibj.controller.SimpleMotorFeedforward;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.geometry.Pose2d;
import edu.wpi.first.wpilibj.geometry.Rotation2d;
import edu.wpi.first.wpilibj.kinematics.DifferentialDriveKinematics;
import edu.wpi.first.wpilibj.kinematics.DifferentialDriveOdometry;
import edu.wpi.first.wpilibj.kinematics.DifferentialDriveWheelSpeeds;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInWidgets;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

import frc.robot.Constants.DriveConstants;

public class Drive extends SubsystemBase {

    private AHRS m_ahrs;

    private Spark s_left;
    private Spark s_right;
    private DifferentialDrive m_driveBase;

    private Encoder e_left;
    private Encoder e_right;

    private SimpleMotorFeedforward ff_left;
    private SimpleMotorFeedforward ff_right;

    private DifferentialDriveOdometry m_odometry;

    @SuppressWarnings("unused")
    private static DifferentialDriveKinematics m_kinematics;

    private Pose2d m_position;

    private NetworkTableEntry b_leftInverted;
    private NetworkTableEntry b_rightInverted;

    public Drive() {

        m_ahrs = new AHRS(DriveConstants.kAHRS);

        s_left = new Spark(DriveConstants.kLeft);
        s_left.setInverted(DriveConstants.bLeftInverted);

        s_right = new Spark(DriveConstants.kRight);

        m_driveBase = new DifferentialDrive(s_left, s_right);

        e_left = new Encoder(DriveConstants.kEncoderLeft[0], DriveConstants.kEncoderLeft[1],
                DriveConstants.kEncoderLeft[2]);
        e_left.setDistancePerPulse(DriveConstants.kDistancePerPulse);
        e_left.reset();

        e_right = new Encoder(DriveConstants.kEncoderRight[0], DriveConstants.kEncoderRight[1],
                DriveConstants.kEncoderRight[2]);
        e_right.setDistancePerPulse(DriveConstants.kDistancePerPulse);
        e_right.reset();

        ff_left = new SimpleMotorFeedforward(DriveConstants.kSL, DriveConstants.kVL, DriveConstants.kAL);
        ff_right = new SimpleMotorFeedforward(DriveConstants.kSR, DriveConstants.kVR, DriveConstants.kAR);

        m_odometry = new DifferentialDriveOdometry(new Rotation2d());
        m_kinematics = new DifferentialDriveKinematics(DriveConstants.kTrackWidth);

        setupNetworkTables();
    }

    //Configure all network table widgets here.
    private void setupNetworkTables() {
        ShuffleboardTab tab = Shuffleboard.getTab("Drive");

        tab.add(m_driveBase);

        tab.add("Left Encoder", e_left);
        tab.add("Right Encoder", e_right);

        tab.add("Heading", m_ahrs);

        b_leftInverted = tab.addPersistent("Invert Left", DriveConstants.bLeftInverted).withWidget(BuiltInWidgets.kToggleButton).getEntry();
        b_rightInverted = tab.addPersistent("Invert Right", DriveConstants.bRightInverted).withWidget(BuiltInWidgets.kToggleButton).getEntry();

        //Left Inverted Listener
        b_leftInverted.addListener(event -> {
            s_left.setInverted(b_leftInverted.getBoolean(DriveConstants.bLeftInverted));
        },EntryListenerFlags.kNew | EntryListenerFlags.kImmediate | EntryListenerFlags.kUpdate | EntryListenerFlags.kLocal);

        //Right Inverted Listener
        b_rightInverted.addListener(event -> {
            s_right.setInverted(b_rightInverted.getBoolean(DriveConstants.bRightInverted));
        },EntryListenerFlags.kNew | EntryListenerFlags.kImmediate | EntryListenerFlags.kUpdate | EntryListenerFlags.kLocal);
    }

    @Override
    public void periodic() {
        Rotation2d angle = Rotation2d.fromDegrees(-m_ahrs.getAngle() % 360);
        m_position  = m_odometry.update(angle, e_left.getDistance(), e_right.getDistance());
    }

    //Tank Drive with no feedforward.
    public void simpleTank(double left, double right){
        m_driveBase.tankDrive(left, right);
    }

    public void feedForwardTank(double speedLeft, double speedRight) {

        double calcLeft = ff_left.calculate(speedLeft, DriveConstants.kAcceleration);
        double calcRight = ff_right.calculate(speedRight, DriveConstants.kAcceleration);

        if(DriveConstants.bLeftInverted) {
            s_left.setVoltage(-calcLeft);
        } else {
            s_left.setVoltage(calcLeft);
        }
        if(DriveConstants.bRightInverted) {
            s_right.setVoltage(-calcRight);
        } else {
            s_right.setVoltage(calcRight);
        }

        m_driveBase.feed();
        
    }

    //Set Gyro to 0
    public void resetGyro() {
        m_ahrs.reset();
    }

    //Set encoders to 0
    public void resetEncoders() {
        e_left.reset();
        e_right.reset();
    }

    //Set Position to given position and reset everything else.
    public void resetOdometry(Pose2d currentPosition) {
        resetEncoders();
        resetGyro();

        m_odometry.resetPosition(currentPosition, m_ahrs.getRotation2d());
    }

    public void setRawVoltage(double leftVoltage, double rightVoltage){
        s_left.setVoltage(leftVoltage);
        s_right.setVoltage(rightVoltage);

        s_left.feed();
        s_right.feed();
        m_driveBase.feed();
    }

    public DifferentialDriveWheelSpeeds getRates() {
        return new DifferentialDriveWheelSpeeds(e_left.getRate(), e_right.getRate());
      }

    public double getHeading() {
        return m_ahrs.getAngle();
    }

    public Pose2d getPose() {
        return m_position;
    }

    public DifferentialDriveKinematics getKinematics(){
        return m_kinematics;
    }

}
