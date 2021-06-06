package eroiko.ani.model.CLI.command.basic;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import eroiko.ani.model.CLI.command.fundamental.*;

public class Search extends Command {
    private final String keyword;

    public Search(String keyword){
        super(Type.SEARCH);
        this.keyword = keyword;
    }
    
    @Override
    public void execute(){
        search(thisDir);
    }

    /** 未處理 AccessDeniedException */
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

    @Override
    public void exeAfterRequest(String cmd) {
        
        
    }
}
