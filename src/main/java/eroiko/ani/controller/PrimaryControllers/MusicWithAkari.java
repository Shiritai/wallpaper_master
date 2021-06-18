/*
 * Author : Shiritai (楊子慶, or Eroiko on Github) at 2021/06/13.
 * See https://github.com/Shiritai/wallpaper_master for more information.
 * Created using VSCode.
 */
package eroiko.ani.controller.PrimaryControllers;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.ResourceBundle;

import eroiko.ani.MainApp;
import eroiko.ani.util.MediaClass.MediaOperator;
import eroiko.ani.util.Method.CarryReturn;
import eroiko.ani.util.Method.Dumper;
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
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.media.MediaPlayer;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.util.StringConverter;

public class MusicWithAkari implements Initializable {
    /* Player part */
    public static final int PROCESSING = 0;
    public static final int COMPLETE = 1;
    public static final int CERTAIN = 2;
    public static final int CURRENT = 3;
    public static final int RANDOM = 4;
    public static final int NEXT = 5;
    public static final int PREVIOUS = 6;
    public MediaOperator box;
    public MediaPlayer player;
    /** True : play, False : pause */
    private BooleanProperty playOrPauseImageSwitcher;
    private DoubleProperty volume = new SimpleDoubleProperty(0.025);
    private DoubleProperty progress = new SimpleDoubleProperty(0.);
    private DoubleProperty maxProgress = new SimpleDoubleProperty(0.);
    private StringProperty nameOfMusic = new SimpleStringProperty();
    
    private static HashMap<Path, Stage> map = new HashMap<>();
    private static Path temporaryPath;
    private Path mediaBoxPath;

    /* Easter egg part */
    private static final MediaOperator akari = new MediaOperator(WallpaperPath.DEFAULT_MUSIC_PATH.resolve("Akari"));
    
    public void playOrPause(){
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

    private void play(int type){
        player.stop(); // 終止當前音樂
        player = switch (type) {
            case CURRENT -> new MediaPlayer(box.getCurrentMedia());
            case RANDOM -> new MediaPlayer(box.getRandomMedia());
            case NEXT -> new MediaPlayer(box.getNextMedia());
            case PREVIOUS -> new MediaPlayer(box.getPreviousMedia());
            default -> throw new IllegalArgumentException("Error type of image!");
        };
        player.currentTimeProperty().addListener((a, b, c) -> progress.setValue(c.toSeconds()));
        player.volumeProperty().addListener(e -> volume.set(player.getVolume()));
        player.setVolume(volume.get());
        player.setOnEndOfMedia(() -> {
            if (loop.getOpacity() == 1){
                if (shuffleButton.getOpacity() == 1){
                    play(RANDOM);
                }
                else {
                    play(NEXT);
                }
                refresh();
            }
        });
        System.out.println(box.getCurrentMediaName());
        nameOfMusic.set(box.getCurrentMediaName());
        player.setOnReady(() -> {
            maxProgress.set(player.getTotalDuration().toSeconds());
        });
        player.play();
    }

    /* Open window */
    private static Image playImage = null;
    private static Image pauseImage = null;
    private static final int lengthOfMusicName = 30;

    public static void openMusicWithAkari(Path musicPath) throws IllegalArgumentException {
        if (!musicPath.toFile().exists() || !Dumper.isMusic(musicPath)){
            throw new IllegalArgumentException("File not exist!");
        }
        temporaryPath = musicPath;
        try {
            var stage = new Stage();
            map.put(musicPath, stage);
            stage.setTitle("Music with Akari");
            stage.setScene(new Scene(FXMLLoader.load(WallpaperPath.FXML_SOURCE_PATH.resolve("MusicWithAkari.fxml").toAbsolutePath().toUri().toURL())));
            stage.setResizable(false);
            stage.getIcons().add(MainApp.icon);
            stage.show();
        } catch (Exception e){
            e.printStackTrace();
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
    @FXML private Label musicName;
    @FXML private Text currentTime;
    @FXML private Text durationTime;
    @FXML private ImageView loop;
    @FXML private Label promptLabel;
    @FXML private ImageView importMusic;

    @FXML private Rectangle addToSyamiko;
    @FXML private Text addToSyamikoText;
    @FXML private Rectangle addToSyamikoDefault;
    @FXML private Text addToSyamikoDefaultText;
    @FXML private Circle sayHi;
    private int addDefaultSwitch = 0;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        mediaBoxPath = temporaryPath;
        box = new MediaOperator(mediaBoxPath.getParent(), mediaBoxPath);
        player = new MediaPlayer(box.getCurrentMedia());
        playOrPauseImageSwitcher = new SimpleBooleanProperty(player.getStatus() == MediaPlayer.Status.PLAYING);
        
        /* Font settings */
        musicName.setFont(MainApp.notoSansCJK22);
        durationTime.setFont(MainApp.firaCode12);
        currentTime.setFont(MainApp.firaCode12);
        addToSyamikoText.setFont(MainApp.firaCode13);
        addToSyamikoDefaultText.setFont(MainApp.firaCode13);

        musicName.setOnMouseClicked(e -> musicName.setText(CarryReturn.stripTypeAndSerialNumberForMusicWithAkari(box.getCurrentMediaName(), lengthOfMusicName)));
        if (pauseImage == null){
            try {
                pauseImage = new Image(WallpaperPath.IMAGE_SOURCE_PATH.resolve("pause.png").toUri().toURL().toString());
                playImage = new Image(WallpaperPath.IMAGE_SOURCE_PATH.resolve("play.png").toUri().toURL().toString());
            } catch (MalformedURLException e1) {
                e1.printStackTrace();
            }
        }

        loop.setOpacity(0.5);
        loop.setOnMouseClicked(e -> activateLoop());

        shuffleButton.setOpacity(0.5);
        shuffleButton.setOnMouseClicked(e -> activateShuffle());

        addToSyamiko.setOnMouseClicked(e -> activateAdd());
        addToSyamikoText.setOnMouseClicked(e -> activateAdd());
        addToSyamikoDefault.setOnMouseClicked(e -> activateAddToDefault());
        addToSyamikoDefault.setOnScroll(e -> activateSwitchAddToDefault(e.getDeltaY()));
        addToSyamikoDefaultText.setOnMouseClicked(e -> activateAddToDefault());
        addToSyamikoDefaultText.setOnScroll(e -> activateSwitchAddToDefault(e.getDeltaY()));

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
        
        musicName.setText(CarryReturn.stripTypeAndSerialNumberForMusicWithAkari(box.getCurrentMediaName(), lengthOfMusicName));
        Bindings.bindBidirectional(musicName.textProperty(), nameOfMusic, new StringConverter<String>(){
            @Override
            public String fromString(String arg0) {
                return arg0.replace("\n", "");
            }
            @Override
            public String toString(String arg0) {
                return CarryReturn.stripTypeAndSerialNumberForMusicWithAkari(arg0, lengthOfMusicName);
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
        map.get(mediaBoxPath).addEventFilter(KeyEvent.KEY_PRESSED, e -> {
            switch (e.getCode()){
                case SPACE -> activatePlay();
                case RIGHT -> activateNext();
                case LEFT-> activatePrevious();
                case UP -> activateVolumeUp();
                case DOWN -> activateVolumeDown();
                case R -> activateRandom();
                case S -> activateShuffle();
                case L -> activateLoop();
                case A -> activateAdd();
                case PLUS -> activateAddToDefault();
                case PERIOD -> activateSwitchAddToDefault(1.);
                case COMMA -> activateSwitchAddToDefault(-1.);
                case DIGIT0, NUMPAD0 -> { play(CURRENT); refresh(); }
                default -> {}
            }
            e.consume();
        });

        map.get(mediaBoxPath).setOnCloseRequest(e -> {
            box.clean();
            player.stop();
            System.out.println("Close Music With Akari");
        });

        sayHi.setOpacity(0);
        sayHi.setOnMouseClicked(e -> {
            var p = new MediaPlayer(akari.getRandomMedia());
            p.setVolume(volumeBar.getValue() / 500);
            p.play();
        });
        
        setPromptLabel();
        refresh();
        playMusicButton.setImage(playImage);
    }
    
    private void refresh(){
        playMusicButton.setImage(pauseImage);
        nameOfMusic.set(box.getCurrentMediaName());
        addToSyamikoText.setText("Add");
        addToSyamikoText.setOpacity(0.5);
        addToSyamikoDefaultText.setOpacity(0.5);
        addDefaultSwitch = NONE;
        addToSyamikoDefaultText.setText(switch (addDefaultSwitch){
            case NONE -> "____";
            case PREV -> "Proc";
            case COMP -> "Comp";
            default -> "NULL";
        });
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
        
        addToSyamiko.addEventFilter(MouseEvent.MOUSE_ENTERED, e -> promptLabel.setText("add this music to repo"));
        addToSyamikoText.addEventFilter(MouseEvent.MOUSE_ENTERED, e -> promptLabel.setText("add this music to repo"));
        addToSyamiko.addEventFilter(MouseEvent.MOUSE_EXITED, e -> promptLabel.setText(""));

        addToSyamikoDefault.addEventFilter(MouseEvent.MOUSE_ENTERED, e -> promptLabel.setText("add music to default"));
        addToSyamikoDefaultText.addEventFilter(MouseEvent.MOUSE_ENTERED, e -> promptLabel.setText("add music to default"));
        addToSyamikoDefault.addEventFilter(MouseEvent.MOUSE_EXITED, e -> promptLabel.setText(""));
    }
    
    private void activatePlay(){
        if (!currentTime.getText().equals("00:00") && currentTime.getText().equals(durationTime.getText())){
            play(CURRENT);
            refresh();
            return;
        }
        playOrPause();
        nameOfMusic.set(box.getCurrentMediaName());
        musicName.setText(CarryReturn.stripTypeAndSerialNumberForMusicWithAkari(box.getCurrentMediaName(), lengthOfMusicName));
    }

    private void activateNext(){
        play(NEXT);
        refresh();
    }

    private void activatePrevious(){
        play(PREVIOUS);
        refresh();
    }

    private void activateRandom(){
        play(RANDOM);
        refresh();
    }

    private void activateShuffle(){
        shuffleButton.setOpacity((shuffleButton.getOpacity() == 1) ? 0.5 : 1);
    }

    private void activateLoop(){
        loop.setOpacity((loop.getOpacity() == 1) ? 0.5 : 1);
    }

    private static final int NONE = 0;
    private static final int PREV = 1;
    private static final int COMP = 2;
    
    private void activateSwitchAddToDefault(double dir){
        addToSyamikoDefaultText.setOpacity(0.5);
        if (dir > 0){
            addDefaultSwitch = switch (addDefaultSwitch){
                case NONE -> PREV;
                case PREV -> COMP;
                case COMP -> NONE;
                default -> -1;
            };
        }
        else {
            addDefaultSwitch = switch (addDefaultSwitch){
                case NONE -> COMP;
                case PREV -> NONE;
                case COMP -> PREV;
                default -> -1;
            };    
        }
        addToSyamikoDefaultText.setText(switch (addDefaultSwitch){
            case NONE -> "____";
            case PREV -> "Proc";
            case COMP -> "Comp";
            default -> "NULL";
        });
    }

    private void activateAdd(){
        if (addToSyamikoText.getOpacity() != 1.){
            addToSyamikoText.setOpacity(1.);
            addToSyamikoText.setText("Added");
            box.addCurrentMusic();
        }
    }

    private void activateAddToDefault(){
        switch (addDefaultSwitch){
            case PREV -> {
                box.addCurrentMusicToProcessing();
                addToSyamikoDefaultText.setOpacity(1.);
            }
            case COMP -> {
                box.addCurrentMusicToComplete();
                addToSyamikoDefaultText.setOpacity(1.);
            }
            default -> addToSyamikoDefaultText.setOpacity(0.5);
        }
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