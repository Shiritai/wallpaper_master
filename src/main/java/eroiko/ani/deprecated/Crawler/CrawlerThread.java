/*
 * Author : Shiritai (楊子慶, or Eroiko on Github) at 2021/06/13.
 * See https://github.com/Shiritai/wallpaper_master for more information.
 * Created using VSCode.
 */
package eroiko.ani.deprecated.Crawler;

import eroiko.ani.model.NewCrawler.CrawlerManager;

// import eroiko.ani.util.SourceRedirector;

/* Deprecated Class... */
@Deprecated
public class CrawlerThread {
    private CrawlerManager cw;
    public CrawlerThread(String folderPath, String [] keywords, Integer pages){
        var tmp = new Thread(() -> {
            cw = new CrawlerManager(folderPath, keywords, pages);
            // cw.A_getLinks();
            // cw.B_download();
            // cw.D_lastDownloadStage();
        });
        tmp.setDaemon(true);
        tmp.start();
    }
}
