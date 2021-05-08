package eroiko.ani.util.WallpaperClass;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

import eroiko.ani.util.SourceRedirector;
import eroiko.ani.util.myPair;
import eroiko.ani.util.NeoWallpaper.WallpaperComparator;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.image.Image;

/** {@code <deprecated>} 實作類似 Iterator 的資料結構, 所有 get functions (除了 Current) 都會移動 Index */
public class WallpaperImageWithFilter implements WallpaperProto{
    private Path directory;
    private DirectoryStream<Path> root;
    private ArrayList<myPair<Integer, Path>> wallpapers;

    public BooleanProperty isChanged;

    private int current;
    private int size;
    private int length;
    /** 
     * @param directory is the testing directory or the image folder of this project
     * @throws IOException
     */
    public WallpaperImageWithFilter(Path directory) throws IOException{
        isChanged = new SimpleBooleanProperty(false);

        this.directory = directory;
        root = Files.newDirectoryStream(this.directory, "*.{jpg,jpeg,png}");
        System.out.println(Path.of(this.directory.toString()));
        wallpapers = new ArrayList<>();
        root.forEach(p -> wallpapers.add(new myPair<>(0, p)));
        wallpapers.sort((a, b) -> WallpaperComparator.pathNameCompare(a.value.getFileName(), b.value.getFileName())); // 讓圖片照順序排佈
        size = wallpapers.size();
        length = wallpapers.size();
    }
    
    public WallpaperImageWithFilter() throws IOException{
        this(SourceRedirector.defaultImagePath);
    }

    private int rightShift(){
        return current = (current + 1 == length) ? (current = 0) : current + 1;
    }

    private int leftShift(){
        return current = (current == 0) ? (current = length - 1) : current - 1;
    }

    public void add(int certainNumber){
        if (size != 0){
            wallpapers.get(certainNumber).key = 1;
            --size;
            if (size > 0){
                while (wallpapers.get(rightShift()).key != 0);
            }
        }
        isChanged.set(true);
    }
    
    public void add(){
        if (size != 0){
            wallpapers.get(current).key = 1;
            --size;
            if (size > 0){
                while (wallpapers.get(rightShift()).key != 0);
            }
        }
        isChanged.set(true);
    }
    
    public void delete(int certainNumber){
        if (size != 0){
            wallpapers.get(certainNumber).key = -1;
            --size;
            if (size > 0){
                while (wallpapers.get(rightShift()).key != 0);
            }
        }
        isChanged.set(true);
    }
    
    public void delete(){
        if (size != 0){
            wallpapers.get(current).key = -1;
            --size;
            if (size > 0){
                while (wallpapers.get(rightShift()).key != 0);
            }
        }
        isChanged.set(true);
    }
    
    public Path getCurrentWallpaperPath(){
        return wallpapers.get(current).value;
    }
    
    public Image getCurrentWallpaper(){
        try {
            return new Image(wallpapers.get(current).value.toUri().toURL().toString());
        } catch (MalformedURLException e) {
            System.out.println(e.toString());
        }
        return null;
    }
    
    public Path getNextWallpaperPath(){
        while (wallpapers.get(rightShift()).key != 0);
        isChanged.set(true);
        return wallpapers.get(current).value;
    }
    
    public Image getNextWallpaper(){
        while (wallpapers.get(rightShift()).key != 0);
        isChanged.set(true);
        try {
            return new Image(wallpapers.get(current).value.toUri().toURL().toString());
        } catch (MalformedURLException e) {
            System.out.println(e.toString());
        }
        return null;
    }
    
    public Path getPreviousWallpaperPath(){
        while (wallpapers.get(leftShift()).key != 0);
        isChanged.set(true);
        return wallpapers.get(current).value;
    }
    
    public Image getPreviousWallpaper(){
        while (wallpapers.get(leftShift()).key != 0);
        isChanged.set(true);
        try {
            return new Image(wallpapers.get(current).value.toUri().toURL().toString());
        } catch (MalformedURLException e) {
            System.out.println(e.toString());
        }
        return null;
    }

    public int getSize(){ return size; }
    public boolean isEmpty(){ return size == 0; }
    /* For debug */
    public void printSelection(){
        for (var arr : wallpapers){
            System.out.println(arr.key);
        }
    }
    /* For Output */
    public void pullToComputer(Path destination){
        /* ABOUT TO APPEND */
        for (var wp : wallpapers){
            if (wp.key == 1){
                // Copy wallpaper to the target position
                // new File()
            }
        }
        for (var wp : wallpapers){
            if (wp.key == -1){
                // Delete file, do this before copying!
            }
        }
        /* Do nothing with wp.key == 0, keep it in eroiko.ani.data (tmp) */
    }
}

