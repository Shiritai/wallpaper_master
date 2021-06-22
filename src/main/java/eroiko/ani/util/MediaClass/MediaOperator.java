/*
 * Author : Shiritai (楊子慶, or Eroiko on Github) at 2021/06/13.
 * See https://github.com/Shiritai/wallpaper_master for more information.
 * Created using VSCode.
 */
package eroiko.ani.util.MediaClass;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;

import eroiko.ani.util.NeoWallpaper.WallpaperUtil;
import eroiko.ani.util.NeoWallpaper.WallpaperPath;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.media.Media;

public class MediaOperator {
    public static MediaOperator playBox = new MediaOperator();
    
    private ArrayList<Path> medias;
    private Path complete = null; // 00_0_xxx...
    private Path processing = null; // 00_1_xxx...
    private Path defaultComplete = null; // 00_0_xxx...
    private Path defaultProcessing = null; // 00_1_xxx...
    private Path rootPath;
    private int current;
    private double defaultProcessingDuration = 0;
    private double defaultCompleteDuration = 0;
    private boolean isDefaultMusic;
    
    /** 音樂查找與遍歷器 */
    private MediaOperator(Path musicPath, Path certainPath, boolean isDefaultMusic){
        this.isDefaultMusic = isDefaultMusic;
        rootPath = musicPath;
        try {
            var root = Files.newDirectoryStream(rootPath, "*.{mp3,wav}"); // also can use try-with-resource
            medias = new ArrayList<Path>();
            root.forEach(p -> {
                if (!p.getFileName().toString().startsWith("00_")){
                    medias.add(p);
                }
            });
            root.close();
        } catch (IOException e) {
            System.out.println(e.toString());
        }
        medias.sort((a, b) -> WallpaperUtil.pathNameCompare(a.getFileName(), b.getFileName())); // 直接沿用有何不可 OwO
        System.out.println("Dir : " + musicPath);
        System.out.println("Music : " + certainPath);
        current = (certainPath != null) ? medias.indexOf(certainPath) : 0;
        System.out.println("Current : " + current);
    }
    
    /** 
     * 音樂查找與遍歷器, 此建構子所建立的 MediaOperator 不支援所有 processing/complete music 的增刪改查 method,
     * <p> 若調用的話必將收到不可預期結果及 Exception
     * @param musicPath : 自定義資料夾
     * @param certainPath : 第一個音樂
     */
    public MediaOperator(Path musicPath, Path certainPath){
        this(musicPath, certainPath, false);
    }

    /** 
     * 音樂查找與遍歷器, 此建構子所建立的 MediaOperator 不支援所有 processing/complete music 的增刪改查 method,
     * <p> 若調用的話必將收到不可預期結果及 Exception
     * @param musicPath : 自定義資料夾
     */
    public MediaOperator(Path musicPath){
        this(musicPath, null, false);
    }
    
    /* 單一音樂播放器 */
    public MediaOperator(){
        this(WallpaperPath.DEFAULT_MUSIC_PATH, null, true);
    }

    private void initDefault(){
        boolean hasProcessing = false;
        boolean hasComplete = false;
        try {
            try (var dirStream = Files.newDirectoryStream(rootPath, "*.{mp3,wav}")){
                for (var p : dirStream){
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
        defaultComplete = medias.get(0);
        defaultProcessing = medias.get(1);
        new Thread(() -> {
            defaultCompleteDuration = getDefaultCompleteMedia().getDuration().toSeconds();
            defaultProcessingDuration = getDefaultProcessingMedia().getDuration().toSeconds();
        }).start();
    }

    public void setDefault(Path path, boolean isComplete){
        if (isComplete && complete != null){
            playBox.complete.toFile().delete();
        }
        else if (!isComplete && processing != null){
            playBox.processing.toFile().delete();
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

    private void addNewMusic(Path path) throws IOException{
        var newMusic = Path.of(
            rootPath.toString()
             + String.format("\\%s%d_", (medias.size() + 1 < 10) ? "0" : "", medias.size() + 1)
             + path.getFileName()
        );
        Files.copy( // 複製目標
            path, newMusic,
            StandardCopyOption.REPLACE_EXISTING
        );
        medias.add(newMusic);
    }

    private void addNewMusic(Path path, int serialNumber) throws IOException{
        var newMusic = Path.of(
            rootPath.toString()
             + String.format("\\%s%d_", (medias.size() + 1 < 10) ? "0" : "", serialNumber)
             + path.getFileName()
        );
        Files.copy( // 複製目標
            path, newMusic,
            StandardCopyOption.REPLACE_EXISTING
        );
        medias.add(newMusic);
    }

    public void addNewMusic(Path path, boolean isComplete) throws IOException{
        addNewMusic(path);
        setDefault(path, isComplete);
    }

    /** return new music path */
    private Path addMusicToDefault(){
        var path = medias.get(current);
        var newMusic = Path.of(
            WallpaperPath.DEFAULT_MUSIC_PATH.toString()
             + String.format("\\%s%d_", (playBox.medias.size() + 1 < 10) ? "0" : "", playBox.medias.size() + 1)
             + path.getFileName()
        );
        try {
            Files.copy( // 複製目標
                path, newMusic,
                StandardCopyOption.REPLACE_EXISTING
            );
        } catch (IOException ie){
            System.out.println(ie.toString());
        }
        return newMusic;
    }

    /** 針對 Music With Akari, 將當前播放音樂加入預設 Music Directory */
    public void addCurrentMusic(){
        playBox.medias.add(addMusicToDefault());
    }
    
    /** 針對 Music With Akari, 將當前播放音樂加入預設 Music Directory, 並初始化為預設 Processing */
    public void addCurrentMusicToProcessing(){
        var newMusic = addMusicToDefault();
        playBox.medias.add(newMusic);
        playBox.setDefault(newMusic, false);
    }
    
    /** 針對 Music With Akari, 將當前播放音樂加入預設 Music Directory, 並初始化為預設 Complete */
    public void addCurrentMusicToComplete(){
        var newMusic = addMusicToDefault();
        playBox.medias.add(newMusic);
        playBox.setDefault(newMusic, true);
    }

    /** 針對任意 import 使用 */
    public static void addNewCompleteToDefault(Path path) throws IOException{
        var newMusic = playBox.addMusicToDefault();
        playBox.medias.add(newMusic);
        playBox.setDefault(newMusic, true);
    }
    
    /** 針對任意 import 使用 */
    public static void addNewProcessingToDefault(Path path) throws IOException{
        var newMusic = playBox.addMusicToDefault();
        playBox.medias.add(newMusic);
        playBox.setDefault(newMusic, false);
    }

    private String shiftToDefault(Path path, boolean isComplete) {
        if (path.getParent().equals(WallpaperPath.DEFAULT_MUSIC_PATH)){
            String p = path.getFileName().toString();
            return ((isComplete) ? "00_0" : "00_1") + p.substring(p.indexOf('_'));
        }
        else {
            return ((isComplete) ? "00_0" : "00_1") + path.getFileName().toString();
        }
    }

    /** 當前音樂是 Processing */
    public boolean isProcessingMusic(){
        if (playBox.processing == null){
            playBox.initDefault();
        }
        if (isDefaultMusic){
            var now = medias.get(current).getFileName().toString();
            var proc = processing.getFileName().toString();
            if (now.substring(now.indexOf('_') + 1).equals(proc.substring(5))){
                return true;
            }
        }
        return false;
    }

    /** 當前音樂是 Complete */
    public boolean isCompleteMusic(){
        if (playBox.complete == null){
            playBox.initDefault();
        }
        if (isDefaultMusic){
            var now = medias.get(current).getFileName().toString();
            var comp = complete.getFileName().toString();
            if (now.substring(now.indexOf('_') + 1).equals(comp.substring(5))){
                return true;
            }
        }
        return false;
    }
    
    public boolean isDefaultComplete(){
        if (playBox.complete == null){
            playBox.initDefault();
        }
        if (isDefaultMusic){
            var now = medias.get(current).getFileName().toString();
            var comp = defaultComplete.getFileName().toString();
            if (now.substring(now.indexOf('_') + 1).equals(comp.substring(5))){
                return true;
            }
        }
        return false;
    }

    public boolean isDefaultProcessing(){
        if (playBox.processing == null){
            playBox.initDefault();
        }
        if (isDefaultMusic){
            var now = medias.get(current).getFileName().toString();
            var comp = defaultProcessing.getFileName().toString();
            if (now.substring(now.indexOf('_') + 1).equals(comp.substring(5))){
                return true;
            }
        }
        return false;
    }

    public Path getCurrentMediaPath(){
        return medias.get(current);
    }
    
    public Media getMedia(int serialNumber){
        if (serialNumber >= medias.size() || serialNumber < 0){
            throw new IllegalArgumentException("Music index out of range : " + serialNumber);
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
    
    public static Media getDefaultCompleteMedia(){
        if (playBox.complete == null){
            playBox.initDefault();
        }
        try {
            var tmp = new Media(playBox.complete.toUri().toURL().toString());
            return tmp;
        } catch (Exception e) {
            System.out.println(e.toString());
            return null;
        }
    }

    public static String getDefaultCompleteName(){
        return playBox.complete.getFileName().toString();
    }

    public static double getDefaultCompleteMediaDuration(){
        return playBox.defaultCompleteDuration;
    }
    
    public static Media getDefaultProcessingMedia(){
        if (playBox.processing == null){
            playBox.initDefault();
        }
        try {
            var tmp = new Media(playBox.processing.toUri().toURL().toString());
            return tmp;
        } catch (Exception e) {
            System.out.println(e.toString());
            return null;
        }
    }
    
    public static String getDefaultProcessingName(){
        return playBox.processing.getFileName().toString();
    }

    public static double getDefaultProcessingMediaDuration(){
        return playBox.defaultProcessingDuration;
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
        int motoCurrent = current;
        while (motoCurrent == current){ // 至少不要 Random 出同一首歌
            current = (int) (Math.random() * medias.size());
        }
        return getMedia(current);
    }

    public void importAWholeMusicFolder(Path folder){
        new Thread(() -> {
            try {
                var root = Files.newDirectoryStream(folder, "*.{mp3,wav}");
                var service = Executors.newCachedThreadPool();
                var calls = new ArrayList<Callable<Boolean>>();
                WallpaperUtil.resetSerialNumber(medias.size() + 1);
                for (var p : root){
                    calls.add(() -> {
                        addNewMusic(p, WallpaperUtil.getIntSerialNumber());
                        System.out.println("Add : " + p);
                        return true;
                    });
                }
                service.invokeAll(calls);
            } catch (Exception ex){
                ex.printStackTrace();
            }
        }).start();
    }

    /** 清除所有預設音樂 */
    public void clean(){
        try { // use try-with-resource
            try (var root = Files.newDirectoryStream(rootPath, "*.{mp3,wav}")){
                for (var p : root){
                    if (p.getFileName().toString().startsWith("00_")){
                        System.out.println("Clean : " + p);
                        p.toFile().delete();
                    }
                }
            }
        } catch (IOException e) {
            System.out.println(e.toString());
        }
    }
    
    public static void cleanDefault(){
        try {
            try (var root = Files.newDirectoryStream(WallpaperPath.DEFAULT_MUSIC_PATH, "*.{mp3,wav}")){
                for (var p : root){
                    if (p.getFileName().toString().startsWith("00_")){
                        System.out.println("Clean : " + p);
                        p.toFile().delete();
                    }
                }
            }
        } catch (IOException e) {
            System.out.println(e.toString());
        }
    }
}