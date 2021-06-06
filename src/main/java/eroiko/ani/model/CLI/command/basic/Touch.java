package eroiko.ani.model.CLI.command.basic;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;

import eroiko.ani.model.CLI.command.fundamental.*;
import eroiko.ani.model.CLI.util.Dumper;


public class Touch extends Command {
    private final String fileName;
    private final String context;

    public Touch(String fileName, String context){
        super(Type.TOUCH);
        this.fileName = fileName;
        this.context = context;
    }

    @Override
    public void execute() throws IllegalArgumentException{
        if (thisDir.resolve(fileName).toFile().exists()){
            throw new IllegalArgumentException(id.getName() + " : File exist and thus cannot create it!");
        }
        else {
            try {
                Dumper.dump(new StringReader(context), new BufferedWriter(new FileWriter(thisDir.resolve(fileName).toFile())));
            } catch (IOException ie){
                throw new IllegalArgumentException(id.getName() + " : Failed to write file.");
            }
        }
    }

    @Override
    public void exeAfterRequest(String cmd) {
        
        
    }
}
