package eroiko.ani.util;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class DoubleToStringProperty {
    private DoubleProperty doubleNumber;
    public StringProperty getDoubleToStringProperty(){
        return new SimpleStringProperty(Double.toString(doubleNumber.get() * 100).substring(0, 5));
    }
    public void setValue(double value){
        doubleNumber.set(value);
    }
    public void setValue(DoubleProperty value){
        doubleNumber = value;
    }
    public static StringProperty toStringProperty(DoubleProperty value){
        var tmp = Double.toString(value.get() * 100);
        return new SimpleStringProperty(tmp.substring(0, (tmp.length() > 4) ? 4 : tmp.length()) + "%");
    }
}
