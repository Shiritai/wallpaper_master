package eroiko.ani.model.CLI.command.critical;

import eroiko.ani.model.CLI.exception.ClearConsoleException;
import eroiko.ani.model.CLI.exception.ExitConsoleException;
import eroiko.ani.model.CLI.exception.ShutdownSoftwareException;

public interface ThrowException {
    public void callHost() throws ShutdownSoftwareException, ExitConsoleException, ClearConsoleException;
}
