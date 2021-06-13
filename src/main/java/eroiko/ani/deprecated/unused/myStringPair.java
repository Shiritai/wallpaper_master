/*
 * Author : Shiritai (楊子慶, or Eroiko on Github) at 2021/06/13.
 * See https://github.com/Shiritai/wallpaper_master for more information.
 * Created using VSCode.
 */
package eroiko.ani.deprecated.unused;

public class myStringPair {
    public String key;
    public String value;
    public myStringPair(String key, String value){
        this.key = key;
        this.value = value;
    }
    public myStringPair(String key){
        this(key, null);
    }
    public String getKey(){ return key; }
    public String getValue(){ return value; }
}
