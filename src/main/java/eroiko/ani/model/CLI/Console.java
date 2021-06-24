/*
 * Author : Shiritai (楊子慶, or Eroiko on Github) at 2021/06/13.
 * See https://github.com/Shiritai/wallpaper_master for more information.
 * Created using VSCode.
 */

package eroiko.ani.model.CLI;

import java.io.PrintStream;
import java.nio.file.Path;
import java.text.DateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;

import eroiko.ani.model.CLI.command.basic.*;
import eroiko.ani.model.CLI.command.critical.*;
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
    private ScheduledExecutorService service;
    private Consulter rq;
    private Comparator<Path> compPath;
    private Future<Void> future;
    private boolean isSearchCommand;
    
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
        this.isSearchCommand = false;
        Command.setDefaultPath(root);
        Command.setPrintBehavior(printRelative);
        Command.setAllCommandPrintStream(consoleOut);
        service = Executors.newScheduledThreadPool(16); // 16 is just a trivial number
        rq = new Consulter(consoleOut);
        this.compPath = compPath;
        future = null;
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

    /**
     * 返回上一個指令是否為查詢式指令
     * <p> 查詢式指令指的是 man CMD 或 CMD --help 這兩種
     * @return 是否為查詢式指令
     */
    public boolean isSearchCmd(){ return isSearchCommand; }
    
    public Path getCurrentPath(){ return Command.getCurrentPath(); }
    
    /** return {@code null} if there is no previous history command */
    public String getPreviousCommand(){ return History.getPreviousCommand(); }
    /** return {@code null} if there is no later history command */
    public String getLaterCommand(){ return History.getNextCommand(); }
    /** restore command traverse */
    public void restoreCommandTraverse(){ History.restoreCommandTraverse(); }
    
    /**
     * @param cmd the command to execute
     * @throws ShutdownSoftwareException    when user try to shutdown this software
     * @throws ClearConsoleException        when need to clear terminal text space
     * @throws ExitConsoleException         when user try to exit this console
     * @throws TerminalSettingException     when user try to change terminal settings
     * @throws CustomInformationException   this is reserved for future use
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
            else {
                consoleOut.println(toString());
            }
            callCommand(cmd);
        }
    }

    /**
     * @param cmd the command to execute
     * @throws ShutdownSoftwareException    when user try to shutdown this software
     * @throws ClearConsoleException        when need to clear terminal text space
     * @throws ExitConsoleException         when user try to exit this console
     * @throws TerminalSettingException     when user try to change terminal settings
     * @throws CustomInformationException   this is reserved for future use
     */
    public void normalReadLine(String cmd) throws Exception{
        if (rq.checkNeedRequest()){
            consoleOut.println();
            rq.takeCommand(cmd);
            consoleOut.print(promptString());
        }
        else {
            if (!cmd.equals("")){ //  when the user didn't press ENTER
                History.addHistory(cmd);
            }
            consoleOut.println();
            callCommand(cmd);
            if (future != null && !future.isDone()){
                var tmp = new Thread(() -> {
                    while (!future.isDone());
                    consoleOut.print(promptString());
                    future = null; // clean
                });
                tmp.setDaemon(true);
                tmp.start();
            }
            else if (!rq.checkNeedRequest()) {
                consoleOut.print(promptString());
            }
        }
    }

    public void callCommand(String cmd) throws Exception{
        String [] cmdLine = cmd.split(" ");
        /* call COMMAND --help --> i.e. man COMMAND */
        if (cmdLine.length == 2 && cmdLine[1].equals("--help")){
            new Man(cmdLine[0]).execute();
            isSearchCommand = true;
            return;
        }
        isSearchCommand = false;
        try {
            var passingStr = stripHeadingCommand(cmd);
            switch (cmdLine[0]){
                /* Fundamental */
                case "" -> {} // this is when the user pressed ENTER
                case "man" -> { new Man(passingStr).execute(); isSearchCommand = true; }
    
                /* Basic */
                case "cd" -> new Cd(passingStr).execute();
                case "mkdir" -> new Mkdir(passingStr).execute();
                case "touch" -> new Touch(passingStr).execute();
                case "rm" -> new Rm(rq, passingStr).execute();
                case "cat" -> new Cat(passingStr).execute();
                case "ls" -> future = service.submit(() -> { new Ls(compPath, passingStr).execute(); return null; }); // 可能會很久, 且必定無異常或者異常不重要, 因此另開新執行緒
                case "ln" -> new Ln(passingStr).execute(); // 可用性有疑慮
                case "search" -> future = service.submit(() -> { new Search(passingStr).execute(); return null; }); // 可能會很久, 且必定無異常或者異常不重要, 因此另開新執行緒
                case "clear" -> new Clear().callHost();
                case "exit" -> new Exit().callHost(); // 終止 Console
                case "history" -> new History().execute();
                case "echo" -> consoleOut.println(passingStr);
                case "shutdown" -> new Shutdown().callHost();
                case "info" -> new Information().callHost();
    
                /* Special */
                case "meow" -> future = service.submit(() -> { new Meow().execute(); return null; });
                case "cmd", "cmd.exe" -> future = service.submit(() -> { new Cmd(passingStr).execute(); return null; });
                case "powershell.exe", "powershell", "pwsh" -> future = service.submit(() -> { new PowerShell(passingStr).execute(); return null; });
                case "wt", "wt.exe" -> future = service.submit(() -> { new WindowsTerminal(passingStr).execute(); return null; });
                case "bash" -> future = service.submit(() -> { new Bash(passingStr).execute(); return null; });

                /* External */
                case "wallpaper" -> new Wallpaper(passingStr).execute();
                case "music" -> new Music(passingStr).execute();
                case "crawler" -> future = service.submit(() -> { new Crawler(passingStr).execute(); return null; });
                case "wm" -> new Wm(passingStr).execute();
                case "setting" -> new Setting().callHost();
                default -> consoleOut.println("Command not defined!");
            }
        } catch (IllegalArgumentException ile){
            consoleOut.println(ile.getMessage() + "\nIllegal argument, please try again.");
        }
    }

    /** Strip heading command, return {@code null} if the input a pure command with no parameter */
    public String stripHeadingCommand(String str){ // 若遇到無空白的情況, cmd.indexOf(' ') + 1 = 0, 表輸出該指令字串
        if (str.indexOf(' ') == -1){
            return null;
        }
        return str.substring(str.indexOf(' ') + 1);
    }

    public void requestInfoPrint(String str){
        consoleOut.println(toString(str));
    }

    /** print current path with the user name, the device's name and time information */
    @Override
    public String toString(){
        return getInfo();
    }
    
    public String toString(String cmd){
        return getInfo() + "\n@ " + cmd;
    }

    public String promptString(){ return toString(""); }
    
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
