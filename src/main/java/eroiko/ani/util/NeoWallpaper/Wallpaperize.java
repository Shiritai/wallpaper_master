package eroiko.ani.util.NeoWallpaper;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Wallpaperize {
    
    public ObservableList<String> wallpaperize = FXCollections.observableArrayList(); // 未來 list 實作可能會用到
    private Path path;
    
    /** 將當前資料夾準備好, 未來可以用此物件對此資料夾進行無數次 Wallpaperize */
    public Wallpaperize(Path path, boolean toUseDefault) throws IllegalArgumentException {
        this.path = path;
        if (!path.toFile().isDirectory()){
            throw new IllegalArgumentException("Not a directory");
        }
        if (toUseDefault){
            WallpaperPath.updateUserWallpaperPath(path);
        }
    }
    
    /** 
     *  將當前資料夾 Wallpaperize, 並取得當前最高 {@code Serial number + 1} 方便再次使用 WallpaperUtil.gerSerialNumber
     * @param toUseDefault : 是否將此資料夾設為預設 Wallpaper 資料夾
     */
    public int execute(){
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
            return Integer.parseInt(WallpaperUtil.getSerialNumber());
        }
        return 0;
    }
    
}
