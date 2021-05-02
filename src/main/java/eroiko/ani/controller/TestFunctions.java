package eroiko.ani.controller;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;

class WallpaperImage {
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
        wallpapers.sort((a, b) -> pathNameCompare(a.getFileName(), b.getFileName()));
        it = wallpapers.iterator();
    }
    
    public Path getNextWallpaper(){
        return (it.hasNext()) ? it.next() : null;
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


public class TestFunctions {
    public static void main(String [] args) throws IOException, URISyntaxException{
        // var cur = FileSystems.getDefault().getPath("").toAbsolutePath();
        // System.out.println(cur);
        // System.out.println(Path.of(cur.toString(), "src", "main", "java", "eroiko", "ani", "img"));
        // var dirSys = Files.newDirectoryStream(Path.of(cur.toString(), "src", "main", "java", "eroiko", "ani", "img"), "*.{jpg,jpeg,png}");
        // dirSys.forEach(p -> {
        //     Set<String> keys = null;
        //     Collection<Object> values = null;
        //     try {
        //         keys = Files.readAttributes(p, "*").keySet();
        //         values = Files.readAttributes(p, "*").values();
        //     } catch (IOException e) {
        //         e.printStackTrace();
        //     }
        //     System.out.println();
        //     var k = keys.iterator();
        //     var v = values.iterator();
        //     while (k.hasNext() && v.hasNext()){
        //         var tmp = v.next();
        //         System.out.println(k.next().toString() + " : " + ((tmp == null) ? "null" : tmp.toString()));
        //     }
        // });
        var w = new WallpaperImage(FileSystems.getDefault().getPath("").toAbsolutePath().toString());
        System.out.println(w.getNextWallpaper().getFileName());
        System.out.println(w.getNextWallpaper().getFileName());
        System.out.println(w.getNextWallpaper().getFileName());
    }
}
