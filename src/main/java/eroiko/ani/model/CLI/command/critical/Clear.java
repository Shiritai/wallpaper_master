/*
 * Author : Shiritai (楊子慶, or Eroiko on Github) at 2021/06/13.
 * See https://github.com/Shiritai/wallpaper_master for more information.
 * Created using VSCode.
 */
package eroiko.ani.model.CLI.command.critical;

import eroiko.ani.model.CLI.command.fundamental.Type;
import eroiko.ani.model.CLI.exception.*;

public class Clear implements ThrowException {

    private final Type type;
    
    public Clear(){
        type = Type.CLEAR;
    }
    
    @Override
    public void callHost() throws ClearConsoleException {
        throw new ClearConsoleException(type.toString());
    }
    
}
