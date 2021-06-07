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
            throw new IllegalArgumentException(id.getName() + " : Not a file!");
        }
    }
}
