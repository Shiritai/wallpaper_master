/*
 * Author : Shiritai (楊子慶, or Eroiko on Github) at 2021/06/13.
 * See https://github.com/Shiritai/wallpaper_master for more information.
 * Created using VSCode.
 */
package eroiko.ani.model.CLI.command.basic;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;

import eroiko.ani.model.CLI.command.fundamental.*;
import eroiko.ani.model.CLI.util.Dumper;


public class Touch extends Command {
    private final String fileName;
    private final String context;

    /**
     * 此 Touch 並非完整的 Linux 的 touch 指令
     * <P> 目前只擁有創建檔案 {@code fileName} 並寫入字串 {@code context} 的功能
     * @param fileName  創建檔案檔名
     * @param context   檔案內容
     * @throws IllegalArgumentException if the parameter is illegal
     */
    public Touch(String parameter){
        super(Type.TOUCH);
        var tmp = parameter.split(" ");
        if (tmp.length != 2){
            throw new IllegalArgumentException(illegalParaStr());
        }
        this.fileName = tmp[0];
        this.context = tmp[1];
    }

    @Override
    public void execute() throws IllegalArgumentException {
        if (thisDir.resolve(fileName).toFile().exists()){
            throw illegalParaStr("File exist and thus cannot create it!");
        }
        else {
            try {
                Dumper.dump(new StringReader(context), new BufferedWriter(new FileWriter(thisDir.resolve(fileName).toFile())));
            } catch (IOException ie){
                throw illegalParaStr("Failed to write file.");
            }
        }
    }
}
