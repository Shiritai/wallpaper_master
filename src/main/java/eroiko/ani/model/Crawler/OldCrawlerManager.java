package eroiko.ani.model.Crawler;

import java.io.IOError;
import java.io.IOException;
import java.util.concurrent.Executors;

import eroiko.ani.controller.MainController;
import eroiko.ani.controller.TestFunctions;
import eroiko.ani.controller.ControllerSupporter.WallpaperImage;
import eroiko.ani.controller.PrimaryControllers.WallpaperViewController;
import eroiko.ani.util.SourceRedirector;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class OldCrawlerManager implements Runnable {
    private boolean quit;
    private String keywords;
    Thread runner;
    public OldCrawlerManager(String keywords, boolean quit){
        this.quit = quit;
        this.keywords = keywords;
        this.runner = new Thread(this);
        this.runner.setDaemon(true);
        this.runner.start();
    }
    
    public synchronized void run(){
        try {
            CrawlerZeroChan crawler = new CrawlerZeroChan(TestFunctions.testWallpaperPath.toString(), keywords.split(" "), 2, 1);
            var service = Executors.newCachedThreadPool();
            var previewResult = crawler.readMultiplePagesAndDownloadPreviews(5, service);
    
            service.shutdown();
            WallpaperImage wp = null;
            if (SourceRedirector.preViewOrNot){
                System.out.println("Opening Preview Viewing Window... " + crawler.getPreviewsFolderPath().replace('/', '\\'));
                if (!quit){
                    System.err.println("Opening Preview Viewing Window... " + crawler.getPreviewsFolderPath().replace('/', '\\'));
                }
                wp = new WallpaperImage(crawler.getPreviewsFolderPath().replace('/', '\\'), false);
            }
            else {
                var service2 = Executors.newCachedThreadPool();
                crawler.downloadSelectedImagesUsingPAIRs(previewResult, service2);
                service2.shutdown();
                while (!service2.isShutdown()); // 等待線程關閉
                System.out.println("Download complete!");
                if (!quit){
                    System.err.println("Download complete!");
                }
                System.out.println("Opening Preview Viewing Window... " + crawler.getFolderPath().replace('/', '\\'));
                if (!quit){
                    System.err.println("Opening Preview Viewing Window... " + crawler.getFolderPath().replace('/', '\\'));
                }
                wp = new WallpaperImage(crawler.getFolderPath().replace('/', '\\'), false);
            }
            MainController.preview = wp;
            MainController.staticImagePreview.setImage(MainController.preview.getCurrentWallpaper());
            if (SourceRedirector.showWallpapersAfterCrawling){
                WallpaperViewController.quit = quit;
                int serialNumber = SourceRedirector.addWallpaper(wp);
                try {
                    var stage = new Stage();
                    stage.setTitle("Wallpaper Viewer");
                    stage.setScene(new Scene(FXMLLoader.load(getClass().getClassLoader().getResource("eroiko/ani/view/WallpaperViewWindow.fxml"))));
                    stage.getIcons().add(new Image(getClass().getClassLoader().getResource("eroiko/ani/img/wallpaper79.png").toString()));
                    stage.setOnCloseRequest(e -> {
                        SourceRedirector.deleteWallpaper(serialNumber);
                    });
                    stage.show();
                } catch (Exception e){
                    e.printStackTrace();
                    System.out.println(e.toString());
                    if (!quit){
                        System.err.println(e.toString());
                    }
                }
            }
        } catch (IOException e){
            System.out.println(e.toString());
            if (!quit){
                System.err.println(e.toString());
            }
            // Alert alert = new Alert(AlertType.INFORMATION);
            // alert.titleProperty().set("Message");
            // alert.headerTextProperty().set("Wrong keywords, please check and search again.");
            // alert.showAndWait();
        }
    }
}
