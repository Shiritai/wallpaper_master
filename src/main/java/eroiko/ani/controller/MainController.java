package eroiko.ani.controller;

import java.io.*;
import java.net.URL;
import java.util.ResourceBundle;

import eroiko.ani.MainApp;
import eroiko.ani.controller.ConsoleTextArea.TerminalThread;
import eroiko.ani.controller.ControllerSupporter.WallpaperImage;
import eroiko.ani.controller.ControllerSupporter.WallpaperImageWithFilter;
import eroiko.ani.controller.PrimaryControllers.PreferenceController;
import eroiko.ani.controller.PrimaryControllers.TestingController;
import eroiko.ani.controller.PrimaryControllers.WallpaperViewController;
import eroiko.ani.model.Crawler.OldCrawlerManager;
import eroiko.ani.model.NewCrawler.CrawlerThread;
import eroiko.ani.util.SourceRedirector;
import eroiko.ani.util.WallpaperComparator;
import eroiko.ani.util.myPair;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class MainController implements Initializable {
    /* Support variables */
    public static WallpaperImage preview;
    public static ImageView staticImagePreview;
    // public static boolean downloadDeliver = false;
    // public static BooleanProperty downloadViewOpener = new SimpleBooleanProperty(false);
    // public static Stage wallpaperViewStage = new Stage();
    /* Terminal */
    private static PrintStream stdOut = new PrintStream(System.out);
    public PipedInputStream pipIn = new PipedInputStream();
    private Thread terminalThread;
    public static boolean quit = true;
    /* Progress bar and indicator */
    public static ProgressBar MainCtrlPbar = new ProgressBar();
    public static ProgressIndicator MainCtrlPin = new ProgressIndicator();
    /* FXML variables */
    @FXML private TextArea Terminal_out = new TextArea();
    @FXML private TextField Terminal_in = new TextField();  
    @FXML private ProgressBar mainPbar;
    // @FXML private ProgressIndicator searchProgressIndicator;
    @FXML private ImageView imagePreview;
    @FXML private TreeView<String> treeFileExplorer;
    @FXML private Label pathLabel;
    @FXML private TextField searchBar;
    @FXML private Label progressBarText;
    @FXML private BorderPane openWindowsFileExplorer = new BorderPane();
    
    
    @FXML private Button addButton;
    @FXML private ChoiceBox<String> downloadAmountChoice;
    @FXML private TableView<myPair<String, String>> searchQueue;
    @FXML private TableColumn<myPair<String, String>, String> keywords;
    @FXML private TableColumn<myPair<String, String>, String> amount;
    @FXML private Button deleteSelectedButton;

    @FXML
    void hitExit(ActionEvent event) {
        Exit();
    }

    public void Exit() {
        if (terminalThread != null){
            killTerminal();
        }
        MainApp.mainStage.close();
        Platform.exit();
        System.exit(0);
    }

    @FXML
    void OpenTestingWindow(ActionEvent event) {
        TestingController.quit = quit;
        try {
            var wallpaperViewStage = new Stage();
            wallpaperViewStage.setTitle("Testing window");
            wallpaperViewStage.setScene(new Scene(FXMLLoader.load(getClass().getClassLoader().getResource("eroiko/ani/view/TestingWindow.fxml"))));
            wallpaperViewStage.getIcons().add(new Image(getClass().getClassLoader().getResource("eroiko/ani/img/wallpaper79.png").toString()));
            wallpaperViewStage.show();
        } catch (Exception e){
            System.out.println(e.toString());
            if (!quit){
                System.err.println(e.toString());
            }
        }
    }
    
    void Search(){
        if (SourceRedirector.useOldCrawlerForFullSpeedMode){
            new OldCrawlerManager(searchBar.getText(), SourceRedirector.pagesToDownload, quit);
        }
        else {
            new CrawlerThread(TestFunctions.testWallpaperPath.toAbsolutePath().toString(), searchBar.getText().split(" "));

        }
    //     Alert alert = new Alert(AlertType.INFORMATION);
    //     alert.titleProperty().set("Message");
    //     alert.headerTextProperty().set("Wrong keywords, please check and search again.");
    //     alert.showAndWait();
    }

    @FXML
    void OpenPreferenceWindow(ActionEvent event) {
        OpenPreferenceWindow();
    }
    
    public void OpenPreferenceWindow(){
        PreferenceController.quit = quit;
        try {
            var stage = new Stage();
            stage.setTitle("Properties");
            stage.setScene(new Scene(FXMLLoader.load(getClass().getClassLoader().getResource("eroiko/ani/view/PreferenceWindow.fxml"))));
            stage.getIcons().add(new Image(getClass().getClassLoader().getResource("eroiko/ani/img/wallpaper79.png").toString()));
            stage.show();
        } catch (Exception e){
            System.out.println(e.toString());
            if (!quit){
                System.err.println(e.toString());
            }
        }
    }

    @FXML
    void PreviewDragOver(DragEvent event) { // 當檔案拖曳到 preview (stack pane) 時, 顯示可以放上來
        if (event.getDragboard().hasFiles()){
            event.acceptTransferModes(TransferMode.ANY);
        }
    }
    
    @FXML
    void PreviewDragDropped(DragEvent event) {
        var files = event.getDragboard().getFiles();
        var file = files.get(0).toPath();
        if (WallpaperComparator.isImage(file)){
            preview = new WallpaperImage(file.getParent().toAbsolutePath().toString(), false);
            imagePreview.setImage(preview.getCurrentWallpaper());    
        }
    }
    
    @FXML
    void WholeDragOver(DragEvent event) {
        if (event.getDragboard().hasFiles()){
            event.acceptTransferModes(TransferMode.ANY);
        }
    }
    
    @FXML
    void WholeDragDropped(DragEvent event) {
        var files = event.getDragboard().getFiles();
        var file = files.get(0).toPath();
        if (WallpaperComparator.isImage(file)){
            OpenWallpaperViewWindow(new WallpaperImage(file.getParent().toAbsolutePath().toString(), false));
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
        quit = false;
        SourceRedirector.quit = quit;
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
            quit = true;
            SourceRedirector.quit = quit;
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

    @FXML
    void hitMinimize(ActionEvent event) {
        MainApp.mainStage.hide();
    }

    void OpenWallpaperViewWindow(WallpaperImage wp, int width, int height) {
        WallpaperViewController.quit = quit;
        int serialNumber = SourceRedirector.addWallpaper(wp);
        try {
            var stage = new Stage();
            if (width != 0){
                stage.setWidth(width);
                stage.setHeight(height);
            }
            if (wp instanceof WallpaperImageWithFilter){
                System.out.println("Open Wallpaper Filter...");
                stage.setTitle("Wallpaper Filter");
                stage.setScene(new Scene(FXMLLoader.load(getClass().getClassLoader().getResource("eroiko/ani/view/WallpaperChooseWindow.fxml"))));
                stage.getIcons().add(new Image(getClass().getClassLoader().getResource("eroiko/ani/img/wallpaper79.png").toString()));
                stage.setOnCloseRequest(e -> {
                    SourceRedirector.deleteWallpaper(serialNumber);
                });
            }
            else {
                System.out.println("Open Wallpaper Viewer...");
                stage.setTitle("Wallpaper Viewer");
                stage.setScene(new Scene(FXMLLoader.load(getClass().getClassLoader().getResource("eroiko/ani/view/WallpaperViewWindow.fxml"))));
                stage.getIcons().add(new Image(getClass().getClassLoader().getResource("eroiko/ani/img/wallpaper79.png").toString()));
                stage.setOnCloseRequest(e -> {
                    SourceRedirector.deleteWallpaper(serialNumber);
                });
            }
            stage.show();
        } catch (Exception e){
            e.printStackTrace();
            System.out.println(e.toString());
            if (!quit){
                System.err.println(e.toString());
            }
        }
    }

    void OpenWallpaperViewWindow(WallpaperImage wp) {
        OpenWallpaperViewWindow(wp, 0, 0);
    }

    @Override
    public void initialize(URL url, ResourceBundle rb){
        preview = new WallpaperImage();
        staticImagePreview = imagePreview;
        imagePreview.setImage(preview.getCurrentWallpaper());
        Terminal_out.setEditable(false);
        // downloadAmountChoice = new ChoiceBox<>();
        searchBar.setPromptText(">  Search Artwork");
        Terminal_in.setPromptText(" <Terminal_Input>");
        downloadAmountChoice.getItems().addAll("Many (hundreds)", "Decent (about 100)", "Snapshot (several dozen)");
        downloadAmountChoice.setValue("Decent (about 100)");
        mainPbar = MainCtrlPbar;
        // searchProgressIndicator = MainCtrlPin;
        initializeKeyBoardShortcuts();
        initializeMouseEvents();
        initSearchQueue();
    }

    public void initializeMouseEvents(){
        imagePreview.setOnMouseClicked((e) -> {
            if (e.getClickCount() == 2){
                OpenWallpaperViewWindow(preview);
            }
        });
        imagePreview.setOnScroll((ScrollEvent e) -> {
            var dist = e.getDeltaY();
            if (dist > 0){
                imagePreview.setImage(preview.getNextWallpaper());
            }
            else if (dist < 0){
                imagePreview.setImage(preview.getLastWallpaper());
            }
            e.consume();
        });
        // imagePreview.setOnMouseDragReleased(e -> {
            
        // });
        addButton.setOnMouseClicked(e -> addSearchQueue());
        deleteSelectedButton.setOnMouseClicked(e -> deleteSelectedSearchQueue());
        openWindowsFileExplorer.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2){
                try {
                    Runtime.getRuntime().exec("explorer /select," + TestFunctions.testWallpaperPath.toAbsolutePath().toString());
                } catch (IOException ex) {
                    System.out.println(ex.toString());
                    if (!quit){
                        System.err.println(ex.toString());
                    }
                }
            }
        });
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
                Terminal_out.setText("");
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
                Terminal_out.setText("");
                e.consume();
            }
        });
        searchBar.addEventFilter(KeyEvent.KEY_PRESSED, (e) -> {
            if (new KeyCodeCombination(KeyCode.ENTER).match(e)){
                // Search();
                addSearchQueue();
                e.consume();
            }
        });
    }

    private void initSearchQueue(){
        // keywords = new TableColumn<>("Keywords");
        keywords.setMinWidth(150);
        keywords.setCellValueFactory(new PropertyValueFactory<>("key"));

        // amount = new TableColumn<>("Amount");
        amount.setMinWidth(150);
        amount.setCellValueFactory(new PropertyValueFactory<>("value"));

        // searchQueue = new TableView<>();
        // searchQueue.getColumns().add(keywords);
        // searchQueue.getColumns().add(amount);
        // searchQueue.getItems();
    }

    private void addSearchQueue(){
        var tmpData = new myPair<String, String>(
            searchBar.getText(), downloadAmountChoice.getValue()
        );
        searchBar.clear();
        System.out.println("Add " + tmpData.key + " : " + tmpData.value + " to Search Queue");
        searchQueue.getItems().add(tmpData);
    }

    private void deleteSelectedSearchQueue(){
        var allData = searchQueue.getItems();
        var selectedData = searchQueue.getSelectionModel().getSelectedItems();
        selectedData.forEach(allData::remove);
    }

    // public ObservableList<myPair<String, String>> getQueueList(){
    //     ObservableList<myPair<String, String>> list = FXCollections.observableArrayList();
        
    // }
}
