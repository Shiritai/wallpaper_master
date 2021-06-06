package eroiko.ani.model.CLI.command.basic;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.StringWriter;
import java.nio.file.AccessDeniedException;
import java.nio.file.Files;
import java.nio.file.Path;

import eroiko.ani.model.CLI.command.basic.fundamental.*;
import eroiko.ani.util.Method.Dumper;

public class Cat extends Command {
    private final String fileName;

    public Cat(String fileName){
        super(Type.CAT);
        this.fileName = fileName;
    }
    
    @Override
    public void execute() throws IllegalArgumentException, AccessDeniedException {
        Path target = thisDir.resolve(fileName);
        if (!Files.isDirectory(target) && target.toFile().exists()){ // is a file
            StringWriter out = new StringWriter();
            try {
                Dumper.dump(new FileReader(target.toFile()), out);
            } catch (FileNotFoundException fe){} // this never happens
            System.out.println(out);
        }
        else {
            throw new IllegalArgumentException(id.getName() + " : Not a file!");
        }
    }

    @Override
    public void exeAfterRequest(String cmd) {
        
        
    }
}
