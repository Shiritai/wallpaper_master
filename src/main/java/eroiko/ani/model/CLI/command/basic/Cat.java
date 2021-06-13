/*
 * Author : Shiritai (楊子慶, or Eroiko on Github) at 2021/06/13.
 * See https://github.com/Shiritai/wallpaper_master for more information.
 * Created using VSCode.
 */
package eroiko.ani.model.CLI.command.basic;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;

import eroiko.ani.model.CLI.command.fundamental.*;
import eroiko.ani.model.CLI.util.Dumper;

public class Cat extends Command {
    private final String fileName;

    public Cat(String fileName){
        super(Type.CAT);
        this.fileName = fileName;
    }
    
    @Override
    public void execute() throws IllegalArgumentException {
        Path target = thisDir.resolve(fileName);
        if (!Files.isDirectory(target) && target.toFile().exists()){ // is a file
            StringWriter outWriter = new StringWriter();
            try {
                Dumper.dump(new FileReader(target.toFile()), outWriter);
            } catch (FileNotFoundException fe){} // this never happens
            out.println(outWriter);
        }
        else {
            throw illegalParaStr("Not a file!");
        }
    }
}
