package eroiko.ani.model.CLI.command;

public class Mkdir extends Command {
    private final String fileName;

    public Mkdir(String fileName){
        this.fileName = fileName;
    }

    @Override
    public void execute() throws IllegalArgumentException{
        if (thisDir.resolve(fileName).toFile().exists()){
            throw new IllegalArgumentException("mkdir failed, file exist and thus cannot create it!");
        }
        else {
            thisDir.resolve(fileName).toFile().mkdir();
        }
    }
}
