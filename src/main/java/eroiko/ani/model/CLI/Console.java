/** Created at 2021/06/05 */

package eroiko.ani.model.CLI;

import java.io.PrintStream;
import java.nio.file.AccessDeniedException;
import java.nio.file.Path;
import java.text.DateFormat;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import eroiko.ani.model.CLI.CLIException.ClearTerminalException;
import eroiko.ani.model.CLI.command.basic.*;
import eroiko.ani.model.CLI.command.basic.fundamental.*;
import eroiko.ani.model.CLI.command.special.*;

public class Console {
    
    private PrintStream consoleOut;
    public final String computerName;
    public final String userName;
    ExecutorService service;
    private RequestCommand rq;
    
    /**
     * Create a new Console
     * <p> The console is based on java.nio so it can go to files which are over the assigned root
     * @param consoleOut    PrintStream of this console
     * @param root          assign the initial root path of this console
     * @param computerName  assign this device's name
     * @param userName      assign the user name
     * @param printRelative {@code true} to print file path by its relative path or {@code false} to print by only its file name 
     */
    public Console(PrintStream consoleOut, Path root, String computerName, String userName, boolean printRelative){
        this.consoleOut = consoleOut;
        this.computerName = computerName;
        this.userName = userName;
        Command.setDefaultPath(root);
        Command.setPrintBehavior(printRelative);
        consoleOut.println(toString());
        service = Executors.newCachedThreadPool();
        rq = new RequestCommand();
    }
    
    /**
     * Create a new Console
     * <p> The console is based on java.nio so it can go to files which are over the assigned root
     * <p> Use default PrintStream : {@code System.out}
     * @param root          assign initial the root path of this console
     * @param computerName  assign this device's name
     * @param userName      assign the user name
     * @param printRelative {@code true} to print file path by its relative path or {@code false} to print by only its file name 
     */
    public Console(Path root, String computerName, String userName, boolean printRelative){
        this(System.out, root, computerName, userName, printRelative);
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
    
    /**
     * @param cmd the command to execute
     * @throws ClearTerminalException when need to clear terminal text space
     */
    public int readLine(String cmd){
        if (rq.checkNeedRequest()){
            rq.takeCommand(cmd);
        }
        else {
            if (!cmd.equals("")){ //  when the user didn't press ENTER
                consoleOut.println(toString(cmd));
                History.addHistory(cmd);
            }
            String [] cmdLine = cmd.split(" ");
            try {
                switch (cmdLine[0]){
                    /* Basic */
                    case "" -> consoleOut.println(toString()); // this is when the user pressed ENTER
                    case "cd" -> new Cd(cmdLine[1]).execute();
                    case "mkdir" -> new Mkdir(cmdLine[1]).execute();
                    case "touch" -> new Touch(cmdLine[1], cmdLine[2]).execute();
                    case "rm" -> new Rm(rq, cmdLine[1]).execute();
                    case "cat" -> new Cat(cmdLine[1]).execute();
                    case "ls" -> service.submit(() -> new Ls().execute()); // 可能會很久, 且必定無異常或者異常不重要, 因此另開新執行緒
                    // case "ls" -> new Thread(() -> new Ls().execute()).start(); // 可能會很久, 且必定無異常或者異常不重要, 因此另開新執行緒
                    // case "ln" -> new Ln(cmdLine[1], cmdLine[2]).execute(); // 可用性未知
                    case "search" -> service.submit(() -> new Search(cmdLine[1]).execute()); // 可能會很久, 且必定無異常或者異常不重要, 因此另開新執行緒
                    case "clear" -> throw new ClearTerminalException(); // 之後可能會去實現
                    case "exit" -> { return 1; } // 終止程式
                    case "history" -> new History().execute();
                    case "echo" -> consoleOut.println(cmd.substring(cmd.indexOf(' ') + 1)); // 若遇到無空白的情況, cmd.indexOf(' ') + 1 = 0, 表輸出 echo
                    /* Special */
                    case "wallpaper" -> {
                        var tmp = new String[cmdLine.length - 1];
                        for (int i = 0; i < cmdLine.length - 1; ++i){
                            tmp[i] = cmdLine[i + 1];
                        }
                        new Wallpaper(tmp).execute();
                    }
                    case "music" -> {
                        var tmp = new String[cmdLine.length - 1];
                        for (int i = 0; i < cmdLine.length - 1; ++i){
                            tmp[i] = cmdLine[i + 1];
                        }
                        new Music(tmp).execute();
                    }
                    case "meow" -> new Meow().execute();
                    case "artwork" -> {
                        service.submit(() -> {
                            try {
                                int number = Integer.parseInt(cmdLine[1]);
                                new Artwork(number, cmd.substring(cmd.indexOf(' ', cmd.indexOf(' ') + 1) + 1)).execute();
                            } catch (NumberFormatException ne){
                                try {
                                    new Artwork(cmd.substring(cmd.indexOf(" ") + 1)).execute();
                                } catch (IllegalArgumentException ile){
                                    consoleOut.println(ile.getMessage() + "\nIllegal argument, please try again.");
                                }
                            }
                        });
                    }
                    default -> consoleOut.println("Command not defined!");
                }
            } catch (IllegalArgumentException ile){
                consoleOut.println(ile.getMessage() + "\nIllegal argument, please try again.");
            } catch (AccessDeniedException ae){
                consoleOut.println(ae.getMessage() + "\nBad Access.");
            }
        }
        return 0; // 正常執行指令
    }

    /** print current path with the user name, the device's name and time information */
    @Override
    public String toString(){
        return getInfo();
    }
    
    public String toString(String cmd){
        return getInfo() + "\n@ " + cmd;
    }
    
    public String getInfo(){
        return "# " + userName + " at " + computerName + " in " + 
            Command.getCurrentPath() + " [" + 
            DateFormat.getTimeInstance(DateFormat.MEDIUM).format(new Date()) + "]";
    }

    public void cancel(){
        service.shutdownNow();
        service = Executors.newCachedThreadPool();
        consoleOut.println("^C");
    }
}
