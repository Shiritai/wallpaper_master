package eroiko.ani.model.CLI.command.external;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.Files;

import eroiko.ani.controller.MainController;
import eroiko.ani.model.CLI.command.fundamental.*;
import eroiko.ani.util.Method.Dumper;

public class Wallpaper extends Command {
    private final String [] fileInfo;
    
    public Wallpaper(String... fileInfo){
        super(Type.WALLPAPER);
        this.fileInfo = fileInfo;
    }
    
    @Override
    public void execute() throws IllegalArgumentException, AccessDeniedException {
        if (fileInfo.length == 0){
            try {
                MainController.OpenWallpaper(new eroiko.ani.util.NeoWallpaper.Wallpaper());
            } catch (IOException e) {
                throw new IllegalArgumentException(id.getName() + " : Unknown error");
            }
        }
        else if (fileInfo.length == 1){
            var tmp = thisDir.resolve(fileInfo[0]);
            try {
                if (tmp.toFile().exists() && Dumper.isImage(tmp)){
                    MainController.OpenWallpaper(new eroiko.ani.util.NeoWallpaper.Wallpaper(tmp.getParent(), tmp));
                }
                else if (Files.isDirectory(tmp)){
                    MainController.OpenWallpaper(new eroiko.ani.util.NeoWallpaper.Wallpaper(tmp));
                }
                else {
                    throw new IllegalArgumentException(id.getName() + " : Wallpaper not exist.");
                }
            } catch (IOException e) { e.printStackTrace(); }
        }
        else {
            throw new IllegalArgumentException(id.getName() +
                " : Wrong Parameter, except:\n\n\"wallpaper <NO_PARAMETER>\" to open default wallpapers  or\n" + 
                "\"wallpaper <DIRECTORY>\" to open wallpapers with DIRECTORY  or\n" + 
                "\"wallpaper <IMAGE_PATH>\" to open wallpapers with IMAGE_PATH\n"
            );
        }
    }

    @Override
    public void exeAfterRequest(String cmd) {
        
        
    }

}
