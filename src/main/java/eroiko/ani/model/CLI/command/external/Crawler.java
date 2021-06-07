package eroiko.ani.model.CLI.command.external;

import java.io.IOException;

import eroiko.ani.controller.MainController;
import eroiko.ani.model.CLI.command.fundamental.*;
import eroiko.ani.model.NewCrawler.CrawlerManager;
import eroiko.ani.util.NeoWallpaper.Wallpaper;
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
            try {
                MainController.OpenWallpaper(Wallpaper.getWallpaper(Wallpaper.getWallpaperSerialNumberImmediately()));
            } catch (IOException e) {
                throw new IllegalArgumentException(id.getName() + " : Failed to open WallpaperViewer.");
            }
        }
        else {
            throw new IllegalArgumentException(id.getName() + " : Invalid keywords.");
        }
    }
}
