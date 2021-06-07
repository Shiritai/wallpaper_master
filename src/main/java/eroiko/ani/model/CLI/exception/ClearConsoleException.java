package eroiko.ani.model.CLI.exception;

public class ClearConsoleException extends RuntimeException {

    public ClearConsoleException(){
        super();
    }
    
    public ClearConsoleException(String exceptionMsg){
        super(exceptionMsg);
    }
}
