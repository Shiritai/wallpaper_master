package eroiko.ani.util.NeoWallpaper;

import java.nio.file.FileSystems;
import java.nio.file.Path;

public class WallpaperPath {
    public static final Path defaultDataPath = FileSystems.getDefault().getPath("data");
    public static final Path defaultWallpaperPath = FileSystems.getDefault().getPath("data/wallpaper");
    public static final Path defaultImagePath = FileSystems.getDefault().getPath("data/default");
    public static final Path defaultMusicPath = FileSystems.getDefault().getPath("src/main/java/eroiko/ani/music");
    private static Path userWallpaperPath = null;
    private static boolean useConfigWallpaperPath = false;

    public static Path getWallpaperPath(){
        return (useConfigWallpaperPath) ? userWallpaperPath : defaultWallpaperPath;
    }

    public static void updateUserWallpaperPath(Path path){
        userWallpaperPath = path;
        useConfigWallpaperPath = true; 
        System.out.println("New default path : " + WallpaperPath.getWallpaperPath());
    }

    public static void resetToDefaultWallpaperPath(){ useConfigWallpaperPath = false; }

    public static Path pushUserPath(){
        if (useConfigWallpaperPath){
            return userWallpaperPath;
        }
        return null;
    }
    
    
}
