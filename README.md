# Wallpaper Master

My first Individual project! (For practice OwO)

包裝好的資源詳見 [Wallpaper Master](https://github.com/Shiritai/wallpaper_master_application)

Wallpaper Master 旨在讓使用者可以輕鬆管理自己的老婆們。

僅需簡單的輸入關鍵字，等待下載完畢，選擇喜愛的老婆後便大功告成。

桌布大師會幫你驗證搜尋的正確性、下載的效率、檔案管理等過程，並且提供非常多額外功能，諸如將資料夾與其中的圖片預覽、篩選、整理至桌布資料夾，右鍵複製的所有圖片可以直接存入桌布資料夾，還附上最小化至工作列，完整的音樂撥放器，以及多種快捷鍵支持等，功能眾多請盡情享受。(持續更新中...應該說暑假後持續更新)

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

## Release version

See [Wallpaper Master](https://github.com/Shiritai/wallpaper_master_application)

## Release note (version 0.1.1)

1. 調整版本號規則, 最右位為細微調整, 中間位為大改後的版本, 最高位保留

2. 新增 Music With Akari, 可以在工作列 Window 處以及最小化右鍵處開啟, 或者雙擊 File Explorer 裡的音樂檔, 實現在所有資料夾裡開啟音樂播放器

3. 完成 Music With Akari 與 Music With Syamiko 的配合, 可以輕鬆導入音樂至 Music With Syamiko

4. 新增兩個音樂播放器的音量快捷鍵, 並更改播放邏輯

5. 完善 File Explorer, 支援圖片與音樂的預覽, 且樹狀圖中所有檔案可以雙擊開啟

6. File Explorer 中圖片排序優化, 更改點擊邏輯

7. 將 Wallpaperize 功能進行擴展, 新增 Merge wallpapers 功能, 可直接在工作列選用

8. 字型維護

9. 微調主視窗

10. 最小化功能追加與修改

## Program Change Note 內部優化紀錄

1. 補齊資源關閉

2. 以函示參考減少多餘匿名函式

3. 調整資料結構與其應用, 加速 File Explorer 讀取時間

4. 新增 Deprecated 標註

5. 當前使用程式碼行數 (不包含 deprecated 類) 約 : 4730 行

## Known issue 已知問題

1. 內嵌 File Explorer 直接開啟的功能在不同電腦上的可用性未知

## Wallpaper (進版圖)

![image](https://i.imgur.com/OqV05rM.jpg)

![image](https://i.imgur.com/sktWxXr.jpg)
