package eroiko.ani.model.CLI.command.critical;

import eroiko.ani.model.CLI.command.fundamental.Type;
import eroiko.ani.model.CLI.exception.*;

public class Exit implements ThrowException {
    private final Type type;
    
    public Exit(){
        type = Type.EXIT;
    }
    
    @Override
    public void callHost() throws ShutdownSoftwareException, ExitConsoleException, ClearConsoleException {
        throw new ExitConsoleException(type.toString());
    }
}