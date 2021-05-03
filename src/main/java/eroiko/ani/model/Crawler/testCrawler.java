package eroiko.ani.model.Crawler;

import static java.lang.System.*;

import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.Executors;

public class testCrawler {
    public static void main(String[] args) {
        var console = new Scanner(in);

        CrawlerZeroChan zch = null;
        try {
            zch = new CrawlerZeroChan(
                "D:/ShiZu_Code/Java/animazation/src/main/java/eroiko/ani/worldpaper/img",
                console.nextLine().split(" "));
        } catch (IOException e) {
            e.printStackTrace();
        }
        zch.setFirstLayerUrl(2, 1);
        System.out.println(zch.getFirstLayerUrl());

        int pagesToDownload = console.nextInt();

        var start = nanoTime();
        
        var service = Executors.newCachedThreadPool();
        var data = zch.readMultiplePagesAndDownloadPreviews(pagesToDownload, service);
        service.shutdown();
        while (!service.isTerminated());
        System.out.println("_______________________Meow_______________________");
        var service2 = Executors.newCachedThreadPool();
        zch.downloadSelectedImagesUsingPAIRs(data, service2);
        service2.shutdown();
        while (!service2.isTerminated());

        var end = nanoTime();
        out.println("Used : " + Double.toString((end - start) / 1000000000.));

        console.close();
    }
}
