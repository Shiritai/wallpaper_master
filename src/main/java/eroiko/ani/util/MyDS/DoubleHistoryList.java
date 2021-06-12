package eroiko.ani.util.MyDS;

import java.util.ArrayList;

public class DoubleHistoryList<T> {
    private ArrayList<T> data;
    private int current;

    /**
     * 實作具有雙向 Iterator 的 ArrayList
     * @param size initial size
     */
    public DoubleHistoryList(int size){
        data = new ArrayList<T>(size);
        current = -1;
    }
    
    /**
     * 實作具有雙向 Iterator 的 ArrayList
     */
    public DoubleHistoryList(){
        this(16);
    }

    public void add(T t, boolean trimOrRestore){ 
        if (data.size() == 0 || (data.size() - 1 >= 0 && !data.get(data.size() - 1).equals(t))){
            data.add(t);
            if (trimOrRestore){
                trimHistory();
            }
            else {
                restoreIterator();
            }
        }
    }

    public int size(){ return data.size(); }

    public boolean hasNext(){ return current < data.size() - 1; }
    
    public boolean hasPrevious(){ return current > 0 ; }

    public T getNext(){
        if (data.isEmpty() || current == data.size() - 1){
            throw new IllegalArgumentException("Index out of range " + current + " in " + data.size());
        }
        return data.get(++current);
    }

    public T getPrevious(){
        if (data.isEmpty() || current == 0){
            throw new IllegalArgumentException("Index out of range " + current + " in " + data.size());
        }
        return data.get(--current);
    }

    private void restoreIterator(){
        current = data.size() - 1;
    }

    private void trimHistory(){
        if (current != data.size() - 1){
            T tmp = data.get(data.size() - 1);
            for (int i = data.size() - 1; i > current; --i){
                data.remove(i);
            }
            data.add(tmp);
            current = data.size() - 1;
        }
    }
    
    public int it(){ return current; }

}
