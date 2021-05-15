package eroiko.ani.util.NeoWallpaper;

import java.nio.file.FileSystems;
import java.nio.file.Path;

public class WallpaperPath {
    public static final Path defaultDataPath = FileSystems.getDefault().getPath("data").toAbsolutePath();
    public static final Path defaultWallpaperPath = FileSystems.getDefault().getPath("data/wallpaper").toAbsolutePath();
    public static final Path defaultImagePath = FileSystems.getDefault().getPath("src/main/java/eroiko/ani/img/default").toAbsolutePath();
    public static final Path defaultMusicPath = FileSystems.getDefault().getPath("src/main/java/eroiko/ani/music").toAbsolutePath();
    private static Path userWallpaperPath = null;
    private static boolean useConfigWallpaperPath = false;

    public static Path getWallpaperPath(){
        return (useConfigWallpaperPath) ? userWallpaperPath : defaultWallpaperPath;
    }

    public static void updateUserWallpaperPath(Path path){
        if (!path.equals(WallpaperPath.defaultWallpaperPath)){ // 確保非預設
            userWallpaperPath = path;
            useConfigWallpaperPath = true; 
            System.out.println("New default path : " + WallpaperPath.getWallpaperPath());
        }
    }

    public static void resetToDefaultWallpaperPath(){ useConfigWallpaperPath = false; }

    public static boolean useConfigOrNot(){ return useConfigWallpaperPath; }

    public static Path pushUserPath(){
        if (useConfigWallpaperPath){
            return userWallpaperPath;
        }
        return null;
    }
}
