package eroiko.ani.util.WallpaperClass;

import java.nio.file.Path;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import eroiko.ani.util.Dumper;

public class WallpaperComparator {
    /* 比較同類型, 以編號區分的檔案, 使用正則表達式, 適用於 ArrayList, Set */
    public static int pathNameCompare(Path a, Path b){
        var numberMatcher = Pattern.compile("\\d+");
        Matcher m1 = numberMatcher.matcher(a.toString());
        Matcher m2 = numberMatcher.matcher(b.toString());
        if (m1.find() && m2.find()){
            return Integer.parseInt(m1.group(0)) - Integer.parseInt(m2.group(0));
        }
        return a.toString().compareTo(b.toString());
    }

    public static int pathNameCompare(String a, String b){
        var numberMatcher = Pattern.compile("\\d+");
        Matcher m1 = numberMatcher.matcher(a);
        Matcher m2 = numberMatcher.matcher(b);
        if (m1.find() && m2.find()){
            return Integer.parseInt(m1.group(0)) - Integer.parseInt(m2.group(0));
        }
        return a.compareTo(b);
    }

    /* 取得各桌布的 Serial Number 直接建成 Map 比較有效率... */
    public static Integer takeWallpaperSerialNumber(Path p){
        var numberMatcher = Pattern.compile("\\d+");
        Matcher m1 = numberMatcher.matcher(p.toString());
        if (m1.find()){
            return Integer.parseInt(m1.group(0));
        }
        throw new IllegalArgumentException("Fail to take wallpaper's serial Number");
    }

    public static boolean isImage(Path pathOfFile){
        return Dumper.imagePattern.matcher(pathOfFile.getFileName().toString()).find();
    }
}
