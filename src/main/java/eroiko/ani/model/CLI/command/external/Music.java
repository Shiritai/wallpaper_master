/*
 * Author : Shiritai (楊子慶, or Eroiko on Github) at 2021/06/13.
 * See https://github.com/Shiritai/wallpaper_master for more information.
 * Created using VSCode.
 */
package eroiko.ani.model.CLI.command.external;

import java.io.IOException;
import java.nio.file.Files;
import java.util.NoSuchElementException;

import eroiko.ani.controller.PrimaryControllers.MusicWithAkari;
import eroiko.ani.controller.PrimaryControllers.MusicWithSyamiko;
import eroiko.ani.model.CLI.command.fundamental.*;
// import eroiko.ani.model.CLI.command.basic.Rm;
// import eroiko.ani.util.NeoWallpaper.WallpaperPath;

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
        // else if (musicToOpen.equals("--clean")){  // 之後實作
        //     Rm.delete(WallpaperPath.DEFAULT_MUSIC_PATH);
        //     WallpaperPath.DEFAULT_MUSIC_PATH.toFile().mkdir();
        // }
        else {
            var tmp = thisDir.resolve(musicToOpen);
            try {
                if (!Files.isDirectory(tmp)){
                    MusicWithAkari.openMusicWithAkari(tmp);
                }
                else {
                    try (var dirStream = Files.newDirectoryStream(tmp, "*.{mp3,wav}")){
                        MusicWithAkari.openMusicWithAkari(dirStream.iterator().next());
                    }
                }
            } catch (IllegalArgumentException | NoSuchElementException ile){
                throw illegalParaStr(ile.getMessage(), "File not exist.");
            } catch (IOException ie){
                throw illegalParaStr(ie.getMessage(), "Directory not exist.");
            }
        }
    }
}
