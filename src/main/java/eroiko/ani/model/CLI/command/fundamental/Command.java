/*
 * Author : Shiritai (楊子慶, or Eroiko on Github) at 2021/06/13.
 * See https://github.com/Shiritai/wallpaper_master for more information.
 * Created using VSCode.
 */
package eroiko.ani.model.CLI.command.fundamental;
import java.io.PrintStream;
import java.nio.file.Path;

/**
 * The fundamental type of all commands <p>
 * To print the file path with their name
 * you can print with file's relative name by calling {@code Command.setPrintBehavior(true)}
 */
public abstract class Command {
    protected static Path thisDir;
    protected static Path defaultDir;
    protected static boolean printRelative = false;
    protected static PrintStream defaultOut;
    protected final PrintStream out;
    protected final Type id;

    protected Command(){
        this(Type.COMMAND);
    }
    
    protected Command(Type id){
        out = (defaultOut == null) ? System.out : defaultOut;
        this.id = id;
    }

    public static void setAllCommandPrintStream(PrintStream defaultOut){
        Command.defaultOut = defaultOut;
    }

    /** 
     * 指令的執行點
     * <p>
     * 遇到不合理的呼叫參數 -> 向外拋出包含訊息的異常
     */
    abstract public void execute() throws IllegalArgumentException;

    public final Type getType(){ return id; }

    public IllegalArgumentException illegalParaStr(){ return new IllegalArgumentException(id.getName() + " : Lost or too much parameters"); }
    public IllegalArgumentException illegalParaStr(String content){ return new IllegalArgumentException(id.getName() + " : " + content); }
    public IllegalArgumentException illegalParaStr(String formerExceptionMsg, String content){ return new IllegalArgumentException(formerExceptionMsg + "\n" + id.getName() + " : " + content); }
    
    public static Path getCurrentPath(){
        thisDir = thisDir.normalize();
        return thisDir;
    }

    public static void setDefaultPath(Path path){ defaultDir = thisDir = path; }
    public static void setPrintBehavior(boolean printRelative){ Command.printRelative = printRelative; }
    
    public static void printRelativePath(Path path){
        defaultOut.println((printRelative) ? thisDir.relativize(path) : path.getFileName());
    }

    // @Override
    // public String getManualDoc(){
    //     return "This is the basic type of all Commands, you shouldn't see this!"; 
    // }
}
