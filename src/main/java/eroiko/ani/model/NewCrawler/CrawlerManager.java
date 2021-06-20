/*
 * Author : Shiritai (楊子慶, or Eroiko on Github) at 2021/06/19.
 * See https://github.com/Shiritai/wallpaper_master for more information.
 * Created using VSCode.
 */
package eroiko.ani.model.NewCrawler;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;

import eroiko.ani.util.Method.Dumper;
import eroiko.ani.util.MyDS.myQuartet;
import eroiko.ani.util.MyDS.myTriple;
import eroiko.ani.util.NeoWallpaper.Wallpaper;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
/** 管理多線程搜尋 / 下載操作 */
public class CrawlerManager {
    private ArrayList<CrawlerBase> crawlers;
    /* Integer 的存在是為了確保多線程不會出錯 */
    private ArrayList<myQuartet<Integer, Integer, String, String>> wpLinks; // serial number, crawler type, preview link, full link
    private int pages;
    private String [] keywords;
    public final String prevSavePath;
    public final String fullSavePath;
    /* Progress peeking */
    public static volatile DoubleProperty progress = new SimpleDoubleProperty(0.); // 對外更新數據
    private final int tasks; // 對內設定
    private volatile int currentTask; // 對內更新
    private final int tasksForALoop;

    private static final int mulTimes = 4;
    
    /**
     * 爬蟲模組, 管理數個多線程爬蟲的查找, 下載, 歸檔
     * @param folderPath    母目標資料夾, 會在其內建立 tmp 資料夾
     * @param keywords      爬蟲搜索關鍵字
     * @param pages         目標頁數, 每頁都有多張圖
     */
    public CrawlerManager(String folderPath, String [] keywords, int pages){
        progress.set(0.);
        fullSavePath = folderPath + "\\" + String.join(" ", keywords);
        prevSavePath = fullSavePath + "\\previews";
        this.keywords = keywords;
        this.crawlers = CrawlersGenerator();
        /* 確認關鍵字無誤後, [新建 / 確認] 資料夾 */
        File outRoot = new File(folderPath); // 確認目標地址存在
        folderPath += "\\" + String.join(" ", keywords);
        File outFull = new File(fullSavePath); // 確認目標地址存在
        File outPrev = new File(prevSavePath); // 確認目標地址存在
        if (!outRoot.exists()){ // 不存在則建立之
            outRoot.mkdirs();
        }
        else {
            // 未來擴充, 讀取檔案, 得到當前 wallpaper 索引值
        }
        if (!outFull.exists()){ // 不存在則建立之
            outFull.mkdirs();
        }
        else {
            // 未來擴充, 讀取檔案, 得到當前 wallpaper 索引值
        }
        if (!outPrev.exists()){ // 不存在則建立之
            outPrev.mkdirs();
        }
        else {
            // 未來擴充, 讀取檔案, 得到當前 wallpaper 索引值
        }
        wpLinks = new ArrayList<>();
        this.pages = pages;
        currentTask = 0;
        tasksForALoop = CrawlerManager.tasksForEachLoop(this);
        tasks = this.Z_getProcessElementsNumber();
        System.out.printf("tasks : %d\n", tasks);
    }

    private ArrayList<CrawlerBase> CrawlersGenerator(){
        var res = new ArrayList<CrawlerBase>();
        for (int i = 1; i <= CrawlerBase.CRAWLER_SUPPORT_NUMBERS; ++i){
            CrawlerBase e;
            if ((e = CrawlerBase.WalkThroughCrawlers(keywords, i)) != null){
                res.add(e);
            }
        }
        if (res.size() > 0){
            return res;
        }
        return null;
    }

    private static int tasksForEachLoop(CrawlerManager cm){
        int res = 0;
        for (var c : cm.crawlers){
            if (CrawlerBase.isMultiThreadable(c)){
                res += mulTimes;
            }
            else {
                res += 1;
            }
        }
        return res;
    }
    
    public static boolean checkValidation(String keyword){
        CrawlerBase [] tmp = new CrawlerBase [] {new CrawlerZeroChan(), new CrawlerWallhaven(), };
        for (CrawlerBase t : tmp){
            if (t.isValidKeyword(keyword)){
                return true;
            }
        }
        return false;
    }

    public int Z_getProcessElementsNumber(){
        int res = 0;
        for (var c : crawlers){
            if (CrawlerBase.isMultiThreadable(c)){
                res += c.numberOfImageInPages(pages * mulTimes);
            }
            else {
                res += c.numberOfImageInPages(pages);
            }
        }
        return res;
    }

    public void A_getLinks(){
        var service = Executors.newCachedThreadPool();
        /** 批次請求不同網站 */
        int numOfCrawler = crawlers.size();
        var container = new ArrayList<ArrayList<myTriple<Integer, String, String>>>(tasksForALoop * 30); // 爬蟲數量的暫存容器
        for (int i = 1; i <= pages; ++i){ // 廣義上的 Pages, 對不支持多線程者就是字面上的數值, 對支持多線程者而言是 mulTimes 倍
            // /* 為了避免並行碰撞, 一次性進行擴容 */
            var tmpArr = new ArrayList<myQuartet<Integer, Integer, String, String>>(wpLinks.size() + tasksForALoop * 30);
            wpLinks.forEach(mp -> tmpArr.add(mp));
            wpLinks = tmpArr;
            /* 蒐集 callable */
            var calls = new ArrayList<Callable<Boolean>>(tasksForALoop * 30);
            int thisPage = i;
            for (int k = 0; k < numOfCrawler; ++k){
                var cw = crawlers.get(k);
                /* 支援多線程 */
                if (CrawlerBase.isMultiThreadable(cw)){
                    for (int mul = 0; mul < mulTimes; ++mul){
                        int m = mul;
                        calls.add(() -> {
                            container.add(cw.fetchImageLinks(thisPage * mulTimes + m - 3, service));
                            return true;
                        });
                    }
                }
                /* 不支援多線程 */
                else {
                    calls.add(() -> {
                        container.add(cw.fetchImageLinks(thisPage, service));
                        return true;
                    });
                }
            }
            /* invoke! */
            try {
                service.invokeAll(calls);
                /* 因為有 iterator is null 的 bug, 改用傳統迴圈 */
                int size = container.size();
                for (int x = 0; x < size; ++x){
                    var tmp = container.get(x);
                    while (tmp == null);
                    int tmpSize = tmp.size();
                    for (int y = 0; y < tmpSize; ++y){
                        wpLinks.add(new myQuartet<Integer, Integer, String, String>(wpLinks.size() + 1, tmp.get(y)));
                    }
                }
                container.clear();
            } catch (InterruptedException ie){
                // System.out.println(ie.toString());
                service.shutdownNow();
            }
        }
        service.shutdown();
    }

    public void print(){
        for (var wp : wpLinks){
            System.out.printf("%d %d %s %s\n", wp.first, wp.second, wp.third, wp.fourth);
        }
    }
    
    /** 下載預覽圖 */
    public void B_download(){
        var service = Executors.newCachedThreadPool();
        /* 蒐集 callable */
        var calls = new ArrayList<Callable<Boolean>>();
        for (int i = 0; i < wpLinks.size(); ++i){
            int thisIndex = i;
            var tmp = wpLinks.get(thisIndex);
            if (tmp.third == null || tmp.fourth == null){ // 避免 Exception
                continue;
            }
            calls.add(() -> {
                (new Dumper()).downloadPicture(this, tmp.first, tmp.third, false);
                return true;
            });
        }
        try {
            service.invokeAll(calls);
        } catch (InterruptedException e) {
            service.shutdownNow();
            // System.out.println(e.toString());
        }
        service.shutdown();
    }

    /** 下載所有完全圖 */
    public void D_lastDownloadStage(){
        currentTask = 0; // init progress
        progress.set((currentTask * 1.) / tasks);
        var service = Executors.newCachedThreadPool();
        var calls = new ArrayList<Callable<Boolean>>(wpLinks.size());
        /* Collect multi-thread supported crawlers */
        for (var wp : wpLinks){
            if (wp.first != 0){
                if (wp.second == null || wp.third == null){
                    continue;
                }
                if (wp.second == CrawlerBase.CRAWLER_ZEROCHAN){ // 支援多線程下載
                    calls.add(() -> {
                        (new Dumper()).downloadPicture(this, wp.first, wp.fourth, true);
                        ++currentTask;
                        progress.set((currentTask * 1.) / tasks);
                        return true;
                    });
                }
            }
        }
        try {
            service.invokeAll(calls);
        } catch (InterruptedException e) {
            service.shutdownNow();
            // System.out.println(e.toString());
        }
        /* Do multi-thread-unsupported crawlers later */
        for (var wp : wpLinks){
            if (wp.first != 0){
                if (wp.second == null || wp.third == null){
                    continue;
                }
                if (wp.second == CrawlerBase.CRAWLER_WALLHAVEN){ // 不支援多線程下載
                    (new Dumper()).downloadPicture(this, wp.first, wp.fourth, true);
                    ++currentTask;
                    progress.set((currentTask * 1.) / tasks);
                }
            }
        }
        service.shutdown();
        while (!service.isShutdown()); // 等待關閉
    }

    public void E_pushWallpaper(){
        progress.set(1.);
        System.out.println(Path.of(this.fullSavePath));
        try {
            var tmp = new Wallpaper(Path.of(this.fullSavePath));
            Wallpaper.addNewWallpaper(tmp);
        } catch (Exception e){
            e.printStackTrace();
            System.out.println(e.toString());
        }
    }
}