package eroiko.ani.model.NewCrawler;

// import eroiko.ani.util.SourceRedirector;

/* Deprecated Class... */
public class CrawlerThread {
    private CrawlerManager cw;
    public CrawlerThread(String folderPath, String [] keywords, Integer pages){
        var tmp = new Thread(() -> {
            cw = new CrawlerManager(folderPath, keywords, pages);
            cw.A_getLinks();
            cw.B_download();
            cw.D_lastDownloadStage();
        });
        tmp.setDaemon(true);
        tmp.start();
    }
}
