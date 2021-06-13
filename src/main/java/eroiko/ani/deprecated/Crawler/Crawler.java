/*
 * Author : Shiritai (楊子慶, or Eroiko on Github) at 2021/06/13.
 * See https://github.com/Shiritai/wallpaper_master for more information.
 * Created using VSCode.
 */
package eroiko.ani.deprecated.Crawler;

import java.io.*;
import java.net.*;
import java.util.regex.Pattern;

import eroiko.ani.util.Method.Dumper;

/** {@code <deprecated>} */
@Deprecated
public class Crawler {
    protected final static String UserAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:87.0) Gecko/20100101 Firefox/87.0";
    protected String folder_path;
    protected String query;
    protected String first_layer_url;
    protected static int prevCnt = 1;
    protected static int fullCnt = 1;

    public boolean downloadPicture(String url, boolean isFull){
        var successful = true;
        if (Pattern.matches(".*?jpe?g|png|gif$", url)){ // 確認是否為圖片檔案
            String suffix = url.substring(url.lastIndexOf('.', url.length()));
            URL src = null;
            try {
                src = new URL(url);
            } catch (MalformedURLException e) {
                // System.out.println("The url was malformed!");
                successful = false;
            }
            URLConnection uri = null;
            try {
                uri = src.openConnection();
            } catch (IOException e) {
                // System.out.println("Connection " + url + " failed!");
                successful = false;
            }
            try {
                InputStream in = uri.getInputStream();
                OutputStream out = new FileOutputStream(new File(
                    (isFull) ? this.folder_path : this.folder_path + "/previews",
                    String.format("wallpaper%d%s", (isFull) ? Crawler.fullCnt++ : Crawler.prevCnt++, suffix))
                    // uri.toString())
                );
                Dumper.dump(in, out);
            } catch (IOException e){    
                System.out.println("IO failed!");
                successful = false;
            }
            // if (successful){
            //     System.out.printf("%s\t", (isFull) ? "f" + Integer.toString(Crawler.fullCnt - 1) : "p" + Integer.toString(Crawler.prevCnt - 1));
            //     if (!MainController.quit){
            //         System.err.printf("%s\t", (isFull) ? "f" + Integer.toString(Crawler.fullCnt - 1) : "p" + Integer.toString(Crawler.prevCnt - 1));
            //     }
            // }
        }
        return successful;
    }
}
