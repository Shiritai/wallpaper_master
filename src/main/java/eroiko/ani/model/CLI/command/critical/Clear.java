package eroiko.ani.model.CLI.command.critical;

import eroiko.ani.model.CLI.command.fundamental.Type;
import eroiko.ani.model.CLI.exception.*;

public class Clear implements ThrowException {

    private final Type type;
    
    public Clear(){
        type = Type.CLEAR;
    }
    
    @Override
    public void callHost() throws ShutdownSoftwareException, ExitConsoleException, ClearConsoleException {
        throw new ClearConsoleException(type.toString());
    }
    
}
