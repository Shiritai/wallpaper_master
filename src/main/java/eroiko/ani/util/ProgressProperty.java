package eroiko.ani.util;

import javafx.beans.property.*;

public class ProgressProperty {

    private DoubleProperty progressOfCrawler;
    private Types type;

    /** 請使用 {@code Types} 裡的 {@code enum elements} 來賦予 {@code type} */
    public ProgressProperty(Types type){
        this.progressOfCrawler = new SimpleDoubleProperty(0.);
        this.type = type;
    }
    
    public final DoubleProperty getProgress(){ return progressOfCrawler; }
    public final void setProgress(double cur){ progressOfCrawler.set(cur); }
    public final String getType(){
        return switch(this.type){
            case CRAWLER_ZERO_CHAN -> "Crawler_Zero_Chan";
            case SEARCH_BAR -> "Search_bar";
            default -> "Null"; 
        };
    }
}
