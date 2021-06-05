package eroiko.ani.model.CLI;

import java.nio.file.Path;

import eroiko.ani.model.CLI.command.*;

public class Console {
    
    public final String computerName;
    
    public Console(Path root, String computerName, boolean printRelative){
        this.computerName = computerName;
        Command.setDefaultPath(root);
        Command.setPrintBehavior(printRelative);
        System.out.println("# guiTerminal@" + computerName + " at " + Command.getCurrentPath());
    }
    
    public void setPath(Path root){
        Command.setDefaultPath(root);
    }
    
    public int readConsole(String cmd){
        System.out.println("# guiTerminal@" + computerName + " at " + Command.getCurrentPath() + "\n@ " + cmd);
        String [] cmdLine = cmd.split(" ");
        try {
            switch (cmdLine[0]){
                case "cd" -> new Cd(cmdLine[1]).execute();
                case "mkdir" -> new Mkdir(cmdLine[1]).execute();
                case "touch" -> new Touch(cmdLine[1], cmdLine[2]).execute();
                case "rm" -> new Rm(cmdLine[1]).execute();
                case "cat" -> new Cat(cmdLine[1]).execute();
                case "ls" -> new Thread(() -> new Ls().execute()).start(); // 可能會很久, 且必定無異常, 因此另開新執行緒
                // case "ln" -> new Ln(cmdLine[1], cmdLine[2]).execute(); // 可用性未知
                case "search" -> new Thread(() -> new Search(cmdLine[1]).execute()).start(); // 可能會很久, 且必定無異常, 因此另開新執行緒
                // case "clear" -> throw new RuntimeException();
                case "exit" -> { return 1; } // 終止程式
                default -> System.out.println("Error command!");
            }
        } catch (IllegalArgumentException ile){
            System.out.println("Illegal command.");
        }
        return 0;
    }
}
