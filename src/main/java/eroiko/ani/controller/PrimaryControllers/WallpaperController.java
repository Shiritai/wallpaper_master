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
import java.io.IOException;
import java.lang.Math;

import eroiko.ani.MainApp;
import eroiko.ani.util.NeoWallpaper.Wallpaper;
import eroiko.ani.util.NeoWallpaper.WallpaperPath;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class WallpaperController implements Initializable{
    public static boolean quit;
    public static Path currentPath;
    public static boolean isPreview = false; // 用來傳遞是否已 Preview 開啟的變數, 每次調用 WallpaperView 前修改之, 基本交給 MainController.OpenWallpaper 處理
    
    public static final int REFRESH = 0;
    public static final int NEXT = 1;
    public static final int PREV = 2;
    public static final int ADD = 3;
    public static final int DEL = 4;

        
    /**
     * @param wp            the Wallpaper to open
     * @param isPreview     whether to use file choosing functions or not
     * @throws IOException
     */
    public static void OpenWallpaper(Wallpaper wp, boolean isPreview) throws IOException{
        int serialNumber = -1;
        if (wp != null){
            serialNumber = Wallpaper.addNewWallpaper(wp);
        }
        else {
            serialNumber = Wallpaper.getWallpaperSerialNumberImmediately();
            wp = Wallpaper.getWallpaper(serialNumber);
        }
        var fixedWp = wp;
        boolean prev = isPreview || wp.getCurrentFullPath().getParent().equals(WallpaperPath.DEFAULT_IMAGE_PATH);
        System.out.println("[Wallpaper Controller]  Is preview ? " + prev);
        WallpaperController.isPreview = prev;
        final int fixedSerialNumber = serialNumber;
        var stage = new Stage();
        System.out.println("[Wallpaper Controller]  Open Neo Wallpaper Viewer...");
        stage.setTitle("Neo Wallpaper Viewer");
        var wallpaperScene = new Scene(FXMLLoader.load(WallpaperPath.FXML_SOURCE_PATH.resolve("WallpaperWindow.fxml").toUri().toURL()));
        wallpaperScene.addEventFilter(KeyEvent.KEY_PRESSED, e -> {
            if (e.getCode() == KeyCode.RIGHT){
                if (!fixedWp.isEmpty()){
                    fixedWp.rightShift();
                }
            }
            else if (e.getCode() == KeyCode.LEFT){
                if (!fixedWp.isEmpty()){
                    fixedWp.leftShift();
                }
            }
            if (!prev){
                if (e.getCode() == KeyCode.EQUALS || e.getCode() == KeyCode.UP){
                    if (!fixedWp.isEmpty()){
                        fixedWp.add();
                    }
                    fixedWp.triggerChangedFlag();
                }
                else if (e.getCode() == KeyCode.MINUS || e.getCode() == KeyCode.DOWN){
                    if (!fixedWp.isEmpty()){
                        fixedWp.delete();
                    }
                    fixedWp.triggerChangedFlag();
                }
            }
            e.consume();
        });
        stage.setScene(wallpaperScene);
        stage.getIcons().add(MainApp.icon);
        stage.setOnCloseRequest(e -> {
            if (!prev){
                Wallpaper.appendToResultList(fixedSerialNumber);
                Wallpaper.executeResultAndCleanPreview();
            }
        });
        stage.show();
    }

    
    Wallpaper wp;
    public static ImageView imageView;
    
    @FXML private ImageView view;
    @FXML private StackPane stackPane;
    @FXML private Text wallpaperName;
    @FXML private Text wallpaperPosition;

    @FXML private Text next;
    @FXML private Text previous;

    @FXML private Text addImage;
    @FXML private Text deleteImage;

    @FXML private Group choosePercentage;
    @FXML private Text numerator;
    @FXML private Text denominator;
    @FXML private Text slash;
    @FXML private Text hint;

    private BooleanProperty isChangedListener;
    private int lastSize;
    private boolean viewMode;

    /* Creative msg */
    private String [] addHints = new String [] {
        "See you later!", "Nice to meet you~", "Love you :)", "How do you do?",
        "It's time to change waifu OwO", "Dinner, shower, or perhaps, me?",
        "Hi there, ...wait, isn't there too many waifus here?"
    };
    private String [] deleteHints = new String [] {
        "I'll never forget the promise we've made...", "I'll swear you forever :(",
        "Bye bye, my fellow", "I love you then, and even now. So may I kill you?",
        "How dare you!", "fxxk..."
    };

    @Override
    public void initialize(URL url, ResourceBundle rb){
        final int serialNumber = Wallpaper.getWallpaperSerialNumberImmediately();
        wp = Wallpaper.getWallpaper(serialNumber);
        /* set listener */
        isChangedListener = new SimpleBooleanProperty(false);
        wp.resetBooleanBind();
        isChangedListener.bind(wp.isChanged);
        isChangedListener.addListener((a, b, c) -> {
            if (!b && c){
                refresh();
                wp.isChanged.set(false);
            }
        });
        lastSize = wp.getSize();
        viewMode = isPreview;
        initFont();
        refresh();
        setMouseBehavior();
    }
    
    private void setMouseBehavior(){
        stackPane.setOnScroll(e -> {
            if (e.getDeltaY() > 0){
                switchImage(NEXT);
            }
            else if (e.getDeltaY() < 0){
                switchImage(PREV);
            }
            e.consume();
        });
        if (!viewMode){
            addImage.setOnMouseEntered(e -> addImage.setOpacity(0.2));
            addImage.setOnMouseExited(e -> addImage.setOpacity(1.));
            addImage.setOnMouseClicked(e -> switchImage(ADD));
            
            deleteImage.setOnMouseEntered(e -> deleteImage.setOpacity(0.2));
            deleteImage.setOnMouseExited(e -> deleteImage.setOpacity(1.));
            deleteImage.setOnMouseClicked(e -> switchImage(DEL));

            numerator.setText(Integer.toString(wp.getSize()));
            denominator.setText(Integer.toString(wp.length()));
        }
        else {
            addImage.setText("");
            deleteImage.setText("");
            numerator.setText("");
            denominator.setText("");
            slash.setText("");
        }
        hint.setText("");

        next.setOnMouseEntered(e -> next.setOpacity(0.2));
        next.setOnMouseExited(e -> next.setOpacity(1.));
        next.setOnMouseClicked(e -> switchImage(NEXT));
        
        previous.setOnMouseEntered(e -> previous.setOpacity(0.2));
        previous.setOnMouseExited(e -> previous.setOpacity(1.));
        previous.setOnMouseClicked(e -> switchImage(PREV));

        wallpaperName.setOnMouseEntered(e -> wallpaperName.setOpacity(0.2));
        wallpaperName.setOnMouseExited(e -> wallpaperName.setOpacity(1.));

        wallpaperPosition.setOnMouseEntered(e -> wallpaperPosition.setOpacity(0.2));
        wallpaperPosition.setOnMouseExited(e -> wallpaperPosition.setOpacity(1.));

        choosePercentage.setOnMouseEntered(e -> choosePercentage.setOpacity(0.2));
        choosePercentage.setOnMouseExited(e -> choosePercentage.setOpacity(1.));
    }

    private void switchImage(int behavior){
        if (wp.isEmpty()){
            wallpaperName.setText("");
            wallpaperPosition.setText("You have choose all the images!");
        }
        else {
            switch (behavior){
                case NEXT -> view.setImage(wp.getNextFullImage());
                case PREV -> view.setImage(wp.getPreviousFullImage());
                case ADD -> {
                    if (!wp.isEmpty()){
                        wp.add();
                    }
                }
                case DEL -> {
                    if (!wp.isEmpty()){
                        wp.delete();
                    }
                }
                default -> {}
            }
            refresh();
        }
    }
    
    public void refresh(){
        if (!viewMode){
            numerator.setText(Integer.toString(wp.getSize()));
        }
        if (lastSize != wp.getSize()){
            if (wp.addOrDeleteFlag){ // add
                hint.setText(addHints[(int) (Math.random() * addHints.length)]);
            }
            else { // delete
                hint.setText(deleteHints[(int) (Math.random() * deleteHints.length)]);
            }
            lastSize = wp.getSize();
        }
        if (wp.isEmpty()){
            try {
                view.setImage(new Image(WallpaperPath.IMAGE_SOURCE_PATH.resolve("no_image.png").toUri().toURL().toString()));
            } catch (MalformedURLException e) {
                System.out.println(e.toString());
            }
            wallpaperName.setText("");
            wallpaperPosition.setText("You have choose all the images!");
        }
        else {
            view.setImage(wp.getCurrentFullImage());
            currentPath = wp.getCurrentFullPath();
            wallpaperName.setText("Current Wallpaper : " + currentPath.getFileName().toString());
            wallpaperPosition.setText("Wallpaper Path : " + currentPath.toString());
        }
    }

    private void initFont(){
        wallpaperName.setFont(MainApp.firaCode16);
        wallpaperPosition.setFont(MainApp.firaCode16);
        
    }
}
