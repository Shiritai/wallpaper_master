package eroiko.ani.util.myDS;

public class myPair<K, V>{
    public K key;
    public V value;
    public myPair(K key, V value){
        this.key = key;
        this.value = value;
    }
    public myPair(K key){
        this(key, null);
    }
    public K getKey(){ return key; }
    public V getValue(){ return value; }
}