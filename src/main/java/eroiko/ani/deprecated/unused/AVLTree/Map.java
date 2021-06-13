/*
 * Author : Shiritai (楊子慶, or Eroiko on Github) at 2021/06/13.
 * See https://github.com/Shiritai/wallpaper_master for more information.
 * Created using VSCode.
 */
package eroiko.ani.deprecated.unused.AVLTree;

public interface Map<K, V> {
    void add(K key, V value);
    /** didn't implement !!! do not call this */
    V remove(K key);
    boolean exist(K key);
    V get(K key);
    void change(K key, V value);
    int getSize();
    boolean isEmpty();
}
