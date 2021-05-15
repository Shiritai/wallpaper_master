package eroiko.ani.controller;

import java.io.*;
import java.net.URL;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.ResourceBundle;

import eroiko.ani.MainApp;
import eroiko.ani.controller.ConsoleTextArea.TerminalThread;
import eroiko.ani.controller.PrimaryControllers.*;
import eroiko.ani.model.NewCrawler.CrawlerManager;
import eroiko.ani.util.*;
import eroiko.ani.util.NeoWallpaper.*;
import eroiko.ani.util.WallpaperClass.*;
import eroiko.ani.util.myDS.TimeWait;
import eroiko.ani.util.myDS.myPair;
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
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

public class MainController implements Initializable {
    /* Support variables */
    // public static WallpaperImage preview;
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

    @FXML private ImageView imagePreview;
    
    /* File explorer */
    @FXML private TreeView<myPair<String, Path>> treeFileExplorer;
    @FXML private TilePane viewImageTileTable;
    @FXML private ScrollPane scrollableTile;
    @FXML private Label pathLabel;
    @FXML private BorderPane openWindowsFileExplorer = new BorderPane();
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
    @FXML private ProgressBar mainPbar;
    @FXML private TextField nowProcessingText;
    @FXML private Label progressBarText;
    @FXML private Text percentageMark;

    /* Media */
    @FXML private Rectangle lastMusicButton;
    @FXML private Rectangle playMusicButton;
    @FXML private Rectangle nextMusicButton;


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
        new TimeWait(2000);
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
        MusicBox.musicBox.playProcessing();
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
                            var cw = new CrawlerManager(WallpaperPath.defaultDataPath.toString(), WallpaperUtil.capitalize(data.get(i).key).split(" "), mode);
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
                    OpenWallpaper(false);
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
                MusicBox.musicBox.playComplete();
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
    
    public void OpenAboutWindow(){
        try {
            var stage = new Stage();
            stage.setTitle("About");
            stage.setScene(new Scene(FXMLLoader.load(getClass().getClassLoader().getResource("eroiko/ani/view/AboutWindow.fxml"))));
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
            if (WallpaperUtil.isImage(file)){
                theWallpaper = new Wallpaper(file.getParent(), file);
                hasChangedPreview.set(true);
                imagePreview.setImage(theWallpaper.getCurrentPreviewImage());
            }
            else {
                theWallpaper = new Wallpaper(file);
                hasChangedPreview.set(true);
                imagePreview.setImage(theWallpaper.getCurrentPreviewImage());
            }
            event.consume();
        } catch (IOException e) {
            return;
        }
    }
    
    @FXML
    void PreviewImageDragOut(MouseEvent event) {
        Dragboard db = imagePreview.startDragAndDrop(TransferMode.ANY);
        var cb  = new ClipboardContent();
        // cb.putImage(preview.getCurrentWallpaper());
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
            if (WallpaperUtil.isImage(file)){ // image
                OpenWallpaper(new Wallpaper(file.getParent(), file), true);
                // OpenWallpaperViewWindow(new WallpaperImage(file.getParent().toAbsolutePath().toString(), false, file));
            }
            else { // directory
                OpenWallpaper(new Wallpaper(file), true);
            }
            // OpenWallpaperViewWindow(new WallpaperImage(file.toAbsolutePath().toString(), false));
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
    void OpenMusicController(ActionEvent event) {
        // OpenMusicWindow();
        MusicWithSyamiko.openMusicWithSyamiko();;
    }
    
    /** Deprecated */
    public void OpenMusicWindow(){
        // tableOfBrowser.getSelectionModel().select(3); // 3 is the index of the tab
        if (!MusicController.isActivating.get()){
            try {
                var stage = new Stage();
                stage.setTitle("Music with Syamiko");
                stage.setScene(new Scene(FXMLLoader.load(getClass().getClassLoader().getResource("eroiko/ani/view/MusicWindow.fxml"))));
                stage.getIcons().add(MainApp.icon);
                stage.setResizable(false);
                stage.setOnCloseRequest(e -> {
                    MusicController.isActivating.set(false);
                });
                stage.show();
            } catch (Exception e){
                e.printStackTrace();
                System.out.println(e.toString());
                if (!quit){
                    System.err.println(e.toString());
                }
            }
        }
        else {
            new Alert(Alert.AlertType.INFORMATION, "You've already open Music with Syamiko :)").showAndWait();
        }
    }

    /** Deprecated */
    void OpenWallpaperViewWindow(WallpaperProto wp, int width, int height) {
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
                var wallpaperScene = new Scene(FXMLLoader.load(getClass().getClassLoader().getResource("eroiko/ani/view/WallpaperChooseWindow.fxml")));
                wallpaperScene.addEventFilter(KeyEvent.KEY_PRESSED, e -> {
                    System.out.println("Meow!?");
                    if (e.getCode().equals(KeyCode.RIGHT)){
                        System.out.println("Right");
                        wp.getNextWallpaper();
                    }
                    else if (e.getCode().equals(KeyCode.LEFT)){
                        System.out.println("Left");
                        wp.getPreviousWallpaper();
                    }
                    else if (e.getCode().equals(KeyCode.PLUS)){
                        System.out.println("Plus");
                        wp.add();
                    }
                    else if (e.getCode().equals(KeyCode.MINUS)){
                        System.out.println("Minus");
                        wp.delete();
                    }
                    e.consume();
                });
                stage.setScene(wallpaperScene);
                stage.getIcons().add(MainApp.icon);
                stage.setOnCloseRequest(e -> {
                    SourceRedirector.popToQueue(serialNumber);
                    SourceRedirector.deleteWallpaper(serialNumber);
                });
            }
            else {
                System.out.println("Open Wallpaper Viewer...");
                stage.setTitle("Wallpaper Viewer");
                var wallpaperScene = new Scene(FXMLLoader.load(getClass().getClassLoader().getResource("eroiko/ani/view/WallpaperViewWindow.fxml")));
                wallpaperScene.addEventFilter(KeyEvent.KEY_PRESSED, e -> {
                    System.out.println("Meow!?");
                    if (e.getCode().equals(KeyCode.RIGHT)){
                        System.out.println("Right");
                        wp.getNextWallpaper();
                    }
                    else if (e.getCode().equals(KeyCode.LEFT)){
                        System.out.println("Left");
                        wp.getPreviousWallpaper();
                    }
                    e.consume();
                });
                stage.setScene(wallpaperScene);
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
    /** Deprecated */
    void OpenWallpaperViewWindow(WallpaperImage wp) {
        OpenWallpaperViewWindow(wp, 0, 0);
    }
    
    void OpenWallpaper(Wallpaper wp, boolean deleteAfterClose) throws IOException{
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
        boolean isPreview = wp.getCurrentFullPath().getParent().equals(WallpaperPath.defaultImagePath);
        final int fixedSerialNumber = serialNumber;
        var stage = new Stage();
        System.out.println("Open Neo Wallpaper Viewer...");
        System.out.println(wp.isChanged.get());
        stage.setTitle("Neo Wallpaper Viewer");
        var wallpaperScene = new Scene(FXMLLoader.load(getClass().getClassLoader().getResource("eroiko/ani/view/WallpaperWindow.fxml")));
        wallpaperScene.addEventFilter(KeyEvent.KEY_PRESSED, e -> {
            if (e.getCode().equals(KeyCode.RIGHT)){
                if (!fixedWp.isEmpty()){
                    fixedWp.rightShift();
                }
            }
            else if (e.getCode().equals(KeyCode.LEFT)){
                if (!fixedWp.isEmpty()){
                    fixedWp.leftShift();
                }
            }
            if (!isPreview){
                if (e.getCode().equals(KeyCode.EQUALS) || e.getCode().equals(KeyCode.UP)){
                    if (!fixedWp.isEmpty()){
                        fixedWp.add();
                    }
                    fixedWp.triggerChangedFlag();
                }
                else if (e.getCode().equals(KeyCode.MINUS) || e.getCode().equals(KeyCode.DOWN)){
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
        if (deleteAfterClose){
            stage.setOnCloseRequest(e -> {
                SourceRedirector.deleteWallpaper(fixedSerialNumber);
                Wallpaper.appendToResultList(fixedSerialNumber);
            });
        }
        else {
            stage.setOnCloseRequest(e -> {
                Wallpaper.appendToResultList(fixedSerialNumber);
            });
        }
        stage.show();
    }

    void OpenWallpaper(boolean deleteAfterClose) throws IOException{
        OpenWallpaper(null, deleteAfterClose);
    }
    
    @FXML
    void SwitchBackToImgPath(ActionEvent event) {
        try {
            theWallpaper = new Wallpaper();
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
            Runtime.getRuntime().exec("explorer /select," + theWallpaper.getCurrentFullPath());
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
        try {
            var wpi = new Wallpaperize(tmp.showDialog(null).toPath(), toDefault);
            wpi.execute();
            wpi = null; // 釋放記憶體
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
        // imagePreview.setImage(preview.getCurrentWallpaper());
        imagePreview.setImage(theWallpaper.getCurrentPreviewImage());

        Terminal_out.setEditable(false);

        searchBar.setPromptText(">  Search Artwork");

        terminalButtonDivider.setMinWidth(100);
        
        downloadAmountChoice.getItems().addAll(modes[0], modes[1], modes[2]);
        downloadAmountChoice.setValue(modes[1]);
        pathLabel.setText(" " + WallpaperPath.defaultDataPath.toString()) ;
        // mainPbar = new ProgressBar();
        hasChangedPreview.addListener((a, b, c) -> {
            pathLabel.setText(" " + theWallpaper.getCurrentFullPath().getParent().toAbsolutePath().toString());
            initTreeView();
            hasChangedPreview.set(false);
            imagePreview.setImage(theWallpaper.getCurrentPreviewImage());
        });
        searchQueue.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        CrawlerManager.progress.addListener((a, b, c) -> {
            mainPbar.progressProperty().bind(CrawlerManager.progress);
            percentageMark.textProperty().bind(DoubleToStringProperty.toStringProperty(CrawlerManager.progress));
        });
        nowProcessingText.setEditable(false);
        
        initializeKeyBoardShortcuts();
        initializeMouseEvents();
        initSearchQueue();
        initTreeView();
    }
    
    public void initializeMouseEvents(){
        imagePreview.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2){
                try {
                    OpenWallpaper(theWallpaper, false);
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
                treeViewSelected();
            } catch (IOException e1) {
                System.out.println(e1);
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
        searchQueue.setOnKeyPressed(e -> {
            if (e.getCode().equals(KeyCode.DELETE)){
                var allData = searchQueue.getItems();
                var selectedData = searchQueue.getSelectionModel().getSelectedItems();
                for (int i = selectedData.size() - 1; i >= 0; --i){
                    allData.remove(selectedData.get(i));
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
    
    /* 採後序 */
    private void initTreeView() {
        // var rootPath = FileSystems.getDefault().getPath("data");
        var rootPath = Path.of(pathLabel.getText().stripLeading());
        var root = new TreeItem<>(new myPair<>(rootPath.getFileName().toString(), rootPath), WallpaperUtil.fetchIconUsePath(rootPath));
        try {
            postOrderTraverse(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
        // treeFileExplorer.setCellFactory(new Callback<TreeView<Path>, TreeCell<Path>>() {
        //     public TreeCell<Path> call(TreeView<Path> tv) {
        //         return new TreeCell<Path>() {
        //             @Override
        //             protected void updateItem(Path item, boolean empty) {
        //                 super.updateItem(item, empty);
        //                 setText((empty || item == null) ? "" : item.getFileName().toString());
        //             }
        //         };
        //     }
        // });
        // postOrderTraverseKillRedundantName(root); // 哀...
        root.setExpanded(true);
        treeFileExplorer.setRoot(root);
    }

    private TreeItem<myPair<String, Path>> postOrderTraverse(TreeItem<myPair<String, Path>> cur) throws IOException{
        if (Files.isDirectory(cur.getValue().value)){
            for (var p : Files.newDirectoryStream(cur.getValue().value)){
                // cur.getChildren().add(postOrderTraverse(new TreeItem<>(p))); // 非洲啊...
                cur.getChildren().add(postOrderTraverse(new TreeItem<>(new myPair<>(p.getFileName().toString(), p), WallpaperUtil.fetchIconUsePath(p)))); // 非洲啊...
            }
        }
        return cur;
    }

    // private TreeItem<Path> postOrderTraverse(TreeItem<Path> cur) throws IOException{
    //     if (Files.isDirectory(cur.getValue())){
    //         for (var p : Files.newDirectoryStream(cur.getValue())){
    //             // cur.getChildren().add(postOrderTraverse(new TreeItem<>(p))); // 非洲啊...
    //             cur.getChildren().add(postOrderTraverse(new TreeItem<>(p, WallpaperUtil.fetchIconUsePath(p)))); // 非洲啊...
    //         }
    //     }
    //     return cur;
    // }
    
    // private void postOrderTraverseKillRedundantName(TreeItem<Path> cur){ // 暫時會影響小圖示顯示問題...保留之後修改
        // if (Files.isDirectory(cur.getValue())){
        //     for (var p : cur.getChildren()){
        //         postOrderTraverseKillRedundantName(p);
        //         p.setValue(p.getValue().getFileName());
        //     }
        // }
    // }
    
    private void treeViewSelected() throws IOException{
        var path = treeFileExplorer.getSelectionModel().getSelectedItem().getValue().value.toAbsolutePath();
        System.out.println(path);
        if (Files.isDirectory(path.toRealPath().toAbsolutePath())){
            viewImageTileTable.getChildren().clear();
            viewImageTileTable.setPadding(new Insets(5., 5., 5., 8.));
            viewImageTileTable.setVgap(8.);
            viewImageTileTable.setHgap(8.);
            var paths = new ArrayList<Path>();
            Files.newDirectoryStream(path).forEach(e -> paths.add(e));
            paths.sort((a, b) -> WallpaperUtil.pathNameCompare(a, b));
            var list = new ArrayList<VBox>(paths.size());
            paths.forEach(p -> {
                var iconView = WallpaperUtil.fetchIconUsePath(p);
                iconView.setFitHeight(58);
                iconView.setFitWidth(58);
                
                var name = new Text(p.getFileName().toString());
                name.setStyle("-fx-font: 11 system;");
                
                var vbox = new VBox(iconView, name);
                vbox.setAlignment(Pos.CENTER);
                list.add(vbox);
            });
            // var container = new VBox();
            // container.setAlignment(Pos.CENTER);
            // container.getChildren().addAll(viewImageTileTable);
            // scrollableTile.setContent(container);
            viewImageTileTable.getChildren().addAll(list);
            scrollableTile.setContent(viewImageTileTable);
        }
        else {
            var iv = new ImageView(new Image(path.toFile().toURI().toString()));
            scrollableTile.setContent(iv);
        }

        scrollableTile.setPannable(true);
        scrollableTile.setFitToHeight(true);
        scrollableTile.setVbarPolicy(ScrollBarPolicy.ALWAYS);
    }
    // private void treeViewSelected() throws IOException{
        // var path = treeFileExplorer.getSelectionModel().getSelectedItem().getValue().toAbsolutePath();
        // System.out.println(path);
        // viewImageTileTable.getChildren().clear();
        // if (Files.isDirectory(path.toRealPath().toAbsolutePath())){
        //     // for (var p : path.toRealPath().getFileSystem().getRootDirectories()){
        //     viewImageTileTable.setPadding(new Insets(5., 5., 5., 8.));
        //     viewImageTileTable.setVgap(8.);
        //     viewImageTileTable.setHgap(8.);
        //     for (var p : Files.newDirectoryStream(path)){
        //         var iconView = WallpaperUtil.fetchIconUsePath(p);
        //         iconView.setFitHeight(58);
        //         iconView.setFitWidth(58);

        //         var name = new Text(p.getFileName().toString());
        //         name.setStyle("-fx-font: 11 system;");

        //         var vbox = new VBox(iconView, name);
        //         vbox.setAlignment(Pos.CENTER);
        //         viewImageTileTable.getChildren().add(vbox);
        //     }
        //     var container = new VBox();
        //     container.setAlignment(Pos.CENTER);
        //     container.getChildren().addAll(viewImageTileTable);
        //     scrollableTile.setContent(container);
        // }
        // else {
        //     var iv = new ImageView(new Image(path.toFile().toURI().toString()));
        //     scrollableTile.setContent(iv);
        // }

        // scrollableTile.setPannable(true);
        // scrollableTile.setFitToHeight(true);
        // scrollableTile.setVbarPolicy(ScrollBarPolicy.ALWAYS);
    // }
}
