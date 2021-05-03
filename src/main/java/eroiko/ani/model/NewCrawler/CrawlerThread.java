package eroiko.ani.model.NewCrawler;

import eroiko.ani.controller.MainController;
import eroiko.ani.controller.ControllerSupporter.WallpaperImage;
import eroiko.ani.util.SourceRedirector;

public class CrawlerThread {
    private CrawlerManager cw;
    public CrawlerThread(String folderPath, String [] keywords){
        var tmp = new Thread(() -> {
            cw = new CrawlerManager(folderPath, keywords);
            cw.A_getLinks(SourceRedirector.pagesToDownload / 4);
            cw.B_download();
            cw.D_lastDownloadStage();
            var wp = new WallpaperImage(cw.fullSavePath, false);
            MainController.preview = wp;
            MainController.staticImagePreview.setImage(MainController.preview.getCurrentWallpaper());
        });
        tmp.setDaemon(true);
        tmp.start();
    }
}
