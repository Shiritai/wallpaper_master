package eroiko.ani.model.CLI.exception;

public class CustomInformationException extends RuntimeException {
    public CustomInformationException(){
        super();
    }
    
    public CustomInformationException(String exceptionMsg){
        super(exceptionMsg);
    }
}
