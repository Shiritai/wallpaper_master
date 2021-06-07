package eroiko.ani.model.CLI.command.special.Shell;

import eroiko.ani.model.CLI.command.fundamental.Type;

public class PowerShell extends Shell {
    
    public PowerShell(String parameter){
        super((
            !parameter.equals("pwsh") && 
            !parameter.equals("powershell") && 
            !parameter.equals("powershell.exe")
            ) ? parameter : "", Type.POWERSHELL
        );
    }
    
    @Override
    public void execute() throws IllegalArgumentException {
        openShell("powershell");
    }
}
