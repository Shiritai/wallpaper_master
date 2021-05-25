package eroiko.ani.model.NewCrawler;

// import java.net.InetSocketAddress;
// import java.net.Proxy;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;

import eroiko.ani.util.MyDS.myTriple;

public abstract class CrawlerBase {
    protected final static String UserAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:87.0) Gecko/20100101 Firefox/87.0";
    // protected final static Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("127.0.0.1", 1080)); // 等理解這個再說
    protected String query;
    protected String first_layer_url;
    public static final int CRAWLER_BASE = 0;
    public static final int CRAWLER_ZEROCHAN = 1;
    public static final int CRAWLER_WALLHAVEN = 2;
    public static final int CRAWLER_SUPPORT_NUMBERS = 2;
    public int CRAWLER_TYPE;
    public static final int [] multiThreadSupport = {CRAWLER_ZEROCHAN, };

    /** 傳入特定頁面與線程池, 讀取單一頁面預覽圖, 保證將結果傳入 {@code downloadSelectedImagesUsingPAIR} 可以順利下載 */
    public abstract ArrayList<myTriple<Integer, String, String>> fetchImageLinks(int page, ExecutorService service);
    public abstract boolean isValidKeyword(String keyword);
    public abstract int numberOfImageInPages(int page);

    public static boolean isMultiThreadable(CrawlerBase cb){
        for (var i : multiThreadSupport){
            if (cb.CRAWLER_TYPE == i){
                return true;
            }
        }
        return false;
    }
}
