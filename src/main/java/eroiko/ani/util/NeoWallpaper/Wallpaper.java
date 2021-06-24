/*
 * Author : Shiritai (楊子慶, or Eroiko on Github) at 2021/06/13.
 * See https://github.com/Shiritai/wallpaper_master for more information.
 * Created using VSCode.
 */
package eroiko.ani.util.NeoWallpaper;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeSet;

import eroiko.ani.util.Method.Dumper;
import eroiko.ani.util.MyDS.myPair;
import eroiko.ani.util.MyDS.myTriple;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.image.Image;

public class Wallpaper {
    /* New wallpaper viewer, for outside classes! */
    private static final int wallpaperViewingSize = 300; // wallpaper preview 的 ImageView 大小, 至少...讓這個東西不要那麼 Magic Number
    /* 開放對外存取 Wallpaper 途徑 */
    private static HashMap<Integer, Wallpaper> wallpapersToFile = new HashMap<>(); // 不必排序, 僅要快速訪問 (O(1))
    private static int lastWallpaperNumber = 0;

    public static int getWallpaperSerialNumberImmediately(){ return lastWallpaperNumber; }
    public static Wallpaper getWallpaper(int serialNumber){ return wallpapersToFile.get(serialNumber); }

    public static int addNewWallpaper(Wallpaper wp){
        wallpapersToFile.put(++lastWallpaperNumber, wp);
        if (!wp.getCurrentFullPath().getParent().equals(WallpaperPath.DEFAULT_IMAGE_PATH) && 
            !wp.getCurrentFullPath().getParent().equals(WallpaperPath.DEFAULT_DATA_PATH) &&
            !wp.getCurrentFullPath().getParent().equals(WallpaperPath.DEFAULT_MUSIC_PATH) && 
            !wp.getCurrentFullPath().getParent().equals(WallpaperPath.DEFAULT_WALLPAPER_PATH) && 
            !wp.getCurrentFullPath().getParent().equals(WallpaperPath.getWallpaperPath()) &&
            wp.hasPreview()
        ){
            previewPathRec.add(wp.getCurrentPreviewPath().getParent()); // 最後要刪掉所有 Preview
        }
        return lastWallpaperNumber;
    }
    public static void deleteNewWallpaper(int serialNumber){ wallpapersToFile.remove(serialNumber); }
    
    /* 處理檔案增刪操作用資結 */
    private static myPair<TreeSet<Path>, TreeSet<Path>> resultList = new myPair<>(new TreeSet<>(), new TreeSet<>()); // 選好的資結的重要性 OwO
    private static ArrayList<Path> previewPathRec = new ArrayList<>();

    /** Append choices to resultList, i.e. myPair<toAdd, toDelete> */
    public static void appendToResultList(int serialNumber){
        var source = wallpapersToFile.get(serialNumber);
        for (var wpTri : source.wallpapers){
            if (wpTri.first == 1){
                resultList.key.add(wpTri.third);
            }
            else if (wpTri.first == -1){
                resultList.value.add(wpTri.third);
            }
        }
    }
    /** 執行 resultList 指定的複製, 刪除 */
    public static void executeResultAndCleanPreview(){
        /* execute resultList */
        /* 1. delete wallpapers */
        resultList.value.forEach(p -> {
            try {
                Files.delete(p);
            } catch (IOException e) {
                System.out.println("File not exist, so we can't delete it.");
            }
        });
        /* 2. copy wallpapers */
        var target = WallpaperPath.getWallpaperPath().toString();
        var targetDir = new File(target);
        if (!targetDir.exists()){
            targetDir.mkdirs();
        }
        WallpaperUtil.resetSerialNumber(); // 準備序列號種子, 填補空號的方式新增
        var numbers = new TreeSet<Integer>();
        try {
            try (var dirStream = Files.newDirectoryStream(WallpaperPath.getWallpaperPath())){
                for (var p : dirStream){
                    numbers.add(WallpaperUtil.getSerialNumberFromAWallpaper(p));
                }
            }
        } catch (IllegalArgumentException | IOException e1) {
            System.out.println(e1.toString());
        }
        resultList.key.forEach(p -> {
            while (numbers.contains(WallpaperUtil.peekSerialNumber())){
                WallpaperUtil.passSerialNumber();
            }
            try {
                Files.copy(p, 
                    Path.of(target + "\\" + "wallpaper" + WallpaperUtil.getSerialNumber() + WallpaperUtil.getFileType(p)), 
                    StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                System.out.println("File not exist, so we can't copy it.");
            }
        });
        resultList.key = new TreeSet<Path>();
        resultList.value = new TreeSet<Path>();
        /* Clean preview directories */
        previewPathRec.forEach(p -> {
            try {
                try (var dirStream = Files.newDirectoryStream(p)){
                    for (var pIn : dirStream){
                        try {
                            Files.delete(pIn);
                        } catch (IOException e) {
                            System.out.println("Previews does not exist, so we can't delete it.");
                        }
                    }
                }
            } catch (IOException e) {
                System.out.println(e.toString());
            }
            try { // 刪除 Directory
                Files.delete(p);
            } catch (IOException e) {
                System.out.println(e.toString());
            }
        });
        System.out.println("Wallpaper manipulation has been executed successfully.");
    }
    
    /* 實例部分 */
    private Path path;
    private Path prevPath;
    private int size; // 會隨著 +- 縮減
    private final int length; // 固定大小
    private ArrayList<myTriple<Integer, Path, Path>> wallpapers;
    public BooleanProperty isChanged;
    private boolean onlyFullFlag = false;
    public boolean addOrDeleteFlag = false;

    /* Iterator */
    private int current;
    private int initIndex;

    /** 
     * 實作類似含 Iterator 的 ArrayList, 統一管理 preview & full, 所有 get functions (除了 Current) 都會移動 Index
     * @param path : the directory of the images or  the path of the first image
     * @throws IOException
     * @throws IllegalArgumentException
     */
    public Wallpaper(Path path) throws IllegalArgumentException, IOException{
        if (!path.toFile().exists()){
            throw new IllegalArgumentException("File not exist!");
        }
        isChanged = new SimpleBooleanProperty(false);
        Path initPath = null;
        if (!Files.isDirectory(path)){
            if (!Dumper.isImage(path)){
                throw new IllegalArgumentException("Not a image file!");
            }
            initPath = path;
            path = path.getParent();
        }
        this.path = path;
        var tmpTarget = new File(path.resolve("previews").toString());

        wallpapers = new ArrayList<>();

        var fullArr = new ArrayList<Path>();
        try (var dirStream = Files.newDirectoryStream(this.path, "*.{jpg,jpeg,png,gif}")){
            dirStream.forEach(fullArr::add);
        }
        fullArr.sort(WallpaperUtil::pathNameCompare); // OwO
        if (fullArr.size() == 0){
            throw new IllegalArgumentException("Not a wallpaper folder!");
        }

        if (!tmpTarget.exists()){ // 確認理想極端狀況 (只有 full)
            fullArr.forEach(p -> wallpapers.add(new myTriple<>(0, null, p)));
            onlyFullFlag = true;
        }
        else {
            prevPath = tmpTarget.toPath();
            var prevArr = new ArrayList<Path>();
            try (var dirStream = Files.newDirectoryStream(this.prevPath, "*.{jpg,jpeg,png,gif}")){
                dirStream.forEach(p -> prevArr.add(p));
            }
            prevArr.sort(WallpaperUtil::pathNameCompare); // OwO
    
            int tmpSize = (fullArr.size() > prevArr.size()) ? fullArr.size() : prevArr.size();
            for (int i = 0, f = 0, p = 0; i < tmpSize; ++i){
                if (WallpaperUtil.matchNameWithoutFormat(prevArr.get(p), fullArr.get(f))){ // 基本上希望都是這情況
                    wallpapers.add(new myTriple<>(0, prevArr.get(p++), fullArr.get(f++)));
                }
                else {
                    // 未來考慮實作
                }
            }
        }
        
        size = wallpapers.size();
        length = size;
        if (initPath != null){
            for (int i = 0; i < length; ++i){
                if (wallpapers.get(i).third.equals(initPath)){
                    initIndex = i;
                    break;
                }
            }
        }
        else {
            initIndex = 0;
        }
        current = initIndex;
    }

    /** 
     * 建立預設圖庫的 Wallpaper, 實作類似含 Iterator 的 ArrayList, 統一管理 preview & full, 所有 get functions (除了 Current) 都會移動 Index 
     * @throws IOException
     */
    public Wallpaper() throws IOException{
        this(WallpaperPath.DEFAULT_IMAGE_PATH);
    }

    public void resetBooleanBind(){
        isChanged = new SimpleBooleanProperty(false);
    }

    public void triggerChangedFlag(){ isChanged.set(true); }

    public int rightShift(){
        while (wallpapers.get(current = (current + 1 == length) ? (current = 0) : current + 1).first != 0);
        isChanged.set(true);
        return current;
    }
    
    public int leftShift(){
        while (wallpapers.get(current = (current == 0) ? (current = length - 1) : current - 1).first != 0);
        isChanged.set(true);
        return current;
    }

    public void add(){
        wallpapers.get(current).first = 1;
        addOrDeleteFlag = true; // means add
        --size;
        if (size != 0){
            rightShift();
        }
    }
    public void delete(){
        wallpapers.get(current).first = -1;
        addOrDeleteFlag = false; // means delete
        --size;
        if (size != 0){
            rightShift();
        }
    }

    public int length(){ return length; }
    public int getSize(){ return size; }
    public boolean isEmpty(){ return size == 0;}
    
    public Path getCurrentPreviewPath(){
        if (onlyFullFlag){ // 理想極端情況
            return getCurrentFullPath();
        }
        return wallpapers.get(current).second;
    }

    public boolean hasPreview(){
        return !onlyFullFlag;
    }

    public Path getCurrentFullPath(){
        return wallpapers.get(current).third;
    }

    public Image getCurrentPreviewImage(){
        if (onlyFullFlag){ // 理想極端情況
            return getCurrentSmallImage();
        }
        try {
            return new Image(wallpapers.get(current).second.toUri().toURL().toString());
        } catch (MalformedURLException e) {
            System.out.println(e.toString());
            return null;
        }
    }

    public Image getCurrentFullImage(){
        try {
            return new Image(wallpapers.get(current).third.toUri().toURL().toString());
        } catch (MalformedURLException e) {
            System.out.println(e.toString());
        }
        return null;
    }

    /** 使用 WallpaperUtil.fetchScaledSmallImage 取得縮小且銳化後的圖片 */
    public Image getCurrentSmallImage(){
        return WallpaperUtil.fetchScaledSmallImage(wallpapers.get(current).third, wallpaperViewingSize);
    }
    
    public Path getPreviousPreviewPath(){
        if (onlyFullFlag){ // 理想極端情況
            return getPreviousFullPath();
        }
        return wallpapers.get(leftShift()).second;
    }
    
    public Path getPreviousFullPath(){
        return wallpapers.get(leftShift()).third;
    }
    
    public Image getPreviousPreviewImage(){
        if (onlyFullFlag){ // 理想極端情況
            return getPreviousSmallImage();
        }
        try {
            return new Image(wallpapers.get(leftShift()).second.toUri().toURL().toString());
        } catch (MalformedURLException e) {
            System.out.println(e.toString());
            return null;
        }
    }
    
    public Image getPreviousFullImage(){
        try {
            return new Image(wallpapers.get(leftShift()).third.toUri().toURL().toString());
        } catch (MalformedURLException e) {
            System.out.println(e.toString());
        }
        return null;
    }
    
    /** 使用 WallpaperUtil.fetchScaledSmallImage 取得縮小且銳化後的圖片 */
    public Image getPreviousSmallImage(){
        return WallpaperUtil.fetchScaledSmallImage(wallpapers.get(leftShift()).third, wallpaperViewingSize);
    }
    
    public Path getNextPreviewPath(){
        if (onlyFullFlag){ // 理想極端情況
            return getNextFullPath();
        }
        else {
            return wallpapers.get(rightShift()).second;
        }
    }
    
    public Path getNextFullPath(){
        return wallpapers.get(rightShift()).third;
    }
    
    public Image getNextPreviewImage(){
        if (onlyFullFlag){ // 理想極端情況
            return getNextSmallImage();
        }
        else {
            try {
                return new Image(wallpapers.get(rightShift()).second.toUri().toURL().toString());
            } catch (MalformedURLException e) {
                System.out.println(e.toString());
            }
            return null;
        }
    }
    
    public Image getNextFullImage(){
        try {
            return new Image(wallpapers.get(rightShift()).third.toUri().toURL().toString());
        } catch (MalformedURLException e) {
            System.out.println(e.toString());
        }
        return null;
    }
    
    /** 使用 WallpaperUtil.fetchScaledSmallImage 取得縮小且銳化後的圖片 */
    public Image getNextSmallImage(){
        return WallpaperUtil.fetchScaledSmallImage(wallpapers.get(rightShift()).third, wallpaperViewingSize);
    }
}