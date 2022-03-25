package frc.team2412.robot.subsystem;

import org.frcteam2910.common.math.RigidTransform2;
import org.frcteam2910.common.math.Rotation2;
import org.frcteam2910.common.math.Vector2;

import edu.wpi.first.math.filter.LinearFilter;
import frc.team2412.robot.Robot;

import static frc.team2412.robot.subsystem.TargetLocalizer.LocalizerConstants.*;

public class TargetLocalizer {
    public static class LocalizerConstants {
        // TODO tune these more
        public static final double TURRET_LATERAL_FF = 0, TURRET_ANGULAR_FF = 4, TURRET_DEPTH_FF = 0;
    }

    private final DrivebaseSubsystem drivebaseSubsystem;
    private final ShooterSubsystem shooterSubsystem;
    private final ShooterVisionSubsystem shooterVisionSubsystem;
    private final LinearFilter distanceFilter;

    public TargetLocalizer(DrivebaseSubsystem drivebase, ShooterSubsystem shooter, ShooterVisionSubsystem vision) {
        drivebaseSubsystem = drivebase;
        shooterSubsystem = shooter;
        shooterVisionSubsystem = vision;
        distanceFilter = LinearFilter.movingAverage(10);

    }

    public double getDistance() {
        return distanceFilter.calculate(
                hasTarget() ? shooterVisionSubsystem.getDistance() + shooterSubsystem.getDistanceBias() : 120);
    }

    public double getAdjustedDistance() {
        return getDistance() + distanceAdjustment();
    }

    /**
     * very basic feedforward math to adjust the depth depending on the distance you are moving away
     * from target
     *
     * @return adjustment
     */
    public double distanceAdjustment() {
        return (getDepthVelocity() * getDistance() * TURRET_DEPTH_FF) / getVoltage();
    }

    public double getPitch() {
        return shooterVisionSubsystem.getAdjustedPitch();
    }

    public boolean hasTarget() {
        return shooterVisionSubsystem.hasTarget();
    }

    /**
     * Returns the yaw (horizontal angle) to the hub.
     *
     * @return The yaw to the hub (0 is straight ahead, positive is clockwise, units are degrees).
     */
    public double getVisionYaw() {
        return shooterVisionSubsystem.getYaw();
    }

    /**
     * Returns the yaw (horizontal angle) to the target position.
     *
     * @return The yaw to the hub plus turret angle bias (0 is straight ahead, positive is clockwise,
     *         units are degrees).
     */
    public double getTargetYaw() {
        // return 0;
        return getVisionYaw() + shooterSubsystem.getTurretAngleBias();
    }

    /**
     * Return the robot's angle relative to the field.
     *
     * @return The robot angle (0 is straight forward from the driver station, positive rotation is
     *         clockwise).
     */
    public Rotation2 getFieldCentricRobotAngle() {
        return drivebaseSubsystem.getGyroscopeUnadjustedAngle().rotateBy(gyroAdjustmentAngle);
    }

    /**
     * Return the turret's angle.
     *
     * @return The turret angle (0 is intake side, positive is clockwise).
     */
    public Rotation2 getTurretAngle() {
        return Rotation2.fromDegrees(shooterSubsystem.getTurretAngle() + STARTING_TURRET_ANGLE);
    }

    /**
     * unit vector component of chassis velocity perpendicular to the turret output
     *
     * @return that
     */
    public double getLateralVelocity() {
        return (drivebaseSubsystem != null)
                // might need to do inverse
                ? drivebaseSubsystem.getVelocity().rotateBy(getTurretAngle()).x
                : 0;
    }

    /**
     * unit vector component of chassis velocity parallel to the turret output
     *
     * @return that
     */
    public double getDepthVelocity() {
        return (drivebaseSubsystem != null)
                // might need to do inverse
                ? drivebaseSubsystem.getVelocity().rotateBy(getTurretAngle()).y
                : 0;
    }

    public double getAngularVelocity() {
        return (drivebaseSubsystem != null) ? drivebaseSubsystem.getAngularVelocity() : 0;
    }

    /**
     * feedforward math for turret angle feedforward
     * multiply the lateral velocity by distance.
     * This is to compensate for a longer time of flight the farther away you are
     * and it is not perfect but it should work.
     * angular velocity is to help the turret keep heading when the robot itself is turning
     *
     * @return adjustment
     */
    public double yawAdjustment() {
        return (getDistance() != 0 && getDistance() > getLateralVelocity()
                ? Math.toDegrees(Math.asin(getLateralVelocity() / getDistance() * TURRET_LATERAL_FF))
                : 0) + (getAngularVelocity() * TURRET_ANGULAR_FF)
                        / getVoltage();
    }

    /**
     * Returns the estimated limelight pose according to vision and the gyroscope.
     *
     * The translation (inches) is relative to the hub, and the rotation is relative to straight forward
     * from the driver station (Positive rotation is clockwise). If the limelight doesn't have a target,
     * returns {@link RigidTransform2#ZERO}.
     *
     * For example, returning {@code RigidTransform2(Vector2(12, -24), Rotation2.fromDegrees(20))} means
     * that, looking out from the driver station, the limelight is one foot to the right of and two feet
     * in front of the center of the hub and is pointing 20 degrees to the right (clockwise).
     *
     * @return The estimated limelight pose according to vision and the gyroscope.
     */
    public RigidTransform2 getVisionGyroLimelightPose() {
        if (!hasTarget()) {
            return RigidTransform2.ZERO;
        }
        Rotation2 fieldCentricLimelightAngle = getFieldCentricRobotAngle().rotateBy(getTurretAngle());
        Rotation2 fieldCentricLimelightToHubAngle = fieldCentricLimelightAngle
                .rotateBy(Rotation2.fromDegrees(getVisionYaw()));
        Rotation2 fieldCentricHubToLimelightAngle = fieldCentricLimelightToHubAngle
                .rotateBy(Rotation2.fromDegrees(180));
        Vector2 forwardXTranslation = Vector2.fromAngle(fieldCentricHubToLimelightAngle).scale(getDistance());
        Vector2 forwardYTranslation = forwardXTranslation.rotateBy(Rotation2.fromDegrees(90));
        return new RigidTransform2(forwardYTranslation, fieldCentricLimelightAngle);
    }

    /**
     * Returns the estimated robot pose according to vision and the gyroscope.
     *
     * The translation (inches) is relative to the hub, and the rotation is relative to straight forward
     * from the drive station (Positive rotation is clockwise). If the limelight doesn't have a target,
     * returns {@link RigidTransform2#ZERO}.
     *
     * For example, returning {@code RigidTransform2(Vector2(12, -24), Rotation.fromDegrees(20))} means
     * that, looking from the driver station, the center of the robot is one foot to the right of and
     * two feet in front of the center of the hub and is pointing 20 degrees to the right (clockwise).
     *
     * @return The estimated robot pose according to vision and the gyroscope.
     */
    public RigidTransform2 getVisionGyroRobotPose() {
        if (!hasTarget()) {
            return RigidTransform2.ZERO;
        }
        Vector2 fieldCentricHubToLimelight = getVisionGyroLimelightPose().translation;
        Vector2 robotCentricTurretToLimelight = Vector2.fromAngle(getTurretAngle())
                .scale(LIMELIGHT_TO_TURRET_CENTER_DISTANCE);
        Vector2 robotCentricLimelightPosition = ROBOT_CENTRIC_TURRET_CENTER.add(robotCentricTurretToLimelight);
        Vector2 fieldCentricRobotToLimelight = robotCentricLimelightPosition.rotateBy(getFieldCentricRobotAngle())
                .rotateBy(Rotation2.fromDegrees(90));
        Vector2 hubToRobot = fieldCentricHubToLimelight.subtract(fieldCentricRobotToLimelight);
        return new RigidTransform2(hubToRobot, getFieldCentricRobotAngle());
    }

    /**
     * Returns the estimated robot pose relative to the start according to vision and the gyroscope.
     *
     * The translation (inches) is from the starting position, and the rotation is relative to the
     * starting rotation (Positive is clockwise). If the limelight doesn't have a target, returns
     * {@link RigidTransform2#ZERO}.
     *
     * For example, returning {@code RigidTransform2(Vector2(12, -24), Rotation2.fromDegrees(20))} means
     * that, looking from the driver station, the robot moved one foot to the right and two feet closer,
     * and rotated 20 degrees clockwise.
     *
     * @return The estimated robot pose relative to the start according to vision and the gyroscope.
     */
    public RigidTransform2 getVisionGyroRobotPoseRelativeToStart() {
        return hasTarget() ? getVisionGyroRobotPose().transformBy(startingPose.inverse()) : RigidTransform2.ZERO;
    }

    public void limelightOn() {
        shooterVisionSubsystem.setLedOn();
    }

    public void limelightOff() {
        shooterVisionSubsystem.setLedOff();
    }

    public double getVoltage() {
        return Robot.getInstance().getVoltage();
    }
}
