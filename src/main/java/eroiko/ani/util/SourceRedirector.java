package eroiko.ani.util;

import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.TreeMap;

import eroiko.ani.util.WallpaperClass.WallpaperProto;
import eroiko.ani.util.myDS.myQuartet;
import eroiko.ani.util.WallpaperClass.WallpaperImageWithFilter;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

public class SourceRedirector {
    public static final Path defaultDataPath = FileSystems.getDefault().getPath("data").toAbsolutePath();
    public static Path userSelectedPath = null;
    public static final Path defaultImagePath = FileSystems.getDefault().getPath("data/default").toAbsolutePath();
    public static Path defaultMediaPath = FileSystems.getDefault().getPath("src/main/java/eroiko/ani/music").toAbsolutePath();

    /* For preview */
    public static TreeMap<Integer, WallpaperProto> wallpapersForViewWindows = new TreeMap<>();
    private static int lastWallpaperNumber = 0;
    
    /** return a {@code Serial number} of a certain wallpaperImage instance */
    public static int addWallpaper(WallpaperProto wallpaperImage){
        wallpapersForViewWindows.put(++lastWallpaperNumber, wallpaperImage);
        return lastWallpaperNumber;
    }
    
    /** delete unused wallpaper image to release memory using {@code serialNumber} */
    public static void deleteWallpaper(int serialNumber){
        System.out.println("Delete wallpaperImage!");
        wallpapersForViewWindows.remove(serialNumber);
    }
    
    /* For filter! */
    public static ArrayList<WallpaperImageWithFilter> wallpaperQueue = new ArrayList<WallpaperImageWithFilter>();
    public static void popToQueue(int serialNumber){
        // var wp = wallpapersForViewWindows.get(serialNumber);
        // if (wp instanceof WallpaperImageWithFilter){
        //     ((WallpaperImageWithFilter) wp).printSelection();
        //     System.out.println("Add to queue!");
        //     wallpaperQueue.add((WallpaperImageWithFilter) wp);
        // }
    }
    public static void executeQueueFileOperation(){

    }
    // public static void popToQueue(WallpaperController wCtrl){
        // if (wp instanceof WallpaperImageWithFilter){
        //     ((WallpaperImageWithFilter) wp).printSelection();
        //     System.out.println("Add to queue!");
        //     wallpaperQueue.add((WallpaperImageWithFilter) wp);
        // }
    // }
    
    public static int getSerialNumberImmediately(){ return lastWallpaperNumber; }
    
    public static WallpaperProto getWallpaperImage(int serialNumber){ return wallpapersForViewWindows.get(serialNumber); }
    

    /* Properties 設定 */
    public static boolean preViewOrNot = true;
    public static boolean showWallpapersAfterCrawling = false;
    public static boolean useOldCrawlerForFullSpeedMode = false;
    public static int pagesToDownload = 4;
    public static boolean quit;
    public static BooleanProperty openPreviewFilter = new SimpleBooleanProperty(false);
    
    /* For selection */
    public static WallpaperImageWithFilter wallpaperImageWithFilter;

    /* For data record */
    public static ArrayList<ArrayList<myQuartet<Integer, Integer, String, String>>> data;

    
}
