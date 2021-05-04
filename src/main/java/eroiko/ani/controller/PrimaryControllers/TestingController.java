package eroiko.ani.controller.PrimaryControllers;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

public class TestingController implements Initializable {
    public static boolean quit;

    @FXML
    void DivideByZero(ActionEvent event) {
        try {
            if (1 / 0 == 0);
        } catch (Exception e){
            System.out.println(e.toString());
            if (!quit){
                System.err.println(e.toString());
            }
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb){
        
    }
}
