package eroiko.ani.util.NeoWallpaper;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.TreeMap;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Wallpaperize {
    
    public ObservableList<String> wallpaperize = FXCollections.observableArrayList(); // 未來 list 實作可能會用到
    private Path path;
    
    /** 
     *  將當前資料夾 Wallpaperize, 並取得當前最高 {@code Serial number + 1} 方便再次使用 WallpaperUtil.gerSerialNumber()
     * @param toUseDefault : 是否將此資料夾設為預設 Wallpaper 資料夾
     */
    public Wallpaperize(Path path, boolean toUseDefault) throws IllegalArgumentException {
        this.path = path;
        if (!path.toFile().isDirectory()){
            throw new IllegalArgumentException("Not a directory");
        }
        if (toUseDefault){
            WallpaperPath.updateUserWallpaperPath(path);
        }
    }

    public Wallpaperize(){
        this.path = WallpaperPath.getWallpaperPath();
        System.out.println("Ready to initialize wallpaper folder");
    }
    
    /** make wallpapers to "wallpaperXX" format */
    public int execute(){
        WallpaperUtil.resetSerialNumber();
        try {
            try (var root = Files.newDirectoryStream(path, "*.{jpg,jpeg,png}")){
                if (root != null){
                    var wallpaperList = new TreeMap<Integer, Path>();
                    var notWallpaperList = new ArrayList<Path>();
                    root.forEach(p -> {
                        try {
                            wallpaperList.put(WallpaperUtil.getSerialNumberFromAWallpaper(p), p);
                        } catch (IllegalArgumentException ie){
                            notWallpaperList.add(p); // not a WallpaperXX form
                        }
                    });
                    WallpaperUtil.resetSerialNumber();
                    notWallpaperList.forEach(p -> {
                        while (wallpaperList.containsKey(WallpaperUtil.peekSerialNumber())){
                            WallpaperUtil.passSerialNumber();
                        }
                        p.toFile().renameTo(
                            new File(
                                path.toAbsolutePath().toString() + 
                                "/wallpaper" +
                                WallpaperUtil.getSerialNumber() + 
                                WallpaperUtil.getFileType(p)
                            )
                        );
                        wallpaperize.add(p.getFileName().toString()); // 也許未來有用
                    });
                    return WallpaperUtil.peekSerialNumber();
                }
            }
        } catch (IOException e) {}
        return 0;
    }
    
}
