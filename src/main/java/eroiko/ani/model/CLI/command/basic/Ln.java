package eroiko.ani.model.CLI.command.basic;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import eroiko.ani.model.CLI.command.fundamental.*;

/** 
 * 有待在 Windows 上更好的實作, 目前這個指令只能在 Linux 下順利運行 
 * <p> 似乎是因為在 Windows 下需要取得 Admin 才可使用 link
 */
public class Ln extends Command {
    private final Path dirName;
    private final Path linkName;
    
    public Ln(String dirName, String linkName){
        super(Type.LN);
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
            throw new IllegalArgumentException(id.getName() + " : Bad directory or link name conflict.");
        }
    }
}
