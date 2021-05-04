package eroiko.ani.model.NewCrawler;

import eroiko.ani.util.SourceRedirector;

public class CrawlerThread {
    private CrawlerManager cw;
    public CrawlerThread(String folderPath, String [] keywords, Integer pages){
        var tmp = new Thread(() -> {
            cw = new CrawlerManager(folderPath, keywords, pages);
            cw.A_getLinks();
            cw.B_download();
            cw.C_openWallpaperFilterViewer();
            cw.D_lastDownloadStage();
        });
        tmp.setDaemon(true);
        tmp.start();
    }

    public CrawlerThread(String folderPath, String [] keywords){
        var tmp = new Thread(() -> {
            cw = new CrawlerManager(folderPath, keywords, SourceRedirector.pagesToDownload);
            cw.A_getLinks();
            cw.B_download();
            cw.C_openWallpaperFilterViewer();
            cw.D_lastDownloadStage();
            cw.E_openFullWallpaperFilterViewer();
            
            // UI update is run on the Application thread
        });
        // Platform.runLater(tmp);
        tmp.setDaemon(true);
        tmp.start();
    }
    // private CrawlerManager cw;
    // Thread executor;
    // private String folderPath;
    // private String [] keywords;
    // public CrawlerThread(String folderPath, String [] keywords){
    //     cw = new CrawlerManager(folderPath, keywords);
    //     executor = new Thread(this);
    //     executor.setDaemon(true);
    //     executor.start();
    // }
    // @Override
    // public synchronized void run(){
    //     cw.A_getLinks(SourceRedirector.pagesToDownload / 4);
    //     cw.B_download();
    //     cw.C_openWallpaperFilterViewer();
    //     // cw.D_lastDownloadStage();
    //     // var wp = new WallpaperImage(cw.fullSavePath, false);
    //     // MainController.preview = wp;
    //     // MainController.staticImagePreview.setImage(MainController.preview.getCurrentWallpaper());
    // }
}
