package eroiko.ani.controller.PrimaryControllers;

import java.net.URL;
import java.util.ResourceBundle;

import eroiko.ani.MainApp;
import eroiko.ani.util.Method.CarryReturn;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Hyperlink;
import javafx.scene.layout.Border;
import javafx.scene.text.Text;

public class AboutController implements Initializable{

    @FXML private Text aboutText;
    @FXML private Text aboutTextTW;
    @FXML private Text infoText;
    @FXML private Text bartenderSaying;
    @FXML private Text aboutTextVersion;
    @FXML private Hyperlink githubLink;
    
    public static final String introEn = "    Wallpaper Master is the application that helps you manage your waifus as easy as possible. With this, all you have to do is simply search using your waifu's name (or rather, the artwork's name), wait a minute for downloading, and choose what you like!";
    public static final String introTW = "　　桌布大師旨在讓使用者可以輕鬆管理自己的老婆。僅需輸入關鍵字，等待下載完畢，選擇喜愛的桌布後就大功告成了。\n　　桌布大師會驗證搜尋的正確性、下載的效率、檔案管理等過程，並且提供非常多額外功能，諸如將資料夾與其中的圖片預覽、篩選、整理至桌布資料夾，右鍵複製後的圖片可以直接存入桌布資料夾，還附上最小化至工作列，完整的音樂播放器，同步整合的終端機，及多種快捷鍵支持等。功能眾多請盡情享受!";
    public static final String author = "Author : Eroiko";
    public static final String others = "Made with all my enthusiasm to ACGN!";

    public static final String githubString = "https://github.com/Shiritai/wallpaper_master";

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        aboutTextTW.setText(CarryReturn.addCarryReturnForAbout(introTW, 31, 1));
        aboutTextTW.setFont(MainApp.notoSansCJK22);
        aboutText.setText(CarryReturn.addCarryReturnForAbout(introEn, 56, 6));
        infoText.setText(String.join("\n", author + ", " + MainApp.version + " at " + MainApp.date, others));
        infoText.setFont(MainApp.rainbow28);
        bartenderSaying.setText("It's my pleasure to be at your service.\nTime to get waifus and change wallpapers!");
        aboutTextVersion.setText(MainApp.version);
        
        githubLink.setText(githubLink.getText() + " : " + githubString);
        githubLink.setFont(MainApp.firaCode16);
        githubLink.setBorder(Border.EMPTY);
        githubLink.setOnAction(e -> {
            MainApp.hostServices.showDocument(githubString);
            githubLink.setStyle("-fx-text-fill: white;");
        });
    }
}
