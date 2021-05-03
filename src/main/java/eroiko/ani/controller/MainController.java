package eroiko.ani.controller;

import java.io.*;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.Executors;

import de.jensd.fx.glyphs.GlyphsDude;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcons;
import eroiko.ani.MainApp;
import eroiko.ani.controller.ConsoleTextArea.TerminalThread;
import eroiko.ani.controller.ControllerSupporter.WallpaperImage;
import eroiko.ani.controller.PrimaryControllers.PreferenceController;
import eroiko.ani.controller.PrimaryControllers.TestingController;
import eroiko.ani.controller.PrimaryControllers.WallpaperViewController;
import eroiko.ani.model.Crawler.CrawlerZeroChan;
import eroiko.ani.model.Crawler.OldCrawlerManager;
import eroiko.ani.util.SourceRedirector;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class MainController implements Initializable {
    /* Support variables */
    public static WallpaperImage preview;
    public static ImageView staticImagePreview;
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
    @FXML private ProgressIndicator searchProgressIndicator;
    @FXML private ImageView imagePreview;
    @FXML private TreeView<String> treeFileExplorer;
    @FXML private Label pathLabel;
    @FXML private TextField searchBar;
    @FXML private Label progressBarText;
    @FXML private BorderPane openWindowsFileExplorer = new BorderPane();

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
    void GoSearch(ActionEvent event) {
        Search();
    }
    
    void Search(){
        new OldCrawlerManager(searchBar.getText(), quit);
        // try {
        //     CrawlerZeroChan crawler = new CrawlerZeroChan(TestFunctions.testWallpaperPath.toString(), keywords.split(" "), 2, 1);
        //     var service = Executors.newCachedThreadPool();
        //     var previewResult = crawler.readMultiplePagesAndDownloadPreviews(5, service);

        //     service.shutdown();
        //     WallpaperImage wp = null;
        //     if (SourceRedirector.preViewOrNot){
        //         System.out.println("Opening Preview Viewing Window... " + crawler.getPreviewsFolderPath().replace('/', '\\'));
        //         if (!quit){
        //             System.err.println("Opening Preview Viewing Window... " + crawler.getPreviewsFolderPath().replace('/', '\\'));
        //         }
        //         wp = new WallpaperImage(crawler.getPreviewsFolderPath().replace('/', '\\'), false);
        //     }
        //     else {
        //         var service2 = Executors.newCachedThreadPool();
        //         crawler.downloadSelectedImagesUsingPAIRs(previewResult, service2);
        //         service2.shutdown();
        //         while (!service2.isShutdown()); // 等待線程關閉
        //         System.out.println("Download complete!");
        //         if (!quit){
        //             System.err.println("Download complete!");
        //         }
        //         System.out.println("Opening Preview Viewing Window... " + crawler.getFolderPath().replace('/', '\\'));
        //         if (!quit){
        //             System.err.println("Opening Preview Viewing Window... " + crawler.getFolderPath().replace('/', '\\'));
        //         }
        //         wp = new WallpaperImage(crawler.getFolderPath().replace('/', '\\'), false);
        //     }
        //     preview = wp;
        //     imagePreview.setImage(preview.getCurrentWallpaper());
        //     if (SourceRedirector.showWallpapersAfterCrawling){
        //         OpenWallpaperViewWindow(wp, 960, 600);
        //     }
        // } catch (IOException e) {
        //     Alert alert = new Alert(AlertType.INFORMATION);
        //     alert.titleProperty().set("Message");
        //     alert.headerTextProperty().set("Wrong keywords, please check and search again.");
        //     alert.showAndWait();
        // }
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
    void startTerminal(ActionEvent event) {
        try {
            pipIn = new PipedInputStream();
            System.setOut(new PrintStream(new PipedOutputStream(pipIn), true));
        } catch (IOException e1) {
            System.err.println(e1.toString());
        }
        quit = false;
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
            stage.setTitle("Wallpaper Viewer");
            stage.setScene(new Scene(FXMLLoader.load(getClass().getClassLoader().getResource("eroiko/ani/view/WallpaperViewWindow.fxml"))));
            stage.getIcons().add(new Image(getClass().getClassLoader().getResource("eroiko/ani/img/wallpaper79.png").toString()));
            stage.setOnCloseRequest(e -> {
                SourceRedirector.deleteWallpaper(serialNumber);
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

    void OpenWallpaperViewWindow(WallpaperImage wp) {
        OpenWallpaperViewWindow(wp, 0, 0);
    }

    @Override
    public void initialize(URL url, ResourceBundle rb){
        preview = new WallpaperImage();
        staticImagePreview = imagePreview;
        imagePreview.setImage(preview.getCurrentWallpaper());
        Terminal_out.setEditable(false);
        openWindowsFileExplorer.setCenter(GlyphsDude.createIcon(FontAwesomeIcons.BARCODE, "400px"));
        // (GlyphsDude.createIcon(FontAwesomeIcons.FOLDER, "40px"));
        mainPbar = MainCtrlPbar;
        searchProgressIndicator = MainCtrlPin;
        initializeKeyBoardShortcuts();
        initializeMouseEvents();
    }

    public void initializeMouseEvents(){
        Terminal_in.focusedProperty().addListener((arg, oldProperty, newProperty) -> {
            if (newProperty){
                Terminal_in.setText("");
            }
            else if (Terminal_in.getText().length() == 0){
                Terminal_in.setText(" <Terminal_Input>");
            }
        });
        searchBar.focusedProperty().addListener((arg, oldProperty, newProperty) -> {
            if (newProperty){
                searchBar.setText("");
            }
            else if (searchBar.getText().length() == 0){
                searchBar.setText(">  Search Artwork");
            }
        });
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
                Search();
                e.consume();
            }
        });
    }
}
