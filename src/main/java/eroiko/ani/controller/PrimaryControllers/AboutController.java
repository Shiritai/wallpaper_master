package eroiko.ani.controller.PrimaryControllers;

import java.net.URL;
import java.util.ResourceBundle;

import eroiko.ani.MainApp;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.text.Text;

public class AboutController implements Initializable{

    @FXML private Text aboutText;
    @FXML private Text aboutTextTW;
    @FXML private Text bartenderSaying;
    @FXML private Text aboutTextVersion;
    
    public static final String intro = "Wallpaper Master is the application that helps you\nmanage your waifus as easy as possible.\nWith this, all you have to do is simply search\nusing your waifu's name (or rather, the artwork's name),\nwait a minute for downloading, and choose what you like!";
    public static final String introTW = "桌布大師旨在讓使用者可以輕鬆管理自己的老婆們。\n僅需簡單的輸入關鍵字，等待下載完畢，選擇喜愛\n的新老婆後就大功告成了。桌布大師會幫你驗證搜尋\n的正確性、下載的效率、檔案管理等過程，並且提供\n非常多額外功能，諸如將資料夾與其中的圖片預覽、\n篩選、整理至桌布資料夾，右鍵複製的所有圖片\n可以直接存入桌布資料夾，還附上最小化至工作列，\n完整的音樂撥放器，以及多種快捷鍵支持等，功能眾\n多請盡情享受。OwO";
    public static final String author = "Author : Eroiko at 2021/05/15";
    public static final String others = "Made with all my enthusiasm to ACGN!";

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        aboutTextTW.setText(introTW);
        aboutText.setText(String.join("\n\n", intro, author + ",\t" + MainApp.version, others));
        bartenderSaying.setText("It's my pleasure to be at your service.\nTime to get waifus and change wallpapers!");
        aboutTextVersion.setText(MainApp.version);
    }
}
