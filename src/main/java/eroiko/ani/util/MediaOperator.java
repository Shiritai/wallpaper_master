package eroiko.ani.util;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

import eroiko.ani.MainApp;
import eroiko.ani.util.WallpaperClass.WallpaperComparator;
import javafx.scene.image.Image;
import javafx.scene.media.Media;


/** 實作類似 Iterator 的資料結構, 所有 get functions (除了 Current) 都會移動 Index */
public class MediaOperator {
    private Path directory;
    private DirectoryStream<Path> root;
    private ArrayList<Path> medias;
    private int initIndex = -1;
    private int current;
    /** 
     * @param directory : the testing directory or the media folder of this project
     * @param certain : is true if  {@code directory} is in testing mode
     * @param initMedia : set this first media you'd like to see with it's path
     * @throws IOException
     */
    public MediaOperator(String directory, boolean certain, Path initMedia) throws IOException{
        this.directory = Path.of(directory);
        if (certain){
            root = Files.newDirectoryStream(SourceRedirector.defaultMediaPath, "*.{mp3,wav,flac}");
        }
        else {
            root = Files.newDirectoryStream(Path.of(this.directory.toString()), "*.{mp3,wav,flac}");
            System.out.println(Path.of(this.directory.toString()));
        }
        medias = new ArrayList<Path>();
        root.forEach(p -> medias.add(p));
        medias.sort((a, b) -> WallpaperComparator.pathNameCompare(a.getFileName(), b.getFileName())); // 直接沿用有何不可 OwO
        if (initMedia != null){
            setInitMedia(initMedia);
            current = initIndex;
        }
    }
    /** 
     * @param directory : the testing directory or the media folder of this project
     * @param certain : is true if  {@code directory} is in testing mode
     * @throws IOException
     */
    public MediaOperator(String directory, boolean certain) throws IOException{
        this(directory, certain, null);
    }
    
    public MediaOperator() throws IOException{
        this(FileSystems.getDefault().getPath("").toAbsolutePath().toString(), MainApp.isTesting);
    }

    public boolean setInitMedia(Path p){
        return (initIndex = medias.indexOf(p)) != -1;
    }

    public Media getInitMedia(){
        try {
            return new Media(medias.get(initIndex).toUri().toURL().toString());
        } catch (Exception e) { // 包含 initIndex == -1 的例外
            System.out.println(e.toString());
            return null;
        }
    }

    public Path getCurrentMediaPath(){
        return medias.get(current);
    }
    
    public Path getNextMediaPath(){
        return medias.get(++current);
    }

    public Media getCurrentMedia(){
        try {
            return new Media(medias.get(current).toUri().toURL().toString());
        } catch (MalformedURLException e) {
            System.out.println(e.toString());
        }
        return null;
    }

    public Media getNextMedia(){
        try {
            return (++current < medias.size()) ? new Media(medias.get(current).toUri().toURL().toString()) : new Media(medias.get((current = 0)).toUri().toURL().toString());
        } catch (MalformedURLException e) {
            System.out.println(e.toString());
        }
        return null;
    }
    
    public Image getLastMedia(){
        try {
            return (--current >= 0) ? new Image(medias.get(current).toUri().toURL().toString()) : new Image(medias.get((current = medias.size() - 1)).toUri().toURL().toString());
        } catch (MalformedURLException e) {
            System.out.println(e.toString());
        }
        return null;
    }

    public static MediaOperator copy(MediaOperator wp) throws IOException{
        var newWp = new MediaOperator(wp.directory.toString(), MainApp.isTesting);
        newWp.current = wp.current;
        return newWp;
    }
}

