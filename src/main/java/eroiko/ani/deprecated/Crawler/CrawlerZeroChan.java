package eroiko.ani.deprecated.Crawler;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import eroiko.ani.controller.MainController;
import eroiko.ani.util.MyDS.myPair;

/** {@code <deprecated>} */
@Deprecated
public class CrawlerZeroChan extends Crawler{
    
    /* 測試下載 number 數量的圖需要訪問多少頁面 */
    public int howManyPagesShouldRead(int number){
        int cnt = 0;
        int page = 0;
        while (cnt < number){
            try {
                var doc = Jsoup.connect(this.first_layer_url + "&p=" + Integer.toString(++page))
                    .userAgent(Crawler.UserAgent)
                    .timeout(10000)
                    .get();
                cnt += doc.select("img[title]").size();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return page;
    }

    /** service : 請給定線程執行池 */
    public ArrayList<ArrayList<myPair<String, String>>> readMultiplePagesAndDownloadPreviews(int pages, ExecutorService service){
        var res = new ArrayList<ArrayList<myPair<String, String>>>(pages);
        final int span = 16;
        for (int h = 1; h < pages; h += span){
            var calls = new ArrayList<Callable<Boolean>>(span);
            /* 蒐集 Callable */
            for (int i = h; i < h + span && i <= pages; ++i){
                int page = i;
                calls.add(() -> res.add(downloadPreviewAndFetchFullImageLink(page, service)));
            }
            /* 全部運行 */
            try {
                service.invokeAll(calls);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println(res.size());
        if (!MainController.quit){
            System.err.println(res.size());
        }
        return res;
    }

    /* 讀取特定頁面 */
    public ArrayList<myPair<String, String>> downloadPreviewAndFetchFullImageLink(int page, ExecutorService service){
        try {
            var doc = Jsoup.connect(this.first_layer_url + "&p=" + Integer.toString(page))
                .userAgent(Crawler.UserAgent)
                .timeout(10000)
                .get();
            System.out.println(doc.title()); // 印出標頭, 確保目標正確
            if (!MainController.quit){
                System.err.println(doc.title()); // 印出標頭, 確保目標正確
            }

            Elements links = doc.select("img[title]"); // 抓取預覽圖, 預覽圖都有 title
            Elements target = doc.select("a[tabindex=1]");

            var size = links.size();
            var data = new ArrayList<myPair<String, String>>(size);
            
            for (int i = 0; i < size; ++i){
                data.add(new myPair<String, String>(links.get(i).attr("src"), CrawlerZeroChan.url_head + target.get(i).attr("href") + "#full"));
            }

            var calls = new ArrayList<Callable<Boolean>>(size);
            for (var d : data){
                calls.add(() -> this.downloadPicture(d.key, false));
            }
            var status = service.invokeAll(calls);
            for (int i = 0; i < status.size(); ++i){
                if (!status.get(i).isDone()){
                    i--;
                }
            }
            return data;
        } catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public void downloadSelectedImagesUsingPAIRs(ArrayList<ArrayList<myPair<String, String>>> data, ExecutorService service){
        for (var d : data){
            this.downloadSelectedImagesUsingPAIR(d, service);
        }
    }

    public void downloadSelectedImagesUsingPAIR(ArrayList<myPair<String, String>> data, ExecutorService service){
        var calls = new ArrayList<Callable<Boolean>>(data.size());
        for (var str : data){
            String strV = str.value;
            calls.add(
                () -> {
                    var res = false;
                    try {
                        var doc = Jsoup.connect(strV)
                            .userAgent(Crawler.UserAgent)
                            .timeout(10000)
                            .get();
                        // System.out.println(doc.title()); // just for sure!
                        if (!MainController.quit){
                            // System.out.println(doc.title()); // just for sure!
                        }
                        Element target = doc.select("a[class=preview]").first();
                        if (target != null){
                            res = this.downloadPicture(target.attr("href"), true);
                        }
                        else {
                            System.out.println("There is a null full image with url : " + str);
                            if (!MainController.quit){
                                System.err.println("There is a null full image with url : " + str);
                            }
                        }
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                    return res;
                }
            );
        }
        try {
            service.invokeAll(calls);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static final String url_head = "https://www.zerochan.net/";
    public static final String [] select_header = {"d=", "s="};
    public static final String [][] select_content = {{"0", "1", "2"}, {"id", "fav", "random"}};
    private int [] select = new int [2];
    
    public CrawlerZeroChan(String folder_path, String [] keywords, int resolution, int sorting) throws IOException{
        this.query = String.join("+", keywords);
        this.select[0] = resolution;
        this.select[1] = sorting;
        var tmp = new StringBuilder();
        tmp.append(CrawlerZeroChan.url_head).append(this.query).append('?')
            .append(CrawlerZeroChan.select_header[0]).append(CrawlerZeroChan.select_content[0][this.select[0]])
            .append('&').append(CrawlerZeroChan.select_header[1]).append(CrawlerZeroChan.select_content[1][this.select[1]]);
        this.first_layer_url = tmp.toString();

        /* 確認關鍵字無誤 */
        var doc = Jsoup.connect(this.first_layer_url)
            .userAgent(Crawler.UserAgent)
            .timeout(10000)
            .get();
        System.out.println(doc.title()); // 印出標頭, 確保目標正確
        if (!MainController.quit){
            System.err.println(doc.title()); // 印出標頭, 確保目標正確
        }
        /* 確認關鍵字無誤後, [新建 / 確認] 資料夾 */
        this.folder_path = folder_path;
        File outRoot = new File(this.folder_path); // 確認目標地址存在
        this.folder_path += "/" + String.join(" ", keywords);
        File outFull = new File(this.folder_path); // 確認目標地址存在
        File outPrev = new File(this.folder_path + "/previews"); // 確認目標地址存在
        if (!outRoot.exists()){ // 不存在則建立之
            outRoot.mkdirs();
        }
        if (!outFull.exists()){ // 不存在則建立之
            outFull.mkdirs();
        }
        if (!outPrev.exists()){ // 不存在則建立之
            outPrev.mkdirs();
        }
    }

    public CrawlerZeroChan(String folder_path, String [] keywords) throws IOException{
        this(folder_path, keywords, 2, 1);
    }

    /** {0, 1, 2}, {id, fav, random} */
    public void setFirstLayerUrl(int resolution, int sorting) {
        this.select[0] = resolution;
        this.select[1] = sorting;
        var tmp = new StringBuilder();
        tmp.append(CrawlerZeroChan.url_head).append(this.query).append('?')
            .append(CrawlerZeroChan.select_header[0]).append(CrawlerZeroChan.select_content[0][this.select[0]])
            .append('&').append(CrawlerZeroChan.select_header[1]).append(CrawlerZeroChan.select_content[1][this.select[1]]);
        this.first_layer_url = tmp.toString();
    }

    public void setQuery(String [] keywords){
        this.query = String.join("+", keywords);
    }
    
    public String getFolderPath(){ return this.folder_path; }
    public String getPreviewsFolderPath(){ return this.folder_path + "/previews"; }
    public String getQuery(){ return this.query; }
    public String getFirstLayerUrl(){ return this.first_layer_url; }
}
