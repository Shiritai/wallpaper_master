/*
 * Author : Shiritai (楊子慶, or Eroiko on Github) at 2021/06/13.
 * See https://github.com/Shiritai/wallpaper_master for more information.
 * Created using VSCode.
 */
package eroiko.ani.util.Method;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Path;
import java.util.regex.Pattern;

import eroiko.ani.model.NewCrawler.CrawlerManager;

public class Dumper {
    public static Pattern imagePattern = Pattern.compile(".*?jpe?g|png|gif$");
    public static Pattern pjImagePattern = Pattern.compile(".*?jpe?g|png$");
    public static Pattern musicPattern = Pattern.compile(".*?wav|mp3$");

    /* 負責讀資料 + 寫檔案 */
    public static void dump(InputStream in, OutputStream out) throws IOException {
        dump(in, out, 16384);
    }

    public static void dump(InputStream in, OutputStream out, int approximateSize) throws IOException {
        try (var input = new BufferedInputStream(in); 
        var output = new BufferedOutputStream(out)){ // try auto close
            var data = new byte[approximateSize]; // 可以設定大小
            var length = 0;
            while ((length = input.read(data)) != 0){
                output.write(data, 0, length);
            }
        }
    }
    
    public static void dump(Reader in, Writer out){
        try (in; out){
            try (var bufIn = new BufferedReader(in); var bufOut = new BufferedWriter(out)){
                String tmp;
                while ((tmp = bufIn.readLine()) != null){
                    bufOut.append(tmp + "\n");
                }
            }
        } catch (IOException ie){
            ie.printStackTrace();
        }
    }

    /**
     * @param cm : Crawler Manager
     * @param serialNumber : Wallpaper serial number in Wallpaper.class
     * @param url : image url link
     * @param isFull : is Full Image or Preview Image
     * @return whether the download done successfully or not
     */
    public static boolean downloadPicture(CrawlerManager cm, int serialNumber, String url, boolean isFull){
        return downloadPicture(cm, serialNumber, url, isFull, 0);
    }

    /**
     * 此私有函數在下載失敗時會遞迴呼叫自己
     * @param tryTime : 嘗試重新下載次數, 上限為 16 次
     * @return 是否下載成功
     */
    private static boolean downloadPicture(CrawlerManager cm, int serialNumber, String url, boolean isFull, int tryTime){
        var successful = true;
        if (imagePattern.matcher(url).find()){ // 確認是否為圖片檔案
            String suffix = url.substring(url.lastIndexOf('.', url.length()));
            URL src = null;
            try {
                src = new URL(url);
            } catch (MalformedURLException e) {
                successful = false;
            }
            URLConnection uri = null;
            try {
                uri = src.openConnection();
            } catch (IOException e) {
                successful = false;
            }
            try {
                InputStream in = uri.getInputStream();
                OutputStream out = new FileOutputStream(new File(
                    (isFull) ? cm.fullSavePath : cm.prevSavePath,
                    String.format("wallpaper%d%s", serialNumber, suffix))
                );
                dump(in, out);
            } catch (IOException e){    
                System.out.println(e.toString());
                successful = false;
            }
        }
        if (!successful && tryTime < 16){ // 遞迴呼叫, 若嘗試 16 次都失敗就放棄
            System.out.printf("Failed to download image, re-trying. Trying time : %d\n", tryTime);
            downloadPicture(cm, serialNumber, url, isFull, tryTime + 1);
        }
        return successful;
    }

    public static boolean isImage(Path filePath){
        return imagePattern.matcher(filePath.getFileName().toString().toLowerCase()).find();
    }

    public static boolean isPngJpg(Path filePath){
        return pjImagePattern.matcher(filePath.getFileName().toString().toLowerCase()).find();
    }

    public static boolean isMusic(Path filePath){
        return musicPattern.matcher(filePath.getFileName().toString().toLowerCase()).find();
    }

    public static boolean quickPing(String target) throws Exception {
        var process = Runtime.getRuntime().exec("cmd /C ping " + target);
        if (process.waitFor() != 0){
            throw new Exception("Bad network connection!");
        }
        return true;
    }

    public static void cmdOpenPath(Path path){
        try {
            var process = Runtime.getRuntime().exec("cmd /C " + "\"" + path.toAbsolutePath() + "\"");
            var reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            var output = new StringBuilder();
            String tmpStr;
            while ((tmpStr = reader.readLine()) != null){
                output.append(tmpStr);
            }
            int exitVal = process.waitFor();
            if (exitVal == 0){
                System.out.println("execute successfully");
                System.out.println(output);
            }
            else {
                System.out.println("execute failed with exit code : " + exitVal);
            }
        } catch (Exception ex){
            ex.printStackTrace();
        }
    }
}
