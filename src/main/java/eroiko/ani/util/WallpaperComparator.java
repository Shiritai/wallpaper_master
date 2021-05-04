package eroiko.ani.util;

import java.nio.file.Path;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WallpaperComparator {
    /* 比較同類型, 以編號區分的檔案, 使用正則表達式, 適用於 ArrayList, Set */
    public static int pathNameCompare(Path a, Path b){
        var numberMatcher = Pattern.compile("\\d+");
        Matcher m1 = numberMatcher.matcher(a.toString());
        Matcher m2 = numberMatcher.matcher(b.toString());
        if (m1.find() && m2.find()){
            return Integer.parseInt(m1.group(0)) - Integer.parseInt(m2.group(0));
        }
        return 0;
    }

    public static int pathNameCompare(String a, String b){
        var numberMatcher = Pattern.compile("\\d+");
        Matcher m1 = numberMatcher.matcher(a.toString());
        Matcher m2 = numberMatcher.matcher(b.toString());
        if (m1.find() && m2.find()){
            return Integer.parseInt(m1.group(0)) - Integer.parseInt(m2.group(0));
        }
        return 0;
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
