/*
 * Author : Shiritai (楊子慶, or Eroiko on Github) at 2021/06/13.
 * See https://github.com/Shiritai/wallpaper_master for more information.
 * Created using VSCode.
 */
package eroiko.ani.model.CLI.command.basic;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import eroiko.ani.model.CLI.command.fundamental.*;
import eroiko.ani.model.CLI.conversation.Consultable;
import eroiko.ani.model.CLI.conversation.Consulter;

public class Rm extends Command implements Consultable{
    private final String fileName;
    private final int NONE = 0;
    private final int QUERY_DELETE = 1;
    private final String reqMsg = "This directory has child file.\nDo you want to continue? [Y/n]";
    private Consulter rq;
    private int state;

    public Rm(Consulter rq, String fileName){
        super(Type.RM);
        this.rq = rq;
        state = 0;
        this.fileName = fileName;
    }

    @Override
    public void execute() throws IllegalArgumentException{
        Path target = thisDir.resolve(fileName);
        if (target.toFile().exists()){ // traverse and delete
            if (!Files.isDirectory(target)){
                target.toFile().delete();
                return;
            }
            try {
                try (var dirStream = Files.newDirectoryStream(target)){
                    if (dirStream.iterator().hasNext()){
                        state = QUERY_DELETE;
                        rq.pushRequest(this);
                        return;
                    }
                    else {
                        delete(target);
                    }
                }
            } catch (IOException ie){ ie.printStackTrace(); }
        }
        else {
            throw illegalParaStr("rm failed, file or directory not exist!");
        }
    }

    public static void delete(Path cur){
        if (Files.isDirectory(cur)){
            try {
                try (var dirStream = Files.newDirectoryStream(cur)){
                    dirStream.forEach(Rm::delete);
                }
            } catch (IOException ie){ ie.printStackTrace(); }
            cur.toFile().delete();
        }
        else {
            cur.toFile().delete();
        }
    }

    @Override
    public void exeAfterRequest(String cmd) throws IllegalArgumentException {
        if (cmd.equals("") || cmd.toLowerCase().trim().equals("y")){
            switch (state){
                case NONE -> throw illegalParaStr("NONE, no required request!");
                case QUERY_DELETE -> {
                    out.println(id.getName() + " : executed.");
                    Path target = thisDir.resolve(fileName);
                    if (target.toFile().exists()){ // traverse and delete
                        try {
                            try (var dirStream = Files.newDirectoryStream(target)){
                                delete(target);
                            }
                        } catch (IOException ie){ ie.printStackTrace(); }
                    }
                    else {
                        throw illegalParaStr("File or directory not exist!");
                    }
                }
            }
        }
        else {
            throw illegalParaStr("Canceled.");
        }
    }

    @Override
    public String getPushingMessage() {
        return reqMsg;
    }
}
