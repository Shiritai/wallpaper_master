/*
 * Author : Shiritai (楊子慶, or Eroiko on Github) at 2021/06/13.
 * See https://github.com/Shiritai/wallpaper_master for more information.
 * Created using VSCode.
 */
package eroiko.ani.model.CLI.command.basic;

import eroiko.ani.model.CLI.command.fundamental.*;

public class Mkdir extends Command {
    private final String fileName;

    public Mkdir(String fileName){
        super(Type.MKDIR);
        this.fileName = fileName;
    }

    @Override
    public void execute() throws IllegalArgumentException {
        if (fileName == null){
            throw new IllegalArgumentException(illegalParaStr());
        }
        else if (thisDir.resolve(fileName).toFile().exists()){
            throw illegalParaStr("File exist and thus cannot create it!");
        }
        else {
            thisDir.resolve(fileName).toFile().mkdir();
        }
    }
}
