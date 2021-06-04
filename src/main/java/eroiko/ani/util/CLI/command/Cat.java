package eroiko.ani.util.CLI.command;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;

import eroiko.ani.util.Method.Dumper;

public class Cat extends Command {
    private final String fileName;

    public Cat(String fileName){
        this.fileName = fileName;
    }
    
    @Override
    public void execute() throws IllegalArgumentException {
        Path target = thisDir.resolve(fileName);
        if (!Files.isDirectory(target) && target.toFile().exists()){ // is a file
            StringWriter out = new StringWriter();
            try {
                Dumper.dump(new FileReader(target.toFile()), out);
            } catch (FileNotFoundException fe){} // this never happens
            System.out.println(out);
        }
        else {
            throw new IllegalArgumentException("cat failed, not a file!");
        }
    }
}
