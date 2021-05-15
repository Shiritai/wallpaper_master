package eroiko.ani.controller.PrimaryControllers;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.ResourceBundle;

import eroiko.ani.MainApp;
import eroiko.ani.util.MediaOperator;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.util.Duration;
import javafx.util.StringConverter;

public class MusicWithSyamiko implements Initializable {
    /* Player part */
    private static final int PROCESSING = 0;
    private static final int COMPLETE = 1;
    private static final int CERTAIN = 2;
    private static final int CURRENT = 3;
    private static final int RANDOM = 4;
    private static final int NEXT = 5;
    private static final int PREVIOUS = 6;
    public static MediaOperator box = MediaOperator.playBox;
    public static MediaPlayer player = new MediaPlayer(box.getCurrentMedia());
    /* Support visualization properties */
    private static boolean openedWindow = false;
    /** True : play, False : pause */
    private static BooleanProperty playOrPauseImageSwitcher = new SimpleBooleanProperty(player.getStatus().equals(MediaPlayer.Status.PLAYING));
    private static DoubleProperty volume = new SimpleDoubleProperty(0.025);
    private static DoubleProperty progress = new SimpleDoubleProperty(0.);
    private static DoubleProperty maxProgress = new SimpleDoubleProperty(0.);
    private static StringProperty nameOfMusic = new SimpleStringProperty();

    public static void playOrPause(){
        if (player.getStatus().equals(MediaPlayer.Status.PLAYING)){
            player.pause();
            playOrPauseImageSwitcher.set(false);
        }
        else if (player.getStatus().equals(MediaPlayer.Status.PAUSED)){
            player.play();
            playOrPauseImageSwitcher.set(true);
        }
        else {
            play(CURRENT);
            playOrPauseImageSwitcher.set(true);
        }
    }

    private static void play(int type, Path path){
        if (MediaPlayer.Status.PLAYING.equals(player.getStatus())){
            player.stop();
        }
        player = switch (type) {
            case PROCESSING -> new MediaPlayer(box.getDefaultProcessingMedia());
            case COMPLETE -> new MediaPlayer(box.getDefaultCompleteMedia());
            case CURRENT -> new MediaPlayer(box.getCurrentMedia());
            case CERTAIN -> new MediaPlayer(new Media(path.toAbsolutePath().toString()));
            case RANDOM -> new MediaPlayer(box.getRandomMedia());
            case NEXT -> new MediaPlayer(box.getNextMedia());
            case PREVIOUS -> new MediaPlayer(box.getPreviousMedia());
            default -> throw new IllegalArgumentException("Error type of image!");
        };
        if (type == PROCESSING){
            player.setCycleCount(MediaPlayer.INDEFINITE);
        }
        player.setOnReady(() -> {
            maxProgress.set(player.getTotalDuration().toSeconds());
        });
        player.currentTimeProperty().addListener((a, b, c) -> progress.setValue(c.toSeconds()));
        player.volumeProperty().addListener(e -> volume.set(player.getVolume()));
        player.setVolume(volume.get());
        if (PreferenceController.keepMusic){
            player.setOnEndOfMedia(() -> {
                if (PreferenceController.randomMusic){
                    play(RANDOM);
                }
                else {
                    play(NEXT);
                }
            });
        }
        player.play();
    }

    private static void play(MusicWithSyamiko th, int type, Path path){
        if (MediaPlayer.Status.PLAYING.equals(player.getStatus())){
            player.stop();
        }
        player = switch (type) {
            case PROCESSING -> new MediaPlayer(box.getDefaultProcessingMedia());
            case COMPLETE -> new MediaPlayer(box.getDefaultCompleteMedia());
            case CURRENT -> new MediaPlayer(box.getCurrentMedia());
            case CERTAIN -> new MediaPlayer(new Media(path.toAbsolutePath().toString()));
            case RANDOM -> new MediaPlayer(box.getRandomMedia());
            case NEXT -> new MediaPlayer(box.getNextMedia());
            case PREVIOUS -> new MediaPlayer(box.getPreviousMedia());
            default -> throw new IllegalArgumentException("Error type of image!");
        };
        if (type == PROCESSING){
            player.setCycleCount(MediaPlayer.INDEFINITE);
        }
        player.setOnReady(() -> {
            maxProgress.set(player.getTotalDuration().toSeconds());
        });
        player.currentTimeProperty().addListener((a, b, c) -> progress.setValue(c.toSeconds()));
        player.volumeProperty().addListener(e -> volume.set(player.getVolume()));
        player.setVolume(volume.get());
        if (PreferenceController.keepMusic){
            player.setOnEndOfMedia(() -> {
                if (PreferenceController.randomMusic){
                    play(RANDOM);
                }
                else {
                    play(NEXT);
                }
                th.refresh();
            });
        }
        nameOfMusic.set(box.getCurrentMediaName());
        player.play();
    }

    private static void play(MusicWithSyamiko th, int type){
        if (type == CERTAIN){
            throw new IllegalArgumentException("Please assign certain music path when calling play(certain, path)");
        }
        play(th, type, null);
    }

    private static void play(int type){
        if (type == CERTAIN){
            throw new IllegalArgumentException("Please assign certain music path when calling play(certain, path)");
        }
        play(type, null);
    }

    /* Open window */
    private static Stage stage;
    private static Image playImage = null;
    private static Image pauseImage = null;
    public static void openMusicWithSyamiko(){
        if (!openedWindow){
            try {
                stage = new Stage();
                stage.setTitle("Music with Syamiko");
                stage.setScene(new Scene(FXMLLoader.load(FileSystems.getDefault().getPath("src/main/java/eroiko/ani/view/MusicWithSyamiko.fxml").toAbsolutePath().toUri().toURL())));
                stage.setResizable(false);
                stage.getIcons().add(MainApp.icon);
                stage.show();
            } catch (Exception e){
                e.printStackTrace();
                System.out.println(e.toString());
            }
        }
        else {
            new Alert(Alert.AlertType.INFORMATION, "You've already open Music with Syamiko :)").showAndWait();
        }
    }
    /* Controller part */
    @FXML private MediaView mediaBox;
    @FXML private Slider progressBar;
    @FXML private Slider volumeBar;
    @FXML private ImageView lastMusicButton;
    @FXML private ImageView playMusicButton;
    @FXML private ImageView nextMusicButton;
    @FXML private ImageView shuffleButton;
    @FXML private CheckBox customizeProcessingMusic;
    @FXML private CheckBox customizeCompleteMusic;
    @FXML private Label musicName;
    @FXML private Text currentTime;
    @FXML private Text durationTime;
    
    @FXML
    void OpenMusicExplorerForComplete(ActionEvent event) {
        if (customizeCompleteMusic.selectedProperty().get()){
            var tmp = new FileChooser();
            try {
                tmp.getExtensionFilters().addAll(new ExtensionFilter("Audio Files", "*.wav", "*.mp3", "*.flac"));
                box.addNewCompleteToDefault(tmp.showOpenDialog(null).toPath());
            } catch (Exception e){} // 表示沒做選擇, InvocationTargetException
        }
    }

    @FXML
    void OpenMusicExplorerForProcessing(ActionEvent event) {
        if (customizeProcessingMusic.selectedProperty().get()){
            var tmp = new FileChooser();
            try {
                tmp.getExtensionFilters().addAll(new ExtensionFilter("Audio Files", "*.wav", "*.mp3", "*.flac"));
                box.addNewProcessingToDefault(tmp.showOpenDialog(null).toPath());
            } catch (Exception e){} // 表示沒做選擇, InvocationTargetException
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        musicName.setOnMouseClicked(e -> musicName.setText(carryReturnStringAndStripType(box.getCurrentMediaName(), 25)));
        openedWindow = true;
        if (pauseImage == null){
            try {
                pauseImage = new Image(FileSystems.getDefault().getPath("src/main/java/eroiko/ani/img/pause.png").toUri().toURL().toString());
                playImage = new Image(FileSystems.getDefault().getPath("src/main/java/eroiko/ani/img/play.png").toUri().toURL().toString());
            } catch (MalformedURLException e1) {
                e1.printStackTrace();
            }
        }

        Bindings.bindBidirectional(currentTime.textProperty(), progress, new StringConverter<Number>(){
            @Override
            public String toString(Number object){
                Integer o = object.intValue();
                var minute = o / 60;
                var second = o % 60;
                return object == null ? "" : (
                    (minute < 10 ? "0" : "") + Integer.toString(minute)
                     + ":" + (second < 10 ? "0" : "") + Integer.toString(second)
                );
            }
            @Override
            public Number fromString(String string){
                return (string != null && !string.isEmpty()) ? Double.valueOf( string ) : null;
            }
        });
        Bindings.bindBidirectional(durationTime.textProperty(), maxProgress, new StringConverter<Number>(){
            @Override
            public String toString(Number object){
                Integer o = object.intValue();
                var minute = o / 60;
                var second = o % 60;
                return object == null ? "" : (
                    (minute < 10 ? "0" : "") + Integer.toString(minute)
                     + ":" + (second < 10 ? "0" : "") + Integer.toString(second)
                );
            }
            @Override
            public Number fromString(String string){
                return (string != null && !string.isEmpty()) ? Double.valueOf( string ) : null;
            }
        });

        System.out.println(player.getStatus().equals(MediaPlayer.Status.PAUSED));
        playMusicButton.setImage((player.getStatus().equals(MediaPlayer.Status.PLAYING)) ? pauseImage : playImage);
        var chImage = new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> arg0, Boolean arg1, Boolean arg2) {
                playMusicButton.setImage((arg2) ? pauseImage : playImage);
            }
        };
        playOrPauseImageSwitcher.addListener(chImage);
        
        musicName.setText(carryReturnStringAndStripType(box.getCurrentMediaName(), 25));
        Bindings.bindBidirectional(musicName.textProperty(), nameOfMusic, new StringConverter<String>(){
            @Override
            public String fromString(String arg0) {
                return arg0.replace("\n", "");
            }
            @Override
            public String toString(String arg0) {
                return carryReturnStringAndStripType(arg0, 25);
            }
        });

        progressBar.valueProperty().bindBidirectional(progress);
        progressBar.maxProperty().bind(maxProgress);
        ChangeListener<? super Duration> chProgress = new ChangeListener<>() {
            @Override
            public void changed(ObservableValue<? extends Duration> arg0, Duration arg1, Duration arg2) {
                progressBar.setValue(arg2.toSeconds());
            }
        };
        player.currentTimeProperty().addListener(chProgress);
        
        progressBar.setOnMouseDragged(e -> player.seek(Duration.seconds(progressBar.getValue())));
        progressBar.setOnMouseClicked(e -> player.seek(Duration.seconds(progressBar.getValue())));
        
        volumeBar.setValue(volume.get() * 1000);
        InvalidationListener chVolume = new InvalidationListener() {
            @Override 
            public void invalidated(Observable o) {
                player.setVolume(volumeBar.getValue() / 1000);
            }
        };
        volumeBar.valueProperty().addListener(chVolume);
        
        playMusicButton.setOnMouseClicked(e -> {
            playOrPause();
            nameOfMusic.set(box.getCurrentMediaName());
            musicName.setText(carryReturnStringAndStripType(box.getCurrentMediaName(), 25));
        });
        nextMusicButton.setOnMouseClicked(e -> {
            play(this, NEXT);
            refresh();
        });
        lastMusicButton.setOnMouseClicked(e -> {
            play(this, PREVIOUS);
            refresh();
        });
        shuffleButton.setOnMouseClicked(e -> {
            play(this, RANDOM);
            refresh();
        });
        /* 把控制器與撥放器整合才可以實現關閉 binds 以及 listeners */
        stage.setOnCloseRequest(e -> {
            openedWindow = false;
            currentTime.textProperty().unbind();
            durationTime.textProperty().unbind();
            playOrPauseImageSwitcher.removeListener(chImage);
            volumeBar.maxProperty().unbind();
            volumeBar.valueProperty().removeListener(chVolume);
            player.currentTimeProperty().removeListener(chProgress);
            musicName.textProperty().unbind();
            progressBar.valueProperty().unbindBidirectional(progress);
        });
    }

    private void refresh(){
        playMusicButton.setImage(pauseImage);
        nameOfMusic.set(box.getCurrentMediaName());
    }

    public static String carryReturnStringAndStripType(String str, int length){
        if (str != null){
            String res = "";
            var tmp = str.toCharArray();
            for (int i = 0; i < tmp.length && tmp[i] != '.'; ++i){
                if (i % length == length - 1){
                    res += "\n";
                }
                res += tmp[i];
            }
            return res;
        }
        return "<Null>";
    }

}