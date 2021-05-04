package eroiko.ani.model.Crawler;

import java.io.IOException;
import java.util.concurrent.Executors;

import eroiko.ani.controller.MainController;
import eroiko.ani.util.SourceRedirector;
import eroiko.ani.util.WallpaperClass.WallpaperImage;

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
            CrawlerZeroChan crawler = new CrawlerZeroChan(SourceRedirector.defaultDataPath.toString(), keywords.split(" "), 2, 1);
            var service = Executors.newCachedThreadPool();
            var previewResult = crawler.readMultiplePagesAndDownloadPreviews(pages, service);
    
            service.shutdown();
            WallpaperImage wp = null;
            if (SourceRedirector.preViewOrNot){
                System.out.println("Opening Preview Viewing Window... " + crawler.getPreviewsFolderPath().replace('/', '\\'));
                if (!quit){
                    System.err.println("Opening Preview Viewing Window... " + crawler.getPreviewsFolderPath().replace('/', '\\'));
                }
                wp = new WallpaperImage(crawler.getPreviewsFolderPath().replace('/', '\\'), false);
            }
            else {
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
                wp = new WallpaperImage(crawler.getFolderPath().replace('/', '\\'), false);
            }
            MainController.preview = wp;
            MainController.staticImagePreview.setImage(MainController.preview.getCurrentWallpaper());
        } catch (IOException e){
            System.out.println(e.toString());
            if (!quit){
                System.err.println(e.toString());
            }
            // Alert alert = new Alert(AlertType.INFORMATION);
            // alert.titleProperty().set("Message");
            // alert.headerTextProperty().set("Wrong keywords, please check and search again.");
            // alert.showAndWait();
        }
    }
}
