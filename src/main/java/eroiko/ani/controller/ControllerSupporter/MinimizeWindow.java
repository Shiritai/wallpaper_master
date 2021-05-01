package eroiko.ani.controller.ControllerSupporter;

import javafx.application.Platform;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

public class MinimizeWindow {
    public static void minimizeWindow(Stage stage){
        Platform.setImplicitExit(false);
        stage.setOnCloseRequest(event -> minimizeAStage());
        stage.addEventFilter(KeyEvent.KEY_PRESSED, (e) -> {
            if (new KeyCodeCombination(KeyCode.M, KeyCodeCombination.CONTROL_DOWN).match(e)){
                minimizeAStage();
            }
        });
    }
    
    private static void minimizeAStage(){
        // FXTrayIcon
        
        //Check the SystemTray is supported
        // if (!SystemTray.isSupported()) {
        //     System.out.println("SystemTray is not supported");
        //     return;
        // }
        // final PopupMenu popup = new PopupMenu();

        // URL url = System.class.getResource("/images/new.png");
        // Image image = Toolkit.getDefaultToolkit().getImage(url);

        // final TrayIcon trayIcon = new TrayIcon(image);

        // final SystemTray tray = SystemTray.getSystemTray();

        // // Create a pop-up menu components
        // MenuItem aboutItem = new MenuItem("About");
        // CheckboxMenuItem cb1 = new CheckboxMenuItem("Set auto size");
        // CheckboxMenuItem cb2 = new CheckboxMenuItem("Set tooltip");
        // Menu displayMenu = new Menu("Display");
        // MenuItem errorItem = new MenuItem("Error");
        // MenuItem warningItem = new MenuItem("Warning");
        // MenuItem infoItem = new MenuItem("Info");
        // MenuItem noneItem = new MenuItem("None");
        // MenuItem exitItem = new MenuItem("Exit");

        // //Add components to pop-up menu
        // popup.add(aboutItem);
        // popup.addSeparator();
        // popup.add(cb1);
        // popup.add(cb2);
        // popup.addSeparator();
        // popup.add(displayMenu);
        // displayMenu.add(errorItem);
        // displayMenu.add(warningItem);
        // displayMenu.add(infoItem);
        // displayMenu.add(noneItem);
        // popup.add(exitItem);

        // trayIcon.setPopupMenu(popup);

        // try {
        //     tray.add(trayIcon);
        // } catch (AWTException e) {
        //     System.out.println("TrayIcon could not be added.");
        // }
    }
}
