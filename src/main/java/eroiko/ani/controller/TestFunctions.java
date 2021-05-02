package eroiko.ani.controller;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.FileSystems;
import java.nio.file.Path;

import eroiko.ani.controller.ControllerSupporter.WallpaperImage;

public class TestFunctions {
    public static Path testWallpaperPath = FileSystems.getDefault().getPath("wallpaper_tmp").toAbsolutePath();
    

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
        var w = new WallpaperImage();
        System.out.println(w.getNextWallpaperPath().getFileName());
        System.out.println(w.getNextWallpaperPath().getFileName());
        System.out.println(w.getNextWallpaperPath().getFileName());
    }
}
