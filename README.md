# Wallpaper Master

`version 0.1.8 (2021/06/25) : Enhanced File Explorer`

## Introduction 簡介

[使用與技術介紹 Introduction (for NCU CSIE Final Project Introduction, version 0.1.6)](https://drive.google.com/file/d/1A1tRqz0CPOHCmy4RV9ET-W0nfSnqViEc/view?usp=sharing)

[使用示範 Demo Video (for NCU CSIE Final Project Demo, version 0.1.6)](https://drive.google.com/file/d/1x6VS8rtZH1xRogoARr1NJ8jrX-ND6anc/view?usp=sharing)

My first Individual project!

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

1. 新增網路快速測試, 沒網路時使用爬蟲會跳出錯誤訊息

2. 全新的訊息通知介面, 可向使用者反映更詳細的錯誤、例外、資訊, 其中 EVA 風背景, 我使用小畫家3D製成

3. 實現自動縮放的 Wallpaper Viewer

4. 改善 MainWindow 左下角的預覽圖品質

5. File Explorer (Tile) 檔案圖示優化

6. File Explorer (Tile) 改採分段載入策略, 確保不濫用記憶體

7. 修正 File Explorer (Tile) 的文字邏輯

8. 進度條顯示 File Explorer 的載入進度

## Change Log 內部變化紀錄

1. WallpaperUtil 的比較函數 debug

2. WallpaperUtil 新增對小圖示的縮放與銳化優化, 搭配 Wallpaper 新增取得經過縮放 + 銳化小圖示的方法

3. Decouple and kill magic number/classes

4. 消除 Crawler 指令的 Bug

5. 當前使用程式碼行數 (Excluded deprecated classes) 約 : 6600 行

## Known issue 已知問題

1. 當前 Complete Terminal 有不少 Bugs, 強烈不建議使用

## Maybe in the future... 未來可能的擴充方向

1. 支援中文介面

2. 雙 Music Player 新增音樂播放清單

3. Wallpaper Viewer 新增簡易圖片編輯功能

4. Music with Akari 新增最小化 or 可關閉視窗的播放功能

5. File Explorer 新增右鍵選單, 支援在檔案總管的基本操作

6. 新增自動下載更新此軟體的功能

7. 實作網路流量監測 (for crawler)

8. File Explorer 右側區域新增可切換以大圖示 / 詳細資料來瀏覽的介面

9. 新增自訂軟體啟動的小幫手

10. 更多可調參數的爬蟲, 如果有必要甚至考慮為其加開 Controller

11. 增加爬蟲目標

12. ~~動態看板娘~~

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

![image](https://i.imgur.com/UreV98s.jpg)

![image](https://i.imgur.com/RDgpUhs.jpg)
