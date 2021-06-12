package eroiko.ani.model.CLI.command.external;

import eroiko.ani.model.CLI.command.fundamental.*;
import eroiko.ani.model.NewCrawler.CrawlerManager;
import eroiko.ani.util.NeoWallpaper.WallpaperPath;

public class Crawler extends Command {

    private final String keywords;
    private final int number;
    
    public Crawler(String keywords){
        super(Type.CRAWLER);
        this.keywords = keywords;
        number = 1;
    }

    public Crawler(int number, String keywords){
        super(Type.CRAWLER);
        this.keywords = keywords;
        this.number = number;
    }

    @Override
    public void execute() throws IllegalArgumentException {
        var check = CrawlerManager.checkValidation(keywords);
        if (check){
            var cw = new CrawlerManager(WallpaperPath.DEFAULT_TMP_WALLPAPER_PATH.toString(), keywords.split(" "), number);
            out.println("[Crawler Manager]  Fetch links...");
            cw.A_getLinks();
            cw.print();
            out.println("[Crawler Manager]  Peek links and Download Previews...");
            cw.B_download();
            out.println("[Crawler Manager]  Download Full Image...");
            cw.D_lastDownloadStage();
            out.println("[Crawler Manager]  Pushing result...");
            cw.E_pushWallpaper();
            out.println("[Crawler Manager]  Close...");
        }
        else {
            throw illegalParaStr("Invalid keywords.");
        }
    }
}
