package eroiko.ani.util;

import java.util.TreeMap;

import eroiko.ani.controller.ControllerSupporter.WallpaperImage;

public class SourceRedirector {
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
    
    
    /* For selection */
    public static TreeMap<Integer, myPair<String, String>> aboutToSelectImageLinks = new TreeMap<>();
}
