# Wallpaper Master

`version 0.0.4 (2021/05/31)`

## Introduction 簡介

My first Individual project! (For practice OwO)

Wallpaper Master 旨在讓使用者可以輕鬆管理自己的老婆。僅需輸入關鍵字，等待下載完畢，選擇喜愛的桌布後就大功告成了。

桌布大師會驗證搜尋的正確性、下載的效率、檔案管理等過程，並且提供非常多額外功能，諸如將資料夾與其中的圖片預覽、篩選、整理至桌布資料夾，右鍵複製後的圖片可以直接存入桌布資料夾，還附上最小化至工作列，完整的音樂播放器，及多種快捷鍵支持等。功能眾多請盡情享受! (持續更新中...應該說暑假後持續更新)

## Release note (version 0.0.4)

1. 新增 Music With Akari, 可以自由欣賞所有資料夾的音樂

2. 完成 Music With Akari 與 Music With Syamiko 的連動, 可以輕鬆導入音樂至 Music With Syamiko

3. Music With Syamiko 多項修正

4. 完善 File Explorer, 支援圖片與音樂的預覽, 且樹狀圖中所有檔案可以雙擊開啟

5. 字型全面完成維護

## Run 使用方法

請先 [下載並安裝 Java SE 15](https://www.oracle.com/tw/java/technologies/javase-downloads.html) (大於或等於 15 皆可)

並在環境變數裡加入相應的 `JDK_DIRECTORY/bin` 後,

滑鼠點擊運行 `run.vbs` 即可。

若想在運行時看到實際的終端機, 請改運行 `start.bat`。

Please [download and install Java SE 15](https://www.oracle.com/tw/java/technologies/javase-downloads.html) (or newer)

Then add `JDK_DIRECTORY/bin` to PATH.

Double click `run.vbs`, and the application should run properly.

If you'd like to see the command window while running, run `start.bat` alternatively.

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

![image](https://i.imgur.com/sktWxXr.jpg)
