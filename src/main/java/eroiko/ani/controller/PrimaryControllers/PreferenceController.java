package eroiko.ani.controller.PrimaryControllers;

import java.net.URL;
import java.util.ResourceBundle;

import eroiko.ani.util.SourceRedirector;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;

public class PreferenceController implements Initializable {

    public static boolean quit;
    public static boolean checkedPreviewOrNot = true;
    public static boolean showWallpapersAfterCrawling = true;
    public static boolean useOldCrawlerForFullSpeedMode = false;
    
    @FXML private CheckBox showWallpapers;
    @FXML private CheckBox previewOrNot;
    @FXML private CheckBox fullSpeedMode;

    @Override
    public void initialize(URL url, ResourceBundle rb){
        previewOrNot.setSelected(checkedPreviewOrNot);
        previewOrNot.selectedProperty().addListener((ov, old_val, new_val) -> {
            SourceRedirector.preViewOrNot = new_val;
            PreferenceController.checkedPreviewOrNot = new_val;
        });
        showWallpapers.setSelected(showWallpapersAfterCrawling);
        showWallpapers.selectedProperty().addListener((ov, old_val, new_val) -> {
            SourceRedirector.showWallpapersAfterCrawling = new_val;
            PreferenceController.showWallpapersAfterCrawling = new_val;
        });
        showWallpapers.setSelected(useOldCrawlerForFullSpeedMode);
        showWallpapers.selectedProperty().addListener((ov, old_val, new_val) -> {
            SourceRedirector.useOldCrawlerForFullSpeedMode = new_val;
            PreferenceController.useOldCrawlerForFullSpeedMode = new_val;
        });
    }
}
