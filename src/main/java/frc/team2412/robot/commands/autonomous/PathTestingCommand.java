package frc.team2412.robot.commands.autonomous;

import org.frcteam2910.common.control.SimplePathBuilder;
import org.frcteam2910.common.control.Trajectory;
import org.frcteam2910.common.math.Rotation2;
import org.frcteam2910.common.math.Vector2;

import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import frc.team2412.robot.subsystem.DrivebaseSubsystem;

public class PathTestingCommand extends SequentialCommandGroup {
    public PathTestingCommand(DrivebaseSubsystem drivebaseSubsystem) {
        Trajectory robotPath = new Trajectory(
                new SimplePathBuilder(Vector2.ZERO, Rotation2.ZERO)
                        .lineTo(new Vector2(20, 20), Rotation2.fromDegrees(180))
                        .arcTo(new Vector2(40, 0), new Vector2(30, 0), Rotation2.fromDegrees(0))
                        .build(),
                DrivebaseSubsystem.DriveConstants.TRAJECTORY_CONSTRAINTS, 0.1);

        addCommands(
                new Follow2910TrajectoryCommand(drivebaseSubsystem, robotPath));

    }
}
