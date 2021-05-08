package eroiko.ani.controller.PrimaryControllers;

import java.net.URL;
import java.nio.file.Path;
import java.util.ResourceBundle;
import java.lang.Math;

import eroiko.ani.util.SourceRedirector;
import eroiko.ani.util.NeoWallpaper.Wallpaper;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;

public class WallpaperController implements Initializable{
    public static boolean quit;
    public static Path currentPath;
    
    public static final int REFRESH = 0;
    public static final int NEXT = 1;
    public static final int PREV = 2;
    public static final int ADD = 3;
    public static final int DEL = 4;
    
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
        "Bye bye, my fellow", "I love you then, even now. So may I kill you?",
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
        viewMode = wp.getCurrentFullPath().getParent().equals(SourceRedirector.defaultImagePath);
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
            view.setImage(new Image(getClass().getClassLoader().getResource("eroiko/ani/img/no_image.png").toString()));
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
}
