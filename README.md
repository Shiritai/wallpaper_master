# Wallpaper Master

`version 0.1.6 (2021/06/18)`

## Introduction 簡介

My first Individual project! (For practice OwO)

Wallpaper Master 旨在讓使用者可以輕鬆管理自己的老婆。僅需輸入關鍵字，等待下載完畢，選擇喜愛的桌布後就大功告成了。

桌布大師會驗證搜尋的正確性、下載的效率、檔案管理等過程，並且提供非常多額外功能，諸如將資料夾與其中的圖片預覽、篩選、整理至桌布資料夾，右鍵複製後的圖片可以直接存入桌布資料夾，還附上最小化至工作列，完整的音樂播放器，能同步整合或者獨立使用的終端機，及多種快捷鍵支持等。功能眾多請盡情享受!

隨緣更新中!

## Run 使用方法

請先 [下載並安裝 Java SE 15](https://www.oracle.com/tw/java/technologies/javase-downloads.html) (大於或等於 15 皆可)

並在環境變數裡加入相應的 `JDK_DIRECTORY/bin` 後,

滑鼠點擊運行 `run.vbs` 即可。

若想在運行時看到實際的終端機, 請改運行 `start.bat`。

Please [download and install Java SE 15 or newer](https://www.oracle.com/tw/java/technologies/javase-downloads.html)

Then add `JDK_DIRECTORY/bin` to PATH.

Double click `run.vbs`, and the application should run properly.

If you'd like to see the command window while running, run `start.bat` alternatively.

## Release note

1. 釋出獨立終端機版本, 可以與 Wallpaper Master 地個部件連動, 有數個 Independent Terminal 介面設定可供選擇

2. 修正多處 Bug, 尤其原本終端機的功能

3. 新增 Music With Akari 的彩蛋

4. 修正 CLI.Command.Music 呼叫 Music With Akari 的 Bug

## Change Log 內部變化紀錄

1. CLI : 補強指令查詢與修復 Bugs, 修改 Exception passing 機制

2. WallpaperUtil 的比較函數 debug

3. Decoupling and kill magic number/classes

4. 內嵌與獨立 Terminal 為 OCP, java.nio 以及多線程的練習成果

5. 當前使用程式碼行數 (Excluded deprecated classes) 約 : 6200 行

## Known issue 已知問題

1. 先以任何形式開啟 Music With Syamiko 後, Music With Akari 無法正常開啟, 反之則沒問題, 推估問題出在 `MediaOperator.class`

## Environment

Java|JRE|JVM
-|:-:|-
15.0.2|15.0.2+7-27|15.0.2+7-27

使用 Maven 管理, 並以 Maven Shade 包裝。

詳見 `pom.xml`

Use Maven to manage project, package with Maven Shade.

See `pom.xml`

## IDE

Undoubtedly, my dear **vscode**!

## Wallpaper (進版圖)

![image](https://i.imgur.com/OqV05rM.jpg)

![image](https://i.imgur.com/sktWxXr.jpg)
