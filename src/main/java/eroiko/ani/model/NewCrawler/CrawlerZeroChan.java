package eroiko.ani.model.NewCrawler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;

import org.jsoup.Jsoup;
import org.jsoup.select.Elements;

// import eroiko.ani.controller.MainController;
import eroiko.ani.util.myPair;


public class CrawlerZeroChan extends CrawlerBase{

    @Override
    public ArrayList<myPair<String, String>> fetchImageLinks(int page, ExecutorService service) {
        try {
            System.out.println(this.first_layer_url + "&p=" + Integer.toString(page));
            var doc = Jsoup.connect(this.first_layer_url + "&p=" + Integer.toString(page))
                .userAgent(CrawlerBase.UserAgent)
                .timeout(10000)
                .get();
            System.out.println(doc.title()); // 印出標頭, 確保目標正確
            // if (!MainController.quit){
            //     System.err.println(doc.title()); // 印出標頭, 確保目標正確
            // }
    
            Elements links = doc.select("img[title]"); // 抓取預覽圖, 預覽圖都有 title
            Elements target = doc.select("a[tabindex=1]");

            var size = links.size();
            var data = new ArrayList<myPair<String, String>>(size);

            String [] prevLinks = new String [size];
            for (int i = 0; i < size; ++i){
                prevLinks[i] = links.get(i).attr("src");
            }
            
            int span = 4;
            String [] fullLinks = new String [size];
            var calls = new ArrayList<Callable<Boolean>>(span);
            for (int i = 0; i < size; i += span){
                for (int j = 0; j < span && i + j < size; ++j){
                    int thisIndex = i + j;
                    calls.add(() -> {
                        fullLinks[thisIndex] = fetchFullLink(CrawlerZeroChan.url_head + target.get(thisIndex).attr("href") + "#full");
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
            /* 可以火力全開 */
            calls = new ArrayList<Callable<Boolean>>(size);
            for (int i = 0; i < size; ++i){
                int thisIndex = i;
                calls.add(() -> {
                    data.add(new myPair<String, String>(prevLinks[thisIndex], fullLinks[thisIndex]));
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
            var doc = Jsoup.connect(url)
                .userAgent(CrawlerBase.UserAgent)
                .timeout(10000)
                .get();
            System.out.println(doc.title()); // just for sure!
            // if (!MainController.quit){
            //     System.err.println(doc.title()); // just for sure!
            // }
            return doc.select("a[class=preview]").first().attr("href");
        } catch (Exception e){
            System.out.println(e.toString());
            System.out.println("Try re-request...");
            return fetchFullLink(url);
        }
    }

    public static final String url_head = "https://www.zerochan.net/";
    public static final String [] select_header = {"d=", "s="};
    public static final String [][] select_content = {{"0", "1", "2"}, {"id", "fav", "random"}};

    private static int _setting_resolution = 2;
    private static int _setting_sorting = 1;

    private int [] select = new int [2];
    
    public CrawlerZeroChan(String [] keywords) throws IOException{
        this.query = String.join("+", keywords);
        this.select[0] = _setting_resolution;
        this.select[1] = _setting_sorting;
        var tmp = new StringBuilder();
        tmp.append(CrawlerZeroChan.url_head).append(this.query).append('?')
            .append(CrawlerZeroChan.select_header[0]).append(CrawlerZeroChan.select_content[0][this.select[0]])
            .append('&').append(CrawlerZeroChan.select_header[1]).append(CrawlerZeroChan.select_content[1][this.select[1]]);
        this.first_layer_url = tmp.toString();
        /* 確認關鍵字無誤 */
        var doc = Jsoup.connect(this.first_layer_url)
            .userAgent(CrawlerBase.UserAgent)
            .timeout(10000)
            .get();
        System.out.println("Zero Chan : " + doc.title()); // 印出標頭, 確保目標正確
        // if (!MainController.quit){
        //     System.err.println("Zero Chan : " + doc.title()); // 印出標頭, 確保目標正確
        // }
    }

    /** 設定畫質, 2 (僅高) ~ 0 (高低都有) */
    public void setDefaultResolution(int res){
        _setting_resolution = res;
    }
    /** 設定排序, 0 {@code by id}, 1 {@code by popularity}, 2 {@code random} */
    public void setDefaultSorting(int sorting){
        _setting_sorting = sorting;
    }
}
