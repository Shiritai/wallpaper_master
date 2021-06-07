package eroiko.ani.model.CLI.command.external;

import java.io.IOException;

import eroiko.ani.controller.MainController;
import eroiko.ani.model.CLI.command.fundamental.*;

public class Wallpaper extends Command {
    private final String fileInfo;
    
    public Wallpaper(String fileInfo){
        super(Type.WALLPAPER);
        this.fileInfo = fileInfo;
    }
    
    @Override
    public void execute() throws IllegalArgumentException {
        if (fileInfo == null){
            try {
                MainController.OpenWallpaper(new eroiko.ani.util.NeoWallpaper.Wallpaper());
            } catch (IOException e) {
                throw new IllegalArgumentException(id.getName() + " : Unknown error");
            }
        }
        else {
            try {
                if (fileInfo.equals("--this")){ // 以當前資料夾為基礎開啟 wallpaper
                    var wp = new eroiko.ani.util.NeoWallpaper.Wallpaper(thisDir);
                    MainController.OpenWallpaper(wp);
                }
                else if (fileInfo.equals("--new")){
                    var wp = eroiko.ani.util.NeoWallpaper.Wallpaper.getWallpaper(
                        eroiko.ani.util.NeoWallpaper.Wallpaper.getWallpaperSerialNumberImmediately()
                    );
                    if (wp != null){
                        MainController.OpenWallpaper(wp);
                    }
                }
                else {
                    var wp = new eroiko.ani.util.NeoWallpaper.Wallpaper(thisDir.resolve(fileInfo));
                    MainController.OpenWallpaper(wp);
                }
            } catch (IllegalArgumentException ile) {
                throw new IllegalArgumentException(ile.getMessage() + "\n" + id.getName() + " : Wallpaper not exist.");    
            } catch (IOException e){ e.printStackTrace(); }
        }
    }
}
