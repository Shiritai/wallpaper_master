package eroiko.ani.model.CLI.command;
import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Cd extends Command {
    private final Path target;
    
    public Cd(String target){
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
                        throw new IllegalArgumentException("cd failed, link corrupt");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else if (Files.isDirectory(target)){
                thisDir = target;
            }
            else {
                throw new IllegalArgumentException("cd failed, file not exist or not a directory");
            }
        }
    }
}
