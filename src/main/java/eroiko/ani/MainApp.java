package eroiko.ani;

import java.io.*;

import com.dustinredmond.fxtrayicon.FXTrayIcon;

import eroiko.ani.controller.MainController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.*;
import javafx.scene.control.Alert;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.input.*;
import javafx.stage.*;

public class MainApp extends Application{
        
    public static Stage mainStage;
    public static Scene mainScene;
    public static MenuItem [] menus;

    public static void main(String [] args){
        launch(args);
    }

    @Override
    public void start (Stage mainStage) throws IOException{
        MainApp.mainStage = mainStage;
        Parent root = FXMLLoader.load(getClass().getResource("view/MainWindow.fxml"));
        mainScene = new Scene(root);


        FXTrayIcon trayIcon = new FXTrayIcon(mainStage, getClass().getResource("img/wallpaper79.png"));
        trayIcon.setTrayIconTooltip("Wallpaper Master\nversion 0.0.1");

        menus = new MenuItem [4];
        menus[0] = new MenuItem("Current Wallpaper");
        menus[1] = new MenuItem("Properties");
        MainApp.menus[1].setOnAction(e -> new Alert(Alert.AlertType.INFORMATION, "Show Properties!").showAndWait());
        menus[2] = new MenuItem("Process");
        MainApp.menus[2].setOnAction(e -> new Alert(Alert.AlertType.INFORMATION, "Clicked on Process Menu!").showAndWait());
        menus[3] = new MenuItem("Exit");
        MainApp.menus[3].setOnAction(e -> {mainStage.close(); Platform.exit(); System.exit(0);});
        
        for (var m : menus){
            trayIcon.addMenuItem(m);
        }
        trayIcon.show();


        mainScene.addEventFilter(KeyEvent.KEY_PRESSED, (e) -> { // 因為是對整個 Scene, 因此宣告在此
            if (new KeyCodeCombination(KeyCode.P, KeyCombination.CONTROL_DOWN).match(e)){
                new MainController().OpenPropertiesWindow(new ActionEvent());
                e.consume();
            }
        });

        MainApp.mainStage.getIcons().add(new Image(getClass().getClassLoader().getResource("eroiko/ani/img/wallpaper79.png").toString()));
        MainApp.mainStage.setTitle("Wallpaper Master");
        MainApp.mainStage.setScene(mainScene);
        MainApp.mainStage.show();

        // MainApp.mainStage.setOnCloseRequest((e) -> {
            // MainController.
        // });

    }
}