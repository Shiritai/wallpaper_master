package eroiko.ani.model.CLI.command.basic.fundamental;
import java.nio.file.AccessDeniedException;
import java.nio.file.Path;

import eroiko.ani.model.CLI.RequestCommand;
/**
 * Print the file path with their name
 * you can print with file's relative name by calling {@code Command.setPrintBehavior(true)}
 */
public abstract class Command {
    protected static Path thisDir;
    protected static Path defaultDir;
    protected static boolean printRelative = false;
    protected static RequestCommand rq = new RequestCommand();

    protected final Type id;

    protected Command(){
        id = Type.COMMAND;
    }

    protected Command(Type id){
        this.id = id;
    }

    /** 
     * 所有指令的進入點
     * <p>
     * 遇到不合理的呼叫參數 -> 向外拋出包含訊息的異常
     */
    abstract public void execute() throws IllegalArgumentException, AccessDeniedException;
    
    /** 請在各自指令完成內部狀態的紀錄, 獲得使用者回應後可以繼續執行 */
    abstract public void exeAfterRequest(String cmd);

    public final Type getType(){ return id; }
    
    public static Path getCurrentPath(){
        thisDir = thisDir.normalize();
        return thisDir;
    }

    public static void setDefaultPath(Path path){ defaultDir = thisDir = path; }
    public static void setPrintBehavior(boolean printRelative){ Command.printRelative = printRelative; }
    
    public static void printRelativePath(Path path){
        System.out.println((printRelative) ? thisDir.relativize(path) : path.getFileName());
    }
}
