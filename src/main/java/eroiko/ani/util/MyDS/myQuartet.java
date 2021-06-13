/*
 * Author : Shiritai (楊子慶, or Eroiko on Github) at 2021/06/13.
 * See https://github.com/Shiritai/wallpaper_master for more information.
 * Created using VSCode.
 */
package eroiko.ani.util.MyDS;

public class myQuartet<T, E, K, V> {
    public T first;
    public E second;
    public K third;
    public V fourth;
    public myQuartet(T t, E e, K k, V v){
        first = t;
        second = e;
        third = k;
        fourth = v;
    }

    public myQuartet(T t, E e, myPair<K, V> mp){
        this(t, e, mp.key, mp.value);
    }
    
    public myQuartet(T t, myTriple<E, K, V> mt){
        this(t, mt.first, mt.second, mt.third);
    }
}
