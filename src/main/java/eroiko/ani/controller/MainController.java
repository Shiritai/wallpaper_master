package eroiko.ani.controller;

import java.io.*;
import java.net.URL;
import java.util.ResourceBundle;

import eroiko.ani.MainApp;
import eroiko.ani.controller.ConsoleTextArea.TerminalThread;
import eroiko.ani.controller.ControllerSupporter.MinimizeWindow;
import eroiko.ani.controller.PrimaryControllers.PropertiesController;
import eroiko.ani.controller.PrimaryControllers.TestingController;
import javafx.event.ActionEvent;
import javafx.fxml.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.stage.Stage;

public class MainController implements Initializable {
    /* Support variables */
    public static ImageView preview;
    /* Terminal */
    private static PrintStream stdOut = new PrintStream(System.out);
    public PipedInputStream pipIn = new PipedInputStream();
    private Thread terminalThread;
    boolean quit = true;
    /* FXML variables */
    @FXML private TextArea Terminal_out = new TextArea();
    @FXML private TextField Terminal_in = new TextField();  
    @FXML private ProgressBar pbar;  
    @FXML private ImageView imagePreview;
    @FXML private TreeView<String> treeFileExplorer;
    @FXML private Label pathLabel;
  
    @FXML
    void hitExit(ActionEvent event) {
        if (terminalThread != null){
            killTerminal();
        }
        MainApp.mainStage.close();
    }

    @FXML
    void OpenTestingWindow(ActionEvent event) {
        TestingController.quit = quit;
        try {
            var stage = new Stage();
            stage.setTitle("Testing Window");
            stage.setScene(new Scene(FXMLLoader.load(new File("src/main/java/eroiko/ani/view/TestingWindow.fxml").toURL())));
            stage.getIcons().add(new Image(new File("src/main/java/eroiko/ani/img/wallpaper79.png").toURI().toString()));
            stage.show();
        } catch (Exception e){
            System.out.println(e.toString());
            if (!quit){
                System.err.println(e.toString());
            }
        }
    }

    @FXML
    void GoSearch(ActionEvent event) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.titleProperty().set("Message");
        alert.headerTextProperty().set("There is no database yet!");
        alert.showAndWait();
    }

    @FXML
    public void OpenPropertiesWindow(ActionEvent event) {
        PropertiesController.quit = quit;
        try {
            var stage = new Stage();
            stage.setTitle("Properties");
            stage.setScene(new Scene(FXMLLoader.load(new File("src/main/java/eroiko/ani/view/PropertiesWindow.fxml").toURL())));
            stage.getIcons().add(new Image(new File("src/main/java/eroiko/ani/img/wallpaper79.png").toURI().toString()));
            stage.show();
        } catch (Exception e){
            System.out.println(e.toString());
            if (!quit){
                System.err.println(e.toString());
            }
        }
    }

    @FXML
    void startTerminal(ActionEvent event) {
        try {
            pipIn = new PipedInputStream();
            System.setOut(new PrintStream(new PipedOutputStream(pipIn), true));
        } catch (IOException e1) {
            System.err.println(e1.toString());
        }
        this.quit = false;
        new TerminalThread(pipIn, terminalThread, Terminal_out, quit);
        System.out.println("GUI Terminal is activated!" + "\nUse Ctrl + C to cancel the terminal, and Ctrl + L to clear the text.");
    }

    @FXML
    void killTerminal(ActionEvent event) {
        killTerminal();
    }
    
    synchronized void killTerminal(){
        if (!quit){
            System.out.println("Closing GUI Terminal..."); // 在 GUI Terminal 上輸出
            this.quit = true;
            notifyAll();
            try {
                this.terminalThread.join(1000l);
                this.pipIn.close();
            } catch (Exception e){}
            System.setOut(stdOut);
            System.out.println("GUI Terminal has closed.");
        }
        else {
            System.out.println("Terminal has already been shutdown.");
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb){
        imagePreview.setOnMouseEntered((e) -> {
            System.out.println("You touched the image!");
            if (!quit){
                System.err.println("You touched the image!");
            }
        });
        initializeKeyBoardShortcuts();
        MinimizeWindow.minimizeWindow(MainApp.mainStage);
    }
    
    public void initializeKeyBoardShortcuts(){
        Terminal_in.addEventFilter(KeyEvent.KEY_PRESSED, (e) -> {
            if (new KeyCodeCombination(KeyCode.ENTER).match(e)){
                System.out.println(Terminal_in.getText());
                if (!quit){
                    System.err.println(Terminal_in.getText());
                }
                Terminal_in.clear();
                e.consume();
            }
            else if (new KeyCodeCombination(KeyCode.C, KeyCodeCombination.CONTROL_DOWN).match(e)){
                System.out.println("GUI Terminal Quit : KeyBoard Interrupt");
                killTerminal();
                Terminal_in.clear();
                e.consume();
            }
            else if (new KeyCodeCombination(KeyCode.L, KeyCodeCombination.CONTROL_DOWN).match(e)){
                Terminal_out.clear();
                e.consume();
            }
        });
        Terminal_out.addEventFilter(KeyEvent.KEY_PRESSED, (e) -> {
            if (new KeyCodeCombination(KeyCode.ENTER).match(e)){
                System.out.println(Terminal_in.getText());
                if (!quit){
                    System.err.println(Terminal_in.getText());
                }
                Terminal_in.clear();
                e.consume();
            }
            else if (new KeyCodeCombination(KeyCode.C, KeyCodeCombination.CONTROL_DOWN).match(e)){
                System.out.println("GUI Terminal Quit : KeyBoard Interrupt");
                killTerminal();
                Terminal_in.clear();
                e.consume();
            }
            else if (new KeyCodeCombination(KeyCode.L, KeyCodeCombination.CONTROL_DOWN).match(e)){
                Terminal_out.clear();
                e.consume();
            }
        });
    }
}
