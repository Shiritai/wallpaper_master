package eroiko.ani.controller.ControllerSupporter;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import eroiko.ani.MainApp;
import eroiko.ani.controller.TestFunctions;
import javafx.scene.image.Image;


public class WallpaperImage {
    private Path directory;
    private DirectoryStream<Path> root;
    private ArrayList<Path> wallpapers;
    // private TreeMap<Integer, Path> wallpapers;
    private int current;
    /** 
     * @param directory is the testing directory or the image folder of this project
     * @param certain : is true if  {@code directory} is in testing mode
     */
    public WallpaperImage(String directory, boolean certain){
        this.directory = Path.of(directory);
        try {
            if (certain){
                root = Files.newDirectoryStream(TestFunctions.testWallpaperPath, "*.{jpg,jpeg,png}");
            }
            else {
                root = Files.newDirectoryStream(Path.of(this.directory.toString()), "*.{jpg,jpeg,png}");
                System.out.println(Path.of(this.directory.toString()));
            }
        } catch (IOException e) {
            System.out.println(e.toString());
        }
        wallpapers = new ArrayList<Path>();
        // wallpapers = new TreeSet<Path>((e1, e2) -> pathNameCompare(a, b));
        // wallpapers = new TreeMap<Integer, Path>();
        root.forEach(p -> wallpapers.add(p));
        // root.forEach(p -> wallpapers.put(takeWallpaperSerialNumber(p), p));
        wallpapers.sort((a, b) -> pathNameCompare(a.getFileName(), b.getFileName()));
    }
    
    public WallpaperImage(){
        this(FileSystems.getDefault().getPath("").toAbsolutePath().toString(), MainApp.isTesting);
    }

    public Path getCurrentWallpaperPath(){
        return wallpapers.get(current);
    }
    
    public Path getNextWallpaperPath(){
        return wallpapers.get(++current);
    }

    public Image getCurrentWallpaper(){
        try {
            return new Image(wallpapers.get(current).toUri().toURL().toString());
        } catch (MalformedURLException e) {
            System.out.println(e.toString());
        }
        return null;
    }

    public Image getNextWallpaper(){
        try {
            return (++current < wallpapers.size()) ? new Image(wallpapers.get(current).toUri().toURL().toString()) : new Image(wallpapers.get((current = 0)).toUri().toURL().toString());
        } catch (MalformedURLException e) {
            System.out.println(e.toString());
        }
        return null;
    }
    
    public Image getLastWallpaper(){
        try {
            return (--current >= 0) ? new Image(wallpapers.get(current).toUri().toURL().toString()) : new Image(wallpapers.get((current = wallpapers.size() - 1)).toUri().toURL().toString());
        } catch (MalformedURLException e) {
            System.out.println(e.toString());
        }
        return null;
    }

    public static WallpaperImage copy(WallpaperImage wp){
        var newWp = new WallpaperImage(wp.directory.toString(), MainApp.isTesting);
        newWp.current = wp.current;
        return newWp;
    }

    /* 比較同類型, 以編號區分的檔案, 使用正則表達式, 適用於 ArrayList, Set */
    private static int pathNameCompare(Path a, Path b){
        var numberMatcher = Pattern.compile("\\d+");
        Matcher m1 = numberMatcher.matcher(a.toString());
        Matcher m2 = numberMatcher.matcher(b.toString());
        if (m1.find() && m2.find()){
            return Integer.parseInt(m1.group(0)) - Integer.parseInt(m2.group(0));
        }
        return 0;
    }

    /* 取得各桌布的 Serial Number 直接建成 Map 比較有效率... */
    // private static Integer takeWallpaperSerialNumber(Path p){
    //     var numberMatcher = Pattern.compile("\\d+");
    //     Matcher m1 = numberMatcher.matcher(p.toString());
    //     if (m1.find()){
    //         return Integer.parseInt(m1.group(0));
    //     }
    //     throw new IllegalArgumentException("Fail to take wallpaper's serial Number");
    // }
}

