package eroiko.ani.util;

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
