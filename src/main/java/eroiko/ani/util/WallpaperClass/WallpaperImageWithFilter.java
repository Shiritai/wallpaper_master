package eroiko.ani.util.WallpaperClass;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

import eroiko.ani.MainApp;
import eroiko.ani.util.SourceRedirector;
import eroiko.ani.util.myPair;
import javafx.scene.image.Image;

/** 實作類似 Iterator 的資料結構, 所有 get functions (除了 Current) 都會移動 Index */
public class WallpaperImageWithFilter extends WallpaperImage{
    private Path directory;
    private DirectoryStream<Path> root;
    private ArrayList<myPair<Boolean, Path>> wallpapers;

    private int current;
    private int size;
    /** 
     * @param directory is the testing directory or the image folder of this project
     * @param certain : is true if {@code directory} is in testing mode
     * @throws IOException
     */
    public WallpaperImageWithFilter(String directory, boolean certain) throws IOException{
        this.directory = Path.of(directory);
        if (certain){
            root = Files.newDirectoryStream(SourceRedirector.defaultDataPath, "*.{jpg,jpeg,png}");
        }
        else {
            root = Files.newDirectoryStream(Path.of(this.directory.toString()), "*.{jpg,jpeg,png}");
            System.out.println(Path.of(this.directory.toString()));
        }
        wallpapers = new ArrayList<>();
        root.forEach(p -> wallpapers.add(new myPair<>(true, p)));
        wallpapers.sort((a, b) -> WallpaperComparator.pathNameCompare(a.value.getFileName(), b.value.getFileName())); // 讓圖片照順序排佈
        size = wallpapers.size();
    }
    
    public WallpaperImageWithFilter() throws IOException{
        this(FileSystems.getDefault().getPath("").toAbsolutePath().toString(), MainApp.isTesting);
    }

    public void delete(int certainNumber){
        wallpapers.get(certainNumber).key = false;
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
        while (!wallpapers.get((current + 1 == size) ? (current = 0) : ++current).key);
        return wallpapers.get(current).value;
    }
    
    public Image getNextWallpaper(){
        while (!wallpapers.get((current + 1 == size) ? (current = 0) : ++current).key);
        try {
            return new Image(wallpapers.get(current).value.toUri().toURL().toString());
        } catch (MalformedURLException e) {
            System.out.println(e.toString());
        }
        return null;
    }
    
    public Image getLastWallpaper(){
        while (!wallpapers.get((current == 0) ? (current = size - 1) : --current).key);
        try {
            return new Image(wallpapers.get(current).value.toUri().toURL().toString());
        } catch (MalformedURLException e) {
            System.out.println(e.toString());
        }
        return null;
    }
}

