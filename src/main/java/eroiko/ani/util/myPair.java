package eroiko.ani.util;

public class myPair<K extends Comparable<K>, V>{
    public K key;
    public V value;
    public myPair(K key, V value){
        this.key = key;
        this.value = value;
    }
    public myPair(K key){
        this(key, null);
    }
    public void setValue(V value){
        this.value = value;
    }
}
