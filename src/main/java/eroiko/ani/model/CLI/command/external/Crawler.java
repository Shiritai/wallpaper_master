/*
 * Author : Shiritai (楊子慶, or Eroiko on Github) at 2021/06/13.
 * See https://github.com/Shiritai/wallpaper_master for more information.
 * Created using VSCode.
 */
package eroiko.ani.model.CLI.command.external;

import eroiko.ani.model.CLI.command.fundamental.*;
import eroiko.ani.model.NewCrawler.CrawlerManager;
import eroiko.ani.util.NeoWallpaper.WallpaperPath;

public class Crawler extends Command {

    private String keywords;
    
    public Crawler(String keywords){
        super(Type.CRAWLER);
        this.keywords = keywords;
    }

    @Override
    public void execute() throws IllegalArgumentException {
        /* 確認是否印出詳細 */
        var printInfo = keywords.startsWith("-i");
        if (printInfo){ keywords = keywords.substring(3); }
        /* 確認數量 */
        var para = keywords.split(" "); // 用來修剪 keywords 並取得參數, 確保 keywords 正確
        int number;
        try {
            number = Integer.parseInt(para[0]);
            keywords = keywords.substring(para[0].length() + 1);
        } catch (NumberFormatException ne){ // 無指定
            number = 1;
        }
        var check = CrawlerManager.checkValidation(keywords);
        if (check){
            var cw = new CrawlerManager(WallpaperPath.DEFAULT_TMP_WALLPAPER_PATH.toString(), keywords.split(" "), number, printInfo);
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
