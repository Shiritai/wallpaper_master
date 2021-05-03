package eroiko.ani.controller.PrimaryControllers;

import java.net.URL;
import java.nio.file.Path;
import java.util.ResourceBundle;

import eroiko.ani.controller.ControllerSupporter.WallpaperImage;
import eroiko.ani.util.SourceRedirector;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class WallpaperViewController implements Initializable{
    public static boolean quit;
    public static Path currentPath;
    public static Stage stage;
    public final static KeyCodeCombination CtrlPlus = new KeyCodeCombination(KeyCode.PLUS, KeyCodeCombination.CONTROL_DOWN);
    public final static KeyCodeCombination CtrlMinus = new KeyCodeCombination(KeyCode.MINUS, KeyCodeCombination.CONTROL_DOWN);
    
    WallpaperImage wp;
    
    @FXML private ImageView view;
    @FXML private StackPane stackPane;
    @FXML private Text wallpaperName;
    @FXML private Text wallpaperPosition;
    @FXML private Text next;
    @FXML private Text previous;
    
    @Override
    public void initialize(URL url, ResourceBundle rb){
        final int serialNumber = SourceRedirector.getSerialNumberImmediately();
        wp = SourceRedirector.getWallpaperImage(serialNumber);
        view.setImage(wp.getCurrentWallpaper());
        currentPath = wp.getCurrentWallpaperPath();
        wallpaperName.setText("Current Wallpaper : " + currentPath.getFileName().toString());
        wallpaperPosition.setText("Wallpaper Path : " + currentPath.toString());
        setMouseBehavior();
    }
    
    private void setMouseBehavior(){
        stackPane.setOnScroll((e) -> {
            switchImage(e.getDeltaY());
            e.consume();
        });

        next.setOnMouseEntered((e) -> next.setOpacity(0.2));
        next.setOnMouseExited((e) -> next.setOpacity(1.));
        next.setOnMouseClicked(e -> switchNextImage());
        
        previous.setOnMouseEntered(e -> previous.setOpacity(0.2));
        previous.setOnMouseExited(e -> previous.setOpacity(1.));
        previous.setOnMouseClicked(e -> switchPreviousImage());

        wallpaperName.setOnMouseEntered((e) -> wallpaperName.setOpacity(0.2));
        wallpaperName.setOnMouseExited((e) -> wallpaperName.setOpacity(1.));

        wallpaperPosition.setOnMouseEntered((e) -> wallpaperPosition.setOpacity(0.2));
        wallpaperPosition.setOnMouseExited((e) -> wallpaperPosition.setOpacity(1.));
    }
    
    private void switchImage(double direction){
        if (direction < 0){
            switchNextImage();
        }
        else if (direction > 0){
            switchPreviousImage();
        }
    }
    
    private void switchNextImage(){
        view.setImage(wp.getNextWallpaper());
        currentPath = wp.getCurrentWallpaperPath();
        wallpaperName.setText("Current Wallpaper : " + currentPath.getFileName().toString());
        wallpaperPosition.setText("Wallpaper Path : " + currentPath.toString());
    }
    
    private void switchPreviousImage(){
        view.setImage(wp.getLastWallpaper());
        currentPath = wp.getCurrentWallpaperPath();
        wallpaperName.setText("Current Wallpaper : " + currentPath.getFileName().toString());
        wallpaperPosition.setText("Wallpaper Path : " + currentPath.toString());
    }

}