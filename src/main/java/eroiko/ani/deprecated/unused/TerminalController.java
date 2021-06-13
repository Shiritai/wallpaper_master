/*
 * Author : Shiritai (楊子慶, or Eroiko on Github) at 2021/06/13.
 * See https://github.com/Shiritai/wallpaper_master for more information.
 * Created using VSCode.
 */
package eroiko.ani.deprecated.unused;

// import java.io.IOException;
// import java.io.ObjectInputStream;
// import java.io.OutputStream;
// import java.io.PipedInputStream;
// import java.io.PipedOutputStream;
// import java.io.PrintStream;
import java.net.URL;
import java.util.ResourceBundle;

import eroiko.ani.MainApp;
import eroiko.ani.model.CLI.Console;
// import eroiko.ani.util.MyDS.ArrayByteToString;
// import eroiko.ani.util.MyDS.EditableLabel;
// import eroiko.ani.util.NeoWallpaper.WallpaperPath;
// import eroiko.ani.util.NeoWallpaper.WallpaperUtil;
// import javafx.application.Application;
// import javafx.concurrent.Service;
// import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
// import javafx.scene.Scene;
import javafx.scene.control.Label;
// import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
// import javafx.scene.image.Image;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
// import javafx.stage.Stage;

public class TerminalController implements Initializable {

    @FXML private VBox textBox;
    // private Font font;
    private double width;
    private Console console;
    // private static PipedInputStream pipIn;
    // private static PipedOutputStream pipOut;
    // private static Service<Void> textService; 
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // var hbox = new HBox();
        // font = MainApp.firaCode16;
        width = 1260.;

        // try {
            //     System.setOut(new PrintStream(new PipedOutputStream(pipIn)));
            // } catch (IOException ie){ ie.printStackTrace(); }
        // new OutputStream();
        var text = new ChoosableLabel(MainApp.firaCode16);
        // var text = new EditableLabel(MainApp.firaCode16);
        // try {
        //     pipIn = new PipedInputStream();
        //     console = new Console(, WallpaperPath.DEFAULT_DATA_PATH, WallpaperUtil::pathDirAndNameCompare, MainApp.hostName, MainApp.userName, true);
        // } catch (IOException e) {
        //     e.printStackTrace();
        // }

        // textService = new Service<Void>(){
        //     @Override
        //     protected Task<Void> createTask(){
        //         return new Task<Void>(){
        //             @Override
        //             protected Void call(){
        //                 while (true){
        //                     try {
        //                         if (pipIn.available() != 0){
        //                             var tmp = new ArrayByteToString();
        //                             do {
        //                                 int av = pipIn.available();
        //                                 if (av == 0) break;
        //                                 var b = new byte [av];
        //                                 pipIn.read(b); 
        //                                 tmp.add(b);
        //                             } while (!tmp.endsWith("\n") && !tmp.endsWith("\n"));
        //                             // text.appendText(tmp.toString());
        //                             text.setText(text.getText() + tmp.toString());
        //                         }
        //                     } catch (IOException ie){
        //                         System.out.println(ie.toString());
        //                     }
        //                 }
        //             }
        //         };
        //     }
        // };

        textBox.setPadding(new Insets(10., 10., 10., 10.));
        textBox.setAlignment(Pos.TOP_LEFT);
        textBox.setSpacing(5.);
        textBox.getChildren().add(text);

        var inputField = new TextField();
        textBox.getChildren().add(inputField);

        inputField.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER){
                try {
                    console.readLine(inputField.getText());
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
                inputField.setText("");
            }
        });
    }

    class ChoosableLabel extends Label {
        TextField txt;
        String saveTxt;
        public ChoosableLabel(Font font){
            this("", font);
        }
        public ChoosableLabel(String content, Font font){
            super(content);
            txt = new TextField();
            saveTxt = content;
            this.setOnMouseClicked(e -> {
                if (e.getButton() == MouseButton.PRIMARY){
                    txt.setText(saveTxt = this.getText());
                    this.setGraphic(txt);
                    this.setText("");
                    txt.requestFocus();
                }
            });
            txt.focusedProperty().addListener((a, b, c) -> {
                if (!c){
                    backToLabel();
                }
            });
            txt.setOnKeyReleased(e -> {
                if (e.getCode() == KeyCode.ENTER){
                    backToLabel();
                }
                else if (e.getCode() == KeyCode.ESCAPE){
                    txt.setText(saveTxt);
                    backToLabel();
                }
            });
            txt.setOnMouseClicked(e -> {
                if (e.getButton() == MouseButton.SECONDARY){
                    if (txt.getSelectedText().length() == 0){
                        System.out.println(Clipboard.getSystemClipboard().getString());
                    }
                    else {
                        var cb = new ClipboardContent();
                        cb.putString(txt.getSelectedText());
                        Clipboard.getSystemClipboard().setContent(cb);
                        backToLabel();
                    }
                }
            });
            this.setStyle("-fx-text-fill: #ffffff");
            this.setFont(font);
            this.setMinWidth(width);
            txt.setStyle("-fx-text-fill: #ffffff");
            txt.setFont(font);
            txt.setMinWidth(width);
        }
        private void backToLabel() {
            this.setGraphic(null);
            this.setText(saveTxt);
        }
    }

    class EditableLabel extends Label {
        TextField txt;
        String saveTxt;
        public EditableLabel(Font font){
            this("", font);
        }
        public EditableLabel(String content, Font font){
            super(content);
            txt = new TextField();
            saveTxt = "";
            this.setOnMouseClicked(e -> {
                if (e.getButton() == MouseButton.PRIMARY){
                    txt.setText(saveTxt = this.getText());
                    this.setGraphic(txt);
                    this.setText("");
                    txt.requestFocus();
                }
            });
            txt.focusedProperty().addListener((a, b, c) -> {
                if (!c){
                    backToLabel();
                }
            });
            txt.setOnKeyReleased(e -> {
                if (e.getCode() == KeyCode.ENTER){
                    backToLabel();
                }
                else if (e.getCode() == KeyCode.ESCAPE){
                    txt.setText(saveTxt);
                    backToLabel();
                }
            });
            txt.setOnMouseClicked(e -> {
                if (e.getButton() == MouseButton.SECONDARY){
                    if (txt.getSelectedText().length() == 0){
                        System.out.println(Clipboard.getSystemClipboard().getString());
                    }
                    else {
                        var cb = new ClipboardContent();
                        cb.putString(txt.getSelectedText());
                        Clipboard.getSystemClipboard().setContent(cb);
                        backToLabel();
                    }
                }
            });
            this.setStyle("-fx-text-fill: #ffffff");
            this.setFont(font);
            this.setMinWidth(width);
            txt.setStyle("-fx-text-fill: #ffffff");
            txt.setFont(font);
            txt.setMinWidth(width);
        }
        private void backToLabel() {
            this.setGraphic(null);
            this.setText(txt.getText());
        }
        public void appendText(String textToAppend){
            txt.appendText(textToAppend);
            try {
                this.setText(saveTxt = txt.getText());
            } catch (java.lang.IllegalStateException ex){ System.err.println(ex.getMessage());}
        }
        public void set(String textToSet){
            super.setText(saveTxt = textToSet);
            txt.setText(textToSet);
        }
    }
}