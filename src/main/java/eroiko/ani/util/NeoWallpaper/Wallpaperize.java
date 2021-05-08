package eroiko.ani.util.NeoWallpaper;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Wallpaperize {
    
    public ObservableList<String> wallpaperize = FXCollections.observableArrayList();
    
    /** make wallpapers to "wallpaperXX" format */
    public Wallpaperize(Path path, boolean toUseDefault) throws IllegalArgumentException {
        if (!path.toFile().isDirectory()){
            throw new IllegalArgumentException("Not a directory");
        }
        /* make wallpapers to "wallpaperXX" format */
        DirectoryStream<Path> root = null;
        WallpaperUtil.resetSerialNumber();
        try {
            root = Files.newDirectoryStream(path, "*.{jpg,jpeg,png}");
        } catch (IOException e) {}
        if (root != null){
            for (var r : root){
                r.toFile().renameTo(new File(
                    "wallpaper" + WallpaperUtil.getSerialNumber() + WallpaperUtil.getFileType(r)
                ));
                wallpaperize.add(r.getFileName().toString());
            }
            if (toUseDefault){
                WallpaperPath.updateUserWallpaperPath(path);
            }
        }
    }
    
}
