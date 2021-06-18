/*
 * Author : Shiritai (楊子慶, or Eroiko on Github) at 2021/06/13.
 * See https://github.com/Shiritai/wallpaper_master for more information.
 * Created using VSCode.
 */
package eroiko.ani.model.CLI.command.external;

import java.io.IOException;

import eroiko.ani.controller.PrimaryControllers.WallpaperController;
import eroiko.ani.model.CLI.command.basic.Rm;
import eroiko.ani.model.CLI.command.fundamental.*;
import eroiko.ani.util.NeoWallpaper.WallpaperPath;

/** 此類允許直接與 Wallpaper Classes 交流, 屬 CLI 擴展類 */
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
                WallpaperController.OpenWallpaper(new eroiko.ani.util.NeoWallpaper.Wallpaper(), false);
            } catch (IOException e) {
                throw illegalParaStr("Unknown error");
            }
        }
        else if (fileInfo.equals("-p")){
            try {
                WallpaperController.OpenWallpaper(new eroiko.ani.util.NeoWallpaper.Wallpaper(), true);
            } catch (IOException e) {
                throw illegalParaStr("Unknown error");
            }
        }
        else if (fileInfo.equals("--clean")){
            Rm.delete(WallpaperPath.DEFAULT_TMP_WALLPAPER_PATH);
        }
        else {
            try {
                if (fileInfo.contains("--this")){ // 以當前資料夾為基礎開啟 wallpaper
                    var wp = new eroiko.ani.util.NeoWallpaper.Wallpaper(thisDir);
                    WallpaperController.OpenWallpaper(wp, fileInfo.contains("-p"));
                }
                else if (fileInfo.contains("--new")){
                    var wp = eroiko.ani.util.NeoWallpaper.Wallpaper.getWallpaper(
                        eroiko.ani.util.NeoWallpaper.Wallpaper.getWallpaperSerialNumberImmediately()
                    );
                    if (wp != null){
                        WallpaperController.OpenWallpaper(wp, fileInfo.contains("-p"));
                    }
                }
                else {
                    var wp = new eroiko.ani.util.NeoWallpaper.Wallpaper(thisDir.resolve(
                        fileInfo.substring(fileInfo.contains("-p") ? fileInfo.indexOf("-p ") + 3 : 0)
                    ));
                    WallpaperController.OpenWallpaper(wp, fileInfo.contains("-p"));
                }
            } catch (IllegalArgumentException ile) {
                throw illegalParaStr(ile.getMessage(), "Wallpaper not exist.");    
            } catch (IOException e){ e.printStackTrace(); }
        }
    }
}
