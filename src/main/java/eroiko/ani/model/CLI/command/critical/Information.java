package eroiko.ani.model.CLI.command.critical;

import eroiko.ani.model.CLI.command.fundamental.Type;
import eroiko.ani.model.CLI.exception.CustomInformationException;

public class Information implements ThrowException {
    private final Type type;

    public Information(){
        type = Type.INFORMATION;
    }
    
    @Override
    public void callHost() throws CustomInformationException {
        throw new CustomInformationException(type.toString());
    }
    
}
