/*
 * Author : Shiritai (楊子慶, or Eroiko on Github) at 2021/06/13.
 * See https://github.com/Shiritai/wallpaper_master for more information.
 * Created using VSCode.
 */
package eroiko.ani.util.MyDS;

import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.text.Font;

public class EditableLabel extends Label {
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
        // this.setMinWidth(width);
        txt.setStyle("-fx-text-fill: #ffffff");
        txt.setFont(font);
        // txt.setMinWidth(width);
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
