package frc.team2412.robot.commands.shooter;

import static frc.team2412.robot.subsystem.ShooterSubsystem.ShooterConstants;

import java.util.function.BooleanSupplier;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.team2412.robot.subsystem.ShooterSubsystem;
import frc.team2412.robot.subsystem.TargetLocalizer;
import frc.team2412.robot.util.ShooterDataDistancePoint;

public class ShooterTargetCommand extends CommandBase {
    private final ShooterSubsystem shooter;
    private final TargetLocalizer localizer;
    private final BooleanSupplier turretEnable;

    private double turretAngle = 0;

    public ShooterTargetCommand(ShooterSubsystem shooter, TargetLocalizer localizer) {
        this(shooter, localizer, () -> false);
    }

    public ShooterTargetCommand(ShooterSubsystem shooter, TargetLocalizer localizer, BooleanSupplier turretButton) {
        this.shooter = shooter;
        this.localizer = localizer;
        turretEnable = turretButton;
        addRequirements(shooter);
    }

    @Override
    public void initialize() {
        localizer.limelightOn();
    }

    public enum TurretState {
        WRAP_LEFT, WRAP_RIGHT, STOPPED, TRACKING;
    }

    TurretState state;

    @Override
    public void execute() {
        // if (!localizer.hasTarget())
        // return;

        if (ShooterConstants.dataPoints != null && localizer.getAdjustedDistance() < 280) {
            ShooterDataDistancePoint shooterData = ShooterConstants.dataPoints
                    .getInterpolated(localizer.getAdjustedDistance());

            System.out.println("Limelight distance: " + localizer.getDistance());
            System.out.println("Localizer distance" + localizer.getAdjustedDistance());

            System.out.println(shooterData);

            shooter.setHoodAngle(shooterData.getAngle());
            shooter.setFlywheelRPM(shooterData.getRPM());

            System.out.println("Actual shooter RPM : " + shooter.getFlywheelRPM());
            System.out.println("Actual hood angle: " + shooter.getHoodAngle());
        }

        if (!turretEnable.getAsBoolean())
            state = TurretState.STOPPED;
        else if (turretAngle < ShooterConstants.LEFT_WRAP_THRESHOLD)
            state = TurretState.WRAP_LEFT;
        else if (turretAngle > ShooterConstants.RIGHT_WRAP_THRESHOLD)
            state = TurretState.WRAP_RIGHT;
        else if (turretAngle > ShooterConstants.LEFT_WRAP && turretAngle < ShooterConstants.RIGHT_WRAP)
            state = TurretState.TRACKING;

        switch (state) {
            case STOPPED:
                turretAngle = 0;
                break;
            case WRAP_LEFT:
                turretAngle = ShooterConstants.RIGHT_WRAP;
                // call the isTurretAt Angle method instead of this logic, also how is this if check being called?
                if (Math.abs(shooter.getTurretAngle() - ShooterConstants.RIGHT_WRAP) < 5)
                    state = TurretState.TRACKING;
                break;
            case WRAP_RIGHT:
                turretAngle = ShooterConstants.LEFT_WRAP;
                if (Math.abs(shooter.getTurretAngle() - ShooterConstants.LEFT_WRAP) < 5)
                    state = TurretState.TRACKING;
                break;
            case TRACKING:
                turretAngle = shooter.getTurretAngle() + localizer.getTargetYaw();
                break;
        }

        double localizerTurretAdjustment = state == TurretState.TRACKING ? localizer.yawAdjustment() : 0;
        System.out.println("Localizer turret adjustment: " + localizerTurretAdjustment);

        turretAngle = turretAngle + localizerTurretAdjustment;
        System.out.println("turret angle : " + turretAngle);

        shooter.setTurretAngle(turretAngle);

        System.out.println("Actual turret angle : " + shooter.getTurretAngle());
    }

    @Override
    public void end(boolean interrupted) {
        localizer.limelightOff();
    }
}
