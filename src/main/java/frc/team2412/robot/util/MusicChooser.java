package frc.team2412.robot.util;

import edu.wpi.first.networktables.EntryListenerFlags;
import edu.wpi.first.wpilibj.Filesystem;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInWidgets;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.team2412.robot.commands.drive.PlayMusicCommand;
import frc.team2412.robot.subsystem.DrivebaseSubsystem;

public class MusicChooser extends CommandBase {
    public enum AudioFiles {
        gameCubeBootUp("Game Cube Boot Up", "gameCubeBootUp.chrp"), amongUsDrip("Among Us Drip", "amongUsDrip.chrp");

        final String name;
        final String path;

        AudioFiles(String name, String path) {
            this.name = name;
            this.path = Filesystem.getDeployDirectory() + "/AudioFiles/" + path;
        }
    }

    private final SendableChooser<AudioFiles> chooser = new SendableChooser<>();
    private final PlayMusicCommand playMusicCommand;

    public MusicChooser(DrivebaseSubsystem drivebaseSubsystem) {
        for (var audio : AudioFiles.values()) {
            chooser.addOption(audio.name, audio);
        }

        playMusicCommand = new PlayMusicCommand(drivebaseSubsystem, this);

        ShuffleboardTab audioTab = Shuffleboard.getTab("Music");
        audioTab.add("Choose music", chooser)
                .withSize(2, 1);

        audioTab.add("Play or Stop Music", false)
                .withWidget(BuiltInWidgets.kToggleButton)
                .withPosition(0, 1)
                .withSize(2, 1)
                .getEntry()
                .addListener(event -> {
                    if (event.getEntry().getBoolean(false)) {
                        playMusicCommand.schedule();
                        System.out.println("lol");
                    } else {
                        playMusicCommand.cancel();
                    }
                }, EntryListenerFlags.kUpdate);

    }

    public String getChoosenMusicPath() {
        return chooser.getSelected().path;
    }
}
