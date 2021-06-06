package eroiko.ani.model.CLI.command.special;

import java.io.IOException;

import eroiko.ani.controller.MainController;
import eroiko.ani.model.CLI.command.basic.fundamental.*;
import eroiko.ani.model.NewCrawler.CrawlerManager;
import eroiko.ani.util.NeoWallpaper.Wallpaper;
import eroiko.ani.util.NeoWallpaper.WallpaperPath;

public class Artwork extends Command {

    private final String keywords;
    private final int number;
    
    public Artwork(String keywords){
        super(Type.ARTWORK);
        this.keywords = keywords;
        number = 1;
    }

    public Artwork(int number, String keywords){
        super(Type.ARTWORK);
        this.keywords = keywords;
        this.number = number;
    }

    @Override
    public void execute() throws IllegalArgumentException {
        var check = CrawlerManager.checkValidation(keywords);
        if (check){
            var cw = new CrawlerManager(WallpaperPath.DEFAULT_TMP_WALLPAPER_PATH.toString(), keywords.split(" "), number);
            System.out.println("[Crawler Manager]  Fetch links...");
            cw.A_getLinks();
            cw.print();
            System.out.println("[Crawler Manager]  Peek links and Download Previews...");
            cw.B_download();
            System.out.println("[Crawler Manager]  Download Full Image...");
            cw.D_lastDownloadStage();
            System.out.println("[Crawler Manager]  Pushing result...");
            cw.E_pushWallpaper();
            System.out.println("[Crawler Manager]  Close...");
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

    @Override
    public void exeAfterRequest(String cmd) {
        
        
    }
    
}
