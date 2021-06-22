/*
 * Author : Shiritai (楊子慶, or Eroiko on Github) at 2021/06/13.
 * See https://github.com/Shiritai/wallpaper_master for more information.
 * Created using VSCode.
 */
package eroiko.ani.util.NeoWallpaper;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.TreeMap;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Wallpaperize {
    
    public ObservableList<String> wallpaperize = FXCollections.observableArrayList(); // 未來 list 實作可能會用到
    private Path path;
    
    /** 
     * 將當前資料夾 Wallpaperize, 所有圖片統一命名為 WallpaperXXX.XXX
     * @param path          要 Wallpaperize 的資料夾
     * @param toUseDefault  是否將此資料夾設為預設 Wallpaper 資料夾
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
    }
    
    /** make wallpapers to "wallpaperXXX" format */
    public void execute(){
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
                            notWallpaperList.add(p); // not a WallpaperXXX form
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
                }
            }
        } catch (IOException e) {}
    }

    public void mergeWith(Path source){
        try {
            try (var root = Files.newDirectoryStream(path, "*.{jpg,jpeg,png}")){
                if (root != null){
                    WallpaperUtil.resetSerialNumber();
                    var wallpaperList = new TreeMap<Integer, Path>();
                    var inner = new ArrayList<Path>();
                    root.forEach(p -> {
                        try {
                            wallpaperList.put(WallpaperUtil.getSerialNumberFromAWallpaper(p), p);
                        } catch (IllegalArgumentException ie){
                            inner.add(p); // not a WallpaperXXX form
                        }
                    });
                    /* merge inner and outer files */
                    WallpaperUtil.resetSerialNumber();
                    inner.forEach(p -> {
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
                    var outer = new ArrayList<Path>();
                    try (var sourceRoot = Files.newDirectoryStream(source, "*.{jpg,jpeg,png}")){
                        sourceRoot.forEach(p -> outer.add(p));
                    }
                    outer.forEach(p -> {
                        while (wallpaperList.containsKey(WallpaperUtil.peekSerialNumber())){
                            WallpaperUtil.passSerialNumber();
                        }
                        try {
                            Files.copy(p, Path.of(
                                path.toAbsolutePath().toString() +
                                "/wallpaper" +
                                WallpaperUtil.getSerialNumber() + 
                                WallpaperUtil.getFileType(p)
                            ), StandardCopyOption.REPLACE_EXISTING
                            );
                        } catch (IOException ie){
                            System.out.println(ie.toString());
                        }
                        wallpaperize.add(p.getFileName().toString()); // 也許未來有用
                    });
                }
            }
        } catch (IOException e) {}
    }
    
}
