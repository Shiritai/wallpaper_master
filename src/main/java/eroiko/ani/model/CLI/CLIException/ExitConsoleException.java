package eroiko.ani.model.CLI.CLIException;

public class ExitConsoleException extends RuntimeException{
    
    public ExitConsoleException(){
        super();
    }
    
    public ExitConsoleException(String exceptionMsg){
        super(exceptionMsg);
    }
}
