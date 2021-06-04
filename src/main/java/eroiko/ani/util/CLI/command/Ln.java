package eroiko.ani.util.CLI.command;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Ln extends Command {
    private final Path dirName;
    private final Path linkName;
    
    public Ln(String dirName, String linkName){
        this.dirName = thisDir.resolve(dirName);
        this.linkName = thisDir.resolve(linkName);
    }

    @Override
    public void execute() throws IllegalArgumentException {
        if (dirName.toFile().exists() && !linkName.toFile().exists()){
            try {
                Files.createSymbolicLink(linkName, dirName);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else {
            throw new IllegalArgumentException("Link failed, bad directory or link name conflict.");
        }
    }
}
