package eroiko.ani.model.NewCrawler;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import eroiko.ani.controller.MainController;
import eroiko.ani.util.Dumper;
import eroiko.ani.util.SourceRedirector;
import eroiko.ani.util.myPair;
// import eroiko.ani.util.ProgressProperty;
import eroiko.ani.util.myTriple;
import eroiko.ani.util.WallpaperClass.WallpaperImageWithFilter;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
/** 管理多線程搜尋 / 過濾 / 下載操作 */
public class CrawlerManager {
    // private ProgressProperty prog = new ProgressProperty(Types.CRAWLER_ZERO_CHAN);
    private CrawlerBase [] crawlers;
    /* Integer 的存在是為了確保多線程不會出錯 */
    private ArrayList<myTriple<Integer, String, String>> wpLinks;
    private int currentPage;
    // private int previousPage;
    private int currentSize;
    private int previousSize;
    private int previousSizeForPreview;
    private int pages;
    // public int prevCnt = 0; // 紀錄 Preview 編號
    // public int fullCnt = 0; // 紀錄 Full image 編號
    public final String prevSavePath;
    public final String fullSavePath;
    /* Progress peeking */
    public static DoubleProperty progress = new SimpleDoubleProperty(0.); // 對外更新數據
    private final int tasks; // 對內設定
    private int currentTask; // 對內更新
    
    public CrawlerManager(String folderPath, String [] keywords, int pages){
        fullSavePath = folderPath + "\\" + String.join(" ", keywords);
        prevSavePath = fullSavePath + "\\previews";
        this.crawlers = CrawlersGenerator(keywords);
        /* 確認關鍵字無誤後, [新建 / 確認] 資料夾 */
        File outRoot = new File(folderPath); // 確認目標地址存在
        folderPath += "\\" + String.join(" ", keywords);
        File outFull = new File(fullSavePath); // 確認目標地址存在
        File outPrev = new File(prevSavePath); // 確認目標地址存在
        if (!outRoot.exists()){ // 不存在則建立之
            outRoot.mkdirs();
        }
        else {
            // 讀取檔案, 得到當前 wallpaper 索引值
        }
        if (!outFull.exists()){ // 不存在則建立之
            outFull.mkdirs();
        }
        else {
            // 讀取檔案, 得到當前 wallpaper 索引值
        }
        if (!outPrev.exists()){ // 不存在則建立之
            outPrev.mkdirs();
        }
        else {
            // 讀取檔案, 得到當前 wallpaper 索引值
        }
        wpLinks = new ArrayList<>();
        this.pages = pages;
        currentPage = 1;
        // previousPage = 1;
        currentSize = 0;
        previousSize = 0;
        previousSizeForPreview = 0;
        tasks = this.Z_getProcessElementsNumber() * crawlers.length;
        System.out.printf("tasks : %d\n", tasks);
    }

    private CrawlerBase [] CrawlersGenerator(String [] keywords){
        try {
            return new CrawlerBase [] {new CrawlerZeroChan(keywords), };
        } catch (IOException e) {
            System.out.println(e.toString());
        }
        return null;
    }

    public static boolean checkValidation(String keyword){
        try {
            var tmp = new CrawlerBase [] {new CrawlerZeroChan(keyword.split(" ")), };
            for (CrawlerBase t : tmp){
                if (t.isValidKeyword(keyword)){
                    return true;
                }
            }
        } catch (IOException e) {
            System.out.println(e.toString());
        }
        return false;
    }

    public int Z_getProcessElementsNumber(){
        int res = 0;
        for (var c : crawlers){
            res += c.numberOfImageInPages(pages);
        }
        return res;
    }

    /** @param times {@code two pages} a time, 傳入批次頁面的次數數, 單一 Crawler 每次 2 頁, 欲支援滾動下載 */
    public void A_getLinks(){
        currentTask = 0; // init progress
        var service = Executors.newCachedThreadPool();
        /** 批次請求不同網站 */
        int expandSize = 128 * crawlers.length;
        // previousPage = currentPage;
        previousSize = currentSize;
        int span = 2; // 一次兩頁
        var container = new ArrayList<ArrayList<myPair<String, String>>>(crawlers.length); // 爬蟲數量的暫存容器
        for (int i = 0; i < pages >> 1; ++i){ // 次數
            // /* 為了避免並行碰撞, 一次性進行擴容 */
            var tmpArr = new ArrayList<myTriple<Integer, String, String>>(wpLinks.size() + expandSize);
            wpLinks.forEach(mp -> tmpArr.add(mp));
            wpLinks = tmpArr;
            for (int j = currentPage; j < currentPage + span; ++j){
                previousSize = currentSize;
                /* 蒐集 callable */
                var calls = new ArrayList<Callable<Boolean>>(4);
                int thisPage = j;
                for (int k = 0; k < crawlers.length; ++k){
                    int thisInd = k;
                    calls.add(() -> {
                        container.add(crawlers[thisInd].fetchImageLinks(thisPage, service));
                        ++currentTask;
                        progress.set((currentTask * 1.) / tasks);
                        System.out.printf("Refresh progress : %f\n", progress.get());
                        return true;
                    });
                }
                /* invoke! */
                try {
                    var status = service.invokeAll(calls);
                    int sizeOfStatus = status.size();
                    for (int h = 0; h < sizeOfStatus; ++h){
                        if (!status.get(h).isDone()){
                            h -= 2;
                        }
                    }
                    int curSerialNumber = previousSize;
                    // for (var ctn : container){
                    //     for (var c : ctn){
                    //         wpLinks.add(new myTriple<>(++curSerialNumber, c));
                    //     }
                    // }
                    /* 以上註解掉的有 iterator is null 的 bug, 改用傳統迴圈 */
                    int size = container.size();
                    for (int x = 0; x < size; ++x){
                        var tmp = container.get(x);
                        while (tmp == null);
                        int tmpSize = tmp.size();
                        for (int y = 0; y < tmpSize; ++y){
                            wpLinks.add(new myTriple<>(++curSerialNumber, tmp.get(y)));
                            ++currentTask;
                            progress.set((currentTask * 1.) / tasks);
                        }
                    }
                    container.clear();
                    currentSize = curSerialNumber;
                } catch (InterruptedException ie){
                    System.out.println(ie.toString());
                }
            }
            currentPage += span;
        }
        service.shutdown();
    }

    public void print(){
        for (var wp : wpLinks){
            System.out.printf("%d %s %s\n", wp.first, wp.second, wp.third);
        }
    }
    
    /** 下載累積的預覽圖 */
    public void B_download(){
        currentTask = 0; // init progress, 剛好先不刷新 progress
        var service = Executors.newCachedThreadPool();
        /* 蒐集 callable */
        var calls = new ArrayList<Callable<Boolean>>();
        for (int i = previousSizeForPreview; i < wpLinks.size(); ++i){
            int thisIndex = i;
            calls.add(() -> {
                var tmp = wpLinks.get(thisIndex);
                (new Dumper()).downloadPicture(this, tmp.first, tmp.second, false);
                ++currentTask;
                progress.set((currentTask * 1.) / tasks);
                return true;
            });
        }
        previousSizeForPreview = currentSize;
        List<Future<Boolean>> status = null;
        try {
            status = service.invokeAll(calls);
        } catch (InterruptedException e) {
            System.out.println(e.toString());
        }
        /* 等待結束 */
        int sizeOfStatus = status.size();
        for (int h = 0; h < sizeOfStatus; ++h){
            if (!status.get(h).isDone()){
                h--;
            }
        }
        service.shutdown();
        MainController.hasFull = false;
    }
    public void C_openWallpaperFilterViewer(){
        try {
            SourceRedirector.wallpaperImageWithFilter = new WallpaperImageWithFilter(this.prevSavePath, false);
            MainController.preview = new WallpaperImageWithFilter(this.prevSavePath, false);
            MainController.hasChangedPreview.set(true);
            MainController.staticImagePreview.setImage(MainController.preview.getCurrentWallpaper());
        } catch (Exception e){
            // System.out.println(e.toString());
            e.printStackTrace();
            if (!SourceRedirector.quit){
                System.err.println(e.toString());
            }
        }
    }
    /** 與 WallpaperImageFilter 一同協作, 搭配圖形介面 WallpaperChooseController */
    public void C_deletePictureChoose(int serialNumber){
        wpLinks.get(serialNumber).first = 0;
    }
    /** 與 WallpaperImageFilter 一同協作, 搭配圖形介面 WallpaperChooseController */
    public void C_recoverPictureChoose(int serialNumber){
        wpLinks.get(serialNumber).first = serialNumber;
    }
    /** 下載所有完全圖 */
    public void D_lastDownloadStage(){
        var service = Executors.newCachedThreadPool();
        var calls = new ArrayList<Callable<Boolean>>(wpLinks.size());
        for (var wp : wpLinks){
            if (wp.first != 0){
                calls.add(() -> {
                    var tmp = wp;
                    (new Dumper()).downloadPicture(this, tmp.first, tmp.third, true);
                    return true;
                });
            }
        }
        try {
            var status = service.invokeAll(calls);
            /* 等待結束 */
            int sizeOfStatus = status.size();
            for (int h = 0; h < sizeOfStatus; ++h){
                if (!status.get(h).isDone()){
                    h--;
                }
            }
        } catch (InterruptedException e) {
            System.out.println(e.toString());
        }
        service.shutdown();
    }
    public void E_openFullWallpaperFilterViewer(){
        System.out.println("Change to view full!");
        try {
            MainController.hasFull = true;
            SourceRedirector.wallpaperImageWithFilter = new WallpaperImageWithFilter(this.fullSavePath, false);
        } catch (Exception e){
            e.printStackTrace();
            if (!SourceRedirector.quit){
                System.err.println(e.toString());
            }
        }
    }
}