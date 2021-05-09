package eroiko.ani.controller.PrimaryControllers;

import java.net.URL;
import java.util.ResourceBundle;

import eroiko.ani.util.MediaOperator;
import eroiko.ani.util.MusicBox;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.ImageView;
import javafx.scene.media.MediaView;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
// import javafx.util.Duration;

public class MusicController implements Initializable{
    
    public static BooleanProperty isActivating = new SimpleBooleanProperty(false);
    public static DoubleProperty progress = new SimpleDoubleProperty(0.);
    public static DoubleProperty progressMax = new SimpleDoubleProperty(0.);
    @FXML private MediaView mediaBox;
    @FXML private ImageView lastMusicButton;
    @FXML private ImageView nextMusicButton;
    @FXML private ImageView playMusicButton;
    @FXML private CheckBox customizeProcessingMusic;
    @FXML private CheckBox customizeCompleteMusic;
    @FXML private Label musicName;
    @FXML private Slider volumeBar;
    // @FXML private Slider progressBar;
    
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
        musicName.setText(carryReturnString(MusicBox.musicBox.getCurrentMusicName(), 27));
        // progress.addListener(e -> progressBar.setValue(progress.getValue()));
        // progressMax.addListener(e -> progressBar.setMax(progressMax.getValue()));
        
        // MusicBox.musicBox.player.currentTimeProperty().addListener((a, b, c) -> progressBar.setValue(c.toSeconds()));
        
        // progressBar.setOnMouseDragged(e -> MusicBox.musicBox.player.seek(Duration.seconds(progressBar.getValue())));
        
        volumeBar.setValue(2.5);
        volumeBar.valueProperty().addListener(e -> MusicBox.musicBox.player.setVolume(volumeBar.getValue() / 100));
        
        playMusicButton.setOnMouseClicked(e -> {
            MusicBox.musicBox.playOrPause();
            musicName.setText(carryReturnString(MusicBox.musicBox.getCurrentMusicName(), 27));
            System.out.println("play or pause");
        });
        nextMusicButton.setOnMouseClicked(e -> {
            MusicBox.musicBox.playNextMusic();
            musicName.setText(carryReturnString(MusicBox.musicBox.getCurrentMusicName(), 27));
            System.out.println("next");
        });
        lastMusicButton.setOnMouseClicked(e -> {
            MusicBox.musicBox.playPreviousMusic();
            musicName.setText(carryReturnString(MusicBox.musicBox.getCurrentMusicName(), 27));
            System.out.println("previous");
        });
        isActivating.set(true);
    }

    public String carryReturnString(String str, int length){
        String res = "";
        var tmp = str.toCharArray();
        for (int i = 0; i < tmp.length; ++i){
            if (i % length == length - 1){
                res += "\n";
            }
            res += tmp[i];
        }
        return res;
    }
}

