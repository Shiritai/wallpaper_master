package eroiko.ani.model.CLI.command.basic;

import java.nio.file.AccessDeniedException;

import eroiko.ani.model.CLI.command.basic.fundamental.Command;
import eroiko.ani.model.CLI.command.basic.fundamental.Type;

public class Clear extends Command {

    public Clear(){
        super(Type.CLEAR);
    }

    @Override
    public void execute() throws IllegalArgumentException, AccessDeniedException {
        
        
    }
    
    @Override
    public void exeAfterRequest(String cmd) {
        
        
    }
    
}
