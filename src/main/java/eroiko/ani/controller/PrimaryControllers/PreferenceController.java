package eroiko.ani.controller.PrimaryControllers;

import java.net.URL;
import java.nio.file.Path;
import java.util.ResourceBundle;

import eroiko.ani.util.SourceRedirector;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.DirectoryChooser;

public class PreferenceController implements Initializable {

    public static boolean quit;
    public static boolean checkedPreviewOrNot = true;
    public static boolean showWallpapersAfterCrawling = true;
    public static boolean useOldCrawlerForFullSpeedMode = false;
    
    @FXML private CheckBox showWallpapers;
    @FXML private CheckBox previewOrNot;
    @FXML private CheckBox fullSpeedMode;
    @FXML private CheckBox customizeBox;
    @FXML private TextField savingDir;


    @FXML
    void OpenFileChooser(ActionEvent event) {
        if (customizeBox.selectedProperty().get()){
            var tmp = new DirectoryChooser();
            try {
                SourceRedirector.userSelectedPath = tmp.showDialog(null).toPath();
                System.out.println("New default path : " + SourceRedirector.userSelectedPath);
                savingDir.setText(SourceRedirector.userSelectedPath.toAbsolutePath().toString());
            } catch (Exception e){} // 表示沒做選擇, InvocationTargetException
        }
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb){
        savingDir.editableProperty().set(false);
        savingDir.setText(SourceRedirector.defaultDataPath.toAbsolutePath().toString());

        savingDir.addEventFilter(KeyEvent.KEY_PRESSED, e -> {
            if (customizeBox.selectedProperty().get() && e.getCode().equals(KeyCode.ENTER)){
                SourceRedirector.userSelectedPath = Path.of(savingDir.getText());
                System.out.println("New default path : " + SourceRedirector.defaultDataPath);
                savingDir.setText(SourceRedirector.userSelectedPath.toAbsolutePath().toString());
            }
        });
        
        customizeBox.selectedProperty().addListener((a, b, c) -> {
            if (c){
                savingDir.editableProperty().set(true);
            }
            else {
                savingDir.editableProperty().set(false);
                SourceRedirector.userSelectedPath = null;
                savingDir.setText(SourceRedirector.defaultDataPath.toAbsolutePath().toString());
            }
        });
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
