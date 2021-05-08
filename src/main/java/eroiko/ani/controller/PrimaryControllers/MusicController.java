package eroiko.ani.controller.PrimaryControllers;

import java.net.URL;
import java.util.ResourceBundle;

import eroiko.ani.util.MediaOperator;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.media.MediaView;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

public class MusicController implements Initializable{

    @FXML private MediaView mediaBox;
    @FXML private Rectangle lastMusicButton;
    @FXML private Rectangle nextMusicButton;
    @FXML private Rectangle playMusicButton;
    @FXML private CheckBox customizeProcessingMusic;
    @FXML private CheckBox customizeCompleteMusic;
    
    @FXML
    void OpenMusicExplorerForComplete(ActionEvent event) {
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
    public void initialize(URL location, ResourceBundle resources) {
        
        
    }

}

