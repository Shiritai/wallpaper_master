package eroiko.ani.deprecated.WallpaperClass;

import java.nio.file.Path;

import javafx.scene.image.Image;

/** {@code <deprecated>} */
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
