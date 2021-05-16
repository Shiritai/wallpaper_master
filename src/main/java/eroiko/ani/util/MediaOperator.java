package eroiko.ani.util;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;

import eroiko.ani.util.NeoWallpaper.WallpaperUtil;
import eroiko.ani.util.NeoWallpaper.WallpaperPath;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.media.Media;

public class MediaOperator {
    /** 單一一個音樂撥放器 */
    public static MediaOperator playBox = new MediaOperator();

    private DirectoryStream<Path> root;
    private ArrayList<Path> medias;
    private Path complete = null; // 00_0_xxx...
    private Path processing = null; // 00_1_xxx...
    private Path defaultComplete = null; // 00_0_xxx...
    private Path defaultProcessing = null; // 00_1_xxx...
    private int current;
    private double defaultProcessingDuration = 0;
    private double defaultCompleteDuration = 0;

    public MediaOperator(){
        try {
            root = Files.newDirectoryStream(WallpaperPath.defaultMusicPath, "*.{mp3,wav,flac}");
        } catch (IOException e) {
            System.out.println(e.toString());
        }
        medias = new ArrayList<Path>();
        root.forEach(p -> {
            if (!p.getFileName().toString().startsWith("00_")){
                medias.add(p);
            }
        });
        medias.sort((a, b) -> WallpaperUtil.pathNameCompare(a.getFileName(), b.getFileName())); // 直接沿用有何不可 OwO
        current = 0; // 從 0, 1 (default) 的下一個開始
        initDefault();
        defaultComplete = medias.get(0);
        defaultProcessing = medias.get(1);
        new Thread(() -> {
            defaultCompleteDuration = getDefaultCompleteMedia().getDuration().toSeconds();
            defaultProcessingDuration = getDefaultProcessingMedia().getDuration().toSeconds();
        }).start();
    }

    private void initDefault(){
        boolean hasProcessing = false;
        boolean hasComplete = false;
        try {
            for (var p : Files.newDirectoryStream(WallpaperPath.defaultMusicPath, "*.{mp3,wav,flac}")){
                var tmp = p.getFileName().toString();
                if (tmp.startsWith("00_0")){
                    hasComplete = true;
                    complete = p;
                }
                else if (tmp.startsWith("00_1")){
                    hasProcessing = true;
                    processing = p;
                }
            }
        } catch (IOException e) {
            System.out.println(e.toString());
        }
        if (!hasComplete){
            setDefault(medias.get(0), true);
        }
        if (!hasProcessing){
            setDefault(medias.get(1), false);
        }
    }

    public void setDefault(Path path, boolean isComplete){
        if (isComplete && complete != null){
            var file = complete.toFile();
            var del = new Thread(() -> {
                while (!file.canWrite());
                file.delete();
            });
            del.setDaemon(true);
            del.start();
        }
        else if (!isComplete && processing != null){
            var file = processing.toFile();
            var del = new Thread(() -> {
                while (!file.canWrite());
                file.delete();
            });
            del.setDaemon(true);
            del.start();
        }
        var newPath = Path.of(path.getParent().toString() + "\\" + shiftToDefault(path, isComplete));
        try {
            Files.copy( // 複製目標
                path, newPath,
                StandardCopyOption.REPLACE_EXISTING
            );
        } catch (IOException e) {
            System.out.println(e.toString());
        }
        if (isComplete){
            complete = newPath;
            new Thread(() -> {
                defaultCompleteDuration = getDefaultCompleteMedia().getDuration().toSeconds();
            }).start();
        }
        else {
            processing = newPath;
            new Thread(() -> {
                defaultProcessingDuration = getDefaultProcessingMedia().getDuration().toSeconds();
            }).start();
        }
    }

    public void restoreDefaultMusic(boolean isComplete){
        if (medias.size() > 1){
            setDefault(medias.get(isComplete ? 0 : 1), isComplete);
        }
    }
    
    public void setMusicToDefault(boolean isComplete){
        setDefault(medias.get(current), isComplete);
    }

    public void addNewMusic(Path path, boolean isComplete) throws IOException{
        var newMusic = Path.of(
            WallpaperPath.defaultMusicPath.toString()
             + String.format("\\%s%d_", (medias.size() + 1 < 10) ? "0" : "", medias.size() + 1)
             + path.getFileName()
        );
        Files.copy( // 複製目標
            path, newMusic,
            StandardCopyOption.REPLACE_EXISTING
        );
        medias.add(newMusic);
        setDefault(path, isComplete);
    }

    public void addNewCompleteToDefault(Path path) throws IOException{
        addNewMusic(path, true);
    }
    
    public void addNewProcessingToDefault(Path path) throws IOException{
        addNewMusic(path, false);
    }

    private String shiftSerialNumber(Path path, int afterNumber) {
        String p = path.getFileName().toString();
        return ((afterNumber < 10) ? "0" : "") + Integer.toString(afterNumber) + p.substring(p.indexOf('_'));
    }

    private String shiftToDefault(Path path, boolean isComplete) {
        String p = path.getFileName().toString();
        return ((isComplete) ? "00_0" : "00_1") + p.substring(p.indexOf('_'));
    }

    /** 當前音樂是 Processing */
    public boolean isProcessingMusic(){
        var now = medias.get(current).getFileName().toString();
        var proc = processing.getFileName().toString();
        if (now.substring(now.indexOf('_') + 1).equals(proc.substring(5))){
            return true;
        }
        return false;
    }

    /** 當前音樂是 Complete */
    public boolean isCompleteMusic(){
        var now = medias.get(current).getFileName().toString();
        var comp = complete.getFileName().toString();
        if (now.substring(now.indexOf('_') + 1).equals(comp.substring(5))){
            return true;
        }
        return false;
    }
    
    public boolean isDefaultComplete(){
        var now = medias.get(current).getFileName().toString();
        var comp = defaultComplete.getFileName().toString();
        if (now.substring(now.indexOf('_') + 1).equals(comp.substring(5))){
            return true;
        }
        return false;
    }

    public boolean isDefaultProcessing(){
        var now = medias.get(current).getFileName().toString();
        var comp = defaultProcessing.getFileName().toString();
        if (now.substring(now.indexOf('_') + 1).equals(comp.substring(5))){
            return true;
        }
        return false;
    }

    public Path getCurrentMediaPath(){
        return medias.get(current);
    }
    
    public Media getMedia(int serialNumber){
        if (serialNumber >= medias.size() || serialNumber < 0){
            throw new IllegalArgumentException("Music index out of range!");
        }
        try {
            var tmp = new Media(medias.get(serialNumber).toUri().toURL().toString());
            return tmp;
        } catch (Exception e) { // 包含 initIndex == -1 的例外
            System.out.println(e.toString());
            return null;
        }    
    }

    public String getCurrentMediaName(){
        return getCurrentMediaPath().getFileName().toString();
    }

    public StringProperty getCurrentMediaNameProperty(){
        return new SimpleStringProperty(getCurrentMediaPath().getFileName().toString());
    }
    
    public Media getDefaultCompleteMedia(){
        if (complete == null){
            throw new IllegalArgumentException("Music index out of range!");
        }
        try {
            var tmp = new Media(complete.toUri().toURL().toString());
            return tmp;
        } catch (Exception e) {
            System.out.println(e.toString());
            return null;
        }
    }

    public String getDefaultCompleteName(){
        return complete.getFileName().toString();
    }

    public double getDefaultCompleteMediaDuration(){
        return defaultCompleteDuration;
    }
    
    public Media getDefaultProcessingMedia(){
        if (processing == null){
            throw new IllegalArgumentException("Music index out of range!");
        }
        try {
            var tmp = new Media(processing.toUri().toURL().toString());
            return tmp;
        } catch (Exception e) {
            System.out.println(e.toString());
            return null;
        }
    }
    
    public String getDefaultProcessingName(){
        return processing.getFileName().toString();
    }

    public double getDefaultProcessingMediaDuration(){
        return defaultProcessingDuration;
    }
    
    public Media getCurrentMedia(){
        return getMedia(current);
    }
    
    public Media getNextMedia(){
        return getMedia((++current < medias.size()) ? current : (current = 0));
    }
    
    public Media getPreviousMedia(){
        return getMedia((current == 0) ? (current = medias.size() - 1) : --current);
    }
    
    public Media getRandomMedia(){
        current = (int) (Math.random() * medias.size());
        return getMedia(current);
    }
}