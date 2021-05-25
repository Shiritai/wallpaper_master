# Wallpaper Master

`version 0.0.3 (2021/05/26)`

## Introduction 簡介

My first Individual project! (For practice OwO)

Wallpaper Master 旨在讓使用者可以輕鬆管理自己的老婆們。

僅需簡單的輸入關鍵字，等待下載完畢，選擇喜愛的新老婆後便大功告成。

桌布大師會幫你驗證搜尋的正確性、下載的效率、檔案管理等過程，並且提供非常多額外功能，諸如將資料夾與其中的圖片預覽、篩選、整理至桌布資料夾，右鍵複製的所有圖片可以直接存入桌布資料夾，還附上最小化至工作列，完整的音樂撥放器，以及多種快捷鍵支持等，功能眾多請盡情享受。(持續更新中...應該說暑假後持續更新)

## Release note

1. 完善 pom.xml 設定, 可以正常 package

2. 整合必要檔案, 可以一鍵運行

3. 調整檔案歸屬與標記

4. 新增最小化系統提示, 可至 Preference 調整

5. 更新部分功能

## Run 使用方法

請先 [下載並安裝 Java SE 15](https://www.oracle.com/tw/java/technologies/javase-downloads.html) (大於或等於 15 皆可)

並在環境變數裡加入相應的 `JDK_DIRECTORY/bin` 後,

滑鼠點擊運行 `run.vbs` 即可。

若想在運行時看到實際的終端機, 請改運行 `start.bat`。

Please [download and install Java SE 15](https://www.oracle.com/tw/java/technologies/javase-downloads.html) (15 or later)

Then add `JDK_DIRECTORY/bin` to PATH.

Double click `run.vbs`, and the application should run properly.

If you'd like to see the command window while running, run `start.bat` alternatively.

## File Settings 檔案設定

### Music Setting 音樂設定

在 data/music 資料夾下有預設的音樂, 若想要更改音樂庫的內容 :

1. 開啟 Wallpaper Master

2. 開啟 Music With Syamiko

3. 滑鼠點擊 Syamiko 的手, 將會出現資料夾圖示

4. 點擊資料夾圖示, 匯入音樂資料夾

To change music repository :

1. Open Wallpaper Master

2. Open Music With Syamiko

3. Click Syamiko's hand, a folder icon should be revealed

4. Click the folder icon to import your own musics from your music folder

## Packaging Environment 包裝環境

Java|JRE|JVM
-|:-:|-
15.0.2|15.0.2+7-27|15.0.2+7-27

使用 Maven 管理, 並以 Maven Shade 包裝。

詳見 `.jar` 檔裡的 `pom.xml`

Use Maven to manage project, package with Maven Shade.

See `pom.xml` in `.jar` file

## Wallpaper 進版圖

![image](https://i.imgur.com/OqV05rM.jpg)

![image](https://i.imgur.com/HMhxR8K.jpg)
