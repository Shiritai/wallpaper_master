# Wallpaper Master

`version 0.1.3 (2021/06/05)`

## Introduction 簡介

My first Individual project! (For practice OwO)

Wallpaper Master 旨在讓使用者可以輕鬆管理自己的老婆們。

僅需簡單的輸入關鍵字，等待下載完畢，選擇喜愛的老婆後便大功告成。

桌布大師會幫你驗證搜尋的正確性、下載的效率、檔案管理等過程，並且提供非常多額外功能，諸如將資料夾與其中的圖片預覽、篩選、整理至桌布資料夾，右鍵複製的所有圖片可以直接存入桌布資料夾，還附上最小化至工作列，完整的音樂撥放器，以及多種快捷鍵支持等，功能眾多請盡情享受。(持續更新中...應該說暑假後持續更新)

## Run 使用方法

請先 [下載並安裝 Java SE 15](https://www.oracle.com/tw/java/technologies/javase-downloads.html) (大於或等於 15 皆可)

並在環境變數裡加入相應的 `JDK_DIRECTORY/bin` 後,

滑鼠點擊運行 `run.vbs` 即可。

若想在運行時看到實際的終端機, 請改運行 `start.bat`。

Please [download and install Java SE 15 or newer](https://www.oracle.com/tw/java/technologies/javase-downloads.html)

Then add `JDK_DIRECTORY/bin` to PATH.

Double click `run.vbs`, and the application should run properly.

If you'd like to see the command window while running, run `start.bat` alternatively.

## Release note (version 0.1.3)

1. File Explorer 中圖片錯誤修正, 更改點擊邏輯

2. 改變主視窗大小與比例, 鎖定不必要的大小調整

3. Terminal 大幅強化, 可接受並執行數個 Linux-based 指令

4. Terminal 新增與 File Explorer 的連動

5. Terminal 實現指令歷史查詢, 可使用方向鍵呼叫歷史指令, 或輸入 history 指令

6. Terminal 新增終端機的當前狀態顯示

7. Add easter egg to GUI Terminal

8. 新增 Terminal 指令例外處理, 實現對談式 Terminal 基礎

9. 新增可呼叫外部終端機, 支援 cmd (`cmd`, `cmd.exe`), powershell (`pwsh`, `powershell`, `powershell.exe`), windows terminal (`wt`, `wt.exe`), bash (`bash`)

## Program Change Note 內部變化紀錄

1. 加強函式參考的應用

2. Terminal 為 OCP 與 java.nio 的練習成果

3. 當前使用程式碼行數 (Exclude deprecated classes) 約 : 5400 行

## Known issue 已知問題

1. <待發現, wait to be found>

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
