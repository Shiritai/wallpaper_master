package eroiko.ani.model.CLI.command.special.Shell;

import eroiko.ani.model.CLI.command.fundamental.Type;

public class Bash extends Shell {
    
    public Bash(String parameter){
        super((!parameter.equals("bash")) ? parameter : "", Type.BASH);
    }
    
    @Override
    public void execute() throws IllegalArgumentException {
        openShell("bash");
    }
}
