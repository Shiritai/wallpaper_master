package eroiko.ani.controller.PrimaryControllers;

import java.net.URL;
import java.nio.file.Path;
import java.util.ResourceBundle;

import eroiko.ani.util.MediaOperator;
import eroiko.ani.util.MusicBox;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.ImageView;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.util.Duration;

public class MusicWithSyamiko implements Initializable{
    public static MusicBox musicBox = new MusicBox();
    
    private static MediaOperator mop = MediaOperator.playBox;
    public static MediaPlayer player = new MediaPlayer(mop.getCurrentMedia());
    
    private static final int PROCESSING = 0;
    private static final int COMPLETE = 1;
    private static final int CERTAIN = 2;
    private static final int CURRENT = 3;
    private static final int RANDOM = 4;
    private static final int NEXT = 5;
    private static final int PREVIOUS = 6;

    public static void playOrPause(){
        if (player.getStatus().equals(MediaPlayer.Status.PLAYING)){
            player.pause();
        }
        else if (player.getStatus().equals(MediaPlayer.Status.PAUSED)){
            player.play();
        }
        else {
            play(CURRENT);
        }
    }
    
    private static void play(int type, Path path){
        if (MediaPlayer.Status.PLAYING.equals(player.getStatus())){
            player.stop();
        }
        player = switch (type) {
            case PROCESSING -> new MediaPlayer(mop.getDefaultProcessingMedia());
            case COMPLETE -> new MediaPlayer(mop.getDefaultCompleteMedia());
            case CURRENT -> new MediaPlayer(mop.getCurrentMedia());
            case CERTAIN -> new MediaPlayer(new Media(path.toAbsolutePath().toString()));
            case RANDOM -> new MediaPlayer(mop.getRandomMedia());
            case NEXT -> new MediaPlayer(mop.getNextMedia());
            case PREVIOUS -> new MediaPlayer(mop.getPreviousMedia());
            default -> new MediaPlayer(mop.getCurrentMedia());
        };
        if (type == PROCESSING){
            player.setCycleCount(MediaPlayer.INDEFINITE);
        }
        player.setVolume(0.025);
        player.play();
    }

    public static void play(int type){
        if (type == CERTAIN){
            throw new IllegalArgumentException("Please assign certain music path when calling play(certain, path)");
        }
        play(type, null);
    }

    public static void playCurrentMusic(){
        play(CURRENT);
    }

    public static void playNextMusic(){
        play(NEXT);
    }

    public static void playPreviousMusic(){
        play(PREVIOUS);
    }
    
    public static void playCertain(Path path){
        play(CERTAIN, path);
    }
    
    public static void playComplete(){
        play(COMPLETE);
    }
    
    public static void playProcessing(){
        play(PROCESSING);
    }

    public static void playRandom(){
        play(RANDOM);
    }

    public static String getCurrentMusicName(){
        return mop.getCurrentMediaPath().getFileName().toString();
    }

    public static StringProperty getCurrentNameProperty(){
        return new SimpleStringProperty(mop.getCurrentMediaPath().getFileName().toString());
    }

    public static Duration getCurrentDuration(){
        return mop.getCurrentMedia().getDuration();
    }

    public static BooleanProperty isActivating = new SimpleBooleanProperty(false);
    @FXML private MediaView mediaBox;
    @FXML private ImageView lastMusicButton;
    @FXML private ImageView nextMusicButton;
    @FXML private ImageView playMusicButton;
    @FXML private CheckBox customizeProcessingMusic;
    @FXML private CheckBox customizeCompleteMusic;
    @FXML private Label musicName;
    @FXML private Slider volumeBar;
    
    @FXML
    void OpenMusicExplorerForComplete(ActionEvent event) {
        if (customizeCompleteMusic.selectedProperty().get()){
            var tmp = new FileChooser();
            try {
                tmp.getExtensionFilters().addAll(new ExtensionFilter("Audio Files", "*.wav", "*.mp3", "*.flac"));
                mop.addNewCompleteToDefault(tmp.showOpenDialog(null).toPath());
            } catch (Exception e){} // 表示沒做選擇, InvocationTargetException
        }
    }
    
    @FXML
    void OpenMusicExplorerForProcessing(ActionEvent event) {
        if (customizeProcessingMusic.selectedProperty().get()){
            var tmp = new FileChooser();
            try {
                tmp.getExtensionFilters().addAll(new ExtensionFilter("Audio Files", "*.wav", "*.mp3", "*.flac"));
                mop.addNewProcessingToDefault(tmp.showOpenDialog(null).toPath());
            } catch (Exception e){} // 表示沒做選擇, InvocationTargetException
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        musicName.setText(carryReturnString(getCurrentMusicName(), 27));
        
        volumeBar.setValue(player.getVolume() * 10);
        volumeBar.valueProperty().addListener(e -> player.setVolume(volumeBar.getValue() / 1000));
        
        playMusicButton.setOnMouseClicked(e -> {
            playOrPause();
            musicName.setText(carryReturnString(getCurrentMusicName(), 27));
            System.out.println("play or pause");
        });
        nextMusicButton.setOnMouseClicked(e -> {
            playNextMusic();
            musicName.setText(carryReturnString(getCurrentMusicName(), 27));
            System.out.println("next");
        });
        lastMusicButton.setOnMouseClicked(e -> {
            playPreviousMusic();
            musicName.setText(carryReturnString(getCurrentMusicName(), 27));
            System.out.println("previous");
        });
        isActivating.set(true);
    }

    public static String carryReturnString(String str, int length){
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
