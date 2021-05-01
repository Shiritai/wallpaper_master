package eroiko.ani;

import java.io.*;

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

        mainScene.addEventFilter(KeyEvent.KEY_PRESSED, (e) -> {
            if (new KeyCodeCombination(KeyCode.P, KeyCombination.CONTROL_DOWN).match(e)){
                new MainController().OpenPropertiesWindow(new ActionEvent());
                e.consume();
            }
        });

        MainApp.mainStage.getIcons().add(new Image(new File("src/main/java/eroiko/ani/img/wallpaper79.png").toURI().toString()));
        MainApp.mainStage.setTitle("Wallpaper Master");
        MainApp.mainStage.setScene(mainScene);
        MainApp.mainStage.show();

    }
}