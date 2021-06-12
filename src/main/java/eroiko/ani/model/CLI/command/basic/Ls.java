package eroiko.ani.model.CLI.command.basic;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.TreeSet;

import eroiko.ani.model.CLI.command.fundamental.*;

public class Ls extends Command {
        
    private final Comparator<Path> comparator;
    private final String cmdLine;

    /** 
     * 會直接列印例外
     * <p> 如果可以, 請調用其他執行緒來執行 
     */
    public Ls(Comparator<Path> comparator, String cmdLine){
        super(Type.LS);
        this.comparator = comparator;
        this.cmdLine = cmdLine;
    }
    
    @Override
    public void execute() throws IllegalArgumentException{
        if (cmdLine == null){
            printList(comparator);
        }
        else if (cmdLine.equals("-s") || cmdLine.equals("--sorted")){ // 照指定 Comparator 排序
            printList(comparator);
        }
        else if (cmdLine.equals("-n") || cmdLine.equals("--normal")){
            printList((a, b) -> a.toString().toLowerCase().compareTo(b.toString().toLowerCase()));
        }
        else if (cmdLine.equals("-p") || cmdLine.equals("--path")){
            printList(Path::compareTo);
        }
        else {
            out.println(illegalParaStr().getMessage());
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
