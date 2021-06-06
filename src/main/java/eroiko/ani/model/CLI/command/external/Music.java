package eroiko.ani.model.CLI.command.external;

import java.nio.file.AccessDeniedException;

import eroiko.ani.controller.PrimaryControllers.MusicWithAkari;
import eroiko.ani.controller.PrimaryControllers.MusicWithSyamiko;
import eroiko.ani.model.CLI.command.fundamental.*;
import eroiko.ani.util.Method.Dumper;

public class Music extends Command {
    private final String [] musicToOpen;
    
    public Music(String... musicToOpen){
        super(Type.MUSIC);
        this.musicToOpen = musicToOpen;
    }
    
    @Override
    public void execute() throws IllegalArgumentException, AccessDeniedException {
        if (musicToOpen.length == 0){
            MusicWithSyamiko.openMusicWithSyamiko();
        }
        else if (musicToOpen.length == 1){
            var tmp = thisDir.resolve(musicToOpen[0]);
            if (tmp.toFile().exists() && Dumper.isMusic(tmp)){
                MusicWithAkari.openMusicWithAkari(tmp);
            }
            else {
                throw new IllegalArgumentException(id.getName() + " : File not exist, bad file or is a directory.");
            }
        }
        else {
            throw new IllegalArgumentException(id.getName() +
                " : Wrong Parameter, except:\n\n\"music <NO_PARAMETER>\" to open Music with Syamiko  or\n"+
                "\"music <MUSIC_PATH>\" to open Music with Akari w.r.t. MUSIC_PATH\n"
            );
        }
    }

    @Override
    public void exeAfterRequest(String cmd) {
        
        
    }
    
}
