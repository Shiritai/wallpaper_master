package eroiko.ani.model.CLI.command.basic;
import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.Files;
import java.nio.file.Path;

import eroiko.ani.model.CLI.command.basic.fundamental.*;

public class Cd extends Command {
    private final Path target;
    
    public Cd(String target){
        super(Type.CD);
        this.target = thisDir.resolve(target);
    }

    @Override
    public void execute() throws IllegalArgumentException, AccessDeniedException {
        if (target.toString().equals("..")){
            thisDir = thisDir.getParent();
            if (thisDir == null){
                thisDir = defaultDir;
            }
        }
        else {
            if (Files.isSymbolicLink(target)){
                try {
                    var unchecked = Files.readSymbolicLink(target);
                    if (unchecked.toFile().exists()){
                        thisDir = unchecked;
                    }
                    else {
                        throw new IllegalArgumentException(id.getName() + " : Link corrupt");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else if (Files.isDirectory(target)){
                thisDir = target;
            }
            else {
                throw new IllegalArgumentException(id.getName() + " : File not exist or not a directory");
            }
        }
    }

    @Override
    public void exeAfterRequest(String cmd) {
        
        
    }
}
