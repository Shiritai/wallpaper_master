/*
 * Author : Shiritai (楊子慶, or Eroiko on Github) at 2021/06/13.
 * See https://github.com/Shiritai/wallpaper_master for more information.
 * Created using VSCode.
 */
package eroiko.ani.deprecated;

import java.nio.file.Path;

import eroiko.ani.deprecated.controller.MusicController;
import eroiko.ani.util.MediaClass.MediaOperator;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

/** {@code <deprecated>} */
@Deprecated
public class MusicBox {
    public static MusicBox musicBox = new MusicBox();

    private MediaOperator mop;
    public MediaPlayer player;
    
    private final int PROCESSING = 0;
    private final int COMPLETE = 1;
    private final int CERTAIN = 2;
    private final int CURRENT = 3;
    private final int RANDOM = 4;
    private final int NEXT = 5;
    private final int PREVIOUS = 6;

    /** 
     * Processing Music 會持續撥放音樂到被 interrupt;
     * Complete Music 只會撥放一次
     */
    public MusicBox(){
        mop = MediaOperator.playBox;
        player = new MediaPlayer(mop.getCurrentMedia());
    }

    public void playOrPause(){
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
    
    private void play(int type, Path path){
        if (MediaPlayer.Status.PLAYING.equals(player.getStatus())){
            player.stop();
        }
        player = switch (type) {
            case PROCESSING -> new MediaPlayer(MediaOperator.getDefaultProcessingMedia());
            case COMPLETE -> new MediaPlayer(MediaOperator.getDefaultCompleteMedia());
            case CURRENT -> new MediaPlayer(mop.getCurrentMedia());
            case CERTAIN -> new MediaPlayer(new Media(path.toAbsolutePath().toString()));
            case RANDOM -> new MediaPlayer(mop.getRandomMedia());
            case NEXT -> new MediaPlayer(mop.getNextMedia());
            case PREVIOUS -> new MediaPlayer(mop.getPreviousMedia());
            default -> throw new IllegalArgumentException("Error type of image!");
        };
        if (type == PROCESSING){
            player.setCycleCount(MediaPlayer.INDEFINITE);
        }
        player.currentTimeProperty().addListener((a, b, c) -> MusicController.progress.setValue(c.toSeconds()));
        player.setOnReady(() -> {
            var total = mop.getCurrentMedia().getDuration();
            MusicController.progressMax.set(total.toSeconds());
        });
        player.setVolume(0.025);
        player.play();
    }

    public void play(int type){
        if (type == CERTAIN){
            throw new IllegalArgumentException("Please assign certain music path when calling play(certain, path)");
        }
        play(type, null);
    }

    public void playCurrentMusic(){
        play(CURRENT);
    }

    public void playNextMusic(){
        play(NEXT);
    }

    public void playPreviousMusic(){
        play(PREVIOUS);
    }
    
    public void playCertain(Path path){
        play(CERTAIN, path);
    }
    
    public void playComplete(){
        play(COMPLETE);
    }
    
    public void playProcessing(){
        play(PROCESSING);
    }

    public void playRandom(){
        play(RANDOM);
    }

    public String getCurrentMusicName(){
        return mop.getCurrentMediaPath().getFileName().toString();
    }

    public StringProperty getCurrentNameProperty(){
        return new SimpleStringProperty(mop.getCurrentMediaPath().getFileName().toString());
    }

    public Duration getCurrentDuration(){
        return mop.getCurrentMedia().getDuration();
    }

    /** 幫助字串換行的函式 */
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
