package eroiko.ani.controller.ControllerSupporter;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

import javafx.scene.image.Image;


public class WallpaperImage {
    private Path directory;
    private DirectoryStream<Path> root;
    private ArrayList<Path> wallpapers; 
    private int current;
    public WallpaperImage(String directory){
        // this.directory = dir;
        this.directory = Path.of(directory);
        try {
            root = Files.newDirectoryStream(Path.of(this.directory.toString(), "src", "main", "java", "eroiko", "ani", "img"), "*.{jpg,jpeg,png}");
        } catch (IOException e) {
            System.out.println(e.toString());
        }
        wallpapers = new ArrayList<Path>();
        root.forEach(p -> wallpapers.add(p));
        wallpapers.sort((a, b) -> pathNameCompare(a.getFileName(), b.getFileName()));
    }
    
    public Path getNextWallpaperPath(){
        return wallpapers.get(current);
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

    /* 比較同類型, 以編號區分的檔案 */
    private int pathNameCompare(Path a, Path b){
        var aChar = a.toString().toCharArray();
        var bChar = b.toString().toCharArray();
        int length = (aChar.length < bChar.length) ? aChar.length : bChar.length;
        int i;
        for (i = 0; i < length; ++i){
            if (aChar[i] != bChar[i]){
                break;
            }
        }
        int ja;
        for (ja = aChar.length - 1; ja > i; --ja){
            if (aChar[ja] == '.'){
                break;
            }
        }
        int jb;
        for (jb = bChar.length - 1; jb > i; --jb){
            if (bChar[jb] == '.'){
                break;
            }
        }
        return Integer.parseInt(a.toString().substring(i, ja)) - Integer.parseInt(b.toString().substring(i, jb));
    }
}

