package eroiko.ani.model.CLI.command.basic;

import eroiko.ani.model.CLI.command.basic.fundamental.*;

public class Mkdir extends Command {
    private final String fileName;

    public Mkdir(String fileName){
        super(Type.MKDIR);
        this.fileName = fileName;
    }

    @Override
    public void execute() throws IllegalArgumentException{
        if (thisDir.resolve(fileName).toFile().exists()){
            throw new IllegalArgumentException(id.getName() + " : File exist and thus cannot create it!");
        }
        else {
            thisDir.resolve(fileName).toFile().mkdir();
        }
    }

    @Override
    public void exeAfterRequest(String cmd) {
        
        
    }
}
