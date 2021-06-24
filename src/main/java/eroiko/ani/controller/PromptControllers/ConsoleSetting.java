/*
 * Author : Shiritai (楊子慶, or Eroiko on Github) at 2021/06/18.
 * See https://github.com/Shiritai/wallpaper_master for more information.
 * Created using VSCode.
 */
package eroiko.ani.controller.PromptControllers;

import java.net.URL;
import java.util.ResourceBundle;

import eroiko.ani.MainApp;
import eroiko.ani.controller.PromptControllers.ConsoleController.ConsoleLabel;
import eroiko.ani.controller.SupportController.MyAlert;
import eroiko.ani.util.NeoWallpaper.WallpaperPath;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.effect.BoxBlur;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.util.StringConverter;

public class ConsoleSetting implements Initializable {

    private static boolean opened = false;
    private static Stage stage;
    private static Slider OpacityBar;
    private static TextField Opacity;
    private static Slider BlurBar;
    private static TextField Blur;
    private static Button ChooseButton;
    private static Button ClearButton;
    private static ColorPicker TextColor;
    private static ColorPicker BackColor;
    private static Button ClearTextButton;
    private static Button ClearBackButton;
    
    public static void OpenConsoleSetting(){
        if (!opened){
            try {
                stage = new Stage();
                stage.setTitle("Terminal Settings");
                stage.setScene(new Scene(FXMLLoader.load(WallpaperPath.FXML_SOURCE_PATH.resolve("ConsoleSetting.fxml").toAbsolutePath().toUri().toURL())));
                stage.setResizable(false);
                stage.getIcons().add(MainApp.icon);
                stage.setOnCloseRequest(e -> opened = false);
                stage.show();
                opened = true;
            } catch (Exception e){
                e.printStackTrace();
            }
        }
        else {
            MyAlert.OpenMyAlert(
                MyAlert.AlertType.INFORMATION,
                "Setting Opened",
                "You've already opened Terminal Settings :)"
            );
        }
    }
    
    @FXML private Slider opacityBar;
    @FXML private TextField opacity;
    @FXML private Slider blurBar;
    @FXML private TextField blur;
    @FXML private Button chooseButton;
    @FXML private Button clearButton;
    @FXML private ColorPicker textColor;
    @FXML private ColorPicker backColor;
    @FXML private Button clearTextButton;
    @FXML private Button clearBackButton;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        /* Connect to static variables */
        Opacity = opacity;
        OpacityBar = opacityBar;
        Blur = blur;
        BlurBar = blurBar;
        ChooseButton = chooseButton;
        ClearButton = clearButton;
        TextColor = textColor;
        BackColor = backColor;
        ClearBackButton = clearBackButton;
        ClearTextButton = clearTextButton;
        /* bind */
        Bindings.bindBidirectional(Opacity.textProperty(), OpacityBar.valueProperty(), new StringConverter<Number>(){
            @Override
            public Number fromString(String arg0) {
                return (arg0 != null) ? Double.valueOf(arg0) : 0.;
            }
            @Override
            public String toString(Number arg0) {
                var tmp = arg0.toString();
                return (arg0 != null) ? tmp.substring(0, (tmp.length() > 4) ? 4 : tmp.length()) : "";
            }
        });
        Bindings.bindBidirectional(Blur.textProperty(), BlurBar.valueProperty(), new StringConverter<Number>(){
            @Override
            public Number fromString(String arg0) {
                return (arg0 != null) ? Double.valueOf(arg0) : 0.;
            }
            @Override
            public String toString(Number arg0) {
                var tmp = arg0.toString();
                return (arg0 != null) ? tmp.substring(0, (tmp.length() > 4) ? 4 : tmp.length()) : "";
            }
        });
        /* init value */
        OpacityBar.setValue(95);
        BlurBar.setValue(24);
        BackColor.setValue(Color.BLACK);
        TextColor.setValue(Color.WHITE);
    }

    public static void SyncProperty(DoubleProperty op, BoxBlur br){
        BlurBar.valueProperty().addListener(e -> {
            br.setWidth(BlurBar.getValue() / 12);
            br.setHeight(BlurBar.getValue() / 16);
        });
        OpacityBar.valueProperty().addListener(e -> {
            op.set(OpacityBar.getValue() / 100);
        });
    }

    public static void SyncImgSelection(ImageView image){
        ChooseButton.setOnMouseClicked(e -> {
            var tmp = new FileChooser();
            tmp.setInitialDirectory(WallpaperPath.DEFAULT_DATA_PATH.toFile());
            tmp.setTitle("Choose background wallpaper");
            try {
                tmp.getExtensionFilters().addAll(new ExtensionFilter("Image Files", "*.jpg", "*.jpeg", "*.png"));
                image.setImage(new Image(
                    tmp.showOpenDialog(null).toPath().toAbsolutePath().toUri().toURL().toString(),
                    1280, 1280, true, false
                ));
                image.setFitWidth(1426.);
                image.setFitHeight(1426.);
            } catch (Exception ex){} // 表示沒做選擇, InvocationTargetException
        });
        ClearButton.setOnMouseClicked(e -> {
            image.imageProperty().set(null);
        });
    }

    public static void SyncColor(StackPane pane, ConsoleLabel console){
        TextColor.valueProperty().addListener(e -> {
            console.setTextFill(TextColor.getValue());
            console.txt.setStyle("-fx-text-fill:" + format(TextColor.getValue()) + ";");
        });
        BackColor.valueProperty().addListener(e -> {
            pane.setStyle(
                "-fx-control-inner-background: " + format(BackColor.getValue()) + ";" + 
                "-fx-background-color: " + format(BackColor.getValue()) + ";"
            );
        });
        ClearBackButton.setOnMouseClicked(e -> BackColor.setValue(Color.BLACK));
        ClearTextButton.setOnMouseClicked(e -> TextColor.setValue(Color.WHITE));
    }

    private static String format(Color c) {
        int r = (int) (255 * c.getRed());
        int g = (int) (255 * c.getGreen());
        int b = (int) (255 * c.getBlue());
        return String.format("#%02x%02x%02x", r, g, b);
    }
}
