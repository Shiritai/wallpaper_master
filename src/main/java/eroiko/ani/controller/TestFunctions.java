package eroiko.ani.controller;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
        // var w = new WallpaperImage();
        // System.out.println(w.getNextWallpaperPath().getFileName());
        // System.out.println(w.getNextWallpaperPath().getFileName());
        // System.out.println(w.getNextWallpaperPath().getFileName());
        var num1 = Pattern.compile("\\d+");
        // var match = num.matcher("wallpaper101.png");
        // if (match.find()){
        //     System.out.println(match.group(0));
        // }
        Matcher m1 = num1.matcher("wallpaper144.jpg");
        Matcher m2 = num1.matcher("wallpaper101.png");
        if (m1.find() && m2.find()){
            System.out.println(m1.group(0));
            System.out.println(m2.group(0));
        }
    }
}
