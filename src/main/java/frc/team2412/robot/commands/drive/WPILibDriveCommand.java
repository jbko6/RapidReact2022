package frc.team2412.robot.commands.drive;

import org.frcteam2910.common.math.Vector2;
import org.frcteam2910.common.robot.input.Axis;

import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.team2412.robot.subsystem.DrivebaseSubsystem;
import frc.team2412.robot.subsystem.WPILibDrivebaseSubsystem;

public class WPILibDriveCommand extends CommandBase {
    private final WPILibDrivebaseSubsystem drivebaseSubsystem;
    private final Axis forward;
    private final Axis strafe;
    private final Axis rotation;

    public WPILibDriveCommand(WPILibDrivebaseSubsystem drivebaseSubsystem, Axis forward, Axis strafe, Axis rotation) {
        this.drivebaseSubsystem = drivebaseSubsystem;
        this.forward = forward;
        this.strafe = strafe;
        this.rotation = rotation;

        addRequirements(drivebaseSubsystem);
    }

    @Override
    public void execute() {
        double x = deadbandCorrection(forward.get(false));
        double y = deadbandCorrection(-strafe.get(false));
        double rot = deadbandCorrection(-rotation.get(false)) / 2;
        drivebaseSubsystem.drive(x, y, Rotation2d.fromDegrees(rot), false);
    }

    public double deadbandCorrection(double input) {
        return Math.abs(input) < 0.05 ? 0 : (input - Math.signum(input) * 0.05) / 0.95;
    }
}
