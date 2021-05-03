package eroiko.ani.controller.PrimaryControllers;

import java.net.URL;
import java.util.ResourceBundle;

import eroiko.ani.util.SourceRedirector;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;

public class PropertiesController implements Initializable {

    public static boolean quit;
    public static boolean checkedPreviewOrNot = true;
    public static boolean showWallpapersAfterCrawling = true;
    
    @FXML private CheckBox showWallpapers;
    @FXML private CheckBox previewOrNot;

    @Override
    public void initialize(URL url, ResourceBundle rb){
        previewOrNot.setSelected(checkedPreviewOrNot);
        previewOrNot.setText("Search and view \"Preview\" wallpaper\nbefore download the full size image");
        previewOrNot.selectedProperty().addListener((ov, old_val, new_val) -> {
            SourceRedirector.preViewOrNot = new_val;
            PropertiesController.checkedPreviewOrNot = new_val;
        });
        showWallpapers.setSelected(checkedPreviewOrNot);
        showWallpapers.selectedProperty().addListener((ov, old_val, new_val) -> {
            SourceRedirector.showWallpapersAfterCrawling = new_val;
            PropertiesController.showWallpapersAfterCrawling = new_val;
        });
    }
}
