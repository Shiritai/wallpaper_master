/*
 * Author : Shiritai (楊子慶, or Eroiko on Github) at 2021/06/13.
 * See https://github.com/Shiritai/wallpaper_master for more information.
 * Created using VSCode.
 */
package eroiko.ani.model.CLI.command.critical;

import eroiko.ani.model.CLI.exception.ClearConsoleException;
import eroiko.ani.model.CLI.exception.ExitConsoleException;
import eroiko.ani.model.CLI.exception.ShutdownSoftwareException;

public interface ThrowException {
    public void callHost() throws ShutdownSoftwareException, ExitConsoleException, ClearConsoleException;
}
