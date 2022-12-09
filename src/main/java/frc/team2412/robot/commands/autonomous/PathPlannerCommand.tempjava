package frc.team2412.robot.commands.autonomous;

import java.util.HashMap;

import com.pathplanner.lib.PathPlannerTrajectory;
import com.pathplanner.lib.commands.PPSwerveControllerCommand;

import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.PrintCommand;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import frc.team2412.robot.subsystem.DrivebaseSubsystem;

public class PathPlannerCommand extends SequentialCommandGroup{
    public PathPlannerCommand(DrivebaseSubsystem drivebaseSubsystem, PathPlannerTrajectory traj, boolean isFirstPath) {
        HashMap<String, Command> eventMap = new HashMap<>();
        eventMap.put("marker1", new PrintCommand("marker 1"));
        
        addCommands(
            new InstantCommand(() -> {
            // Reset odometry for the first path you run during auto
            if(isFirstPath){
                drivebaseSubsystem.resetPose(traj.getInitialHolonomicPose());
            }
            })
            // new PPSwerveControllerCommand(
            //     traj, 
            //     this::getPose, // Pose supplier
            //     drivebaseSubsystem.swerveKinematics, // SwerveDriveKinematics
            //     new PIDController(0, 0, 0), // X controller. Tune these values for your robot. Leaving them 0 will only use feedforwards.
            //     new PIDController(0, 0, 0), // Y controller (usually the same values as X controller)
            //     new PIDController(0, 0, 0), // Rotation controller. Tune these values for your robot. Leaving them 0 will only use feedforwards.
            //     this::setModuleStates, // Module states consumer
            //     eventMap, // This argument is optional if you don't use event markers
            //     this // Requires this drive subsystem
            // )
        );
    }
}
