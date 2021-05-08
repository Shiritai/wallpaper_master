package eroiko.ani.model.NewCrawler;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;


import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import eroiko.ani.controller.MainController;
import eroiko.ani.util.TimeWait;
import eroiko.ani.util.myTriple;
import eroiko.ani.util.WallpaperClass.WallpaperComparator;

// example : https://wallhaven.cc/search?q=Girls%27%20last%20tour&page=2
public class CrawlerWallhaven extends CrawlerBase {

    public static final String url_head = "https://wallhaven.cc/";
    /* Advance selection may implement int the future... */
    // public static final String [] select_header = {"categories=", "purity=", "atleast=", "ratios=", "sorting=", "order=", "colors="};
    // public static final String [][] select_content = {null, null, };
    public CrawlerWallhaven(String [] keywords) throws IOException {
        CRAWLER_TYPE = CRAWLER_WALLHAVEN;

        this.query = String.join("+", keywords);
        var tmp = new StringBuilder();
        tmp.append(CrawlerWallhaven.url_head).append("search?q=").append(this.query);
        this.first_layer_url = tmp.toString();
        /* 確認關鍵字無誤 */
        var doc = Jsoup.connect(this.first_layer_url)
            .userAgent(CrawlerBase.UserAgent)
            .timeout(10000)
            .get();
        System.out.println("Wallhaven : " + doc.title()); // 印出標頭, 確保目標正確
        if (!MainController.quit){
            System.err.println("Wallhaven : " + doc.title()); // 印出標頭, 確保目標正確
        }
    }

    @Override
    public ArrayList<myTriple<Integer, String, String>> fetchImageLinks(int page, ExecutorService service) {
        try {
            System.out.println(this.first_layer_url + "&page=" + Integer.toString(page));
            var doc = Jsoup.connect(this.first_layer_url + "&page=" + Integer.toString(page))
                .userAgent(CrawlerBase.UserAgent)
                .timeout(10000)
                .get();
            System.out.println(doc.title()); // 印出標頭, 確保目標正確
            if (!MainController.quit){
                System.err.println(doc.title()); // 印出標頭, 確保目標正確
            }
    
            Elements links = doc.select("img[data-src]"); // 抓取預覽圖, 預覽圖都有 title
            Elements target = doc.select("a[class=preview][target=_blank]");

            var size = links.size();
            var data = new ArrayList<myTriple<Integer, String, String>>(size);

            String [] prevLinks = new String [size];
            for (int i = 0; i < size; ++i){
                prevLinks[i] = links.get(i).attr("data-src");
            }
            
            int span = 2;
            String [] fullLinks = new String [size];
            var calls = new ArrayList<Callable<Boolean>>(span);
            for (int i = 0; i < size; i += span){
                for (int j = 0; j < span && i + j < size; ++j){
                    int thisIndex = i + j;
                    calls.add(() -> {
                        fullLinks[thisIndex] = fetchFullLink(target.get(thisIndex).attr("href"));
                        return true;
                    });
                }
                var status = service.invokeAll(calls);
                int sizeOfStatus = status.size();
                for (int j = 0; j < sizeOfStatus; ++j){
                    if (!status.get(j).isDone()){
                        --j;
                    }
                }
                calls.clear();
            }
            for (int i = 0; i < size; ++i){
                fullLinks[i] = fetchFullLink(target.get(i).attr("href"));
            }
            /* 可以火力全開 */
            calls = new ArrayList<Callable<Boolean>>(size);
            for (int i = 0; i < size; ++i){
                int thisIndex = i;
                calls.add(() -> {
                    data.add(new myTriple<Integer, String, String>(CRAWLER_WALLHAVEN, prevLinks[thisIndex], fullLinks[thisIndex]));
                    return true;
                });
            }
            var status = service.invokeAll(calls);
            int sizeOfStatus = status.size();
            for (int i = 0; i < sizeOfStatus; ++i){
                if (!status.get(i).isDone()){
                    --i;
                }
            }
            return data;
        } catch (Exception e){
            System.out.println(e.toString());
        }
        return null;
    }

    private String fetchFullLink(String url){
        try {
            new TimeWait(4000);
            Document doc = Jsoup.connect(url)
                .userAgent(CrawlerBase.UserAgent)
                .timeout(10000)
                .get();
            System.out.printf(">> ");
            System.out.println(doc.title()); // just for sure!
            if (!MainController.quit){
                System.err.println(doc.title()); // just for sure!
            }
            return doc.select("img[id=wallpaper]").first().attr("src");
        } catch (NullPointerException ne){
            return null; // 避免讀空後進入無窮遞迴
        } catch (Exception e){
            System.out.println(e.toString());
            System.out.println("Try re-request...");
            new TimeWait(2000);
            return fetchFullLink(url);
        }
    }

    public CrawlerWallhaven(){ CRAWLER_TYPE = CRAWLER_WALLHAVEN; } // for validation check

    @Override
    public boolean isValidKeyword(String keyword) {
        var tmp = new StringBuilder();
        this.first_layer_url = tmp.append(CrawlerWallhaven.url_head).append("search?q=").append(keyword.replace(' ', '+')).toString();
        /* 確認關鍵字無誤 */
        try {
            Document doc = Jsoup.connect(tmp.toString())
                .userAgent(CrawlerBase.UserAgent)
                .timeout(10000)
                .get();
            if (WallpaperComparator.hasSubstring(doc.title(), "null")){
                return false;
            }
            else if (WallpaperComparator.hasSubstring(doc.select("h1").text(), "0 Wallpapers found")){
                return false;
            }
            System.out.println("Wallhaven : " + doc.title()); // 印出標頭, 確保目標正確
            if (!MainController.quit){
                System.err.println("Wallhaven : " + doc.title()); // 印出標頭, 確保目標正確
            }
        } catch (SocketTimeoutException ie){
            return isValidKeyword(keyword);
        } catch (IOException ie){
            return false;
        }
        return true;
    }

    @Override
    public int numberOfImageInPages(int page) {
        int res = 0;
        try {
            for (int i = 1; i <= page; ++i){
                var doc = Jsoup.connect(this.first_layer_url + "&page=" + Integer.toString(i))
                    .userAgent(CrawlerBase.UserAgent)
                    .timeout(10000)
                    .get();
                res += doc.select("img[data-src]").size();
            }
        } catch (IOException ie){
            System.out.println(ie.toString());
        }
        return res;
    }
}
