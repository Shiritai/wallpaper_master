/*
* Author : Shiritai (楊子慶, or Eroiko on Github) at 2021/06/13.
* See https://github.com/Shiritai/wallpaper_master for more information.
* Created using VSCode.
*/
package eroiko.ani.controller;

import java.awt.Desktop;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.TreeSet;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;

import eroiko.ani.MainApp;
import eroiko.ani.controller.PrimaryControllers.*;
import eroiko.ani.controller.PromptControllers.ConsoleController;
import eroiko.ani.controller.PromptControllers.TerminalThread;
import eroiko.ani.controller.SupportController.MyAlert;
import eroiko.ani.controller.SupportController.MyAlert.AlertType;
import eroiko.ani.model.CLI.Console;
import eroiko.ani.model.CLI.exception.ClearConsoleException;
import eroiko.ani.model.CLI.exception.ExitConsoleException;
import eroiko.ani.model.CLI.exception.ShutdownSoftwareException;
import eroiko.ani.model.NewCrawler.CrawlerManager;
import eroiko.ani.util.Method.CarryReturn;
import eroiko.ani.util.Method.DoubleToStringProperty;
import eroiko.ani.util.Method.Dumper;
import eroiko.ani.util.Method.TimeWait;
import eroiko.ani.util.MyDS.DoubleHistoryList;
import eroiko.ani.util.MyDS.myPair;
import eroiko.ani.util.NeoWallpaper.*;
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
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

/** 主視窗 Controller */
public class MainController implements Initializable {
    /* Support variables */
    public static ImageView staticImagePreview;
    public static BooleanProperty hasChangedPreview = new SimpleBooleanProperty(false);
    public static Label staticPathLabel;
    public static Wallpaper theWallpaper;

    /* Terminal */
    public PipedInputStream pipIn = new PipedInputStream();
    private Thread terminalThread;
    public static boolean quit = true;
    /* Progress bar and indicator */
    public static ProgressBar MainCtrlPbar = new ProgressBar();
    public static ProgressIndicator MainCtrlPin = new ProgressIndicator();
    /* FXML variables */
    @FXML private VBox rootPane;
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
    @FXML private Label previousExplorer;
    @FXML private Label nextExplorer;
    private DoubleHistoryList<Path> explorerRec;
    private Path lastTilePath = null; // 之後給 Tile Pane 使用
    
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
        MainApp.closeMainStage();
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
            MyAlert.OpenMyAlert(
                AlertType.INFORMATION,
                "Still downloading...",
                "We are downloading for you...\nPlease wait a minute :)"
            );
        }
        else {
            MyAlert.OpenMyAlert(
                AlertType.INFORMATION,
                "Empty Queue",
                "Please add some keywords :)"
            );
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
            else if ((c.equals("Preparing view window...") || c.equals("Preparing view window...")) && it.hasNext()){
                new TimeWait(2000);
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
                mainPbar.progressProperty().unbind();
            }
        });
        crawlerThread.setOnFailed(e -> MyAlert.OpenMyAlert(AlertType.ERROR, crawlerThread.getException(), true));
        crawlerThread.restart();
    }

    @FXML
    void OpenRealTerminal(ActionEvent event) {
        ConsoleController.OpenCompleteTerminal();
    }

    @FXML
    void OpenPreferenceWindow(ActionEvent event) {
        OpenPreferenceWindow();
    }
    
    public static void OpenPreferenceWindow(){
        PreferenceController.quit = quit;
        try {
            var stage = new Stage();
            stage.setTitle("Preference");
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
                WallpaperController.OpenWallpaper(new Wallpaper(file), false);
            }
            else { // directory
                WallpaperController.OpenWallpaper(new Wallpaper(file), false);
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
                refreshExplorerPath(file.getParent(), true, true);
                theWallpaper = new Wallpaper(file);
                hasChangedPreview.set(true);
                imagePreview.setImage(theWallpaper.getCurrentPreviewImage());
            }
            else {
                refreshExplorerPath(file, true, true);
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
        System.out.println("Created by Eroiko, " + MainApp.version + " at " + MainApp.date +
        "\nUse Ctrl + C to cancel executing command, and Ctrl + L to clear the text." +
        "\n\nSupport several linux-based commands." +
        "\nCheck commands with \"man COMMAND_NAME\" or \"COMMAND_NAME --help\"" +
        "\nList all available commands with \"man -a\" or \"man --all\"" + 
        "\nTry key in \"meow\" and see what will happen OwO\n");
        console = new Console(currentPath, WallpaperUtil::pathDirAndNameCompare, MainApp.hostName, MainApp.userName, true);
        System.out.println(console.toString());
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
            System.setOut(MainApp.stdOut);
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
    
    /** 開啟 FileChooser 來選擇檔案, 並呼叫 Music with Akari 的靜態開啟方法 */
    public static void OpenMusicWithAkari(){
        var tmp = new javafx.stage.FileChooser();
        tmp.setTitle("Open music with Akari");
        tmp.setInitialDirectory(WallpaperPath.DEFAULT_DATA_PATH.toFile());
        try {
            tmp.getExtensionFilters().addAll(new javafx.stage.FileChooser.ExtensionFilter("Audio Files", "*.wav", "*.mp3"));
            MusicWithAkari.openMusicWithAkari(tmp.showOpenDialog(null).toPath());
        } catch (Exception e){} // 表示沒做選擇, InvocationTargetException
    }
    
    @FXML
    void SwitchBackToImgPath(ActionEvent event) {
        try {
            theWallpaper = new Wallpaper();
            refreshExplorerPath(WallpaperPath.DEFAULT_DATA_PATH, true, true);
            imagePreview.setImage(theWallpaper.getCurrentPreviewImage());
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

        Terminal_in.setContextMenu(new ContextMenu());
        Terminal_in.setStyle("-fx-background-color: #333333;-fx-text-fill: #eeeeee;");
        Terminal_out.setEditable(false);
        Terminal_out.setContextMenu(new ContextMenu());
        try {
            Terminal_out.getStylesheets().addAll(WallpaperPath.FXML_SOURCE_PATH.resolve("style/transparentTextArea.css").toAbsolutePath().toUri().toURL().toString());
            scrollableTile.getStylesheets().addAll(WallpaperPath.FXML_SOURCE_PATH.resolve("style/transparentScrollPane.css").toAbsolutePath().toUri().toURL().toString());
        } catch (MalformedURLException e) {}
        Terminal_out.setStyle("-fx-background-color: #333333;");

        searchBar.setPromptText(">  Search Artwork");
        
        terminalButtonDivider.setMinWidth(150);
        
        downloadAmountChoice.getItems().addAll(modes[0], modes[1], modes[2]);
        downloadAmountChoice.setValue(modes[2]);
        
        hasChangedPreview.addListener((a, b, c) -> {
            if (!theWallpaper.isEmpty()){
                imagePreview.setImage(theWallpaper.getCurrentPreviewImage());
            }
            initTreeView(true);
            hasChangedPreview.set(false);
        });
        searchQueue.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        CrawlerManager.progress.addListener((a, b, c) -> {
            mainPbar.progressProperty().bind(CrawlerManager.progress);
            percentageMark.textProperty().bind(DoubleToStringProperty.toStringProperty(CrawlerManager.progress));
        });
        nowProcessingText.setEditable(false);

        viewImageTileTable.setPadding(new Insets(5., 15., 5., 8.));
        viewImageTileTable.setVgap(8.);
        viewImageTileTable.setHgap(8.);
        
        initFont();
        initializeKeyBoardShortcuts();
        initializeMouseEvents();
        initSearchQueue();
        refreshExplorerPath(WallpaperPath.DEFAULT_DATA_PATH, true, false);
        startTerminal();
    }
    
    public void initFont(){
        searchBar.setFont(MainApp.firaCode12);
        pathLabel.setFont(MainApp.firaCode12);
        Terminal_in.setFont(MainApp.firaCode13);
        Terminal_out.setFont(MainApp.firaCode15);
        percentageMark.setFont(MainApp.firaCode12);
        nextExplorer.setFont(MainApp.firaCode20);
        previousExplorer.setFont(MainApp.firaCode20);
    }
    
    public void initializeMouseEvents(){
        imagePreview.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2){
                try {
                    WallpaperController.OpenWallpaper(theWallpaper, true);
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
                    MyAlert.OpenMyAlert(
                        AlertType.INFORMATION,
                        "Invalid keywords!",
                        "Keywords : " + tmp + "\nKeyword is too short!\nPlease check again :)"
                    );
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
                MyAlert.OpenMyAlert(AlertType.INFORMATION, "Path copied", ":)");
                new Alert(Alert.AlertType.INFORMATION, "Path has copied :)").showAndWait();
            }
        });
        Terminal_in.setOnMouseClicked(e -> {
            if (e.getButton().equals(MouseButton.SECONDARY)){
                if (Terminal_in.getSelectedText().equals("")){
                    var cb = Clipboard.getSystemClipboard().getString();
                    Terminal_in.appendText(cb);
                }
                else {
                    var content = new ClipboardContent();
                    content.putString(Terminal_in.getSelectedText());
                    Clipboard.getSystemClipboard().setContent(content);
                }
            }
        });
        Terminal_out.setOnMouseClicked(e -> {
            if (e.getButton().equals(MouseButton.SECONDARY)){
                var content = new ClipboardContent();
                content.putString(Terminal_out.getSelectedText());
                Clipboard.getSystemClipboard().setContent(content);
                int pos = Terminal_out.getSelection().getStart();
                Terminal_out.selectRange(pos, pos); // clear selected text
            }
        });
        treeFileExplorer.setOnMouseClicked(e -> {
            try {
                treeViewSelected(e);
            } catch (IOException e1) {
                System.out.println(e1);
            }
        });
        nextExplorer.setOnMouseEntered(e -> nextExplorer.setTextFill(Color.rgb(12, 81, 249)));
        nextExplorer.setOnMouseClicked(e -> {
            if (explorerRec.hasNext()){
                refreshPathWithoutAdding(explorerRec.getNext(), true);
            }
        });
        nextExplorer.setOnMouseExited(e -> nextExplorer.setTextFill(Color.rgb(0, 0, 0)));
        previousExplorer.setOnMouseEntered(e -> previousExplorer.setTextFill(Color.rgb(12, 81, 249)));
        previousExplorer.setOnMouseExited(e -> previousExplorer.setTextFill(Color.rgb(0, 0, 0)));
        previousExplorer.setOnMouseClicked(e -> {
            if (explorerRec.hasPrevious()){
                refreshPathWithoutAdding(explorerRec.getPrevious(), true);
            }
        });
        rootPane.addEventFilter(MouseEvent.MOUSE_CLICKED, e -> {
            if (e.getButton() == MouseButton.FORWARD){
                if (explorerRec.hasNext()){
                    refreshPathWithoutAdding(explorerRec.getNext(), true);
                }
                e.consume();
            }
            else if (e.getButton() == MouseButton.BACK){
                if (explorerRec.hasPrevious()){
                    refreshPathWithoutAdding(explorerRec.getPrevious(), true);
                }
                e.consume();
            }
        });
    }
    
    public void initializeKeyBoardShortcuts(){
        rootPane.addEventFilter(KeyEvent.KEY_PRESSED, e -> { //  彈出 Preference 視窗
            if (new KeyCodeCombination(KeyCode.P, KeyCombination.CONTROL_DOWN).match(e)){
                MainController.OpenPreferenceWindow();
                e.consume();
            }
            if (e.getCode() == KeyCode.F5){
                refreshPathWithoutAdding(currentPath, true);
                e.consume();
            }
        });
        Terminal_in.addEventFilter(KeyEvent.KEY_PRESSED, e -> {
            if (new KeyCodeCombination(KeyCode.ENTER).match(e)){
                if (console != null){
                    console.setPath(currentPath);
                    console.restoreCommandTraverse();
                    try {
                        console.readLine(Terminal_in.getText());
                    } catch (ClearConsoleException cle){
                        Terminal_out.clear();
                    } catch (ExitConsoleException exit){
                        killTerminal(); // exit!
                    } catch (ShutdownSoftwareException shutdown){
                        Exit();
                    } catch (Exception ex){
                        ex.printStackTrace();
                    }
                    if (console != null && !console.isSearchCmd()){ // 可能已經 exit 或 shutdown, is search command 讓查詢指令時不須用重新整理資料夾
                        refreshExplorerPath(console.getCurrentPath(), true, true);
                    }
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
                        Terminal_in.end();
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
                    Terminal_in.end();
                }
            }
            else if (new KeyCodeCombination(KeyCode.C, KeyCodeCombination.CONTROL_DOWN).match(e)){
                if (console != null){
                    console.cancel();
                }
                e.consume();
            }
            else if (new KeyCodeCombination(KeyCode.K, KeyCodeCombination.CONTROL_DOWN).match(e)){
                System.out.println("[GUI Terminal]  Quit : Kill Terminal");
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
                System.out.println("[GUI Terminal]  Quit : KeyBoard Interrupt");
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
                        MyAlert.OpenMyAlert(
                            AlertType.INFORMATION,
                            "Invalid keywords!",
                            "Keywords : " + tmp + "\nKeyword is too long.\nPlease check again :)"
                        );
                    }
                    else if (tmp.length() <= 3 && tmp.charAt(0) != '\n'){
                        MyAlert.OpenMyAlert(
                            AlertType.INFORMATION,
                            "Invalid keywords!",
                            "Keywords : " + tmp + "\nKeyword is too short.\nPlease check again :)"
                        );
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
        refreshExplorer.setOnMouseClicked(e -> refreshPathWithoutAdding(currentPath, true));
    }

    private void initSearchQueue(){
        /* Set the two columns */
        keywords.setMinWidth(170);
        keywords.setPrefWidth(170);
        keywords.setCellValueFactory(new PropertyValueFactory<>("key"));
        amount.setMinWidth(150);
        amount.setPrefWidth(150);
        amount.setCellValueFactory(new PropertyValueFactory<>("value"));
    }

    /** 新增至佇列, 並確認是否合法 */
    private void addSearchQueue(){
        String keyword = searchBar.getText();
        searchBar.clear();
        for (var i : searchQueue.getItems()){
            if (i.key.equals(WallpaperUtil.capitalize(keyword))){
                MyAlert.OpenMyAlert(
                    AlertType.INFORMATION,
                    "Invalid keywords!",
                    "Keywords : " + keyword + "\nWe've already have that :)"
                );
                return;
            }
        }
        Service<Boolean> check = new Service<Boolean>(){
            @Override
            protected Task<Boolean> createTask(){
                return new Task<Boolean>(){
                    @Override 
                    protected Boolean call() throws Exception{
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
                    System.out.println("[Crawler Queue]  Add " + tmpData.key + " : " + tmpData.value + " to Search Queue");
                    searchQueue.getItems().add(tmpData);
                    searchBar.setStyle("-fx-background-color: #ffffff;");
                }
                else {
                    searchBar.setText(keyword);
                    searchBar.setStyle("-fx-background-color: #efb261;");
                    searchBar.selectAll();
                    MyAlert.OpenMyAlert(
                        AlertType.INFORMATION,
                        "Invalid keywords!",
                        "Keywords : " + keyword + "\nPlease check again :)"
                    );
                }
            }
        });
        check.setOnFailed(e -> MyAlert.OpenMyAlert(AlertType.EXCEPTION, check.getException(), true));
        check.restart();
    }

    private void deleteSelectedSearchQueue(){
        var allData = searchQueue.getItems();
        var selectedData = searchQueue.getSelectionModel().getSelectedItems();
        for (int i = selectedData.size() - 1; i >= 0; --i){
            allData.remove(selectedData.get(i));
        }
    }
    
    private void initTreeView(boolean isExpanded){
        var rootPath = currentPath;
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
            root.setExpanded(isExpanded);
            treeFileExplorer.setRoot(root);
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

    /* 優化二層遍歷效率, 避免低效第二層遍歷 */
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
        var clicked = me.getPickResult().getIntersectedNode();
        if (clicked.toString().contains("null")){
            treeFileExplorer.getSelectionModel().clearSelection();
        }
        else if (clicked.getStyleClass().toString().equals("arrow") || clicked.getStyleClass().toString().equals("tree-disclosure-node")){
            var tmp = clicked.getParent();
            while (!tmp.toString().contains("TreeViewSkin")){
                tmp = tmp.getParent();
            }
            treeFileExplorer.getRoot().getChildren().forEach(p -> {
                if (p.isExpanded() && p.getChildren().size() <= 1){ // 不起眼但很有效的優化, 正確判斷需要被 BFS 的子項目, 而非一股腦對所有展開的子項目進行 BFS
                    try {
                        bfsSurface(p);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
        Path path;
        TreeItem<myPair<String, Path>> dir;
        try {
            dir = treeFileExplorer.getSelectionModel().getSelectedItem();
            path = dir.getValue().value.toAbsolutePath();
        } catch (java.lang.NullPointerException ne){ return; }
        System.out.println("[File Explorer (Path)]  " + path);
        if (Files.isDirectory(path)){
            initTreeDir(dir);
            initTileExplorer(path);
        }
        else { // 真正的 FileExplorer 因此完善 OwO
            if (Dumper.isImage(path)){
                if (me.getClickCount() == 2){
                    WallpaperController.OpenWallpaper(new Wallpaper(path), false);
                }
                var iv = new ImageView(new Image(path.toFile().toURI().toString()));
                iv.setOnMouseClicked(e -> {
                    if (e.getClickCount() == 2){
                        try {
                            WallpaperController.OpenWallpaper(new Wallpaper(path), false);
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

    /**
     * 以 {@code root} 展開, 對所有圖片並行讀取, 以預覽圖的方式呈現, 好快啊 XD
     * @param root : 欲展開之根目錄
     * @throws IOException
     */
    private void initTileExplorer(Path root) throws IOException{

        lastTilePath = root;
        var paths = new TreeSet<Path>(WallpaperUtil::pathDirAndNameCompare);
        try (var dirStream = Files.newDirectoryStream(root)){
            dirStream.forEach(e -> paths.add(e));
        }
        viewImageTileTable.getChildren().clear();

        final int size = paths.size();
        var list = new ArrayList<VBox>(size);
        /* 優化 File Explorer 圖示載入的策略 */
        int cnt = 0;
        for (var p : paths){ if (Dumper.isImage(p)){ ++cnt; }}
        final int imgCnt = cnt;
        final boolean fastImageLoadLimit = imgCnt <= 32; // 小於此數者, 直接取得高品質小圖示
        final boolean scanImageLoadLimit = imgCnt > 128; // 超過此數者, 先掃描系統小圖示, 之後逐一載入高品質小圖示
        /* 開啟 Service */
        var service = new Service<Void>(){
            @Override
            protected Task<Void> createTask(){
                return new Task<Void>(){
                    @Override
                    protected Void call(){
                        var pool = Executors.newFixedThreadPool(size);
                        var poolList = new ArrayList<Callable<VBox>>(size);
                        /* 建立圖示 */
                        long i = 0;
                        for (var p : paths){
                            poolList.add(() -> {
                                ImageView iconView;
                                if (Dumper.isImage(p)){
                                    if (fastImageLoadLimit){
                                        iconView = WallpaperUtil.fetchScaledSmallImageView(p);
                                    }
                                    else if (scanImageLoadLimit){ // 避免爆記憶體
                                        iconView = WallpaperUtil.fetchIconUsePathWithHightQuality(p);
                                    }
                                    else { // 適合一般數量的圖片資料夾
                                        iconView = WallpaperUtil.fetchSmallImageView(p);
                                    }
                                }
                                else {
                                    iconView = WallpaperUtil.fetchIconUsePathWithHightQuality(p);
                                }
                                
                                var name = new Text(CarryReturn.addCarryReturnForTile(p.getFileName().toString(), 19));
                                name.setFont(MainApp.notoSansCJKLight12);
                                
                                var vbox = new VBox(iconView, name);
                                vbox.setAlignment(Pos.TOP_CENTER);
                                vbox.setOnMouseClicked(e -> {
                                    if (e.getButton() == MouseButton.PRIMARY){
                                        initTreeView(false);
                                        if (e.getClickCount() == 2){
                                            if (Files.isDirectory(p)){
                                                refreshExplorerPath(p, false, true);
                                                try {
                                                    initTileExplorer(p);
                                                } catch (IOException ie){ ie.printStackTrace(); }
                                            }
                                            else if (Dumper.isImage(p)){
                                                try {
                                                    WallpaperController.OpenWallpaper(new Wallpaper(p), false);
                                                } catch (IOException e1) { System.out.println("Failed to open wallpaper"); }
                                            }
                                            else if (Dumper.isMusic(p)){
                                                MusicWithAkari.openMusicWithAkari(p);
                                            }
                                            else {
                                                openFile(p);
                                            }
                                        }
                                    }
                                    else {
                                        
                                    }
                                });
                                return vbox;
                            });
                            ++i;
                            updateProgress(i, size);
                        }
                        try {
                            var tmp = pool.invokeAll(poolList);
                            for (var t : tmp){
                                list.add(t.get());
                            }
                        } catch (Exception e){ e.printStackTrace(); }
                        return null;
                    }
                };
            }
        };
        mainPbar.progressProperty().bind(service.progressProperty());
        service.setOnSucceeded(e -> {
            list.sort((a, b) -> WallpaperUtil.pathNameCompare(((Text) a.getChildren().get(1)).getText(), ((Text) a.getChildren().get(1)).getText()));
            viewImageTileTable.getChildren().addAll(list);
            viewImageTileTable.prefHeightProperty().bind(scrollableTile.heightProperty()); // 自由改變高
            viewImageTileTable.prefWidthProperty().bind(scrollableTile.widthProperty()); // 自由改變寬
            scrollableTile.setContent(viewImageTileTable);
            /* 慢慢載入高品質圖片小圖示 */
            var str = (lastTilePath.toAbsolutePath().toString().length() < 45) ? lastTilePath.toString() : ".../" + lastTilePath.getFileName().toString();
            if (!fastImageLoadLimit){
                Service<Void> imageRefresh = new Service<Void>(){
                    @Override
                    protected Task<Void> createTask(){
                        return new Task<Void>(){
                            @Override 
                            protected Void call() throws Exception{
                                var pIt = paths.iterator();
                                Path tmpP;
                                for (int i = 0; i < size; ++i){
                                    if (root.equals(lastTilePath)){ // Tile 目錄不變的情況下
                                        assert pIt.hasNext() : "Bad iterator";
                                        if (Dumper.isPngJpg(tmpP = pIt.next())){
                                            ((ImageView) list.get(i).getChildren().get(0)).setImage(WallpaperUtil.fetchScaledSmallImage(tmpP, WallpaperUtil.SMALL_IMG_SIZE));
                                        }
                                        else if (Dumper.isImage(tmpP)){
                                            ((ImageView) list.get(i).getChildren().get(0)).setImage(WallpaperUtil.fetchSmallImage(tmpP, WallpaperUtil.SMALL_IMG_SIZE));
                                        }
                                    }
                                    else { break; }
                                    updateProgress(i + 1, size);
                                    if (i + 1 == size){
                                        percentageMark.setText("Finish loading " + str);
                                    }
                                }
                                return null;
                            }
                        };
                    }
                };
                mainPbar.progressProperty().unbind();
                mainPbar.progressProperty().bind(imageRefresh.progressProperty());
                percentageMark.setText("Loading " + str + " high quality images...");
                imageRefresh.setOnSucceeded(ev -> {
                    mainPbar.progressProperty().unbind();
                });
                imageRefresh.restart();
            }
            else {
                percentageMark.setText("Finish loading " + str);
            }
        });
        service.restart();
    }

    public void openFile(Path path){
        try {
            Desktop.getDesktop().open(path.toFile());
        } catch (IOException ie){
            var openFileThread = new Thread(() -> Dumper.cmdOpenPath(path));
            openFileThread.setDaemon(true);
            openFileThread.start();
        }
    }

    private void refreshExplorerPath(Path path, boolean isExpanded, boolean trimOrRestore){
        currentPath = path;
        if (explorerRec == null){
            explorerRec = new DoubleHistoryList<>();
        }
        explorerRec.add(path, trimOrRestore);
        pathLabel.setText(" " + currentPath.toAbsolutePath());
        initTreeView(isExpanded);
        try {
            initTileExplorer(currentPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private void refreshPathWithoutAdding(Path path, boolean isExpanded){
        currentPath = path;
        pathLabel.setText(" " + currentPath.toAbsolutePath());
        initTreeView(isExpanded);
        try {
            initTileExplorer(currentPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /** 完全刪除檔案, 含子資料夾 
     * @throws IOException */
    private void deleteFile(Path path) throws IOException{
        if (path != null){
            if (Files.isDirectory(path)){
                try (var dirStream = Files.newDirectoryStream(path)){
                    dirStream.forEach(t -> {
                        try {
                            deleteFile(t);
                        } catch (IOException e) {}
                    });
                }
            }
            else {
                path.toFile().delete();
            }
        }
    }
}
