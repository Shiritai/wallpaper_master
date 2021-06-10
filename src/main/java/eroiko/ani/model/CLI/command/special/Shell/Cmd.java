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
