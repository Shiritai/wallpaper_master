/*
 * Author : Shiritai (楊子慶, or Eroiko on Github) at 2021/06/13.
 * See https://github.com/Shiritai/wallpaper_master for more information.
 * Created using VSCode.
 */
package eroiko.ani.deprecated.WallpaperClass;

import java.nio.file.Path;

import javafx.scene.image.Image;

/** {@code <deprecated>} */
@Deprecated
public interface WallpaperProto {
    public void add(int certainNumber);
    public void add();
    public void delete(int certainNumber);
    public void delete();
    public Path getCurrentWallpaperPath();
    public Path getPreviousWallpaperPath();
    public Path getNextWallpaperPath();
    public Image getCurrentWallpaper();
    public Image getPreviousWallpaper();
    public Image getNextWallpaper();
    public int getSize();
}
