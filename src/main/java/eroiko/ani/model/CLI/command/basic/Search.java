/*
 * Author : Shiritai (楊子慶, or Eroiko on Github) at 2021/06/13.
 * See https://github.com/Shiritai/wallpaper_master for more information.
 * Created using VSCode.
 */
package eroiko.ani.model.CLI.command.basic;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import eroiko.ani.model.CLI.command.fundamental.*;

public class Search extends Command {
    private final String keyword;

    /**
     * 列出該路徑下所有擁有指定 {@code keyword} 的檔案與資料夾
     */
    public Search(String keyword){
        super(Type.SEARCH);
        this.keyword = keyword;
    }
    
    @Override
    public void execute(){
        search(thisDir); // recur
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
}
