package eroiko.ani.util;

import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.TreeMap;

import eroiko.ani.util.WallpaperClass.WallpaperImage;
import eroiko.ani.util.WallpaperClass.WallpaperImageWithFilter;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

public class SourceRedirector {
    public static Path defaultDataPath = FileSystems.getDefault().getPath("data").toAbsolutePath();


    /* For preview */
    public static TreeMap<Integer, WallpaperImage> wallpapersForViewWindows = new TreeMap<>();
    private static int lastWallpaperNumber = 0;
    
    /** return a {@code Serial number} of a certain wallpaperImage instance */
    public static int addWallpaper(WallpaperImage wallpaperImage){
        wallpapersForViewWindows.put(++lastWallpaperNumber, wallpaperImage);
        return lastWallpaperNumber;
    }
    
    public static int getSerialNumberImmediately(){ return lastWallpaperNumber; }
    
    public static WallpaperImage getWallpaperImage(int serialNumber){ return wallpapersForViewWindows.get(serialNumber); }
    
    /** delete unused wallpaper image to release memory using {@code serialNumber} */
    public static void deleteWallpaper(int serialNumber){
        wallpapersForViewWindows.remove(serialNumber);
    }
    

    /* Properties 設定 */
    public static boolean preViewOrNot = true;
    public static boolean showWallpapersAfterCrawling = false;
    public static boolean useOldCrawlerForFullSpeedMode = false;
    public static int pagesToDownload = 4;
    public static boolean quit;
    public static BooleanProperty openPreviewFilter = new SimpleBooleanProperty(false);
    
    /* For selection */
    public static WallpaperImageWithFilter wallpaperImageWithFilter;
    

    public static String capitalize(String str){
        var ret = new StringBuilder();
        var strArr = str.toCharArray();
        // boolean flag = true;
        for (int i = 0; i < str.length(); ++i){
            if (i == 0){
                ret.append(Character.toUpperCase(strArr[i]));
            }
            else if (strArr[i - 1] == ' '){
                ret.append(Character.toUpperCase(strArr[i]));
            }
            else {
                ret.append(Character.toLowerCase(strArr[i]));
            }
        }
        return ret.toString();
    } 
    
}
