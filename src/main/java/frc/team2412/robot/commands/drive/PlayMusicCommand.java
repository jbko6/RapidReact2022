package frc.team2412.robot.commands.drive;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.team2412.robot.subsystem.DrivebaseSubsystem;
import frc.team2412.robot.util.MusicChooser;

public class PlayMusicCommand extends CommandBase {
    private final DrivebaseSubsystem drivebaseSubsystem;
    private final MusicChooser musicChooser;

    public PlayMusicCommand(DrivebaseSubsystem drivebaseSubsystem, MusicChooser musicChooser) {
        this.drivebaseSubsystem = drivebaseSubsystem;
        this.musicChooser = musicChooser;

        addRequirements(drivebaseSubsystem);
    }

    @Override
    public void initialize() {
        drivebaseSubsystem.playMusic(musicChooser.getChoosenMusicPath());
    }

    @Override
    public void end(boolean interrupted) {
        drivebaseSubsystem.stopMusic();
    }

    @Override
    public boolean isFinished() {
        return false;
    }
}
