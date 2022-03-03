package frc.team2412.robot.util;

import edu.wpi.first.networktables.EntryListenerFlags;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInWidgets;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.team2412.robot.subsystem.DrivebaseSubsystem;

public class MusicChooser extends CommandBase {
    public enum AudioFiles {
        gameCubeBootUp("Game Cube Boot Up", "gameCubeBootUp.midi"), amongUsDrip("Among Us Drip", "amongUsDrip.midi");

        String name;
        String path;

        AudioFiles(String name, String path) {
            this.name = name;
            this.path = "AudioFiles/" + path;
        }
    }

    private final SendableChooser<AudioFiles> chooser = new SendableChooser<>();

    public MusicChooser(DrivebaseSubsystem drivebaseSubsystem) {
        for (var audio : AudioFiles.values()) {
            chooser.addOption(audio.name, audio);
        }

        ShuffleboardTab audioTab = Shuffleboard.getTab("Music");
        audioTab.add("Choose music", chooser)
            .withSize(2, 1);

        audioTab.add("Play/Stop Music", false)
            .withWidget(BuiltInWidgets.kToggleButton)
            .withPosition(0, 1)
            .getEntry()
            .addListener(event -> {
                if (event.getEntry().getBoolean(false)) {
                    drivebaseSubsystem.playMusic(getChoosenMusicPath());
                    System.out.println("lol");
                } else {
                    drivebaseSubsystem.stopMusic();
                }
            }, EntryListenerFlags.kUpdate);     

    }

    public String getChoosenMusicPath() {
        return chooser.getSelected().path;
    }
}
