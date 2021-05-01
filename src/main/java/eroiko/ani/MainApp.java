package eroiko.ani;

import java.io.*;

import com.dustinredmond.fxtrayicon.FXTrayIcon;

import eroiko.ani.controller.MainController;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.*;
import javafx.scene.image.Image;
import javafx.scene.input.*;
import javafx.stage.*;

public class MainApp extends Application{
        
    public static Stage mainStage;
    public static Scene mainScene;

    public static void main(String [] args){
        launch(args);
    }

    @Override
    public void start (Stage mainStage) throws IOException{
        MainApp.mainStage = mainStage;
        Parent root = FXMLLoader.load(getClass().getResource("view/MainWindow.fxml"));
        mainScene = new Scene(root);

        FXTrayIcon trayIcon = new FXTrayIcon(mainStage, getClass().getResource("img/wallpaper79.png"));
        trayIcon.show();
        // MenuItem menu = new MenuItem("Meow!");
        // menu.setOnAction(e -> new Alert(Alert.AlertType.INFORMATION, "Clicked on Menu!").showAndWait());
        // trayIcon.addMenuItem(menu);

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