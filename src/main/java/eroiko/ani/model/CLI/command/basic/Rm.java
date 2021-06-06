package eroiko.ani.model.CLI.command.basic;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.Files;
import java.nio.file.Path;

import eroiko.ani.model.CLI.RequestCommand;
import eroiko.ani.model.CLI.command.fundamental.*;

public class Rm extends Command{
    private final String fileName;
    private final int NONE = 0;
    private final int QUERY_DELETE = 1;
    private final String reqMsg = "This directory has child files.\nDo you want to continue? [Y/n]";
    private RequestCommand rq;
    private int state;

    public Rm(RequestCommand rq, String fileName){
        super(Type.RM);
        this.rq = rq;
        state = 0;
        this.fileName = fileName;
    }

    @Override
    public void execute() throws IllegalArgumentException, AccessDeniedException{
        Path target = thisDir.resolve(fileName);
        if (target.toFile().exists()){ // traverse and delete
            try {
                try (var dirStream = Files.newDirectoryStream(target)){
                    if (dirStream.iterator().hasNext()){
                        state = QUERY_DELETE;
                        out.println(reqMsg);
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
            throw new IllegalArgumentException("rm failed, file or directory not exist!");
        }
    }

    private void delete(Path cur){
        if (Files.isDirectory(cur)){
            try {
                try (var dirStream = Files.newDirectoryStream(cur)){
                    dirStream.forEach(this::delete);
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
                case NONE -> throw new IllegalArgumentException(id.getName() + " : NONE, some error happens!");
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
                        throw new IllegalArgumentException(id.getName() + " : File or directory not exist!");
                    }
                }
            }
        }
        else {
            throw new IllegalArgumentException(id.getName() + " : Canceled!");
        }
    }
}
