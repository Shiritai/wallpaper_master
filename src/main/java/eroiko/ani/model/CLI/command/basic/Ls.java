package eroiko.ani.model.CLI.command.basic;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.TreeSet;

import eroiko.ani.model.CLI.command.fundamental.*;
import eroiko.ani.util.NeoWallpaper.WallpaperUtil;

public class Ls extends Command {
        
    public Ls(){
        super(Type.LS);
    }
    
    @Override
    public void execute(){
        var res = new TreeSet<Path>(WallpaperUtil::pathDirAndNameCompare);
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
