/*
 * Author : Shiritai (楊子慶, or Eroiko on Github) at 2021/06/13.
 * See https://github.com/Shiritai/wallpaper_master for more information.
 * Created using VSCode.
 */
package eroiko.ani.model.CLI.exception;

public class ExitConsoleException extends RuntimeException{
    
    public ExitConsoleException(){
        super();
    }
    
    public ExitConsoleException(String exceptionMsg){
        super(exceptionMsg);
    }
}
