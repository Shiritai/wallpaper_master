package eroiko.ani.model.CLI;

import java.nio.file.AccessDeniedException;
import java.nio.file.Path;
import java.text.DateFormat;
import java.util.Date;

import eroiko.ani.model.CLI.command.*;

public class Console {
    
    public final String computerName;
    
    public Console(Path root, String computerName, boolean printRelative){
        this.computerName = computerName;
        Command.setDefaultPath(root);
        Command.setPrintBehavior(printRelative);
        System.out.println(toString());
    }
    
    public void setPath(Path root){
        Command.setDefaultPath(root);
    }

    public Path getCurrentPath(){ return Command.getCurrentPath(); }
    
    /** return {@code null} if there is no previous history command */
    public String getPreviousCommand(){ return History.getPreviousCommand(); }
    /** return {@code null} if there is no later history command */
    public String getLaterCommand(){ return History.getNextCommand(); }
    /** restore command traverse */
    public void restoreCommandTraverse(){ History.restoreCommandTraverse(); }
    
    public int readConsole(String cmd){
        if (!cmd.equals("")){
            System.out.println(toString(cmd));
            History.addHistory(cmd);
        }
        else {
            System.out.println(toString());
        }
        String [] cmdLine = cmd.split(" ");
        try {
            switch (cmdLine[0]){
                case "" -> {}
                case "cd" -> new Cd(cmdLine[1]).execute();
                case "mkdir" -> new Mkdir(cmdLine[1]).execute();
                case "touch" -> new Touch(cmdLine[1], cmdLine[2]).execute();
                case "rm" -> new Rm(cmdLine[1]).execute();
                case "cat" -> new Cat(cmdLine[1]).execute();
                case "ls" -> new Thread(() -> new Ls().execute()).start(); // 可能會很久, 且必定無異常或者異常不重要, 因此另開新執行緒
                // case "ln" -> new Ln(cmdLine[1], cmdLine[2]).execute(); // 可用性未知
                case "search" -> new Thread(() -> new Search(cmdLine[1]).execute()).start(); // 可能會很久, 且必定無異常或者異常不重要, 因此另開新執行緒
                // case "clear" -> throw new RuntimeException(); // 之後可能會去實現
                case "exit" -> { return 1; } // 終止程式
                case "history" -> new History().execute();
                case "meow" -> new Meow().execute();
                default -> System.out.println("Error command!");
            }
        } catch (IllegalArgumentException ile){
            System.out.println("Illegal command.");
        } catch (AccessDeniedException ae){

        }
        return 0;
    }

    @Override
    public String toString(){
        return getInfo();
    }
    
    public String toString(String cmd){
        return getInfo() + "\n@ " + cmd;
    }
    
    public String getInfo(){
        return "# guiTerminal@" + computerName + " in " + 
            Command.getCurrentPath() + " [" + 
            DateFormat.getTimeInstance(DateFormat.MEDIUM).format(new Date()) + "]";
    }
}
