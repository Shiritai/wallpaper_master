package eroiko.ani.util.CLI.command;
import java.nio.file.Path;

/**
 * Print the file path with their name
 * you can print with file's relative name by calling {@code Command.setPrintBehavior(true)}
 */
public abstract class Command {
    protected static Path thisDir;
    protected static Path defaultDir;
    protected static boolean printRelative = false;
    abstract public void execute() throws IllegalArgumentException;

    public static Path getCurrentPath(){
        thisDir = thisDir.normalize();
        return thisDir;
    }

    public static void setDefaultPath(Path path){ defaultDir = thisDir = path; }
    public static void setPrintBehavior(boolean printRelative){ Command.printRelative = printRelative; }
    
    public static void printRelativePath(Path path){
        if (printRelative){
            System.out.println(thisDir.relativize(path));
        }
        else {
            System.out.println(path.getFileName());
        }
    }
}
