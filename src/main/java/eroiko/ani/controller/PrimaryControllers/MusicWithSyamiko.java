/*
 * Author : Shiritai (楊子慶, or Eroiko on Github) at 2021/06/13.
 * See https://github.com/Shiritai/wallpaper_master for more information.
 * Created using VSCode.
 */
package eroiko.ani.controller.PrimaryControllers;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.util.ResourceBundle;

import eroiko.ani.MainApp;
import eroiko.ani.util.MediaClass.MediaOperator;
import eroiko.ani.util.Method.CarryReturn;
import eroiko.ani.util.Method.TimeWait;
import eroiko.ani.util.NeoWallpaper.WallpaperPath;
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
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.util.Duration;
import javafx.util.StringConverter;

/** 一肩扛起 Controller 與 player 的重擔 */
public class MusicWithSyamiko implements Initializable {
    /* Player part */
    public static final int PROCESSING = 0;
    public static final int COMPLETE = 1;
    public static final int CERTAIN = 2;
    public static final int CURRENT = 3;
    public static final int RANDOM = 4;
    public static final int NEXT = 5;
    public static final int PREVIOUS = 6;
    public static MediaOperator box = MediaOperator.playBox;
    public static MediaPlayer player = new MediaPlayer(box.getCurrentMedia());
    /* Support visualization properties */
    private static boolean openedWindow = false;
    /** True : play, False : pause */
    private static BooleanProperty playOrPauseImageSwitcher = new SimpleBooleanProperty(player.getStatus().equals(MediaPlayer.Status.PLAYING));
    private static DoubleProperty volume = new SimpleDoubleProperty(25);
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
        play(null, type, path);
    }

    private static void play(MusicWithSyamiko th, int type, Path path){
        player.stop(); // 終止當前音樂
        player = switch (type) {
            case PROCESSING -> new MediaPlayer(MediaOperator.getDefaultProcessingMedia());
            case COMPLETE -> new MediaPlayer(MediaOperator.getDefaultCompleteMedia());
            case CURRENT -> new MediaPlayer(box.getCurrentMedia());
            case CERTAIN -> new MediaPlayer(new Media(path.toAbsolutePath().toString()));
            case RANDOM -> new MediaPlayer(box.getRandomMedia());
            case NEXT -> new MediaPlayer(box.getNextMedia());
            case PREVIOUS -> new MediaPlayer(box.getPreviousMedia());
            default -> throw new IllegalArgumentException("Error type of music!");
        };
        player.currentTimeProperty().addListener((a, b, c) -> progress.setValue(c.toSeconds()));
        player.volumeProperty().addListener(e -> volume.set(player.getVolume()));
        player.setVolume(volume.get());
        player.setOnEndOfMedia(() -> {
            if (PreferenceController.keepMusic){
                if (PreferenceController.randomMusic){
                    play(RANDOM);
                }
                else {
                    play(NEXT);
                }
                if (th != null){
                    th.refresh();
                }
            }
        });
        System.out.println("[Syamiko]  " + box.getCurrentMediaName());
        if (type == PROCESSING){
            player.setCycleCount(MediaPlayer.INDEFINITE);
            nameOfMusic.set(MediaOperator.getDefaultProcessingName());
        }
        else if (type == COMPLETE){
            nameOfMusic.set(MediaOperator.getDefaultCompleteName());
        }
        else {
            nameOfMusic.set(box.getCurrentMediaName());
            player.setOnReady(() -> {
                maxProgress.set(player.getTotalDuration().toSeconds());
            });
        }
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

    public static void playProcessing(){
        play(PROCESSING);
    }

    public static void playComplete(){
        play(COMPLETE);
    }

    /* Open window */
    private static Stage stage;
    private static Image playImage = null;
    private static Image pauseImage = null;
    private static Image emptyHeartImage = null;
    private static Image fullHeartImage = null;
    private static final int lengthOfMusicName = 28;
    public static void openMusicWithSyamiko(){
        if (!openedWindow){
            try {
                stage = new Stage();
                stage.setTitle("Music with Syamiko");
                stage.setScene(new Scene(FXMLLoader.load(WallpaperPath.FXML_SOURCE_PATH.resolve("MusicWithSyamiko.fxml").toAbsolutePath().toUri().toURL())));
                stage.setResizable(false);
                stage.getIcons().add(MainApp.icon);
                stage.show();
            } catch (Exception e){
                e.printStackTrace();
            }
        }
        else {
            new Alert(Alert.AlertType.INFORMATION, "You've already opened Music with Syamiko :)").showAndWait();
        }
    }
    /* Controller part */
    @FXML private Slider progressBar;
    @FXML private Slider volumeBar;
    @FXML private ImageView lastMusicButton;
    @FXML private ImageView playMusicButton;
    @FXML private ImageView nextMusicButton;
    @FXML private ImageView shuffleButton;
    @FXML private ImageView randomButton;
    @FXML private CheckBox customizeProcessingMusic;
    @FXML private CheckBox customizeCompleteMusic;
    @FXML private Label musicName;
    @FXML private Text currentTime;
    @FXML private Text durationTime;
    @FXML private ImageView loop;
    @FXML private ImageView processingHeart;
    @FXML private ImageView completeHeart;
    @FXML private Label promptLabel;    
    @FXML private AnchorPane importPane;
    @FXML private ImageView importMusic;

    
    @FXML
    void OpenMusicExplorerForComplete(ActionEvent event) {
        if (customizeCompleteMusic.selectedProperty().get()){
            var tmp = new FileChooser();
            tmp.setInitialDirectory(WallpaperPath.DEFAULT_DATA_PATH.toFile());
            tmp.setTitle("Choose complete music");
            try {
                tmp.getExtensionFilters().addAll(new ExtensionFilter("Audio Files", "*.wav", "*.mp3"));
                MediaOperator.addNewCompleteToDefault(tmp.showOpenDialog(null).toPath());
            } catch (Exception e){} // 表示沒做選擇, InvocationTargetException
        }
    }
    
    @FXML
    void OpenMusicExplorerForProcessing(ActionEvent event) {
        if (customizeProcessingMusic.selectedProperty().get()){
            var tmp = new FileChooser();
            tmp.setInitialDirectory(WallpaperPath.DEFAULT_DATA_PATH.toFile());
            tmp.setTitle("Choose processing music");
            try {
                tmp.getExtensionFilters().addAll(new ExtensionFilter("Audio Files", "*.wav", "*.mp3"));
                MediaOperator.addNewProcessingToDefault(tmp.showOpenDialog(null).toPath());
            } catch (Exception e){} // 表示沒做選擇, InvocationTargetException
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        /* Font settings */
        musicName.setFont(MainApp.notoSansCJK22);
        durationTime.setFont(MainApp.firaCode12);
        currentTime.setFont(MainApp.firaCode12);

        musicName.setOnMouseClicked(e -> musicName.setText(CarryReturn.stripTypeAndSerialNumberForMusicWithSyamiko(box.getCurrentMediaName(), lengthOfMusicName)));
        openedWindow = true;
        if (pauseImage == null){
            try {
                pauseImage = new Image(WallpaperPath.IMAGE_SOURCE_PATH.resolve("pause.png").toUri().toURL().toString());
                playImage = new Image(WallpaperPath.IMAGE_SOURCE_PATH.resolve("play.png").toUri().toURL().toString());
                emptyHeartImage = new Image(WallpaperPath.IMAGE_SOURCE_PATH.resolve("heart.png").toUri().toURL().toString());
                fullHeartImage = new Image(WallpaperPath.IMAGE_SOURCE_PATH.resolve("heart filled.png").toUri().toURL().toString());
            } catch (MalformedURLException e1) {
                e1.printStackTrace();
            }
        }
        importMusic.setOnMouseClicked(e -> activateImport());

        loop.setOpacity((PreferenceController.keepMusic) ? 1 : 0.5);
        loop.setOnMouseClicked(e -> activateLoop());

        shuffleButton.setOpacity((PreferenceController.randomMusic) ? 1 : 0.5);
        shuffleButton.setOnMouseClicked(e -> activateShuffle());

        processingHeart.setOnMouseClicked(e -> activateProcessingHeart());
        completeHeart.setOnMouseClicked(e -> activateCompleteHeart());

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

        playMusicButton.setImage((player.getStatus().equals(MediaPlayer.Status.PLAYING)) ? pauseImage : playImage);
        var chImage = new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> arg0, Boolean arg1, Boolean arg2) {
                playMusicButton.setImage((arg2) ? pauseImage : playImage);
            }
        };
        playOrPauseImageSwitcher.addListener(chImage);
        
        musicName.setText(CarryReturn.stripTypeAndSerialNumberForMusicWithSyamiko(box.getCurrentMediaName(), lengthOfMusicName));
        Bindings.bindBidirectional(musicName.textProperty(), nameOfMusic, new StringConverter<String>(){
            @Override
            public String fromString(String arg0) {
                return arg0.replace("\n", "");
            }
            @Override
            public String toString(String arg0) {
                return CarryReturn.stripTypeAndSerialNumberForMusicWithSyamiko(arg0, lengthOfMusicName);
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
        
        volumeBar.setValue(volume.get() * 500);
        InvalidationListener chVolume = new InvalidationListener() {
            @Override 
            public void invalidated(Observable o) {
                player.setVolume(volumeBar.getValue() / 500);
            }
        };
        volumeBar.valueProperty().addListener(chVolume);
        
        playMusicButton.setOnMouseClicked(e -> activatePlay());
        nextMusicButton.setOnMouseClicked(e -> activateNext());
        lastMusicButton.setOnMouseClicked(e -> activatePrevious());
        randomButton.setOnMouseClicked(e -> activateRandom());

        /* stage settings */
        stage.addEventFilter(KeyEvent.KEY_PRESSED, e -> {
            switch (e.getCode()){
                case SPACE -> activatePlay();
                case RIGHT -> activateNext();
                case LEFT-> activatePrevious();
                case UP -> activateVolumeUp();
                case DOWN -> activateVolumeDown();
                case DIGIT0, NUMPAD0 -> { play(CURRENT); refresh(); }
                case R -> activateRandom();
                case S -> activateShuffle();
                case L -> activateLoop();
                case P -> activateProcessingHeart();
                case C -> activateCompleteHeart();
                case I -> activateImport();
                default -> {}
            }
            e.consume();
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
        setPromptLabel();
        refreshWithPlayingCheck();
        playMusicButton.setImage(playImage);
    }
    
    private void refreshWithPlayingCheck(){
        /* 測試是否正在撥放 */
        var tmp = refresh();
        new TimeWait(200); // 等待進度數值變化, 但效果不彰 = =
        playMusicButton.setImage(tmp == progress.get() ? pauseImage : playImage);
    }
    
    private double refresh(){
        playMusicButton.setImage(pauseImage);
        nameOfMusic.set(box.getCurrentMediaName());
        processingHeart.setImage((box.isProcessingMusic()) ? fullHeartImage : emptyHeartImage);
        completeHeart.setImage((box.isCompleteMusic()) ? fullHeartImage : emptyHeartImage);
        return progress.get();
    }

    private void setPromptLabel() {
        playMusicButton.addEventFilter(MouseEvent.MOUSE_ENTERED, e -> promptLabel.setText("play / pause music"));
        playMusicButton.addEventFilter(MouseEvent.MOUSE_EXITED, e -> promptLabel.setText(""));

        nextMusicButton.addEventFilter(MouseEvent.MOUSE_ENTERED, e -> promptLabel.setText("next music"));
        nextMusicButton.addEventFilter(MouseEvent.MOUSE_EXITED, e -> promptLabel.setText(""));

        lastMusicButton.addEventFilter(MouseEvent.MOUSE_ENTERED, e -> promptLabel.setText("previous music"));
        lastMusicButton.addEventFilter(MouseEvent.MOUSE_EXITED, e -> promptLabel.setText(""));

        loop.addEventFilter(MouseEvent.MOUSE_ENTERED, e -> promptLabel.setText("keep playing music"));
        loop.addEventFilter(MouseEvent.MOUSE_EXITED, e -> promptLabel.setText(""));

        randomButton.addEventFilter(MouseEvent.MOUSE_ENTERED, e -> promptLabel.setText("random music"));
        randomButton.addEventFilter(MouseEvent.MOUSE_EXITED, e -> promptLabel.setText(""));

        shuffleButton.addEventFilter(MouseEvent.MOUSE_ENTERED, e -> promptLabel.setText("shuffle music"));
        shuffleButton.addEventFilter(MouseEvent.MOUSE_EXITED, e -> promptLabel.setText(""));

        processingHeart.addEventFilter(MouseEvent.MOUSE_ENTERED, e -> promptLabel.setText("as processing music"));
        processingHeart.addEventFilter(MouseEvent.MOUSE_EXITED, e -> promptLabel.setText(""));

        completeHeart.addEventFilter(MouseEvent.MOUSE_ENTERED, e -> promptLabel.setText("as complete music"));
        completeHeart.addEventFilter(MouseEvent.MOUSE_EXITED, e -> promptLabel.setText(""));

        importMusic.setOnMouseEntered(e -> {
            importPane.setOpacity(1.);
            promptLabel.setText("import musics");
        });
        importMusic.setOnMouseExited(e -> {
            importPane.setOpacity(0.);
            promptLabel.setText("");
        });
    }
    
    private void activatePlay(){
        if (!currentTime.getText().equals("00:00") && currentTime.getText().equals(durationTime.getText())){
            play(CURRENT);
            refresh();
            return;
        }
        playOrPause();
        nameOfMusic.set(box.getCurrentMediaName());
        musicName.setText(CarryReturn.stripTypeAndSerialNumberForMusicWithSyamiko(box.getCurrentMediaName(), lengthOfMusicName));
    }

    private void activateNext(){
        play(this, NEXT);
        refresh();
    }

    private void activatePrevious(){
        play(this, PREVIOUS);
        refresh();
    }

    private void activateRandom(){
        play(this, RANDOM);
        refresh();
    }

    private void activateShuffle(){
        if (PreferenceController.randomMusic){
            PreferenceController.randomMusic = false;
            shuffleButton.setOpacity(0.5);
        }
        else {
            PreferenceController.randomMusic = true;
            shuffleButton.setOpacity(1);
        }
    }

    private void activateLoop(){
        if (PreferenceController.keepMusic){
            PreferenceController.keepMusic = false;
            loop.setOpacity(0.5);
        }
        else {
            PreferenceController.keepMusic = true;
            loop.setOpacity(1);
        }
    }

    private void activateProcessingHeart(){
        if (!box.isDefaultProcessing()){
            if (box.isProcessingMusic()){
                processingHeart.setImage(emptyHeartImage);
                box.restoreDefaultMusic(false);
            }
            else {
                processingHeart.setImage(fullHeartImage);
                box.setMusicToDefault(false);
            }
        }
        else {
            new Thread(() -> {
                var str = "This is the default processing music :)";
                promptLabel.setText(str);
                new TimeWait(5000);
                if (promptLabel.getText().equals(str)){
                    promptLabel.setText("");
                }
            }).start();
        }
    }

    private void activateCompleteHeart(){
        if (!box.isDefaultComplete()){
            if (box.isCompleteMusic()){
                box.restoreDefaultMusic(true);
                completeHeart.setImage(emptyHeartImage);
            }
            else {
                completeHeart.setImage(fullHeartImage);
                box.setMusicToDefault(true);
            }
        }
        else {
            new Thread(() -> {
                var str = "This is the default complete music :)";
                promptLabel.setText(str);
                new TimeWait(5000);
                if (promptLabel.getText().equals(str)){
                    promptLabel.setText("");
                }
            }).start();
        }
    }

    private void activateImport(){
        var tmp = new DirectoryChooser();
        try {
            box.importAWholeMusicFolder(tmp.showDialog(null).toPath());
        } catch (Exception ex){} // 表示沒做選擇, InvocationTargetException
    }

    private void activateVolumeUp(){
        if (player == null){
            return;
        }
        var tmp = volumeBar.getValue();
        if (tmp + 1. > 100.){
            volumeBar.valueProperty().set(100.);
        }
        else {
            volumeBar.valueProperty().set(tmp + 1.);
        }
    }
    
    private void activateVolumeDown(){
        if (player == null){
            return;
        }
        var tmp = volumeBar.getValue();
        if (tmp - 1. < 0.){
            volumeBar.valueProperty().set(0.);
        }
        else {
            volumeBar.valueProperty().set(tmp - 1.);
        }
    }
}