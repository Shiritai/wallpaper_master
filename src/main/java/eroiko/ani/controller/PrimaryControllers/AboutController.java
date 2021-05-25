package eroiko.ani.controller.PrimaryControllers;

import java.net.URL;
import java.util.ResourceBundle;

import eroiko.ani.MainApp;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Hyperlink;
import javafx.scene.layout.Border;
import javafx.scene.text.Text;
// import javafx.scene.

public class AboutController implements Initializable{

    @FXML private Text aboutText;
    @FXML private Text aboutTextTW;
    @FXML private Text infoText;
    @FXML private Text bartenderSaying;
    @FXML private Text aboutTextVersion;
    @FXML private Hyperlink githubLink;
    
    public static final String introEn = "    Wallpaper Master is the application\nthat helps you manage your waifus as easy\nas possible. With this, all you have to\ndo is simply search using your waifu's name\n(or rather, the artwork's name), wait a minute\nfor downloading, and choose what you like!";
    public static final String introTW = "      桌布大師旨在讓使用者可以輕鬆管理自己的\n老婆們。僅需簡單的輸入關鍵字，等待下載完畢，\n選擇喜愛的新老婆後就大功告成了。桌布大師會\n驗證搜尋的正確性、下載的效率、檔案管理等過程，\n並且提供非常多額外功能，諸如將資料夾與其中的\n圖片預覽、篩選、整理至桌布資料夾，右鍵複製後\n的圖片可以直接存入桌布資料夾，還附上最小化\n至工作列的便利功能，完整的音樂撥放器，以及\n多種快捷鍵支持等。功能眾多請盡情享受。OwO";
    public static final String author = "Author : Eroiko";
    public static final String others = "Made with all my enthusiasm to ACGN!";

    public static final String githubString = "https://github.com/Shiritai/wallpaper_master";

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        aboutTextTW.setText(introTW);
        aboutText.setText(introEn);
        infoText.setText(String.join("\n", author + ", " + MainApp.version + " at " + MainApp.date, others));
        bartenderSaying.setText("It's my pleasure to be at your service.\nTime to get waifus and change wallpapers!");
        aboutTextVersion.setText(MainApp.version);
        
        githubLink.setText(githubLink.getText() + " : " + githubString);
        githubLink.setBorder(Border.EMPTY);
        githubLink.setOnAction(e -> {
            MainApp.hostServices.showDocument(githubString);
            githubLink.setStyle("-fx-text-fill: white;");
        });
    }
}
