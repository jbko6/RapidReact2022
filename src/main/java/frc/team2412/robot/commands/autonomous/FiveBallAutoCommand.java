package frc.team2412.robot.commands.autonomous;

import org.frcteam2910.common.control.SplinePathBuilder;
import org.frcteam2910.common.control.Trajectory;
import org.frcteam2910.common.math.Rotation2;
import org.frcteam2910.common.math.Vector2;

import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import frc.team2412.robot.subsystem.DrivebaseSubsystem;

public class FiveBallAutoCommand extends SequentialCommandGroup {
    DrivebaseSubsystem drivebaseSubsystem;

    public FiveBallAutoCommand(DrivebaseSubsystem drivebaseSubsystem) {
        this.drivebaseSubsystem = drivebaseSubsystem;
        Trajectory trajectory = new Trajectory(
            new SplinePathBuilder(new Vector2(345, 70), Rotation2.ZERO, Rotation2.ZERO)
            .bezier(new Vector2(297, 26), new Vector2(245, 17), new Vector2(297, 26), Rotation2.ZERO)
            .bezier(new Vector2(198, 73), new Vector2(146, 87), new Vector2(198, 73), Rotation2.ZERO)
            .bezier(new Vector2(62, 51), new Vector2(48, 87), new Vector2(62, 51), Rotation2.ZERO)
            .bezier(new Vector2(200, 104), new Vector2(200, 104), new Vector2(200, 104), Rotation2.ZERO)
            .build(), 
            DrivebaseSubsystem.DriveConstants.TRAJECTORY_CONSTRAINTS, 0.1);

        addCommands(new Follow2910TrajectoryCommand(drivebaseSubsystem, trajectory));
    }
}