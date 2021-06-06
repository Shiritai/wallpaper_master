package eroiko.ani.model.CLI.CLIException;

public class ClearTerminalException extends RuntimeException {

    public ClearTerminalException(){
        super();
    }
    
    public ClearTerminalException(String exceptionMsg){
        super(exceptionMsg);
    }
}
