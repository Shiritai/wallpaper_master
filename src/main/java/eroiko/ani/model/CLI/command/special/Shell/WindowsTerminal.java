/*
 * Author : Shiritai (楊子慶, or Eroiko on Github) at 2021/06/13.
 * See https://github.com/Shiritai/wallpaper_master for more information.
 * Created using VSCode.
 */
package eroiko.ani.model.CLI.command.special.Shell;

import eroiko.ani.model.CLI.command.fundamental.Type;

public class WindowsTerminal extends Shell {
    
    public WindowsTerminal(String parameter){
        super(((!parameter.equals("wt") && !parameter.equals("wt.exe")) ? parameter : ""), Type.WT);
    }
    
    @Override
    public void execute() throws IllegalArgumentException {
        openShell("wt");
    }
}
