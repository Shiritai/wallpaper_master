package eroiko.ani.controller;

import java.io.*;
import java.net.URL;
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
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class MainController implements Initializable {
    /* Support variables */
    public static WallpaperImage preview;
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
    
    @FXML private TreeView<String> treeFileExplorer;
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
    @FXML private MediaView mediaBox;
    static MediaPlayer staticCompleteMusic;
    static MediaPlayer staticProcessingMusic;
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
        mediaBox.setMediaPlayer(staticProcessingMusic);
        staticProcessingMusic.setCycleCount(data.size() * 4);
        staticProcessingMusic.play();
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
                            var cw = new CrawlerManager(SourceRedirector.defaultDataPath.toAbsolutePath().toString(), WallpaperUtil.capitalize(data.get(i).key).split(" "), mode);
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
                staticProcessingMusic.stop();
                mediaBox.setMediaPlayer(staticCompleteMusic);
                staticCompleteMusic.play();
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
            if (WallpaperUtil.isImage(file)){
                theWallpaper = new Wallpaper(file.getParent(), file);
                // preview = new WallpaperImage(file.getParent().toAbsolutePath().toString(), false, file);
                hasChangedPreview.set(true);
                // imagePreview.setImage(preview.getCurrentWallpaper());    
                imagePreview.setImage(theWallpaper.getCurrentPreviewImage());
            }
            else {
                theWallpaper = new Wallpaper(file);
                // preview = new WallpaperImage(file.toAbsolutePath().toString(), false);
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
        OpenMusicWindow();
    }
    
    public void OpenMusicWindow(){
        // tableOfBrowser.getSelectionModel().select(3); // 3 is the index of the tab
        try {
            var stage = new Stage();
            stage.setTitle("Music with syamiko");
            stage.setScene(new Scene(FXMLLoader.load(getClass().getClassLoader().getResource("eroiko/ani/view/MusicWindow.fxml"))));
            stage.getIcons().add(new Image(getClass().getClassLoader().getResource("eroiko/ani/img/wallpaper79.png").toString()));
            stage.setResizable(false);
            stage.show();
        } catch (Exception e){
            System.out.println(e.toString());
            if (!quit){
                System.err.println(e.toString());
            }
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
                stage.getIcons().add(new Image(getClass().getClassLoader().getResource("eroiko/ani/img/wallpaper79.png").toString()));
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
                if (e.getCode().equals(KeyCode.PLUS) || e.getCode().equals(KeyCode.UP)){
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
        stage.getIcons().add(new Image(getClass().getClassLoader().getResource("eroiko/ani/img/wallpaper79.png").toString()));
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
            // Runtime.getRuntime().exec("explorer /select," + preview.getCurrentWallpaperPath());
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
        pathLabel.setText(" " + WallpaperPath.defaultDataPath.toAbsolutePath().toString()) ;
        // mainPbar = new ProgressBar();
        hasChangedPreview.addListener((a, b, c) -> {
            pathLabel.setText(" " + preview.getCurrentWallpaperPath().getParent().toAbsolutePath().toString());
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
        initMediaSettings();
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
                // imagePreview.setImage(preview.getNextWallpaper());
                imagePreview.setImage(theWallpaper.getPreviousPreviewImage());
            }
            else if (dist < 0){
                // imagePreview.setImage(preview.getPreviousWallpaper());
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
    
    private void initMediaSettings() {
        staticProcessingMusic = new MediaPlayer(MediaOperator.playBox.getDefaultProcessingMedia());
        staticCompleteMusic = new MediaPlayer(MediaOperator.playBox.getDefaultCompleteMedia());
    }
}
