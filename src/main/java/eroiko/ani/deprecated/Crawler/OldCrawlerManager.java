/*
 * Author : Shiritai (楊子慶, or Eroiko on Github) at 2021/06/13.
 * See https://github.com/Shiritai/wallpaper_master for more information.
 * Created using VSCode.
 */
package eroiko.ani.deprecated.Crawler;

import java.io.IOException;
import java.util.concurrent.Executors;

import eroiko.ani.controller.MainController;
import eroiko.ani.util.NeoWallpaper.WallpaperPath;

/** {@code <deprecated>} */
@Deprecated
public class OldCrawlerManager implements Runnable {
    private boolean quit;
    private String keywords;
    private int pages;
    Thread runner;
    public OldCrawlerManager(String keywords, int pages, boolean quit){
        this.quit = quit;
        this.pages = pages;
        this.keywords = keywords;
        this.runner = new Thread(this);
        this.runner.setDaemon(true);
        this.runner.start();
    }
    
    public synchronized void run(){
        try {
            CrawlerZeroChan crawler = new CrawlerZeroChan(WallpaperPath.DEFAULT_TMP_WALLPAPER_PATH.toString(), keywords.split(" "), 2, 1);
            var service = Executors.newCachedThreadPool();
            var previewResult = crawler.readMultiplePagesAndDownloadPreviews(pages, service);
    
            service.shutdown();
            // WallpaperImage wp = null;
            var service2 = Executors.newCachedThreadPool();
            crawler.downloadSelectedImagesUsingPAIRs(previewResult, service2);
            service2.shutdown();
            while (!service2.isShutdown()); // 等待線程關閉
            System.out.println("Download complete!");
            if (!quit){
                System.err.println("Download complete!");
            }
            System.out.println("Opening Preview Viewing Window... " + crawler.getFolderPath().replace('/', '\\'));
            if (!quit){
                System.err.println("Opening Preview Viewing Window... " + crawler.getFolderPath().replace('/', '\\'));
            }
            // wp = new WallpaperImage(Path.of(crawler.getFolderPath().replace('/', '\\')));
            // MainController.preview = wp;
            MainController.hasChangedPreview.set(true);
            // MainController.staticImagePreview.setImage(MainController.preview.getCurrentWallpaper());
        } catch (IOException e){
            System.out.println(e.toString());
            if (!quit){
                System.err.println(e.toString());
            }
        }
    }
}
