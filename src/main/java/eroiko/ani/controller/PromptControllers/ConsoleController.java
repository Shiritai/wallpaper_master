/*
 * Author : Shiritai (楊子慶, or Eroiko on Github) at 2021/06/18.
 * See https://github.com/Shiritai/wallpaper_master for more information.
 * Created using VSCode.
 */
package eroiko.ani.controller.PromptControllers;

import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintStream;
import java.net.URL;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.ResourceBundle;

import eroiko.ani.MainApp;
import eroiko.ani.model.CLI.Console;
import eroiko.ani.model.CLI.exception.ClearConsoleException;
import eroiko.ani.model.CLI.exception.CustomInformationException;
import eroiko.ani.model.CLI.exception.ExitConsoleException;
import eroiko.ani.model.CLI.exception.ShutdownSoftwareException;
import eroiko.ani.model.CLI.exception.TerminalSettingException;
import eroiko.ani.util.Method.TimeWait;
import eroiko.ani.util.NeoWallpaper.WallpaperPath;
import eroiko.ani.util.NeoWallpaper.WallpaperUtil;
import javafx.beans.property.StringProperty;
import javafx.concurrent.Service;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.effect.BoxBlur;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.text.Font;

public class ConsoleController implements Initializable {

    public static void OpenCompleteTerminal(){
        try {
            MainApp.mainStage.setScene(
                new Scene(FXMLLoader.load(WallpaperPath.FXML_SOURCE_PATH.resolve("TerminalWindow.fxml").toUri().toURL()))
            );
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    @FXML private StackPane textBox;
    @FXML private ImageView wallpaper;
    private BoxBlur blur;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            var str = WallpaperPath.DEFAULT_DATA_PATH.resolve("view/style/transparentTextArea.css").toAbsolutePath().toUri().toURL().toString();
            blur = new BoxBlur(2.0, 1.5, 1);
            wallpaper.setEffect(blur);
            var text = new ConsoleLabel(
                MainApp.firaCode20, str, textBox,
                WallpaperPath.DEFAULT_DATA_PATH, WallpaperUtil::pathDirAndNameCompare,
                MainApp.hostName, MainApp.userName,
                wallpaper, textBox, (BoxBlur) wallpaper.effectProperty().get()
            );
            textBox.getChildren().add(text);
            StackPane.setAlignment(text, Pos.TOP_LEFT);
            textBox.addEventFilter(KeyEvent.KEY_PRESSED, e -> {
                if (new KeyCodeCombination(KeyCode.C, KeyCodeCombination.CONTROL_DOWN).match(e)){
                    text.cancel();
                }
                else if (new KeyCodeCombination(KeyCode.L, KeyCodeCombination.CONTROL_DOWN).match(e)){
                    text.clear();
                }
                else if (new KeyCodeCombination(KeyCode.COMMA, KeyCodeCombination.CONTROL_DOWN).match(e)){
                    ConsoleSetting.OpenConsoleSetting();
                    ConsoleSetting.SyncProperty(wallpaper.opacityProperty(), (BoxBlur) wallpaper.effectProperty().get());
                    ConsoleSetting.SyncImgSelection(wallpaper);
                    ConsoleSetting.SyncColor(textBox, text);
                }
            });
            /* invert color text */ // 未來實作
            // ColorInput color = new ColorInput();
            // color.setPaint(Color.WHITE);
            // color.setWidth(Double.MAX_VALUE);
            // color.setHeight(Double.MAX_VALUE);
            // Blend blend = new Blend(BlendMode.DIFFERENCE);
            // blend.setBottomInput(color);
        } catch (Exception e) { e.printStackTrace(); }
    }

    class ConsoleLabel extends Label {
        class ConsoleTextArea extends TextArea {
            String readOnly;
            String cache;
            int offset;
            
            /**
             * 接受使用者輸入、選取 (for copy) 的 class
             */
            public ConsoleTextArea(){
                offset = 0;
                readOnly = cache = "";
                setText("");
                setContextMenu(new ContextMenu());
            }

            public void restore(){
                setText(readOnly);
            }
            
            /**
             * protect read only string
             * @return the distance from the last caret position to current caret position
             */
            public int repair(){
                var res = getCaretPosition();
                setText(cache);
                return (res < offset) ? res : offset;
            }

            public String refresh(){
                String res = "";
                var txt = getText();
                if (txt.length() > readOnly.length()){
                    res = txt.substring(readOnly.length());
                }
                readOnly = cache = txt;
                offset = readOnly.length();
                return res;
            }

            public int getCaret(){ return getCaretPosition(); }

            /**
             * 所有鍵盤操作請先以此函數驗證安全性
             * @return 是否在安全區域編輯
             */
            public boolean isReadOnly(){    
                if (getCaretPosition() == offset || getCaretPosition() == offset + 1){
                    cache = getText();
                }
                return getCaretPosition() < offset;
            }
        }

        static final String info = """
        Wallpaper Master Complete GUI Terminal.
        Created by Eroiko, version 0.0.1 at 2021/06/17.
        
        See more information on https://github.com/Shiritai/wallpaper_master
    
        """;

        ConsoleTextArea txt; // 可以直接訪問並讀寫, 相對的, this (Label) 不允許直接訪問讀寫
        Service<Void> service;
        // FXConsole console;
        Console console;
        StringProperty savedText;
        PrintStream print;
        PipedInputStream pipIn;
        ImageView image;
        BoxBlur blur;
        StackPane belong;

        /**
         * 
         * @param font
         * @throws IOException
         */
        public ConsoleLabel(Font font, String style, Pane toFit, Path initPath,
            Comparator<Path> pathComp, String computerName, String userName,
            ImageView image, StackPane belong, BoxBlur blur) throws IOException
        {
            /* set image properties transitive */
            this.image = image;
            this.blur = blur;  
            this.belong = belong;  
            /* set Label */
            this.setWrapText(true);
            this.setMinWidth(toFit.getPrefWidth());
            this.setStyle("-fx-text-fill: #ffffff");
            this.setFont(font);
            this.setTranslateX(10);
            this.setTranslateY(4);
            /* set TextArea */
            txt = new ConsoleTextArea();
            txt.setWrapText(true);
            txt.setFont(font);
            txt.setMinHeight(toFit.getPrefHeight());
            txt.setMinWidth(1426.);
            txt.getStylesheets().addAll(style);
            txt.setTranslateX(-13);
            txt.setTranslateY(-6);
            /* set Console */
            pipIn = new PipedInputStream();
            print = new PrintStream(new PipedOutputStream(pipIn), true);
            console = new Console(print, initPath, pathComp, computerName, userName, true);
            new ConsoleThread(pipIn, txt.textProperty());
            txt.setText(info + console.promptString());
            turnToText();
            /* set events */
            txt.addEventFilter(KeyEvent.ANY, e -> {
                switch (e.getCode()){
                    case ENTER -> {
                        if (e.getEventType() == KeyEvent.KEY_PRESSED){
                            backToLabel();
                        }
                        e.consume();
                    }
                    case UP -> {
                        if (e.getEventType() == KeyEvent.KEY_PRESSED){
                            txt.restore();
                            var tmp = console.getPreviousCommand();
                            txt.appendText(tmp == null ? "" : tmp);
                        }
                        e.consume();
                        txt.end();
                    }
                    case DOWN -> {
                        if (e.getEventType() == KeyEvent.KEY_PRESSED){
                            txt.restore();
                            var tmp = console.getLaterCommand();
                            txt.appendText(tmp == null ? "" : tmp);
                        }
                        e.consume();
                        txt.end();
                    }
                    default -> {
                    }
                }
                if (txt.isReadOnly()){
                    // txt.repair();
                    // txt.end();
                    var dist = txt.repair();
                    txt.positionCaret(dist);
                    txt.setScrollTop(0);
                    return;
                }
            });
            txt.addEventFilter(MouseEvent.MOUSE_CLICKED, e -> {
                if (e.getButton() == MouseButton.SECONDARY){
                    e.consume();
                    if (txt.getSelectedText().length() == 0){
                        txt.appendText(Clipboard.getSystemClipboard().getString());
                    }
                    else {
                        var cb = new ClipboardContent();
                        cb.putString(txt.getSelectedText());
                        Clipboard.getSystemClipboard().setContent(cb);
                    }
                }
            });
            txt.textProperty().addListener(e -> txt.end());
        }
        
        private void backToLabel(){
            var cmd = txt.refresh();
            System.out.println("[Command]\n" + cmd);
            setGraphic(null);
            try {
                new TimeWait(100);
                console.normalReadLine(cmd);
            } catch (ExitConsoleException | ShutdownSoftwareException ex) {
                MainApp.closeMainStage();
            } catch (ClearConsoleException cl){
                txt.setText(console.promptString());
            } catch (TerminalSettingException st){
                openSetting();
            } catch (CustomInformationException ci){
                
            } catch (Exception e){ e.printStackTrace(); }
            new TimeWait(100);
            turnToText();
        }
        
        private void turnToText(){
            txt.refresh();
            this.setGraphic(txt);
            txt.requestFocus();
            txt.end();
        }

        private void cancel(){
            console.cancel();
            print.print(console.promptString());
            new TimeWait(100);
            txt.refresh();
        }
        
        private void clear(){
            txt.setText(console.promptString());
            txt.refresh();
        }

        private void openSetting(){
            ConsoleSetting.OpenConsoleSetting();
            ConsoleSetting.SyncProperty(image.opacityProperty(), blur);
            ConsoleSetting.SyncImgSelection(image);
            ConsoleSetting.SyncColor(belong, this);
        }
    }
}