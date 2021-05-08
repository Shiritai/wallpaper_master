package eroiko.ani.util.NeoWallpaper;

import java.io.File;
import java.nio.file.Path;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import eroiko.ani.util.Dumper;

public class WallpaperUtil {
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

    public static boolean hasSubstring(String str, String subStr){
        var numberMatcher = Pattern.compile(String.format("(%s)+", subStr));
        return numberMatcher.matcher(str).find();
    }
    
    public static boolean matchNameWithoutFormat(String a, String b){
        return a.substring(0, a.indexOf('.')).equals(b.substring(0, b.indexOf('.')));
    }

    public static boolean matchNameWithoutFormat(Path a_, Path b_){
        var a = a_.getFileName().toString();
        var b = b_.getFileName().toString();
        return a.substring(0, a.indexOf('.')).equals(b.substring(0, b.indexOf('.')));
    }
    
    public static String capitalize(String str){
        var ret = new StringBuilder();
        var strArr = str.toCharArray();
        // boolean flag = true;
        for (int i = 0; i < str.length(); ++i){
            if (i == 0){
                ret.append(Character.toUpperCase(strArr[i]));
            }
            else if (strArr[i - 1] == ' '){
                ret.append(Character.toUpperCase(strArr[i]));
            }
            else {
                ret.append(Character.toLowerCase(strArr[i]));
            }
        }
        return ret.toString();
    } 

    /** For serial number generation */
    private static int seed = 1;
    public static String getSerialNumber(){
        return Integer.toString(seed++);
    }
    /** return 1 */
    public static void resetSerialNumber(){ seed = 1; }
    public static void resetSerialNumber(int start){ seed = start; }

    /** get serialNumber from a wallpaperXXX.img */
    public static int getSerialNumberFromAWallpaper(Path p) throws IllegalArgumentException {
        var nameMatcher = Pattern.compile("wallpaper\\d+");
        var numberMatcher = Pattern.compile("\\d+");
        Matcher m1 = nameMatcher.matcher(p.toString());
        if (m1.find()){
            System.out.println(Integer.parseInt(numberMatcher.matcher(m1.group()).group()));
            return Integer.parseInt(numberMatcher.matcher(m1.group()).group());
        }
        throw new IllegalArgumentException("Fail to take wallpaper's serial Number");
    }
    public static String getFileType(File file){
        var tmp = file.getName();
        return tmp.substring(tmp.lastIndexOf('.'), tmp.length());
    }

    public static String getFileType(Path file){
        var tmp = file.getFileName().toString();
        return tmp.substring(tmp.lastIndexOf('.'), tmp.length());
    }
}
