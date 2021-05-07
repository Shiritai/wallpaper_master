package eroiko.ani.controller.PrimaryControllers;

import java.net.URL;
import java.nio.file.Path;
import java.util.ResourceBundle;

import eroiko.ani.util.SourceRedirector;
import eroiko.ani.util.WallpaperClass.WallpaperImageWithFilter;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;

public class WallpaperChooseController implements Initializable{
    public static boolean quit;
    public static Path currentPath;
    public final static KeyCodeCombination CtrlPlus = new KeyCodeCombination(KeyCode.PLUS, KeyCodeCombination.CONTROL_DOWN);
    public final static KeyCodeCombination CtrlMinus = new KeyCodeCombination(KeyCode.MINUS, KeyCodeCombination.CONTROL_DOWN);
    
    WallpaperImageWithFilter wp;
    ImageView imageView;
    private int size;
    
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

    
    @Override
    public void initialize(URL url, ResourceBundle rb){
        final int serialNumber = SourceRedirector.getSerialNumberImmediately();
        var tmp = SourceRedirector.getWallpaperImage(serialNumber);
        if (tmp instanceof WallpaperImageWithFilter){
            wp = (WallpaperImageWithFilter) tmp;
        }
        size = wp.getSize();
        numerator.setText(Integer.toString(size));
        denominator.setText(Integer.toString(size));
        // wp = SourceRedirector.wallpaperImageWithFilter;
        view.setImage(wp.getCurrentWallpaper());
        imageView = view;
        currentPath = wp.getCurrentWallpaperPath();
        wallpaperName.setText("Current Wallpaper : " + currentPath.getFileName().toString());
        wallpaperPosition.setText("Wallpaper Path : " + currentPath.toString());
        setMouseBehavior();
        stackPane.setOnKeyPressed(e -> {
            if (e.getCode().equals(KeyCode.RIGHT)){
                switchNextImage();
            }
            else if (e.getCode().equals(KeyCode.LEFT)){
                switchPreviousImage();
            }
        });
    }
    
    private void setMouseBehavior(){
        stackPane.setOnScroll(e -> {
            switchImage(e.getDeltaY());
            e.consume();
        });
        
        addImage.setOnMouseEntered(e -> addImage.setOpacity(0.2));
        addImage.setOnMouseExited(e -> addImage.setOpacity(1.));
        addImage.setOnMouseClicked(e -> {
            wp.add();
            numerator.setText(Integer.toString(wp.getSize()));
            if (wp.isEmpty()){
                view.setImage(new Image(getClass().getClassLoader().getResource("eroiko/ani/img/no_image.png").toString()));
            }
            refresh();
        });
        
        deleteImage.setOnMouseEntered(e -> deleteImage.setOpacity(0.2));
        deleteImage.setOnMouseExited(e -> deleteImage.setOpacity(1.));
        deleteImage.setOnMouseClicked(e -> { 
            wp.delete();
            numerator.setText(Integer.toString(wp.getSize()));
            if (wp.isEmpty()){
                view.setImage(new Image(getClass().getClassLoader().getResource("eroiko/ani/img/no_image.png").toString()));
            }
            refresh();
        });

        next.setOnMouseEntered(e -> next.setOpacity(0.2));
        next.setOnMouseExited(e -> next.setOpacity(1.));
        next.setOnMouseClicked(e -> switchNextImage());
        
        previous.setOnMouseEntered(e -> previous.setOpacity(0.2));
        previous.setOnMouseExited(e -> previous.setOpacity(1.));
        previous.setOnMouseClicked(e -> switchPreviousImage());

        wallpaperName.setOnMouseEntered(e -> wallpaperName.setOpacity(0.2));
        wallpaperName.setOnMouseExited(e -> wallpaperName.setOpacity(1.));

        wallpaperPosition.setOnMouseEntered(e -> wallpaperPosition.setOpacity(0.2));
        wallpaperPosition.setOnMouseExited(e -> wallpaperPosition.setOpacity(1.));

        choosePercentage.setOnMouseEntered(e -> choosePercentage.setOpacity(0.2));
        choosePercentage.setOnMouseExited(e -> choosePercentage.setOpacity(1.));

        stackPane.addEventHandler(KeyEvent.KEY_PRESSED, e -> {
            System.out.println("Meow!?");
            if (e.getCode().equals(KeyCode.RIGHT)){
                System.out.println("1");
                wp.getNextWallpaper();
            }
            else if (e.getCode().equals(KeyCode.LEFT)){
                System.out.println("2");
                wp.getLastWallpaper();
            }
            else if (e.getCode().equals(KeyCode.PLUS)){
                System.out.println("3");
                wp.add();
                view.setImage(new Image(getClass().getClassLoader().getResource("eroiko/ani/img/no_image.png").toString()));
            }
            else if (e.getCode().equals(KeyCode.MINUS)){
                System.out.println("4");
                wp.delete();
                view.setImage(new Image(getClass().getClassLoader().getResource("eroiko/ani/img/no_image.png").toString()));
            }
            e.consume();
        });
        stackPane.requestFocus();
    }
    
    private void switchImage(double direction){
        if (direction < 0){
            switchNextImage();
        }
        else if (direction > 0){
            switchPreviousImage();
        }
    }
    
    private void switchNextImage(){
        if (wp.isEmpty()){
            wallpaperName.setText("");
            wallpaperPosition.setText("You have choose all the images!" );
        }
        else {
            view.setImage(wp.getNextWallpaper());
            currentPath = wp.getCurrentWallpaperPath();
            wallpaperName.setText("Current Wallpaper : " + currentPath.getFileName().toString());
            wallpaperPosition.setText("Wallpaper Path : " + currentPath.toString());
        }
    }
    
    private void switchPreviousImage(){
        if (wp.isEmpty()){
            wallpaperName.setText("");
            wallpaperPosition.setText("You have choose all the images!");
        }
        else {
            view.setImage(wp.getLastWallpaper());
            currentPath = wp.getCurrentWallpaperPath();
            wallpaperName.setText("Current Wallpaper : " + currentPath.getFileName().toString());
            wallpaperPosition.setText("Wallpaper Path : " + currentPath.toString());    
        }
    }
    
    private void refresh(){
        if (wp.isEmpty()){
            wallpaperName.setText("");
            wallpaperPosition.setText("You have choose all the images!");
        }
        else {
            view.setImage(wp.getCurrentWallpaper());
            currentPath = wp.getCurrentWallpaperPath();
            wallpaperName.setText("Current Wallpaper : " + currentPath.getFileName().toString());
            wallpaperPosition.setText("Wallpaper Path : " + currentPath.toString());
        }
    }

}