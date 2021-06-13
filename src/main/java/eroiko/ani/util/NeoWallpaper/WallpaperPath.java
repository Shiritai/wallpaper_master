/*
 * Author : Shiritai (楊子慶, or Eroiko on Github) at 2021/06/13.
 * See https://github.com/Shiritai/wallpaper_master for more information.
 * Created using VSCode.
 */
package eroiko.ani.util.NeoWallpaper;

import java.nio.file.FileSystems;
import java.nio.file.Path;

public class WallpaperPath {
    public static final Path DEFAULT_DATA_PATH = FileSystems.getDefault().getPath("data").toAbsolutePath();
    public static final Path DEFAULT_TMP_WALLPAPER_PATH = FileSystems.getDefault().getPath("data/tmp").toAbsolutePath();
    public static final Path DEFAULT_WALLPAPER_PATH = FileSystems.getDefault().getPath("data/wallpaper").toAbsolutePath();
    public static final Path DEFAULT_IMAGE_PATH = FileSystems.getDefault().getPath("data/default").toAbsolutePath();
    public static final Path DEFAULT_MUSIC_PATH = FileSystems.getDefault().getPath("data/music").toAbsolutePath();
    public static final Path IMAGE_SOURCE_PATH = FileSystems.getDefault().getPath("data/img").toAbsolutePath();
    public static final Path FXML_SOURCE_PATH = FileSystems.getDefault().getPath("data/view").toAbsolutePath();
    
    private static Path userWallpaperPath = null;
    private static boolean useConfigWallpaperPath = false;

    public static Path getWallpaperPath(){
        return (useConfigWallpaperPath) ? userWallpaperPath : DEFAULT_WALLPAPER_PATH;
    }

    public static void updateUserWallpaperPath(Path path){
        if (!path.equals(WallpaperPath.DEFAULT_WALLPAPER_PATH)){ // 確保非預設
            userWallpaperPath = path;
            useConfigWallpaperPath = true; 
            System.out.println("New default path : " + WallpaperPath.getWallpaperPath());
        }
    }

    public static void resetToDEFAULT_WALLPAPER_PATH(){ useConfigWallpaperPath = false; }

    public static boolean useConfigOrNot(){ return useConfigWallpaperPath; }

    public static Path pushUserPath(){
        if (useConfigWallpaperPath){
            return userWallpaperPath;
        }
        return null;
    }
}
