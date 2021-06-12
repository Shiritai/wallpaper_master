/** Created at 2021/06/05 by Eroiko */

package eroiko.ani.model.CLI;

import java.io.PrintStream;
import java.nio.file.Path;
import java.text.DateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import eroiko.ani.model.CLI.command.basic.*;
import eroiko.ani.model.CLI.command.critical.Clear;
import eroiko.ani.model.CLI.command.critical.Exit;
import eroiko.ani.model.CLI.command.critical.Shutdown;
import eroiko.ani.model.CLI.command.external.*;
import eroiko.ani.model.CLI.command.fundamental.*;
import eroiko.ani.model.CLI.command.special.*;
import eroiko.ani.model.CLI.command.special.Shell.*;
import eroiko.ani.model.CLI.conversation.Consulter;
import eroiko.ani.model.CLI.exception.*;

/**
 * <h2>Console</h2>
 * <h3>The module for a consultative terminal</h3>
 * Created by Eroiko at 2021/06/06
 */
public class Console {
    
    private PrintStream consoleOut;
    public final String computerName;
    public final String userName;
    ScheduledExecutorService service;
    private Consulter rq;
    private Comparator<Path> compPath;
    
    /**
     * Create a new Console
     * <p> The console is based on java.nio so it can go to files which are over the assigned root
     * @param consoleOut    PrintStream of this console
     * @param root          assign the initial root path of this console
     * @param compPath      Path comparator for the commands which needs to print paths
     * @param computerName  assign this device's name
     * @param userName      assign the user name
     * @param printRelative {@code true} to print file path by its relative path or {@code false} to print by only its file name 
     */
    public Console(PrintStream consoleOut, Path root, Comparator<Path> compPath, String computerName, String userName, boolean printRelative){
        this.consoleOut = consoleOut;
        this.computerName = computerName;
        this.userName = userName;
        Command.setDefaultPath(root);
        Command.setPrintBehavior(printRelative);
        Command.setAllCommandPrintStream(consoleOut);
        consoleOut.println(toString());
        service = Executors.newSingleThreadScheduledExecutor();
        service = Executors.newScheduledThreadPool(4);
        rq = new Consulter(consoleOut);
        this.compPath = compPath;
    }
    
    /**
     * Create a new Console
     * <p> The console is based on java.nio so it can go to files which are over the assigned root
     * <p> Use default PrintStream : {@code System.out}
     * @param root          assign initial the root path of this console
     * @param compPath      Path comparator for the commands which needs to print paths
     * @param computerName  assign this device's name
     * @param userName      assign the user name
     * @param printRelative {@code true} to print file path by its relative path or {@code false} to print by only its file name 
     */
    public Console(Path root, Comparator<Path> compPath, String computerName, String userName, boolean printRelative){
        this(System.out, root, compPath, computerName, userName, printRelative);
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
     * @throws ShutdownSoftwareException    when user try shutdown this software
     * @throws ClearConsoleException        when need to clear terminal text space
     * @throws ExitConsoleException         when user try exit this console
     */
    public void readLine(String cmd) throws Exception{
        if (rq.checkNeedRequest()){
            rq.takeCommand(cmd);
        }
        else {
            if (!cmd.equals("")){ //  when the user didn't press ENTER
                History.addHistory(cmd);
                consoleOut.println(toString(cmd));
            }
            String [] cmdLine = cmd.split(" ");
            /* call COMMAND --help --> i.e. man COMMAND */
            if (cmdLine.length == 2 && cmdLine[1].equals("--help")){
                new Man(cmdLine[0]).execute();
                return;
            }
            try {
                var passingStr = stripHeadingCommand(cmd);
                switch (cmdLine[0]){
                    /* Fundamental */
                    case "" -> consoleOut.println(toString()); // this is when the user pressed ENTER
                    case "man" -> new Man(passingStr).execute();

                    /* Basic */
                    case "cd" -> new Cd(passingStr).execute();
                    case "mkdir" -> new Mkdir(passingStr).execute();
                    case "touch" -> new Touch(passingStr).execute();
                    case "rm" -> new Rm(rq, passingStr).execute();
                    case "cat" -> new Cat(passingStr).execute();
                    case "ls" -> service.submit(() -> new Ls(compPath, passingStr).execute()); // 可能會很久, 且必定無異常或者異常不重要, 因此另開新執行緒
                    case "ln" -> new Ln(passingStr).execute(); // 可用性有疑慮
                    case "search" -> service.submit(() -> new Search(passingStr).execute()); // 可能會很久, 且必定無異常或者異常不重要, 因此另開新執行緒
                    case "clear" -> new Clear().callHost();
                    case "exit" -> new Exit().callHost(); // 終止 Console
                    case "history" -> new History().execute();
                    case "echo" -> consoleOut.println(passingStr);
                    case "shutdown" -> new Shutdown().callHost();

                    /* Special */
                    case "meow" -> new Meow().execute();
                    case "cmd", "cmd.exe" -> service.submit(() -> new Cmd(passingStr).execute());
                    case "powershell.exe", "powershell", "pwsh" -> service.submit(() -> new PowerShell(passingStr).execute());
                    case "wt", "wt.exe" -> service.submit(() -> new WindowsTerminal(passingStr).execute());
                    case "bash" -> service.submit(() -> new Bash(passingStr).execute());
                    /* External */
                    case "wallpaper" -> new Wallpaper(passingStr).execute();
                    case "music" -> new Music(passingStr).execute();
                    case "crawler" -> {
                        service.submit(() -> {
                            try {
                                int number = Integer.parseInt(cmdLine[1]);
                                new Crawler(number, cmd.substring(cmd.indexOf(' ', cmd.indexOf(' ') + 1) + 1)).execute();
                            } catch (NumberFormatException ne){
                                try {
                                    new Crawler(passingStr).execute();
                                } catch (IllegalArgumentException ile){
                                    consoleOut.println(ile.getMessage() + "\nIllegal argument, please try again.");
                                }
                            }
                        });
                    }
                    case "wm" -> new Wm(passingStr).execute();
                    default -> consoleOut.println("Command not defined!");
                }
            } catch (IllegalArgumentException ile){
                consoleOut.println(ile.getMessage() + "\nIllegal argument, please try again.");
            }
        }
    }

    /** Strip heading command, return {@code null} if the input a pure command with no parameter */
    public String stripHeadingCommand(String str){ // 若遇到無空白的情況, cmd.indexOf(' ') + 1 = 0, 表輸出該指令字串
        if (str.indexOf(' ') == -1){
            return null;
        }
        return str.substring(str.indexOf(' ') + 1);
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
        service = Executors.newSingleThreadScheduledExecutor();
        consoleOut.println("^C");
    }
}
