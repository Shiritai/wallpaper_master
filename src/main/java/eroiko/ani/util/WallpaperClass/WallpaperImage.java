package eroiko.ani.util.WallpaperClass;

import java.io.EOFException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

import eroiko.ani.MainApp;
import eroiko.ani.util.SourceRedirector;
import javafx.scene.image.Image;


/** 實作類似 Iterator 的資料結構, 所有 get functions (除了 Current) 都會移動 Index */
public class WallpaperImage {
    private Path directory;
    private DirectoryStream<Path> root;
    private ArrayList<Path> wallpapers;
    private int initIndex = -1;
    // private TreeMap<Integer, Path> wallpapers;
    private int current;
    /** 
     * @param directory : the testing directory or the image folder of this project
     * @param certain : is true if  {@code directory} is in testing mode
     * @param initImage : set this first image you'd like to see with it's path
     * @throws IOException
     */
    public WallpaperImage(String directory, boolean certain, Path initImage) throws IOException{
        this.directory = Path.of(directory);
        if (certain){
            root = Files.newDirectoryStream(SourceRedirector.defaultDataPath, "*.{jpg,jpeg,png}");
        }
        else {
            root = Files.newDirectoryStream(Path.of(this.directory.toString()), "*.{jpg,jpeg,png}");
            System.out.println(Path.of(this.directory.toString()));
        }
        wallpapers = new ArrayList<Path>();
        // wallpapers = new TreeSet<Path>((e1, e2) -> pathNameCompare(a, b));
        // wallpapers = new TreeMap<Integer, Path>();
        root.forEach(p -> wallpapers.add(p));
        // root.forEach(p -> wallpapers.put(takeWallpaperSerialNumber(p), p));
        wallpapers.sort((a, b) -> WallpaperComparator.pathNameCompare(a.getFileName(), b.getFileName())); // 讓圖片照順序排佈
        if (initImage != null){
            setInitImage(initImage);
            current = initIndex;
        }
    }
    /** 
     * @param directory : the testing directory or the image folder of this project
     * @param certain : is true if  {@code directory} is in testing mode
     * @throws IOException
     */
    public WallpaperImage(String directory, boolean certain) throws IOException{
        this(directory, certain, null);
    }
    
    public WallpaperImage() throws IOException{
        this(FileSystems.getDefault().getPath("").toAbsolutePath().toString(), MainApp.isTesting);
    }

    /* 此四為了繼承之用 */
    public void add() throws EOFException{}
    public void add(int serialNumber) throws EOFException{}
    public void delete() throws EOFException{}
    public void delete(int serialNumber) throws EOFException{}

    public boolean setInitImage(Path p){
        return (initIndex = wallpapers.indexOf(p)) != -1;
    }

    public Image getInitImage(){
        try {
            return new Image(wallpapers.get(initIndex).toUri().toURL().toString());
        } catch (Exception e) { // 包含 initIndex == -1 的例外
            System.out.println(e.toString());
            return null;
        }
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

    public static WallpaperImage copy(WallpaperImage wp) throws IOException{
        var newWp = new WallpaperImage(wp.directory.toString(), MainApp.isTesting);
        newWp.current = wp.current;
        return newWp;
    }
}

