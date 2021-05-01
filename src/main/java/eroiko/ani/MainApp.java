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
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.input.*;
import javafx.stage.*;

public class MainApp extends Application{
        
    public static Stage mainStage;
    public static Scene mainScene;
    public static MenuItem [] menuItems;
    public static FXTrayIcon trayIcon;

    public static void main(String [] args){
        launch(args);
    }

    @Override
    public void start (Stage mainStage) throws IOException{
        MainApp.mainStage = mainStage;
        Parent root = FXMLLoader.load(getClass().getResource("view/MainWindow.fxml"));
        mainScene = new Scene(root);

        setMinimizedMenu();

        mainScene.addEventFilter(KeyEvent.KEY_PRESSED, (e) -> { // 因為是對整個 Scene, 因此宣告在此
            if (new KeyCodeCombination(KeyCode.P, KeyCombination.CONTROL_DOWN).match(e)){
                new MainController().OpenPropertiesWindow(new ActionEvent());
                e.consume();
            }
        });

        mainStage.getIcons().add(new Image(getClass().getClassLoader().getResource("eroiko/ani/img/wallpaper79.png").toString()));
        mainStage.setTitle("Wallpaper Master");
        mainStage.setScene(mainScene);
        mainStage.show();

        // mainStage.setOnCloseRequest(e -> trayIcon.show());
        mainStage.setOnHiding(e -> trayIcon.show());
        mainStage.setOnShowing(e -> trayIcon.hide());

    }

    private void setMinimizedMenu() {
        /* Adjust minimized menu */
        trayIcon = new FXTrayIcon(mainStage, getClass().getResource("img/wallpaper79.png"));
        trayIcon.setTrayIconTooltip("Wallpaper Master\nversion 0.0.1");

        var menu = new Menu("Options");
        var childMenu1 = new MenuItem("opt1");
        childMenu1.setOnAction(e -> System.out.println("clicked opt1"));
        var childMenu2 = new MenuItem("opt2");
        childMenu2.setOnAction(e -> System.out.println("clicked opt2"));
        menu.getItems().addAll(childMenu1, childMenu2);
        
        menuItems = new MenuItem [4];
        menuItems[0] = menu;
        menuItems[1] = new MenuItem("Properties");
        MainApp.menuItems[1].setOnAction(e -> new Alert(Alert.AlertType.INFORMATION, "Show Properties!").showAndWait());
        menuItems[2] = new MenuItem("Process");
        MainApp.menuItems[2].setOnAction(e -> new Alert(Alert.AlertType.INFORMATION, "Clicked on Process Menu!").showAndWait());
        menuItems[3] = new MenuItem("Exit");
        MainApp.menuItems[3].setOnAction(e -> {mainStage.close(); Platform.exit(); System.exit(0);});
        
        for (var m : menuItems){
            trayIcon.addMenuItem(m);
        }
        trayIcon.setOnAction(e -> mainStage.show());
    }
}