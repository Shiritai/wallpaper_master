package eroiko.ani.model.CLI.CLIException;

public class ClearConsoleException extends RuntimeException {

    public ClearConsoleException(){
        super();
    }
    
    public ClearConsoleException(String exceptionMsg){
        super(exceptionMsg);
    }
}
