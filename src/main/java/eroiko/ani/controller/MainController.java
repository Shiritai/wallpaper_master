package eroiko.ani.controller;

import java.awt.Desktop;
import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.TreeSet;

import eroiko.ani.MainApp;
import eroiko.ani.controller.ConsoleTextArea.TerminalThread;
import eroiko.ani.controller.PrimaryControllers.*;
import eroiko.ani.model.CLI.Console;
import eroiko.ani.model.CLI.CLIException.ClearTerminalException;
import eroiko.ani.model.NewCrawler.CrawlerManager;
import eroiko.ani.util.Method.DoubleToStringProperty;
import eroiko.ani.util.Method.Dumper;
import eroiko.ani.util.Method.TimeWait;
import eroiko.ani.util.MyDS.myPair;
import eroiko.ani.util.NeoWallpaper.*;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

public class MainController implements Initializable {
    /* Support variables */
    public static ImageView staticImagePreview;
    public static BooleanProperty hasChangedPreview = new SimpleBooleanProperty(false);
    public static Label staticPathLabel;
    public static Wallpaper theWallpaper;

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
    public Console console;

    @FXML private ImageView imagePreview;
    
    /* File explorer */
    @FXML private TreeView<myPair<String, Path>> treeFileExplorer;
    @FXML private TilePane viewImageTileTable;
    @FXML private ScrollPane scrollableTile;
    @FXML private Label pathLabel;
    @FXML private BorderPane openWindowsFileExplorer = new BorderPane();
    @FXML private BorderPane refreshExplorer;
    @FXML private TabPane tableOfBrowser;
    
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
    private Service<Void> crawlerThread;
    private Service<Void> doBFS;
    @FXML private ProgressBar mainPbar;
    @FXML private TextField nowProcessingText;
    @FXML private Label progressBarText;
    @FXML private Text percentageMark;

    private Path currentPath;


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
    void DeleteAllQueue(ActionEvent event) {
        searchQueue.getItems().clear();
    }
    
    @FXML
    void DeleteSelectedQueue(ActionEvent event) {
        deleteSelectedSearchQueue();
    }

    @FXML
    void GoSearch(ActionEvent event) {
        new TimeWait(2000); // 等待 Queue 準備好
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
    
    public void StartWalkingQueue(){
        ObservableList<myPair<String, String>> data = searchQueue.getItems();
        int size = data.size();
        MusicWithSyamiko.playProcessing();
        crawlerThread = new Service<Void>(){
            @Override
            protected Task<Void> createTask(){
                return new Task<Void>(){
                    @Override
                    protected Void call() throws Exception {
                        for (int i = 0; i < size; ++i){
                            int mode = switch (data.get(i).value){
                                case "Many (hundreds)" -> 6;
                                case "Decent (about 200)" -> 2;
                                case "Snapshot (about 100)" -> 1;
                                default -> -1;
                            };
                            updateMessage("Ready...");
                            var cw = new CrawlerManager(WallpaperPath.DEFAULT_TMP_WALLPAPER_PATH.toString(), WallpaperUtil.capitalize(data.get(i).key).split(" "), mode);
                            updateMessage("Fetch image information");
                            cw.A_getLinks();
                            updateMessage("Download preview wallpapers");
                            cw.B_download();
                            updateMessage("Peek links");
                            cw.print();
                            updateMessage("Download full wallpapers");
                            cw.D_lastDownloadStage();
                            updateMessage("Preparing view window...");
                            cw.E_pushWallpaper();
                            updateMessage("Done!");
                        }
                        return null;
                    }
                };
            }
        };
        percentageMark.setText("Pending...");
        var it = data.iterator();
        progressBarText.textProperty().addListener((a, b, c) -> {
            if (c.equals("Ready...") && it.hasNext()){
                nowProcessingText.setText("Now Processing : " + it.next().key);
            }
            else if ((c.equals("Preparing view window...") || c.equals("Preparing view window..."))
                && it.hasNext() && PreferenceController.showWallpapersAfterCrawling.get()){
                new TimeWait(2000);
                try {
                    OpenWallpaper(null);
                } catch (IOException e1) {
                    System.out.println(e1.toString());
                }
                it.remove();
            }
        });
        progressBarText.textProperty().bind(crawlerThread.messageProperty());
        crawlerThread.setOnSucceeded(new EventHandler<WorkerStateEvent>(){
            @Override
            public void handle(WorkerStateEvent e){
                System.out.println("Done, closing crawlerThread.");
                MusicWithSyamiko.playComplete();
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
    
    public static void OpenPreferenceWindow(){
        PreferenceController.quit = quit;
        try {
            var stage = new Stage();
            stage.setTitle("Properties");
            stage.setScene(new Scene(FXMLLoader.load(WallpaperPath.FXML_SOURCE_PATH.resolve("PreferenceWindow.fxml").toUri().toURL())));
            stage.getIcons().add(MainApp.icon);
            stage.show();
        } catch (Exception e){
            System.out.println(e.toString());
            if (!quit){
                System.err.println(e.toString());
            }
        }
    }
    
    @FXML
    void OpenAboutWindow(ActionEvent event) {
        OpenAboutWindow();
    }
    
    public static void OpenAboutWindow(){
        try {
            var stage = new Stage();
            stage.setTitle("About");
            stage.setScene(new Scene(FXMLLoader.load(WallpaperPath.FXML_SOURCE_PATH.resolve("AboutWindow.fxml").toUri().toURL())));
            stage.getIcons().add(MainApp.icon);
            stage.setResizable(false);
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
            if (WallpaperUtil.isImage(file)){ // image
                OpenWallpaper(new Wallpaper(file.getParent(), file));
            }
            else { // directory
                OpenWallpaper(new Wallpaper(file));
            }
        } catch (IOException e) {
            System.out.println(e.toString());
        }
    }
    
    @FXML
    void PreviewImageDragOut(MouseEvent event) {
        Dragboard db = imagePreview.startDragAndDrop(TransferMode.ANY);
        var cb  = new ClipboardContent();
        cb.putImage(theWallpaper.getCurrentFullImage());
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
            if (WallpaperUtil.isImage(file)){
                theWallpaper = new Wallpaper(file.getParent(), file);
                hasChangedPreview.set(true);
                imagePreview.setImage(theWallpaper.getCurrentPreviewImage());
            }
            else {
                currentPath = file;
                pathLabel.setText(" " + currentPath.toAbsolutePath());
                theWallpaper = new Wallpaper(currentPath);
                hasChangedPreview.set(true);
                imagePreview.setImage(theWallpaper.getCurrentPreviewImage());
            }
            event.consume();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
    }
    
    @FXML
    void startTerminal(ActionEvent event) {
        startTerminal();
    }
    
    void startTerminal(){    
        try {
            pipIn = new PipedInputStream();
            System.setOut(new PrintStream(new PipedOutputStream(pipIn), true));
        } catch (IOException e1) {
            System.err.println(e1.toString());
        }
        quit = false;
        new TerminalThread(pipIn, terminalThread, Terminal_out, quit);
        System.out.println("Create by Eroiko, terminal version 1.0 at 2021/06/05" +
        "\nUse Ctrl + C to cancel the terminal, and Ctrl + L to clear the text." +
        "\nSupport several linux-based commands such as\n\"ls\", \"cd <../dir_path>\", \"mkdir/rm <dir_path>\", etc." +
        "\ntry key in \"meow\" and see what will happen OwO\n");
        console = new Console(currentPath, MainApp.hostName, MainApp.userName, true);
    }

    @FXML
    void killTerminal(ActionEvent event) {
        killTerminal();
    }
    
    synchronized void killTerminal(){
        console = null;
        if (!quit){
            System.out.println("Closing GUI Terminal..."); // 在 GUI Terminal 上輸出
            quit = true;
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
    void retryProcessingCrawler(ActionEvent event) {
        crawlerThread.restart();
        percentageMark.setText("restart crawler");
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

    @FXML
    void OpenMusicWithSyamiko(ActionEvent event) {
        MusicWithSyamiko.openMusicWithSyamiko();
    }

    @FXML
    void OpenMusicWithAkari(ActionEvent event) { // 暫時僅支持以音樂檔形式開啟
        OpenMusicWithAkari();
    }
    
    public static void OpenMusicWithAkari(){
        var tmp = new javafx.stage.FileChooser();
        tmp.setTitle("Open music with Akari");
        try {
            tmp.getExtensionFilters().addAll(new javafx.stage.FileChooser.ExtensionFilter("Audio Files", "*.wav", "*.mp3"));
            MusicWithAkari.openMusicWithAkari(tmp.showOpenDialog(null).toPath());
        } catch (Exception e){} // 表示沒做選擇, InvocationTargetException
    }
    
    public static void OpenWallpaper(Wallpaper wp) throws IOException{
        WallpaperController.quit = quit;
        int serialNumber = -1;
        if (wp != null){
            serialNumber = Wallpaper.addNewWallpaper(wp);
        }
        else {
            serialNumber = Wallpaper.getWallpaperSerialNumberImmediately();
            wp = Wallpaper.getWallpaper(serialNumber);
        }
        var fixedWp = wp;
        boolean isPreview = wp.getCurrentFullPath().getParent().equals(WallpaperPath.DEFAULT_IMAGE_PATH);
        System.out.println("Is preview ? " + isPreview);
        final int fixedSerialNumber = serialNumber;
        var stage = new Stage();
        System.out.println("Open Neo Wallpaper Viewer...");
        stage.setTitle("Neo Wallpaper Viewer");
        var wallpaperScene = new Scene(FXMLLoader.load(WallpaperPath.FXML_SOURCE_PATH.resolve("WallpaperWindow.fxml").toUri().toURL()));
        wallpaperScene.addEventFilter(KeyEvent.KEY_PRESSED, e -> {
            if (e.getCode() == KeyCode.RIGHT){
                if (!fixedWp.isEmpty()){
                    fixedWp.rightShift();
                }
            }
            else if (e.getCode() == KeyCode.LEFT){
                if (!fixedWp.isEmpty()){
                    fixedWp.leftShift();
                }
            }
            if (!isPreview){
                if (e.getCode() == KeyCode.EQUALS || e.getCode() == KeyCode.UP){
                    if (!fixedWp.isEmpty()){
                        fixedWp.add();
                    }
                    fixedWp.triggerChangedFlag();
                }
                else if (e.getCode() == KeyCode.MINUS || e.getCode() == KeyCode.DOWN){
                    if (!fixedWp.isEmpty()){
                        fixedWp.delete();
                    }
                    fixedWp.triggerChangedFlag();
                }
            }
            e.consume();
        });
        stage.setScene(wallpaperScene);
        stage.getIcons().add(MainApp.icon);
        stage.setOnCloseRequest(e -> {
            Wallpaper.appendToResultList(fixedSerialNumber);
        });
        stage.show();
    }
    
    @FXML
    void SwitchBackToImgPath(ActionEvent event) {
        try {
            theWallpaper = new Wallpaper();
            currentPath = WallpaperPath.DEFAULT_DATA_PATH;
            pathLabel.setText(" " + currentPath);
            imagePreview.setImage(theWallpaper.getCurrentPreviewImage());
        } catch (IOException e) {
            System.out.println(e.toString());
        }
        initTreeView();
    }

    @FXML
    void MenuFileOpenFileExplorer(ActionEvent event) {
        OpenFileExplorer();
    }

    public void OpenFileExplorer(){
        try {
            Runtime.getRuntime().exec("explorer /select," + currentPath);
        } catch (IOException ex) {
            System.out.println(ex.toString());
            if (!quit){
                System.err.println(ex.toString());
            }
        }
    }

    @FXML
    void WallpaperizeAFolder(ActionEvent event) {
        WallpaperizeFolder(false);
    }
    
    @FXML
    void WallpaperizeAFolderAsDefault(ActionEvent event) {
        WallpaperizeFolder(true);
    }
    
    public void WallpaperizeFolder(boolean toDefault){
        var tmp = new DirectoryChooser();
        tmp.setTitle("Choose a folder to Wallpaperize");
        try {
            var wpi = new Wallpaperize(tmp.showDialog(null).toPath(), toDefault);
            wpi.execute();
        } catch (Exception e){} // 表示沒做選擇, InvocationTargetException    
    }
    
    @FXML
    void MergeToWallpaper(ActionEvent event) {
        MergeToWallpaper(WallpaperPath.getWallpaperPath(), false);
    }

    @FXML
    void MergeWallpaperFolders(ActionEvent event) {
        try {
            var tmp = new DirectoryChooser();
            tmp.setTitle("Choose your main folder");
            MergeToWallpaper(tmp.showDialog(null).toPath(), false);
        } catch (Exception e){}
    }

    @FXML
    void MergeWallpaperFoldersAsDefault(ActionEvent event) {
        try {
            var tmp = new DirectoryChooser();
            tmp.setTitle("Choose your main folder");
            MergeToWallpaper(tmp.showDialog(null).toPath(), true);
        } catch (Exception e) {}
    }
    
    public void MergeToWallpaper(Path origin, boolean toDefault){
        try {
            var tar = new DirectoryChooser();
            tar.setTitle("Choose merge sources");
            var target = tar.showDialog(null).toPath();
            var wpi = new Wallpaperize(origin, toDefault);
            wpi.mergeWith(target);
        } catch (Exception e){} // 表示沒做選擇, InvocationTargetException        
    }

    @Override
    public void initialize(URL url, ResourceBundle rb){
        new Wallpaperize().execute(); // 每次開啟時初始化 Wallpaper folder
        try {
            theWallpaper = new Wallpaper();
        } catch (IOException e) {
            System.out.println(e.toString());
        }
        okToGo = true;
        staticImagePreview = imagePreview;
        staticPathLabel = pathLabel;
        imagePreview.setImage(theWallpaper.getCurrentPreviewImage());

        Terminal_out.setEditable(false);

        searchBar.setPromptText(">  Search Artwork");
        
        terminalButtonDivider.setMinWidth(150);
        
        downloadAmountChoice.getItems().addAll(modes[0], modes[1], modes[2]);
        downloadAmountChoice.setValue(modes[2]);
        currentPath = WallpaperPath.DEFAULT_DATA_PATH;
        pathLabel.setText(" " + currentPath);
        hasChangedPreview.addListener((a, b, c) -> {
            if (!theWallpaper.isEmpty()){
                imagePreview.setImage(theWallpaper.getCurrentPreviewImage());
            }
            initTreeView();
            hasChangedPreview.set(false);
        });
        searchQueue.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        CrawlerManager.progress.addListener((a, b, c) -> {
            mainPbar.progressProperty().bind(CrawlerManager.progress);
            percentageMark.textProperty().bind(DoubleToStringProperty.toStringProperty(CrawlerManager.progress));
        });
        nowProcessingText.setEditable(false);
        
        initFont();
        initializeKeyBoardShortcuts();
        initializeMouseEvents();
        initSearchQueue();
        initTreeView();
        startTerminal();
    }
    
    public void initFont(){
        searchBar.setFont(MainApp.firaCode12);
        pathLabel.setFont(MainApp.firaCode12);
        Terminal_in.setFont(MainApp.firaCode13);
        Terminal_out.setFont(MainApp.firaCode15);
        percentageMark.setFont(MainApp.firaCode12);
    }
    
    public void initializeMouseEvents(){
        imagePreview.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2){
                try {
                    OpenWallpaper(theWallpaper);
                } catch (IOException e1) {
                    System.out.println(e1.toString());
                }
            }
        });
        imagePreview.setOnScroll((ScrollEvent e) -> {
            var dist = e.getDeltaY();
            if (dist > 0){
                imagePreview.setImage(theWallpaper.getPreviousPreviewImage());
            }
            else if (dist < 0){
                imagePreview.setImage(theWallpaper.getNextPreviewImage());
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
        pathLabel.setOnMouseClicked(e -> {
            if (e.getButton().equals(MouseButton.SECONDARY)){
                var content = new ClipboardContent();
                content.putString(pathLabel.getText().stripLeading());
                Clipboard.getSystemClipboard().setContent(content);
                new Alert(Alert.AlertType.INFORMATION, "Path has copied :)").showAndWait();
            }
        });
        treeFileExplorer.setOnMouseClicked(e -> {
            try {
                treeViewSelected(e);
            } catch (IOException e1) {
                System.out.println(e1);
            }
        });
    }
    
    public void initializeKeyBoardShortcuts(){
        Terminal_in.addEventFilter(KeyEvent.KEY_PRESSED, e -> {
            if (new KeyCodeCombination(KeyCode.ENTER).match(e)){
                if (console != null){
                    console.setPath(currentPath);
                    console.restoreCommandTraverse();
                    try {
                        if (console.readLine(Terminal_in.getText()) == 1){
                            killTerminal(); // exit!
                        }
                    } catch (ClearTerminalException cle){
                        Terminal_out.clear();
                    }
                    currentPath = console.getCurrentPath();
                    initTreeView();
                }
                else {
                    System.out.println(Terminal_in.getText());
                }
                if (!quit){
                    System.err.println(Terminal_in.getText());
                }
                Terminal_in.clear();
                e.consume();
            }
            else if (e.getCode() == KeyCode.UP){
                if (console != null){
                    var tmp = console.getPreviousCommand();
                    if (tmp != null){
                        Terminal_in.setText(tmp);
                    }
                }
            }
            else if (e.getCode() == KeyCode.DOWN){
                if (console != null){
                    var tmp = console.getLaterCommand();
                    if (tmp != null){
                        Terminal_in.setText(tmp);
                    }
                    else {
                        Terminal_in.clear();
                    }
                }
            }
            else if (new KeyCodeCombination(KeyCode.C, KeyCodeCombination.CONTROL_DOWN).match(e)){
                if (console != null){
                    console.cancel();
                }
                e.consume();
            }
            else if (new KeyCodeCombination(KeyCode.K, KeyCodeCombination.CONTROL_DOWN).match(e)){
                System.out.println("GUI Terminal Quit : Kill Terminal");
                killTerminal();
                Terminal_in.clear();
                e.consume();
            }
            else if (new KeyCodeCombination(KeyCode.L, KeyCodeCombination.CONTROL_DOWN).match(e)){
                Terminal_out.setText("");
                e.consume();
            }
        });
        Terminal_out.addEventFilter(KeyEvent.KEY_PRESSED, e -> {
            if (new KeyCodeCombination(KeyCode.K, KeyCodeCombination.CONTROL_DOWN).match(e)){
                System.out.println("GUI Terminal Quit : KeyBoard Interrupt");
                killTerminal();
                Terminal_in.clear();
                e.consume();
            }
            else if (new KeyCodeCombination(KeyCode.C, KeyCodeCombination.CONTROL_DOWN).match(e)){
                if (console != null){
                    console.cancel();
                }
                e.consume();
            }
            else if (new KeyCodeCombination(KeyCode.L, KeyCodeCombination.CONTROL_DOWN).match(e)){
                Terminal_out.setText("");
                e.consume();
            }
        });
        searchBar.addEventFilter(KeyEvent.KEY_PRESSED, e -> {
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
        searchQueue.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.DELETE){
                var allData = searchQueue.getItems();
                var selectedData = searchQueue.getSelectionModel().getSelectedItems();
                for (int i = selectedData.size() - 1; i >= 0; --i){
                    allData.remove(selectedData.get(i));
                }
                e.consume();
            }
        });
        refreshExplorer.setOnMouseClicked(e -> initTreeView());
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
        for (var i : searchQueue.getItems()){
            if (i.key.equals(WallpaperUtil.capitalize(keyword))){
                new Alert(Alert.AlertType.INFORMATION, "We've already have that :)").showAndWait();
                return;
            }
        }
        Service<Boolean> check = new Service<Boolean>(){
            @Override
            protected Task<Boolean> createTask(){
                return new Task<Boolean>(){
                    @Override 
                    protected Boolean call(){
                        return CrawlerManager.checkValidation(WallpaperUtil.capitalize(keyword));
                    }
                };
            }
        };
        check.setOnSucceeded(new EventHandler<WorkerStateEvent>(){
            @Override
            public void handle(WorkerStateEvent e){
                if (check.getValue()){
                    var tmpData = new myPair<String, String>(
                        WallpaperUtil.capitalize(keyword), downloadAmountChoice.getValue()
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
    
    private void initTreeView(){
        var rootPath = currentPath;
        pathLabel.setText(" " + currentPath);
        var rootName = (rootPath.getNameCount() == 0) ? rootPath.toString() : rootPath.getFileName().toString();
        var root = new TreeItem<>(new myPair<>(rootName, rootPath), WallpaperUtil.fetchIconUsePath(rootPath));
        if (doBFS != null && doBFS.isRunning()){
            doBFS.cancel();
        }
        doBFS = new Service<Void>(){
            @Override
            protected Task<Void> createTask(){
                return new Task<Void>(){
                    @Override
                    protected Void call(){
                        var str = (rootPath.toAbsolutePath().toString().length() < 30) ? rootPath.toString() : "~~/" + rootPath.getFileName().toString();
                        percentageMark.setText("loading directory : " + str);
                        initTreeDir(root);
                        return null;
                    }
                };
            }
        };
        doBFS.setOnSucceeded(e -> {
            root.setExpanded(true);
            treeFileExplorer.setRoot(root);
            var str = (rootPath.toAbsolutePath().toString().length() < 45) ? rootPath.toString() : "*/" + rootPath.getFileName().toString();
            percentageMark.setText("Finish loading " + str);
        });
        doBFS.restart();
    }

    private void initTreeDir(TreeItem<myPair<String, Path>> root){
        try {
            bfsSurface(root);
            for (var cur : root.getChildren()){
                touchSurface(cur);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /* myPair 真的萬用 */
    private TreeItem<myPair<String, Path>> bfsSurface(TreeItem<myPair<String, Path>> cur) throws IOException{
        if (cur != null && Files.isDirectory(cur.getValue().value)){
            try (var dirStream = Files.newDirectoryStream(cur.getValue().value)){
                cur.getChildren().clear(); // 先淨空當前內容
                dirStream.forEach(p -> cur.getChildren().add(new TreeItem<>(new myPair<>(p.getFileName().toString(), p), WallpaperUtil.fetchIconUsePath(p))));
                cur.getChildren().sort((a, b) -> WallpaperUtil.pathDirAndNameCompare(a.getValue().value, b.getValue().value));
            } catch (java.nio.file.AccessDeniedException ae){
                return cur;
            }
        }
        return cur;
    }

    /* 優化二層遍歷效率, 避免低效二層遍歷 */
    private TreeItem<myPair<String, Path>> touchSurface(TreeItem<myPair<String, Path>> cur) throws IOException{
        if (cur != null && Files.isDirectory(cur.getValue().value)){
            try (var dirStream = Files.newDirectoryStream(cur.getValue().value)){
                cur.getChildren().clear(); // 先淨空當前內容
                for (var d : dirStream){
                    if (d != null){
                        cur.getChildren().add(new TreeItem<>(new myPair<>(d.getFileName().toString(), d), WallpaperUtil.fetchIconUsePath(d)));
                        break; // 只取一個
                    }
                }
            } catch (java.nio.file.AccessDeniedException ae){
                return cur;
            }
        }
        return cur;
    }
    
    private void treeViewSelected(MouseEvent me) throws IOException{
        Path path;
        TreeItem<myPair<String, Path>> dir;
        try {
            dir = treeFileExplorer.getSelectionModel().getSelectedItem();
            path = dir.getValue().value.toAbsolutePath();
        } catch (java.lang.NullPointerException ne){
            System.out.println("No selected item.");
            return;
        }
        System.out.println(path);
        if (Files.isDirectory(path.toRealPath().toAbsolutePath())){
            if (me.getClickCount() == 1){
                var tmp = treeFileExplorer.getSelectionModel().getSelectedItem();
                if (tmp != null){
                    initTreeDir(tmp);
                    tmp.setExpanded(true);
                }
            }

            viewImageTileTable.getChildren().clear();
            viewImageTileTable.setPadding(new Insets(5., 5., 5., 8.));
            viewImageTileTable.setVgap(8.);
            viewImageTileTable.setHgap(8.);

            var paths = new TreeSet<Path>(WallpaperUtil::pathDirAndNameCompare);
            try (var dirStream = Files.newDirectoryStream(path)){
                dirStream.forEach(e -> paths.add(e));
            }

            var list = new ArrayList<VBox>(paths.size());
            paths.forEach(p -> {
                var iconView = WallpaperUtil.fetchIconUsePath(p);
                iconView.setFitHeight(58);
                iconView.setFitWidth(58);
                
                var name = new Text(p.getFileName().toString());
                name.setStyle("-fx-font: 11 system;");
                
                var vbox = new VBox(iconView, name);
                vbox.setAlignment(Pos.CENTER);
                vbox.setOnMouseClicked(e -> {
                    if (e.getClickCount() == 2){
                        if (Files.isDirectory(p)){
                            currentPath = p;
                            pathLabel.setText(" " + currentPath);
                            initTreeView();
                        }
                        else if (Dumper.isImage(p)){
                            try {
                                OpenWallpaper(new Wallpaper(p.getParent(), p));
                            } catch (IOException e1) { System.out.println("Failed to open wallpaper"); }
                        }
                        else if (Dumper.isMusic(p)){
                            MusicWithAkari.openMusicWithAkari(p);
                        }
                        else {
                            openFile(p);
                        }
                    }
                });
                list.add(vbox);
            });
            viewImageTileTable.getChildren().addAll(list);
            scrollableTile.setContent(viewImageTileTable);
        }
        else { // 真正的 FileExplorer 因此完善 OwO
            if (Dumper.isImage(path)){
                if (me.getClickCount() == 2){
                    OpenWallpaper(new Wallpaper(path.getParent(), path));
                }
                var iv = new ImageView(new Image(path.toFile().toURI().toString()));
                iv.setOnMouseClicked(e -> {
                    if (e.getClickCount() == 2){
                        try {
                            OpenWallpaper(new Wallpaper(path.getParent(), path));
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                    }
                });
                scrollableTile.setContent(iv);
            }
            else if (Dumper.isMusic(path)) {
                if (me.getClickCount() == 2){
                    MusicWithAkari.openMusicWithAkari(path);
                }
            }
            else {
                if (me.getClickCount() == 2){
                    openFile(path);
                }
            }
        }
        scrollableTile.setPannable(true);
        scrollableTile.setFitToHeight(true);
        scrollableTile.setVbarPolicy(ScrollBarPolicy.ALWAYS);
    }

    public void openFile(Path path){
        try {
            Desktop.getDesktop().open(path.toFile());
        } catch (IOException ie){
            var openFileThread = new Thread(() -> cmdOpenPath(path));
            openFileThread.setDaemon(true);
            openFileThread.start();
        }
    } 

    public static void cmdOpenPath(Path path){
        try {
            var process = Runtime.getRuntime().exec("cmd /C " + "\"" + path.toAbsolutePath() + "\"");
            var reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            var output = new StringBuilder();
            String tmpStr;
            while ((tmpStr = reader.readLine()) != null){
                output.append(tmpStr);
            }
            int exitVal = process.waitFor();
            if (exitVal == 0){
                System.out.println("execute successfully");
                System.out.println(output);
                if (!quit){
                    System.err.println(output);
                }
            }
            else {
                System.out.println("execute failed with exit code : " + exitVal);
            }
        } catch (Exception ex){
            ex.printStackTrace();
        }
    }
}
