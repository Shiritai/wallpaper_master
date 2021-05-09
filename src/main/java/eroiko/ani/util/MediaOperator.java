package eroiko.ani.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;

import eroiko.ani.util.NeoWallpaper.WallpaperUtil;
import eroiko.ani.util.NeoWallpaper.WallpaperPath;
import javafx.scene.media.Media;


/** 第一個為 Complete music, 第二個為 processing music */
public class MediaOperator {
    public static MediaOperator playBox = new MediaOperator();

    private DirectoryStream<Path> root;
    private ArrayList<Path> medias;
    private int current;

    public MediaOperator(){
        try {
            root = Files.newDirectoryStream(WallpaperPath.defaultMusicPath, "*.{mp3,wav,flac}");
        } catch (IOException e) {
            System.out.println(e.toString());
        }
        medias = new ArrayList<Path>();
        root.forEach(p -> medias.add(p));
        medias.sort((a, b) -> WallpaperUtil.pathNameCompare(a.getFileName(), b.getFileName())); // 直接沿用有何不可 OwO
        current = 0; // 從 0, 1 (default) 的下一個開始
    }

    public void addNewMusic(Path path, boolean isComplete) throws IOException{
        var motoFirst = medias.get(isComplete ? 0 : 1);
        motoFirst.toFile().renameTo(new File(motoFirst.getParent().toString() + shiftSerialNumber(motoFirst, medias.size() + 1)));
        System.out.print("Default Music has renamed to : ");
        System.out.println(motoFirst);
        medias.add( // 把首位複製到最後
            motoFirst
        );
        var newMusic = Path.of(
            WallpaperPath.defaultMusicPath.toString()
             + String.format("\\0%d_", isComplete ? 1 : 2)
             + path.getFileName()
        );
        Files.copy( // 複製目標
            path, newMusic,
            StandardCopyOption.REPLACE_EXISTING
        );
        medias.set(isComplete ? 0 : 1, newMusic);
    }

    public void addNewCompleteToDefault(Path path) throws IOException{
        addNewMusic(path, true);
    }
    
    public void addNewProcessingToDefault(Path path) throws IOException{
        addNewMusic(path, false);
    }

    private String shiftSerialNumber(Path path, int afterNumber) {
        String p = path.getFileName().toString();
        return p.charAt(0) + Integer.toString(afterNumber) + p.substring(p.indexOf('_'));
    }

    public Path getCurrentMediaPath(){
        return medias.get(current);
    }
    
    public Media getMedia(int serialNumber){
        if (serialNumber >= medias.size() || serialNumber < 0){
            throw new IllegalArgumentException("Music index out of range!");
        }
        try {
            return new Media(medias.get(serialNumber).toUri().toURL().toString());
        } catch (Exception e) { // 包含 initIndex == -1 的例外
            System.out.println(e.toString());
            return null;
        }    
    }

    public String getCurrentMediaName(){
        return getCurrentMediaPath().getFileName().toString();
    }
    
    public Media getDefaultCompleteMedia(){
        return getMedia(0);
    }

    public Media getDefaultProcessingMedia(){
        return getMedia(1);
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
        return getMedia((int) (Math.random() * medias.size()));
    }
}