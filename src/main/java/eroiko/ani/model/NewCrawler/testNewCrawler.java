package eroiko.ani.model.NewCrawler;

import eroiko.ani.controller.TestFunctions;

public class testNewCrawler {
    public static void main(String [] args){
        var tmp = new CrawlerManager(TestFunctions.testWallpaperPath.toString(), "Girls frontline".split(" "));
        tmp.A_getLinks(2);
        System.out.println("Finish first Stage");
        tmp.print();
        tmp.B_download();
        System.out.println("Finish Second Stage");
        tmp.D_lastDownloadStage();
        System.out.println("Finish Last Stage");
    }
}
