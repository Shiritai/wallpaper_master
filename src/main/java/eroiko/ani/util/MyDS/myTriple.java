/*
 * Author : Shiritai (楊子慶, or Eroiko on Github) at 2021/06/13.
 * See https://github.com/Shiritai/wallpaper_master for more information.
 * Created using VSCode.
 */
package eroiko.ani.util.MyDS;

public class myTriple<E, K, V>{
    public E first;
    public K second;
    public V third;
    public myTriple(E first, K second, V third){
        this.first = first;
        this.second = second;
        this.third = third;
    }
    public myTriple(E code, myPair<K, V> mp){
        this.first = code;
        this.second = mp.key;
        this.third = mp.value;
    }
}
