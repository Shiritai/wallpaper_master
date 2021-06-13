/*
 * Author : Shiritai (楊子慶, or Eroiko on Github) at 2021/06/13.
 * See https://github.com/Shiritai/wallpaper_master for more information.
 * Created using VSCode.
 */
package eroiko.ani.model.CLI.command.special.Shell;

import eroiko.ani.model.CLI.command.fundamental.Type;

public class Cmd extends Shell {
    
    public Cmd(String parameter){
        super((!parameter.equals("cmd") && !parameter.equals("cmd.exe")) ? parameter : "", Type.CMD);
    }
    
    @Override
    public void execute() throws IllegalArgumentException {
        openShell("cmd /C");
    }
}
