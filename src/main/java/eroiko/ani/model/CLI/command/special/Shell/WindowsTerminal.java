package eroiko.ani.model.CLI.command.special.Shell;

import eroiko.ani.model.CLI.command.fundamental.Type;

public class WindowsTerminal extends Shell {
    
    public WindowsTerminal(String parameter){
        super(((!parameter.equals("wt") && !parameter.equals("wt.exe")) ? parameter : ""), Type.WINDOWS_TERMINAL);
    }
    
    @Override
    public void execute() throws IllegalArgumentException {
        openShell("wt");
    }
}
