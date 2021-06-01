package eroiko.ani;

import java.awt.SystemTray;
import java.io.*;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import com.dustinredmond.fxtrayicon.FXTrayIcon;

import eroiko.ani.controller.MainController;
import eroiko.ani.controller.PrimaryControllers.MusicWithSyamiko;
import eroiko.ani.controller.PrimaryControllers.PreferenceController;
import eroiko.ani.util.MediaClass.MediaOperator;
import eroiko.ani.util.NeoWallpaper.Wallpaper;
import eroiko.ani.util.NeoWallpaper.WallpaperPath;
import eroiko.ani.util.NeoWallpaper.Wallpaperize;
import javafx.application.Application;
import javafx.application.HostServices;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.*;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.input.*;
import javafx.scene.text.Font;
import javafx.stage.*;

public class MainApp extends Application{
        
    public static boolean isTesting = true;
    public static final String version = "version 0.1.1";
    public static final String date = "2021/06/01";
    public static Image icon;
    
    public static Stage mainStage;
    public static Scene mainScene;
    public static MenuItem [] menuItems;
    public static Menu menu;
    public static MenuItem [] childMenu;
    public static FXTrayIcon trayIcon;

    public static HostServices hostServices;
    public static Font rainbow28;
    public static Font firaCode12;
    public static Font firaCode13;
    public static Font firaCode16;
    public static Font firaCode20;
    public static Font firaCodeBold24;
    public static Font bookAntiquaItalic25;
    public static Font notoSansCJK22;

    public static void main(String [] args){
        launch(args);
    }
    
    @Override
    public void start (Stage mainStage) throws IOException{
        /* System settings */
        hostServices = getHostServices();
        /* Load fonts */
        rainbow28 = Font.loadFont(new FileInputStream(WallpaperPath.DEFAULT_DATA_PATH.resolve("font/rainyhearts.ttf").toFile()), 28.);
        firaCode12 = Font.loadFont(new FileInputStream(WallpaperPath.DEFAULT_DATA_PATH.resolve("font/FiraCode-Regular.ttf").toFile()), 12.);
        firaCode13 = Font.loadFont(new FileInputStream(WallpaperPath.DEFAULT_DATA_PATH.resolve("font/FiraCode-Regular.ttf").toFile()), 13.);
        firaCode16 = Font.loadFont(new FileInputStream(WallpaperPath.DEFAULT_DATA_PATH.resolve("font/FiraCode-Regular.ttf").toFile()), 16.);
        firaCode20 = Font.loadFont(new FileInputStream(WallpaperPath.DEFAULT_DATA_PATH.resolve("font/FiraCode-Regular.ttf").toFile()), 20.);
        firaCodeBold24 = Font.loadFont(new FileInputStream(WallpaperPath.DEFAULT_DATA_PATH.resolve("font/FiraCode-Bold.ttf").toFile()), 24.);
        bookAntiquaItalic25 = Font.loadFont(new FileInputStream(WallpaperPath.DEFAULT_DATA_PATH.resolve("font/Book Antiqua Italic.ttf").toFile()), 25.);
        notoSansCJK22 = Font.loadFont(new FileInputStream(WallpaperPath.DEFAULT_DATA_PATH.resolve("font/NotoSansCJKtc-Regular.otf").toFile()), 22.);

        MainApp.mainStage = mainStage;
        Parent root = FXMLLoader.load(WallpaperPath.FXML_SOURCE_PATH.resolve("MainWindow.fxml").toUri().toURL());
        mainScene = new Scene(root);

        if (SystemTray.isSupported()){
            setMinimizedMenu();
            initTrayIcon();
        }

        /* 小圖示與主視窗的顯示反向同步, 爾後僅針對主視窗的行為定義 */
        mainStage.setOnHiding(e -> {
            if (SystemTray.isSupported() && !trayIcon.isShowing()){
                initTrayIcon();
                trayIcon.show();
                if (PreferenceController.minimizedMsg){
                    trayIcon.showMessage("Minimized Wallpaper Master", "Right click the tray icon to see more information");
                }
            }
        });
        mainStage.setOnShowing(e -> {
            if (SystemTray.isSupported() && trayIcon.isShowing()){
                trayIcon.clear();
                trayIcon.hide();
            }
        });
        /* 定義全視窗快捷鍵 */
        mainScene.addEventFilter(KeyEvent.KEY_PRESSED, (e) -> { //  彈出 Properties 視窗, 因為是對整個 Scene, 因此宣告在此
            if (new KeyCodeCombination(KeyCode.P, KeyCombination.CONTROL_DOWN).match(e)){
                MainController.OpenPreferenceWindow();
                e.consume();
            }
        });
        mainStage.addEventFilter(KeyEvent.KEY_PRESSED, (e) -> { // 最小化至 System tray
            if (new KeyCodeCombination(KeyCode.M, KeyCodeCombination.CONTROL_DOWN).match(e)){
                mainStage.hide();
            }
        });
        /* mainStage 基礎設定 */
        icon = new Image(WallpaperPath.IMAGE_SOURCE_PATH.resolve("wallpaper79.png").toFile().toURI().toURL().toString());
        mainStage.getIcons().add(icon);
        mainStage.setTitle("Wallpaper Master");
        mainStage.setScene(mainScene);
        mainStage.setResizable(false);
        mainStage.show();

        mainStage.setOnCloseRequest(e -> {
            MediaOperator.playBox.clean();
            Wallpaper.executeResultAndCleanPreview(); // 執行所有 Wallpaper 檔案操作!
            new Wallpaperize().execute();
            mainStage.close();
            Platform.exit();
            System.exit(0);
        });
    }

    private static final int menuSize = 7;
    
    private void setMinimizedMenu() {
        int num = 0;
        menuItems = new MenuItem [menuSize];
        menuItems[num] = new MenuItem("About");
        menuItems[num++].setOnAction(e -> MainController.OpenAboutWindow());
        menuItems[num] = new MenuItem("Open Wallpaper Master");
        menuItems[num++].setOnAction(e -> mainStage.show());
        menuItems[num] = new MenuItem("Open Music with Syamiko");
        menuItems[num++].setOnAction(e -> MusicWithSyamiko.openMusicWithSyamiko());
        menuItems[num] = new MenuItem("Open Music with Akari");
        menuItems[num++].setOnAction(e -> MainController.OpenMusicWithAkari());
        menuItems[num] = new MenuItem("Paste images to wallpaper folder");
        menuItems[num++].setOnAction(e -> {
            var cb = Clipboard.getSystemClipboard().getFiles();
            try {
                var tmpFile = new File(WallpaperPath.getWallpaperPath().toString()); // 未來必須改用 Preference Path
                if (!tmpFile.exists()){
                    tmpFile.mkdir();
                }
                for (var f : cb){
                    Files.copy(new FileInputStream(f), Path.of(tmpFile + "\\" + f.getName()), StandardCopyOption.REPLACE_EXISTING);
                    System.out.println(f.getName());
                    System.out.println(Path.of(tmpFile + "\\" + f.getName()));
                }
            } catch (IOException e1) {
                System.out.println(e1.toString());
            }
        });
        menuItems[num] = new MenuItem("Preference");
        menuItems[num++].setOnAction(e -> MainController.OpenPreferenceWindow());
        menuItems[num] = new MenuItem("Play/Pause music");
        menuItems[num++].setOnAction(e -> MusicWithSyamiko.playOrPause());
    }
    
    private void initTrayIcon(){
        try {
            trayIcon = new FXTrayIcon(mainStage, WallpaperPath.IMAGE_SOURCE_PATH.resolve("wallpaper79.png").toAbsolutePath().toUri().toURL());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        trayIcon.setTrayIconTooltip("Wallpaper Master\n" + version);
        for (var m : menuItems){
            trayIcon.addMenuItem(m);
        }
        trayIcon.insertSeparator(1);
        trayIcon.insertSeparator(5);
        trayIcon.insertSeparator(9);
        trayIcon.addExitItem(true);
    }
}