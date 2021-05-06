package eroiko.ani.model.NewCrawler;

// import java.io.IOException;

import eroiko.ani.util.SourceRedirector;

// import eroiko.ani.util.SourceRedirector;

public class testNewCrawler {
    public static void main(String [] args){
        // var tmp = new CrawlerManager(SourceRedirector.defaultDataPath.toString(), "Girls frontline".split(" "), 6);
        // tmp.A_getLinks();
        // System.out.println("Finish first Stage");
        // tmp.print();
        // tmp.B_download();
        // System.out.println("Finish Second Stage");
        // tmp.D_lastDownloadStage();
        // System.out.println("Finish Last Stage");
        // try {
        //     var crawler = new CrawlerWallhaven("Girls' last tour".split(" "));
        //     System.out.println("Meow");
        // } catch (IOException e) {
        //     e.printStackTrace();
        // }
        // System.out.println(new CrawlerWallhaven().isValidKeyword("Girls' last tour"));
        var tmp = new CrawlerManager(SourceRedirector.defaultDataPath.toAbsolutePath().toString(), "Girls' last tour".split(" "), 2);
        tmp.Z_getProcessElementsNumber();
        System.out.println("------ z ------");
        tmp.A_getLinks();
        System.out.println("------ a ------");
        tmp.B_download();
        System.out.println("------ b ------");
        tmp.C_openWallpaperFilterViewer();
        System.out.println("------ c ------");
        tmp.D_lastDownloadStage();
        System.out.println("------ d ------");
        tmp.E_openFullWallpaperFilterViewer();
        System.out.println("------ e ------");
    }
}
