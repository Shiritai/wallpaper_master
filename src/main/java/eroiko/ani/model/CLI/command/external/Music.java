package eroiko.ani.model.CLI.command.external;

import eroiko.ani.controller.PrimaryControllers.MusicWithAkari;
import eroiko.ani.controller.PrimaryControllers.MusicWithSyamiko;
import eroiko.ani.model.CLI.command.fundamental.*;

public class Music extends Command {
    private final String musicToOpen;
    
    public Music(String musicToOpen){
        super(Type.MUSIC);
        this.musicToOpen = musicToOpen;
    }
    
    @Override
    public void execute() throws IllegalArgumentException {
        if (musicToOpen == null){
            MusicWithSyamiko.openMusicWithSyamiko();
        }
        else {
            var tmp = thisDir.resolve(musicToOpen);
            try {
                MusicWithAkari.openMusicWithAkari(tmp);
            } catch (IllegalArgumentException ile){
                throw new IllegalArgumentException(ile.getMessage() + "\n" + id.getName() + " : File not exist, bad file or is a directory.");
            }
        }
    }
}
