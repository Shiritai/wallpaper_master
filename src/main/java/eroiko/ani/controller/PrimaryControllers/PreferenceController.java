package eroiko.ani.controller.PrimaryControllers;

import java.net.URL;
import java.nio.file.Path;
import java.util.ResourceBundle;

import eroiko.ani.util.MediaOperator;
import eroiko.ani.util.SourceRedirector;
import eroiko.ani.util.NeoWallpaper.WallpaperPath;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

public class PreferenceController implements Initializable {

    public static boolean quit;
    public static BooleanProperty showWallpapersAfterCrawling = new SimpleBooleanProperty(true);
    
    
    @FXML private CheckBox showWallpapers;
    @FXML private CheckBox customizeBox;
    @FXML private TextField savingDir;

    @FXML private CheckBox customizeProcessingMusic;
    @FXML private CheckBox customizeCompleteMusic;

    @FXML
    void OpenFileChooser(ActionEvent event) {
        if (customizeBox.selectedProperty().get()){
            var tmp = new DirectoryChooser();
            try {
                WallpaperPath.updateUserWallpaperPath(tmp.showDialog(null).toPath());
                savingDir.setText(WallpaperPath.getWallpaperPath().toString());
            } catch (Exception e){} // 表示沒做選擇, InvocationTargetException
        }
    }
    
    @FXML
    public void OpenMusicExplorerForComplete(ActionEvent event) {
        if (customizeCompleteMusic.selectedProperty().get()){
            var tmp = new FileChooser();
            try {
                tmp.getExtensionFilters().addAll(new ExtensionFilter("Audio Files", "*.wav", "*.mp3", "*.flac"));
                MediaOperator.playBox.addNewCompleteToDefault(tmp.showOpenDialog(null).toPath());
            } catch (Exception e){} // 表示沒做選擇, InvocationTargetException
        }
    }
    
    @FXML
    void OpenMusicExplorerForProcessing(ActionEvent event) {
        if (customizeProcessingMusic.selectedProperty().get()){
            var tmp = new FileChooser();
            try {
                tmp.getExtensionFilters().addAll(new ExtensionFilter("Audio Files", "*.wav", "*.mp3", "*.flac"));
                MediaOperator.playBox.addNewProcessingToDefault(tmp.showOpenDialog(null).toPath());
            } catch (Exception e){} // 表示沒做選擇, InvocationTargetException
        }
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb){
        customizeBox.selectedProperty().set(WallpaperPath.useConfigOrNot());
        savingDir.editableProperty().set(WallpaperPath.useConfigOrNot());
        savingDir.setText(WallpaperPath.getWallpaperPath().toAbsolutePath().toString());
        savingDir.addEventFilter(KeyEvent.KEY_PRESSED, e -> {
            if (customizeBox.selectedProperty().get() && e.getCode().equals(KeyCode.ENTER)){
                WallpaperPath.updateUserWallpaperPath(Path.of(savingDir.getText()));
                savingDir.setText(WallpaperPath.getWallpaperPath().toString());
            }
        });
    
        customizeBox.selectedProperty().addListener((a, b, c) -> {
            if (c){
                savingDir.editableProperty().set(true);
            }
            else {
                savingDir.editableProperty().set(false);
                WallpaperPath.resetToDefaultWallpaperPath();
                savingDir.setText(WallpaperPath.getWallpaperPath().toString());
            }
        });
        showWallpapers.setSelected(showWallpapersAfterCrawling.get());
        showWallpapers.selectedProperty().addListener((ov, old_val, new_val) -> {
            SourceRedirector.showWallpapersAfterCrawling = new_val;
            PreferenceController.showWallpapersAfterCrawling.set(new_val);
        });
    }
}
