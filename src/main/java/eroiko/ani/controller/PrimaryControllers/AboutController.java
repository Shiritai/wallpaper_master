package eroiko.ani.controller.PrimaryControllers;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.text.Text;

public class AboutController implements Initializable{

    @FXML private Text aboutText;
    @FXML private Text aboutTextTW;
    @FXML private Text bartenderSaying;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        var intro = "Wallpaper Master is the application that helps you\nmanage your waifus as easy as possible.\nWith this, all you have to do is simply search\nusing your waifu's name (or rather, the artwork's name),\nwait a minute for downloading, and choose what you like!";
        var introTW = "桌布大師旨在讓使用者可以輕鬆管理自己的老婆們。\n僅需簡單的輸入關鍵字，等待下載完畢，選擇喜愛\n的新老婆後就大功告成了。桌布大師會幫你驗證搜尋\n的正確性、下載的效率、檔案管理等過程，並且提供\n非常多額外功能，諸如將資料夾與其中的圖片預覽、\n篩選、整理至桌布資料夾，右鍵複製的所有圖片\n可以直接存入桌布資料夾，還附上最小化至工作列，\n以及多種快捷鍵支持等，功能多多請盡情享受。OwO";
        var author = "Author : Eroiko at 2021/05/09";
        var copyright = "COPYRIGHT?\tSir, that does not exist...";
        aboutTextTW.setText(introTW);
        aboutText.setText(String.join("\n\n", intro, author, copyright));
        bartenderSaying.setText("It's my pleasure to be at you service.\nTime to get waifus and change wallpapers!");
    }
}
