package eroiko.ani.util;

import java.util.ArrayList;
import java.util.LinkedList;


public class ArrayAndLinkedList<E> {
    private ArrayList<E> arr;
    private LinkedList<E> list;

    public ArrayAndLinkedList(ArrayList<E> arr){
        this.arr = arr;
        this.list = new LinkedList<>();
        this.arr.forEach(e -> list.add(e));
    }

    public void print(){
        var tmp = this.list.listIterator();
        while (tmp.hasNext()){
            System.out.printf("%d\t", tmp.next());
        }
        tmp.remove();
        while (tmp.hasPrevious()){
            System.out.printf("%d\t", tmp.previous());
        }
        // list.remove(2);
        System.out.println();
        while (tmp.hasNext()){
            System.out.printf("%d\t", tmp.next());
        }
        while (tmp.hasPrevious()){
            System.out.printf("%d\t", tmp.previous());
        }
        // var tmp = this.arr.listIterator();
        // while (tmp.hasNext()){
        //     System.out.printf("%d\t", tmp.next());
        // }
        // tmp.remove();
        // while (tmp.hasPrevious()){
        //     System.out.printf("%d\t", tmp.previous());
        // }
        // // list.remove(2);
        // System.out.println();
        // while (tmp.hasNext()){
        //     System.out.printf("%d\t", tmp.next());
        // }
        // while (tmp.hasPrevious()){
        //     System.out.printf("%d\t", tmp.previous());
        // }
        // System.out.println();
        // arr.forEach(System.out::println);

    }

    public static void main(String [] args){
        int [] tmp = {1, 2, 3, 4, 5};
        var tmpArr = new ArrayList<Integer>(tmp.length);
        for (var i : tmp){
            tmpArr.add(i);
        }
        var eroiko = new ArrayAndLinkedList<>(tmpArr);
        eroiko.print();
    }

    
    
}
