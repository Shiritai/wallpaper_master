package eroiko.ani.controller.ControllerSupporter;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;


public class WallpaperImage {
    // private String directory;
    private Path directory;
    private DirectoryStream<Path> root;
    private ArrayList<Path> wallpapers; 
    private Iterator<Path> it;
    public WallpaperImage(String dir){
        // this.directory = dir;
        directory = Path.of(dir);
        try {
            root = Files.newDirectoryStream(Path.of(directory.toString(), "src", "main", "java", "eroiko", "ani", "img"), "*.{jpg,jpeg,png}");
        } catch (IOException e) {
            System.out.println(e.toString());
        }
        wallpapers = new ArrayList<Path>();
        root.forEach(p -> wallpapers.add(p));
        it = wallpapers.iterator();
    }
    
    public Path getNextWallpaper(){
        return (it.hasNext()) ? it.next() : null;
    }
}
