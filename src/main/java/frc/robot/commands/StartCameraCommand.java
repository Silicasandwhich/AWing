package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.subsystems.Camera.Camera;

public class StartCameraCommand extends CommandBase {
    
    public StartCameraCommand(Camera camera) {
        camera.startCapture();
        addRequirements(camera);
    }
}
