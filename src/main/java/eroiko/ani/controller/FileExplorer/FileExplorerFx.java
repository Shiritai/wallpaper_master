package eroiko.ani.controller.FileExplorer;

import java.io.File;
import java.text.SimpleDateFormat;

import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.TilePane;

public class FileExplorerFx implements FileExplorer {

    static File CurrDirFile; // 當前目錄僅有一個
    static String CurrDirStr;
    static Label lbl;
    static String CurrDirName;
    static TilePane tilePane;
    SimpleDateFormat sdf;

    TableView<FileInfo> tableView;
    TableColumn<FileInfo, ImageView> image;
    TableColumn<FileInfo, String> date;
    TableColumn<FileInfo, String> name;
    TableColumn<FileInfo, String> size;

    @Override
    public Image getIconImageFX(File f) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public TreeItem<String>[] TreeCreate(File dir) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String calculateSize(File f) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String FindAbsolutePath(TreeItem<String> item, String s) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean IsDrive(File f) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public int HiddenFilesCount(File dir) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void CreateTreeView(TreeView<String> treeView) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void CreateTableView(TableView<FileInfo> tableView, TableColumn<FileInfo, ImageView> image,
            TableColumn<FileInfo, String> date, TableColumn<FileInfo, String> name,
            TableColumn<FileInfo, String> size) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void CreateTableView() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void setLabelTxt() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void setValues(TableView<FileInfo> tableView, TableColumn<FileInfo, ImageView> image,
            TableColumn<FileInfo, String> date, TableColumn<FileInfo, String> name,
            TableColumn<FileInfo, String> size) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void CreateTiles() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public int NumOfDirectChildren(File f) {
        // TODO Auto-generated method stub
        return 0;
    }
    
}
