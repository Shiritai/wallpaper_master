package eroiko.ani.controller;

import java.io.*;
import java.net.URL;
import java.util.ResourceBundle;

import eroiko.ani.MainApp;
import eroiko.ani.controller.ConsoleTextArea.TerminalThread;
import eroiko.ani.controller.PrimaryControllers.*;
import eroiko.ani.model.NewCrawler.CrawlerManager;
import eroiko.ani.util.*;
import eroiko.ani.util.WallpaperClass.*;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
// import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class MainController implements Initializable {
    /* Support variables */
    public static WallpaperImage preview;
    public static ImageView staticImagePreview;
    public static boolean hasFull;
    public static BooleanProperty hasChangedPreview = new SimpleBooleanProperty(false);
    public static Label staticPathLabel;

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
    @FXML private SplitPane terminalButtonDivider;

    @FXML private ImageView imagePreview;
    
    @FXML private TreeView<String> treeFileExplorer;
    @FXML private Label pathLabel;
    @FXML private BorderPane openWindowsFileExplorer = new BorderPane();
    
    /* About Search */
    @FXML private TextField searchBar;
    public static final String [] modes = {"Many (hundreds)", "Decent (about 200)", "Snapshot (about 100)"};
    public static boolean okToGo;
    @FXML private Button addButton;
    @FXML private ChoiceBox<String> downloadAmountChoice;
    @FXML private TableView<myPair<String, String>> searchQueue;
    @FXML private TableColumn<myPair<String, String>, String> keywords;
    @FXML private TableColumn<myPair<String, String>, String> amount;
    /* About downloading processing */
    @FXML private ProgressBar mainPbar;
    // @FXML private ProgressIndicator searchProgressIndicator;
    @FXML private TextField nowProcessingText;
    @FXML private Label progressBarText;
    @FXML private Text percentageMark;

    // public static boolean isHaltOrRun = false;
    
    private Service<Void> crawlerThread;

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
    
    @FXML
    void DeleteAllQueue(ActionEvent event) {
        searchQueue.getItems().clear();
    }
    
    @FXML
    void DeleteSelectedQueue(ActionEvent event) {
        deleteSelectedSearchQueue();
    }

    @FXML
    void GoSearch(ActionEvent event) {
        if (searchQueue.getItems().size() > 0 && okToGo){
            okToGo = false;
            StartWalkingQueue();
        }
        else if (!okToGo){
            new Alert(Alert.AlertType.INFORMATION, "We are downloading for you... please wait a minute :)").showAndWait();
        }
        else {
            new Alert(Alert.AlertType.INFORMATION, "Please add some keywords :)").showAndWait();
        }
    }
    
    void StartWalkingQueue(){
        ObservableList<myPair<String, String>> data = searchQueue.getItems();
        int size = data.size();
        crawlerThread = new Service<Void>(){
            @Override
            protected Task<Void> createTask(){
                return new Task<Void>(){
                    @Override
                    protected Void call() throws Exception {
                        for (int i = 0; i < size; ++i){
                            if (isCancelled()){
                                throw new InterruptedException();
                            }
                            int mode = switch (data.get(i).value){
                                case "Many (hundreds)" -> 6;
                                case "Decent (about 200)" -> 2;
                                case "Snapshot (about 100)" -> 1;
                                default -> -1;
                            };
                            updateMessage("Ready...");
                            var cw = new CrawlerManager(SourceRedirector.defaultDataPath.toAbsolutePath().toString(), SourceRedirector.capitalize(data.get(i).key).split(" "), mode);
                            // var cw = new CrawlerManager(SourceRedirector.defaultDataPath.toAbsolutePath().toString(), SourceRedirector.capitalize(data.get(i).key).split(" "), 1);
                            updateMessage("Fetch image information");
                            cw.A_getLinks();
                            updateMessage("Download preview wallpapers");
                            cw.B_download();

                            updateMessage("Peek links");
                            cw.print();

                            updateMessage("Open preview view window...");        //////////////////
                            cw.C_openWallpaperFilterViewer();        //////////////////
                            updateMessage("Download full wallpapers");       //////////////////
                            cw.D_lastDownloadStage();        //////////////////
                            updateMessage("Open full view window...");       //////////////////
                            cw.E_openFullWallpaperFilterViewer();        //////////////////
                            updateMessage("Done!");      //////////////////
                        }
                        return null;
                    }
                };
            }
        };
        percentageMark.setText("Pending...");
        progressBarText.textProperty().bind(crawlerThread.messageProperty());
        var it = data.iterator();
        progressBarText.textProperty().addListener((a, b, c) -> {
            if (c.equals("Ready...") && it.hasNext()){
                nowProcessingText.setText("Now Processing : " + it.next().key);
            }
        });
        // percentageMark.textProperty().bind(CrawlerManager.progress.asString());
        crawlerThread.setOnSucceeded(new EventHandler<WorkerStateEvent>(){
            @Override
            public void handle(WorkerStateEvent e){
                System.out.println("Done, closing crawlerThread.");
                progressBarText.textProperty().unbind();
                searchQueue.getItems().clear();
                nowProcessingText.clear();
                okToGo = true;
            }
        });
        crawlerThread.restart();
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
        try {
            if (WallpaperComparator.isImage(file)){
                preview = new WallpaperImage(file.getParent().toAbsolutePath().toString(), false, file);
                hasChangedPreview.set(true);
                imagePreview.setImage(preview.getCurrentWallpaper());    
            }
            else {
                preview = new WallpaperImage(file.toAbsolutePath().toString(), false);
                hasChangedPreview.set(true);
                imagePreview.setImage(preview.getCurrentWallpaper());
            }
        } catch (IOException e) {
            return;
        }
    }
    
    @FXML
    void PreviewImageDragOut(MouseEvent event) {
        Dragboard db = imagePreview.startDragAndDrop(TransferMode.ANY);
        var cb  = new ClipboardContent();
        cb.putImage(preview.getCurrentWallpaper());
        db.setContent(cb);
        event.consume();
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
        try {
            if (WallpaperComparator.isImage(file)){
                OpenWallpaperViewWindow(new WallpaperImage(file.getParent().toAbsolutePath().toString(), false, file));
            }
            OpenWallpaperViewWindow(new WallpaperImage(file.toAbsolutePath().toString(), false));
        } catch (IOException e) {
            System.out.println(e.toString());
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
        System.out.println("@2021/05/01 by Eroiko\n" + "GUI Terminal is activated!" + "\nUse Ctrl + C to cancel the terminal, and Ctrl + L to clear the text.");
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
    void TerminateProcess(ActionEvent event) {
        if (crawlerThread != null && crawlerThread.isRunning()){
            System.out.println(crawlerThread.getState().toString());
            if (!quit){
                System.err.println(crawlerThread.getState().toString());
            }
            crawlerThread.cancel();
        }
        else {
            new Alert(Alert.AlertType.INFORMATION, "There is nothing running now...").showAndWait();
        }
    }
    
    @FXML
    void RestartProcessThread(ActionEvent event) {
        if (crawlerThread != null && crawlerThread.isRunning()){
            System.out.println(crawlerThread.getState().toString());
            if (!quit){
                System.err.println(crawlerThread.getState().toString());
            }
            crawlerThread.restart();
        }
        else {
            new Alert(Alert.AlertType.INFORMATION, "There is nothing running now...").showAndWait();
        }
    }
    
    @FXML
    void ResetWindow(ActionEvent event) {
        System.out.println("Resetting...");
        if (!quit){
            System.err.println("Resetting...");
        }
        initialize(null, null);
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
    
    @FXML
    void SwitchBackToImgPath(ActionEvent event) {
        try {
            preview = new WallpaperImage();
            imagePreview.setImage(preview.getCurrentWallpaper());
        } catch (IOException e) {
            System.out.println(e.toString());
        }
    }

    @FXML
    void MenuFileOpenFileExplorer(ActionEvent event) {
        OpenFileExplorer();
    }

    public void OpenFileExplorer(){
        try {
            Runtime.getRuntime().exec("explorer /select," + preview.getCurrentWallpaperPath());
        } catch (IOException ex) {
            System.out.println(ex.toString());
            if (!quit){
                System.err.println(ex.toString());
            }
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb){

        try {
            preview = new WallpaperImage();
        } catch (IOException e) {
            System.out.println(e.toString());
        }
        okToGo = true;
        staticImagePreview = imagePreview;
        hasFull = false;
        staticPathLabel = pathLabel;
        imagePreview.setImage(preview.getCurrentWallpaper());

        Terminal_out.setEditable(false);

        searchBar.setPromptText(">  Search Artwork");

        terminalButtonDivider.setMinWidth(100);
        
        downloadAmountChoice.getItems().addAll(modes[0], modes[1], modes[2]);
        downloadAmountChoice.setValue(modes[1]);
        pathLabel.setText(" " + SourceRedirector.defaultDataPath.toString()) ;
        // mainPbar = new ProgressBar();
        hasChangedPreview.addListener((a, b, c) -> {
            pathLabel.setText(" " + preview.getCurrentWallpaperPath().getParent().toString());
            hasChangedPreview.set(false);
            imagePreview.setImage(MainController.preview.getCurrentWallpaper());
        });
        searchQueue.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        CrawlerManager.progress.addListener((a, b, c) -> {
            mainPbar.progressProperty().bind(CrawlerManager.progress);
            percentageMark.textProperty().bind(myDoubleToStringProperty.toStringProperty(CrawlerManager.progress));
        });
        nowProcessingText.setEditable(false);
        
        initializeKeyBoardShortcuts();
        initializeMouseEvents();
        initSearchQueue();
    }

    public void initializeMouseEvents(){
        imagePreview.setOnMouseClicked((e) -> {
            if (e.getClickCount() == 2){
                if (hasFull){
                    OpenWallpaperViewWindow(SourceRedirector.wallpaperImageWithFilter);
                }
                else {
                    OpenWallpaperViewWindow(preview);
                }
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
        addButton.setOnMouseClicked(e -> {
            var tmp = searchBar.getText();
            if (tmp.length() > 0){
                if (tmp.length() > 3 && tmp.charAt(0) != '\n' && tmp.charAt(0) != '\r'){
                    addSearchQueue();
                }
                else if (tmp.length() <= 3 && tmp.charAt(0) != '\n'){
                    new Alert(Alert.AlertType.INFORMATION, "keyword is too short! Please check again :)").showAndWait();
                }
            }
            else {
                searchBar.clear();
            }
        });
        openWindowsFileExplorer.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2){
                OpenFileExplorer();
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
                var tmp = searchBar.getText();
                if (tmp.length() > 0){
                    if (tmp.length() >= 60){
                        new Alert(Alert.AlertType.INFORMATION, "Bad keyword! Please check again :)").showAndWait();
                    }
                    else if (tmp.length() <= 3 && tmp.charAt(0) != '\n'){
                        new Alert(Alert.AlertType.INFORMATION, "keyword is too short! Please check again :)").showAndWait();
                    }
                    else if (tmp.length() > 3 && tmp.charAt(0) != '\n' && tmp.charAt(0) != '\r'){
                        addSearchQueue();
                    }
                }
                else {
                    searchBar.clear();
                }
                e.consume();
            }
        });
    }

    private void initSearchQueue(){
        /* Set the two columns */
        keywords.setMinWidth(170);
        keywords.setPrefWidth(170);
        keywords.setCellValueFactory(new PropertyValueFactory<>("key"));
        amount.setMinWidth(130);
        amount.setPrefWidth(130);
        amount.setCellValueFactory(new PropertyValueFactory<>("value"));
    }

    /** 新增至佇列, 並確認是否合法 */
    private void addSearchQueue(){
        String keyword = searchBar.getText();
        searchBar.clear();
        Service<Boolean> check = new Service<Boolean>(){
            @Override
            protected Task<Boolean> createTask(){
                return new Task<Boolean>(){
                    @Override 
                    protected Boolean call(){
                        return CrawlerManager.checkValidation(SourceRedirector.capitalize(keyword));
                    }
                };
            }
        };
        check.setOnSucceeded(new EventHandler<WorkerStateEvent>(){
            @Override
            public void handle(WorkerStateEvent e){
                if (check.getValue()){
                    var tmpData = new myPair<String, String>(
                        SourceRedirector.capitalize(keyword), downloadAmountChoice.getValue()
                    );
                    System.out.println("Add " + tmpData.key + " : " + tmpData.value + " to Search Queue");
                    searchQueue.getItems().add(tmpData);
                    searchBar.setStyle("-fx-background-color: #ffffff;");
                }
                else {
                    searchBar.setText(keyword);
                    searchBar.setStyle("-fx-background-color: #efb261;");
                    searchBar.selectAll();
                    new Alert(Alert.AlertType.INFORMATION, "Invalid keywords! Please check again :)").showAndWait();
                }
            }
        });
        check.restart();
    }

    private void deleteSelectedSearchQueue(){
        var allData = searchQueue.getItems();
        var selectedData = searchQueue.getSelectionModel().getSelectedItems();
        for (int i = selectedData.size() - 1; i >= 0; --i){
            allData.remove(selectedData.get(i));
        }
    }
}
