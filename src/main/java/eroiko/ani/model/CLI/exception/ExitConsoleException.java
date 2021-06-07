package eroiko.ani.model.CLI.exception;

public class ExitConsoleException extends RuntimeException{
    
    public ExitConsoleException(){
        super();
    }
    
    public ExitConsoleException(String exceptionMsg){
        super(exceptionMsg);
    }
}
