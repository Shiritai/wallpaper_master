package eroiko.ani.util;

import java.util.ArrayList;

public class DoubleArrayListCombiner<E> {
    private ArrayList<E> res;
    public ArrayList<E> combineAndSortArrList(ArrayList<ArrayList<E>> source){
        int length = 0;
        for (var s : source){
            length += s.size();
        }
        res = new ArrayList<E>(length);
        source.forEach(s -> s.forEach(e -> res.add(e))); // 真蘇胡 OwO
        return res;
    }
}
