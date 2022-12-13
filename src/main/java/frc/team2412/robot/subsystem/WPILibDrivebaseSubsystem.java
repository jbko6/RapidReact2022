package frc.team2412.robot.subsystem;

import com.ctre.phoenix.motorcontrol.TalonFXControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonFX;
import com.ctre.phoenix.sensors.CANCoder;

import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.math.kinematics.SwerveDriveKinematics;
import edu.wpi.first.math.kinematics.SwerveModuleState;
import edu.wpi.first.math.util.Units;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.team2412.robot.Hardware;

public class WPILibDrivebaseSubsystem extends SubsystemBase {

    private final double ticksPerRotation = 2048.0;
    private final double wheelDiameter = 3.0;
    private final double driveReduction = 1.0;
    private final double steerReduction = 1.0;

    // position units is one rotation / 2048
    // extrapolate this to meters using wheel perimeter (pi * wheel diameter)
    // raw sensor unit = perimeter / 2048

    // units: raw sensor units
    private final double steerPositionCoefficient = Math.PI * wheelDiameter * steerReduction / ticksPerRotation;
    private final double driveVelocityCoefficient = (Math.PI * wheelDiameter * driveReduction / ticksPerRotation) * 10.0;



    TalonFX[] moduleDriveMotors = {
        new TalonFX(Hardware.DRIVETRAIN_FRONT_LEFT_DRIVE_MOTOR),
        new TalonFX(Hardware.DRIVETRAIN_FRONT_RIGHT_DRIVE_MOTOR),
        new TalonFX(Hardware.DRIVETRAIN_BACK_LEFT_DRIVE_MOTOR),
        new TalonFX(Hardware.DRIVETRAIN_BACK_RIGHT_DRIVE_MOTOR)
    };

    TalonFX motor1 = null;

    TalonFX[] moduleAngleMotors = {
        new TalonFX(Hardware.DRIVETRAIN_FRONT_LEFT_ANGLE_MOTOR),
        new TalonFX(Hardware.DRIVETRAIN_FRONT_RIGHT_ANGLE_MOTOR),
        new TalonFX(Hardware.DRIVETRAIN_BACK_LEFT_ANGLE_MOTOR),
        new TalonFX(Hardware.DRIVETRAIN_BACK_RIGHT_ANGLE_MOTOR)
    };

    CANCoder[] moduleEncoders = {
        new CANCoder(Hardware.DRIVETRAIN_FRONT_LEFT_ENCODER_PORT),
        new CANCoder(Hardware.DRIVETRAIN_FRONT_RIGHT_ENCODER_PORT),
        new CANCoder(Hardware.DRIVETRAIN_BACK_LEFT_ENCODER_PORT),
        new CANCoder(Hardware.DRIVETRAIN_BACK_RIGHT_ENCODER_PORT)
    };

    double[] moduleOffsets = {
        Hardware.DRIVETRAIN_FRONT_LEFT_ENCODER_OFFSET,
        Hardware.DRIVETRAIN_FRONT_RIGHT_ENCODER_OFFSET,
        Hardware.DRIVETRAIN_BACK_LEFT_ENCODER_OFFSET,
        Hardware.DRIVETRAIN_BACK_RIGHT_ENCODER_OFFSET
    };

    // 2ft x 2ft for practice bot
    private final Translation2d[] moduleLocations = {
        new Translation2d(Units.feetToMeters(1), Units.feetToMeters(1)), // front left
        new Translation2d(Units.feetToMeters(1), Units.feetToMeters(-1)), // front right
        new Translation2d(Units.feetToMeters(-1), Units.feetToMeters(1)), // back left
        new Translation2d(Units.feetToMeters(-1), Units.feetToMeters(-1)) // back right
    };

    SwerveDriveKinematics kinematics = new SwerveDriveKinematics(
        moduleLocations[0], moduleLocations[1], moduleLocations[2], moduleLocations[3]
    );

    public WPILibDrivebaseSubsystem() {
        // configure encoders offsets
        moduleEncoders[0].configMagnetOffset(moduleOffsets[0]);
        moduleEncoders[1].configMagnetOffset(moduleOffsets[1]);
        moduleEncoders[2].configMagnetOffset(moduleOffsets[2]);
        moduleEncoders[3].configMagnetOffset(moduleOffsets[3]); 

        motor1 = new TalonFX(Hardware.DRIVETRAIN_FRONT_LEFT_DRIVE_MOTOR);
        System.out.println(motor1.getSupplyCurrent());
    }

    /**
     * Drives the robot using forward, strafe, and rotation. Units in meters
     * 
     * @param forward
     * @param strafe
     * @param rotation
     * @param fieldOriented
     */
    public void drive(double forward, double strafe, Rotation2d rotation, boolean fieldOriented) {
        SwerveModuleState[] moduleStates = getModuleStates(new ChassisSpeeds(0,0,0));
        if (fieldOriented) {
        
        } else {
            moduleStates = getModuleStates(new ChassisSpeeds(forward, strafe, rotation.getRadians()));
        }
        drive(moduleStates);
    } 

    /**
     * Drives the robot using states
     * 
     * @param states
     */
    public void drive(SwerveModuleState[] states) {        
        // Set motor speeds and angles
        for (int i=0; i < moduleDriveMotors.length; i++) {
            // meters/100ms * raw sensor units conversion
            moduleDriveMotors[i].set(TalonFXControlMode.Velocity, (states[i].speedMetersPerSecond / 10) * driveVelocityCoefficient);
        }
        for (int i=0; i < moduleAngleMotors.length; i++) {
            moduleAngleMotors[i].set(TalonFXControlMode.Position, states[i].angle.getRadians() * steerPositionCoefficient);
        }
    }

    /**
     * @param speeds
     * @return Array with modules with front left at [0], front right at [1], back left at [2], back right at [3]
     */
    private SwerveModuleState[] getModuleStates(ChassisSpeeds speeds) {
        return kinematics.toSwerveModuleStates(speeds);
    }
}