package eroiko.ani.controller.ControllerSupporter;

import javafx.application.Platform;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

public class MinimizeWindow {
    public void minimizeWindow(Stage stage){
        Platform.setImplicitExit(false);
        stage.setOnCloseRequest(event -> minimizeAStage());
        stage.addEventFilter(KeyEvent.KEY_PRESSED, (e) -> {
            if (new KeyCodeCombination(KeyCode.M, KeyCodeCombination.CONTROL_DOWN).match(e)){
                minimizeAStage();
            }
        });
    }
    
    private void minimizeAStage(){

        // FXTrayIcon trayIcon = new FXTrayIcon(MainApp.mainStage, getClass().getClassLoader().getResource("eroiko.ain.img.wallpaper79.ico"));
        // MenuItem menu = new MenuItem("Meow!");
        // menu.setOnAction(e -> new Alert(Alert.AlertType.INFORMATION, "Clicked on Menu!").showAndWait());
        // trayIcon.addMenuItem(menu);
    }
}
