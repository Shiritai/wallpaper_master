package eroiko.ani.model.CLI.command.critical;

import eroiko.ani.model.CLI.command.fundamental.Type;
import eroiko.ani.model.CLI.exception.*;

public class Setting implements ThrowException {
    private final Type type;

    public Setting(){
        type = Type.SETTING;
    }
    
    @Override
    public void callHost() throws Exception {
        throw new TerminalSettingException(type.toString());
    }
}
