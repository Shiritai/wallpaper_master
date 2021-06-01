package eroiko.ani.deprecated.WallpaperClass;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

import eroiko.ani.util.NeoWallpaper.WallpaperUtil;
import eroiko.ani.util.NeoWallpaper.WallpaperPath;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.image.Image;


/** {@code <deprecated>} 實作類似 Iterator 的資料結構, 所有 get functions (除了 Current) 都會移動 Index */
public class WallpaperImage implements WallpaperProto{
    public BooleanProperty isChanged;

    private Path directory;
    private ArrayList<Path> wallpapers;
    private int initIndex = -1;
    // private TreeMap<Integer, Path> wallpapers;
    private int current;
    /** 
     * @param directory : the testing directory or the image folder of this project
     * @param initImage : set this first image you'd like to see with it's path
     * @throws IOException
     */
    public WallpaperImage(Path directory, Path initImage) throws IOException{
        isChanged = new SimpleBooleanProperty(false);

        this.directory = directory;
        wallpapers = new ArrayList<Path>();
        try (var root = Files.newDirectoryStream(this.directory, "*.{jpg,jpeg,png}")){
            root.forEach(p -> wallpapers.add(p));
        }
        System.out.println(Path.of(this.directory.toString()));
        wallpapers.sort((a, b) -> WallpaperUtil.pathNameCompare(a.getFileName(), b.getFileName())); // 讓圖片照順序排佈
        if (initImage != null){
            setInitImage(initImage);
            current = initIndex;
        }
    }
    /** 
     * @param directory : the testing directory or the image folder of this project
     * @throws IOException
     */
    public WallpaperImage(Path directory) throws IOException{
        this(directory, null);
    }
    
    /** 建立預設圖片庫的 WallpaperImage */
    public WallpaperImage() throws IOException{
        this(WallpaperPath.DEFAULT_IMAGE_PATH);
    }

    public void add(){}
    public void add(int certainNumber){}
    public void delete(){}
    public void delete(int certainNumber){}

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
        var tmp = wallpapers.get((++current < wallpapers.size()) ? current : (current = 0));
        isChanged.set(true);
        return tmp;
    }

    public Path getPreviousWallpaperPath(){
        var tmp = wallpapers.get((--current >= 0) ? current : (current = wallpapers.size() - 1));
        isChanged.set(true);
        return tmp;
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
            var tmp = new Image(wallpapers.get((++current < wallpapers.size()) ? current : (current = 0)).toUri().toURL().toString());
            isChanged.set(true);
            return tmp;
        } catch (MalformedURLException e) {
            System.out.println(e.toString());
        }
        return null;
    }
    
    public Image getPreviousWallpaper(){
        try {
            var tmp = new Image(wallpapers.get((--current >= 0) ? current : (current = wallpapers.size() - 1)).toUri().toURL().toString());
            isChanged.set(true);
            return tmp;
        } catch (MalformedURLException e) {
            System.out.println(e.toString());
        }
        return null;
    }

    public static WallpaperImage copy(WallpaperImage wp) throws IOException{
        var newWp = new WallpaperImage(wp.directory);
        newWp.current = wp.current;
        return newWp;
    }

    @Override
    public int getSize() {
        return wallpapers.size();
    }
}

