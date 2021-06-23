/*
 * Author : Shiritai (楊子慶, or Eroiko on Github) at 2021/06/13.
 * See https://github.com/Shiritai/wallpaper_master for more information.
 * Created using VSCode.
 */
package eroiko.ani.util.Method;

public class CarryReturn {
    /** 不處理 str == null 的情況 */
    private static String carryReturn(String str, int length){
        String res = "";
        int currentSize = 0;
        var tmp = str.toCharArray();
        for (int i = 0; i < tmp.length; ++i){ // 掠過格式
            currentSize += (Character.isAlphabetic(tmp[i])) ? 1 : 2;
            if (currentSize >= length){
                if (!Character.isAlphabetic(tmp[i]) || Character.isWhitespace(tmp[i])){ // 保持英文單字完整性
                    res += "\n";
                    currentSize = 0;
                }
            }
            res += tmp[i];
        }
        return res;
    }

    /** 只為 Music With Syamiko 使用 */
    public static String stripTypeAndSerialNumberForMusicWithSyamiko(String str, int length){
        if (str != null){
            if (str.startsWith("00_")){
                str = str.substring(3);
            }
            var tmp = str.toCharArray();
            int i = 0;
            if (str.contains("_")){
                while (tmp[i++] != '_'); // 掠過編號
            }
            return carryReturn(str.substring(i, str.lastIndexOf('.')), length);
        }
        else {
            var tmp = "<Ready to play>";
            return carryReturn(tmp, length);
        }
    }
    /** 只為 Music With Akari 使用 */
    public static String stripTypeAndSerialNumberForMusicWithAkari(String str, int length){
        if (str != null){
            return carryReturn(str.substring(0, str.lastIndexOf('.')), length);
        }
        else {
            var tmp = "<Ready to play>";
            return carryReturn(tmp, length);        
        }
    }

    /** 
     * 只為 About controller 使用 
     * @param tolerantLength : 最高長度為 length + tolerantLength
     **/
    public static String addCarryReturnForAbout(String str, int length, int tolerantLength){
        String res = "";
        var tmp = str.toCharArray();
        int currentSize = 0;
        for (int i = 0; i < tmp.length; ++i){
            currentSize += (Character.isAlphabetic(tmp[i])) ? 1 : 2;
            if (currentSize >= length){
                if (currentSize >= length + tolerantLength || Character.isWhitespace(tmp[i]) || isChineseNotation(tmp[i])){
                    res += "\n";
                    currentSize = 0;
                    if (Character.isWhitespace(tmp[i])){
                        ++i;
                    }
                }
            }
            if (tmp[i] == '\n'){
                currentSize = 0;
            }
            res += tmp[i];
        }
        return res;
    }

    public static String addCarryReturnForTile(String str, int length){
        String res = "";
        var tmp = str.toCharArray();
        int currentSize = 0;
        for (int i = 0; i < tmp.length; ++i){
            currentSize += (Character.isAlphabetic(tmp[i])) ? 1 : 3;
            res += tmp[i];
            if (currentSize >= length){
                res += "\n";
                currentSize = 0;
            }
        }
        return res;
    }

    private static boolean isChineseNotation(char ch){
        return ch == '。' || ch == '，' || ch == '、' || ch == '「' || ch == '」';
    }

}
