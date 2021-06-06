package eroiko.ani.util.MyDS;

public class myPair<K, V>{
    public K key;
    public V value;
    private boolean isKeyToString = true;
    
    public myPair(K key, V value){
        this.key = key;
        this.value = value;
    }
    public myPair(K key){
        this(key, null);
    }
    public K getKey(){ return key; }
    public V getValue(){ return value; }

    public void setToStringElement(boolean isKeyToString){
        this.isKeyToString = isKeyToString;
    }
    
    public String toString(){
        return (isKeyToString) ? key.toString() : value.toString();
    }

    public boolean equals(myPair<K, V> other){
        return this.key.equals(other.key) && this.value.equals(other.value);
    }
}