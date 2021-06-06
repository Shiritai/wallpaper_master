package eroiko.ani.model.CLI.command.basic;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.TreeSet;

import eroiko.ani.model.CLI.command.fundamental.*;

public class Ls extends Command {
        
    private final Comparator<Path> comparator;
    
    public Ls(){
        super(Type.LS);
        comparator = null;
    }

    public Ls(Comparator<Path> comparator){
        super(Type.LS);
        this.comparator = comparator;
    }
    
    @Override
    public void execute(){
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

    @Override
    public void exeAfterRequest(String cmd) {
        
    }
}
