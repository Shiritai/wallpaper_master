package eroiko.ani.controller.FileExplorer;

import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.File;


/**
 * Created by Fahim on 4/27/2017.
 */
public interface FileExplorer {
    /**this class will contain all the method's name. as other derived classes
     * will be created, they will implement necessary methods accordingly...*/
    Image getIconImageFX(File f);
    TreeItem<String>[] TreeCreate(File dir);
    String calculateSize(File f);
    String FindAbsolutePath(TreeItem<String> item, String s);
    boolean IsDrive(File f);
    int HiddenFilesCount(File dir);
    void CreateTreeView(TreeView<String> treeView);
    void CreateTableView(TableView<FileInfo> tableView, TableColumn<FileInfo, ImageView> image,
        TableColumn<FileInfo, String> date, TableColumn<FileInfo, String> name, TableColumn<FileInfo, String> size);
    void CreateTableView();
    // void CreateTilesView();
    void setLabelTxt();
    // void Initiate();
    void setValues(TableView<FileInfo> tableView,TableColumn<FileInfo, ImageView> image,
        TableColumn<FileInfo, String> date, TableColumn<FileInfo, String> name,TableColumn<FileInfo, String> size);
    void CreateTiles();
    public int NumOfDirectChildren(File f);
}
