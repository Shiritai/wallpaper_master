/*
 * Author : Shiritai (楊子慶, or Eroiko on Github) at 2021/06/23.
 * See https://github.com/Shiritai/wallpaper_master for more information.
 * Created using VSCode.
 */
package eroiko.ani.controller.SupportController;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.util.ResourceBundle;

import eroiko.ani.MainApp;
import eroiko.ani.util.Method.Dumper;
import eroiko.ani.util.NeoWallpaper.WallpaperPath;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.stage.Stage;


/** 提供兩種自製 Alert */
public class MyAlert implements Initializable {
    
    public enum AlertType {
        ERROR("Error", "エラー", WallpaperPath.IMAGE_SOURCE_PATH.resolve("usagyuuun_error.gif")),
        EXCEPTION("Exception", "例外", WallpaperPath.IMAGE_SOURCE_PATH.resolve("usagyuuun_exception.gif")),
        INFORMATION("Information", "情報",WallpaperPath.IMAGE_SOURCE_PATH.resolve("usagyuuun_information.gif")),
        ;
        public final String name;
        public final String labelStr;
        public final Path path;
        private AlertType(String name, String labelStr, Path path){
            this.name = name;
            this.labelStr = labelStr;
            this.path = path;
        }
    }
    /* 準備傳給某實例 */
    private static String textMsg;
    private static String innerTitle;
    private static Path imagePath;
    private static Stage stage;
    private static String labelStr;
    
    /**
     * 打開自訂 Alert, 附圖片地址
     * @param type          Alert 種類
     * @param innerTitle    內部大標題
     * @param textMsg       詳細內容
     * @param imagePath     附圖的地址
     */
    public static void OpenMyAlert(AlertType type, String innerTitle, String textMsg, Path imagePath){
        MyAlert.textMsg = textMsg;
        MyAlert.innerTitle = innerTitle;
        MyAlert.imagePath = imagePath;
        labelStr = type.labelStr;
        try {
            stage = new Stage();
            stage.setTitle(type.name);
            stage.setScene(new Scene(FXMLLoader.load(WallpaperPath.FXML_SOURCE_PATH.resolve("SupportView/MyAlert.fxml").toUri().toURL())));
            stage.getIcons().add(MainApp.icon);
            stage.show();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 提交種類與例外內容來開啟自訂 Alert
     * @param type      Alert 種類
     * @param ex        Throwable
     * @param withImage Open with image
     */
    public static void OpenMyAlert(AlertType type, Throwable ex, boolean withImage){
        var tmp = new StringWriter();
        ex.printStackTrace(new PrintWriter(tmp));
        if (withImage){
            OpenMyAlert(type, ex.getMessage(), tmp.toString(), type.path);
        }
        else {
            OpenMyAlert(type, ex.getMessage(), tmp.toString());
        }
    }

    /**
     * 打開自訂 Alert
     * @param type          Alert 種類
     * @param innerTitle    內部大標題
     * @param textMsg       詳細內容
     */
    public static void OpenMyAlert(AlertType type, String innerTitle, String textMsg){
        MyAlert.textMsg = textMsg;
        MyAlert.innerTitle = innerTitle;
        MyAlert.imagePath = null;
        labelStr = type.labelStr;
        try {
            stage = new Stage();
            stage.setTitle(type.name);
            stage.setScene(new Scene(FXMLLoader.load(WallpaperPath.FXML_SOURCE_PATH.resolve("SupportView/MyShortAlert.fxml").toUri().toURL())));
            stage.getIcons().add(MainApp.icon);
            stage.show();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    @FXML private Label info;
    @FXML private TextArea textPlace;
    @FXML private ImageView imagePlace;
    @FXML private Button okButton;
    @FXML private Button copyMsg;
    @FXML private Label exceptionLabel;
    private Stage thisStage;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        thisStage = stage;
        stage.setResizable(false);
        if (imagePath != null){
            try {
                var image = new Image(imagePath.toAbsolutePath().toUri().toURL().toString());
                imagePlace.setImage(image);
                imagePlace.setOnMouseClicked(e -> {
                    if (e.getClickCount() == 2){
                        Dumper.cmdOpenPath(imagePath);
                    }
                });
            } catch (MalformedURLException e1) {
                e1.printStackTrace();
            }
        }
        textPlace.setText(textMsg);
        textPlace.setFont(MainApp.firaCode12);
        textPlace.getStylesheets().addAll(
            WallpaperPath.FXML_SOURCE_PATH.resolve("style/transparentTextArea.css")
            .toFile().toURI().toString()
        );
        textPlace.setEditable(false);
        info.setText(innerTitle);
        exceptionLabel.setFont(MainApp.minchoE60);
        exceptionLabel.setText(labelStr);
        okButton.setOnMouseClicked(e -> thisStage.close());
        copyMsg.setOnMouseClicked(e -> {
            var content = new ClipboardContent();
            content.putString(innerTitle);
            Clipboard.getSystemClipboard().setContent(content);
        });
    }
    
}
