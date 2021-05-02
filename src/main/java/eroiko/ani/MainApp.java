package eroiko.ani;

import java.awt.SystemTray;
import java.io.*;

import com.dustinredmond.fxtrayicon.FXTrayIcon;

import eroiko.ani.controller.MainController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.*;
import javafx.scene.control.Alert;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.input.*;
import javafx.stage.*;

public class MainApp extends Application{
        
    public static boolean isTesting = true;
    
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

        if (SystemTray.isSupported()){
            setMinimizedMenu();
            initTrayIcon();
        }

        /* 小圖示與主視窗的顯示, 僅針對主視窗的行為定義 */
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
                new MainController().OpenPropertiesWindow(new ActionEvent());
                e.consume();
            }
        });
        mainStage.addEventFilter(KeyEvent.KEY_PRESSED, (e) -> { // 最小化至 System tray
            if (new KeyCodeCombination(KeyCode.M, KeyCodeCombination.CONTROL_DOWN).match(e)){
                mainStage.hide();
            }
        });
        /* mainStage 基礎設定 */
        mainStage.getIcons().add(new Image(getClass().getClassLoader().getResource("eroiko/ani/img/wallpaper79.png").toString()));
        mainStage.setTitle("Wallpaper Master");
        mainStage.setScene(mainScene);
        mainStage.show();

        mainStage.setOnCloseRequest((e) -> {
            mainStage.close();
            Platform.exit();
            System.exit(0);
        });
    }

    private void setMinimizedMenu() {
        /* Set minimized menu identities */
        menu = new Menu("Options");
        childMenu = new MenuItem[2];
        childMenu[0] = new MenuItem("opt1");
        childMenu[0].setOnAction(e -> System.out.println("clicked opt1"));
        childMenu[1] = new MenuItem("opt2");
        childMenu[1].setOnAction(e -> System.out.println("clicked opt2"));
        menu.getItems().addAll(childMenu[0], childMenu[1]);
        
        menuItems = new MenuItem [2];
        menuItems[0] = new MenuItem("Properties");
        menuItems[0].setOnAction(e -> new Alert(Alert.AlertType.INFORMATION, "Show Properties!").showAndWait());
        menuItems[1] = new MenuItem("Process");
        menuItems[1].setOnAction(e -> new Alert(Alert.AlertType.INFORMATION, "Clicked on Process Menu!").showAndWait());
    }
    
    private void initTrayIcon(){
        trayIcon = new FXTrayIcon(mainStage, getClass().getResource("img/wallpaper79.png"));
        trayIcon.setTrayIconTooltip("Wallpaper Master\nversion 0.0.1");
        for (var m : menuItems){
            trayIcon.addMenuItem(m);
        }
        trayIcon.insertSeparator(0);
        trayIcon.insertMenuItem(menu, 0);
        trayIcon.insertSeparator(5);
        trayIcon.addExitItem(true);
    }
}