package eroiko.ani.model.CLI.command.basic;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.TreeSet;

import eroiko.ani.model.CLI.command.fundamental.*;

public class Ls extends Command {
        
    private final Comparator<Path> comparator;
    private final String [] cmdLine;

    public Ls(Comparator<Path> comparator, String [] cmdLine){
        super(Type.LS);
        this.comparator = comparator;
        this.cmdLine = cmdLine;
    }
    
    @Override
    public void execute(){
        if (cmdLine.length == 1){
            printList(comparator);
        }
        else if (cmdLine.length == 2){
            if (cmdLine[1].equals("-s") || cmdLine[1].equals("--sorted")){ // 照 WallpaperUtil.pathDirAndNameCompare 排序
                printList(comparator);
            }
            else if (cmdLine[1].equals("-n") || cmdLine[1].equals("--normal")){
                printList((a, b) -> a.toString().toLowerCase().compareTo(b.toString().toLowerCase()));
            }
            else if (cmdLine[1].equals("-p") || cmdLine[1].equals("--path")){
                printList(Path::compareTo);
            }

        }
    }
    
    private static void printList(Comparator<Path> comparator){
        var res = new TreeSet<Path>(comparator);
        try {
            try (var dirStream = Files.newDirectoryStream(thisDir)){
                dirStream.forEach(res::add);
                res.forEach(Command::printRelativePath);
            }
        } catch (IOException ie){
            ie.printStackTrace();
        }
    }
}
