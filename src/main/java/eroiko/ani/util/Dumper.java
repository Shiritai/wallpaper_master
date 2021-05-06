package eroiko.ani.util;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.regex.Pattern;

import eroiko.ani.model.NewCrawler.CrawlerManager;

public class Dumper {
    public static Pattern imagePattern = Pattern.compile(".*?jpe?g|png|gif$");

    /* 負責讀資料 + 寫檔案 */
    public void dump(InputStream in, OutputStream out) throws IOException {
        try (var input = new BufferedInputStream(in); 
        var output = new BufferedOutputStream(out)){ // try auto close
            var data = new byte[16384];
            var length = 0;
            while ((length = input.read(data)) != -1){
                output.write(data, 0, length);
            }
        }
    }

    public void dump(InputStream in, OutputStream out, int approximateSize) throws IOException {
        try (var input = new BufferedInputStream(in); 
        var output = new BufferedOutputStream(out)){ // try auto close
            var data = new byte[approximateSize]; // 可以設定大小
            var length = 0;
            while ((length = input.read(data)) != 0){
                output.write(data, 0, length);
            }
        }
    }

    public void dump(Reader in, Writer out){
        try (in; out){
            var data = new char[1024];
            int length = 0;
            while ((length = in.read(data)) != 0){
                out.write(data, 0, length);
            }
        } catch (IOException ie){
            ie.printStackTrace();
        }
    }

    public boolean downloadPicture(CrawlerManager cm, int serialNumber, String url, boolean isFull){
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
                // System.out.println("IO failed!");
                successful = false;
            }
        }
        return successful;
    }
}
