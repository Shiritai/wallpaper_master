
/*
 * Author : Shiritai (楊子慶, or Eroiko on Github) at 2021/06/13.
 * See https://github.com/Shiritai/wallpaper_master for more information.
 * Created using VSCode.
 */package eroiko.ani.model.CLI.exception;

public class ClearConsoleException extends RuntimeException {

    public ClearConsoleException(){
        super();
    }
    
    public ClearConsoleException(String exceptionMsg){
        super(exceptionMsg);
    }
}
