package eroiko.ani.model.CLI.command;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Rm extends Command{
    private final String fileName;

    public Rm(String fileName){
        this.fileName = fileName;
    }

    @Override
    public void execute() throws IllegalArgumentException, AccessDeniedException{
        Path target = thisDir.resolve(fileName);
        if (target.toFile().exists()){ // traverse and delete
            delete(target);
        }
        else {
            throw new IllegalArgumentException("rm failed, file or directory not exist!");
        }
    }

    private void delete(Path cur){
        if (Files.isDirectory(cur)){
            try {
                try (var dirStream = Files.newDirectoryStream(cur)){
                    dirStream.forEach(this::delete);
                }
            } catch (IOException ie){ ie.printStackTrace(); }
            cur.toFile().delete();
        }
        else {
            cur.toFile().delete();
        }
    }
}
