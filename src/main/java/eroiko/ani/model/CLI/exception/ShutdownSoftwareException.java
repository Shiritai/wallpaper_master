package eroiko.ani.model.CLI.exception;

public class ShutdownSoftwareException extends Exception {
        
    public ShutdownSoftwareException(){
        super();
    }
    
    public ShutdownSoftwareException(String exceptionMsg){
        super(exceptionMsg);
    }
}
