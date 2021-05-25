package eroiko.ani.util.MyDS;
/* 輸入輸出輔助資結, 僅支援寫入位元, 並以字串形式讀出 */
public class ArrayByteToString {
    private byte [] data;
    private int capacity;
    private int size;

    public ArrayByteToString(int capacity){
        this.capacity = capacity;
        data = new byte[capacity];
        size = 0;
    }

    public ArrayByteToString(){
        this(1024);
    }

    public int getSize(){ return this.size; }
    public int getCapacity(){ return this.capacity; }
    
    /* 加入字組陣列, 若有需要會進行擴容 */
    public void add(byte [] byteArray){
        if (size + byteArray.length >= capacity){
            var tmpData = new byte[(size + byteArray.length) << 1];
            for (int i = 0; i < capacity; ++i){
                tmpData[i] = data[i];
            }
            data = tmpData;
            capacity = (size + byteArray.length) << 1;
        }
        for (int i = 0; i < byteArray.length; ++i){
            data[size + i] = byteArray[i];
        }
        size += byteArray.length;
    }

    public boolean endsWith(String str){
        if (str.length() > size){
            throw new IllegalArgumentException("The substring is longer than this data structure!");
        }
        return str.equals(new String(data, size - str.length(), str.length()));
    }

    @Override
    public String toString(){
        return new String(data);
    }
}
