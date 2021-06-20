/*
 * Author : Shiritai (楊子慶, or Eroiko on Github) at 2021/06/13.
 * See https://github.com/Shiritai/wallpaper_master for more information.
 * Created using VSCode.
 */
package eroiko.ani.util.NeoWallpaper;

import java.io.File;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.filechooser.FileSystemView;

import eroiko.ani.util.Method.Dumper;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class WallpaperUtil {
    public static Pattern numberMatcher = Pattern.compile("\\d+");
    /* 比較同類型, 以編號區分的檔案, 使用正則表達式, 適用於 ArrayList, Set */
    public static int pathNameCompare(Path a, Path b){
        return pathNameCompare(a.getFileName().toString(), b.getFileName().toString());
    }
    
    public static int pathNameCompare(String a, String b){
        Matcher m1 = numberMatcher.matcher(a);
        Matcher m2 = numberMatcher.matcher(b);
        if (m1.find() && m2.find()){
            var s1 = m1.group(0);
            var s2 = m2.group(0);
            if (s1.length() != s2.length()){
                return s1.length() - s2.length();
            }
            return s1.compareTo(s2);
        }
        else if (m1.find()){
            return 1;
        }
        else if (m2.find()){
            return -1;
        }
        return a.toString().compareTo(b.toString());
    }

    public static int pathDirAndNameCompare(Path a, Path b){
        if (Dumper.isImage(a) && Dumper.isImage(b)){ // 比 Wallpaper 數字, 確保 Wallpaper 依正確的編號顯示
            return pathNameCompare(a, b);
        }
        boolean aD = Files.isDirectory(a);
        boolean bD = Files.isDirectory(b);
        if (aD && !bD){ return -1; }
        if (!aD && bD){ return 1; }
        return a.toString().compareTo(b.toString()); // 這個不是直接返回這個即可
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
    public static int getIntSerialNumber(){
        return seed++;
    }
    /** jump over this number */
    public static int passSerialNumber(){ return seed++; }
    /** Peek current serial number without changing it */
    public static int peekSerialNumber(){
        return seed;
    }
    
    public static void resetSerialNumber(){ seed = 1; }
    public static void resetSerialNumber(int start){ seed = start; }

    /** get serialNumber from a wallpaperXXX.img */
    public static int getSerialNumberFromAWallpaper(Path p) throws IllegalArgumentException {
        var nameMatcher = Pattern.compile("wallpaper\\d+");
        Matcher m1 = nameMatcher.matcher(p.toString());
        if (m1.find()){
            var m2 = numberMatcher.matcher(m1.group());
            if (m2.find()){
                return Integer.parseInt(m2.group());
            }
        }
        throw new IllegalArgumentException("Fail to take wallpaper's serial Number, path : " + p.getFileName().toString());
    }
    public static String getFileType(File file){
        var tmp = file.getName();
        return tmp.substring(tmp.lastIndexOf('.'), tmp.length());
    }

    public static String getFileType(Path file){
        var tmp = file.getFileName().toString();
        return tmp.substring(tmp.lastIndexOf('.'), tmp.length());
    }

    /** 取得系統小圖示, 這野太方便... */
    public static ImageView fetchIconUsePath(Path path){ // 真方便
        var tmp = FileSystemView.getFileSystemView().getSystemIcon(path.toFile());
        var tmpBufferImage = new java.awt.image.BufferedImage(
            tmp.getIconWidth(),
            tmp.getIconHeight(),
            java.awt.image.BufferedImage.TYPE_INT_ARGB
        );
        tmp.paintIcon(null, tmpBufferImage.getGraphics(), 0, 0);
        return new ImageView(SwingFXUtils.toFXImage(tmpBufferImage, null));
    }

    public static ImageView fetchSmallImage(Path path){ // 真方便
        var tmp = FileSystemView.getFileSystemView().getSystemIcon(path.toFile());
        var tmpBufferImage = new java.awt.image.BufferedImage(
            tmp.getIconWidth(),
            tmp.getIconHeight(),
            java.awt.image.BufferedImage.TYPE_INT_ARGB
        );
        tmp.paintIcon(null, tmpBufferImage.getGraphics(), 0, 0);

        try {
            var img = new Image(path.toAbsolutePath().toUri().toURL().toString(), 64, 64, true, false);
            return new ImageView(img);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
