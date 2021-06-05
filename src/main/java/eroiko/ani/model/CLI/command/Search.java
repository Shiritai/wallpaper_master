package eroiko.ani.model.CLI.command;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Search extends Command {
    private final String keyword;

    public Search(String keyword){
        this.keyword = keyword;
    }
    
    @Override
    public void execute(){
        search(thisDir);
    }

    private void search(Path cur){
        if (cur != null){
            if (cur.getFileName().toString().contains(keyword)){
                printRelativePath(cur);
            }
            if (Files.isDirectory(cur)){
                try {
                    try (var dirStream = Files.newDirectoryStream(cur)){
                        dirStream.forEach(this::search);
                    }
                } catch (IOException ie){
                    ie.printStackTrace();
                }
            }
        }
    }
}
