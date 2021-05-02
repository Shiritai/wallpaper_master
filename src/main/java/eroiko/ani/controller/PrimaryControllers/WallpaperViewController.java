package eroiko.ani.controller.PrimaryControllers;

import java.net.URL;
import java.nio.file.Path;
import java.util.ResourceBundle;

import eroiko.ani.controller.ControllerSupporter.WallpaperImage;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class WallpaperViewController implements Initializable{
    public static boolean quit;
    public static WallpaperImage wp;
    public static Path currentPath;
    public static Stage stage;
    public final static KeyCodeCombination CtrlPlus = new KeyCodeCombination(KeyCode.PLUS, KeyCodeCombination.CONTROL_DOWN);
    public final static KeyCodeCombination CtrlMinus = new KeyCodeCombination(KeyCode.MINUS, KeyCodeCombination.CONTROL_DOWN);

    @FXML private ImageView view;
    @FXML private AnchorPane anchor;
    @FXML private Text wallpaperName;
    @FXML private Text wallpaperPosition;
    
    @Override
    public void initialize(URL url, ResourceBundle rb){
        view.setImage(wp.getCurrentWallpaper());
        currentPath = wp.getCurrentWallpaperPath();
        wallpaperName.setText("Current Wallpaper : " + currentPath.getFileName().toString());
        wallpaperPosition.setText("Wallpaper Path : " + currentPath.toString());
        
        anchor.setOnScroll((e) -> {
            switchImage(e.getDeltaY());
            e.consume();
        });
        wallpaperName.setOnMouseEntered((e) -> {
            wallpaperName.setOpacity(0.2);
        });
        wallpaperName.setOnMouseExited((e) -> {
            wallpaperName.setOpacity(1.);
        });
        wallpaperPosition.setOnMouseEntered((e) -> {
            wallpaperPosition.setOpacity(0.2);
        });
        wallpaperPosition.setOnMouseExited((e) -> {
            wallpaperPosition.setOpacity(1.);
        });
    }
    
    private void switchImage(double direction){
        if (direction > 0){
            view.setImage(wp.getNextWallpaper());
        }
        else if (direction < 0){
            view.setImage(wp.getLastWallpaper());
        }
        currentPath = wp.getCurrentWallpaperPath();
        wallpaperName.setText("Current Wallpaper : " + currentPath.getFileName().toString());
        wallpaperPosition.setText("Wallpaper Path : " + currentPath.toString());
    }
}
