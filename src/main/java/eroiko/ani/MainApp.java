package eroiko.ani;

import java.awt.SystemTray;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;


import com.dustinredmond.fxtrayicon.FXTrayIcon;

import eroiko.ani.controller.MainController;
import eroiko.ani.util.MusicBox;
import eroiko.ani.util.NeoWallpaper.Wallpaper;
import eroiko.ani.util.NeoWallpaper.WallpaperPath;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.*;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.input.*;
import javafx.stage.*;

public class MainApp extends Application{
        
    public static boolean isTesting = true;
    public static final String version = "version 0.0.1";
    
    public static Stage mainStage;
    public static Scene mainScene;
    public static MenuItem [] menuItems;
    public static Menu menu;
    public static MenuItem [] childMenu;
    public static FXTrayIcon trayIcon;

    public static void main(String [] args){
        launch(args);
    }

    @Override
    public void start (Stage mainStage) throws IOException{
        MainApp.mainStage = mainStage;
        Parent root = FXMLLoader.load(getClass().getResource("view/MainWindow.fxml"));
        mainScene = new Scene(root);

        // setListeners();

        if (SystemTray.isSupported()){
            setMinimizedMenu();
            initTrayIcon();
        }

        /* 小圖示與主視窗的顯示反向同步, 爾後僅針對主視窗的行為定義 */
        mainStage.setOnHiding(e -> {
            if (SystemTray.isSupported() && !trayIcon.isShowing()){
                initTrayIcon();
                trayIcon.show();
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
                new MainController().OpenPreferenceWindow();
                e.consume();
            }
        });
        mainStage.addEventFilter(KeyEvent.KEY_PRESSED, (e) -> { // 最小化至 System tray
            if (new KeyCodeCombination(KeyCode.M, KeyCodeCombination.CONTROL_DOWN).match(e)){
                mainStage.hide();
            }
        });
        /* mainStage 基礎設定 */
        // mainStage.getIcons().add(new Image(getClass().getClassLoader().getResource("eroiko/ani/img/wallpaper79.png").toString()));
        mainStage.getIcons().add(new Image(getClass().getClassLoader().getResource("eroiko/ani/img/wallpaper79.png").toString()));
        mainStage.setTitle("Wallpaper Master");
        mainStage.setScene(mainScene);
        mainStage.setResizable(false);
        mainStage.show();

        mainStage.setOnCloseRequest(e -> {

            Wallpaper.executeResultAndCleanPreview(); // 執行所有 Wallpaper 檔案操作!
            mainStage.close();
            Platform.exit();
            System.exit(0);
        });
    }

    private void setMinimizedMenu() {
        /* Set minimized menu identities */
        // menu = new Menu("Options");
        // childMenu = new MenuItem[2];
        // childMenu[0] = new MenuItem("opt1");
        // childMenu[0].setOnAction(e -> System.out.println("clicked opt1"));
        // childMenu[1] = new MenuItem("opt2");
        // childMenu[1].setOnAction(e -> System.out.println("clicked opt2"));
        // menu.getItems().addAll(childMenu[0], childMenu[1]);
        
        menuItems = new MenuItem [5];
        menuItems[0] = new MenuItem("Open Wallpaper Master");
        menuItems[0].setOnAction(e -> mainStage.show());
        menuItems[1] = new MenuItem("Take clipboard images to wallpaper folder");
        menuItems[1].setOnAction(e -> {
            var cb = Clipboard.getSystemClipboard().getFiles();
            try {
                var tmpFile = new File(WallpaperPath.defaultWallpaperPath.toString()); // 未來必須改用 Preference Path
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
        menuItems[2] = new MenuItem("Preference");
        menuItems[2].setOnAction(e -> (new MainController()).OpenPreferenceWindow());
        menuItems[3] = new MenuItem("Play/Pause music");
        menuItems[3].setOnAction(e -> MusicBox.musicBox.playOrPause());
        // menuItems[3].setOnAction(e -> MusicWithSyamiko.musicBox.playOrPause());
        menuItems[4] = new MenuItem("Music with Syamiko");
        menuItems[4].setOnAction(e -> (new MainController()).OpenMusicWindow());
        // menuItems[4].setOnAction(e -> (new MainController()).OpenSyamikoWindow());
    }
    
    private void initTrayIcon(){
        trayIcon = new FXTrayIcon(mainStage, getClass().getResource("img/wallpaper79.png"));
        trayIcon.setTrayIconTooltip("Wallpaper Master\n" + version);
        for (var m : menuItems){
            trayIcon.addMenuItem(m);
        }
        trayIcon.insertSeparator(2);
        // trayIcon.insertMenuItem(menu, 0);
        trayIcon.insertSeparator(6);
        trayIcon.addExitItem(true);
    }
}