package frc.team2412.robot.subsystem;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.InvertType;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.SupplyCurrentLimitConfiguration;
import com.ctre.phoenix.motorcontrol.TalonFXFeedbackDevice;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonFX;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class ShooterSubsystem extends SubsystemBase {
    // instance variables
    private final WPI_TalonFX flywheelMotor1;
    private final WPI_TalonFX flywheelMotor2;
    private final WPI_TalonFX turretMotor;
    private final WPI_TalonFX hoodMotor;

    // Constants
    public static final double FLYWHEEL_VELOCITY = 10;
    public static final double STOP_MOTOR = 0;
    public static final double DEGREES_TO_ENCODER_TICKS = 2048 / 360; // 2048 ticks per 360 degrees
    public static final double MIN_TURRET_ANGLE = -180; // Total ~360 degrees of rotation, assumes 0 is center
    public static final double MAX_TURRET_ANGLE = 180;
    public static final double TURRET_MAX_SPEED = 0.1;
    public static final double TURRET_MIN_SPEED = -0.1;
    public static final double TURRET_P = 0.01; // Placeholder PID constants
    public static final double TURRET_I = 0;
    public static final double TURRET_D = 0;
    public static final double MAX_HOOD_ANGLE = 40.0;
    public static final double MIN_HOOD_ANGLE = 5;
    public static final SupplyCurrentLimitConfiguration flywheelCurrentLimit = new SupplyCurrentLimitConfiguration(true,
            40, 40, 500);
    public static final SupplyCurrentLimitConfiguration turretCurrentLimit = new SupplyCurrentLimitConfiguration(true,
            10, 10, 500);
    public static final SupplyCurrentLimitConfiguration hoodCurrentLimit = turretCurrentLimit;

    /**
     * Constructor for shooter subsystem.
     * 
     * @param flywheelMotor1
     *            The first motor connected to the flywheel
     * 
     * @param flywheelMotor2
     *            The second motor connected to the flywheel
     * 
     * @param turretMotor
     *            The motor that controls the horizontal rotation of the
     *            turret
     * 
     * @param hoodMotor
     *            The motor that controls the angle of the hood above the
     *            turret
     * 
     */
    public ShooterSubsystem(WPI_TalonFX flywheelMotor1, WPI_TalonFX flywheelMotor2, WPI_TalonFX turretMotor,
            WPI_TalonFX hoodMotor) {
        // Motor configs
        flywheelMotor1.configSupplyCurrentLimit(flywheelCurrentLimit);
        flywheelMotor1.setNeutralMode(NeutralMode.Coast);
        flywheelMotor2.configSupplyCurrentLimit(flywheelCurrentLimit);
        flywheelMotor2.setNeutralMode(NeutralMode.Coast);
        flywheelMotor1.setInverted(false);
        flywheelMotor2.follow(flywheelMotor1);
        flywheelMotor2.setInverted(InvertType.OpposeMaster);

        turretMotor.configForwardSoftLimitThreshold(MAX_TURRET_ANGLE * DEGREES_TO_ENCODER_TICKS);
        turretMotor.configReverseSoftLimitThreshold(MIN_TURRET_ANGLE * DEGREES_TO_ENCODER_TICKS);
        turretMotor.configForwardSoftLimitEnable(true);
        turretMotor.configReverseSoftLimitEnable(true);
        turretMotor.configSupplyCurrentLimit(turretCurrentLimit);
        turretMotor.setNeutralMode(NeutralMode.Brake);
        turretMotor.configSelectedFeedbackSensor(TalonFXFeedbackDevice.IntegratedSensor, 0, 0);
        turretMotor.config_kP(0, TURRET_P);
        turretMotor.config_kI(0, TURRET_I);
        turretMotor.config_kD(0, TURRET_D);

        hoodMotor.configForwardSoftLimitThreshold(MAX_HOOD_ANGLE * DEGREES_TO_ENCODER_TICKS);
        hoodMotor.configReverseSoftLimitThreshold(0); // Current hood setup plan starts hood at 0, below MIN_HOOD_ANGLE
        hoodMotor.configForwardSoftLimitEnable(true);
        hoodMotor.configReverseSoftLimitEnable(true);
        hoodMotor.configSupplyCurrentLimit(hoodCurrentLimit);
        hoodMotor.setNeutralMode(NeutralMode.Brake);

        this.flywheelMotor1 = flywheelMotor1;
        this.flywheelMotor2 = flywheelMotor2;
        this.turretMotor = turretMotor;
        this.hoodMotor = hoodMotor;
    }

    /**
     * Sets the target angle for the hood motor
     * 
     * @param degrees
     *            Target angle for the hood motor in degrees
     */
    public void hoodMotorSetAngle(double degrees) {
        degrees = Math.min(Math.max(degrees, MIN_HOOD_ANGLE), MAX_HOOD_ANGLE);
        hoodMotor.set(ControlMode.Position, DEGREES_TO_ENCODER_TICKS * degrees);
    }

    /**
     * Stops the hood motor
     */
    // TODO make hardstop
    public void hoodMotorStop() {
        hoodMotor.set(STOP_MOTOR);
    }

    /**
     * Starts both flywheel motors
     */
    public void startFlywheel() {
        flywheelMotor1.set(ControlMode.Velocity, FLYWHEEL_VELOCITY);
        flywheelMotor2.set(ControlMode.Velocity, FLYWHEEL_VELOCITY);
    }

    /**
     * Stops both flywheel motors
     */
    public void stopFlywheel() {
        flywheelMotor1.set(STOP_MOTOR);
        flywheelMotor2.set(STOP_MOTOR);
    }

    /**
     * Resets the turret motor's integrated encoder to 0.
     */
    // TODO use limit switches to reset the encoder
    public void resetTurretEncoder() {
        turretMotor.setSelectedSensorPosition(0);
    }

    /**
     * Gets angle of the turret motor (horizontal swivel)
     * 
     * @return Angle, in degrees
     */
    public double getTurretAngle() {
        return turretMotor.getSelectedSensorPosition() / DEGREES_TO_ENCODER_TICKS;
    }

    /**
     * Sets the turret's angle to the given angle (Does not check angle limits).
     * 
     * @param angle
     *            the angle (in degrees) to set the turret to (negative for
     *            counterclockwise)
     */
    public void setTurretAngle(double angle) {
        turretMotor.set(ControlMode.Position, DEGREES_TO_ENCODER_TICKS * angle);
    }

    /**
     * Sets the turret angle realative to the current angle - not the last target
     * angle, but the current position of the motor
     * 
     * @param deltaAngle
     *            Amount to change the turret angle by in degrees
     */
    public void updateTurretAngle(double deltaAngle) {
        double currentAngle = turretMotor.getSelectedSensorPosition() / DEGREES_TO_ENCODER_TICKS;
        setTurretAngle(currentAngle + deltaAngle);
    }
}
