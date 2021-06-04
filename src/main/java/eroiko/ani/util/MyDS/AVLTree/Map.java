package eroiko.ani.util.MyDS.AVLTree;

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
